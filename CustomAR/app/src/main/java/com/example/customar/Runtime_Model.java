package com.example.customar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Runtime_Model extends AppCompatActivity {


    private ArFragment arFragment;

    Button btnCube,btnSphere;

    ArrayList<Vector3> anchorPoses;

    private enum ShapeType{

        CUBE,
        SPHERE
    }

    private ShapeType shapeType=ShapeType.CUBE;

    private FloatingActionButton FabCalDistance;

    private  int ModelPoints=0;

    private ArrayList<Float> allDistance;
    private ArrayList<AnchorNode> allLineNodes;
    private float totalLength=0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runtime__model);
        anchorPoses=new ArrayList<>();
        allLineNodes=new ArrayList<>();
        arFragment= (ArFragment) getSupportFragmentManager().findFragmentById(R.id.runtime_frag);

        btnCube=findViewById(R.id.btncude);
        btnSphere=findViewById(R.id.btnsphere);
        FabCalDistance=findViewById(R.id.calDistance);
        allDistance=new ArrayList<>() ;
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            
            if(shapeType==ShapeType.CUBE){
                placeCude(hitResult.createAnchor());
            }else{
             //   placeSphere(hitResult.createAnchor());
                Rendertext(hitResult.createAnchor());
            }
        });

        // Below line will automatically add object to AR Scene when it detects plane
       // arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);

        btnCube.setOnClickListener(v -> shapeType=ShapeType.CUBE);
        btnSphere.setOnClickListener(v -> shapeType=ShapeType.SPHERE);
        FabCalDistance.setOnClickListener(v -> {

            if(allLineNodes.size()>0){
                for (AnchorNode anchorNode : allLineNodes)
                {
                    removeAnchorNode(anchorNode);
                }
                allLineNodes.clear();
            }
            for (int i=0; i<anchorPoses.size()-1; i++){
                Vector3 start=anchorPoses.get(i);
                Vector3 end = anchorPoses.get(i+1);
                addLineBetweenPoints(arFragment.getArSceneView().getScene(),start,end);
            }

            Toast.makeText(getApplicationContext(),"You need to travel"+totalLength+"meters",Toast.LENGTH_LONG).show();

        });

        

    }

    // This method will be called when frame is Updated
    private void onUpdate(FrameTime frameTime) {

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<Plane> planes=frame.getUpdatedTrackables(Plane.class);

        for (Plane plane : planes){
            if (plane.getTrackingState() == TrackingState.TRACKING){
                Anchor anchor = plane.createAnchor(plane.getCenterPose());
                Rendertext(anchor);
                break;
            }
        }
    }

    private void placeCude(Anchor anchor) {

        MaterialFactory.
                makeOpaqueWithColor(this,new Color(android.graphics.Color.RED))
                .thenAccept(material -> {
                   ModelRenderable modelRenderable= ShapeFactory.makeCube(new Vector3(0.1f,0.1f,0.1f),new Vector3(0f,0.1f,0f),material);
                    placeModel(modelRenderable,anchor);

                });
    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {

        AnchorNode anchorNode= new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private void placeSphere(Anchor anchor) {
        MaterialFactory.makeOpaqueWithColor(this,new Color(android.graphics.Color.BLUE))
                .thenAccept(material -> {
                   ModelRenderable modelRenderable= ShapeFactory.makeSphere(0.1f,new Vector3(0f,0.1f,0f),material);
                    placeModel(modelRenderable,anchor);
                });
    }


    private void Rendertext(Anchor anchor){

        ViewRenderable.builder()
                .setView(this, R.layout.randomtext)
                .build()
                .thenAccept(renderable -> {
                    TextView textView=renderable.getView().findViewById(R.id.planetInfoCard);
                    ModelPoints++;
                    textView.setText(ModelPoints +"");
//                    Button btn=renderable.getView().findViewById(R.id.arbtn);
//                    btn.setOnClickListener(v -> {
//                        Toast.makeText(getApplicationContext(),"You click that button",Toast.LENGTH_LONG).show();
////                        //textView.setText("mitul");
////                        int index= Integer.parseInt(textView.getText().toString());
////                        anchorPoses.remove(index-1);
//                    });

                    AddElementToScreen(renderable,anchor,textView);

                });

    }

    private void AddElementToScreen(Renderable renderable,Anchor anchor,TextView tv){


        AnchorNode anchorNode = new  AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.getScaleController().setMinScale(0.5f);
        node.getScaleController().setMaxScale(3.0f);

        node.setLocalScale(new Vector3(2.25f, 2.25f, 2.25f));
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        node.select();
        Vector3 pos= node.getWorldPosition();
        anchorPoses.add(pos);
        // Attaching Tap listener on Node on Ar Scene
        node.setOnTapListener((hitTestResult, motionEvent) -> {
            Node removeNode=hitTestResult.getNode();

            int index= Integer.parseInt(tv.getText().toString());
            anchorPoses.remove(index-1);
            anchorNode.removeChild(removeNode);     // Removing selected Node from AR scene

        });


       // Toast.makeText(getApplicationContext(), anchor.getPose().getXAxis()+"",Toast.LENGTH_LONG).show();
        //anchors.add(anchor);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }


    private void addLineBetweenPoints(Scene scene, Vector3 from, Vector3 to) {
        // prepare an anchor position
        Quaternion camQ = scene.getCamera().getWorldRotation();
        float[] f1 = new float[]{to.x, to.y, to.z};
        float[] f2 = new float[]{camQ.x, camQ.y, camQ.z, camQ.w};
        Pose anchorPose = new Pose(f1, f2);

        // make an ARCore Anchor
        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(anchorPose);
        // Node that is automatically positioned in world space based on the ARCore Anchor.
        AnchorNode anchorNode = new AnchorNode(anchor);
        allLineNodes.add(anchorNode);
        anchorNode.setParent(scene);

        // Compute a line's length
        float lineLength = Vector3.subtract(from, to).length();
        //
        totalLength+=lineLength;

//        allDistance.add(lineLength);
        // Prepare a color
        Color colorOrange = new Color(android.graphics.Color.parseColor("#ffa71c"));

        // 1. make a material by the color
        MaterialFactory.makeOpaqueWithColor(getApplicationContext(), colorOrange)
                .thenAccept(material -> {
                    // 2. make a model by the material
                    ModelRenderable model = ShapeFactory.makeCylinder(0.025f, lineLength,
                            new Vector3(0f, lineLength / 2, 0f), material);
                    model.setShadowReceiver(false);
                    model.setShadowCaster(false);

                    // 3. make node
                    Node node = new Node();
                    node.setRenderable(model);
                    node.setParent(anchorNode);

                    // 4. set rotation
                    final Vector3 difference = Vector3.subtract(to, from);
                    final Vector3 directionFromTopToBottom = difference.normalized();
                    final Quaternion rotationFromAToB =
                            Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
                    node.setWorldRotation(Quaternion.multiply(rotationFromAToB,
                            Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 90)));
                });
    }


        private void removeAnchorNode(AnchorNode nodeToremove) {
            //Remove an anchor node
            if (nodeToremove != null) {
                arFragment.getArSceneView().getScene().removeChild(nodeToremove);
                nodeToremove.getAnchor().detach();
                nodeToremove.setParent(null);
                nodeToremove = null;
                Toast.makeText(getApplicationContext(), "Test Delete - anchorNode removed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Test Delete - markAnchorNode was null", Toast.LENGTH_SHORT).show();
            }
        }





}
