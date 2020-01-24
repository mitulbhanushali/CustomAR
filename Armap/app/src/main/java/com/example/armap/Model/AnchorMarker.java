package com.example.armap.Model;

public class AnchorMarker {

    String anchorId;
    String label;

    public AnchorMarker(){}

    public AnchorMarker(String anchorId, String label) {
        this.anchorId = anchorId;
        this.label = label;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
