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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.northeastern.smartspendmax.notification.NotificationActivity;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;
    private ValueEventListener initialValueEventListener;
    private ScheduledExecutorService executor;
    private ScheduledFuture<?> scheduledFuture;
    private static final String LOG = "-----Coupon Test-----";
    private HashSet<String> couponCollection;
    private static final String CHANNEL_ID = "smart_spend_max";
    private static final String CHANNEL_NAME = "SmartSpendMax";
    private static final String CHANNEL_DESC = "Discount Promotions for Users";
    private static final int NOTIFICATION_ID = 9;
    private static final int NOTIFICATION_PERMISSION = 9009;
    private String currentAdMaker;
    private String currentDescription;
    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private int curFragmentID;
    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        return super.getOnBackInvokedDispatcher();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

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
                            // User clicked "Yes" button, perform exit action
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        //Initiate a value in case it is null
        currentAdMaker = "Ads Maker";
        currentDescription ="You received a promotion";
        // Initialize Firebase Database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("coupons");
        // Initialize ScheduledExecutorService with a single thread
        executor = Executors.newSingleThreadScheduledExecutor();
        //Create Notification Channel
        createNotificationChannel();
        //Collect a set to store all existing coupons
        couponCollection = new HashSet<>();
        // Initialize couponCollection with existing coupons' keys in the database. Then start monitoring changes
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isFinishing()) return;
                for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
                    couponCollection.add(entrySnapshot.getKey());
                }
                // Start listening for changes in Firebase Database
                startListening();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
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
        } else if (itemID == R.id.budget) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddNewTransactionAIFragment()).commit();
            return true;
        } else if (itemID == R.id.wallet) {
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

    private void startListening() {
        // Schedule a task to listen for changes in Firebase Database every 1 minute
        scheduledFuture = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mValueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
                            String key = entrySnapshot.getKey();
                            // Handle data change here
                            if (key != null) {
                                Log.d(LOG, key);
                                if (!couponCollection.contains(key)) {
                                    couponCollection.add(key);
                                    Log.d(LOG, "You have a new coupon " + key);
                                    //Retrieve information
                                    Map<String, Object> map = (Map<String, Object>) entrySnapshot.getValue();
                                    if(map != null) {
                                        currentAdMaker = Objects.requireNonNull(map.get("adMakerName")).toString();
                                        Log.d(LOG, "currentAdMaker " );
                                        currentDescription = Objects.requireNonNull(map.get("description")).toString();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            Log.d(LOG, "sendNotification" );
                                            sendNotification(getBaseContext());
                                        }
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                };

                // Start listening for changes in Firebase Database
                mDatabaseReference.addValueEventListener(mValueEventListener);
            }
        }, 0, 1, TimeUnit.MINUTES); // Initial delay: 0, Repeat interval: 1 minute
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
        } else if (itemID == R.id.notification) {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void sendNotification(Context context) {
        // Prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(context, NotificationActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setContentTitle(currentAdMaker)
                        .setContentText(currentDescription)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION);
            return;
        }

        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == NOTIFICATION_PERMISSION) {
            boolean permissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(!permissionGranted) {
                Toast.makeText(getApplicationContext(), "You won't receive notifications unless you grant notification permission", Toast.LENGTH_LONG).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    sendNotification(getBaseContext());
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDatabaseReference != null && mValueEventListener != null) {
            mDatabaseReference.removeEventListener(mValueEventListener);
        }
    }

}