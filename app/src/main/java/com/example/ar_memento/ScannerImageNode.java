package com.example.ar_memento;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"AndroidApiChecker"})
public class ScannerImageNode extends AnchorNode {
    private static final String TAG = "ScannerImageNode";
    // The augmented image represented by this node.
    private AugmentedImage image;
    // We use completable futures here to simplify
    // the error handling and asynchronous loading.  The loading is started with the
    // first construction of an instance, and then used when the image is set.
    private static CompletableFuture<ModelRenderable> laptop;

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
    public void setImage(AugmentedImage image) {
        this.image = image;

        // IT IS VERY IMPORTANT THAT THE MODELS ARE FIRST LOADED!
        // TODO: This will have to be dynamic, to display certain AR objects.
        if (!laptop.isDone()) {
            CompletableFuture.allOf(laptop)
                    .thenAccept((Void aVoid) -> setImage(image))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
        }
        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        Node node;
        node = new Node();
        node.setParent(this);
        // TODO: This will have to be dynamic, to display certain AR objects.
        node.setRenderable(laptop.getNow(null));
    }
}
