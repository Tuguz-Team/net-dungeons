package com.tuguzteam.netdungeons;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.a = 8;
        config.useAccelerometer = false;
        config.useCompass = true;
        config.hideStatusBar = true;
        initialize(new NetDungeonsGame(), config);
    }
}
