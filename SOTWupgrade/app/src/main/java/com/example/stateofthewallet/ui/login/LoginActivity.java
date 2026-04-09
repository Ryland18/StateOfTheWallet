package com.example.stateofthewallet.ui.login;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.stateofthewallet.AuthManager;
import com.example.stateofthewallet.MainActivity;
import com.example.stateofthewallet.R;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button loginBTN;
    private TextView registerTXT, errorText;
    private EditText codeNameEditTXT, passwordEditTXT;
    boolean registerMode = false;
    private AuthManager authManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBTN = findViewById(R.id.btnLogin);
        registerTXT = findViewById(R.id.tvSignUp);
        codeNameEditTXT = findViewById(R.id.etAgentEmail);
        passwordEditTXT = findViewById(R.id.etAgentPassword);
        errorText = findViewById(R.id.errorTXT);

        authManager = new AuthManager();

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!registerMode) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else{
                    //TODO: password strength test... be a good function for here ----Done
                    String un = codeNameEditTXT.getText().toString();
                    String pw = passwordEditTXT.getText().toString();
                    if (authManager.passwordCheck(pw)){
                    authManager.signUpWithEmailAndPassword(un, pw, new AuthManager.AuthCallback() {
                        @Override
                        public void onComplete(boolean success, String errorMessage) {
                            if(success){
//                                Intent intent = new Intent(LoginActivity.this, RegistrationSuccessActivity.class);
//                                startActivity(intent);
                                Snackbar.make(v,"SUCCESS!!!", Snackbar.LENGTH_SHORT).show();
                            } else{
                                Snackbar.make(v,errorMessage, Snackbar.LENGTH_SHORT).show();
                            }
                        }

                    });}else {
                        errorText.setVisibility(VISIBLE);
                        errorText.setText(getString(R.string.invalid_password));

                    }


                }
            }
        });
        registerTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!registerMode){
                    registerMode = true;
                    loginBTN.setText("REGISTER USER");
                    registerTXT.setText("BACK TO LOGIN");}
                else{
                    registerMode = false;
                    loginBTN.setText("AUTHORIZE ACCESS");
                    registerTXT.setText("REGISTER NEW AGENT");
                }
            }
        });

    }
}