package com.bignerdranch.android.beatbox;

public class Sound {
    private String mAssetPath;
    private String mName;
    private Integer mSoundId;

    // Sound constructor
    public Sound(String assetPath) {
        mAssetPath = assetPath;
        // Split sound filename after last forwardslash
        String[] components = assetPath.split("/");
        // Get the last component after the forwardslash split
        // This is our sound filename
        String filename = components[components.length-1];
        // Strip file extension
        mName = filename.replace(".wav","");
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public String getName() {
        return mName;
    }

    public Integer getSoundId() {
        return  mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }
}
