package com.example.ar_memento;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private Uri selectedObject;

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
//        if (!checkIsSupportedDeviceOrFinish(this)) {
//            return;
//        }

        setContentView(R.layout.activity_artest);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        InitializeAssetsMenu();

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING){
                        return;
                    }

                    Anchor anchor = hitResult.createAnchor();

                    placeObject(arFragment, anchor, selectedObject);

                }
        );


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

    private void InitializeAssetsMenu(){
        LinearLayout gallery = findViewById(R.id.asset_layout);

        ImageView pencil = new ImageView(this);
        pencil.setImageResource(R.drawable.pencil_thumb);
        pencil.setContentDescription("pencil");
        pencil.setOnClickListener(view -> {selectedObject = Uri.parse("Pencil_01.sfb");});
        gallery.addView(pencil);

        ImageView eraser = new ImageView(this);
        eraser.setImageResource(R.drawable.eraser_thumb);
        eraser.setContentDescription("eraser");
        eraser.setOnClickListener(view -> {selectedObject = Uri.parse("Eraser_01(1).sfb");});
        gallery.addView(eraser);

        ImageView laptop = new ImageView(this);
        laptop.setImageResource(R.drawable.laptop_thumb);
        laptop.setContentDescription("laptop");
        laptop.setOnClickListener(view -> {selectedObject = Uri.parse("Laptop_01.sfb");});
        gallery.addView(laptop);

        ImageView notebook = new ImageView(this);
        notebook.setImageResource(R.drawable.notebook_thumb);
        notebook.setContentDescription("notebook");
        notebook.setOnClickListener(view -> {selectedObject = Uri.parse("Notebook.sfb");});
        gallery.addView(notebook);
    }

    private void placeObject(ArFragment arFragment, Anchor anchor, Uri model){
        ModelRenderable.builder()
                .setSource(arFragment.getContext(), model)
                .build()
                .thenAccept(renderable -> addNodeToScene(arFragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));

    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable){
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

}
