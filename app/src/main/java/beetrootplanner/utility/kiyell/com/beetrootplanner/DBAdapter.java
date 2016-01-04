package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    static final String KEY_ROWID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_EMAIL = "email";
    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "contacts";
    static final int DATABASE_VERSION = 1;
    static final String TERMS_TABLE_CREATE =
            "create table terms (term_id integer primary key autoincrement, term_title text not null, term_start text not null, term_end text not null);";

    static final String COURSES_TABLE_CREATE = "create table courses (course_id integer primary key autoincrement, course_title text not null, course_start text not null," +
            " course_end text not null, course_status text not null, term_id integer not null, foreign key (term_id) references terms(term_id) ON DELETE NO ACTION);";
    /*"create table contacts (_id integer primary key autoincrement, "
            + "name text not null, email text not null);";*/



    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(TERMS_TABLE_CREATE);
                db.execSQL(COURSES_TABLE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion +"to"
            +newVersion +",which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }
    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //-- Insert Term Data
    public long addTerm(String ttl, String st, String ed)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("term_title", ttl);
        initialValues.put("term_start", st);
        initialValues.put("term_end", ed);
        return db.insert("terms", null, initialValues);
    }

    public long addCourse(String ttl, String st, String ed, String stat, String id)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("course_title", ttl);
        initialValues.put("course_start", st);
        initialValues.put("course_end", ed);
        initialValues.put("course_status", stat);
        initialValues.put("term_id", id);
        return db.insert("courses", null, initialValues);
    }

    public void delete(String table,String pk, long rowId) {
        db.delete(table, pk + "=" + rowId, null);
    }
    //-- Update Term Data
    public long updateTerm(String ttl, String st, String ed, String rowid)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put("term_title", ttl);
        updateValues.put("term_start", st);
        updateValues.put("term_end", ed);
        return db.update("terms", updateValues, "term_id = "+rowid, null);
    }

    //-- Retrieve Term Data
    public Cursor getAllTerms()
    {
        return db.query("terms", new String[] {"term_id", "term_title",
                "term_start", "term_end"}, null, null, null, null, null);
    }

    public Cursor getSubset(String dt, String wv)
    {
        if (dt.equals("courses")) {
            return db.query("courses", new String[] {"course_id", "course_title",
                    "course_start", "course_end", "course_status", "term_id"}, "term_id = "+wv, null, null, null, null); //wpk+" = "+wv
        }

        return null;
    }

    public Cursor getRow(String dt, String wh) {

        if (dt.equals("terms")) {
            return db.query("terms", new String[] {"term_id", "term_title",
                    "term_start", "term_end"}, "term_id = "+wh, null, null, null, null);
        }

        return null;
    }
    //---deletes a particular contact---
    public boolean deleteContact(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }






}

