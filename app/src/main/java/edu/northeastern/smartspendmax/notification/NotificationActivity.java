package edu.northeastern.smartspendmax.notification;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.smartspendmax.R;
import edu.northeastern.smartspendmax.model.Coupon;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    private RecyclerView recyclerView;
    private List<Coupon> couponList = new ArrayList<>();

    private String currUserId = "user1";
    private List<String> couponIDList = new ArrayList<>();

    private FirebaseDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.rv_notification);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseDatabase.getInstance();

//        getCouponIDList();

        getReceivedCoupons();
        Log.d(TAG, "after getCouponIDList ");

    }

    // get couponID list ===== old version ==== get receivedCoupon from "ads" node
    private void getCouponIDList() {
        Query query = db.getReference("ads").orderByChild("sentTo/" + currUserId).equalTo(true);

        query.addValueEventListener(new ValueEventListener() {
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot adsSnapshot : snapshot.getChildren()) {
                    String sentFrom = adsSnapshot.child("sentFrom").getValue(String.class);
                    String couponID = adsSnapshot.child("couponID").getValue(String.class);
                    couponIDList.add(couponID);
                    Log.d(TAG, "onDataChange: sentFrom:" + sentFrom + ", couponId: " + couponID);
                }
                getCouponDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getReceivedCoupons() {
        db.getReference("user-coupon/" + currUserId + "/receivedCoupon").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                System.out.println("snapshot.key = " + snapshot.getKey() + ", ChildCount = " + snapshot.getChildrenCount());
                for (DataSnapshot entry : snapshot.getChildren()) {
                    couponIDList.add(entry.getKey());
                    System.out.println("receivedCoupon in notification = " + entry.getKey());
                }
                getCouponDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCouponDetails() {
        for (String couponId : couponIDList) {
//            Query query = couponsRef.orderByKey().equalTo(couponId);
//            Log.d(TAG, "getCouponContentList: couponsRef.child(couponId): " + couponsRef.child(couponId)); // 只打印路径名：https://group-21-smartspendmax-default-rtdb.firebaseio.com/coupons/coupon1
            db.getReference("coupons").child(couponId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    Log.d(TAG, "onDataChange: snapshot size: " + snapshot.getChildrenCount());
                    Coupon coupon = snapshot.getValue(Coupon.class);
                    coupon.setCouponId(couponId);
                    couponList.add(coupon);
//                    Log.d(TAG, "notification: " + notification.getDiscount());
//                    for (DataSnapshot couponSnap: snapshot.getChildren()) {
//                        String discount = snapshot.child("discount").getValue(String.class);
//                        Log.d(TAG, "onDataChange: discount: " + discount);
////                        Notification notification = couponSnap.getValue(Notification.class);
////                        Log.d(TAG, "onDataChange: " + notification.getDiscount());
//                    }
                    CouponAdapter adapter = new CouponAdapter(NotificationActivity.this, couponList);
                    Log.d(TAG, "getCouponContentList: notificationList size: " + couponList.size());
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
