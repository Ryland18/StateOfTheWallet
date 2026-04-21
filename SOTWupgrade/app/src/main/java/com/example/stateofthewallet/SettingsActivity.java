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
import com.example.stateofthewallet.ui.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
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
        
        // Apply saved theme preference
        applyThemePreference();
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
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
        darkLightMode.setChecked(sharedPreferences.getBoolean("isDarkMode", false));


        darkLightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save preference
                SharedPreferences.Editor editor = getSharedPreferences("darkMode", MODE_PRIVATE).edit();
                editor.putBoolean("isDarkMode", isChecked);
                editor.apply();
                
                // Apply the theme change
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                
                // Recreate the current activity to apply the theme immediately
                recreate();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String un = newUsername.getText().toString();
                String pw = newPassword.getText().toString();
                
                // Validate username is not empty
                if (un.isEmpty()) {
                    Snackbar.make(v, "Username cannot be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                
                // Update password if it passes strength validation
                if (authManager.passwordCheck(pw)){
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        // Update profile with display name
                        com.google.firebase.auth.UserProfileChangeRequest profileUpdates = 
                            new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(un)
                                .build();
                        
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Now update password
                                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(pw)
                                        .addOnCompleteListener(pwTask -> {
                                            if (pwTask.isSuccessful()) {
                                                Snackbar.make(v, "Profile updated successfully!", Snackbar.LENGTH_SHORT).show();
                                                newUsername.setText("");
                                                newPassword.setText("");
                                            } else {
                                                Snackbar.make(v, "Password update failed", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                } else {
                                    Snackbar.make(v, "Profile update failed", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    }
                } else {
                    Snackbar.make(v, "Password must be at least 8 characters with uppercase, lowercase, number, and special character", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
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

    // Apply theme preference from SharedPreferences
    private void applyThemePreference() {
        boolean isDarkMode = getSharedPreferences("darkMode", MODE_PRIVATE)
            .getBoolean("isDarkMode", false);
        
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}
