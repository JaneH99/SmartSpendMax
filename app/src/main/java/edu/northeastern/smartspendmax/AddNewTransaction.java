package edu.northeastern.smartspendmax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AddNewTransaction extends AppCompatActivity {

    private Spinner spinner;
    private EditText transactionDate;
    private EditText transactionVendor;
    private EditText transactionAmount;
    private Button saveTransaction;
    private LocalDate date;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_transaction);


        spinner = findViewById(R.id.spinnerTransactionCategory);
        transactionDate = findViewById(R.id.transactionDate);
        transactionVendor = findViewById(R.id.transactionVendor);
        transactionAmount = findViewById(R.id.transactionAmount);
        saveTransaction = findViewById(R.id.transactionSave);

        //Set today's date as default transaction date
        date = LocalDate.now();
        transactionDate.setText(date.getMonthValue() +"/" + date.getDayOfMonth() + "/" + date.getYear());

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
                    //TODO: Save data in database
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please verify your input", Toast.LENGTH_LONG).show();
                };
            }
        });

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
                        transactionDate.setText((month + 1) + "/" +dayOfMonth + "/" + year);
                    }
                },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }
}