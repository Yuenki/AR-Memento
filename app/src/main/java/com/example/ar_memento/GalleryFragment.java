package com.example.ar_memento;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment {
    ImageView mImageView;
    Button mChooseBtn;
    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;
    public GalleryFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        container.clearDisappearingChildren();
        if(container != null){
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mImageView = view.findViewById(R.id.image_view);
        mChooseBtn=view.findViewById(R.id.choose_image_btn);
        mChooseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
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


//     ImageView imageView = (ImageView) view.findViewById(R.id.my_image);
        return view;
                //inflater.inflate(R.layout.fragment_gallery,container,false);
    }

    private void pickImageFromGallery() {
        //user picks image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    pickImageFromGallery();
                }
                else {
                    //permision denied
                    Toast.makeText(mImageView.getContext(), "Permisison Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
            mImageView.setImageURI(data.getData());
        }
    }
}
