package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * @description This java file is to show the detail of the code from the code list of the searched user
 */
public class SelectedSearchUserQr extends AppCompatActivity {
    String codeDisplay;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_search_user_qr);
        Intent intent=getIntent();
        String qrid=intent.getStringExtra("qrid");
        String searchedUserName = intent.getStringExtra("searchedUserName");
        db = FirebaseFirestore.getInstance();
        SharedData appData = (SharedData) getApplication();
        String user = appData.getUsername();
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
                Intent intent = new Intent(SelectedSearchUserQr.this,WhoAlsoScan.class);
                intent.putExtra("qrid",hashScore.hash256(qrid));
                startActivity(intent);
            }
        });
        //back button
        Button backbutton;
        backbutton = findViewById(R.id.btnBackToCodeList);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SelectedSearchUserQr.this,SearchUserCode.class);
                intent.putExtra("userName",searchedUserName);
                startActivity(intent);
            }
        });
        // comment button
        Button codecommentbutton;
        codecommentbutton = findViewById(R.id.btnCodeComment);
        codecommentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashScore hashScore = new HashScore();
                Intent intent = new Intent(SelectedSearchUserQr.this,CodeCommentActivity.class);
                intent.putExtra("userName",user);
                intent.putExtra("qrid", hashScore.hash256(qrid));
                intent.putExtra("searchedUserName", searchedUserName);

                startActivity(intent);
            }
        });
        // show the score of the code
        Long score = intent.getLongExtra("score", 0);
        TextView scoretxt = findViewById(R.id.txtCodeScore);
        scoretxt.setText("Score: " + score);
        //display the location of the code on the map
        Button codeLocation = findViewById(R.id.txtCodeLocation);
        codeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashScore hashScore = new HashScore();
                Intent mapdemo2 = new Intent(SelectedSearchUserQr.this, MapDemo2.class);
                mapdemo2.putExtra("strqrid",hashScore.hash256(qrid));
                startActivity(mapdemo2);
            }
        });
    }
}