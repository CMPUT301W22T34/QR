package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//signin, and remember me
public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    String savedName;
    String savedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button btnTmp;
        TextView txtTmp;
        //btnTmp = (Button) findViewById(R.id.btnSignin);
        //btnTmp.setOnClickListener(this);
        txtTmp = (TextView) findViewById(R.id.txtSignup);
        txtTmp.setOnClickListener(this);
        txtTmp = (TextView) findViewById(R.id.txtSigninQRCode);
        txtTmp.setOnClickListener(this);

        // read from local, auto sign in
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSignup:
                Intent intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.txtSigninQRCode:
                scanCode();
                break;
            default:
                break;
        }

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
//                SharedData appData = (SharedData) getApplication();
//                appData.setUsername(result.getContents());
//                appData.setPlayerName(result.getContents());
//                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
//                startActivity(intent);
                signin(result.getContents());


            } else {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void signin(String account) {
        SharedData appData = (SharedData) getApplication();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        usersRef.whereEqualTo("userName", account)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        showMessage("Account or password error");
//                        edtTmp.requestFocus();
                        return;
                    } else {
                        //showMessage("Existing Account,Please Use QR Code Sign In ");
//                    for (QueryDocumentSnapshot document : task.getResult()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        appData.setUsername(document.getId());
                        appData.setPlayerName(document.getId());
                        Log.d(TAG, document.getId() + " => " + document.getData());
//                        Log.d("TAG", "onComplete1: " + " => " + document.toObject(User.class));
//                        Log.d("TAG", "onComplete2: " + " => " + appData.getUser().getName() + "  " + appData.getUser().getPassword());
                        CheckBox chkTmp = (CheckBox) findViewById(R.id.chkRemember);
                        if (chkTmp.isChecked()) {
                            // 把登录信息保存到本地文件中
                            SharedPreferences.Editor editor = getSharedPreferences("QRHunter", MODE_PRIVATE).edit();
                            editor.putString("userName", account);
                            editor.putString("userPassword", "");
                            editor.apply();

                        }

                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(intent);
//                    }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void signin2(String account) {
        SharedData appData = (SharedData) getApplication();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        usersRef.whereEqualTo("userName", account)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        showMessage("Account or password error");
//                        edtTmp.requestFocus();
                        return;
                    } else {
                        showMessage("Existing Account,Please Use QR Code Sign In ");
//                    for (QueryDocumentSnapshot document : task.getResult()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        appData.setUsername(document.getId());
                        appData.setPlayerName(document.getId());
                        Log.d(TAG, document.getId() + " => " + document.getData());
//                        Log.d("TAG", "onComplete1: " + " => " + document.toObject(User.class));
//                        Log.d("TAG", "onComplete2: " + " => " + appData.getUser().getName() + "  " + appData.getUser().getPassword());
                        CheckBox chkTmp = (CheckBox) findViewById(R.id.chkRemember);
                        if (chkTmp.isChecked()) {
                            // 把登录信息保存到本地文件中
                            SharedPreferences.Editor editor = getSharedPreferences("QRHunter", MODE_PRIVATE).edit();
                            editor.putString("userName", account);
                            editor.putString("userPassword", "");
                            editor.apply();

                        }

                        //Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        //startActivity(intent);
//                    }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
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
}