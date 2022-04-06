package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
//in progress

/**
 * @description Enables users to comment on the code
 *
 *
 */
public class CodeCommentActivity extends AppCompatActivity implements View.OnClickListener {
    String codeDisplay;
    String userName;
    String qrId;
    FirebaseFirestore db;
    SharedData appData;
    String searchedUserName;

    final String TAG = "CodeCommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codecomment);

        Intent intent=getIntent();
        qrId = intent.getStringExtra("qrid");
        searchedUserName = intent.getStringExtra("searchedUserName");


        db = FirebaseFirestore.getInstance();
        appData = (SharedData) getApplication();
        codeDisplay = appData.getCodedisplay();
        userName = appData.getUsername();

        Button button;
        button = findViewById(R.id.btnAddCodeComment);
        button.setOnClickListener(this);
        button = findViewById(R.id.btnBackToCodeImage);
        button.setOnClickListener(this);

        displayInformation();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddCodeComment:
                doAddComment();
                break;
            case R.id.btnBackToCodeImage:

                goBack(searchedUserName);
                break;
            default:
                break;
        }
    }
    /**
     * @description show information to the user
     *
     *
     */

    private void displayInformation() {
        HashScore hashScore = new HashScore();
        CollectionReference codesRef = db.collection("QRCodes");
        DocumentReference docCodeRef = codesRef.document(hashScore.hash256(qrId));
        docCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String comment = document.getString("comment");
                    if (comment != null) {
                        TextView textView = findViewById(R.id.txtAllComments);
                        textView.setText(comment);
                    }
                    Log.d(TAG, "User documents read success. ");
                } else {
                    Log.d(TAG, "Error getting user documents: ", task.getException());
                }
            }
        });
    }

    /**
     * @description Perform the action of adding a comment by the user
     *
     *
     */

    private void doAddComment() {
        EditText editText = findViewById(R.id.txtCodeComment);
        String comment = editText.getText().toString();
        if (comment == null)
            return;
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        TextView textView = findViewById(R.id.txtAllComments);
        String allComments = textView.getText().toString();
        if (allComments == null)
            allComments = comment + "\n        Written by " + userName + "    " + dateFormat.format(date);
        else
            allComments = allComments + "\n\n" + comment + "\n        Written by " + userName + "    " + dateFormat.format(date);
        textView.setText(allComments);

        HashScore hashScore = new HashScore();
        CollectionReference codesRef = db.collection("QRCodes");
        DocumentReference docCodeRef = codesRef.document(hashScore.hash256(qrId));
        docCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String, String> data = new HashMap<>();
                    data.put("comment", textView.getText().toString());
                    docCodeRef.set(data, SetOptions.merge());
                    Log.d(TAG, "User documents write success. ");
                } else {
                    Log.d(TAG, "Error getting user documents: ", task.getException());
                }
            }
        });
    }

    /**
     * @description Return to the previous page
     * @param searchedUserName
     */

    private void goBack(String searchedUserName) {
//        if (appData.getComefromme()) {
//            intent = new Intent(this, SelectedQrActivity.class);
//        } else {
//            intent = new Intent(this, SelectedSearchUserQr.class);
//        }


//        Intent intent = new Intent(this, SelectedSearchUserQr.class);
//
//
//        intent.putExtra("qrid", qrId);
//        intent.putExtra("searchedUserName", searchedUserName);
//        Log.d(TAG, "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + searchedUserName);
//
//        startActivity(intent);
        finish();
    }
}