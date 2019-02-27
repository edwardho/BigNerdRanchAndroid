package android.bignerdranch.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private List<Crime> mCrimes;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        // Create ordered list of 100 UUIDs to assign
        UUID[] uuidArray = new UUID[100];
        for (int i = 0; i < 100; i++) {
            // Assign random UUID
            uuidArray[i] = UUID.randomUUID();
        }
        // Sort new UUID arraylist
        Arrays.sort(uuidArray);

        // Populating Crime arraylist with 100 boring crime objects
        // assigning UUID from our list of sorted UUIDs
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            // Create new Crime
            Crime crime = new Crime(uuidArray[i]);
            // Set Crime title
            crime.setTitle("Crime #" + i );
            // Set Crime Solved
            crime.setSolved(i % 2 == 0);
            // Set Crime Requires Police
            crime.setRequiresPolice(i % 3 == 0);
            mCrimes.add(crime);
        }
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        // Create a new crime with the given id to compare
        Crime selectedCrime = new Crime(id);
        // Use binary search to find the selected crime from our list of crimes
        int index = Collections.binarySearch(mCrimes, selectedCrime);
        // Return the crime in mCrimes with index that has matching UUID
        return mCrimes.get(index);
    }
}
