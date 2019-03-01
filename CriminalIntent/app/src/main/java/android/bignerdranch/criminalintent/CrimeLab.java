package android.bignerdranch.criminalintent;

import android.bignerdranch.criminalintent.database.CrimeBaseHelper;
import android.bignerdranch.criminalintent.database.CrimeCursorWrapper;
import android.bignerdranch.criminalintent.database.CrimeDbSchema;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);

        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public void deleteCrime(UUID crimeId) {
        mDatabase.delete(CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.CrimeTable.Columns.UUID + " = ?",
                new String[] { crimeId.toString() });
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        // Query all crimes
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        // Iterate through crimes in the DB using cursor
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        // Close cursor once try block is complete
        finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID uuid) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeDbSchema.CrimeTable.Columns.UUID + " = ?",
                new String[] { uuid.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        }
        finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Columns.UUID + " = ?",
                new String[] { uuidString });
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null // order by
        );

        return new CrimeCursorWrapper(cursor);
    }

    public static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Columns.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Columns.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Columns.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Columns.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Columns.CALL_POLICE, crime.requiresPolice() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Columns.SUSPECT, crime.getSuspect());

        return values;
    }
}
