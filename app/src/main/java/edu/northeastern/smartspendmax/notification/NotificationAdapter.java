package edu.northeastern.smartspendmax.notification;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.smartspendmax.R;
import edu.northeastern.smartspendmax.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Log.d("NotificationAdapter", "onBindViewHolder");
        Notification notification = notificationList.get(position);
        Log.d("NotificationAdapter", "notification: " + notification.getCouponId());
        holder.tvAdsMaker.setText(notification.getAdMakerName());
        holder.tvDiscount.setText(notification.getDiscount());
        holder.tvDescription.setText(notification.getDescription());
        holder.tvValidity.setText("Expire time:" + notification.getValidity());

        holder.addToWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle adding item to wallet
                // For demonstration purposes, let's just print the item
                System.out.println("Added item to wallet: " + notification.getCouponId());
            }
        });

//        // Use Glide to load the image from URL into the ImageView
//        Glide.with(context)
//                .load(movie.getPosterUrl())
//                .into(holder.ivAdsMaker);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
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
