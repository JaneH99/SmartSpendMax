package edu.northeastern.smartspendmax;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.northeastern.smartspendmax.model.Budget;
import edu.northeastern.smartspendmax.model.SpendingRecord;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private PieChart overallChart;
    private TextView tv_month;
    private List<PieEntry> pieEntryList = new ArrayList<>();
    private int totalBudget;
    private int totalSpending;
    private FirebaseDatabase db;
    private String currUserId = "user1";

    private int currMonth;
    private int currYear;

    private String firstDayOfMonth;
    private String lastDayOfMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        overallChart = view.findViewById(R.id.chart_overall);
        tv_month = view.findViewById(R.id.tv_month);
        // collect data
        totalBudget = 0;
        totalSpending = 0;
        db = FirebaseDatabase.getInstance();

        Log.d(TAG, "onCreateView: currUserId = " + currUserId);
        getCurrentMonth();
        tv_month.setText("Month: " + currYear + "/" + String.format("%02d", currMonth));

        getTotalSpending();
        getTotalBudget();

        return view;
    }

    private void getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        currMonth = calendar.get(Calendar.MONTH) + 1;
        currYear = calendar.get(Calendar.YEAR);

        currMonth = 4;

        String formattedMonth = String.format("%02d", currMonth);
//        String formattedDay = "01"; // For the first day of the month
        String formattedLastDay = String.format("%02d", calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        firstDayOfMonth = formattedMonth + "/01/" + currYear;
        lastDayOfMonth = formattedMonth + "/" + formattedLastDay + "/" + currYear;
        System.out.println("firstDayOfMonth: " + firstDayOfMonth + ", lastDayOfMonth = " + lastDayOfMonth);
    }

    private void getTotalSpending() {
//        db.getReference("spendings").child(currUserId).orderByChild("/timestamp").startAt("03").endAt("2024").addListenerForSingleValueEvent(
        db.getReference("spendings").child(currUserId).orderByChild("timestamp").startAt(firstDayOfMonth).endAt(lastDayOfMonth + "\uf8ff").addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: " + snapshot.getChildrenCount());
                        for (DataSnapshot entry : snapshot.getChildren()) {
                            SpendingRecord record = entry.getValue(SpendingRecord.class);
                            System.out.println("record.amount = " + record.getAmount());
                            totalSpending += record.getAmount().intValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void getTotalBudget() {
        db.getReference("budget").child(currUserId + "/" + currYear + String.format("-%02d", currMonth)).addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "budget onDataChange: " + snapshot.getChildrenCount() + ", totalBudget = " + totalBudget);
                        Budget budget = snapshot.getValue(Budget.class);
                        System.out.println("budget.getHousing = " + budget.getHousing());
                        System.out.println("budget.getGrocery = " + budget.getGrocery());
                        System.out.println("budget.getUtilities = " + budget.getUtilities());
                        System.out.println("budget.getTransportation = " + budget.getTransportation());
                        System.out.println("budget.getPersonalExpense = " + budget.getPersonalExpense());
                        System.out.println("budget.getOther = " + budget.getOther());

                        totalBudget += budget.getHousing()
                                + budget.getGrocery()
                                + budget.getUtilities()
                                + budget.getTransportation()
                                + budget.getPersonalExpense()
                                + budget.getOther();
                        System.out.println("totalBudget = " + totalBudget);
                        assembleChart();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void assembleChart() {
        // assemble chart
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieEntryList.add(new PieEntry(totalSpending, "Total Spending"));
        pieEntryList.add(new PieEntry(totalBudget - totalSpending, "Remaining Budget"));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.colorPrimaryLight));
        colors.add(getResources().getColor(R.color.colorPrimary));

        pieDataSet.setColors(colors);

        pieDataSet.setDrawValues(true); // Ensure values are drawn
        pieDataSet.setValueFormatter(new PercentFormatter(overallChart)); // Format values as percentages
        overallChart.setUsePercentValues(true);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.colorWhite));
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        overallChart.setData(pieData);
        overallChart.setDrawEntryLabels(false);
        overallChart.setExtraOffsets(8, 8, 8, 8);
        overallChart.getLegend().setEnabled(false);
        overallChart.getDescription().setEnabled(false);
        overallChart.setCenterText("Total Expenses: " + totalSpending +
                "\n" + "Total Budget: " + totalBudget);
        overallChart.setCenterTextSize(18f);
        overallChart.invalidate();
    }
}