package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @description to Directing the activities of QRcode
 */
public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener{

    String codeDisplay;
    FirebaseFirestore db;

    String TAG = "QRCodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        db = FirebaseFirestore.getInstance();
        SharedData appData = (SharedData) getApplication();
        codeDisplay = appData.getCodedisplay();



        TextView textView;
        Button button;
        textView = findViewById(R.id.txtElse);
        textView.setOnClickListener(this);
        button = findViewById(R.id.btnCodeDelete);
        button.setOnClickListener(this);
        button = findViewById(R.id.btnCodeComment);
        button.setOnClickListener(this);
        button = findViewById(R.id.btnBackToCodeList);
        button.setOnClickListener(this);

        displayCodeInformation();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtElse:
                displayElse();
                break;
            case R.id.btnCodeDelete:
                deleteCode();
                break;
            case R.id.btnCodeComment:
                updateComment();
                break;
            case R.id.btnBackToCodeList:
                goBack();
                break;
            default:
                break;
        }
    }
    /**
     * @description to display the QR code information
     */
    private void displayCodeInformation() {
        CollectionReference codesRef = db.collection("QRCodes");
        DocumentReference docUserRef = codesRef.document(codeDisplay);
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Long codeScore = document.getLong("score");
                    // pistion

                    String sTmp;
                    if (codeDisplay.length()>18) {
                        sTmp = codeDisplay.substring(0, 18) + "...";
                    } else {
                        sTmp = codeDisplay;
                    }
                    TextView textView;
                    textView = findViewById(R.id.txtQrcode);
                    textView.setText(sTmp);
                    textView = findViewById(R.id.txtCodeScore);
                    textView.setText(Long.toString(codeScore));

                    generateQRImage();
                    Log.d(TAG, "User documents write success. ");
                } else {
                    Log.d(TAG, "Error getting user documents: ", task.getException());
                }
            }
        });
    }
    /**
     * @description to generate QR code Image
     */
    private void generateQRImage() {
        int width = 200; // 图像宽度
        int height = 200; // 图像高度
        String format = "gif";// 图像类型
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = null;
        try {
            bitmap = barcodeEncoder.encodeBitmap(codeDisplay, BarcodeFormat.QR_CODE, width, height,hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        ImageView imageView = findViewById(R.id.imgQrcode1);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * @description go back to the previous page
     */

    private void displayElse() {
        Intent intent = new Intent(this, CodeScannedbyActivity.class);
        startActivity(intent);
    }

    /**
     * @description delete QR code
     */

    private void deleteCode() {
        Intent intent = getIntent();
        Integer i = intent.getIntExtra("index", 0);
        //String i = intent.getStringExtra("index");
        SharedData appData = (SharedData) getApplication();
        String username = appData.getUsername();
        CollectionReference userRef = db.collection("Users");
        DocumentReference docUserRef = userRef.document(username);
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                ArrayList<HashMap> tmp_codeScoreList = (ArrayList<HashMap>) document.get("codes");
                ArrayList<CodeScore> codeScoreList = new ArrayList<>();

                for(int i=0;i<tmp_codeScoreList.size();i++){
                    CodeScore tmp = new CodeScore((String)tmp_codeScoreList.get(i).get("code"),((Long)tmp_codeScoreList.get(i).get("score")).intValue());
                    codeScoreList.add(tmp);
                }
                codeScoreList.remove(codeScoreList.get(i));

                AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeActivity.this);
                builder.setMessage("Are you sure to delete this code?");
                builder.setTitle("Information");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete
                        Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + " deleting code xdxdxdxdxdxdxdxdxdxdxdxd");
                        codeScoreList.remove(codeScoreList.get(i));
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
     * @description update users comment
     */

    private void updateComment() {
        Intent intent = new Intent(this, CodeCommentActivity.class);
        startActivity(intent);
    }

    /**
     * @description go back to the previous page
     */

    private void goBack() {
        Intent intent = new Intent(this,UserProfile.class);
        startActivity(intent);
    }

}
