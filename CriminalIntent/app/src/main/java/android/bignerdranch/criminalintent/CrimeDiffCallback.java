package android.bignerdranch.criminalintent;

// DiffUtils class to handle updating crimelist

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class CrimeDiffCallback extends DiffUtil.Callback {

    List<Crime> oldCrimes;
    List<Crime> newCrimes;

    public CrimeDiffCallback(List<Crime> newCrimes, List<Crime> oldCrimes) {
        this.newCrimes = newCrimes;
        this.oldCrimes = oldCrimes;
    }

    @Override
    public int getOldListSize() {
        return oldCrimes.size();
    }

    @Override
    public int getNewListSize() {
        return newCrimes.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCrimes.get(oldItemPosition).getId() == newCrimes.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCrimes.get(oldItemPosition).equals(newCrimes.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
