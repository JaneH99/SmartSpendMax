package edu.northeastern.smartspendmax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AddNewTransaction extends AppCompatActivity {

    private Spinner spinner;
    private EditText transactionDate;
    private EditText transactionVendor;
    private EditText transactionAmount;
    private Button saveTransaction;
    private LocalDate date;
    private DatabaseReference usersRef;
    private String curUserName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_transaction);

        //retrieve current user
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        curUserName = sharedPref.getString("LastLoggedInUser", "defaultUser");

        spinner = findViewById(R.id.spinnerTransactionCategory);
        transactionDate = findViewById(R.id.transactionDate);
        transactionVendor = findViewById(R.id.transactionVendor);
        transactionAmount = findViewById(R.id.transactionAmount);
        saveTransaction = findViewById(R.id.transactionSave);

        //Set today's date as default transaction date
        date = LocalDate.now();
//        transactionDate.setText(date.getMonthValue() +"/" + date.getDayOfMonth() + "/" + date.getYear());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = date.format(formatter);
        transactionDate.setText(formattedDate);

        //Back button at top
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("SPENDING");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        //Spinner for transaction category
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,R.array.transactionCategoryArray, android.R.layout.simple_spinner_item
        );
        spinner.setAdapter(adapter);

        //Date picker for transaction date
        transactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        saveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    saveDataToDB();
                } else {
                    Toast.makeText(getApplicationContext(), "Please verify your input", Toast.LENGTH_LONG).show();
                };
            }
        });

    }

    private void saveDataToDB() {

        String category = spinner.getSelectedItem().toString().toLowerCase();
        String date = transactionDate.getText().toString();
        String vendor = transactionVendor.getText().toString();
        Number amount = Double.valueOf(transactionAmount.getText().toString());

        //Save data to database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("spendings").child(curUserName);
        String key = dbRef.push().getKey();
        HashMap<String,Object> transactoionInfo = new HashMap<>();
        transactoionInfo.put("category", category);
        transactoionInfo.put("vendor", vendor);
        transactoionInfo.put("amount", amount);
        transactoionInfo.put("timestamp", date);

        if (key != null) {
            dbRef.child(key).setValue(transactoionInfo)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(
                            e -> Toast.makeText(getApplicationContext(), "Failed to save the transaction", Toast.LENGTH_SHORT).show());
        }
        finish();
    }

    //Check if user input all data regarding a transaction
    private boolean validateInput() {
        if (spinner == null || spinner.getSelectedItemPosition() == 0) {
            return false;
        }
        if (transactionDate == null || transactionDate.getText().toString().trim().isEmpty()) {
            return false;
        }
        if (transactionVendor == null || transactionVendor.getText().toString().trim().isEmpty()) {
            return false;
        }
        if (transactionAmount == null || transactionAmount.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String formattedMonth = String.format("%02d", month + 1);
                        String formattedDay = String.format("%02d", dayOfMonth);
                        transactionDate.setText(formattedMonth + "/" + formattedDay + "/" + year);
                    }
                },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }
}