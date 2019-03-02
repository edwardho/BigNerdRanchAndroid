package com.bignerdranch.android.beatbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBox";

    private static final String SOUNDS_FOLDER = "sample_sounds";

    // AssetManager used to access sounds in sample_sounds asset directory
    private AssetManager mAssetManager;
    // List of sounds
    private List<Sound> mSounds = new ArrayList<>();

    public BeatBox(Context context) {
        // Get assets from AssetManager
        mAssetManager = context.getAssets();
        loadSounds();
    }

    public void loadSounds() {
        String[] soundNames;

        // Try to load the sounds in folder sample_sounds
        try {
            soundNames = mAssetManager.list(SOUNDS_FOLDER);
            Log.i(TAG, "Found " + soundNames.length + " sounds");
        }
        // Catch IO exception if there are no sound assets to list
        catch (IOException e) {
            Log.e(TAG, "Could not list assets", e);
            return;
        }

        // For every file in the list of sounds
        for (String fileName : soundNames) {
            // Get the path of the asset
            String assetPath = SOUNDS_FOLDER + "/" + fileName;
            // Get the sound at the asset path
            Sound sound = new Sound(assetPath);
            // Add sound to arraylist mSounds
            mSounds.add(sound);
        }

    }

    // Return list of sounds
    public List<Sound> getSounds(){
        return mSounds;
    }
}
