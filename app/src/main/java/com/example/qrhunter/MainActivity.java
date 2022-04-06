package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * @description This file is responsible for the overall operation
 * of the app, and the jumping of pages
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnTmp;
    //String userId  = "1234";
    FirebaseFirestore db;
    SharedData appData;
    boolean addCode ;
    boolean exist = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appData = (SharedData) getApplication();

        btnTmp = findViewById(R.id.btnPlay);
        btnTmp.setOnClickListener(this);
        //btnTmp = findViewById(R.id.btnMap);
        btnTmp.setOnClickListener(this);
        btnTmp = findViewById(R.id.btnSignOut);
        btnTmp.setOnClickListener(this);
//        btnTmp = findViewById(R.id.btnProfile);
//        btnTmp.setOnClickListener(this);

        //sharedData appData = (SharedData) getApplication();
        SharedPreferences preferences = getSharedPreferences("QRHunter", MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String userPassword = preferences.getString("userPassword", "");
        if (!userName.equals("")) {
            appData.setUsername(userName);
            appData.setPlayerName(userName);
        }

        String user = appData.getUsername();
        if (user.equals("")) {
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
        }
        db = FirebaseFirestore.getInstance();
        //1234 Log.e("user",user.getUserPasscode() );
        /**
         * @description to check user profile
         */
        Button profile = findViewById(R.id.btnProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appData.setUsername(appData.getPlayerName());
                Intent intent = new Intent(MainActivity.this, UserCode.class);
                startActivity(intent);
            }
        });
        /**
         * @description to check user's rank
         */
        Button rank= findViewById(R.id.btnRank);
        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appData.setUsername(appData.getPlayerName());
                Intent rankActivity = new Intent(MainActivity.this, RankActivity.class);
                startActivity(rankActivity);
            }
        });
        /**
         * @description to check the map
         */
        Button map= findViewById(R.id.btnMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appData.setUsername(appData.getPlayerName());
                Intent rankActivity = new Intent(MainActivity.this, MapDemo.class);
                startActivity(rankActivity);
            }
        });
        /**
         * @description to search
         */

        final Button searchCode = findViewById(R.id.searchbtn);
        searchCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCode= false;
                scanCode();
            }
        });


        /**
         * @description to check user profile
         */
        Button code = findViewById(R.id.btnCode);
        code.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appData.setUsername(appData.getPlayerName());
                Intent GenerateCOde = new Intent(MainActivity.this, GenerateCOde.class);
                startActivity(GenerateCOde);
            }
        }));

        SearchView searchView = (SearchView) findViewById(R.id.txtSearchUser);
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchUser(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
                    Log.d("", "text change");
                }
                return true;
            }
        });


        final CollectionReference collectionReference = db.collection("Owners");
        Button manage= findViewById(R.id.btnManage);
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {
                        boolean yes = false;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if(user.equals(doc.getId())){
                                Intent rankActivity = new Intent(MainActivity.this, ManageActivity.class);
                                startActivity(rankActivity);
                                yes=true;
                            }

                        }
                        if(yes == false){ notOwner();}
                    }
                });

            }
        });



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                appData.setUsername(appData.getPlayerName());
                addCode=true;
                scanCode();
                break;
            //case R.id.btnMap:
            //break;
            case R.id.btnSignOut:
                signOut();
                break;

            default:
                break;

        }
    }
    private void notOwner(){
        android.app.AlertDialog dlg =new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle("NOT Owner")
                .setMessage("You are not a owner, can not manage")
                .setPositiveButton("Now I know that", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to sign out?");
        builder.setTitle("Information");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                appData.setUsername("");

                SharedPreferences.Editor editor = getSharedPreferences("QRHunter", MODE_PRIVATE).edit();
                editor.putString("userName", "");
                editor.putString("userPassword", "");
                editor.apply();

                Intent intent = new Intent(MainActivity.this, SigninActivity.class);
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

    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.setBarcodeImageEnabled(true);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            if (result.getContents() != null) {
                SharedData appData = (SharedData) getApplication();
                if (!(addCode)) {
                    CollectionReference usersRef = db.collection("Users");
                    DocumentReference docUserRef = usersRef.document(result.getContents().replace("/","\\/"));
                    docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().exists()) {
                                    showMessage("User not found.");
                                }else{
                                    appData.setUsername(result.getContents());
                                    Intent intent = new Intent(MainActivity.this, UserCode.class);
                                    intent.putExtra("check", true);
                                    startActivity(intent);
                                }
                            }
                        }});

                    //boolean now = existUser(result.getContents());
                    //Log.e(TAG, "onActivityResult: "+now );
                      //if(now==true) {
                          //appData.setUsername(result.getContents());
                          //Intent intent = new Intent(this, UserCode.class);
                          //intent.putExtra("check", true);
                          //startActivity(intent);
                      //}
               } else if (addCode) {
                    appData.setQrcode(result.getContents());
                    appData.setImagepath(result.getBarcodeImagePath());
                    HashScore hashScore = new HashScore();
                    appData.setQrcodekey(hashScore.hash256(result.getContents()));
                    Intent intent1 = new Intent(this, ScoreActivity.class);
                    startActivity(intent1);
              }



                /**if(search == false){
                 Intent intent = new Intent(this, UserProfile.class);
                 startActivity(intent);
                 }else{
                 Intent intent = new Intent(this, ScoreActivity.class);
                 startActivity(intent);}**/

//
            } else {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void SearchUser(String SearchName) {
        CollectionReference usersRef = db.collection("Users");
        DocumentReference docUserRef = usersRef.document(SearchName);
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        showMessage("User not found.");
                    } else {

                        appData.setSearchname(SearchName);

                        Intent intent = new Intent(MainActivity.this, UserProfile.class);


                        startActivity(intent);


                        //Button go = findViewById(R.id.search_btn);
                         //go.setOnClickListener(new View.OnClickListener() {
                         //@Override
                          //public void onClick(View view) {
                        // Intent intent = new Intent(MainActivity.this, UserProfile.class);
                        // intent.putExtra("username",SearchName);
                        //Intent intent = new Intent(MainActivity.this, UserCode.class);
                        // startActivity(intent);
                        // }
                        // });

                    }
                    Log.e(TAG, "User documents write success. ");
                } else {
                    Log.e(TAG, "Error getting user documents: ", task.getException());
                }
            }
        });
    }
    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Error");
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean existUser(String result){
        CollectionReference usersRef = db.collection("Users");
        DocumentReference docUserRef = usersRef.document(result);
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        showMessage("User not found.");
                        exist=false;
                    }

                    }
                }});
        return exist;

    }

}