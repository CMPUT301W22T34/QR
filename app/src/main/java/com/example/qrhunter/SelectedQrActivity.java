package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrhunter.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;

/**
 * @description This file is to show the details about the selected code
 */
public class SelectedQrActivity extends AppCompatActivity {
    String codeDisplay;
    FirebaseFirestore db;
    boolean check =false;
    String qrid;
    boolean locationOr = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_qr);
        Intent intent=getIntent();
        qrid=intent.getStringExtra("qrid");

        check =intent.getBooleanExtra("check",false);


        Log.d(TAG, "从UserCode传递过来的参数"+qrid);
        db = FirebaseFirestore.getInstance();
        SharedData appData = (SharedData) getApplication();
        codeDisplay = appData.getCodedisplay();
        TextView qr = findViewById(R.id.txtQrcode);
        qr.setText(qrid);
        TextView textView;
        // who scanned this qrCode
        textView = findViewById(R.id.txtElse);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashScore hashScore = new HashScore();
                Intent intent = new Intent(SelectedQrActivity.this,WhoAlsoScan.class);
                intent.putExtra("qrid",hashScore.hash256(qrid));
                startActivity(intent);
            }
        });


        //delete button
        Button deletebutton;
        deletebutton = findViewById(R.id.btnCodeDelete);
        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check==false){
                    deleteCode();
                }
                else{
                    showdetail();
                }
            }
        });





        // Show the image of the code
        ImageView image = findViewById(R.id.imageView);
        HashScore hashScore = new HashScore();
        CollectionReference userRef = db.collection("QRCodes");
        DocumentReference docUserRef = userRef.document(hashScore.hash256(qrid));
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Boolean sharePicture = document.getBoolean("sharedPicture");
                Log.d(TAG, "ssssssssssssssssssssssssssssssssssssssss"+sharePicture);
                if (sharePicture){
                    ArrayList<String> tmp_photoList = (ArrayList<String>) document.get("http");
                    Log.d(TAG, "immmmmmmmmmmmmmmmage"+ tmp_photoList.get(0));
                    String http = tmp_photoList.get(0);
                    Picasso.get().load(http).into(image);


                }else{

                    Toast.makeText(SelectedQrActivity.this, "This code has no image upload.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //back button
        Button backbutton;
        backbutton = findViewById(R.id.btnBackToCodeList);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //comment button
        Button commbutton;
        commbutton = findViewById(R.id.btnCodeComment);
        commbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check==false){
                    Intent comment = new Intent(SelectedQrActivity.this, CodeCommentActivity.class);
                    comment.putExtra("qrid", qrid);
                    startActivity(comment);
                }
                else{
                    showdetail();
                }

            }
        });

        //show the score of the code
        Long score = intent.getLongExtra("score", 0);
        TextView scoretxt = findViewById(R.id.txtCodeScore);
        scoretxt.setText("Score: " + score.toString());



        //display geolocation
        Button codeLocation = findViewById(R.id.txtCodeLocation);
        codeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashScore hashScore = new HashScore();
                CollectionReference userRef = db.collection("QRCodes");
                DocumentReference docUserRef = userRef.document(hashScore.hash256(qrid));
                docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        locationOr = document.getBoolean("sharedLocation");
                        // check the code shared the location or not
                        if (locationOr == false) {
                            ShowMessage();
                        } else {
                            Intent mapdemo2 = new Intent(SelectedQrActivity.this, MapDemo2.class);
                            mapdemo2.putExtra("strqrid", qrid);
                            startActivity(mapdemo2);
                        }


                    }
                });

            }

        });

    }

    /**
     * @description to show the detail to the user
     */
    private void showdetail(){
        android.app.AlertDialog dlg =new android.app.AlertDialog.Builder(SelectedQrActivity.this)
                .setTitle("Can not click")
                .setMessage("This is for owner to know the detail of code, if want operate the code, go to the profile")
                .setPositiveButton("Now I know that", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    /**
     * @description delete the code
     */
    private void deleteCode() {
        Intent intent = getIntent();
        Integer i = intent.getIntExtra("index", 0);
        SharedData appData = (SharedData) getApplication();
        String username = appData.getUsername();
        CollectionReference userRef = db.collection("Users");
        DocumentReference docUserRef = userRef.document(username);
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Long totalNumber = document.getLong("total");
                ArrayList<HashMap> tmp_codeScoreList = (ArrayList<HashMap>) document.get("codes");
                ArrayList<CodeScore> codeScoreList = new ArrayList<>();

                for(int i=0;i<tmp_codeScoreList.size();i++){
                    CodeScore tmp = new CodeScore((String)tmp_codeScoreList.get(i).get("code"),((Long)tmp_codeScoreList.get(i).get("score")).intValue());
                    codeScoreList.add(tmp);
                }
                Collections.sort(codeScoreList);
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectedQrActivity.this);
                //asking to delete the code or not
                builder.setMessage("Are you sure to delete this code?");
                builder.setTitle("Information");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete the code
                        codeScoreList.remove(codeScoreList.get(i));
                        docUserRef.update("codes",codeScoreList);
                        docUserRef.update("total",totalNumber-1);
                        Intent intent = new Intent(SelectedQrActivity.this,UserCode.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    /**
     * @description to show the message
     */

    public void ShowMessage(){
        android.app.AlertDialog dlg =new android.app.AlertDialog.Builder(SelectedQrActivity.this)
                .setTitle("The code has no Location")
                .setMessage("The code do not have Location, can not check the map")
                .setPositiveButton("Now I know that", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dlg.show();
    }




}