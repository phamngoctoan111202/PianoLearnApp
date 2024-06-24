package com.noatnoat.pianoapp.utils;


import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class SettingConfigs {

    private static SettingConfigs objInsta;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    public SettingConfigs() {
    }

    public FirebaseRemoteConfig getConfig() {
        return this.firebaseRemoteConfig;
    }

    public void setConfig(FirebaseRemoteConfig config) {
        this.firebaseRemoteConfig = config;
    }

    public static SettingConfigs getInstance() {
        if (objInsta == null) {
            objInsta = new SettingConfigs();
        }
        return objInsta;
    }
}
