package com.example.ar_memento;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable bookRenderable;
    private ModelRenderable deskRenderable;
    private Node infoCard;
    //private final Context context;
    Context context;
    private static final float INFO_CARD_Y_POS_COEFF = 0.55f;

    // True once scene is loaded
    private boolean hasFinishedLoading = false;

    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Uncomment if the layout set is activity_camera
//        // The proceeding section sets the toolbar back arrow to home screen.
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        // Display the back arrow to home screen.
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_artest);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        /*CompletableFuture<ModelRenderable> bookStage =
                ModelRenderable.builder().setSource(this, Uri.parse("model.sfb")).build();

        CompletableFuture.allOf(
                bookStage)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderable", throwable);
                                return null;
                            }

                            try {
                                bookRenderable = bookStage.get();


                                // Everything finished loading successfully.
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderable", ex);
                            }

                            return null;
                        }); */

        ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(renderable -> bookRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast toast = Toast.makeText(this, "Unable to load book renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });
        ModelRenderable.builder()
                .setSource(this, Uri.parse("Desk.sfb"))
                .build()
                .thenAccept(renderable -> deskRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast toast = Toast.makeText(this, "Unable to load desk renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 150, 0);
                    toast.show();
                    return null;
                });

        /*private Node createObject(
                String name,
                Node parent,
                ModelRenderable renderable) {

            return ;
        }
        createObject("Mercury", sun, 0.4f, 47f, bookRenderable, 0.019f, 0.03f); */

        arFragment.setOnTapArPlaneListener(
                (hitResult, plane, motionEvent) -> {
                    if (bookRenderable == null || deskRenderable == null) {
                        return;
                    }
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    //having another anchor, specifically for the other object, allows us
                    //to be able to move these objects separately.
                    //when we have lots of objects, we are gonna have to implement a for loop
                    //to be able to make many anchors for each object.
                    Anchor anchor2 = hitResult.createAnchor();
                    AnchorNode anchorNode2 = new AnchorNode(anchor2);
                    anchorNode2.setParent(arFragment.getArSceneView().getScene());

                    //TransformableNode book = new TransformableNode(arFragment.getTransformationSystem());
                    Node book= new Node();
                    book.setParent(anchorNode);
                    book.setRenderable(bookRenderable);
                    //book.select();
                    infoCard = new Node();
                    infoCard.setParent(anchorNode);
                    infoCard.setEnabled(false);
                    book.setOnTapListener(
                            (hitTestResult, motionEvento) -> infoCard.setEnabled(!infoCard.isEnabled()));
                    //infoCard.setLocalPosition(new Vector3(1.0f, 1.0f * INFO_CARD_Y_POS_COEFF, 1.0f));
                    infoCard.setLocalPosition(new Vector3(0.0f,0.25f,0.0f));
                    ViewRenderable.builder()
                            .setView(context, R.layout.card_view)//took out context and put in 'this'
                            .build()
                            .thenAccept(
                                    (renderable) -> {
                                        infoCard.setRenderable(renderable);
                                        TextView textView = (TextView) renderable.getView();
                                        // textView.setText(planetName);
                                        textView.setText("randomText");
                                    })
                            .exceptionally(
                                    (throwable) -> {
                                        throw new AssertionError("Could not load plane card view.", throwable);
                                    });

                    /*TransformableNode desk = new TransformableNode(arFragment.getTransformationSystem());
                    desk.setParent(anchorNode2);
                    desk.setRenderable(deskRenderable);
                    desk.select();*/
                });
    }

    // What proceeds here are just some compatibility checks.
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

}
