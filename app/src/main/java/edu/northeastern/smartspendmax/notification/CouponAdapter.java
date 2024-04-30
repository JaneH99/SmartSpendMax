package edu.northeastern.smartspendmax.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.northeastern.smartspendmax.MainActivity;
import edu.northeastern.smartspendmax.R;
import edu.northeastern.smartspendmax.model.Coupon;
import edu.northeastern.smartspendmax.util.ImageHelper;

public class CouponAdapter {
 //       extends RecyclerView.Adapter<CouponAdapter.NotificationViewHolder> {

//    private static final String TAG = "CouponAdapter";
//    private Context context;
//    private List<Coupon> couponList;
//    private String currUserId = "";
//
//    private FirebaseDatabase db = FirebaseDatabase.getInstance();
//
//    public CouponAdapter(Context context, List<Coupon> couponList, String currUserId) {
//        this.context = context;
//        this.couponList = couponList;
//        this.currUserId = currUserId;
//    }
//
//    @NonNull
//    @Override
//    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
//        return new NotificationViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
//        Coupon coupon = couponList.get(position);
//        holder.ivAdsMakerImage.setImageResource(ImageHelper.setImageBasedOnString(coupon.getAdMakerName()));
//        holder.tvAdsMaker.setText(coupon.getAdMakerName());
//        holder.tvDiscount.setText(coupon.getDiscount());
//        holder.tvDescription.setText(coupon.getDescription());
//        holder.tvValidity.setText("Expire time:" + coupon.getValidity().substring(0, 10));
//        holder.addToWalletBtn.setEnabled(!coupon.getCollected());
//
//        holder.addToWalletBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.getReference("user-coupon/" + currUserId + "/collectedCoupon")
//                    .child(coupon.getCouponId()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(context, "Add coupon successfully", Toast.LENGTH_SHORT);
//                                Log.d(TAG,"write successful: " + coupon.getCouponId() + ", for user: " + currUserId);
//                                notifyDataSetChanged();
//                                Intent intent = new Intent(context, MainActivity.class);
//                                context.startActivity(intent);
//                            } else {
//                                Toast.makeText(context, "Fail to add coupon.", Toast.LENGTH_SHORT);
//                                Log.d(TAG,"write fail: " + coupon.getCouponId() + ", for user: " + currUserId);
//                            }
//                        }
//                    });
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return couponList.size();
//    }

//    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivAdsMakerImage;
//        TextView tvAdsMaker, tvDiscount, tvDescription, tvValidity;
//        Button addToWalletBtn;
//
//        public NotificationViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ivAdsMakerImage = itemView.findViewById(R.id.iv_adsMaker_image);
//            tvAdsMaker = itemView.findViewById(R.id.tv_adsMaker);
//            tvDiscount = itemView.findViewById(R.id.tv_discount);
//            tvDescription = itemView.findViewById(R.id.tv_description);
//            tvValidity = itemView.findViewById(R.id.tv_validity);
//            addToWalletBtn = itemView.findViewById(R.id.btn_add_to_wallet);
//        }
//    }
}
