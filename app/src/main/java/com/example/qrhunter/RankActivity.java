package com.example.qrhunter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
/**
 * This interface has four button to check the button
 */
public class RankActivity extends AppCompatActivity {
    Button amount,highest,sum,unique;
    Intent intent01,intent02,intent03,intent04;
    private ArrayList<User> userDataList;
   // FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        userDataList = new ArrayList<>();




        amount = findViewById(R.id.amount_rank_button);
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent01 = new Intent(RankActivity.this, RankAmount.class);
                startActivity(intent01);

            }
        });

        highest = findViewById(R.id.highest_rank_button);
        highest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent02 = new Intent(RankActivity.this, RankHighest.class);
                startActivity(intent02);
            }
        });


        sum = findViewById(R.id.sum_rank_button);
        sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent03 = new Intent(RankActivity.this, RankSum.class);
                startActivity(intent03);
            }
        });

        unique = findViewById(R.id.unique_rank_button);
        unique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent04 = new Intent(RankActivity.this, RankUnique.class);
                startActivity(intent04);
            }
        });
        Button btn =  findViewById(R.id.back_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(RankActivity.this,MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });


    }}



