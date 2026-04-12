package com.example.stateofthewallet;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        newPassword = findViewById(R.id.newPassword);
        newUsername = findViewById(R.id.newUsername);

       // darkLightMode = findViewById(R.id.darkOrLight);

        authManager = new AuthManager();
        saveBtn = findViewById(R.id.savestuff);
        logout = findViewById(R.id.logout);
        exitbtn = findViewById(R.id.exit);



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






    }

}
