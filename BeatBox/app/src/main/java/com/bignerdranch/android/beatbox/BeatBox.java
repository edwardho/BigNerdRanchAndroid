package com.bignerdranch.android.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBox";

    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    // AssetManager used to access sounds in sample_sounds asset directory
    private AssetManager mAssetManager;
    // List of sounds
    private List<Sound> mSounds = new ArrayList<>();
    // SoundPool to control the maximum number of sounds that can play at once
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        // Get assets from AssetManager
        mAssetManager = context.getAssets();
        // This old constructor is deprecated, but needed for compatibility
        // SoundPool.Builder is not available on API 19, so we are using SoundPool(int, int, int)
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        // Before playing soundId, make sure it is not null
        if (soundId == null) {
            return;
        }
        // Play sound with right and left volume at 1, priority 1, no loops, and a standard rate
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    // Method that cleans up SoundPool
    public void release() {
        mSoundPool.release();
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
            try {
                // Get the path of the asset
                String assetPath = SOUNDS_FOLDER + "/" + fileName;
                // Get the sound at the asset path
                Sound sound = new Sound(assetPath);
                // Load sound
                load(sound);
                // Add sound to arraylist mSounds
                mSounds.add(sound);
            }
            catch (IOException e) {
                Log.e(TAG, "Could not load sound" + fileName, e);
            }
        }

    }

    private void load(Sound sound) throws IOException {
        // Open sound's assetpath using AssetFileDescriptor
        AssetFileDescriptor afd = mAssetManager.openFd(sound.getAssetPath());
        // Load sound into mSoundPool with priority 1
        int soundId = mSoundPool.load(afd, 1);
        // Set the sound
        sound.setSoundId(soundId);
    }

    // Return list of sounds
    public List<Sound> getSounds(){
        return mSounds;
    }
}
