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

        Pattern lp = Pattern.compile("[0-9]");
        Matcher lm = lp.matcher(password);

        Pattern wp = Pattern.compile("[a-z]");
        Matcher wm = wp.matcher(password);



        if (password.length() < 8 || !(sm.find())||!(um.find())||!lm.find() || !wm.find()){
            return false;
        }else {
            return true;
        }
    }


    //get current user
    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();   //TODO:  Is this the email or User ID?...
    }
}
