package edu.northeastern.smartspendmax;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryInsightAdapter extends RecyclerView.Adapter<CategoryInsightAdapter.ViewHolder> {
    private List<CategoryInsight> itemInsights;
    int progressColor;

    public CategoryInsightAdapter(List<CategoryInsight> itemInsights) {
        this.itemInsights = itemInsights; // Corrected
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your views here
        TextView budgetUsedView;
        ProgressBar budgetProgressBar;
        TextView budgetLeftView;
        ImageView itemIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            budgetUsedView = itemView.findViewById(R.id.item_text_left);
            budgetProgressBar = itemView.findViewById(R.id.category_progress_bar);
            budgetLeftView = itemView.findViewById(R.id.item_text_right);
            itemIcon = itemView.findViewById(R.id.item_icon);
        }
    }

    @Override
    public CategoryInsightAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_insight, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoryInsight item = itemInsights.get(position);
        holder. budgetUsedView.setText(item.getBudgetUsed() + "/" +item.getTotal() + " Used");
        int progress = (int) ((item.getBudgetUsed() / item.getTotal()) * 100);
        holder.budgetProgressBar.setProgress(progress);
        holder.budgetLeftView.setText(((item.getTotal()) - item.getBudgetUsed()) + " Left");

        switch (item.getCategoryName()) {
            case "Housing":
                holder.itemIcon.setImageResource(R.drawable.housing_background);
                progressColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorHousing);
                break;
            case "Transportation":
                holder.itemIcon.setImageResource(R.drawable.transpotation_background);
                progressColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorTransportation);
                break;
            // Add more cases for other categories
            default:
                holder.itemIcon.setImageResource(R.drawable.otherspending_background); // Default or other category icon
                holder.budgetProgressBar.setProgressDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.progress_bar)); // Default progress drawable
                break;
        }
        Drawable progressDrawable = holder.budgetProgressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
        holder.budgetProgressBar.setProgressDrawable(progressDrawable);

    }

    public void updateData(List<CategoryInsight> newInsights) {
        itemInsights.clear();
        itemInsights.addAll(newInsights);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemInsights.size();
    }
}