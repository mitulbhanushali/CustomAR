package com.example.testar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;


import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.Collection;

public class CustomFragment extends AppCompatActivity  implements Scene.OnUpdateListener {

    private  CustomARFragment CFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_fragment);
        CFrag= (CustomARFragment) getSupportFragmentManager().findFragmentById(R.id.customArFrag);



    }


    public  void  setupDatabase(Config config, Session session){

        Bitmap foxBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.foxim );
        AugmentedImageDatabase AugDB=new AugmentedImageDatabase(session);
        AugDB.addImage("fox",foxBitmap);
        config.setAugmentedImageDatabase(AugDB);


    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        Frame frame=CFrag.getArSceneView().getArFrame();
        Collection<AugmentedImage> images= frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage image : images){
            if(image.getTrackingState()== TrackingState.TRACKING){
                if(image.getName().equals("fox")){
                    Anchor anchor=image.createAnchor(image.getCenterPose());

                    createModel(anchor);
                }
            }
        }
    }

    private void createModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this,Uri.parse("Fox.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeModel(modelRenderable,anchor));
    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {
        AnchorNode anchorNode=new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        CFrag.getArSceneView().getScene().addChild(anchorNode);
    }
}
