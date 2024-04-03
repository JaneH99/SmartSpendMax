package edu.northeastern.smartspendmax.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.smartspendmax.R;
import edu.northeastern.smartspendmax.model.Coupon;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.NotificationViewHolder>{

    private Context context;
    private List<Coupon> couponList;

    public CouponAdapter(Context context, List<Coupon> couponList) {
        this.context = context;
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
//        Log.d("NotificationAdapter", "onBindViewHolder");
        Coupon coupon = couponList.get(position);
//        Log.d("NotificationAdapter", "notification: " + notification.getCouponId());
        holder.tvAdsMaker.setText(coupon.getAdMakerName());
        holder.tvDiscount.setText(coupon.getDiscount());
        holder.tvDescription.setText(coupon.getDescription());
        holder.tvValidity.setText("Expire time:" + coupon.getValidity());

        holder.addToWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Added item to wallet: " + coupon.getCouponId());
            }
        });

//        // Use Glide to load the image from URL into the ImageView
//        Glide.with(context)
//                .load(movie.getPosterUrl())
//                .into(holder.ivAdsMaker);
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAdsMakerImage;
        TextView tvAdsMaker, tvDiscount, tvDescription, tvValidity;
        Button addToWalletBtn;
//        ConstraintLayout constraintLayout;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAdsMakerImage = itemView.findViewById(R.id.iv_adsMaker_image);
            tvAdsMaker = itemView.findViewById(R.id.tv_adsMaker);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvValidity = itemView.findViewById(R.id.tv_validity);
            addToWalletBtn = itemView.findViewById(R.id.btn_add_to_wallet);
//            constraintLayout = itemView.findViewById(R.id.);
        }
    }
}
