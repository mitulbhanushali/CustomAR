package com.example.armap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.armap.Helper.CreateDataPoints;
import com.example.armap.Helper.CustomFragmentConfig;
import com.example.armap.Helper.DBopearations;
import com.example.armap.Model.MapData;
import com.example.armap.Model.VectorMarker;
import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class renderVectorPoints extends AppCompatActivity {

    private CustomFragmentConfig CFrag;
    private Button BtnVectorPoints,BtndrawLine;

    private DBopearations dBopearations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_vector_points);
        CFrag= (CustomFragmentConfig) getSupportFragmentManager().findFragmentById(R.id.renderpointFrag);

        dBopearations=new DBopearations(this,getResources().getString(R.string.DBname));
        BtnVectorPoints=findViewById(R.id.btngetvectorpoints);

        BtnVectorPoints.setOnClickListener(v -> {
            List<VectorMarker> allmarker=dBopearations.getAllVectorPoints();
            for(VectorMarker marker : allmarker){
                Vector3 vector3=marker.getPostion();
                //Pose pose=new Pose();
              //  Anchor anchor= CFrag.getArSceneView().getSession().createAnchor();
            }
           //CreateDataPoints.CreatePoint();

        });
    }
}
