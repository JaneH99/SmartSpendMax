package edu.northeastern.smartspendmax;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.northeastern.smartspendmax.notification.NotificationActivity;

public class MainActivity extends AppCompatActivity implements OnFragmentSwitchListener {

    private Handler uiHandler;
    private Executor backgroundExecutor;
    private ScheduledExecutorService executor;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;
    private ValueEventListener initialValueEventListener;
    private ScheduledFuture<?> scheduledFuture;
    private static final String LOG = "-----Coupon Test-----";
    private HashSet<String> couponCollection;
    private static final String CHANNEL_ID = "smart_spend_max";
    //private static final String CHANNEL_NAME = "SmartSpendMax";
    private static final String CHANNEL_DESC = "Discount Promotions for Users";
    private static final int NOTIFICATION_ID = 9;
    private static final int NOTIFICATION_PERMISSION = 9009;
    private String currentAdMaker;
    private String currentDescription;
    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private int curFragmentID;
    private BottomNavigationView bottomNavigationView;
    Toolbar toolBar;

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        return super.getOnBackInvokedDispatcher();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        ImageView imageViewProgress = findViewById(R.id.loadingProgress);

        backgroundExecutor = Executors.newSingleThreadExecutor();
        uiHandler = new Handler(Looper.getMainLooper());

        ImageView iv_Progress = findViewById(R.id.loadingProgress);
        Glide.with(this)
                .load(R.drawable.loading_progress_bar)
                .into(imageViewProgress);

        initiateFragment(savedInstanceState);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                return linkToFragment(itemID);
            }
        });

        //Handle the case when back button is pressed. Show warning message
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // User clicked "Yes" button, perform sign out and navigate to LoginUser
                            Toast.makeText(MainActivity.this, "User Logged Out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Login.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }

    private boolean linkToFragment(int itemID) {
        if (itemID == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            return true;
        } else if (itemID == R.id.insight) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InsightFragment()).commit();
            return true;
        } else if (itemID == R.id.spending) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SpendingFragment()).commit();
            return true;
        } else if (itemID == R.id.expenseGenie) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddNewTransactionAIFragment()).commit();
            return true;
        } else if (itemID == R.id.invoice) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InvoiceFragment()).commit();
            return true;
        }
        return false;

    }
    private void initiateFragment(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            curFragmentID = savedInstanceState.getInt(KEY_OF_INSTANCE);
            linkToFragment(curFragmentID);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_OF_INSTANCE, curFragmentID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.signout) {
            Log.d(LOG, "onOptionsItemSelected: signout");
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // User clicked "Yes" button, perform sign out and navigate to LoginUser
                        Toast.makeText(MainActivity.this, "User Logged Out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDatabaseReference != null && mValueEventListener != null) {
            mDatabaseReference.removeEventListener(mValueEventListener);
        }
    }

    public void showLoadingIndicator() {
        ImageView imageViewProgress = findViewById(R.id.loadingProgress);
        Glide.with(this)
                .load(R.drawable.loading_progress_bar)
                .into(imageViewProgress);
        imageViewProgress.setVisibility(View.VISIBLE);
    }

    public void hideLoadingIndicator() {
        ImageView imageViewProgress = findViewById(R.id.loadingProgress);
        imageViewProgress.setVisibility(View.GONE);
    }


    @Override
    public void onFragmentSwitch(String fragmentTag) {
        if(fragmentTag.equals("AddNewTransactionAIFragment")) {
            bottomNavigationView.setSelectedItemId(R.id.expenseGenie);
        } else if(fragmentTag.equals("Invoice")) {
            bottomNavigationView.setSelectedItemId(R.id.invoice);
        }
    }
}