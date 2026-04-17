package com.example.stateofthewallet;

import android.annotation.SuppressLint;
import android.app.AppComponentFactory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stateofthewallet.data.model.LoggedInUser;

import com.example.stateofthewallet.data.model.Transaction;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText newPassword;
    private TextInputEditText newUsername;
    private Switch darkLightMode;
    private Button saveBtn;
    private Button logout;
    private Button exitbtn;

    private AuthManager authManager;

    private Transaction grabbed;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);


        newPassword = findViewById(R.id.newPassword);
        newUsername = findViewById(R.id.newUsername);

        darkLightMode = findViewById(R.id.darkOrLight);

        authManager = new AuthManager();
        saveBtn = findViewById(R.id.savestuff);
        logout = findViewById(R.id.logout);
        exitbtn = findViewById(R.id.exit);

        SharedPreferences sharedPreferences = getSharedPreferences("darkMode", MODE_PRIVATE);
        darkLightMode.setChecked(sharedPreferences.getBoolean("isDarkMode", true));


        darkLightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);}
                SharedPreferences.Editor editor = getSharedPreferences("darkMode", MODE_PRIVATE).edit();
                editor.putBoolean("isDarkMode", isChecked);
                editor.apply();}});

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String un = newUsername.getText().toString();
                String pw = newPassword.getText().toString();
                if (authManager.passwordCheck(pw)){
                    //    setDisplayName(un);

                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(pw);
                }
            }
        });

        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
            }
        });



        return insets;});

    }

}
