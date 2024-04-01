package edu.northeastern.smartspendmax;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
    final int OFFSET_VALUE = 3;
    final int ICON_MARGIN = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_insight, container, false);
        pieChart =view.findViewById(R.id.expenseChart);
        setValues();
        setUpChart();

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

        // Calculate the angle step
//        int chartRadiusPlusMargin = pieChart.getWidth() / 2 + ICON_MARGIN;
//        float angleStep = 360f / pieEntryList.size();
//        ArrayList<MPPointF> offsets = new ArrayList<>();
//
//        // Loop to assign icons to each PieEntry (assuming the order matches)
//        for (int i = 0; i < pieEntryList.size(); i++) {
//            float angle = (float) Math.toRadians(-angleStep * i - angleStep / 2);
//            // Calculate offset for icon
//            float xOffset = (float) (Math.cos(angle) * chartRadiusPlusMargin);
//            float yOffset = (float) (Math.sin(angle) * chartRadiusPlusMargin +5);
//            offsets.add(new MPPointF(xOffset, yOffset));
//            pieEntryList.get(i).setIcon(icons.get(i));
//        }

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

    private void setValues(){
        pieEntryList.add(new PieEntry(1800,"Housing"));
        pieEntryList.add(new PieEntry(600,"Transport"));
        pieEntryList.add(new PieEntry(800,"Grocery"));
        pieEntryList.add(new PieEntry(200,"Utilities"));
        pieEntryList.add(new PieEntry(500,"Personal Expense"));
        pieEntryList.add(new PieEntry(1200,"Other"));
    }
}