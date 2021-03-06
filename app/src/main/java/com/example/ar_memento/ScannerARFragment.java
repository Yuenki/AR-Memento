package com.example.ar_memento;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

// This is not an activity because we are not going to set view here!
// This is to facilitate staging the scanner.
public class ScannerARFragment extends ArFragment {
    private static final String TAG = "armemento: ScannerARFragment.java";
    private static final String SAMPLE_IMGDB = "imgdb/myimages.imgdb";
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static AugmentedImageDatabase augmentedImageDatabase;

    // onAttach attaches activity to Fragment.
    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "calledOnAttach");
        super.onAttach(context);

        String openGlVersionString =
                ((ActivityManager) Objects.requireNonNull(context.getSystemService(Context.ACTIVITY_SERVICE)))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 or later");
        }
    }

    // Needs to return root view to Android OS, or such core code.
    // Returned view needed to create view hierarchy associated with fragment.
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "calledOnCreateView");
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Turn off the plane discovery since we're only looking for images
        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);
        getArSceneView().getPlaneRenderer().setEnabled(false);
        return view;
    }

    // A session config is required to set the ArImgDB to the current session.
    @Override
    protected Config getSessionConfiguration(Session session) {
        Log.d(TAG, "called getSessionConfiguration");
        Config config = super.getSessionConfiguration(session);
        if (!setupAugmentedImageDatabase(config, session)) {
            SnackbarHelper.getInstance()
                    .showError(getActivity(), "Could not setup augmented image database");
        }
        Log.d(TAG, "finished getSessionConfiguration");
        return config;
    }

    // The above methods seem to get called each time we trigger scannerActivity.

    private boolean setupAugmentedImageDatabase(Config config, Session session) {

        // requireNonNull() Checks that the specified object reference is not null.
        // Designed primarily for doing parameter validation.

        try (InputStream is = Objects.requireNonNull(getContext()).getAssets().open(SAMPLE_IMGDB)) {
            augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, is);
        } catch (IOException e) {
            Log.e(TAG, "IO exception loading augmented image database.", e);
            return false;
        }

        // It's not enough to just 'load' the ArImgDB. You have to SET it too!
        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }

    // TODO: When and where should this be called?
    boolean updateAugmentedImageDatabase(Uri uri, Context context) {
        Log.d(TAG, "Starting updateAugmentedImageDatabase");
        // REVIEW: Performing getContext() in this method while this method is called
        //  from another class returned NULL. Why?
        // This issue was resolved by passing the context from where this method
        //  was called.
        Bitmap bitmap ;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
        } catch (IOException e) {
            Log.e(TAG, "I/O exception loading augmented image bitmap.", e);
            return false;
        }
        Log.d(TAG, "Finished getting bitmap");

        // addImage method set on working thread to not stall/'hang' UI thread.
        new Thread(() -> augmentedImageDatabase.addImage(uri.toString(),
                Objects.requireNonNull(bitmap)));

        Log.d(TAG, "Finished addImage(uri, bitmap)");
        return true;
    }

}
