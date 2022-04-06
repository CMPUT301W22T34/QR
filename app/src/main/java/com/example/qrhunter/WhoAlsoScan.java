package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This java file is to show the list of users scanned the same qr code
 * but we did not develop the function about clicking the name of users :)
 */
public class WhoAlsoScan extends AppCompatActivity {
    FirebaseFirestore db;
    ArrayAdapter<HashMap> qrAdapter;
    ListView AlsoScanList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_also_scan);
        Intent intent=getIntent();
        String qrid=intent.getStringExtra("qrid");
        db = FirebaseFirestore.getInstance();
        CollectionReference qrRef = db.collection("QRCodes");
        DocumentReference docQrRef = qrRef.document(qrid);
        AlsoScanList = findViewById(R.id.qr_also_list);
        docQrRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        showMessage();
                    } else {
                        ArrayList<HashMap> alsoScanList = (ArrayList<HashMap>) document.get("scanners");
                        qrAdapter = new ArrayAdapter<HashMap>(WhoAlsoScan.this, android.R.layout.simple_list_item_1,alsoScanList);
                        AlsoScanList.setAdapter(qrAdapter);

                    }
                    Log.d(TAG, "Code documents write success. ");
                } else {
                    Log.d(TAG, "Error getting code documents: ", task.getException());
                }


            }
        });

        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    /**
     * show message
     */
    public void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The database does not have the code right now!");
        builder.setTitle("Error");
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}