package com.bignerdranch.android.beatbox;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

public class SoundViewModel extends BaseObservable {

    private Sound mSound;
    private BeatBox mBeatBox;

    public SoundViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }

    // Set title as bindable in order to keep buttons static
    @Bindable
    public String getTitle() {
        return mSound.getName();
    }

    public Sound getSound() {
        return mSound;
    }

    public void setSound(Sound sound) {
        mSound = sound;
        // Notifies all bindable fields on our objects have been updated
        notifyChange();
    }

    public void onButtonClicked() {
        mBeatBox.play(mSound);
    }
}
