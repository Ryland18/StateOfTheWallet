package com.example.stateofthewallet;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stateofthewallet.data.model.Transaction;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        
        // Apply saved theme preference
        applyThemePreference();
        
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
            etVendor.setText(capBetweenSpacing(grabbed.getVendor()));
            etDamages.setText(String.valueOf(grabbed.getAmount()));
            etNotes.setText(grabbed.getNotes());
            tbIsDeposit.setChecked(grabbed.isDeposit());
            displaySystemDate(grabbed.getDateTimestamp());
            btnSave.setText("UPDATE REPORT");
            
            // Load the stored image if it exists
            Bitmap loadedImage = loadImageFromInternalStorage(grabbed.getId());
            if (loadedImage != null) {
                cameraImage.setImageBitmap(loadedImage);
                cameraImage.setVisibility(VISIBLE);
                cameraMaterial.setVisibility(GONE);
                capturedBitmap = loadedImage;
            }
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

    // from geeks for greeks on capitolizing words after spacing https://www.geeksforgeeks.org/java/java-program-to-capitalize-the-first-letter-of-each-word-in-a-string/
    public static String capBetweenSpacing(String input){
            String [] words = input.split("\\s");
            StringBuilder newString = new StringBuilder();

            for (String word: words){
                newString.append(Character.toTitleCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
            return newString.toString().trim();
    }



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
        String vendorName = capBetweenSpacing(etVendor.getText().toString());
        String amountStr = etDamages.getText().toString();
        try{
            double amountValue = Double.parseDouble(amountStr);
            double fixedAmount = Double.parseDouble(String.format("%.2f",amountValue));
            boolean isDeposit = tbIsDeposit.isChecked();  //TODO: DOUBLE CHECK THIS?????????
            Log.d("false issue - isDeposit", String.valueOf(isDeposit));

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


            if(etNotes.getText().length()<30){
            if (existing){
                Transaction t = new Transaction(
                        grabbed.getId(),vendorName,
                        etNotes.getText().toString(),
                        amountValue, isDeposit,
                        selectedDate);
                Log.d("changing", String.valueOf(t));

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
                    // Save the image if one was captured
                    if (capturedBitmap != null) {
                        saveImageToInternalStorage(t.getId());
                    }
                    
                    // Show celebration message for deposits
                    if (t.isDeposit()) {
                        Snackbar.make(findViewById(android.R.id.content), 
                            "🎉 DEPOSIT RECORDED! SURPLUS INCOMING!", 
                            Snackbar.LENGTH_LONG).show();
                    } else {
                        // Play sound effect for expenses
                        SoundManager.playSound(AddEntryActivity.this, R.raw.metal_pipe);
                        Snackbar.make(findViewById(android.R.id.content), 
                            "💸 EXPENSE RECORDED!", 
                            Snackbar.LENGTH_LONG).show();
                    }
                    
                    // Return to main activity
                    btnSave.postDelayed(() -> {
                        Intent i = new Intent(AddEntryActivity.this, MainActivity.class);
                        // Pass flag to show confetti if deposit was saved
                        if (t.isDeposit()) {
                            i.putExtra("showConfetti", true);
                        }
                        startActivity(i);
                    }, 1500);
                } else{
                    Snackbar.make(findViewById(android.R.id.content),errorMessage,Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateTransaction(Transaction t){
        crud.updateTransaction(t, new CRUDManager.CrudCallback() {
            @Override
            public void onComplete(boolean success, String errorMessage) {
                if(success) {
                    // Save the image if one was captured or updated
                    if (capturedBitmap != null) {
                        saveImageToInternalStorage(t.getId());
                    }
                    
                    // Play sound for expenses
                    if (!t.isDeposit()) {
                        SoundManager.playSound(AddEntryActivity.this, R.raw.metal_pipe);
                    }
                    
                    Intent i = new Intent(AddEntryActivity.this, MainActivity.class);
                    startActivity(i);
                } else{
                   Snackbar.make(findViewById(android.R.id.content),errorMessage,Snackbar.LENGTH_LONG).show();
                }
            }});
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_entry_options, menu);
        // Only show delete button for existing entries
        MenuItem deleteItem = menu.findItem(R.id.deleteBTN);
        if (deleteItem != null) {
            deleteItem.setVisible(existing);
        }
        return true;
    }
    //give items actions in the menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //switch case running based on selected item
        int id = item.getItemId();

        if (id==R.id.deleteBTN){
            destroyEvidence();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void destroyEvidence(){
        if (grabbed == null) return;
        
        // Show confirmation dialog
        new MaterialAlertDialogBuilder(this)
            .setTitle("CONFIRM DESTRUCTION")
            .setMessage("Are you sure you want to delete this transaction? This action cannot be undone.")
            .setPositiveButton("DELETE", (dialog, which) -> {
                // Delete from Firebase
                CRUDManager.deleteTransaction(grabbed, AddEntryActivity.this, new CRUDManager.CrudCallback() {
                    @Override
                    public void onComplete(boolean success, String errorMessage) {
                        if (success) {
                            // Delete the image from local storage
                            deleteImageFromInternalStorage(grabbed.getId());
                            
                            // Show success message
                            Snackbar.make(findViewById(android.R.id.content), 
                                "Transaction destroyed successfully", 
                                Snackbar.LENGTH_SHORT).show();
                            
                            // Return to main activity
                            Intent i = new Intent(AddEntryActivity.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content),
                                "Deletion failed: " + errorMessage,
                                Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            })
            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
            .show();
    }

    // Save image to internal storage
    private void saveImageToInternalStorage(String transactionId) {
        if (capturedBitmap == null) return;
        
        try {
            FileOutputStream fos = openFileOutput(transactionId + ".jpg", MODE_PRIVATE);
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Log.d("ImageStorage", "Image saved: " + transactionId);
        } catch (Exception e) {
            Log.e("ImageStorage", "Error saving image: " + e.getMessage());
        }
    }

    // Load image from internal storage
    private Bitmap loadImageFromInternalStorage(String transactionId) {
        try {
            FileInputStream fis = openFileInput(transactionId + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            Log.d("ImageStorage", "Image loaded: " + transactionId);
            return bitmap;
        } catch (Exception e) {
            Log.d("ImageStorage", "Image not found: " + transactionId);
            return null;
        }
    }

    // Delete image from internal storage
    private void deleteImageFromInternalStorage(String transactionId) {
        try {
            File file = new File(getFilesDir(), transactionId + ".jpg");
            if (file.exists()) {
                boolean deleted = file.delete();
                Log.d("ImageStorage", "Image delete status: " + deleted);
            }
        } catch (Exception e) {
            Log.e("ImageStorage", "Error deleting image: " + e.getMessage());
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up sound resources
        SoundManager.release();
    }

}