package com.example.ar_memento;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ScannerActivity extends AppCompatActivity {

    //CompletableFuture<ViewRenderable> infoCardStage;
    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final Map<AugmentedImage, ScannerImageNode> augmentedImageMap = new HashMap<>();
    private ArFragment arFragment;
    private ImageView fitToScanView;
    private ViewRenderable scannerInfoCard_Vr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.scanner_fragment);


        // Overlay, img view, that prompts user to fit the image they are scanning.
        fitToScanView = findViewById(R.id.image_view_fit_to_scan);

        // Enables ar session to update frames to make ar session work. Move up?
        arFragment.getArSceneView()
                .getScene()
                .addOnUpdateListener(this::onUpdateFrame);

        // REVIEW: Can this be moved to ScannerImageNode?
        // Build the view renderable that will be passed to setImage().
        ViewRenderable.builder()
                .setView(this, R.layout.scanner_card_view)
                .build()
                .thenAccept(finishedVr -> scannerInfoCard_Vr = finishedVr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If Map is empty, show fitToScan to fill Map.
        if (augmentedImageMap.isEmpty()) {
            fitToScanView.setVisibility(View.VISIBLE);
        }
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
                    String status = "Detected Image " + augmentedImage.getIndex();
                    SnackbarHelper.getInstance().showMessage(this, status);
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View.
                    fitToScanView.setVisibility(View.GONE);

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        ScannerImageNode node = new ScannerImageNode(this);
                        // TODO: scannerInfoCard_Vr is hardcoded.
                        //  will need to be dynamic!
                        node.setImage(augmentedImage, arFragment, scannerInfoCard_Vr, this);
                        augmentedImageMap.put(augmentedImage, node);
                        arFragment.getArSceneView().getScene().addChild(node);
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }
}
