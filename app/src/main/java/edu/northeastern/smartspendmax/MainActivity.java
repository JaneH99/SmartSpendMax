package edu.northeastern.smartspendmax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        Toolbar toolBar=findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if(itemID == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    return true;
                } else if(itemID == R.id.insight) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InsightFragment()).commit();
                    return true;
                } else if(itemID == R.id.spending) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SpendingFragment()).commit();
                    return true;
                } else if(itemID == R.id.budget) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BudgetFragment()).commit();
                    return true;
                } else if(itemID == R.id.wallet) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WalletFragment()).commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.signout) {
            Toast.makeText(this, "User Log Out", Toast.LENGTH_SHORT).show();
        } else if(itemID == R.id.notification) {
            Toast.makeText(this, "Notification Selected", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}