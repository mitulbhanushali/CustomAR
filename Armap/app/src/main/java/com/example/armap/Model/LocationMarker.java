package com.example.armap.Model;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;

public class LocationMarker {

    private Vector3 position;
    private Node node;
    private String label;

    public LocationMarker(){}

    public LocationMarker(Vector3 position, Node node, String label) {
        this.position = position;
        this.node = node;
        this.label = label;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Node getAnchorNode() {
        return node;
    }

    public void setAnchorNode(Node node) {
        this.node = node;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }





}
