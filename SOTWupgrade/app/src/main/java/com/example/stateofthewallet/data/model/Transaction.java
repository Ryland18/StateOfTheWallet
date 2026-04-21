package com.example.stateofthewallet.data.model;

import java.io.Serializable;

public class Transaction implements Serializable {
        private String id, vendor, notes;
    private double amount;
    private boolean isDeposit;
    private long dateTimestamp;

    public Transaction(){}
    public Transaction(String id, String vendor, String notes,
                       double amount, boolean isDeposit, long dateTimestamp) {
        this.id = id;
        this.vendor = vendor;
        this.notes = notes;
        this.amount = amount;
        this.isDeposit = isDeposit;
        this.dateTimestamp = dateTimestamp;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getVendor() {
        return vendor;
    }
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public boolean isDeposit() {
        return isDeposit;
    }
    public void setDeposit(boolean deposit) {
        isDeposit = deposit;
    }
    public long getDateTimestamp() {
        return dateTimestamp;
    }
    public void setDateTimestamp(long dateTimestamp) {
        this.dateTimestamp = dateTimestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", vendor='" + vendor + '\'' +
                ", notes='" + notes + '\'' +
                ", amount=" + amount +
                ", isDeposit=" + isDeposit +
                ", dateTimestamp=" + dateTimestamp +
                '}';
    }
}
