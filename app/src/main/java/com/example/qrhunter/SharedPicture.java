package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

/**
 * this file shows that player shared the picture
 * and take a photo
 * and resize it
 */
public class SharedPicture extends AppCompatActivity {
    FirebaseFirestore db;
    HashScore hashScore;
    String qrCode;
    String filePath;
    String downloadUrl;
    int PICK_IMAGE_REQUEST = 111;
    private File picture;
    String imagePath;

    Uri imageUri;
    final String TAG = "SharedPicture";

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://w301t34.appspot.com");    //change the url according to your firebase app


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_picture);
        SharedData appData = (SharedData) getApplication();
        qrCode = appData.getQrcode();
        imagePath = appData.getImagepath();
        filePath = appData.getImagepath();

        db = FirebaseFirestore.getInstance();
        hashScore = new HashScore();

        //don't share
        TextView notBtn = findViewById(R.id.btntxt);
        notBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donotShare();
                Intent intent = new Intent(SharedPicture.this, SharedGeo.class);
                startActivity(intent);
            }
        });

        /*
        //pick a photo
        Button select = findViewById(R.id.btnSelect);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

         */

        //share
        Button share = findViewById(R.id.btnShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //modify the share
                notBigPhoto();
                share();
                Intent intent1 = new Intent(SharedPicture.this, SharedGeo.class);
                startActivity(intent1);
            }
        });

        //back
        Button back = findViewById(R.id.btnBackToScan);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Intent intent = new Intent(SharedPicture.this, ScoreActivity.class);
                //  startActivity(intent);
                finish();
            }
        });

        //use the code photo
        TextView image = findViewById(R.id.btnphoto);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardPhoto();
            }
        });

        //take a photo
        Button take = findViewById(R.id.btnTake);
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        //default use card photo
//        cardPhoto();
    }
    /**
     * do not share the picture
     */
    private void donotShare() {
        CollectionReference codesRef = db.collection("QRCodes");
        DocumentReference docCodeRef = codesRef.document(hashScore.hash256(qrCode));
        docCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //ArrayList<String> userList = (ArrayList<String>) document.get("scanners");
                    //userList.add(userName);
                    Map<String, Object> data = new HashMap<>();
                    data.put("sharedPicture", false);
                    docCodeRef.set(data, SetOptions.merge());
                }
            }
        });
    }
    /**
     * share the picture
     */
    private void share() {
        if(filePath != null) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
            String image_save_path =  sdf.format(date);
            StorageReference childRef = storageRef.child(image_save_path+".jpg");
            //uploading the image
            Uri uri = Uri.fromFile(new File(filePath));
//            UploadTask uploadTask = childRef.putFile(uri);

            Bitmap bitmap = resizeImage(filePath, 200, 200);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();
            UploadTask uploadTask = childRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //get and save download url
                            downloadUrl = uri.toString();

                            CollectionReference codesRef = db.collection("QRCodes");
                            DocumentReference docCodeRef = codesRef.document(hashScore.hash256(qrCode));
                            docCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        ArrayList<String> urlList = (ArrayList<String>) document.get("http");
                                        urlList.add(downloadUrl);
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("sharedPicture", true);
                                        data.put("http", urlList);
                                        docCodeRef.set(data, SetOptions.merge());
                                    }
                                }

                            });
                            Log.e(TAG, "downloadUrl: " + downloadUrl );
                        }
                    });
                    Toast.makeText(SharedPicture.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SharedPicture.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(SharedPicture.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * save the image path
     */
    private void cardPhoto() {
        ImageView imageView = findViewById(R.id.imgQrcode);
        File file = new File(imagePath);
        imageView.setImageURI(Uri.fromFile(file));
        filePath = imagePath;
    }
    /**
     * check the picture size and resize
     */

    public void notBigPhoto(){
        File file = new File(filePath);
        double size = getFileOrFilesSize(file);
      //  Log.e("size", ""+size);
        if(size<=64){
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);//Environment.getExternalStorageDirectory().getAbsolutePath() + "/compresstest/test.png"
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String str = "2";
            int quality = Integer.parseInt(str);
            if (bitmap != null) {
//            bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos); // 设置Bitmap.CompressFormat.PNG，quality将不起作用，PNG是无损压缩
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                byte[] bytes = baos.toByteArray();
                Bitmap newBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            }

        }
    }

    /**
     * get the file
     * @param file
     * @return static
     */
    public static double getFileOrFilesSize(File file) {

        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Log.e("fail","not exist!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * change the  size format
     * @param fileS
     * @return
     */
    private static double FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
        return fileSizeLong;

    }
    /**
     * get file size
     * @param f
     * @return
     * @throws Exception
     */

    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }
    /**
     * get file size
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            // Log.e("fail","not exit!");
        }
        return size;
    }


    /**
     * take photo
     */
    public void takePhoto() {
//        File outputImage = new File(Environment.getExternalStorageDirectory()+"/outputImage.jpg");
        filePath = getExternalCacheDir() + "/outputImage.jpg";
        File outputImage = new File(getExternalCacheDir(), "outputImage.jpg");
        try {
            if(outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(SharedPicture.this,
                    "com.example.qrhunter.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode==1) && (resultCode==RESULT_OK) && (data!=null)) {
            try {
                Log.e(TAG, "onActivityResult: " + filePath );
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                ImageView imageView = findViewById(R.id.imgQrcode);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void saveScreenShot(Bitmap bitmap)  {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        //以保存时间为文件名
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        String image_save_path =  sdf.format(date);

        File file = new File(extStorageDirectory, image_save_path+".jpg");//创建文件，第一个参数为路径，第二个参数为文件名

        try {
            outStream = new FileOutputStream(file);//创建输入流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            filePath = extStorageDirectory + image_save_path+".jpg";
//          这三行可以实现相册更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            sendBroadcast(intent);
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveScreenShot: " + "Take photo error!" );
            Toast.makeText(SharedPicture.this, "exception:" + e, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * resize the photo
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap resizeImage(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        int inSampleSize = 1;
        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }

        int expectedWidth = width / inSampleSize;
        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Log.d(TAG, "resizeImage: " + options.outHeight + "," + options.outWidth);
        return BitmapFactory.decodeFile(path, options);
    }



}




