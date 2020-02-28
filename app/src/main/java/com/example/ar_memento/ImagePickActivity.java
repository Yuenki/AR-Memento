package com.example.ar_memento;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

public class ImagePickActivity extends AppCompatActivity {
    ImageView mImageView;
    Button mChooseBtn;
    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pick);

        //Toast.makeText(this, "Permisison Denied", Toast.LENGTH_SHORT).show();


       //Views
        mImageView=findViewById(R.id.image_view);
        mChooseBtn = findViewById(R.id.choose_image_btn);

        //handle button click
        mChooseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission denied; needs permission
                        String [] permisison= {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        };
                        //pop up shows up for permisison
                        requestPermissions(permisison,PERMISSION_CODE);
                    }
                    else {
                        //permission granted
                        pickImageFromGallery();
                    }
                }
                else {
                    //if System OS is less than marshmallow
                    pickImageFromGallery();
                }

            }
        });
    }

    private void pickImageFromGallery() {
        //user picks image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    //handling of the result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    pickImageFromGallery();
                }
                else {
                    //permision denied
                    Toast.makeText(this, "Permisison Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //handles the result of pick image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
            mImageView.setImageURI(data.getData());
        }
    }

}