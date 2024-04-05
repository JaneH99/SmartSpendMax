package edu.northeastern.smartspendmax.adsMaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.smartspendmax.R;
import edu.northeastern.smartspendmax.util.CommonConstants;

public class AddNewCoupon extends AppCompatActivity {

    private static final String TAG = "AddNewCoupon";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_coupon);

        Intent intent =getIntent();
        if(intent.hasExtra("userName")) {
            String userName = intent.getStringExtra("userName");
            Log.d(TAG, "Received userName: " + userName);
        } else {
            Log.d(TAG, "No userName extra found in Intent");
        }
    }
}
