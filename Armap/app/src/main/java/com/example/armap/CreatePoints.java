package com.example.armap;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.armap.Helper.CreateDataPoints;
import com.example.armap.Helper.CustomFragmentConfig;
import com.example.armap.Helper.DBopearations;
import com.example.armap.Helper.Render3dObject;
import com.example.armap.Model.AnchorMarker;
import com.example.armap.Model.MapData;
import com.example.armap.Model.NodeSelection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePoints extends AppCompatActivity {

    private CustomFragmentConfig mainARfragemnt;

    private MapData currentMapData;

    private Button BtnSavePoints;

    private boolean MarkerPlaced=false;
    private boolean MarkerSaved=false;

    private EditText EdtnodeId;

    private enum  AnchorState{
        NONE,
        HOSTING,
        HOSTED
    }
    private Anchor anchor;
    private  AnchorState currentState=AnchorState.NONE;
    private  boolean ishosted=false;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private List<AnchorMarker> allCloudAnchors;
    private DBopearations dBopearations;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_points);
        prefs=getSharedPreferences(getResources().getString(R.string.SHARED_PREF_NAME),MODE_PRIVATE);
        editor=prefs.edit();
        dBopearations=new DBopearations(this,getResources().getString(R.string.DBname));


        currentMapData=new MapData();
       // Spinner nodeSelctionSpinner =  findViewById(R.id.nodeselection);
        BtnSavePoints=findViewById(R.id.btnsavepoint);
        EdtnodeId=findViewById(R.id.edtnodeid);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, NodeSelection.getAllTypes());

//        nodeSelctionSpinner.setAdapter(dataAdapter);
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //enable full screen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainARfragemnt= (CustomFragmentConfig) getSupportFragmentManager().findFragmentById(R.id.createpointFrag);
        mainARfragemnt.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if(!ishosted){
                anchor=mainARfragemnt.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                currentState=AnchorState.HOSTING;
                ishosted=true;
                BtnSavePoints.setEnabled(false);


                Thread thread=new Thread(() -> {
                    try{
                        Thread.sleep(3000);

                    }catch (Exception e){

                    }finally {
                       this.runOnUiThread(() -> { BtnSavePoints.setEnabled(true);});

                    }

                });

                thread.start();


            }



            Render3dObject.Rendertext(anchor,currentMapData,mainARfragemnt,this,R.layout.datapoints,R.id.txtDataPoint,EdtnodeId.getText().toString());
        });

        BtnSavePoints.setOnClickListener(v -> {
//            Node node=mainARfragemnt.getArSceneView().getScene().findByName(EdtnodeId.getText().toString());
//
//            if(node!=null){
//               Vector3 pos= node.getWorldPosition();
//
//                Toast.makeText(getApplicationContext(),"X : "+pos.x+" "+"Y : "+pos.y+" "+"Z : "+pos.z+" ",Toast.LENGTH_LONG).show();
//
//            }

            if(currentState != AnchorState.HOSTING) return ;

            Anchor.CloudAnchorState cloudAnchorState =anchor.getCloudAnchorState();
            if(cloudAnchorState.isError()){
                Toast.makeText(getApplicationContext(),cloudAnchorState.toString(),Toast.LENGTH_LONG).show();
            } else if(cloudAnchorState==Anchor.CloudAnchorState.SUCCESS){
                currentState=AnchorState.HOSTED;

                String anchorId= anchor.getCloudAnchorId();
//                String prvanchorid= prefs.getString("anchorid","null");
//                if(prvanchorid.equals("null")) {
//                    editor.putString("anchorid",anchorId);
//                    editor.apply();
//                    Toast.makeText(getApplicationContext(),"Cloud Anchor Created ",Toast.LENGTH_LONG).show();
//
//                }else{
//                    editor.putString("anchorid1",anchorId);
//                    editor.apply();
//                    Toast.makeText(getApplicationContext(),"Cloud Anchor Created ",Toast.LENGTH_LONG).show();
//                }

                AnchorMarker marker=new AnchorMarker(anchorId,EdtnodeId.getText().toString());
                dBopearations.insertCloudAnchor(marker);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> datapoint = new HashMap<>();
                datapoint.put("Label", marker.getLabel());
                datapoint.put("anchorid", marker.getAnchorId());
                db.collection("Datapoints")
                        .add(datapoint)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(),"Data Point Stored ",Toast.LENGTH_LONG).show();
                                Log.d("FirebaseTesting", "DocumentSnapshot added with ID: " + documentReference.getId());
                                dBopearations.loadDatabase();
                                Intent mainIntent = new Intent(CreatePoints.this,StartPointQR.class);
                                CreatePoints.this.startActivity(mainIntent);
                                CreatePoints.this.finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Data Point Not Stored ",Toast.LENGTH_LONG).show();
                                Log.w("FirebaseTesting", "Error adding document", e);
                            }
                        });


            }
        });

    }




}
