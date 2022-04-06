package com.example.qrhunter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @description to control the share activity
 */

public class ShareActivity extends AppCompatActivity {
    private ImageView shotview ;//shotview定义在这里
    //【warning】如果这里写成：private ImageView shotview = findViewById(R.id.imageView);会报错
    //因为需要在onCreate()将类实例化之后，才可以进行初始化。
    private File currentImageFile = null;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        shotview = findViewById(R.id.picture); //shotview的初始化在这里

        TextView notShare = findViewById(R.id.refuse);
        notShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button takePhoto = findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener()
        {
            //直接调用系统摄像头
            @Override
            public void onClick(View v)
            {
                //dir指的是directory，目录。storage directory 存储目录。

                //如果你想在外存储上放公共文件你可以使用getExternalStoragePublicDirectory()
                //but！如果你的api 版本低于8，那么不能使用getExternalStoragePublicDirectory()，
                //而是使用Environment.getExternalStorageDirectory(),不带参数，不能自己创建一个目录，只是返回外部存储的根路径。
                File dir = new File(Environment.getExternalStorageDirectory(),"pictures");
                //函数原型：File newFile=new File(directory, filename)
                //创建了一个文件夹，名字是dir，路径是外部存储的根路径，名字是"pictures"。
                if(dir.exists())
                {
                    dir.mkdirs();//在根路径下建子目录，子目录名是"pictures"
                }

                //命名临时图片的文件名
                currentImageFile = new File(dir,System.currentTimeMillis() + ".jpg");
                if(!currentImageFile.exists())
                {
                    try
                    {
                        currentImageFile.createNewFile();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//通过intent调用照相机照相
                it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
                //存到外部存储的临时文件currentImageFile的路径下
                startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
                //如果想在Activity中得到新打开Activity 关闭后返回的数据，
                // 需要使用系统提供的startActivityForResult(Intent intent, int requestCode)方法
                //打开新的Activity，新的Activity 关闭后会向前面的Activity传回数据，为了得到传回的数据，
                //必须在前面的Activity中重写onActivityResult(int requestCode, int resultCode, Intent data)方法。

            }
        });
        Button back = findViewById(R.id.back);
        notShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    //重写onActivityResult(int requestCode, int resultCode, Intent data)方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.DEFAULT_KEYS_DIALER) {
            shotview.setImageURI(Uri.fromFile(currentImageFile));
        }

        db = FirebaseFirestore.getInstance();
        SharedData appData = (SharedData) getApplication();
        String qrCode = appData.getQrcode();
        HashScore hashScore = new HashScore();
        CollectionReference qrCodeRef = db.collection("QRCode");
        DocumentReference docqrCodeRef = qrCodeRef.document(hashScore.hash256(qrCode));
        docqrCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = new HashMap<>();
                    data.put("image", currentImageFile);
                    docqrCodeRef.set(data, SetOptions.merge());

                }
            }
        });
    }


}