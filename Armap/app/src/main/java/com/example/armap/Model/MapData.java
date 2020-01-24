package com.example.armap.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;

import java.security.PublicKey;
import java.util.ArrayList;

public class MapData  {


    private  int ModelPoints=0;

    private  ArrayList<Vector3> anchorPoses;
    private ArrayList<AnchorNode> allLineNodes;
    private float totalLength=0f;

    private  ArrayList<LocationMarker> allMarkers;
    public MapData(){

        anchorPoses=new ArrayList<>();
        allLineNodes=new ArrayList<>();
        allMarkers=new ArrayList<>();

    }

    public void IncModelPoints() {
        ModelPoints++;
    }

    public int getModelPoints(){
        return ModelPoints;
    }

    public ArrayList<Vector3> getAnchorPoses() {
        return anchorPoses;
    }

    public Vector3 getAnchorPosByIndex(int index) {
        return anchorPoses.get(index);
    }

    public void setAnchorPoses(Vector3 anchorPos) {
        this.anchorPoses.add(anchorPos);
    }

    public void removeAnchorPos(int index){
        if(this.anchorPoses.get(index)!=null){
            this.anchorPoses.remove(index);
        }

    }

    public ArrayList<AnchorNode> getAllLineNodes() {
        return allLineNodes;
    }

    public void setLineNodes(AnchorNode LineNode) {
        this.allLineNodes.add(LineNode);
    }
    public void removeAllLineNodes() {
        this.allLineNodes.clear();
    }

    public float getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(float Length) {
        this.totalLength += Length;
    }


    public  void saveToDB(){

    }


    public  AnchorNode getAnchorNode(String nameofNode){
        for(AnchorNode node : allLineNodes){
            if(node.getName().equalsIgnoreCase(nameofNode)){
                return node;
            }
        }
        return null;
    }

    public void setAllMarkers(LocationMarker marker){
        allMarkers.add(marker);
    }

    public LocationMarker getLocationmarkerByName(String label){

        for (LocationMarker marker:
             allMarkers) {

            if(marker.getLabel().equalsIgnoreCase(label)){
                return marker;
            }
        }
        return null;
    }

    public  LocationMarker getLocationMarkerByIndex(int index){
        return allMarkers.get(index);
    }




}
