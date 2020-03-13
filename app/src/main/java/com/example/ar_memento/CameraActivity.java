package com.example.ar_memento;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
//    private static final String TAG = CameraActivity.class.getSimpleName();
//    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private Uri selectedObject;
    private ModelRenderable laptopRenderable;
    private ViewRenderable cardRenderable;
    private GestureDetector gestureDetector;
    private ArSceneView arSceneView;
    private String objectName;
    Vector<Anchor> arranchors = new Vector<>();
    Vector<TransformableNode> arrnode = new Vector<>();
    Vector<AnchorNode> arranchornode = new Vector<>();

    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_artest);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        InitializeAssetsMenu();

        CompletableFuture<ViewRenderable> solarControlsStage =
                ViewRenderable.builder().setView(this, R.layout.card_view).build();

        CompletableFuture.allOf(
                solarControlsStage)
                .handle(
                        (notUsed, throwable) -> {
                            try {
                                cardRenderable= solarControlsStage.get();
                            }catch (InterruptedException | ExecutionException ex) {

                            }
                            return null;
                        }
                );

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING){
                        return;
                    }
                    Anchor anchor = hitResult.createAnchor();
                    arranchors.addElement(anchor);

                    placeObject(arFragment, arranchors.lastElement(), selectedObject);
                    //placeObject(arFragment, anchor, selectedObject); //selectedObject is the renderable (like laptopRenderable)

                    if(arranchors.size() >1)
                    {
                        spreadapart();
                    }
                }
        );
    }

    // What proceeds here are just some compatibility checks.
//    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            Log.e(TAG, "Sceneform requires Android N or later");
//            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
//            activity.finish();
//            return false;
//        }
//        String openGlVersionString =
//                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
//                        .getDeviceConfigurationInfo()
//                        .getGlEsVersion();
//        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
//            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
//            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
//                    .show();
//            activity.finish();
//            return false;
//        }
//        return true;
//    }

    private void InitializeAssetsMenu(){
        LinearLayout gallery = findViewById(R.id.asset_layout);

        ImageView pencil = new ImageView(this);
        pencil.setImageResource(R.drawable.pencil_thumb);
        pencil.setContentDescription("pencil");
        pencil.setOnClickListener(view -> {selectedObject = Uri.parse("Pencil_01.sfb");objectName= "Pencil";});
        gallery.addView(pencil);

        ImageView eraser = new ImageView(this);
        eraser.setImageResource(R.drawable.eraser_thumb);
        eraser.setContentDescription("eraser");
        eraser.setOnClickListener(view -> {selectedObject = Uri.parse("Eraser_01(1).sfb");objectName="Eraser";});
        gallery.addView(eraser);

        ImageView laptop = new ImageView(this);
        laptop.setImageResource(R.drawable.laptop_thumb);
        laptop.setContentDescription("laptop");
        laptop.setOnClickListener(view -> {selectedObject = Uri.parse("Laptop_01.sfb");objectName="Laptop";});
        gallery.addView(laptop);

        ImageView notebook = new ImageView(this);
        notebook.setImageResource(R.drawable.notebook_thumb);
        notebook.setContentDescription("notebook");
        notebook.setOnClickListener(view -> {selectedObject = Uri.parse("Notebook.sfb");objectName="Notebook";});
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
        //node.setLocalPosition(new Vector3(0.1f, 0.0f, 0.0f));
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
        arrnode.addElement(node); //keeps track of the transformable nodes
        arranchornode.addElement(anchorNode); //keeps track of the anchor nodes
        createInfoCard(node);
        //spreadapart();
    }
    private Node createInfoCard(TransformableNode parent){
        Node infoCard = new Node();
        infoCard.setParent(parent);
        infoCard.setEnabled(true);
        infoCard.setRenderable(cardRenderable);
        TextView textView = (TextView)cardRenderable.getView();
        textView.setText(this.objectName);
        infoCard.setLocalPosition(new Vector3(0.0f, 0.25f, 0.0f));
        parent.setOnTapListener(
                (hitTestResult, motionEvent) -> {
                    infoCard.setEnabled(!infoCard.isEnabled());
                    spreadapart();
                });

        return infoCard;
    }
    void spreadapart()
    {
        //here we will spread apart our objects
        if(arrnode.size() ==2){
            double dist= getDistanceMeters(arranchors.elementAt(0).getPose(),arranchors.elementAt(1).getPose());
            System.out.printf("\n\ndist is %f",dist);
            if(dist <0.2f){
                arrnode.elementAt(1).setLocalPosition(new Vector3(0.1f,0.0f,0.0f));
                arrnode.elementAt(0).setLocalPosition(new Vector3(-0.1f,0.0f,0.0f));
            }
        }
        if(arrnode.size() ==3){ //first two anchors were already spread apart so only check for last one
            double dist1= getDistanceMeters(arranchors.elementAt(2).getPose(),arranchors.elementAt(0).getPose());
            double dist2= getDistanceMeters(arranchors.elementAt(2).getPose(),arranchors.elementAt(1).getPose());
            if(dist1 <0.2f && dist2 <0.2f){
                arrnode.elementAt(2).setLocalPosition(new Vector3(0.0f,0.0f,-0.2f)); //move it back
            }
            else if(dist1<0.2f && dist2 >0.2f){
                arrnode.elementAt(2).setLocalPosition(new Vector3(0.0f,0.0f,0.2f)); //move it closer to us
            }
            else
                arrnode.elementAt(2).setLocalPosition(new Vector3(0.2f,0.0f,0.0f)); //move it towards the right
        }
        //try to implement a function which returns a location that has enough spaces between itself and the other objects.
        if(arrnode.size() == 4){

        }
        if(arrnode.size() == 5){

        }
    }
    float getMetersBetweenAnchors(Anchor anchor1, Anchor anchor2)
    {
        float[] distance_vector = anchor1.getPose().inverse()
                .compose(anchor2.getPose()).getTranslation();
        float totalDistanceSquared = 0.0f;
        for(int i=0; i<3; ++i)
            totalDistanceSquared += distance_vector[i]*distance_vector[i];
        return (float) Math.sqrt(totalDistanceSquared);
    }
    //have two getdistance functions just to see which one works correctly or better.
    private double getDistanceMeters(Pose pose0, Pose pose1) {

        float distanceX = pose0.tx() - pose1.tx();
        float distanceY = pose0.ty() - pose1.ty();
        float distanceZ = pose0.tz() - pose1.tz();

        return Math.sqrt(distanceX * distanceX +
                distanceY * distanceY +
                distanceZ * distanceZ);
    }
}
