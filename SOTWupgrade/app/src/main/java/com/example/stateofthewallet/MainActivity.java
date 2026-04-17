package com.example.stateofthewallet;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stateofthewallet.data.model.LoggedInUser;
import com.example.stateofthewallet.data.model.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private RecyclerView rvEvidenceStream;
    private TransactionAdapter adapter;

    static TextView totalIncome;

    static TextView totalExpenses;

    static TextView statusTxt;
    private TextView welcomeMsg;


    static double income = 0;
    static double expenses = 0;

    private static List<Transaction> transactionList;
    private TextView tvTotalIncome, tvTotalExpenses, tvNetStatus, overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        overview = findViewById(R.id.overview);
    try{
        Log.d("ContentValues", String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null){
            overview.setText("Welcome User");
        }
        else {
            overview.setText("Welcome " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString());
         }
    }catch (Exception e){
        Log.d(TAG, e.getMessage());
    }
        transactionList = new ArrayList<>();

        rvEvidenceStream = findViewById(R.id.rvEvidenceStream);

        //RecyclerView and Adapters

        statusTxt = findViewById(R.id.tvNetStatus);
        totalExpenses = findViewById(R.id.tvTotalExpenses);
        totalIncome = findViewById(R.id.tvTotalIncome);

        adapter = new TransactionAdapter(transactionList);
        adapter.importFirebaseData();
        rvEvidenceStream.setAdapter(adapter);
        rvEvidenceStream.setLayoutManager(new LinearLayoutManager(this));

        //TODO: calculate briefingStats() aka update the top part
        
        //TODO: updateFiscalStabilityStatus() aka update the Status in the top part
        //  Overbudget or FISCAL COLLAPSE IMMINENT
        //  Underbudget or UNEXPLAINED SURPLUS DETECTED
        //  At budget or FISCAL STABILITY MAINTAINED

        //TODO:  Double check for title case on all data displayed

        //TODO:  Double check the amount is always 2 decimals
        //  HINT: might have to do these in the adapter......


        //fab
        FloatingActionButton fab = findViewById(R.id.fabAddEvidence);
        fab.setOnClickListener(v->{
            Intent i = new Intent(MainActivity.this, AddEntryActivity.class);

            startActivity(i);
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainoptions,menu);
        return true;
    }
    //give items actions in the menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //switch case running based on selected item
        int id = item.getItemId();

        if (id==R.id.action_settings){
            //TODO: navigate to settings page
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id==R.id.filter_all) {
            //TODO: filter the stream to include all transactions
            adapter.setSortMode(0);
            adapter.setFilterMode(0);

            return true;

        }else if (id == R.id.filter_deposits){
            adapter.setFilterMode(1);
        }
        else if(id == R.id.filter_charges){
            adapter.setFilterMode(2);
        }

        else if(id == R.id.sort_newest){
            adapter.setSortMode(0);
        }else if(id == R.id.sort_oldest){
            adapter.setSortMode(1);
        } else if (id == R.id.sort_amount_acend) {
            adapter.setSortMode(2);
        } else if (id == R.id.sort_amount_decend) {
            adapter.setSortMode(3);
        }

        //todo: implement the rest of the items in the menu


        return super.onOptionsItemSelected(item);


    }
}