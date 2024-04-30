package edu.northeastern.smartspendmax;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpendingFragment extends Fragment {

    private RecyclerView recyclerView;
    private SpendingAdapter spendingAdapter;
    private List<SpendingInOneCategory> mylist;
    private FloatingActionButton fab;
    private FirebaseDatabase database;
    private String curUserName;
    SpendingInOneCategory spendingInHousing;
    SpendingInOneCategory spendingInTransportation;
    SpendingInOneCategory spendingInUtilities;
    SpendingInOneCategory spendingInGrocery;
    SpendingInOneCategory spendingInPersonalExpense;
    SpendingInOneCategory spendingInOther;

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            resetCategoryContent();
            retrieveUpdatedData(snapshot);
            rebuildContent();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spending,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        database = FirebaseDatabase.getInstance();

        //retrieve current user
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        curUserName = sharedPref.getString("LastLoggedInUser", "defaultUser");

        //Start event listener
        database.getReference().child("spendings").child(curUserName).addValueEventListener(valueEventListener);

        //Floating Button
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event
                addNewTransaction();
            }
        });
        
        return view;
    }

    private void resetCategoryContent() {
        spendingInHousing = new SpendingInOneCategory(Category.HOUSING);
        spendingInTransportation = new SpendingInOneCategory(Category.TRANSPORTATION);
        spendingInUtilities = new SpendingInOneCategory(Category.UTILITIES);
        spendingInGrocery = new SpendingInOneCategory(Category.GROCERY);
        spendingInPersonalExpense = new SpendingInOneCategory(Category.PERSONAL_EXPENSE);
        spendingInOther = new SpendingInOneCategory(Category.OTHER);
    }

    private void retrieveUpdatedData(DataSnapshot dataSnapshot) {
        for(DataSnapshot entrySnapshot: dataSnapshot.getChildren()) {
            //Retrieve information
            Map<String, Object> map = (Map<String, Object>) entrySnapshot.getValue();
            if(map != null) {
                double amount = ((Number) Objects.requireNonNull(map.get("amount"))).doubleValue();
                String category = Objects.requireNonNull(map.get("category")).toString();
                String vendor = Objects.requireNonNull(map.get("vendor")).toString();
                String timestamp = Objects.requireNonNull(map.get("timestamp")).toString();
                LocalDate date = convertStringToDate(timestamp);
                if(date.getMonthValue() == LocalDate.now().getMonthValue()) {
                    if(category.equals("housing")) {
                        SpendingTransaction transaction = new SpendingTransaction(date, Category.HOUSING, vendor, amount);
                        spendingInHousing.getSpendingInTheCategory().add(transaction);
                    } else if(category.equals("transportation")) {
                        SpendingTransaction transaction = new SpendingTransaction(date, Category.TRANSPORTATION, vendor, amount);
                        spendingInTransportation.getSpendingInTheCategory().add(transaction);
                    } else if(category.equals("utilities")) {
                        SpendingTransaction transaction = new SpendingTransaction(date, Category.UTILITIES, vendor, amount);
                        spendingInUtilities.getSpendingInTheCategory().add(transaction);
                    } else if(category.equals("grocery")) {
                        SpendingTransaction transaction = new SpendingTransaction(date, Category.GROCERY, vendor, amount);
                        spendingInGrocery.getSpendingInTheCategory().add(transaction);
                    } else if(category.equals("personal expense")) {
                        SpendingTransaction transaction = new SpendingTransaction(date, Category.PERSONAL_EXPENSE, vendor, amount);
                        spendingInPersonalExpense.getSpendingInTheCategory().add(transaction);
                    } else if(category.equals("other")) {
                        SpendingTransaction transaction = new SpendingTransaction(date, Category.OTHER, vendor, amount);
                        spendingInOther.getSpendingInTheCategory().add(transaction);
                    }
                }
            }
        }

        //Sort each category in descending order
        spendingInHousing.sortByTransactionDate();
        spendingInTransportation.sortByTransactionDate();
        spendingInUtilities.sortByTransactionDate();
        spendingInGrocery.sortByTransactionDate();
        spendingInPersonalExpense.sortByTransactionDate();
        spendingInOther.sortByTransactionDate();
    }

    private void rebuildContent() {
        mylist = new ArrayList<>();
        mylist.add(spendingInHousing);
        mylist.add(spendingInTransportation);
        mylist.add(spendingInUtilities);
        mylist.add(spendingInGrocery);
        mylist.add(spendingInPersonalExpense);
        mylist.add(spendingInOther);
        spendingAdapter = new SpendingAdapter(mylist);
        recyclerView.setAdapter(spendingAdapter);
    }

    private LocalDate convertStringToDate(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return LocalDate.parse(timestamp,formatter);
    }

    //Direct to the add new transaction fragment
    private void addNewTransaction() {
        Fragment addTransactionFragment = new AddNewTransactionAIFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addTransactionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //remove the event listener
        database.getReference().child("spendings").child(curUserName)
                .removeEventListener(valueEventListener);
    }
}