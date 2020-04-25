package com.example.ar_memento;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ar_memento.databinding.ActivityScannerBinding;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScannerActivity extends AppCompatActivity {

    //CompletableFuture<ViewRenderable> infoCardStage;
    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final static String TAG = "armemento: ScannerActivity.java";
    private final Map<AugmentedImage, ScannerImageNode> augmentedImageMap = new HashMap<>();
    private ArFragment arFragment;
    private ImageView Iv_fitToScan;
    private Button btn_addImage;
    private ViewRenderable Vr_scannerInfoCard;
    private ScannerImageNode node_SI;
    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//    private ActivityScannerBinding scannerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityScannerBinding scannerBinding;

        scannerBinding = ActivityScannerBinding.inflate(getLayoutInflater());
        View view = scannerBinding.getRoot();
        Log.d(TAG, "starting setContentView");
        setContentView(view);
        Log.d(TAG, "finishing setContentView");

        arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.scanner_fragment);

        // Overlay, img view, that prompts user to fit the image they are scanning.
        Iv_fitToScan = scannerBinding.IMGVFitToScan;
        btn_addImage = scannerBinding.BTNAddImage;

        Objects.requireNonNull(arFragment).getArSceneView()
                .getScene()
                .addOnUpdateListener(this::onUpdateFrame);

        // REVIEW: Can this not take any arguments?
        node_SI = new ScannerImageNode(this, 0);

        // REVIEW: Can this be moved to ScannerImageNode? No.
        // Build the view renderable that will be passed to setARObject().
        ViewRenderable.builder()
                .setView(this, R.layout.scanner_card_view)
                .build()
                .thenAccept(finishedVr -> Vr_scannerInfoCard = finishedVr);
        Log.d(TAG, "Finished onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Started onResume");
        // If Map is empty, show fitToScan to fill Map.
        if (augmentedImageMap.isEmpty()) {
            Iv_fitToScan.setVisibility(View.VISIBLE);
//            SnackbarHelper.getInstance().showMessage(this, "augmentedImageMap is empty!");
        }
        btn_addImage.setOnClickListener(addImage -> {
            Fragment frag_addImage = new AddImageFragment();
            transaction.add(R.id.CONTAINER_add_image, frag_addImage)
                    .addToBackStack(null)
                    .commit();
        });
        Log.d(TAG, "Finished onResume");
    }

    // REVIEW: Can I delete Frametime parameter?
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                // TODO: Snackbar will need to be removed from PAUSED case.
                //  It's current purpose is to debug.
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
//                    String status = "Detected Image " + augmentedImage.getIndex();
//                    SnackbarHelper.getInstance().showMessage(this, status);
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View.
                    Iv_fitToScan.setVisibility(View.GONE);

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        node_SI.loadAugmentedImage(this, augmentedImage.getIndex());
                        node_SI.setARObject(augmentedImage, arFragment, Vr_scannerInfoCard, this);
                        augmentedImageMap.put(augmentedImage, node_SI);
                        arFragment.getArSceneView().getScene().addChild(node_SI);
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }
}
