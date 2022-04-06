package com.example.qrhunter;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * this file for signing up
 */
//if you dont have an account, then you can signup one
public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button btnTmp = (Button) findViewById(R.id.btnSignup);
        btnTmp.setOnClickListener(this);
        TextView txtTmp = (TextView) findViewById(R.id.txtSignin);
        txtTmp.setOnClickListener(this);
    }

    /**
     * player action
     * @param view
     */

    @Override
    public void onClick(View view) {
        EditText edtTmp;
        switch (view.getId()) {
            case R.id.btnSignup:
                edtTmp = (EditText) findViewById(R.id.txtUserSignup);
                account = edtTmp.getText().toString();
                if (account.equals("")) {
                    showMessage("Account can't be empty!");
                    edtTmp.requestFocus();
                    return;
                }
                saveAccount(account, "");
                break;
            case R.id.txtSignin:
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * save account in the database
     * @param account
     * @param password
     */

    private void saveAccount(String account, String password) {
        User Account = new User(account, password);
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");

        DocumentReference docRef = usersRef.document(account);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
//                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        showMessage("Account already exists");
                        EditText edtTmp = (EditText) findViewById(R.id.txtUserSignup);
                        edtTmp.requestFocus();
                    } else {
//                        Log.d("TAG", "No such document");
                        hideInput();
                        docRef.set(Account);

                        TextView textView = findViewById(R.id.txtSignin);
                        textView.setVisibility(View.VISIBLE);
                        Bitmap bitmap = createBitmap(account);
                        ImageView imageView = findViewById(R.id.userQRCode);
                        imageView.setImageBitmap(bitmap);
                        showMessage("Account sign success, please download your QRCode.");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }


    /**
     * show the player unique code
     * @param signInQrVl
     * @return
     */
    private Bitmap createBitmap(String signInQrVl) {
        BitMatrix result = null;
        try {
            result = new MultiFormatWriter().encode(signInQrVl,
                    BarcodeFormat.QR_CODE,
                    220,
                    220);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for(int i = 0; i < height; i++) {
            int offset = i * width;
            for (int k=0; k<width; k++) {
                pixels[offset + k] = result.get(k, i) ? BLACK : WHITE;
            }
        }
        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        myBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return myBitmap;
    }

    private void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * do not show input
     */
    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * show message
     * @param message
     */
    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Information");
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}