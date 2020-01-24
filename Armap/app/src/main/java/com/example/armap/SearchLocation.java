package com.example.armap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.armap.Helper.CreateDataPoints;
import com.example.armap.Helper.CustomFragmentConfig;
import com.example.armap.Helper.DBopearations;
import com.example.armap.Helper.MapLines;
import com.example.armap.Helper.Render3dObject;
import com.example.armap.Helper.Routing;
import com.example.armap.Model.AnchorMarker;
import com.example.armap.Model.MapData;
import com.example.armap.Model.NodeSelection;
import com.example.armap.Model.VectorMarker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class SearchLocation extends AppCompatActivity implements Scene.OnUpdateListener  {

    private CustomFragmentConfig mainARfragemnt;

    private DBopearations dBopearations;


    private FloatingActionButton BtnSearch;

    private Spinner sourceSpinner,destSpinner;

    private  String Source,Destination;

    private MapData curreMapData;
    private List<String> path;



    private boolean isReached=false;

    private int pathCompleted=0;

    private List<Vector3> allRealPositions;

    private float totalDistance=0f;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private ImageView ImgDirection;

    private TextView startTxt;//endTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //enable full screen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainARfragemnt= (CustomFragmentConfig) getSupportFragmentManager().findFragmentById(R.id.searchFrag);
        startTxt=findViewById(R.id.txtStart);
       // endTxt=findViewById(R.id.txtEnd);
        BtnSearch=findViewById(R.id.btnsearch);
        ImgDirection=findViewById(R.id.ImgDirection);
        dBopearations=new DBopearations(this,getResources().getString(R.string.DBname));
        prefs=getSharedPreferences(getResources().getString(R.string.SHARED_PREF_NAME),MODE_PRIVATE);
        editor=prefs.edit();

        Source=prefs.getString("startpoint","");
        Destination=prefs.getString("endpoint","");
//        sourceSpinner=findViewById(R.id.startnode);
//        destSpinner=findViewById(R.id.endnode);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,dBopearations.getAllEndNodeLabel());
//        sourceSpinner.setAdapter(dataAdapter);
//        destSpinner.setAdapter(dataAdapter);
        curreMapData=new MapData();

//        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Source=sourceSpinner.getSelectedItem().toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        destSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Destination=destSpinner.getSelectedItem().toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        BtnSearch.setOnClickListener(v -> {
       path=new Routing().Search(Source,Destination,dBopearations.getAllNodeLabel());
       if(!path.contains(Destination)){
           path.add(Destination);
       }
        if(path==null){
            Toast.makeText(getApplicationContext(),"No path found",Toast.LENGTH_LONG).show();
        }else{
            isReached=false;
            totalDistance=0f;
            pathCompleted=0;
            dBopearations.clearVectorData();
            allRealPositions=new ArrayList<>();
            //directions=Routing.findDirections(path);
            for (String label : path){
                Anchor anchor= mainARfragemnt.getArSceneView().getSession().resolveCloudAnchor(dBopearations.getAnchorId(label));
               CreateDataPoints.CreatePoint(anchor,dBopearations,mainARfragemnt,getApplicationContext(),R.layout.datapoints,R.id.txtDataPoint,label);
                //Render3dObject.Rendertext(anchor,curreMapData,mainARfragemnt,getApplicationContext(),R.layout.datapoints,R.id.txtDataPoint,label);
            }
            mainARfragemnt.getArSceneView().getScene().addOnUpdateListener(this);
            //DrawLine(path);



        }

        });
    }

    private void DrawLine(List<String> path) {
        List<VectorMarker> allpoints=dBopearations.getAllVectorPoints();
        try{
            for (int i=0;i<path.size()-1;i++){
                Vector3 start=dBopearations.getVectorPoint(path.get(i));
                Vector3 end=dBopearations.getVectorPoint(path.get(i+1));
                MapLines.addLineBetweenPoints(mainARfragemnt.getArSceneView().getScene(),start,end,getApplicationContext(),mainARfragemnt,curreMapData);
            }
        }catch (Exception e){
            Log.d("LineError",e.getMessage());
        }


    }

    @Override
    public void onUpdate(FrameTime frameTime) {

     try{
         Node node=mainARfragemnt.getArSceneView().getScene().findByName(path.get(pathCompleted));



         if(node!=null){
             Vector3 pos= node.getWorldPosition();

             if (!(pos.x==0f && pos.y==0f && pos.z==0f) ){
                 if(isReached) return;
                 if(pathCompleted>=path.size()){
                     allRealPositions.add(pos);
                     float lineLength = Vector3.subtract(allRealPositions.get(pathCompleted-1), pos).length();
                     totalDistance+=lineLength;
                     Toast.makeText(getApplicationContext(),"You Reached to your Destination",Toast.LENGTH_LONG).show();
                     isReached=true;
                     setImageDirection("Reached");
                     return;
                 }

                 if(node.getName().equalsIgnoreCase(Destination)){
                     Toast.makeText(getApplicationContext(),"You Reached to your Destination",Toast.LENGTH_LONG).show();
                     isReached=true;
                     setImageDirection("Reached");
                     return;
                 }
              allRealPositions.add(pos);
                 String Direction=Routing.nextDirection(path.get(pathCompleted),path.get(pathCompleted+1));
              if(pathCompleted>0){
                  float lineLength = Vector3.subtract(allRealPositions.get(pathCompleted-1), pos).length();
                  totalDistance+=lineLength;

                  //endTxt.setText(path.get(pathCompleted));
                  Toast.makeText(getApplicationContext(),"Path : "+pathCompleted+" Distance"+lineLength +" Move "+Direction,Toast.LENGTH_LONG).show();

                 // MapLines.addLineBetweenPoints(mainARfragemnt.getArSceneView().getScene(),allRealPositions.get(pathCompleted-1),pos,getApplicationContext(),mainARfragemnt,curreMapData);
              }else{
                  Toast.makeText(getApplicationContext()," Move "+Direction,Toast.LENGTH_LONG).show();
              }

                 startTxt.setText("Total Distance Traveled : "+totalDistance+ "meters");
                 setImageDirection(Direction);
//              Toast.makeText(getApplicationContext(),"X : "+pos.x+" "+"Y : "+pos.y+" "+"Z : "+pos.z+" ",Toast.LENGTH_LONG).show();
              pathCompleted++;
          }
         }else{

         }
     }catch (Exception e){
         Log.d("Pathfinding",e.getMessage());
     }
    }

    public void setImageDirection(String direction){

        if(direction.equalsIgnoreCase("Left and Forward")){
            ImgDirection.setImageDrawable(getDrawable(R.drawable.left));
        }else if(direction.equalsIgnoreCase("Right and Forward")){
            ImgDirection.setImageDrawable(getDrawable(R.drawable.right));
        }else if(direction.equalsIgnoreCase("forward")){
            ImgDirection.setImageDrawable(getDrawable(R.drawable.forward));
        }else if(direction.equalsIgnoreCase("reached")){
            ImgDirection.setImageDrawable(getDrawable(R.drawable.success));
        }else{
            ImgDirection.setImageDrawable(getDrawable(R.drawable.lookaround));
        }
    }

}
