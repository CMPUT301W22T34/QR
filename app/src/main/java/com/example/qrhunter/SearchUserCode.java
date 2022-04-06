package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @description This function is to show the code list with the highest and the lowest scoring code of the searched user
 */
public class SearchUserCode extends AppCompatActivity {

    FirebaseFirestore db;
    SharedData appData;
    Button buttonlowest;
    Button buttonhighest;
    String highestscore="";
    String lowestscore="";
    ArrayAdapter<CodeScore> codeAdapter;
    ArrayList<CodeScore> codeScoreList;
    TextView txtTotalScore;
    TextView txtNumber ;
    ListView codeList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_code);

        TextView txtUsername = findViewById(R.id.txtUsername);
        Intent intent=getIntent();
        String userName=intent.getStringExtra("userName");
        txtUsername.setText(userName);

        //connect with database
        db = FirebaseFirestore.getInstance();
        txtTotalScore = findViewById(R.id.txtTotalScore);
        txtNumber = findViewById(R.id.txtNumber);
        codeList = findViewById(R.id.code_list);
        CollectionReference userRef = db.collection("Users");
        DocumentReference docUserRef = userRef.document(userName);
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                //get data from database
                Long totalScore = document.getLong("sum");
                Long totalNumber = document.getLong("total");
                ArrayList<HashMap> tmp_codeScoreList = (ArrayList<HashMap>) document.get("codes");
                codeScoreList = new ArrayList<>();

                for (int i = 0; i < tmp_codeScoreList.size(); i++) {
                    CodeScore tmp = new CodeScore((String) tmp_codeScoreList.get(i).get("code"), ((Long) tmp_codeScoreList.get(i).get("score")).intValue());
                    codeScoreList.add(tmp);
                }
                codeAdapter = new ArrayAdapter<CodeScore>(SearchUserCode.this, android.R.layout.simple_list_item_1,codeScoreList);
                txtTotalScore.setText(totalScore.toString());
                txtNumber.setText(totalNumber.toString());
                codeList.setAdapter(codeAdapter);
                //sort the codeList
                Collections.sort(codeScoreList);
                codeAdapter.notifyDataSetChanged();
                if (codeScoreList.size()>=1) {
                    docUserRef.update("highest",codeScoreList.get(codeScoreList.size()-1).score);
                    lowestscore="THe  lowest score QR name is "+codeScoreList.get(0).code+"\n"+"It's score is "+codeScoreList.get(0).score+".";
                    highestscore="THe highest score QR name is "+codeScoreList.get(codeScoreList.size()-1).code+"\n"+"It's score is "+codeScoreList.get(codeScoreList.size()-1).score+".";
                }else{
                    lowestscore = "There no codes";
                    highestscore = "There no codes";
                }

              codeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String userstr=codeScoreList.get(i).getCode();
                        Intent intent=new Intent(SearchUserCode.this,CodeCommentActivity.class);
                        intent.putExtra("qrid",userstr);
                        //CollectionReference codes  = db.collection("QRCodes");
                        //codes.addSnapshotListener(new EventListener<QuerySnapshot>() {
                          //  @Override
                            //public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable
                              //      FirebaseFirestoreException error) {
                                //for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                  //  String ID = doc.getId();
                                    //Intent intent=new Intent(SearchUserCode.this,SelectedSearchUserQr.class);
                                    //intent.putExtra("qrid",userstr);
                                    //intent.putExtra("index", i);
                                    //intent.putExtra("searchedUserName",userName);
                                   // long score = codeScoreList.get(i).getScore();
                                   // intent.putExtra("score", score);
                                    startActivity(intent);}
                           // }
                                /*
                                boolean exit = false;
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    String ID = doc.getId();
                                    if (ID.equals(userstr)) {
                                        exit = true;
                                    }
                                }
                                if (exit == false) {
//                                    showDelete(i);
                                } else {
                                    SharedData appData = (SharedData) getApplication();
                                    appData.setComefromme(false);
                                    Intent intent = new Intent(SearchUserCode.this, SelectedSearchUserQr.class);
                                    intent.putExtra("qrid", userstr);
                                    intent.putExtra("index", i);
                                    intent.putExtra("searchedUserName", userName);
                                    Log.d(TAG, "---------------------------------==" + userName);
                                    long score = codeScoreList.get(i).getScore();
                                    intent.putExtra("score", score);
                                    startActivity(intent);
                                }
                            }
                        });*/


                   // });
                    //}
                });
            }
        });
        buttonhighest = findViewById(R.id.high_code);
        buttonlowest = findViewById(R.id.low_code);
        buttonhighest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show the highest scoring code
                highestDialog(highestscore);
            }
        });
        buttonlowest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show the lowest scoring code
                lowestDialog(lowestscore);
            }
        });
        Button back = findViewById(R.id.back_to_profile);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchUserCode.this,UserProfile.class);
                startActivity(intent);
            }
        });

    }

    /**
     * @description to show the highest score to user
     * @param message
     */
    public void highestDialog(String message){
        AlertDialog dlg =new AlertDialog.Builder(SearchUserCode.this)
                .setTitle("Highest Score QR Code")
                .setMessage(message)
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
     * @description to show the lowest score to user
     * @param message
     */
    public void lowestDialog(String message){
        AlertDialog dlg =new AlertDialog.Builder(SearchUserCode.this)
                .setTitle("Lowest Score QR Code")
                .setMessage(message)
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