package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @description to control the activity of score
 */
public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {
    String qrCode;
    String imagePath;
    int  score;
    String userName;
    FirebaseFirestore db;
    Boolean savedPicture = false;
    Boolean savedGeo = false;

    final String TAG = "ScoreActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Button btnTmp;
        TextView txtTmp;
        btnTmp = (Button) findViewById(R.id.btnBackToScan);
        btnTmp.setOnClickListener(this);
        btnTmp = (Button) findViewById(R.id.btnAddQRCode);
        btnTmp.setOnClickListener(this);
        txtTmp = (TextView) findViewById(R.id.btnwhoscanned);
        txtTmp.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        SharedData appData = (SharedData) getApplication();
        qrCode = appData.getQrcode();
        imagePath = appData.getImagepath();
        userName = appData.getUsername();
        //qrCode = "123";
        ImageView imageView = findViewById(R.id.imgQrcode);
        Log.d(TAG, "onCreate: " + imagePath + "    " + qrCode);

        // method 1
        File file = new File(imagePath);
        imageView.setImageURI(Uri.fromFile(file));
        // method 2
//        try {
//            Bitmap bmp = MediaStore.Images.Media.getBitmap(ScoreActivity.this.getContentResolver(), Uri.fromFile(file));
//            imageView.setImageBitmap(bmp);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        HashScore hashScore = new HashScore();
        /* 这里是课程里的例子，但hash值的结果不一样，如果用例子中的hash计算score结果是一样的
        qrcode = "BFG5DGW54";
        Log.d(TAG, "HashCode: " + hashScore.hash256(qrcode));
        String hashcode = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        int score = hashScore.score(hashScore.counter(hashcode));
        */
        Log.d(TAG, "onCreate: Hash = " + hashScore.hash256(qrCode));
        score = hashScore.score(hashScore.counter(hashScore.hash256(qrCode)));
        appData.setCodescore(score);
        TextView textView = findViewById(R.id.txtScore);
        textView.setText(String.valueOf(score));

        CheckBox checkBox = findViewById(R.id.chkRecord);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Button button = findViewById(R.id.btnAddQRCode);
                button.setEnabled(isChecked);
            }
        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackToScan:
                back();
                break;
            case R.id.btnAddQRCode:
                add();
                Intent intent = new Intent(this, SharedPicture.class);
                startActivity(intent);
                break;
            case R.id.btnwhoscanned:
                /*
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(this, CodeScannedbyActivity.class);
                startActivity(intent2);

                 */
                whoscanned();
                //back();
                break;
            default:
                break;
        }
    }

    /**
     * @description to show the list of who also scan this qr code
     */

    private void whoscanned() {
        HashScore hashScore = new HashScore();
        Intent intent = new Intent(this,WhoAlsoScan.class);
        intent.putExtra("qrid",hashScore.hash256(qrCode));
        startActivity(intent);
    }

    /**
     * @description to go back to previous activity
     */
    private void back() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * @description one code can scan so many times and keep adding score
     */
    private void add() {
        //saveGeo();
        // Read user records modify parameters and write back
        CollectionReference usersRef = db.collection("Users");
        DocumentReference docUserRef = usersRef.document(userName);
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = new HashMap<>();
                    data.put("sum", document.getLong("sum") + score);
                    data.put("total", document.getLong("total") + 1);
                    ArrayList<CodeScore> codeScoreList = (ArrayList<CodeScore>) document.get("codes");
                    codeScoreList.add(new CodeScore(qrCode, score));
                    data.put("codes", codeScoreList);
                    // qrCode = "123";
                    //Long var = (Long) score;
                    if(qrCode.equals(userName)){
                        data.put("unique",score);
                    }
                    docUserRef.set(data, SetOptions.merge());

                    Log.d(TAG, "User documents write success. ");
                } else {
                    Log.d(TAG, "Error getting user documents: ", task.getException());
                }
            }
        });
        // If Code exists, read it out, create it if it does not exist, and write it back by modifying the parameters
        HashScore hashScore = new HashScore();
        CollectionReference codesRef = db.collection("QRCodes");

        ///删除了下面代码
        //hashScore.hash256(qrCode)
        DocumentReference docCodeRef = codesRef.document(hashScore.hash256(qrCode));
        docCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        QRCode newCode = new QRCode(qrCode, score);
                        newCode.addScanner(userName);
                        docCodeRef.set(newCode, SetOptions.merge());
                    } else {
                        DocumentSnapshot document = task.getResult();
                        ArrayList<String> userList = (ArrayList<String>) document.get("scanners");
                        userList.add(userName);
                        Map<String, Object> data = new HashMap<>();
                        data.put("scanners", userList);
                        docCodeRef.set(data, SetOptions.merge());
                    }
                    Log.d(TAG, "Code documents write success. ");
                } else {
                    Log.d(TAG, "Error getting code documents: ", task.getException());
                }
            }
        });





    }





    /**public void saveGeo(){

     AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this);
     builder.setMessage("Are you sure to Save Location?");
     builder.setTitle("Information");
     builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
    savedGeo= true;
    savePicture();

    }
    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
    savedGeo = false;
    savePicture();
    }
    });
     AlertDialog dialog = builder.create();
     dialog.show();}

     public void savePicture(){

     AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this);
     builder.setMessage("Are you sure to Save Picture?");
     builder.setTitle("Information");
     builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
    savedPicture = true;
    notBigPhoto();

    }
    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
    savedPicture = false;
    Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
    startActivity(intent);

    }
    });
     AlertDialog dialog = builder.create();
     dialog.show();}


     **/




}