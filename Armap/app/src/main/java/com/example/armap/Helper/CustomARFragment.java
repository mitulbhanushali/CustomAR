package com.example.armap.Helper;

import com.example.armap.MainActivity;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class CustomARFragment extends ArFragment {
    @Override
    protected Config getSessionConfiguration(Session session) {
       Config config = new Config(session);
       config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
       config.setCloudAnchorMode(Config.CloudAnchorMode.ENABLED);
       config.setFocusMode(Config.FocusMode.AUTO);
       session.configure(config);
       this.getArSceneView().setupSession(session);

//        ((MainActivity)getActivity()).setupDatabase(config,session);
       return config;

    }
}
