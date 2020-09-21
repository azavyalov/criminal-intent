package com.azavyalov.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.azavyalov.criminalintent.database.CrimeBaseHelper;
import com.azavyalov.criminalintent.database.CrimeCursorWrapper;
import com.azavyalov.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.azavyalov.criminalintent.database.CrimeDbSchema.CrimeTable.Cols;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.azavyalov.criminalintent.database.CrimeDbSchema.CrimeTable.Cols.DATE;
import static com.azavyalov.criminalintent.database.CrimeDbSchema.CrimeTable.Cols.SOLVED;
import static com.azavyalov.criminalintent.database.CrimeDbSchema.CrimeTable.Cols.SUSPECT;
import static com.azavyalov.criminalintent.database.CrimeDbSchema.CrimeTable.Cols.TITLE;
import static com.azavyalov.criminalintent.database.CrimeDbSchema.CrimeTable.Cols.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private SQLiteDatabase mDatabase;
    private Context mContext;

    public static CrimeLab getInstance(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id) {

        CrimeCursorWrapper cursor = queryCrimes(
                Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(
                CrimeTable.NAME,
                values,
                Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public void deleteCrime(Crime crime) {
        mDatabase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{crime.getId().toString()});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {

        Cursor cursor = mDatabase.query(CrimeTable.NAME,
                null, // - с null выбираются все столбцы
                whereClause,
                whereArgs,
                null,
                null,
                null);

        return new CrimeCursorWrapper(cursor);
    }

    // Converting crime object to content values
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(UUID, crime.getId().toString());
        values.put(TITLE, crime.getTitle());
        values.put(DATE, crime.getDate().getTime());
        values.put(SOLVED, crime.isSolved() ? 1 : 0);
        values.put(SUSPECT, crime.getSuspect());
        return values;
    }
}
