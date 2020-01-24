package com.example.armap.Model;

import com.google.ar.sceneform.math.Vector3;

public class VectorMarker {

   private  String label;
   private Vector3 postion;
   private String relative;

    public VectorMarker() {
    }

    public VectorMarker(String label, Vector3 postion, String relative) {
        this.label = label;
        this.postion = postion;
        this.relative = relative;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Vector3 getPostion() {
        return postion;
    }

    public void setPostion(Vector3 postion) {
        this.postion = postion;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }

    public  float getX(){
        return postion.x;
    }

    public  float getY(){
        return postion.y;
    }


    public  float getZ(){
        return postion.z;
    }

    public void ReletivePosition(Vector3 relativeTo){

        this.postion=Vector3.subtract(this.postion,relativeTo);

    }


}
