package com.example.armap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.armap.Helper.CreateDataPoints;
import com.example.armap.Helper.CustomFragmentConfig;
import com.example.armap.Helper.DBopearations;
import com.example.armap.Helper.Render3dObject;
import com.example.armap.Model.MapData;
import com.example.armap.Model.NodeSelection;
import com.google.ar.core.Pose;

public class CreateVectorPoint extends AppCompatActivity {

    private CustomFragmentConfig mainARfragemnt;

    private MapData currentMapData;

    private Button BtnSavePoints;

    private DBopearations dBopearations;

    private EditText EdtnodeLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vector_point);
        dBopearations=new DBopearations(this,getResources().getString(R.string.DBname));
        currentMapData=new MapData();
        Spinner nodeSelctionSpinner =  findViewById(R.id.nodeselection);
        BtnSavePoints=findViewById(R.id.btnsavepoint);
        EdtnodeLabel=findViewById(R.id.edtnodeid);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, NodeSelection.getAllTypes());

        nodeSelctionSpinner.setAdapter(dataAdapter);
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //enable full screen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainARfragemnt= (CustomFragmentConfig) getSupportFragmentManager().findFragmentById(R.id.createvectorpointFrag);
        mainARfragemnt.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            CreateDataPoints.CreatePoint(hitResult.createAnchor(),dBopearations,mainARfragemnt,this,R.layout.datapoints,R.id.txtDataPoint,EdtnodeLabel.getText().toString());
        });
    }
}
