package com.example.armap.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.armap.Model.MapData;
import com.example.armap.Model.VectorMarker;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

public class CreateDataPoints {

    public static void CreatePoint(Anchor anchor, DBopearations dBopearations, CustomFragmentConfig arFragment, Context appContext, int layoutResID, int txtdatapointId, String customPointName){

        ViewRenderable.builder()
                .setView(appContext, layoutResID)
                .build()
                .thenAccept(renderable -> {
                    TextView textView=renderable.getView().findViewById(txtdatapointId);

                    if(customPointName==null || customPointName.isEmpty() || customPointName.trim().length()==0){
                       return;
                    }else{
                        textView.setText(customPointName);
                    }

                    Log.d("AddElementToScreen","Calling");
                    AddElementToScreen(renderable,anchor,textView,arFragment,dBopearations);

                }).exceptionally(throwable -> {
            Log.d("AddElementToScreen",throwable.getMessage());
            Toast.makeText(appContext,throwable.getMessage(),Toast.LENGTH_LONG).show();
            return null;
        });


    }

    public static void AddElementToScreen(Renderable renderable, Anchor anchor, TextView tv, CustomFragmentConfig arFragment, DBopearations dBopearations){


        AnchorNode anchorNode = new  AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.getScaleController().setMinScale(0.5f);
        node.getScaleController().setMaxScale(3.0f);

        node.setLocalScale(new Vector3(2.25f, 2.25f, 2.25f));
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        node.select();
        node.setName(tv.getText().toString());
        Vector3 pos= node.getWorldPosition();
     //   dBopearations.SaveVectorPosition(new VectorMarker(tv.getText().toString(),pos,"Camera"));


        //anchorPoses.add(pos);
        // Attaching Tap listener on Node on Ar Scene
        node.setOnTapListener((hitTestResult, motionEvent) -> {
            Node removeNode=hitTestResult.getNode();

            try{
                //  Vector3 position=removeNode.getWorldPosition();
//                int index= Integer.parseInt(tv.getText().toString());
//                currentMapData.removeAnchorPos(index-1);
                //anchorPoses.remove(index-1);
               // anchorNode.removeChild(removeNode);     // Removing selected Node from AR scene

            }catch (Exception e){

            }

        });


        // Toast.makeText(getApplicationContext(), anchor.getPose().getXAxis()+"",Toast.LENGTH_LONG).show();
        //anchors.add(anchor);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }
}


