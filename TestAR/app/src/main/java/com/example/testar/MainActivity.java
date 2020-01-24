package com.example.testar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment arfrag;

    Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnNext=findViewById(R.id.nextActivity);
        btnNext.setOnClickListener(v -> {

            Intent  next=new Intent(MainActivity.this,CustomFragment.class);
            startActivity(next);

        });

        arfrag = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFrag);
        arfrag.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor=hitResult.createAnchor();
            ModelRenderable.builder()
                    .setSource(this, Uri.parse("Fox.sfb"))
                    .build()
                    .thenAccept((modelRenderable -> addModelToScene(anchor,modelRenderable)))
                    .exceptionally(throwable -> {
                        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                        Log.d("Ar version Problem",throwable.getMessage());
                        return null;
                    });
        });
    }

    private  void addModelToScene(Anchor anchor,ModelRenderable modelRenderable){

        AnchorNode anchorNode=new AnchorNode(anchor);
        TransformableNode transformableNode=new TransformableNode(arfrag.getTransformationSystem());

//        transformableNode.getScaleController().setMaxScale(0.02f);
//        transformableNode.getScaleController().setMinScale(0.01f);
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arfrag.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();


    }
}
