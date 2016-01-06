package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBAdapter {

    static final String KEY_ROWID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_EMAIL = "email";
    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "contacts";
    static final int DATABASE_VERSION = 2;
    static final String TERMS_TABLE_CREATE =
            "create table terms (term_id integer primary key autoincrement, term_title text not null, term_start text not null, term_end text not null);";

    static final String COURSES_TABLE_CREATE = "create table courses (course_id integer primary key autoincrement, course_title text not null, course_start text not null," +
            " course_end text not null, course_status text not null, course_notes text, term_id integer not null, foreign key (term_id) references terms(term_id) ON DELETE NO ACTION);";

    static final String ASSESSMENT_TABLE_CREATE = "create table assessments (assessment_id integer primary key autoincrement, assessment_title text not null, assessment_type text not null, assessment_due text not null," +
            " assessment_photo_note text not null, course_id integer not null, foreign key (course_id) references courses(course_id) ON DELETE CASCADE);";

    static final String MENTORS_TABLE_CREATE = "create table mentors (mentor_id integer primary key autoincrement, mentor_name text not null, mentor_phone text not null," +
            " mentor_email text not null, course_id integer not null, foreign key (course_id) references courses(course_id) ON DELETE CASCADE);";



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
                db.execSQL(ASSESSMENT_TABLE_CREATE);
                db.execSQL(MENTORS_TABLE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion +"to"
            +newVersion +",which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS terms");
            db.execSQL("DROP TABLE IF EXISTS courses");
            db.execSQL("DROP TABLE IF EXISTS assessments");
            db.execSQL("DROP TABLE IF EXISTS mentors");
            onCreate(db);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
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

    public long addCourse(String ttl, String st, String ed, String stat, String not, String id)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("course_title", ttl);
        initialValues.put("course_start", st);
        initialValues.put("course_end", ed);
        initialValues.put("course_status", stat);
        initialValues.put("course_notes", not);
        initialValues.put("term_id", id);
        return db.insert("courses", null, initialValues);
    }

    public long addAssessment(String ttl, String typ, String due, String pht, String id)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("assessment_title", ttl);
        initialValues.put("assessment_type", typ);
        initialValues.put("assessment_due", due);
        initialValues.put("assessment_photo_note", pht);
        initialValues.put("course_id", id);
        return db.insert("assessments", null, initialValues);
    }

    public long addMentor(String nm, String ph, String em, String id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("mentor_name", nm);
        initialValues.put("mentor_phone", ph);
        initialValues.put("mentor_email", em);
        initialValues.put("course_id", id);
        return db.insert("mentors", null, initialValues);
    }

    public void delete(String table,String pk, long rowId) {
        try {
            db.delete(table, pk + "=" + rowId, null);
        } catch(SQLiteConstraintException sec) {
            Toast.makeText(this.context, "Unable to delete item from " + table + " because it is not empty ", Toast.LENGTH_LONG).show();
        }

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

    public long updateCourse(String ttl, String st, String ed, String stat, String not, String rowid)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put("course_title", ttl);
        updateValues.put("course_start", st);
        updateValues.put("course_end", ed);
        updateValues.put("course_status", stat);
        updateValues.put("course_notes", not);
        return db.update("courses", updateValues, "course_id = " + rowid, null);
    }

    public long updateAssessment(String ttl, String typ, String due, String pht, String rowid)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put("assessment_title", ttl);
        updateValues.put("assessment_due", due);
        updateValues.put("assessment_type", typ);
        updateValues.put("assessment_photo_note", pht);
        return db.update("assessments", updateValues, "assessment_id = " + rowid, null);
    }

    public long updateMentor(String nm, String ph, String em, String id, String rowid){
        ContentValues updateValues = new ContentValues();
        updateValues.put("mentor_name", nm);
        updateValues.put("mentor_phone", ph);
        updateValues.put("mentor_email", em);
        updateValues.put("course_id", id);
        return db.update("mentors", updateValues, "mentor_id = " + rowid, null);
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
                    "course_start", "course_end", "course_status", "course_notes", "term_id"}, "term_id = "+wv, null, null, null, null); //wpk+" = "+wv
        }
        if (dt.equals("assessments")) {
            return db.query("assessments", new String[] {"assessment_id", "assessment_title", "assessment_type",
                    "assessment_due", "assessment_photo_note", "course_id"}, "course_id = "+wv, null, null, null, null); //wpk+" = "+wv
        }
        if (dt.equals("mentors")) {
            return db.query("mentors", new String[] {"mentor_id", "mentor_name", "mentor_phone",
                    "mentor_email", "course_id"}, "course_id = "+wv, null, null, null, null); //wpk+" = "+wv
        }

        return null;
    }

    public Cursor getRow(String dt, String wh) {

        if (dt.equals("terms")) {
            return db.query("terms", new String[] {"term_id", "term_title",
                    "term_start", "term_end"}, "term_id = "+wh, null, null, null, null);
        }
        if (dt.equals("courses")) {
            return db.query("courses", new String[] {"course_id", "course_title",
                    "course_start", "course_end", "course_status", "course_notes"}, "course_id = "+wh, null, null, null, null);
        }
        if (dt.equals("assessments")) {
            return db.query("assessments", new String[] {"assessment_id", "assessment_title", "assessment_type",
                    "assessment_due", "assessment_photo_note", "course_id"}, "assessment_id = "+wh, null, null, null, null);
        }
        if (dt.equals("mentors")) {
            return db.query("mentors", new String[] {"mentor_id", "mentor_name", "mentor_phone",
                    "mentor_email", "course_id"}, "mentor_id = "+wh, null, null, null, null); //wpk+" = "+wv
        }

        return null;
    }






}

