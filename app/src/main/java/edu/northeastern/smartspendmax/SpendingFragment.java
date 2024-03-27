package edu.northeastern.smartspendmax;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendingFragment extends Fragment {

    private RecyclerView recyclerView;
    private SpendingAdapter spendingAdapter;
    private List<SpendingInOneCategory> mylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_spending, container, false);
        View view = inflater.inflate(R.layout.fragment_spending,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SpendingTransaction t1 = new SpendingTransaction(LocalDate.of(2023,3,15), Category.GROCERY,
                "Target",99.99);
        SpendingTransaction t2 = new SpendingTransaction(LocalDate.of(2023,3,15), Category.GROCERY,
                "Costco",19.99);
        SpendingTransaction t4 = new SpendingTransaction(LocalDate.of(2023,3,15), Category.GROCERY,
                "Whole Food",29.99);
        List<SpendingTransaction> listOfOneCategory = new ArrayList<>();
        listOfOneCategory.add(t1);
        listOfOneCategory.add(t2);
        listOfOneCategory.add(t4);
        SpendingInOneCategory spendingInOneCategory = new SpendingInOneCategory(Category.GROCERY, listOfOneCategory,false);

        SpendingTransaction t3 = new SpendingTransaction(LocalDate.of(2023,3,15), Category.HOUSING,
                "Avalon",2000);
        List<SpendingTransaction> listOfOneCategory2 = new ArrayList<>();
        listOfOneCategory2.add(t3);
        SpendingInOneCategory spendingInOneCategory2 = new SpendingInOneCategory(Category.HOUSING, listOfOneCategory2,false);

        SpendingTransaction t5 = new SpendingTransaction(LocalDate.of(2023,3,15), Category.UTILITIES,
                "Avalon",2000);
        List<SpendingTransaction> listOfOneCategory3 = new ArrayList<>();
        listOfOneCategory3.add(t5);
        SpendingInOneCategory spendingInOneCategory3 = new SpendingInOneCategory(Category.UTILITIES, listOfOneCategory3,false);

        mylist = new ArrayList<>();
        mylist.add(spendingInOneCategory);
        mylist.add(spendingInOneCategory2);
        mylist.add(spendingInOneCategory3);

        spendingAdapter = new SpendingAdapter(mylist);
        recyclerView.setAdapter(spendingAdapter);
        return view;
    }
}