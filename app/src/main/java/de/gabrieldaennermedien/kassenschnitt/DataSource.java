package de.gabrieldaennermedien.kassenschnitt;

//imports
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DataSource manages the content/data of the database and provides methods to interact with it.
 */
class DataSource {
    //private instances
    private DbHelper       dbHelper;
    private SQLiteDatabase database;

    //private arrays
    private String[] queries = {
            ("select * from " + DbHelper.TABLE_DATA),
            ("select sum(" + DbHelper.COLUMN_MONEY + "),count(" + DbHelper.COLUMN_MONEY + ") from "
                    + DbHelper.TABLE_DATA),
            ("select sum(" + DbHelper.COLUMN_MONEY + "),count(" + DbHelper.COLUMN_MONEY + ") from "
                    + DbHelper.TABLE_DATA + " where " + DbHelper.COLUMN_YEAR + "=?"),
            ("select sum(" + DbHelper.COLUMN_MONEY + "),count(" + DbHelper.COLUMN_MONEY + ") from "
                    + DbHelper.TABLE_DATA + " where " + DbHelper.COLUMN_YEAR + "=? and "
                    + DbHelper.COLUMN_DAY + " like ?"),
            ("select * from " + DbHelper.TABLE_DATA + " where " + DbHelper.COLUMN_YEAR
                    + "=? order by " +  DbHelper.COLUMN_MONEY + " desc limit 10")
    };

    /**
     * Constructor.
     * @param context the application context.
     */
    DataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * open opens the database.
     */
    void open() {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * close closes the database.
     */
    void close() {
        dbHelper.close();
    }

    /**
     * createEntry creates a new entry in the database.
     * @param day the day of the entry.
     * @param year the year of the entry.
     * @param weekday the weekday of the entry.
     * @param money the money of the entry.
     */
    void createEntry(String day, String year, String weekday, double money) {
        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_DAY,     day);
        values.put(DbHelper.COLUMN_YEAR,    year);
        values.put(DbHelper.COLUMN_WEEKDAY, weekday);
        values.put(DbHelper.COLUMN_MONEY,   money);

        database.insert(DbHelper.TABLE_DATA, null, values);
    }

    /**
     * deleteEntry deletes an entry.
     * @param entry the entry which should be deleted.
     */
    void deleteEntry(DbEntry entry) {
        long id = entry.getId();

        database.delete(DbHelper.TABLE_DATA, DbHelper.COLUMN_ID + "=" + id, null);
    }

    /**
     * updateEntry creates a new entry in the database.
     * @param id the id of the entry (primary key).
     * @param new_day the new day of the entry.
     * @param new_year the new year of the entry.
     * @param new_weekday the new weekday of the entry.
     * @param new_money the new money of the entry.
     */
    void updateEntry(long id, String new_day, String new_year,
                            String new_weekday, double new_money) {
        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_DAY, new_day);
        values.put(DbHelper.COLUMN_YEAR, new_year);
        values.put(DbHelper.COLUMN_WEEKDAY, new_weekday);
        values.put(DbHelper.COLUMN_MONEY, new_money);

        database.update(DbHelper.TABLE_DATA, values, DbHelper.COLUMN_ID + "=" + id, null);
    }

    /**
     * cursorToEntry converts an entry of the db to an instance of DbEntry.
     * @param cursor the cursor to the specific entry.
     * @return the requested entry.
     */
    private DbEntry cursorToEntry(Cursor cursor) {
        int idIndex   = cursor.getColumnIndex(DbHelper.COLUMN_ID);
        int idDay     = cursor.getColumnIndex(DbHelper.COLUMN_DAY);
        int idYear    = cursor.getColumnIndex(DbHelper.COLUMN_YEAR);
        int idWeekday = cursor.getColumnIndex(DbHelper.COLUMN_WEEKDAY);
        int idMoney   = cursor.getColumnIndex(DbHelper.COLUMN_MONEY);

        String day     = cursor.getString(idDay);
        String year    = cursor.getString(idYear);
        String weekday = cursor.getString(idWeekday);
        double money   = cursor.getDouble(idMoney);
        long id        = cursor.getLong(idIndex);

        return new DbEntry(day, year, weekday, money, id);
    }

    private double[] convertListToDoubleArray(List<Double> list) {
        double[]         primitiveList = new double[list.size()];
        Iterator<Double> iterator      = list.iterator();

        for (int i = 0; i < primitiveList.length; i++) {
            primitiveList[i] = iterator.next();
        }

        return primitiveList;
    }

    /**
     * getAllEntries returns a list which contains all entries of the db.
     * @return a list which contains all entries.
     */
    List<DbEntry> getAllEntries() {
        ArrayList<DbEntry> dbEntryList = new ArrayList<>();
        Cursor             cursor      = database.rawQuery(queries[0], null);

        cursor.moveToFirst();
        DbEntry dbEntry;

        while(!cursor.isAfterLast()) {
            dbEntry = cursorToEntry(cursor);
            dbEntryList.add(dbEntry);
            cursor.moveToNext();
        }

        cursor.close();

        return dbEntryList;
    }

    double[] getWholeMoneyAsSum() {
        List<Double> returnList = new ArrayList<>();
        Cursor       cursor     = database.rawQuery(queries[1], null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            returnList.add(cursor.getDouble(1));
            returnList.add(cursor.getDouble(0));
            cursor.moveToNext();
        }

        cursor.close();

        return convertListToDoubleArray(returnList);
    }

    double[] getYearlyMoneyAsSum(String year) {
        List<Double> returnList = new ArrayList<>();
        Cursor       cursor     = database.rawQuery(queries[2], new String[]{year.trim()});

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            returnList.add(cursor.getDouble(1));
            returnList.add(cursor.getDouble(0));
            cursor.moveToNext();
        }

        cursor.close();

        return convertListToDoubleArray(returnList);
    }

    double[] getMonthlyMoneyAsSum(String month, String year) {
        List<Double> returnList = new ArrayList<>();
        Cursor       cursor     = database.rawQuery(queries[3], new String[]{year.trim(),
                "%" + StringConversionHelper.monthToNumber(month)});

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            returnList.add(cursor.getDouble(1));
            returnList.add(cursor.getDouble(0));
            cursor.moveToNext();
        }

        cursor.close();

        return convertListToDoubleArray(returnList);
    }

    List<DbEntry> getBestDaysInYear(String year) {
        ArrayList<DbEntry> dbEntryList = new ArrayList<>();
        Cursor             cursor      = database.rawQuery(queries[4], new String[]{year.trim()});

        cursor.moveToFirst();
        DbEntry dbEntry;

        while(!cursor.isAfterLast()) {
            dbEntry = cursorToEntry(cursor);
            dbEntryList.add(dbEntry);
            cursor.moveToNext();
        }

        cursor.close();

        return dbEntryList;
    }
}
