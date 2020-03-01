package com.example.ar_memento;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.ScientificNumberFormatter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AddImageFragment extends Fragment {
    private ImageView mImageView;
    private final String TAG = "armemento: AddImageFragment.java";
    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_image,
                container, false);

        mImageView = view.findViewById(R.id.image_view);
        Button mChooseBtn = view.findViewById(R.id.choose_image_btn);

        mChooseBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    Objects.requireNonNull(getActivity()),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                //permission denied; needs permission
                String [] permisison= {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };
                //pop up shows up for permisison
                requestPermissions(permisison,PERMISSION_CODE);
            } else {
                //permission granted
                chooseImageToAdd();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                chooseImageToAdd();
            } else {
                Toast.makeText(mImageView.getContext(),
                        "Permisison Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void chooseImageToAdd() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri uriImage= Objects.requireNonNull(data).getData();

            mImageView.setImageURI(uriImage);
            ScannerARFragment sARf = new ScannerARFragment();
            if (!sARf.updateAugmentedImageDatabase(uriImage, getContext())
            ) {
                Log.d(TAG, "updatingAugmentedImageDatabase returned false");
            }

        }
    }
}
