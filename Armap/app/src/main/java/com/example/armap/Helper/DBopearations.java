package com.example.armap.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.armap.Model.AnchorMarker;
import com.example.armap.Model.VectorMarker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.math.Vector3;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DBopearations extends SQLiteOpenHelper {


    public   DBopearations(Context context,String DBName){

        super(context,DBName,null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS CloudAnchors(anchorid varchar,label varchar);");
        db.execSQL("CREATE TABLE IF NOT EXISTS VectorAnchors(label varchar,x real,y real,z real,relative varchar);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean clearVectorData(){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            db.execSQL("delete from VectorAnchors ;");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void clearAnchorData(){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            db.execSQL("delete from CloudAnchors ;");

        }catch (Exception e){

        }
    }

    public boolean insertCloudAnchor(AnchorMarker cloudAnchor){
       try{
           SQLiteDatabase db=this.getWritableDatabase();
           ContentValues anchorValues= new ContentValues();
           anchorValues.put("anchorid",cloudAnchor.getAnchorId());
           anchorValues.put("label",cloudAnchor.getLabel());
           db.insert("CloudAnchors",null,anchorValues);
           return true;
       }catch (Exception e){
           return false;
       }
    }

    public boolean SaveVectorPosition(VectorMarker Marker){

        try{
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues anchorValues= new ContentValues();
            anchorValues.put("relative",Marker.getRelative());
            anchorValues.put("label",Marker.getLabel());
            anchorValues.put("x",Marker.getX());
            anchorValues.put("y",Marker.getY());
            anchorValues.put("z",Marker.getZ());
            db.insert("VectorAnchors",null,anchorValues);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public List<AnchorMarker> getAllCloudAnchors(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from CloudAnchors", null );
        List<AnchorMarker> allMarkers=new ArrayList<>();

        while (res.moveToNext()){
            AnchorMarker marker=new AnchorMarker(res.getString(0),res.getString(1));
            allMarkers.add(marker);

        }
    return allMarkers;

    }

    public List<VectorMarker> getAllVectorPoints(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from VectorAnchors", null );
        List<VectorMarker> allMarkers=new ArrayList<>();

        while (res.moveToNext()){
            VectorMarker marker=new VectorMarker(res.getString(0), new Vector3(res.getFloat(1),res.getFloat(2),res.getFloat(3)),res.getString(4));
            allMarkers.add(marker);

        }
        return allMarkers;

    }

    public Vector3 getVectorPoint(String Label){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from VectorAnchors where label='"+Label+"';", null );
        res.moveToFirst();
        return new Vector3(res.getFloat(1),res.getFloat(2),res.getFloat(3));

    }



    public List<String> getAllEndNodeLabel(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from CloudAnchors", null );
        List<String> alllabels=new ArrayList<>();

        while (res.moveToNext()){
           if(!res.getString(1).contains("-")){
               alllabels.add(res.getString(1));
           }


        }
        return alllabels;
    }

    public List<String> getAllNodeLabel(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from CloudAnchors", null );
        List<String> alllabels=new ArrayList<>();

        while (res.moveToNext()){

                alllabels.add(res.getString(1));



        }
        return alllabels;
    }

    public String getAnchorId(String Label){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from CloudAnchors where label= '"+Label+"';", null );
       res.moveToFirst();
       String anchor=res.getString(0);
        return res.getString(0);
    }

    public boolean isLabelAvailable(String Label){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from CloudAnchors where label= '"+Label+"';", null );
        if(res.moveToNext()) return true;

        return false;

    }

    public void loadDatabase(){
        clearAnchorData();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Datapoints")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FirebaseTesting", document.getId() + " => " + document.getData().get("Label"));
                                insertCloudAnchor(new AnchorMarker(document.getData().get("anchorid").toString(),document.getData().get("Label").toString()));
                            }
                        } else {
                            Log.w("FirebaseTesting", "Error getting documents.", task.getException());
                        }
                    }
                });

    }



}
