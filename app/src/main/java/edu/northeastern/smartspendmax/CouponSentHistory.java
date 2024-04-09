package edu.northeastern.smartspendmax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.smartspendmax.adsMaker.AddNewCoupon;
import edu.northeastern.smartspendmax.notification.CouponAdapter;
import edu.northeastern.smartspendmax.util.DateHandler;

public class CouponSentHistory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CouponSentHistoryAdapter couponAdapter;
    String curDate = DateHandler.getCurrentDate();
    private FloatingActionButton fab;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String curUserName;

    private TextView tvAdsMaker;
    private TextView tvIntro;

    private String TAG = "===CouponSentHistory===";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_sent_history);

        tvAdsMaker =findViewById(R.id.welcome_text);
        tvIntro = findViewById(R.id.intro_text);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String curDate = today.format(formatter);


        recyclerView = findViewById(R.id.coupon_history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        couponAdapter = new CouponSentHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(couponAdapter);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        curUserName = sharedPref.getString("LastLoggedInUser", "defaultUser");

        tvAdsMaker.setText("WELCOME! " + curUserName.toUpperCase());
        tvIntro.setText("Below are the results statistics until " + curDate);

        fab = findViewById(R.id.fab_coupon);
        fab.setOnClickListener(v -> startActivity(new Intent(CouponSentHistory.this, AddNewCoupon.class)));

        fetchCoupon();
    }


    private void fetchCoupon() {
        databaseReference.child("coupons").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Coupon> coupons = new ArrayList<>();
                for (DataSnapshot couponSnapshot : dataSnapshot.getChildren()) {
                    String adMakerName = couponSnapshot.child("adMakerName").getValue(String.class);

                    if (curUserName.equals(adMakerName)) {
                        String discount = couponSnapshot.child("discount").getValue(String.class) != null ? couponSnapshot.child("discount").getValue(String.class) : "[N/A]";
                        String description = couponSnapshot.child("description").getValue(String.class) != null ? couponSnapshot.child("description").getValue(String.class) : "[N/A]";
                        // Use current date as validity date
                        String date = couponSnapshot.child("validity").getValue(String.class);

                        String couponID = couponSnapshot.getKey();

                        int count = 0; // Placeholder for actual count logic

                        // Add a placeholder coupon with a count of 0 for now
                        coupons.add(new Coupon(couponID, discount, description, date, count));
                    }
                }

                // Once all coupons are added, now fetch and update collected counts
                updateCollectedCounts(coupons);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching coupons", error.toException());
            }
        });
    }

    private void updateCollectedCounts(List<Coupon> coupons) {
        databaseReference.child("user-coupon").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Map to keep track of collected counts for each coupon
                Map<String, Integer> collectedCounts = new HashMap<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot collectedCouponSnapshot : userSnapshot.child("collectedCoupon").getChildren()) {
                        String couponId = collectedCouponSnapshot.getKey();
                        collectedCounts.put(couponId, collectedCounts.getOrDefault(couponId, 0) + 1);
                    }
                }
                // Update the collected number for each coupon
                for (Coupon coupon : coupons) {
                    Integer count = collectedCounts.get(coupon.getId());
                    if (count != null) {
                        coupon.setCollectedNumber(count);
                    }
                }
                // Notify the adapter of the updated dataset
                couponAdapter.setCouponList(coupons);
                couponAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching user coupons", error.toException());
            }
        });
    }



}