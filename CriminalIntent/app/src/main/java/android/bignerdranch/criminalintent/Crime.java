package android.bignerdranch.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {

    // Read only
    private UUID mId;

    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    // ID
    public UUID getId() {
        return mId;
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
}
