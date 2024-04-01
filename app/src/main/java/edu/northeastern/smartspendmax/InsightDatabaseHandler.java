package edu.northeastern.smartspendmax;

import static com.google.common.base.Predicates.equalTo;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsightDatabaseHandler {

    private DatabaseReference mDatabase;
    private List<CategoryInsight> categoryInsights;
    private CategoryInsightAdapter adapter;
    private String currentMonth = "2024-03";
    private String username = "user1";
    public final Double DEFAULT_BUDGET = 1000.0;
    double housingBudget = DEFAULT_BUDGET;
    double transportationBudget = DEFAULT_BUDGET;
    double utilitiesBudget = DEFAULT_BUDGET;
    double groceryBudget = DEFAULT_BUDGET;
    double personalExpenseBudget = DEFAULT_BUDGET;
    double otherBudget = DEFAULT_BUDGET;

    public InsightDatabaseHandler(DatabaseReference mDatabase, List<CategoryInsight> categoryInsights,
                                  CategoryInsightAdapter adapter, String currentMonth, String username) {
        this.mDatabase = mDatabase;
        this.categoryInsights = categoryInsights;
        this.adapter = adapter;
        this.currentMonth = currentMonth;
        this.username = username;
    }

    public void fetchBudgetAndSpending() {
        // Query the budget node
        mDatabase.child("budget").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("FirebaseData", "Data fetched successfully.");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("FirebaseData", "Data: " + snapshot.getValue());
                    // Extract the month
                    String budgetMonth = snapshot.child("month").getValue(String.class);
                    // Check if the budget is for the current month
                    if (budgetMonth != null && budgetMonth.startsWith(currentMonth)) {
                        housingBudget = (double)snapshot.child("housing").getValue();
                        // Other budget categories can be assigned similarly...

                        // Query the spending node for the user
                        mDatabase.child("spending").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Double> spendingSums = new HashMap<>();
                                for (DataSnapshot spendingSnapshot : dataSnapshot.getChildren()) {
                                    String spendingTimestamp = spendingSnapshot.child("timestamp").getValue(String.class);
                                    if (spendingTimestamp != null && spendingTimestamp.startsWith(currentMonth)) {
                                        String category = spendingSnapshot.child("category").getValue(String.class);
                                        double amount = (double)spendingSnapshot.child("amount").getValue();
                                        spendingSums.merge(category, amount, Double::sum);
                                    }
                                }
                                // Now, create CategoryInsight objects with the fetched data
                                categoryInsights.add(new CategoryInsight("Housing", spendingSums.getOrDefault("housing", 0.0), housingBudget));
                                // Update RecyclerView
                                adapter.updateData(categoryInsights);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}

