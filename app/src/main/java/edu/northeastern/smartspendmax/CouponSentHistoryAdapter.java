package edu.northeastern.smartspendmax;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.northeastern.smartspendmax.model.Coupon;

public class CouponSentHistoryAdapter extends RecyclerView.Adapter<CouponSentHistoryAdapter.CouponHistoryViewHolder>{
    private List<Coupon> couponList;

    public CouponSentHistoryAdapter(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    public List<Coupon> getCouponList() {
        return couponList;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public CouponHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_sent_history, parent, false);
        return new CouponHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponHistoryViewHolder holder, int position) {
        Coupon currentCoupon = couponList.get(position);
        holder.tvCouponDiscountContent.setText("Discount: " + currentCoupon.getDiscount());
        holder.tvDescription.setText("Description: "+ currentCoupon.getDescription());

        String formattedValidity = currentCoupon.getValidity();
        holder.tvValidity.setText("Valid Date: "+ formattedValidity);
        holder.tvCollectedNumber.setText(String.valueOf(currentCoupon.getCollectedNumber()));
    }



    public static class CouponHistoryViewHolder extends RecyclerView.ViewHolder{
        TextView tvCouponDiscountContent;
        TextView tvDescription;
        TextView tvValidity;
        TextView tvCollectedNumber;

        public CouponHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCouponDiscountContent = itemView.findViewById(R.id.tv_coupon_discount_content);
            tvDescription = itemView.findViewById(R.id.tv_description_content);
            tvValidity = itemView.findViewById(R.id.tv_validity_content);
            tvCollectedNumber = itemView.findViewById(R.id.tv_collected_number);
        }

    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }
}
