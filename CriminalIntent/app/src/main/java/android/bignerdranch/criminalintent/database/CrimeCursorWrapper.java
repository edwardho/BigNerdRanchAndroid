package android.bignerdranch.criminalintent.database;

import android.bignerdranch.criminalintent.Crime;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Columns.UUID));
        String title = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Columns.TITLE));
        long date = getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Columns.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Columns.SOLVED));
        int requiresPolice = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Columns.CALL_POLICE));
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Columns.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setRequiresPolice(requiresPolice != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
