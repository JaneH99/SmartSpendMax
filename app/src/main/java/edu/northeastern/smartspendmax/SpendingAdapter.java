package edu.northeastern.smartspendmax;

import android.annotation.SuppressLint;
import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.TransitionRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SpendingAdapter extends RecyclerView.Adapter<SpendingAdapter.SpendingHolder> {

    private List<SpendingInOneCategory> spendingInCategories;
    //    private static final int NUM_OF_CATEGORIES = 6;
    private List<SpendingTransaction> list = new ArrayList<>();
    private String TAG = "------RV------";

    public SpendingAdapter(List<SpendingInOneCategory> spendingInCategories) {
        this.spendingInCategories = spendingInCategories;
    }

    @NonNull
    @Override
    public SpendingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_spending_category,parent,false);
        return new SpendingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpendingHolder holder, int position) {
        Log.d(TAG,String.valueOf(position));
        SpendingInOneCategory spendingInCurrentCategory = spendingInCategories.get(position);

        //Set icon
        Category currentCategory = spendingInCurrentCategory.getCategory();
        if(currentCategory == Category.HOUSING) {
            holder.categoryIcon.setImageResource(R.drawable.housing_background);
        } else if(currentCategory == Category.GROCERY) {
            holder.categoryIcon.setImageResource(R.drawable.grocery_background);
        } else if(currentCategory == Category.TRANSPORTATION) {
            holder.categoryIcon.setImageResource(R.drawable.transpotation_background);
        } else if(currentCategory == Category.UTILITIES) {
            holder.categoryIcon.setImageResource(R.drawable.utility_background);
        } else if(currentCategory == Category.PERSONAL_EXPENSE) {
            holder.categoryIcon.setImageResource(R.drawable.personalexpense_background);
        } else if(currentCategory == Category.OTHER) {
            holder.categoryIcon.setImageResource(R.drawable.otherspending_background);
        }

        //Set category
        holder.spendingCategory.setText(spendingInCurrentCategory.getCategory().getStringValue());

        //Set total amount for the category
        double totalSpending = 0.00;
        for(SpendingTransaction transaction: spendingInCurrentCategory.getSpendingInTheCategory()) {
            totalSpending += transaction.getAmount();
        }
        String formattedTotalSpending = String.format("%.2f", totalSpending);
        holder.spendingCategoryAmount.setText(formattedTotalSpending);

        boolean isExpandable = spendingInCurrentCategory.isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.INVISIBLE);
        if(isExpandable) {
            holder.arrowImage.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
        } else {
            holder.arrowImage.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        }

        //Set nested recycler view
        SpendingDetailAdapter adapter = new SpendingDetailAdapter(list);
        holder.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.nestedRecyclerView.setHasFixedSize(true);
        holder.nestedRecyclerView.setAdapter(adapter);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if(!spendingInCurrentCategory.isExpandable()) {
                    spendingInCurrentCategory.setExpandable(!spendingInCurrentCategory.isExpandable());
                    list = spendingInCurrentCategory.getSpendingInTheCategory();
                    notifyItemChanged(holder.getAdapterPosition());
                } else {
                    spendingInCurrentCategory.setExpandable(!spendingInCurrentCategory.isExpandable());
                    list = new ArrayList<>();
                    notifyItemChanged(holder.getAdapterPosition());
                }

            }
        });

        Log.d(TAG,"Binded");
    }

    @Override
    public int getItemCount() {
        return spendingInCategories.size();
    }

    public static class SpendingHolder extends RecyclerView.ViewHolder {

        private ImageView categoryIcon;
        private TextView spendingCategory;
        private TextView spendingCategoryAmount;
        private ImageView arrowImage;
        private LinearLayout linearLayout;
        private RelativeLayout expandableLayout;
        private RecyclerView nestedRecyclerView;

        public SpendingHolder(View view) {
            super(view);
            spendingCategory = itemView.findViewById(R.id.spending_category);
            spendingCategoryAmount = itemView.findViewById(R.id.spending_category_amount);
            arrowImage = itemView.findViewById(R.id.arrow_image);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            nestedRecyclerView = itemView.findViewById(R.id.nested_recycler_view);
            categoryIcon = itemView.findViewById(R.id.category_icon);
        }
    }
}