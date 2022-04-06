package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
/**
 * This file is mainly responsible for owner to delete QRCode in the database.
 *
 */
public class DeleteCodesActivity extends AppCompatActivity {
    FirebaseFirestore db;
    private ListView codeList;
    private ArrayList<String> codeDataList;
    private ArrayAdapter<String> codeAdapter;
    private int chosenLine=0;
    private String ID; //= "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_codes);


        codeList = findViewById(R.id.codes_list);
        codeDataList = new ArrayList<>();
        codeAdapter=new ArrayAdapter<>(DeleteCodesActivity.this,android.R.layout.simple_list_item_1,codeDataList);
        codeList.setAdapter(codeAdapter);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("QRCodes");

        SharedData appData = (SharedData) getApplication();
        String user = appData.getUsername();





        /**
         * the way to delete a code is that:
         * owner firstly click a code
         * then click Delete Button
         */
        codeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chosenLine=i;
            }
        });

        codeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                CollectionReference userRef = db.collection("QRCodes");
                DocumentReference docUserRef = userRef.document(codeDataList.get(i));
                //DocumentReference docUserRef = userRef.document(searchName);
                docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                         Long score = document.getLong("score");
                         String qrid = document.getString("qrid");
                         Intent intent=new Intent(DeleteCodesActivity.this,SelectedQrActivity.class);
                         intent.putExtra("qrid",qrid);
                         intent.putExtra("index", i);
                         intent.putExtra("score", score);
                         intent.putExtra("check",true);
                         startActivity(intent);

                        } });

               /** Intent intent=new Intent(UserCode.this,SelectedQrActivity.class);
                intent.putExtra("qrid",userstr);
                intent.putExtra("index", i);
                intent.putExtra("score", score);
                startActivity(intent);
                return true;
                **/
               return false;
            }
        });


        /**
         * owner can click long on the code item to check the code detail
         * but it only check the location and score
         * can not delete in the detail interface
         * and can not comment it, if want can go to the profile or search the code
         */
        Button btn =  findViewById(R.id.back_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(!ID.equals(codeDataList.get(chosenLine))){
                    collectionReference
                            .document(codeDataList.get(chosenLine))
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   // Log.e("work", "Data has been deleted successfully!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                  //  Log.d("not work", "Data could not be deleted!" + e.toString());
                                }
                            });

                    codeAdapter.notifyDataSetChanged();
                    chosenLine=0;

                //}
               /// else{
                 ///   Log.e("cannot","this is own" );
              //  }
               // db = FirebaseFirestore.getInstance();
               // final CollectionReference collectionReference = db.collection("Users");

            }

        });


        /**
         * make the list show by using data from database
         */

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {

                codeDataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                { String code = doc.getId();
                    codeDataList.add(code);
                }
                codeAdapter.notifyDataSetChanged();
            }
        });

    }
}