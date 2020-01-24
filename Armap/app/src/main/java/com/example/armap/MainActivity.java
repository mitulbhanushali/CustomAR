package com.example.armap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.armap.Helper.CustomFragmentConfig;
import com.example.armap.Helper.MapLines;
import com.example.armap.Helper.Render3dObject;
import com.example.armap.Model.MapData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CustomFragmentConfig mainARfragemnt;

    private FloatingActionButton DrawMapLines;


    private MapData currentMapData;

    Button BtnCreatePoints,BtnRenderPoints,BtnSearch,BtnReadQR;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("ARMaP",MODE_PRIVATE);
        currentMapData=new MapData();
        mainARfragemnt= (CustomFragmentConfig) getSupportFragmentManager().findFragmentById(R.id.mapArFrag);
        DrawMapLines=findViewById(R.id.drawMapLines);
        BtnCreatePoints=findViewById(R.id.btncreatepoints);
        BtnRenderPoints=findViewById(R.id.btnrenderpoints);
        BtnSearch=findViewById(R.id.btnsearch);
        BtnReadQR=findViewById(R.id.btnreadQr);


        mainARfragemnt.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            Render3dObject.Rendertext(hitResult.createAnchor(),currentMapData,mainARfragemnt,this,R.layout.datapoints,R.id.txtDataPoint,null);
        });


        DrawMapLines.setOnClickListener(v -> {


                for (AnchorNode anchorNode : currentMapData.getAllLineNodes())
                {
                   MapLines.removeAnchorNode(anchorNode,mainARfragemnt,getApplicationContext());
                }
             currentMapData.removeAllLineNodes();

            for (int i=0; i<currentMapData.getAnchorPoses().size()-1; i++){
                Vector3 start=currentMapData.getAnchorPosByIndex(i);
                Vector3 end = currentMapData.getAnchorPosByIndex(i+1);
                MapLines.addLineBetweenPoints(mainARfragemnt.getArSceneView().getScene(),start,end,getApplicationContext(),mainARfragemnt,currentMapData);
            }

            Toast.makeText(getApplicationContext(),"You need to travel"+currentMapData.getTotalLength()+"meters",Toast.LENGTH_LONG).show();

        });

        BtnCreatePoints.setOnClickListener(v -> {
            Intent createPointActivity=new Intent(this,CreatePoints.class);


            startActivity(createPointActivity);
        });
        BtnRenderPoints.setOnClickListener(v -> {
            Intent createPointActivity=new Intent(this,RenderPoints.class);


            startActivity(createPointActivity);
        });
        BtnSearch.setOnClickListener(v -> {
            Intent createPointActivity=new Intent(this,SearchLocation.class);


            startActivity(createPointActivity);
        });
        BtnReadQR.setOnClickListener(v -> {
            Intent createPointActivity=new Intent(this,StartPointQR.class);


            startActivity(createPointActivity);
        });
    }
}
