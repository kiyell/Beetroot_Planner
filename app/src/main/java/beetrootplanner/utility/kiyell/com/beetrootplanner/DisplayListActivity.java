package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplayListActivity extends ListActivity {


    ArrayList<String> results = new ArrayList<>();
    ArrayList<DBListEntry> dblist = new ArrayList<>();
    DBAdapter db;

    static int VIEW_MODE = 0;
    static int DELETE_MODE = 2;
    static int EDIT_MODE = 3;
    int currentMode;
    String dataTitle;
    String wherePK;
    String whereValue;

    AlertDialog dialog;
    Intent nextPage;
    String[] intentExt;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final Context c = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_list);
        db = new DBAdapter(this);

        dataTitle= getIntent().getStringExtra("table");
        wherePK = getIntent().getStringExtra("where_pk");
        whereValue = getIntent().getStringExtra("where_value");

        TextView tView = new TextView(this);
        tView.setText("Displaying " + dataTitle + getIntent().getStringExtra("header_sub")+whereValue);
        tView.setOnClickListener(null);
        getListView().addHeaderView(tView);




        currentMode = VIEW_MODE;
        populateListFromSql();




    }

    private void populateListFromSql() {

        db.open();

        Cursor c = null;

        switch (dataTitle) {
            case "terms": c = db.getAllTerms();
                break;
            case "courses": c = db.getSubset(dataTitle,whereValue);
                break;
            case "assessments": //retrieveAssessments(where_pk);
                break;
            case "mentors": //retrieveMentors(where_pk);
                break;
        }



        dblist.clear();
        if (c.moveToFirst())
        {
            do {
                dblist.add(new DBListEntry(c.getString(1)+" : "+c.getString(2)+" - "+c.getString(3),c.getLong(0)));
            } while (c.moveToNext());
        }
        db.close();

        ArrayAdapter la = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        for (DBListEntry item : dblist) {
            la.add(item);
        }
        setListAdapter(la);
    }




    public void addData(View v) {

        if(currentMode == DELETE_MODE || currentMode == EDIT_MODE) {
            setMode(VIEW_MODE);
        } else {


            switch (dataTitle) {
                case "terms": buildTermAdder();
                    break;
                case "courses": buildCourseAdder();
                    break;
                case "assessments":
                    break;
                case "mentors":
                    break;
            }


        }

    }

    public void buildTermAdder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_term, null));

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Dialog d = (Dialog) dialog;
                EditText termTitle, termStart, termEnd;
                termTitle = (EditText) d.findViewById(R.id.term_title);
                termStart = (EditText) d.findViewById(R.id.term_start);
                termEnd = (EditText) d.findViewById(R.id.term_end);

                StringBuilder output = new StringBuilder();
                output.append(termTitle.getText() + " ");
                output.append(termStart.getText() + " ");
                output.append(termEnd.getText() + " ");

                Toast.makeText(d.getContext(), "Inserting the data: " + output, Toast.LENGTH_LONG).show();
                db.open();
                long term_id = db.addTerm(termTitle.getText().toString(), termStart.getText().toString(), termEnd.getText().toString());
                db.close();
                populateListFromSql();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage("Add a new Term")
                .setTitle("Add Term");
        dialog = builder.create();
        dialog.show();
    }

    public void buildTermEditor(String wh) {
        final String where = wh;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_add_term, null);
        EditText termTitle, termStart, termEnd;

        termTitle = (EditText) v.findViewById(R.id.term_title);
        termStart = (EditText) v.findViewById(R.id.term_start);
        termEnd = (EditText) v.findViewById(R.id.term_end);

        db.open();
        Cursor result = db.getRow("terms",where);


            if (result.moveToFirst()) {
                termTitle.setText(result.getString(1));
                termStart.setText(result.getString(2));
                termEnd.setText(result.getString(3));
            }

        db.close();
        builder.setView(v);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Dialog d = (Dialog) dialog;
                EditText termTitle, termStart, termEnd;
                termTitle = (EditText) d.findViewById(R.id.term_title);
                termStart = (EditText) d.findViewById(R.id.term_start);
                termEnd = (EditText) d.findViewById(R.id.term_end);
                /*
                db.open();
                db.getRow("terms",where);
                db.close();
                */




                StringBuilder output = new StringBuilder();
                output.append(termTitle.getText() + " ");
                output.append(termStart.getText() + " ");
                output.append(termEnd.getText() + " ");

                Toast.makeText(d.getContext(), "Updating the data: " + output, Toast.LENGTH_LONG).show();

                db.open();
                long term_id = db.updateTerm(termTitle.getText().toString(), termStart.getText().toString(), termEnd.getText().toString(), where);
                db.close();
                populateListFromSql();
                setMode(VIEW_MODE);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                setMode(VIEW_MODE);
            }
        });
        builder.setMessage("Edit a Term")
                .setTitle("Edit Term");
        dialog = builder.create();
        dialog.show();
    }

    public void buildCourseAdder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_course, null));

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Dialog d = (Dialog) dialog;
                EditText courseTitle, courseStart, courseEnd, courseStatus;
                courseTitle = (EditText) d.findViewById(R.id.course_title);
                courseStart = (EditText) d.findViewById(R.id.course_start);
                courseEnd = (EditText) d.findViewById(R.id.course_end);
                courseStatus = (EditText) d.findViewById(R.id.course_status);

                StringBuilder output = new StringBuilder();
                output.append(courseTitle.getText() + " ");
                output.append(courseStart.getText() + " ");
                output.append(courseEnd.getText() + " ");
                output.append(courseStatus.getText() + " ");
                output.append(" WHERE value is " + whereValue);


                Toast.makeText(d.getContext(), "Inserting the data: " + output, Toast.LENGTH_LONG).show();
                db.open();
                long term_id = db.addCourse(courseTitle.getText().toString(), courseStart.getText().toString(), courseEnd.getText().toString(), courseStatus.getText().toString(), whereValue);
                Toast.makeText(d.getContext(), "term_id created is: " + term_id, Toast.LENGTH_LONG).show();
                db.close();
                populateListFromSql();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage("Add a new Course")
                .setTitle("Add Course");
        dialog = builder.create();
        dialog.show();
    }

    public void buildCourseEditor(String wh) {
        final String where = wh;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_add_course, null);
        EditText courseTitle, courseStart, courseEnd, courseStatus;
        courseTitle = (EditText) v.findViewById(R.id.course_title);
        courseStart = (EditText) v.findViewById(R.id.course_start);
        courseEnd = (EditText) v.findViewById(R.id.course_end);
        courseStatus = (EditText) v.findViewById(R.id.course_status);

        db.open();
        Cursor result = db.getRow("courses",where);


        if (result.moveToFirst()) {
            courseTitle.setText(result.getString(1));
            courseStart.setText(result.getString(2));
            courseEnd.setText(result.getString(3));
            courseStatus.setText(result.getString(4));
        }

        db.close();
        builder.setView(v);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Dialog d = (Dialog) dialog;
                EditText courseTitle, courseStart, courseEnd, courseStatus;
                courseTitle = (EditText) d.findViewById(R.id.course_title);
                courseStart = (EditText) d.findViewById(R.id.course_start);
                courseEnd = (EditText) d.findViewById(R.id.course_end);
                courseStatus = (EditText) d.findViewById(R.id.course_status);


                StringBuilder output = new StringBuilder();
                output.append(courseTitle.getText() + " ");
                output.append(courseStart.getText() + " ");
                output.append(courseEnd.getText() + " ");
                output.append(courseStatus.getText() + " ");

                Toast.makeText(d.getContext(), "Updating the data: " + output, Toast.LENGTH_LONG).show();

                db.open();
                long term_id = db.updateCourse(courseTitle.getText().toString(), courseStart.getText().toString(), courseEnd.getText().toString(), courseStatus.getText().toString(), where);
                db.close();
                populateListFromSql();
                setMode(VIEW_MODE);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                setMode(VIEW_MODE);
            }
        });
        builder.setMessage("Edit a Term")
                .setTitle("Edit Term");
        dialog = builder.create();
        dialog.show();
    }

    public void deleteData(View v) {

     //       Intent intent = new Intent(this, DisplayListActivity.class);
     //       startActivity(intent);
            setMode(DELETE_MODE);
    }

    public void editData(View v) {
        setMode(EDIT_MODE);
    }

    public void setMode(int mode) {
        currentMode = mode;

        if(mode == DELETE_MODE || mode == EDIT_MODE) {
            //change button to Confirm delete
            //Change layout
            Button deleteButton = (Button) findViewById(R.id.button_delete);
       //     deleteButton.setText("Confirm Delete");
            deleteButton.setEnabled(false);

            Button editButton = (Button) findViewById(R.id.button_edit);
            //     deleteButton.setText("Confirm Delete");
            editButton.setEnabled(false);

            Button cancelButton = (Button) findViewById(R.id.button_add);
            cancelButton.setText("Done");
        }
        if(mode == VIEW_MODE) {
            Button deleteButton = (Button) findViewById(R.id.button_delete);
        //    deleteButton.setText("Delete");
            deleteButton.setEnabled(true);

            Button editButton = (Button) findViewById(R.id.button_edit);
            //     deleteButton.setText("Confirm Delete");
            editButton.setEnabled(true);

            Button addButton = (Button) findViewById(R.id.button_add);
            addButton.setText("Add Term");
        }
    }


    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    //    Object o = this.getListAdapter().getItem(position-1);
     //   String selected = o.toString();
        DBListEntry e = (DBListEntry) this.getListAdapter().getItem(position-1);
       // Toast.makeText(this, "You have chosen the "+wherePK+": " + " " + e.rowid, Toast.LENGTH_LONG).show();

        if (currentMode == VIEW_MODE ) { // && !(dataTitle.equals("assessments") || dataTitle.equals("mentors")) PUTBACK AFTER DETAIL VIEW IMPLEMENTED

            switch (dataTitle) {
                case "terms": intentExt = new String[] {"courses", "course_id", String.valueOf(e.rowid), " in term "};
                    break;
                case "courses": intentExt = new String[] {"assessments", "assessment_id", String.valueOf(e.rowid), " in course "};
                    break;
                case "assessments": intentExt = new String[] {"mentors", "mentor_id", String.valueOf(e.rowid), " in course "};
                    break;
                case "mentors": intentExt = new String[] {"terms", "term_id", String.valueOf(e.rowid), " in course "};
                    break;
            }


            Intent intent = new Intent(this, DisplayListActivity.class);
            intent.putExtra("table",intentExt[0]);
            intent.putExtra("where_pk",intentExt[1]);
            intent.putExtra("where_value", intentExt[2]);
            intent.putExtra("header_sub", intentExt[3]);
            startActivity(intent);
        }

        if (currentMode == DELETE_MODE) {
            db.open();

            switch (dataTitle) {
                case "terms": db.delete("terms", "term_id", e.rowid);
                    break;
                case "courses": db.delete("courses", "course_id", e.rowid);
                    break;
                case "assessments": db.delete("assessments", "assessment_id", e.rowid);
                    break;
                case "mentors": db.delete("mentors", "mentor_id", e.rowid);
                    break;
            }


            db.close();

            populateListFromSql();
        }

        if (currentMode == EDIT_MODE) {

            switch (dataTitle) {
                case "terms": buildTermEditor(String.valueOf(e.rowid));
                    break;
                case "courses": buildCourseEditor(String.valueOf(e.rowid));
                    break;
                case "assessments":
                    break;
                case "mentors":
                    break;
            }

        }
    }

    public class DBListEntry {
        String display;
        long rowid;
        public DBListEntry(String s, long l) {
            display = s;
            rowid = l;
        }
        @Override
        public String toString() {
            return display;
        }
    }
}