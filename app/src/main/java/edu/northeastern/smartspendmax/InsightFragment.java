package edu.northeastern.smartspendmax;

import edu.northeastern.smartspendmax.DataFetchListener;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class InsightFragment extends Fragment {
    private List<PieEntry> pieEntryList =new ArrayList<>();
    private PieChart pieChart;
    private RecyclerView recyclerView;
    private CategoryInsightAdapter insightAdapter;
    private List<CategoryInsight> categoryInsightsList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private LocalDate date;
    private Integer curMonth = 3;
    private String curUserName = "user1";
    private int dataFetchCompleteCounter = 0;
    double spendingHousing = 0.0;
    double budgetHousing = 1000.0;
    double spendingTransportation = 0.0;
    double budgetTransportation = 1000.0;
    double spendingUtilities = 0.0;
    double budgetUtilities = 1000.0;
    double spendingGrocery = 0.0;
    double budgetGrocery = 1000.0;
    double spendingPersonalExpense = 0.0;
    double budgetPersonalExpense = 1000.0;
    double spendingOther = 0.0;
    double budgetOther = 1000.0;
    private String TAG = "--------INSIGHT FRAG------";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_insight, container, false);
        pieChart = view.findViewById(R.id.expenseChart);
        recyclerView = view.findViewById(R.id.insight_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize InsightDatabaseHandler and fetch data
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        //InsightDatabaseHandler databaseHandler = new InsightDatabaseHandler(databaseReference, new ArrayList<>(),
        //        insightAdapter, curMonth, curUser, this);

        //databaseHandler.fetchBudgetAndSpending();
        fetchSpendingData();
        fetchBudgetData();
        onDataFetchComplete();

        return view;
    }


    public void fetchSpendingData() {

        Log.d(TAG, "Starting to fetch budget and spending data.");
        databaseReference.child("spendings").child(curUserName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        resetSpendingAmounts();
                        // Process each budget entry
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                            if(map != null) {
                                String category = Objects.requireNonNull(map.get("category")).toString();
                                String timestamp = Objects.requireNonNull(map.get("timestamp")).toString();
                                double amount = ((Number) Objects.requireNonNull(map.get("amount"))).doubleValue();
                                date = convertStringToDate(timestamp);
                                // Log.d(TAG, "Processing transaction for category: " + category + ", amount: " + amount + " on " + timestamp);

                                if(timestamp.startsWith("03")){
                                    //Log.d(TAG, "Processing transaction for category: " + category + ", amount: " + amount);

                                    SpendingTransaction transaction = null;
                                    switch (category) {
                                        case "housing":
                                            transaction = new SpendingTransaction(Category.HOUSING, amount);
                                            spendingHousing += amount;
                                            break;
                                        case "transportation":
                                            transaction = new SpendingTransaction(Category.TRANSPORTATION,amount);
                                            spendingTransportation += amount;
                                            break;
                                        case "utilities":
                                            transaction = new SpendingTransaction(Category.UTILITIES, amount);
                                            spendingUtilities += amount;
                                            break;
                                        case "grocery":
                                            transaction = new SpendingTransaction(Category.GROCERY, amount);
                                            spendingGrocery += amount;
                                            break;
                                        case "personal expense":
                                            transaction = new SpendingTransaction(Category.PERSONAL_EXPENSE, amount);
                                            spendingPersonalExpense += amount;
                                            break;
                                        case "other":
                                            transaction = new SpendingTransaction(Category.OTHER, amount);
                                            spendingOther += amount;
                                            break;
                                    }

                                }
                            }
                        }
                        setupPieChart();
                        onDataFetchComplete();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("DBError", "Spending fetch cancelled", databaseError.toException());
                    }
                });
    }

    private void fetchBudgetData() {
        databaseReference.child("budget").child(curUserName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        resetBudgetAmounts();

                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map != null) {
                            for (String key : map.keySet()) {
                                if (!key.equals("timestamp")) {
                                    double budget = ((Number) map.get(key)).doubleValue();
                                    if (key.equals("housing")) {
                                        budgetHousing = budget;
                                    } else if (key.equals("transportation")) {
                                        budgetTransportation = budget;
                                    } else if (key.equals("grocery")) {
                                        budgetGrocery = budget;
                                    } else if (key.equals("utilities")) {
                                        budgetUtilities = budget;
                                    } else if (key.equals("personalExpense")) {
                                        budgetPersonalExpense = budget;
                                    } else if (key.equals("other")) {
                                        budgetOther = budget;
                                    }
                                }
                            }
                            Log.d(TAG, "Budget Housing: " + budgetHousing + ", Budget Grocery: " + budgetGrocery);
                        }
                        onDataFetchComplete();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("DBError", "Budget fetch cancelled", databaseError.toException());
                    }
            });

        }

    private void onDataFetchComplete() {
        dataFetchCompleteCounter++;
        if (dataFetchCompleteCounter >= 2) { // Both fetch operations are complete
            createCategoryInsights();
            setupRecyclerView();

        }
    }

    private void createCategoryInsights() {
        categoryInsightsList = new ArrayList<>();
        categoryInsightsList.add(new CategoryInsight("Housing", spendingHousing, budgetHousing));
        categoryInsightsList.add(new CategoryInsight("Transportation", spendingTransportation, budgetTransportation));
        categoryInsightsList.add(new CategoryInsight("Grocery", spendingGrocery, budgetGrocery));
        categoryInsightsList.add(new CategoryInsight("Utility", spendingUtilities, budgetUtilities));
        categoryInsightsList.add(new CategoryInsight("PersonalExpense", spendingPersonalExpense, budgetPersonalExpense));
        categoryInsightsList.add(new CategoryInsight("Other", spendingOther, budgetOther));
        Log.d(TAG, "CategoryList: " + categoryInsightsList.size());
    }


    private void setupPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.colorHousing));
        colors.add(getResources().getColor(R.color.colorTransportation));
        colors.add(getResources().getColor(R.color.colorGrocery));
        colors.add(getResources().getColor(R.color.colorUtility));
        colors.add(getResources().getColor(R.color.colorPersonalExpense));
        colors.add(getResources().getColor(R.color.colorOther));



        double totalSpending = spendingHousing + spendingTransportation + spendingUtilities +
                spendingGrocery + spendingPersonalExpense + spendingOther;

        // Only add entries for categories with spending
        if (spendingHousing > 0) pieEntries.add(new PieEntry((float) spendingHousing, "Housing"));
        if (spendingTransportation > 0) pieEntries.add(new PieEntry((float) spendingTransportation, "Transportation"));
        if (spendingUtilities > 0) pieEntries.add(new PieEntry((float) spendingUtilities, "Utilities"));
        if (spendingGrocery > 0) pieEntries.add(new PieEntry((float) spendingGrocery, "Grocery"));
        if (spendingPersonalExpense > 0) pieEntries.add(new PieEntry((float) spendingPersonalExpense, "PersonalExpense"));
        if (spendingOther > 0) pieEntries.add(new PieEntry((float) spendingOther, "Other"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
       // pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Use predefined color template
        pieDataSet.setDrawValues(true);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        // Additional chart styling
        pieChart.setDrawEntryLabels(false);
        pieChart.setExtraOffsets(8, 8, 8, 8);
        pieChart.getLegend().setEnabled(false);
        pieChart.setCenterText(String.format(Locale.US, "Total Expenses\n%.2f", totalSpending));
        pieChart.setCenterTextSize(20f);

        pieChart.invalidate();

        pieDataSet.setValueTextColor(getResources().getColor(R.color.colorWhite));
        pieDataSet.setValueTextSize(16f);
    }

    private void setupRecyclerView() {
        createCategoryInsights();
        insightAdapter = new CategoryInsightAdapter(categoryInsightsList);
        //Log.d(TAG, "can you get insightssssss: " + insightAdapter.getItemInsights());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(insightAdapter);
    }

    private LocalDate convertStringToDate(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return LocalDate.parse(timestamp,formatter);
    }

    private void resetSpendingAmounts() {
        spendingHousing = 0.0;
        spendingTransportation = 0.0;
        spendingUtilities = 0.0;
        spendingGrocery = 0.0;
        spendingPersonalExpense = 0.0;
        spendingOther = 0.0;
    }

    private void resetBudgetAmounts() {
        budgetHousing = 1000.0;
        budgetTransportation = 1000.0;
        budgetUtilities = 1000.0;
        budgetGrocery = 1000.0;
        budgetPersonalExpense = 1000.0;
        budgetOther = 1000.0;
    }





}