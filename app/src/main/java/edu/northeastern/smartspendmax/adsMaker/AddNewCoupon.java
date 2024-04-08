package edu.northeastern.smartspendmax.adsMaker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.smartspendmax.R;
import edu.northeastern.smartspendmax.model.Coupon;
import edu.northeastern.smartspendmax.util.CommonConstants;
import edu.northeastern.smartspendmax.util.DateHandler;

public class AddNewCoupon extends AppCompatActivity {

    private static final String TAG = "AddNewCoupon";
    private Spinner spinner_category;
    private EditText et_discount, et_description, et_expireTime;
    private Button btn_save_coupon;

    private FirebaseDatabase db;
    private String currAdsMaker;

    private String discountCategory,discount,  description, validity;
    private List<String> userIdList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_coupon);

        Intent intent = getIntent();
        if (intent.hasExtra("userName")) {
            String userName = intent.getStringExtra("userName");
            Log.d(TAG, "Received userName: " + userName);
        } else {
            Log.d(TAG, "No userName extra found in Intent");
        }

        spinner_category = findViewById(R.id.spinner_discount_category);
        et_discount = findViewById(R.id.et_discount);
        et_description = findViewById(R.id.et_description);
        et_expireTime = findViewById(R.id.et_validity);
        btn_save_coupon = findViewById(R.id.btn_save_coupon);

        //retrieve current adsMaker
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currAdsMaker = sharedPref.getString("LastLoggedInUser", "defaultUser");

        //Spinner for transaction category
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.transactionCategoryArray, android.R.layout.simple_spinner_item
        );
        spinner_category.setAdapter(adapter);

        //Date picker for transaction date
        et_expireTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        db = FirebaseDatabase.getInstance();

        btn_save_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    saveDataToDB();
                } else {
                    Toast.makeText(getApplicationContext(), "Please verify your input", Toast.LENGTH_LONG).show();
                }
                ;
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String formattedMonth = String.format("%02d", month + 1);
                        String formattedDay = String.format("%02d", dayOfMonth);
                        et_expireTime.setText(formattedMonth + "/" + formattedDay + "/" + year);
                    }
                },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }

    private boolean validateInput() {
        if (spinner_category == null || spinner_category.getSelectedItemPosition() == 0) {
            return false;
        }
        if (et_discount == null || et_discount.getText().toString().trim().isEmpty()) {
            return false;
        }
        if (et_description == null || et_description.getText().toString().trim().isEmpty()) {
            return false;
        }
        if (et_expireTime == null || et_expireTime.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void saveDataToDB() {

         discountCategory = spinner_category.getSelectedItem().toString().toLowerCase();
         discount = et_discount.getText().toString();
         description = et_description.getText().toString();
         validity = et_expireTime.getText().toString();

        // get all users id (except ads maker)

        DatabaseReference usersRef = db.getReference(CommonConstants.NODE_USERS);
        usersRef.orderByChild("role").equalTo(CommonConstants.ROLE_USER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "the count of users whose role is user = " + snapshot.getChildrenCount());
                for (DataSnapshot entry : snapshot.getChildren()) {
                    String userId = entry.getKey();
                    Log.d(TAG, "userId = " + userId);
                    userIdList.add(userId);
                }
                updateMultipleNodes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "operation is cancelled: get all users id (except ads maker)");
            }
        });


    }

    /**
     * write data for multiple nodes(tables) as a batch
     * 1. save the metadata for a coupon
     * 2. send the coupon to all users(role: "user")
     *   2.1 get add users whose role is "user"
     *   2.2 write data in user-coupon/${userId}/receivedCoupon/#{couponId}:true
     *   2.3 write data in ads/${adsId}/
     */
    private void updateMultipleNodes() {
        Map<String, Object> childUpdates = new HashMap<>();
        // add a new record to coupon table
        DatabaseReference couponsRef = db.getReference(CommonConstants.NODE_COUPONS);
        String couponId = couponsRef.push().getKey();
        Coupon coupon = new Coupon(currAdsMaker, discount, discountCategory, discountCategory, description, validity);
        Map<String, Object> couponValues = coupon.toMap();
        childUpdates.put(CommonConstants.NODE_COUPONS + "/" + couponId + "/", couponValues);

        // add a new record to user-coupon
        for (String userId : userIdList) {
            childUpdates.put(CommonConstants.NODE_USER_COUPON + "/" +userId + "/receivedCoupon/" + couponId, true);
        }

        // add a new record to ads
        DatabaseReference adsRef = db.getReference(CommonConstants.NODE_ADS);
        String adsId = adsRef.push().getKey();
        childUpdates.put(CommonConstants.NODE_ADS + "/" +adsId + "/couponId/", couponId);
        childUpdates.put(CommonConstants.NODE_ADS + "/" +adsId + "/sendFrom/", currAdsMaker);
        childUpdates.put(CommonConstants.NODE_ADS + "/" +adsId + "/timestamp/", DateHandler.getCurrentTime());

        for (String userId : userIdList) {
            childUpdates.put(CommonConstants.NODE_ADS + "/" +adsId + "/sentTo/"+userId, true);
        }
        for (String key : childUpdates.keySet()) {
            System.out.println(key + ":" + childUpdates.get(key));
        }
        db.getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "create and send coupon successfully!", Toast.LENGTH_LONG).show();
            }
        });

    }
}
