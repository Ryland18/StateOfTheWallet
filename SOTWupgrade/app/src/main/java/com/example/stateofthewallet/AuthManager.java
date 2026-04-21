package com.example.stateofthewallet;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private FirebaseAuth auth;

    //constructor
    public AuthManager(){
        auth = FirebaseAuth.getInstance();   //go connect to the Authentication tool
    }
    //The verb or method that we can inherit here
    public interface AuthCallback{
        public void onComplete(boolean success, String errorMessage);
    }

    //signUp
    public void signUpWithEmailAndPassword(String email, String password, final AuthCallback callback){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"Created user: "+email);
                            callback.onComplete(true,null);
                        }
                        else{
                            Log.d(TAG,"Failure to create user: "+email);
                            callback.onComplete(false,task.getException().getMessage());
                        }
                    }
                }
        );

    }

    //signIn
    public void signInWithEmailAndPassword(String email, String password, final AuthCallback callback){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"Welcome "+email);
                            callback.onComplete(true,null);
                        }
                        else{
                            Log.d(TAG,"Failure to sign in user: "+email);
                            callback.onComplete(false,task.getException().getMessage());
                        }
                    }
                }
        );
    }
    public boolean passwordCheck(String password){
        //from StackOverflow to find special characters
        Pattern sp = Pattern.compile("[!@#$%^&*()_+=|<>?{}\\[\\]~-]"); //finds any character that is selected
        Matcher sm = sp.matcher(password);

        Pattern up = Pattern.compile("[A-Z]");
        Matcher um = up.matcher(password);

        Pattern np = Pattern.compile("[0-9]");  // Fixed: numbers/digits
        Matcher nm = np.matcher(password);

        Pattern lp = Pattern.compile("[a-z]");
        Matcher wm = lp.matcher(password);

        // Requirements:
        // - At least 8 characters long
        // - At least one uppercase letter
        // - At least one lowercase letter
        // - At least one number
        // - At least one special character

        if (password.length() < 8 || !sm.find() || !um.find() || !nm.find() || !wm.find()){
            return false;
        }else {
            return true;
        }
    }


    //get current user
    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();   //TODO:  Is this the email or User ID?...
    }

    // Update user display name (codeName)
    public void updateDisplayName(String displayName, final AuthCallback callback) {
        if (displayName == null || displayName.isEmpty()) {
            callback.onComplete(false, "Display name cannot be empty");
            return;
        }
        
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            com.google.firebase.auth.UserProfileChangeRequest profileUpdates = 
                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            
            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Display name updated: " + displayName);
                    callback.onComplete(true, null);
                } else {
                    Log.d(TAG, "Failed to update display name");
                    callback.onComplete(false, task.getException() != null ? 
                        task.getException().getMessage() : "Unknown error");
                }
            });
        } else {
            callback.onComplete(false, "No user currently signed in");
        }
    }
}
