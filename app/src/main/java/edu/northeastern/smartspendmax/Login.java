package edu.northeastern.smartspendmax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.northeastern.smartspendmax.util.CommonConstants;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button userLoginButton = findViewById(R.id.user_login);
        Button adsMakerLoginButton = findViewById(R.id.adsMaker_login);

        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the UserLoginActivity
                Intent intent = new Intent(Login.this, LoginUser.class);
                intent.putExtra(CommonConstants.ROLE, CommonConstants.ROLE_USER);
                startActivity(intent);
            }
        });

        adsMakerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AdsMakerLoginActivity
                Intent intent = new Intent(Login.this, LoginUser.class);
                intent.putExtra(CommonConstants.ROLE, CommonConstants.ROLE_ADS_MAKER);
                startActivity(intent);

            }
        });
    }
}