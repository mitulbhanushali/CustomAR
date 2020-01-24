package com.example.customar;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class CustomFragmentConfig extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setCloudAnchorMode(Config.CloudAnchorMode.ENABLED);
        config.setFocusMode(Config.FocusMode.AUTO);
        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL);
        session.configure(config);
        this.getArSceneView().setupSession(session);


        return config;
    }
}
