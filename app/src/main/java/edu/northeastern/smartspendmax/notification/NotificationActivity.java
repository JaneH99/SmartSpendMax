package edu.northeastern.smartspendmax.notification;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.smartspendmax.R;
import edu.northeastern.smartspendmax.model.Notification;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    private RecyclerView recyclerView;
    private List<Notification> notificationList = new ArrayList<>();

    private String currUser = "user2";
    private List<String> couponIDList = new ArrayList<>();

    private DatabaseReference adsRef;
    private DatabaseReference couponsRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.rv_notification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adsRef = FirebaseDatabase.getInstance().getReference("ads");
        couponsRef = FirebaseDatabase.getInstance().getReference("coupons");

        getCouponIDList();
        Log.d(TAG, "after getCouponIDList ");


//        NotificationAdapter adapter = new NotificationAdapter(NotificationActivity.this, notificationList);
//        Log.d(TAG, "onCreate: notificationList size: " + notificationList.size());
//        recyclerView.setAdapter(adapter);
    }

    // get couponID list
    private void getCouponIDList() {
        Query query = adsRef.orderByChild("sentTo/" + currUser).equalTo(true);

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
                getCouponContentList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCouponContentList() {
        for (String couponId : couponIDList) {
//            Query query = couponsRef.orderByKey().equalTo(couponId);
//            Log.d(TAG, "getCouponContentList: couponsRef.child(couponId): " + couponsRef.child(couponId)); // 只打印路径名：https://group-21-smartspendmax-default-rtdb.firebaseio.com/coupons/coupon1
            couponsRef.child(couponId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange: snapshot size: " + snapshot.getChildrenCount());
                    Notification notification = snapshot.getValue(Notification.class);
                    notification.setCouponId(couponId);
                    notificationList.add(notification);
                    Log.d(TAG, "notification: " + notification.getDiscount());
//                    for (DataSnapshot couponSnap: snapshot.getChildren()) {
//                        String discount = snapshot.child("discount").getValue(String.class);
//                        Log.d(TAG, "onDataChange: discount: " + discount);
////                        Notification notification = couponSnap.getValue(Notification.class);
////                        Log.d(TAG, "onDataChange: " + notification.getDiscount());
//                    }
                    NotificationAdapter adapter = new NotificationAdapter(NotificationActivity.this, notificationList);
                    Log.d(TAG, "getCouponContentList: notificationList size: " + notificationList.size());
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
