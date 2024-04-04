package edu.northeastern.smartspendmax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginUser extends AppCompatActivity {

    EditText username;
    Button userLoginButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        username = findViewById(R.id.editTextUserName);
        userLoginButton = findViewById(R.id.buttonUserLogin);

        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = username.getText().toString().trim();
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                if(name != null && !name.isEmpty()){
                    disconnectCurrentUser();
                }
                if (name.isEmpty()) {
                    Toast.makeText(LoginUser.this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                    return;
                }

                reference.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            proceedToNextActivity(name);
                        } else{
                            reference.child(name).setValue(new Users(name, true, getCurrentTime()));
                            proceedToNextActivity(name);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginUser.this, "Database error", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }


    private void proceedToNextActivity(String userName) {
        DatabaseReference userRef = reference.child(userName);

        // Update the user's login state and time
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("online", true);
        userUpdates.put("userName", userName);
        userUpdates.put("loginTime", getCurrentTime());

        userRef.updateChildren(userUpdates);

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        // Continue with shared preferences and starting the next activity
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("LastLoggedInUser", userName);
        editor.putInt("LoginMonth", month);
        editor.putInt("LoginYear", year);
        editor.apply();

        Intent intent = new Intent(LoginUser.this, MainActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    private String getCurrentTime() {
        // Your method already matches the expected format.
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        return dateFormat.format(now);
    }


    // To disconnect the current user, log-out current user if any
    private void disconnectCurrentUser() {
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String currentUserName = sharedPref.getString("LastLoggedInUser", null);
        if (currentUserName != null) {
            DatabaseReference userRef = database.getReference("users").child(currentUserName);
            userRef.child("online").setValue(false);
        }
    }

    @Override
    public void onBackPressed() {
        disconnectCurrentUser();
        super.onBackPressed();
    }


}