package com.example.customar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity  implements Scene.OnUpdateListener {

    private  CustomARFragment CFrag;
    private  Anchor anchor;
    private enum  AnchorState{
        NONE,
        HOSTING,
        HOSTED
    }

    private  AnchorState currentState=AnchorState.NONE;
    private  boolean ishosted=false;


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Button GetAnchorBtn,BtnModel,BtnAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs=getSharedPreferences("AnchorId",MODE_PRIVATE);
        editor=prefs.edit();
        GetAnchorBtn=findViewById(R.id.getAnchor);
        BtnModel=findViewById(R.id.btnmodels);
        BtnAnimation=findViewById(R.id.btnAnimationActivity);
        CFrag= (CustomARFragment) getSupportFragmentManager().findFragmentById(R.id.customArFrag); // attaching custom XML fragment to java code
        CFrag.getArSceneView().getScene().addOnUpdateListener(this);  // attaching update scene listener to current activity
        CFrag.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            if(!ishosted){
                anchor=CFrag.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                currentState=AnchorState.HOSTING;
                createCloudAnchorModel(anchor);
                ishosted=true;
            }
        });
        GetAnchorBtn.setOnClickListener(v -> {
            SetCloudAnchor();

        });

        BtnModel.setOnClickListener(v -> {
            Intent custom3dModel=new Intent(this,Runtime_Model.class);
            startActivity(custom3dModel);

        });

        BtnAnimation.setOnClickListener(v -> {
            Intent Animation_Act=new Intent(this,AnimationAR.class);
            startActivity(Animation_Act);

        });



    }

    private void SetCloudAnchor() {
     String anchorid= prefs.getString("anchorid","null");
        if(anchorid.equals("null")) {
            Toast.makeText(getApplicationContext(),"No Anchor Found",Toast.LENGTH_LONG).show();
            return;
        }

       Anchor cloudAnchor= CFrag.getArSceneView().getSession().resolveCloudAnchor(anchorid);
        createCloudAnchorModel(cloudAnchor);

    }

    private void createCloudAnchorModel(Anchor anchor) {

        ModelRenderable.builder()
                .setSource(this,Uri.parse("stopSign.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeModel(modelRenderable,anchor))
                .exceptionally(throwable -> {
                    Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                    return null;
                });

    }

    /**
     * attaching images to
     * */
    public  void  setupDatabase(Config config, Session session){

        Bitmap foxBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.foxim );
        Bitmap wallBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.wall );
        AugmentedImageDatabase AugDB=new AugmentedImageDatabase(session);
        AugDB.addImage("fox",foxBitmap);
         AugDB.addImage("wall",wallBitmap);
        config.setAugmentedImageDatabase(AugDB);


    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        Frame frame=CFrag.getArSceneView().getArFrame();
       // Toast.makeText(getApplicationContext(),"Update called",Toast.LENGTH_LONG).show();
        Collection<AugmentedImage> images= frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage image : images){
            if(image.getTrackingState()== TrackingState.TRACKING){
                Toast.makeText(getApplicationContext(),image.getName(),Toast.LENGTH_LONG).show();
                if(image.getName().equals("fox")){
                    Anchor anchor=image.createAnchor(image.getCenterPose());
                    Toast.makeText(getApplicationContext(),"Fox found",Toast.LENGTH_LONG).show();
                    createModel(anchor);
                }
                if(image.getName().equals("wall")){
                    Anchor anchor=image.createAnchor(image.getCenterPose());
                    Toast.makeText(getApplicationContext(),"Wall found",Toast.LENGTH_LONG).show();
                    createArrowModel(anchor);
                }
            }
        }

        if(currentState != AnchorState.HOSTING) return ;

        Anchor.CloudAnchorState cloudAnchorState =anchor.getCloudAnchorState();
        if(cloudAnchorState.isError()){
            Toast.makeText(getApplicationContext(),cloudAnchorState.toString(),Toast.LENGTH_LONG).show();
        } else if(cloudAnchorState==Anchor.CloudAnchorState.SUCCESS){
            currentState=AnchorState.HOSTED;

            String anchorId= anchor.getCloudAnchorId();
            editor.putString("anchorid",anchorId);
            editor.apply();
            Toast.makeText(getApplicationContext(),"Cloud Anchor Created ",Toast.LENGTH_LONG).show();


        }


    }

    private void createModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("Fox.sfb"))
                .build()
                .thenAccept(modelRenderable  -> placeModel(modelRenderable,anchor));
    }

    private void createArrowModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable  -> placeModel(modelRenderable,anchor));
    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {
        AnchorNode anchorNode=new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        CFrag.getArSceneView().getScene().addChild(anchorNode);
    }
}
