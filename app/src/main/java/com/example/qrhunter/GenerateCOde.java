package com.example.qrhunter;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * @description The generated QR code is for players
 * to scan the QR code for login in
 *
 *
 */

public class GenerateCOde extends AppCompatActivity {
    ImageView SignInCode;
    Button back;
    String username;
    SharedData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);
        appData = (SharedData) getApplication();
        username = appData.getUsername();

        //set the value for generate QR code
        SignInCode = findViewById(R.id.SigninCode);
        String SignInQrVl = username;
        Bitmap SignInQR = createBitmap(SignInQrVl);
        SignInCode.setImageBitmap(SignInQR);

        //set the button for go back to the main page
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    /**
     * @description generate the QR code for user to log in
     *
     *
     */
    private Bitmap createBitmap(String signInQrVl) {
        BitMatrix result = null;
        try {
            result = new MultiFormatWriter().encode(signInQrVl,
                    BarcodeFormat.QR_CODE,
                    440,
                    440);
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

}