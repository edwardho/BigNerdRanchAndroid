package com.bignerdranch.android.beatbox;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.beatbox.databinding.FragmentBeatBoxBinding;
import com.bignerdranch.android.beatbox.databinding.ListItemSoundBinding;

import java.util.List;

public class BeatBoxFragment extends Fragment {

    private BeatBox mBeatBox;

    // Constructor for BeatBoxFragment
    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retains BeatBox instance to keep it alive between configuration change
        setRetainInstance(true);

        // New instance of BeatBox in BeatBoxFragment
        mBeatBox = new BeatBox(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate instance of FragmentBeatBoxBinding to bind the fragment to layout fragment_beat_box
        FragmentBeatBoxBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_beat_box, container, false);

        // Get and configure GridLayoutManager as the layoutManager for recyclerview
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        // Get and configure SoundAdapter when the view is created
        binding.recyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Releases SoundPool
        mBeatBox.release();
    }

    // SoundHolder hooked up to list_item_sound.xml
    private class SoundHolder extends RecyclerView.ViewHolder {
        private ListItemSoundBinding mBinding;

        public SoundHolder(ListItemSoundBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            // Set a new Sound View Model to the binding class
            mBinding.setViewModel(new SoundViewModel(mBeatBox));
        }

        // Method to bind the sound to the ViewHolder
        public void bind(Sound sound) {
            mBinding.getViewModel().setSound(sound);
            mBinding.executePendingBindings();
        }
    }



    // Adapter hooked up to SoundHolder
    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {
        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @NonNull
        @Override
        public SoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ListItemSoundBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.list_item_sound, parent, false);
            return new SoundHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundHolder soundHolder, int position) {
            // Get and bind sound
            Sound sound = mSounds.get(position);
            soundHolder.bind(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }
}
