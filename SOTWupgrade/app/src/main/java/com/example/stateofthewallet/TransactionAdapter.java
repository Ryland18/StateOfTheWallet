package com.example.stateofthewallet;


import android.content.Intent;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stateofthewallet.data.model.Transaction;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactionList;
    private int currentSortMode = 0;
    private List<Transaction> masterList;
    private int currentFilterMode;

    //This is where this file honestly begins
    public TransactionAdapter(List<Transaction> tList){
        this.transactionList = tList;
        this.masterList = new ArrayList<>(tList);   //Fix:  wasn't here
    }

    public void isNotDeposit(Transaction i ){
        i.isDeposit();
    }


    public class TransactionViewHolder extends RecyclerView.ViewHolder{

        public CardView containerView;   //CardView because the layout of a card is a CardView
        TextView tvVendor, tvDate, tvAmount;
        View vIndicator;
        //This is where you can connect java to the widgets of item_evidence
        //  and give them functions and do stuff with them
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendor = itemView.findViewById(R.id.tvRowSuspect);
            tvDate = itemView.findViewById(R.id.tvRowDate);
            tvAmount = itemView.findViewById(R.id.tvRowAmount);
            vIndicator = itemView.findViewById(R.id.vIndicator);
            containerView = itemView.findViewById(R.id.transaction_card_layout);
            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Transaction t = (Transaction) containerView.getTag();
                    Intent i = new Intent(view.getContext(),AddEntryActivity.class);
                    Log.d("ViewHolder clicked", String.valueOf(t));
                    i.putExtra("transaction", (Serializable) t);
                    view.getContext().startActivity(i);   //view.getContext() is cuz intent has it
                }
            });
        }
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This says which xml files are for the recyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evidence,parent,false);
        return new TransactionViewHolder(view);
    }

    //Method to insert data into our cards
    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionViewHolder holder, int position) {
        Transaction t = transactionList.get(position);
        holder.containerView.setTag(t);   //containerView is a field variable for our holder

        holder.tvVendor.setText("Vendor: "+t.getVendor());

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        holder.tvDate.setText("Date: "+sdf.format(new Date(t.getDateTimestamp())));

        //TODO: need to be sure only 2 decimals...
        holder.tvAmount.setText("$ "+t.getAmount());

        //TODO: change the text color based on income or outcome

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    //Think of this file as a controller to run the functions from CRUD, but is being
    //      told to run by MainActivity
    public void importFirebaseData(){
        CRUDManager.readAllTransaction(new CRUDManager.TransactionListCallback() {
            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {
                //update our local reference and tell the RecyclerView to refresh
                //Sort the list before giving the adapter the data
//                transactions.sort((t1,t2) -> Long.compare(t2.getDateTimestamp(),t1.getDateTimestamp()));

                masterList = new ArrayList<>(transactions);
//                transactionList = transactions;   //transactionList is the global variable of this file
//                notifyDataSetChanged();  //"refresh" the recyclerview
                refreshView();
            }
            @Override
            public void onTransactionsLoadedError(String errorMessage) {
                Log.e("importFirebaseData Error",errorMessage);
            }
        });
    }
    //depending on currentSortMode's int value, we'll sort differently
    private void applySort(List<Transaction> list){
        switch(currentSortMode){
            case 0:  //New First
                list.sort((t1,t2) -> Long.compare(t2.getDateTimestamp(),t1.getDateTimestamp()));
                break;
            case 1:  //Old First
                list.sort(Comparator.comparingLong(Transaction::getDateTimestamp)); //compare using the getDateTimestamp
                break;
            //TODO:  Amount hi vs lo and lo vs hi
        }
    }
    //updates the sorting mode and refreshes the current list
    public void setSortMode(int sortMode){
        this.currentSortMode = sortMode;
        refreshView();
    }
    public void setFilterMode(int filterMode){
        this.currentFilterMode = filterMode;
        refreshView();
    }

    private void refreshView() {
        if (masterList == null) return;

        //1.  apply a filter
        List<Transaction> processedList;
        if(currentFilterMode == 1){  //Deposits Only
            processedList = masterList.stream().filter(Transaction::isDeposit).collect(Collectors.toList());
        } else if (currentFilterMode ==2) {
            processedList = masterList.stream().filter(Transaction::isDeposit).collect(Collectors.toList());
        }
        else{
            processedList = new ArrayList<>(masterList);
        }
        //2.  apply sort
        applySort(processedList);
        //3.  update the adapter's display list
        transactionList=processedList;
        notifyDataSetChanged();
    }
}

