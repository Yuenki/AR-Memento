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
import java.util.concurrent.ExecutionException;

@SuppressWarnings({"AndroidApiChecker"})
public class ScannerImageNode extends AnchorNode {
    private static final String TAG = "ScannerImageNode";
    private ViewRenderable cardRenderable;
    // The augmented image represented by this node.
    private AugmentedImage image;
    // We use completable futures here to simplify
    // the error handling and asynchronous loading.  The loading is started with the
    // first construction of an instance, and then used when the image is set.
    private static CompletableFuture<ModelRenderable> laptop;
    String objectName = "test";

    public ScannerImageNode(Context context) {
        // Upon construction, start loading the model.
        // TODO: This will have to be dynamic, to display certain AR objects.
        if (laptop== null) {
            laptop= ModelRenderable.builder()
                    .setSource(context, Uri.parse("Laptop_01.sfb"))
                    .build();
        }
    }

    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void setImage(AugmentedImage image, ArFragment arFragment, Context ctx, CompletableFuture<ViewRenderable> infoCardStage) {
        this.image = image;

        // IT IS VERY IMPORTANT THAT THE MODELS ARE FIRST LOADED!
        // TODO: This will have to be dynamic, to display certain AR objects.
        if (!laptop.isDone() || infoCardStage.isDone()) {
            CompletableFuture.allOf(laptop)
                    .thenAccept((Void aVoid) -> setImage(image, arFragment,ctx, infoCardStage))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
        }
        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        CompletableFuture.allOf(infoCardStage).handle((notUsed, throwable) -> {
                            try {
                                cardRenderable= infoCardStage.get();
                            }catch (InterruptedException | ExecutionException ex) {

                                Log.e(TAG,"error in cardRenderable=infoCardStage.get()",ex);
                        }
                            return null;
                        }
                );
//        Node node;
        TransformableNode tNode = new TransformableNode(arFragment.getTransformationSystem());
//        node = new Node();
        tNode.setParent(this);
        // TODO: This will have to be dynamic, to display certain AR objects.
        tNode.setRenderable(laptop.getNow(null));


        Node infoCard = new Node();
        infoCard.setParent(tNode);
        infoCard.setEnabled(true);
        infoCard.setRenderable(cardRenderable);
        TextView textView = (TextView)cardRenderable.getView();
        textView.setText(this.objectName);
        infoCard.setLocalPosition(new Vector3(0.0f, 0.25f, 0.0f));
        tNode.setOnTapListener(
                (hitTestResult, motionEvent) -> infoCard.setEnabled(!infoCard.isEnabled()));
    }
}
