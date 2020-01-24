package com.example.armap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.armap.Helper.CustomFragmentConfig;
import com.example.armap.Helper.DBopearations;
import com.example.armap.Helper.MapLines;
import com.example.armap.Helper.Render3dObject;
import com.example.armap.Model.AnchorMarker;
import com.example.armap.Model.LocationMarker;
import com.example.armap.Model.MapData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RenderPoints extends AppCompatActivity implements Scene.OnUpdateListener {

    private CustomFragmentConfig CFrag;
    private Anchor anchor1,anchor2;



    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private MapData curreMapData;

    private Button BtngetCloudAnchor,BtndrawLine;

    private DBopearations dBopearations;

    private List<AnchorMarker> allCloudAnchors;
    private List<LocationMarker> allMarkers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_points);
        CFrag= (CustomFragmentConfig) getSupportFragmentManager().findFragmentById(R.id.renderpointFrag);
        curreMapData=new MapData();
        prefs=getSharedPreferences(getResources().getString(R.string.SHARED_PREF_NAME),MODE_PRIVATE);
        editor=prefs.edit();
        dBopearations=new DBopearations(this,getResources().getString(R.string.DBname));
        BtngetCloudAnchor=findViewById(R.id.btngetcloudanchor);
        allMarkers=new ArrayList<>();
        BtngetCloudAnchor.setOnClickListener(v -> {

            SetCloudAnchor();
            CFrag.getArSceneView().getScene().addOnUpdateListener(this);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Datapoints")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("FirebaseTesting", document.getId() + " => " + document.getData().get("Label"));
                                }
                            } else {
                                Log.w("FirebaseTesting", "Error getting documents.", task.getException());
                            }
                        }
                    });
        });
        BtndrawLine=findViewById(R.id.btndrawLine);

        BtndrawLine.setOnClickListener(v -> {
            if(anchor1!=null && anchor2!=null){
                CFrag.getArSceneView().getScene().getChildren();    
                //MapLines.addLineBetweenPoints(CFrag.getArSceneView().getScene(),curreMapData.getAnchorPosByIndex(0),curreMapData.getAnchorPosByIndex(1),getApplicationContext(),CFrag,curreMapData);
        }

        });



    }

    private void SetCloudAnchor() {
//        String anchorid= prefs.getString("anchorid","null");
//        String anchorid1=prefs.getString("anchorid1","null");
//        if(anchorid.equals("null")) {
//            Toast.makeText(getApplicationContext(),"No Anchor Found",Toast.LENGTH_LONG).show();
//            return;
//        }else{
//           anchor1= CFrag.getArSceneView().getSession().resolveCloudAnchor(anchorid);
//
//            Render3dObject.Rendertext(anchor1,curreMapData,CFrag,getApplicationContext(),R.layout.datapoints,R.id.txtDataPoint,"");
//        }
//
//        if(anchorid1.equals("null")) {
//            Toast.makeText(getApplicationContext(),"No Anchor Found",Toast.LENGTH_LONG).show();
//            return;
//        }else{
//            anchor2= CFrag.getArSceneView().getSession().resolveCloudAnchor(anchorid1);
//            Log.d("AddElementToScreen","Start");
//            Render3dObject.Rendertext(anchor2,curreMapData,CFrag,getApplicationContext(),R.layout.datapoints,R.id.txtDataPoint,"");
//        }




//        if(anchor1!=null && anchor2!=null){
//            MapLines.addLineBetweenPoints(CFrag.getArSceneView().getScene(),curreMapData.getAnchorPosByIndex(0),curreMapData.getAnchorPosByIndex(1),getApplicationContext(),CFrag,curreMapData);
//        }


        allCloudAnchors=dBopearations.getAllCloudAnchors();

        for (AnchorMarker marker : allCloudAnchors){
            if(marker.getAnchorId().equals("null") || marker.getAnchorId().trim().length()==0 || marker.getAnchorId().equals("")) {
            Toast.makeText(getApplicationContext(),"No Anchor Found",Toast.LENGTH_LONG).show();
            return;
        }else{
           anchor1= CFrag.getArSceneView().getSession().resolveCloudAnchor(marker.getAnchorId());

            Render3dObject.Rendertext(anchor1,curreMapData,CFrag,getApplicationContext(),R.layout.datapoints,R.id.txtDataPoint,marker.getLabel());
        }
        }

    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        List<Node> allNodes=CFrag.getArSceneView().getScene().getChildren();
        for (Node node : allNodes){
            Vector3 pos= node.getWorldPosition();
            String label=node.getName();
            if(pos.x!=0f && pos.y!=0f && pos.z!=0f){
                if(!checkLabel(label)){
                    LocationMarker marker=new LocationMarker(pos,node,label);
                    allMarkers.add(marker);
                    Log.d("objectcreated",marker.getLabel());
                }
            }

        }
        DrawPath();

    }


    public  boolean checkLabel(String label){

        for(LocationMarker marker : allMarkers){
            if(marker.getLabel().equalsIgnoreCase(label)){
                return true;
            }
        }
        return false;
    }


    public  void DrawPath(){
        if(allMarkers.size()>=2){
            MapLines.addLineBetweenPoints(CFrag.getArSceneView().getScene(),allMarkers.get(0).getPosition(),allMarkers.get(1).getPosition(),getApplicationContext(),CFrag,curreMapData);
        }

    }


}
