package edu.northeastern.smartspendmax;

import static android.content.Context.MODE_PRIVATE;
import static com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL;
import static com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.northeastern.smartspendmax.model.Budget;
import edu.northeastern.smartspendmax.model.Coupon;
import edu.northeastern.smartspendmax.model.SpendingRecord;
import edu.northeastern.smartspendmax.notification.CouponAdapter;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private View view;

    private PieChart overallChart;
    private TextView tv_month, tv_rv_coupons_placeholder;
    private List<PieEntry> pieEntryList = new ArrayList<>();
    private int totalBudget;
    private int totalSpending;
    private FirebaseDatabase db;
    private String currUserId = "";

    private int currMonth;
    private int currYear;

    private String firstDayOfMonth;
    private String lastDayOfMonth;

    private RecyclerView recyclerView;

    private List<String> uncollectedCouponIds = new ArrayList<>();
    private List<Coupon> uncollectedCouponDetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        overallChart = view.findViewById(R.id.chart_overall);
        tv_month = view.findViewById(R.id.tv_month);
        tv_rv_coupons_placeholder = view.findViewById(R.id.tv_rv_coupons_placeholder);
        tv_rv_coupons_placeholder.setVisibility(View.INVISIBLE);
        getCurrentMonth();
        tv_month.setText("Total Summary (" + currYear + "/" + String.format("%02d", currMonth) + ")");

        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currUserId = sharedPref.getString("LastLoggedInUser", "defaultUser");
        Log.d(TAG, "currUserId = " + currUserId);

        // collect data
        totalBudget = 0;
        totalSpending = 0;
        db = FirebaseDatabase.getInstance();
        getTotalSpending();

        // show coupons horizontally
        recyclerView = view.findViewById(R.id.rv_coupons);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));

        getUncollectedCouponIds();

        return view;
    }

    private void getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        currMonth = calendar.get(Calendar.MONTH) + 1;
        currYear = calendar.get(Calendar.YEAR);

        // TODO just for test
        currMonth = 4;

        String formattedMonth = String.format("%02d", currMonth);
        String formattedLastDay = String.format("%02d", calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        firstDayOfMonth = formattedMonth + "/01/" + currYear;
        lastDayOfMonth = formattedMonth + "/" + formattedLastDay + "/" + currYear;
        System.out.println("firstDayOfMonth: " + firstDayOfMonth + ", lastDayOfMonth = " + lastDayOfMonth);
    }

    private void getTotalSpending() {
//        db.getReference("spendings").child(currUserId).orderByChild("/timestamp").startAt("03").endAt("2024").addListenerForSingleValueEvent(
        db.getReference("spendings").child(currUserId).orderByChild("timestamp").startAt(firstDayOfMonth).endAt(lastDayOfMonth + "\uf8ff")
                .addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: " + snapshot.getChildrenCount());
                        for (DataSnapshot entry : snapshot.getChildren()) {
                            SpendingRecord record = entry.getValue(SpendingRecord.class);
                            System.out.println("record.amount = " + record.getAmount());
                            totalSpending += record.getAmount().intValue();

                        }
                        getTotalBudget();
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
        colors.add(getResources().getColor(R.color.colorPrimary));
        colors.add(getResources().getColor(R.color.colorPrimaryLight));

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
        Legend legend = overallChart.getLegend();
        legend.setEnabled(true);
        legend.setOrientation(HORIZONTAL);
        legend.setXOffset(120f);
        legend.setYOffset(0f);
        overallChart.getDescription().setEnabled(false);
        overallChart.setCenterText("Expenses: " + totalSpending +
                "\n" + "Budget: " + totalBudget);
        overallChart.setCenterTextSize(18f);
        overallChart.invalidate();
    }

    private void getUncollectedCouponIds() {
        DatabaseReference userCouponRef = db.getReference("user-coupon/" + currUserId);
        List<String> receivedCoupons = new ArrayList<>();
        List<String> collectedCoupons = new ArrayList<>();
        userCouponRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Retrieve receivedCoupon
                DataSnapshot receivedCouponSnapshot = snapshot.child("receivedCoupon");
                for (DataSnapshot couponSnapshot : receivedCouponSnapshot.getChildren()) {
                    String couponId = couponSnapshot.getKey();
                    System.out.println("received coupon: " + couponId);
                    receivedCoupons.add(couponId);
                }

                // Retrieve collectedCoupon
                DataSnapshot collectedCouponSnapshot = snapshot.child("collectedCoupon");
                for (DataSnapshot couponSnapshot : collectedCouponSnapshot.getChildren()) {
                    String couponId = couponSnapshot.getKey();
                    System.out.println("collected coupon: " + couponId);
                    collectedCoupons.add(couponId);
                }
                for (String c : receivedCoupons) {
                    if (!collectedCoupons.contains(c)) {
                        uncollectedCouponIds.add(c);
                    }
                }
                if (uncollectedCouponIds.size() == 0) {
                    tv_rv_coupons_placeholder.setVisibility(View.VISIBLE);
                } else {
                    getCouponDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCouponDetails() {
        for (String couponId : uncollectedCouponIds) {
            Log.d(TAG, "uncollected coupon id: " + couponId);
            db.getReference("coupons").child(couponId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    Log.d(TAG, "onDataChange: snapshot size: " + snapshot.getChildrenCount());
                    Coupon coupon = snapshot.getValue(Coupon.class);
                    coupon.setCouponId(couponId);
                    uncollectedCouponDetails.add(coupon);
                    CouponAdapter adapter = new CouponAdapter(view.getContext(), uncollectedCouponDetails, currUserId);
                    Log.d(TAG, "uncollectedCouponDetails size: " + uncollectedCouponDetails.size());
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}