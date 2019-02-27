package android.bignerdranch.criminalintent;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

public class Crime implements Comparable<Crime> {

    // Read only
    private UUID mId;

    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID uuid) {
        mId = uuid;
        mDate = new Date();
    }

    // ID
    public UUID getId() {
        return mId;
    }

    @Override
    public int compareTo(@NonNull Crime crime) {
        return  mId.compareTo(crime.getId());
    }

    // Title
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    // Date
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    // Resolution
    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    // Requires Police
    public boolean requiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }
}
