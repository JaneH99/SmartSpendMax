package edu.northeastern.smartspendmax;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.PorterDuff;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DatabaseReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddNewTransactionAIFragment extends Fragment {

    private Spinner spinner;
    private EditText transactionDate;
    private EditText transactionVendor;
    private EditText transactionAmount;
    private Button saveTransaction;
    private LocalDate date;
    private DatabaseReference usersRef;
    private String curUserName;

    private static final int REQUEST_CODE_SPEECH_INPUT = 5000;
    private static final int REQUEST_CODE_RECORD_AUDIO = 99;

    private String userVoiceInput;
    private TextView myAudioTextView;
    private Button micButton;
    private Button imageButton;
    private static String TAG = "Add Transaction AI Fragment";
    private String hiddenKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_new_transaction_ai, container, false);

        //retrieve current user
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        curUserName = sharedPref.getString("LastLoggedInUser", "defaultUser");

        spinner = view.findViewById(R.id.spinnerTransactionCategory);
        transactionDate = view.findViewById(R.id.transactionDate);
        transactionVendor = view.findViewById(R.id.transactionVendor);
        transactionAmount = view.findViewById(R.id.transactionAmount);
        saveTransaction = view.findViewById(R.id.transactionSave);


        micButton = view.findViewById(R.id.recordAudioButton);
        myAudioTextView = view.findViewById(R.id.myAudioInput);
        imageButton = view.findViewById(R.id.recordImageButton);

        //Get API key from meta data
        String apiKey;
        try {
            ApplicationInfo ai = requireContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            String value = metaData.getString("keyValue");
            hiddenKey = value != null ? value : "";
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }

        //Set today's date as default transaction date
        date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = date.format(formatter);
        transactionDate.setText(formattedDate);


        //Spinner for transaction category
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),R.array.transactionCategoryArray, android.R.layout.simple_spinner_item
        );
        spinner.setAdapter(adapter);

        //Date picker for transaction date
        transactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //Click listener for Save button
        saveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    saveDataToDB();
                } else {
                    Toast.makeText(requireContext(), "Please verify your input", Toast.LENGTH_LONG).show();
                };
            }
        });

        //Click listener for record audio button
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(requireContext());
//                processAudio();
            }
        });

        //Click listener or image button

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment invoiceFragment = new InvoiceFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, invoiceFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        //Check if user get information from Invoice Fragment
        Bundle bundle = getArguments();
        if(bundle != null) {
            InvoiceInformation invoiceInformation = (InvoiceInformation) bundle.getSerializable("information");
            if(invoiceInformation != null) {
                if(invoiceInformation.getInvoiceVendor() != null) {
                    transactionVendor.setText(invoiceInformation.getInvoiceVendor());
                }
                if(invoiceInformation.getInvoiceAmount() != null) {
                    transactionAmount.setText(String.valueOf(invoiceInformation.getInvoiceAmount()));
                }
                if(invoiceInformation.getInvoiceDate() != null) {
                    transactionDate.setText(String.valueOf(invoiceInformation.getInvoiceDate()));
                }
                if(invoiceInformation.getInvoiceCategory() != null) {
                    String transactionCategory = invoiceInformation.getInvoiceCategory();
                    Log.d("TAG", transactionCategory);
                    if(transactionCategory.trim().equals("Housing")) {
                        spinner.setSelection(1);
                    } else if(transactionCategory.trim().equals("Transportation")) {
                        spinner.setSelection(2);
                    } else if(transactionCategory.trim().equals("Grocery")) {
                        spinner.setSelection(3);
                    } else if(transactionCategory.trim().equals("Utilities")) {
                        spinner.setSelection(4);
                    }else if(transactionCategory.trim().equals("Personal Expense")) {
                        spinner.setSelection(5);
                    }else if(transactionCategory.trim().equals("Other")) {
                        spinner.setSelection(6);
                    }
                }
            }
        }

        return view;

    }

    private void speak(Context context) {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO);
            return;
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi, speak something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(requireContext()," " +e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_RECORD_AUDIO) {
            boolean permissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(!permissionGranted) {
                Toast.makeText(requireContext(), "You won't receive notifications unless you grant notification permission", Toast.LENGTH_LONG).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    speak(getContext());
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                userVoiceInput = result.get(0);
                myAudioTextView.setText(userVoiceInput);
                processAudio();
            }
        }
    }

    private void processAudio() {
        String context = myAudioTextView.getText().toString();
        GenerativeModel gm = new GenerativeModel("gemini-pro", hiddenKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        HashMap<String, View> questionMap = new HashMap<>();
        questionMap.put("What's the store name?. Only show the store name without description.", transactionVendor);
        questionMap.put("What's the transaction date? Only show the date in format MM/DD/YYYY.", transactionDate);
        questionMap.put("What's the total amount? Please show amount only without dollar sign.", transactionAmount);
        questionMap.put("Based on the store name, which category do you think the spending belongs to? Choose from categories listed below " +
                "Housing, Transportation, Utilities, Grocery, Personal Expense, Other, Restaurant. Response with the category name without description", spinner);

        for(Map.Entry<String, View> entry: questionMap.entrySet()) {
            String question = entry.getKey();
            View view = entry.getValue();
            Content content = new Content.Builder()
                    .addText(context + question)
                    .build();

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText().trim();
                        if(view instanceof EditText) {
                            ((EditText) view).setText(resultText);
                        } else if(view instanceof Spinner) {
                            Spinner spinner = (Spinner) view;
                            if(resultText.equals("Housing")) {
                                spinner.setSelection(1);
                            } else if(resultText.equals("Transportation")) {
                                spinner.setSelection(2);
                            } else if(resultText.equals("Grocery")) {
                                spinner.setSelection(3);
                            } else if(resultText.equals("Utilities")) {
                                spinner.setSelection(4);
                            }else if(resultText.equals("Personal Expense")) {
                                spinner.setSelection(5);
                            }else if(resultText.equals("Other")) {
                                spinner.setSelection(6);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                }, this.requireActivity().getMainExecutor());
            }

        }

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
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                        cleanUserInput();
                    })
                    .addOnFailureListener(
                            e -> Toast.makeText(requireContext(), "Failed to save the transaction", Toast.LENGTH_SHORT).show());
        }
    }

    private void cleanUserInput() {
        spinner.setSelection(0);
        transactionVendor.setText("");
        transactionAmount.setText("");
        //Set today's date as default transaction date
        date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = date.format(formatter);
        transactionDate.setText(formattedDate);
        myAudioTextView.setText("");
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.requireContext(),
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