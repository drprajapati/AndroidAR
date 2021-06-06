package com.example.androidar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;

    private int clickNo = 0;

    public static boolean checkSystem(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String openGlVersion = ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))).getDeviceConfigurationInfo().getGlEsVersion();

            if (Double.parseDouble(openGlVersion) >= 3.0) {
                return true;
            } else {
                Toast.makeText(activity, "App needs OpenGl Version 3.0 or later", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            }
        } else {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSystem(this)) {

            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            Objects.requireNonNull(arFragment).setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                clickNo++;
                if (clickNo == 1) {
                    Anchor anchor = hitResult.createAnchor();
                    ModelRenderable.builder()
                            .setSource(this, R.raw.gladiador)
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept(modelRenderable -> add(anchor, modelRenderable))
                            .exceptionally(throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Somthing is not right" + throwable.getMessage()).show();
                                return null;
                            });
                }
            });
        }
    }

    private void add(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
        model.setParent(anchorNode);
        model.setRenderable(modelRenderable);
        model.select();
    }
}