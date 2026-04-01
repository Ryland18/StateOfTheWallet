package com.example.stateofthewallet;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stateofthewallet.data.model.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CRUDManager {
    /* 4 main functions of SQL or mainly any database structure
        C - CREATE
        R - READ
        U - UPDATE
        D - DELETE
    */

    private FirebaseAuth auth;
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "CRUDManager";
    private static List<Transaction> transactionList = new ArrayList<>();

    public interface CrudCallback {
        void onComplete(boolean success, String errorMessage);
    }
    public interface TransactionListCallback{
        void onTransactionsLoaded(List<Transaction> transactions);
        void onTransactionsLoadedError(String errorMessage);
    }

    //////////// CREATE Section ////////////
    public void writeNewTransaction(Transaction t, CrudCallback callback){
        //1. set transaction and get reference
        DatabaseReference currentRef = databaseReference.child("transactions");

        //2. generate unique key with push
        String fbid = currentRef.push().getKey();

        //3. set the id, save and then check if it ran properly
        if(fbid !=null){
            t.setId(fbid);
            currentRef.child(fbid).setValue(t).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    callback.onComplete(true,null);

                }else{
                    callback.onComplete(false,task.getException().getMessage());
                }
            });
        }

    }
    //////////// READ Section ////////////
    public static void readAllTransaction(TransactionListCallback callback) {
        //Go obtain a "snapshot" or current state of the DB
        databaseReference.child("transactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {   //snapshot is the database
                //Take what you need
                List<Transaction> snapShotTransactions = new ArrayList<>();
                for (DataSnapshot eachTransaction : snapshot.getChildren()) {
                    Transaction t = eachTransaction.getValue(Transaction.class);
                    if (t != null) {
                        snapShotTransactions.add(t);
                    }
                }
                //Place it where you need it
                transactionList = snapShotTransactions;   //pushing our data to a global variable in this file
                Log.d(TAG, String.valueOf(transactionList));
                callback.onTransactionsLoaded(snapShotTransactions);   //using the interface to pull the tList
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Report errors
                callback.onTransactionsLoadedError(error.getMessage());
            }
        });
    }
    /////////////UPDATE //////////////////////////////////


    public void updateTransaction(Transaction t, CrudCallback crudCallback){
        // set t into the existing place
        databaseReference.child("transactions").child(t.getId()).setValue(t).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               crudCallback.onComplete(true,null);
           }
           else {
               crudCallback.onComplete(false,task.getException().getMessage());
           }
        });
    }



    //////////////Delete //////////////////////////////////

    public void deleteTransaction(Transaction t, CrudCallback crudCallback){
        // set t into the existing place
        //database look at                 transaction subnode ID         remove it             work or not
        databaseReference.child("transactions").child(t.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                crudCallback.onComplete(true,null);
            }
            else {
                crudCallback.onComplete(false,task.getException().getMessage());
            }
        });
    }



    //getter for the transactionList global variable
    public static List<Transaction> getTransactionList() {
        return transactionList;
    }

}
