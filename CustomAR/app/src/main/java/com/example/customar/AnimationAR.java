package com.example.customar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.SkeletonNode;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class AnimationAR extends AppCompatActivity {

    CustomFragmentConfig AnimatedFragment;
    Button animateAction;

    private ModelAnimator modelAnimator;

    private int animattionIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_ar);
        AnimatedFragment= (CustomFragmentConfig) getSupportFragmentManager().findFragmentById(R.id.animationfragment);
       animateAction=findViewById(R.id.btnAnimate);

        AnimatedFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            createModel(hitResult.createAnchor(),AnimatedFragment);
           
        });




    }

    private void createModel(Anchor anchor, CustomFragmentConfig animatedFragment) {
        ModelRenderable.builder()
            .setSource(this, Uri.parse("skeleton.sfb"))
                .build()
                .thenAccept(modelRenderable -> {

                    AnchorNode anchorNode =new AnchorNode(anchor);

                    SkeletonNode skeletonNode= new SkeletonNode();
                    skeletonNode.setParent(anchorNode);
                    skeletonNode.setRenderable(modelRenderable);

                    animatedFragment.getArSceneView().getScene().addChild(anchorNode);

                    animateAction.setOnClickListener(v -> {
                        animateModel(modelRenderable);
                    });
                });
    }

    private void animateModel(ModelRenderable modelRenderable) {

        if(modelAnimator!=null && modelAnimator.isRunning()){
            modelAnimator.end();
        }
        int animations=modelRenderable.getAnimationDataCount();
        if (animattionIndex==animations) animattionIndex=0;

        AnimationData animationData=modelRenderable.getAnimationData(animattionIndex);

        modelAnimator =new ModelAnimator(animationData,modelRenderable);
        modelAnimator.start();
        animattionIndex++;
    }
}
