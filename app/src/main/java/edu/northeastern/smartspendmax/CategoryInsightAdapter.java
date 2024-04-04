package edu.northeastern.smartspendmax;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CategoryInsightAdapter extends RecyclerView.Adapter<CategoryInsightAdapter.ViewHolder> {
    private List<CategoryInsight> itemInsights;
    int progressColor;

    private String TAG = "------CatAdapter------";

    public CategoryInsightAdapter(List<CategoryInsight> itemInsights) {
        this.itemInsights = itemInsights; // Corrected
    }

    public List<CategoryInsight> getItemInsights() {
        return itemInsights;
    }

    public void setItemInsights(List<CategoryInsight> itemInsights) {
        this.itemInsights = itemInsights;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your views here
        TextView budgetUsedView;
        ProgressBar budgetProgressBar;
        TextView budgetLeftView;
        ImageView itemIcon;
        ImageView warningDot;

        public ViewHolder(View itemView) {
            super(itemView);
            budgetUsedView = itemView.findViewById(R.id.item_text_left);
            budgetProgressBar = itemView.findViewById(R.id.category_progress_bar);
            budgetLeftView = itemView.findViewById(R.id.item_text_right);
            itemIcon = itemView.findViewById(R.id.item_icon);
            warningDot = itemView.findViewById(R.id.warning_dot);
        }
    }

    @Override
    public CategoryInsightAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_insight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoryInsight item = itemInsights.get(position);
        String budgetUsedFormatted = String.format(Locale.getDefault(), "%.2f", item.getBudgetUsed());
        String totalBudgetFormatted = String.format(Locale.getDefault(), "%.2f", item.getTotal());



        int progress = (int) ((item.getBudgetUsed() / item.getTotal()) * 100);
        Log.d(TAG, String.valueOf(position) + " category is " + item.getCategoryName() + " budget is " + budgetUsedFormatted);
        holder.budgetUsedView.setText(budgetUsedFormatted + "/" + totalBudgetFormatted + " Used");

        holder.budgetProgressBar.setProgress(progress);

        if (item.getBudgetUsed() > item.getTotal()) {
            // Display warning dot
            holder.warningDot.setVisibility(View.VISIBLE);

            // Change text to red
            holder.budgetUsedView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent));
            holder.budgetLeftView.setText("Over Budget");
            holder.budgetLeftView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent));
        } else {
            // Hide warning dot
            holder.warningDot.setVisibility(View.INVISIBLE);
            String budgetLeftFormatted = String.format(Locale.getDefault(), "%.2f", (item.getTotal() - item.getBudgetUsed()));
            holder.budgetLeftView.setText(budgetLeftFormatted + " Left");

            // Reset text color if needed
            holder.budgetUsedView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimaryDark));
            holder.budgetLeftView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimaryDark));
        }


        switch (item.getCategoryName()) {
            case "Housing":
                holder.itemIcon.setImageResource(R.drawable.housing_background);
                progressColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorHousing);
                break;
            case "Transportation":
                holder.itemIcon.setImageResource(R.drawable.transpotation_background);
                progressColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorTransportation);
                break;
            case "Grocery":
                holder.itemIcon.setImageResource(R.drawable.grocery_background);
                progressColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorGrocery);
                break;
            case "Utility":
                holder.itemIcon.setImageResource(R.drawable.utility_background);
                progressColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorUtility);
                break;
            case "PersonalExpense":
                holder.itemIcon.setImageResource(R.drawable.personalexpense_background);
                progressColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPersonalExpense);
                break;
            case "Other":
                holder.itemIcon.setImageResource(R.drawable.otherspending_background);
                progressColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorOther);
                break;
            default:
                holder.itemIcon.setImageResource(R.drawable.otherspending_background);
                holder.budgetProgressBar.setProgressDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.progress_bar));
                break;
        }
        Drawable progressDrawable = holder.budgetProgressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
        holder.budgetProgressBar.setProgressDrawable(progressDrawable);

    }

    @Override
    public int getItemCount() {
        return itemInsights.size();
    }
}