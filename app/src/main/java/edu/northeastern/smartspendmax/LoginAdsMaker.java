package edu.northeastern.smartspendmax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import edu.northeastern.smartspendmax.model.AdsMaker;
import edu.northeastern.smartspendmax.util.CommonConstants;
import edu.northeastern.smartspendmax.util.DateHandler;

public class LoginAdsMaker extends AppCompatActivity {

    private EditText et_username;
    private Button btn_login;

    private FirebaseDatabase db;
    private DatabaseReference adsMakerRef;
    private String currAdsMakerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ads_maker);

        et_username = findViewById(R.id.editTextAdsMakerName);
        btn_login = findViewById(R.id.buttonAdsMakerLogin);

        db = FirebaseDatabase.getInstance();
        loginAdsMaker();
    }

    private void loginAdsMaker() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currAdsMakerId = et_username.getText().toString().trim();

                // validate input for currAdsMakerId
                if(currAdsMakerId != null && !currAdsMakerId.isEmpty()){
                    disconnectCurrentUser();
                }
                if (currAdsMakerId.isEmpty()) {
                    Toast.makeText(LoginAdsMaker.this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if the user exists in the db
                adsMakerRef = db.getReference(CommonConstants.NODE_ADS_MAKERS);
                adsMakerRef.child(currAdsMakerId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Log.d("LoginAdsMaker", "ads maker exists");
//                            proceedToNextActivity(currAdsMakerId);
                        } else{
                            Log.d("LoginAdsMaker", "ads maker doesn't exist");
                            adsMakerRef.child(currAdsMakerId).setValue(new AdsMaker(currAdsMakerId, DateHandler.getCurrentTime(), true));
//                            proceedToNextActivity(currAdsMakerId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginAdsMaker.this, "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void disconnectCurrentUser() {
        if (db != null) {
            SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String currentUserName = sharedPref.getString("LastLoggedInUser", null);
            if (currentUserName != null) {
//              DatabaseReference userRef = db.getReference("users").child(currentUserName);
                adsMakerRef.child(currAdsMakerId + "/online").setValue(false);
            }
        }else {
            Log.d("AdsMaker", "Firebase database not initialized");
        }
    }


}