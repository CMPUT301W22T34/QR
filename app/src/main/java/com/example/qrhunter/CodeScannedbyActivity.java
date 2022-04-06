package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
//in progress
/**
 * @description Shows which user scanned each QR code.
 *
 *
 */
public class CodeScannedbyActivity extends AppCompatActivity implements View.OnClickListener{

    String codeDisplay;
    FirebaseFirestore db;

    ListView userList;
    ArrayList<UserScore> userScoreList;
    ArrayAdapter<UserScore> userScoreAdapter;

    final String TAG = "CodeScannedbyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codescannedby);

        db = FirebaseFirestore.getInstance();
        SharedData appData = (SharedData) getApplication();
        codeDisplay = appData.getCodedisplay();

        Button button;
        button = findViewById(R.id.btnBackToCodeImage1);
        button.setOnClickListener(this);

        CollectionReference codesRef = db.collection("QRCodes");
        DocumentReference docCodeRef = codesRef.document(codeDisplay);
        docCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    int userScore;
                    userScoreList = new ArrayList<UserScore>();
                    DocumentSnapshot document = task.getResult();
                    ArrayList<String> mapList = (ArrayList<String>) document.get("scanners");
                    for(String userName:mapList){
//                        userScore = getUserScore(userName);
                        userScore = 0;
                        userScoreList.add(new UserScore(userName, userScore));
                    }
                    userScoreAdapter = new UserList(userScoreList,CodeScannedbyActivity.this);
                    userList = findViewById(R.id.lstCodeScannedby);
                    userList.setAdapter(userScoreAdapter);
                    Log.d(TAG, "User documents write success. ");
                } else {
                    Log.d(TAG, "Error getting user documents: ", task.getException());
                }
            }
        });
    }
    /**
     * @description Get the user's score
     *
     *
     */

    private int getUserScore(String userName) {
        CollectionReference codesRef = db.collection("Users");
        DocumentReference docCodeRef = codesRef.document(userName);
        Task<DocumentSnapshot> task = docCodeRef.get();
        DocumentSnapshot document = task.getResult();
        int score = ((Long) document.get("score")).intValue();
        return score;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnBackToCodeImage1) {
            Intent intent = new Intent(this, QRCodeActivity.class);
            startActivity(intent);
        }
    }
}