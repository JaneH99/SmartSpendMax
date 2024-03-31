package edu.northeastern.smartspendmax;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpendingDetailAdapter extends RecyclerView.Adapter<SpendingDetailAdapter.SpendingDetailHolder> {

    private List<SpendingTransaction> transactions;

    public SpendingDetailAdapter(List<SpendingTransaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public SpendingDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_spending_detail,parent,false);
        return new SpendingDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpendingDetailHolder holder, int position) {
        SpendingTransaction transaction = transactions.get(position);
        holder.transactionDate.setText(String.valueOf(transaction.getTransactionDate()));
        holder.vendor.setText(transaction.getVendor());
        String formattedTotalSpending = String.format("%.2f", transaction.getAmount());
        holder.transactionAmount.setText(formattedTotalSpending);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class SpendingDetailHolder extends RecyclerView.ViewHolder {

        private TextView transactionDate;
        private TextView vendor;
        private TextView transactionAmount;

        public SpendingDetailHolder(View view) {
            super(view);
            transactionDate = itemView.findViewById(R.id.transaction_date);
            vendor = itemView.findViewById(R.id.vendor);
            transactionAmount = itemView.findViewById(R.id.transaction_amount);
        }
    }
}
