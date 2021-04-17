package com.rohita.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    CameraSource mCameraSource;
    SurfaceView mCameraView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageView;
    LinearLayout Gallery;
    private Uri filePath;
    TextView textView;
    LinearLayout Camera;
    ImageButton Copy;
    boolean check = false;
    private final int PICK_IMAGE_REQUEST = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Gallery = findViewById(R.id.gallery);
        textView = findViewById(R.id.text_view);
        Camera = findViewById(R.id.take_photo);
        imageView = findViewById(R.id.image_view);
        Copy = findViewById(R.id.copy_txt);
        Copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", textView.getText());
                manager.setPrimaryClip(clipData);

                Toast.makeText(getApplicationContext(),"The Text is copied in ClipBoard",Toast.LENGTH_LONG).show();
            }
        });
        Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
                SelectImage();
                detect();

            }
        });
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = false;
                dispatchTakePictureIntent();
                detect();

            }
        });

    }

    private void detect() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> sparseArray = textRecognizer.detect(frame);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<sparseArray.size();i++)
        {
            TextBlock tx = sparseArray.get(i);
            String str = tx.getValue();
            stringBuilder.append(str);
            stringBuilder.append("\n");

        }
        textView.setText(stringBuilder.toString() );

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, PICK_IMAGE_REQUEST);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }


    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);


    }

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(check)
        {
            super.onActivityResult(requestCode,
                    resultCode,
                    data);

            // checking request code and result code
            // if request code is PICK_IMAGE_REQUEST and
            // resultCode is RESULT_OK
            // then set image in the image view
            if (requestCode == PICK_IMAGE_REQUEST
                    && resultCode == Activity.RESULT_OK
                    && data != null
                    && data.getData() != null) {

                // Get the Uri of data
                filePath = data.getData();
                try {
                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    this.getApplicationContext().getContentResolver(),
                                    filePath);
                    imageView.setImageBitmap(bitmap);
                    detect();
                }

                catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                detect();

            }
        }

}






}