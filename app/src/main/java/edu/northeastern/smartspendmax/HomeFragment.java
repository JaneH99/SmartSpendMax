package edu.northeastern.smartspendmax;

import static android.content.Context.MODE_PRIVATE;
import static com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL;
import static com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
    private TextView tv_month;
    private List<PieEntry> pieEntryList = new ArrayList<>();
    private int totalBudget;
    private int totalSpending;
    private double spendingPercentage;
    private FirebaseDatabase db;
    private String currUserId = "";

    private int currMonth;
    private int currYear;

    private String firstDayOfMonth;
    private String lastDayOfMonth;
    private int orientation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        overallChart = view.findViewById(R.id.chart_overall);
        tv_month = view.findViewById(R.id.tv_month);

        getCurrentMonth();
        tv_month.setText(currYear + "/" + String.format("%02d", currMonth));

        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currUserId = sharedPref.getString("LastLoggedInUser", "defaultUser");

        totalBudget = 0;
        totalSpending = 0;
        db = FirebaseDatabase.getInstance();
        getTotalSpending();

        return view;
    }

    private void getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        currMonth = calendar.get(Calendar.MONTH) + 1;
        currYear = calendar.get(Calendar.YEAR);

        String formattedMonth = String.format("%02d", currMonth);
        String formattedLastDay = String.format("%02d", calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        firstDayOfMonth = formattedMonth + "/01/" + currYear;
        lastDayOfMonth = formattedMonth + "/" + formattedLastDay + "/" + currYear;
        Log.d(TAG, "firstDayOfMonth: " + firstDayOfMonth + ", lastDayOfMonth = " + lastDayOfMonth);
    }

    private void getTotalSpending() {
        db.getReference("spendings").child(currUserId).orderByChild("timestamp").startAt(firstDayOfMonth).endAt(lastDayOfMonth + "\uf8ff")
                .addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "get spending records: " + snapshot.getChildrenCount());
                        for (DataSnapshot entry : snapshot.getChildren()) {
                            SpendingRecord record = entry.getValue(SpendingRecord.class);
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
                        Budget budget = snapshot.getValue(Budget.class);
                        totalBudget += budget.getHousing()
                                + budget.getGrocery()
                                + budget.getUtilities()
                                + budget.getTransportation()
                                + budget.getPersonalExpense()
                                + budget.getOther();
                        assembleChart();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void assembleChart() {
        // Calculate spending percentage
        double spendingPercentage = (double) totalSpending / totalBudget * 100;
        Log.d(TAG, "SPENDING PERCENTAGE IS: " + spendingPercentage + "SPENDING IS: " + totalSpending + "BUDGET IS: " + totalBudget);

        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieEntryList.add(new PieEntry(totalSpending, "Total Spending"));
        pieEntryList.add(new PieEntry(totalBudget - totalSpending, "Remaining Budget"));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.colorPrimary));
        colors.add(getResources().getColor(R.color.colorDarkGrey));
        pieDataSet.setColors(colors);

        pieDataSet.setDrawValues(true);
        pieDataSet.setValueFormatter(new PercentFormatter(overallChart));
        overallChart.setUsePercentValues(true);

        PieData pieData = new PieData(pieDataSet);
        overallChart.setData(pieData);
        overallChart.setDrawEntryLabels(false);
        overallChart.getLegend().setEnabled(false);
        overallChart.getDescription().setEnabled(false);
        overallChart.setHoleColor(getResources().getColor(R.color.colorBlack));

        // Format the center text
        String formattedCenterText = String.format("%.1f%%\nSpending: %d/%d", spendingPercentage, totalSpending, totalBudget);
        SpannableString centerText = new SpannableString(formattedCenterText);

        // Set custom font and size for the percentage
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.tt_ramillas_bold);
        int percentEndIndex = formattedCenterText.indexOf("%") + 1;
        centerText.setSpan(new CustomTypefaceSpan("", typeface), 0, percentEndIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        centerText.setSpan(new RelativeSizeSpan(2f), 0, percentEndIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        centerText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, percentEndIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        // Set color and size for the rest of the text
        centerText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryLight)), percentEndIndex, formattedCenterText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        centerText.setSpan(new RelativeSizeSpan(0.5f), percentEndIndex, formattedCenterText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        overallChart.setCenterText(centerText);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            overallChart.setCenterTextSize(14f);
        } else {
            overallChart.setCenterTextSize(22f);
        }
        overallChart.invalidate(); // Refresh the chart to apply changes
    }


//    private void getUncollectedCouponIds() {
//        DatabaseReference userCouponRef = db.getReference("user-coupon/" + currUserId);
//        List<String> receivedCoupons = new ArrayList<>();
//        List<String> collectedCoupons = new ArrayList<>();
//        userCouponRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // Retrieve receivedCoupon
//                DataSnapshot receivedCouponSnapshot = snapshot.child("receivedCoupon");
//                for (DataSnapshot couponSnapshot : receivedCouponSnapshot.getChildren()) {
//                    String couponId = couponSnapshot.getKey();
//                    receivedCoupons.add(couponId);
//                }
//
//                // Retrieve collectedCoupon
//                DataSnapshot collectedCouponSnapshot = snapshot.child("collectedCoupon");
//                for (DataSnapshot couponSnapshot : collectedCouponSnapshot.getChildren()) {
//                    String couponId = couponSnapshot.getKey();
//                    collectedCoupons.add(couponId);
//                }
//                for (String c : receivedCoupons) {
//                    if (!collectedCoupons.contains(c)) {
//                        uncollectedCouponIds.add(c);
//                    }
//                }
//                if (uncollectedCouponIds.size() == 0) {
//                    tv_rv_coupons_placeholder.setVisibility(View.VISIBLE);
//                } else {
//                    tv_rv_coupons_placeholder.setVisibility(View.INVISIBLE);
//                    getCouponDetails();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    private void getCouponDetails() {
//        for (String couponId : uncollectedCouponIds) {
//            Log.d(TAG, "uncollected coupon id: " + couponId);
//            db.getReference("coupons").child(couponId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    Coupon coupon = snapshot.getValue(Coupon.class);
//                    coupon.setCouponId(couponId);
//                    coupon.setCollected(false);
//                    uncollectedCouponDetails.add(coupon);
//                    CouponAdapter adapter = new CouponAdapter(view.getContext(), uncollectedCouponDetails, currUserId);
//                    recyclerView.setAdapter(adapter);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
//
//    }
}