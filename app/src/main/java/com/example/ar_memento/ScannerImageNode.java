package com.example.ar_memento;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"AndroidApiChecker"})
class ScannerImageNode extends AnchorNode {
    // We use completable futures here to simplify
    // the error handling and asynchronous loading.  The loading is started with the
    // first construction of an instance, and then used when the image is set.
    private CompletableFuture<ModelRenderable> selected_CF_Mr;
    private String[] models = {"Thick marker.sfb", "tree01.sfb", "Pencil_01.sfb"};
    private final String TAG = "ScannerImageNode";

    ScannerImageNode(Context context, int modelIndex) {
        // TODO: This will have to be dynamic, to display certain AR objects.
        //  Can this be replaced by a callback function that takes in the one image?
        // Upon construction, start loading the model.
        if (selected_CF_Mr == null) {
            selected_CF_Mr= ModelRenderable.builder()
                    .setSource(context, Uri.parse(models[modelIndex]))
                    .build();
        }
    }

    void loadAugmentedImage(Context context, int modelIndex) {
        if (selected_CF_Mr == null) {
            selected_CF_Mr= ModelRenderable.builder()
                    .setSource(context, Uri.parse(models[modelIndex]))
                    .build();
        }
    }

    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    void setARObject(AugmentedImage image, ArFragment arFragment, ViewRenderable scannerInfoCard_Vr, Activity act) {
        String text = "Laptop Loading...";

        // IT IS VERY IMPORTANT THAT THE MODEL IS FIRST LOADED!
            if (!selected_CF_Mr.isDone()) {
            CompletableFuture.allOf(selected_CF_Mr)
                    .thenAccept((Void aVoid) ->
                    {
                        SnackbarHelper.getInstance().showMessage(act, text);
                        setARObject(image, arFragment, scannerInfoCard_Vr, act);
                    })
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
        }
        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        TransformableNode node_T = new TransformableNode(arFragment.getTransformationSystem());
        node_T.setParent(this);
        node_T.setRenderable(selected_CF_Mr.getNow(null));

        Node infoCard = new Node();
        infoCard.setParent(node_T);
        infoCard.setEnabled(false);
        infoCard.setRenderable(scannerInfoCard_Vr);
        TextView textView = (TextView) scannerInfoCard_Vr.getView();

        textView.setText(models[image.getIndex()]);
        infoCard.setLocalPosition(new Vector3(0.0f, 0.20f, 0.0f));
        node_T.setOnTapListener(
                (hitTestResult, motionEvent) -> infoCard.setEnabled(!infoCard.isEnabled()));
    }

}
