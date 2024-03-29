package edu.northeastern.smartspendmax;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

public class InsightFragment extends Fragment {
    List<PieEntry> pieEntryList =new ArrayList<>();
    PieChart pieChart;
    RecyclerView recyclerView;
    CategoryInsightAdapter adapter;
    List<CategoryInsight> categoryInsights;
    final int OFFSET_VALUE = 3;
    final int ICON_MARGIN = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_insight, container, false);
        pieChart =view.findViewById(R.id.expenseChart);
        setValues();
        setUpChart();

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.insight_recyclerView);
        setupRecyclerView();

        return view;

    }

    private void setUpChart() {
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.colorHousing));
        colors.add(getResources().getColor(R.color.colorTransportation));
        colors.add(getResources().getColor(R.color.colorGrocery));
        colors.add(getResources().getColor(R.color.colorUtility));
        colors.add(getResources().getColor(R.color.colorPersonalExpense));
        colors.add(getResources().getColor(R.color.colorOther));

        pieDataSet.setColors(colors);

        pieDataSet.setDrawValues(true); // Ensure values are drawn
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart)); // Format values as percentages
        pieChart.setUsePercentValues(true);

        ArrayList<Drawable> icons = new ArrayList<>();
        icons.add(ContextCompat.getDrawable(getContext(), R.drawable.housing_background));
        icons.add(ContextCompat.getDrawable(getContext(), R.drawable.transpotation_background));
        icons.add(ContextCompat.getDrawable(getContext(), R.drawable.grocery_background));
        icons.add(ContextCompat.getDrawable(getContext(), R.drawable.utility_background));
        icons.add(ContextCompat.getDrawable(getContext(), R.drawable.personalexpense_background));
        icons.add(ContextCompat.getDrawable(getContext(), R.drawable.otherspending_background));

        pieDataSet.setValueTextColor(getResources().getColor(R.color.colorWhite));
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setDrawEntryLabels(false);
        pieChart.setExtraOffsets(8, 8, 8, 8);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Total Expenses"+
                "\n" +"8,000");
        pieChart.setCenterTextSize(24f);
        pieChart.invalidate();

    }

    private void setupRecyclerView() {
        // Initialize your data list
        categoryInsights = new ArrayList<>();
        // Dummy data, replace or modify according to your actual data source
        categoryInsights.add(new CategoryInsight("Housing", 1800, 2000, 200));
        categoryInsights.add(new CategoryInsight("Transportation", 420.52, 800, 200));

        adapter = new CategoryInsightAdapter(categoryInsights);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setValues(){
        pieEntryList.add(new PieEntry(1800,"Housing"));
        pieEntryList.add(new PieEntry(600,"Transportation"));
        pieEntryList.add(new PieEntry(800,"Grocery"));
        pieEntryList.add(new PieEntry(200,"Utilities"));
        pieEntryList.add(new PieEntry(500,"Personal Expense"));
        pieEntryList.add(new PieEntry(1200,"Other"));

         }



}