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
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable bookRenderable;
    private ModelRenderable deskRenderable;

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
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });
        arFragment.setOnTapArPlaneListener(
                (hitResult, plane, motionEvent) -> {
                    if (bookRenderable == null || deskRenderable == null) {
                        return;
                    }
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    TransformableNode book = new TransformableNode(arFragment.getTransformationSystem());
                    book.setParent(anchorNode);
                    book.setRenderable(bookRenderable);
                    book.select();

                    TransformableNode desk = new TransformableNode(arFragment.getTransformationSystem());
                    desk.setParent(anchorNode);
                    desk.setRenderable(deskRenderable);
                    desk.select();
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
