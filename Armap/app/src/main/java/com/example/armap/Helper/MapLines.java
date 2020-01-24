package com.example.armap.Helper;

import android.content.Context;
import android.widget.Toast;

import com.example.armap.Model.MapData;
import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;

public class MapLines {

    public static void addLineBetweenPoints(Scene scene, Vector3 from, Vector3 to, Context appContext, CustomFragmentConfig arFragment, MapData currentMapdata) {
        // prepare an anchor position
        Quaternion camQ = scene.getCamera().getWorldRotation();
        float[] f1 = new float[]{to.x, to.y, to.z};
        float[] f2 = new float[]{camQ.x, camQ.y, camQ.z, camQ.w};
        Pose anchorPose = new Pose(f1, f2);

        // make an ARCore Anchor
        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(anchorPose);
        // Node that is automatically positioned in world space based on the ARCore Anchor.
        AnchorNode anchorNode = new AnchorNode(anchor);
        currentMapdata.setLineNodes(anchorNode);
        //allLineNodes.add(anchorNode);
        anchorNode.setParent(scene);

        // Compute a line's length
        float lineLength = Vector3.subtract(from, to).length();
        //
        currentMapdata.setTotalLength(lineLength);
        //totalLength+=lineLength;

//        allDistance.add(lineLength);
        // Prepare a color
        Color colorOrange = new Color(android.graphics.Color.parseColor("#4C4CFF"));

        // 1. make a material by the color
        MaterialFactory.makeOpaqueWithColor(appContext, colorOrange)
                .thenAccept(material -> {
                    // 2. make a model by the material
                    ModelRenderable model = ShapeFactory.makeCylinder(0.015f, lineLength,
                            new Vector3(0f, lineLength / 2, 0f), material);
                    model.setShadowReceiver(false);
                    model.setShadowCaster(false);

                    // 3. make node
                    Node node = new Node();
                    node.setRenderable(model);
                    node.setParent(anchorNode);

                    // 4. set rotation
                    final Vector3 difference = Vector3.subtract(to, from);
                    final Vector3 directionFromTopToBottom = difference.normalized();
                    final Quaternion rotationFromAToB =
                            Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
                    node.setWorldRotation(Quaternion.multiply(rotationFromAToB,
                            Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 90)));
                });
    }

    public static void removeAnchorNode(AnchorNode nodeToremove,CustomFragmentConfig arFragment,Context appContext) {
        //Remove an anchor node
        if (nodeToremove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToremove);
            nodeToremove.getAnchor().detach();
            nodeToremove.setParent(null);
            nodeToremove = null;
            Toast.makeText(appContext, "Test Delete - anchorNode removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(appContext, "Test Delete - markAnchorNode was null", Toast.LENGTH_SHORT).show();
        }
    }

}
