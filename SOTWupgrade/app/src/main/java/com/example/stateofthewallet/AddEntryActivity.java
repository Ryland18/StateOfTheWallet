package com.example.stateofthewallet;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stateofthewallet.data.model.Transaction;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddEntryActivity extends AppCompatActivity {

    private TextInputEditText etVendor, etDamages, etNotes;
    private ToggleButton tbIsDeposit;
    private TextView tvDateDisplay, cameraMaterial;
    private Button btnSave;

    private ImageView cameraImage;

    private CardView cvPreview;
    private Bitmap capturedBitmap;
    private long selectedDate;
    private CRUDManager crud = new CRUDManager();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private boolean existing;

    private Transaction grabbed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_entry);
        setSupportActionBar(findViewById(R.id.toolbar));

        etVendor = findViewById(R.id.etSuspect);
        etDamages = findViewById(R.id.etDamages);
        etNotes = findViewById(R.id.etNotes);
        tbIsDeposit = findViewById(R.id.tbIsDeposit);
        tvDateDisplay = findViewById(R.id.tvDateDisplay);
        btnSave = findViewById(R.id.btnSave);
        cvPreview = findViewById(R.id.cvPreview);
        cameraImage = findViewById(R.id.cameraImageview);
        cameraMaterial = findViewById(R.id.cameraPlaceholder);

        cvPreview.setOnClickListener(v-> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(takePictureIntent);

        });


        grabbed = (Transaction) getIntent().getSerializableExtra("transaction");


        if (grabbed == null){
            existing =false;
            displaySystemDate(null);
        }
        else {
            existing = true;
            etVendor.setText(grabbed.getVendor());
            etDamages.setText(String.valueOf(grabbed.getAmount()));
            etNotes.setText(grabbed.getNotes());
            tbIsDeposit.setChecked(grabbed.isDeposit());
            displaySystemDate(grabbed.getDateTimestamp());
            btnSave.setText("Update");
            //todo: Delete Button

        }


        btnSave.setOnClickListener(v -> reportTransaction());

    }
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                    if (result.getResultCode() == RESULT_OK && result.getData() != null){
                        capturedBitmap = (Bitmap) result.getData().getExtras().get("data");
                        cameraImage.setImageBitmap(capturedBitmap);
                        cameraImage.setVisibility(VISIBLE);
                        cameraMaterial.setVisibility(GONE);
                        Toast.makeText(this,"Staged locally",Toast.LENGTH_SHORT).show();
                    }
            }
    );





    private void displaySystemDate(Long importTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        if (importTime ==null){
            selectedDate = System.currentTimeMillis();
        }else {
            selectedDate = importTime;
        }

        String dateStr = sdf.format(new Date(selectedDate));
        tvDateDisplay.setText("INCIDENT DADTE: "+dateStr+" (SYSTEM SECURE)" );
    }
    private void reportTransaction() {
        String vendorName = etVendor.getText().toString();
        String amountStr = etDamages.getText().toString();
        try{
            double amountValue = Double.parseDouble(amountStr);
            boolean isDeposit = tbIsDeposit.isActivated();

            /* --- CLOUD STORAGE MISSION BRIEFING ---
            If we were using Firebase Storage (Paid/Spark limits), the logic would be:

            1. if (capturedBitmap != null) {
            2.    Call StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("evidence/" + id + ".jpg");
            3.    Upload the bitmap using storageRef.putBytes(byteArray);
            4.    Inside the onSuccess listener, call storageRef.getDownloadUrl();
            5.    Take that resulting URL string and put it into the 'evidenceUrl' field of the Transaction object below.
            6.    THEN call writeNewTransaction() or updateTransaction().

            For now, we just pass 'null' for the URL to keep the Bureau's costs at zero.
            */



            if (existing){
                Transaction t = new Transaction(
                        grabbed.getId(),vendorName,
                        etNotes.getText().toString(),
                        amountValue, isDeposit,
                        selectedDate);

                updateTransaction(t);
            }else {
                String transactionId = "1";//UUID.randomUUID().toString(); generate a random id
                Transaction t = new Transaction(
                        transactionId, vendorName,
                        etNotes.getText().toString(),
                        amountValue, isDeposit,
                        selectedDate);
                writeNewTransaction(t);
                Log.d("reportTransaction", String.valueOf(t));
            }

        }catch(NumberFormatException e){
            Log.e("reportTransaction", String.valueOf(e));
        }
    }


    private void writeNewTransaction(Transaction t) {
        crud.writeNewTransaction(t, new CRUDManager.CrudCallback() {
            @Override
            public void onComplete(boolean success, String errorMessage) {
                if(success) {
                    Intent i = new Intent(AddEntryActivity.this, MainActivity.class);
                    startActivity(i);
                } else{
                    Snackbar.make(findViewById(R.id.container),errorMessage,Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateTransaction(Transaction t){
        crud.updateTransaction(t, new CRUDManager.CrudCallback() {
            @Override
            public void onComplete(boolean success, String errorMessage) {
                if(success) {
                    Intent i = new Intent(AddEntryActivity.this, MainActivity.class);
                    startActivity(i);
                } else{
                    Snackbar.make(findViewById(R.id.container),errorMessage,Snackbar.LENGTH_LONG).show();
                }
            }
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

        if (id==R.id.deleteBTN){
            //TODO: destroyEVIDENCE();
            return true;}

        return super.onOptionsItemSelected(item);
    }
    private void destroyEvidence(int id){

        //todo: if exist delete it
        //todo: need a coustom design toast or snack bar design

        Toast.makeText(this, "TODO: delete transaction", Toast.LENGTH_SHORT).show();
    }

}