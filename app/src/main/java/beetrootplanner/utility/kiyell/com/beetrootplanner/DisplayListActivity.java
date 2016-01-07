package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        tView.setText("Displaying " + dataTitle + getIntent().getStringExtra("header_sub"));// + whereValue);
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
            case "assessments": c = db.getSubset(dataTitle,whereValue);
                break;
            case "mentors": c = db.getSubset(dataTitle,whereValue);
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
                case "assessments": buildAssessmentAdder();
                    break;
                case "mentors": buildMentorAdder();
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
                EditText courseTitle, courseStart, courseEnd, courseNotes;
                Spinner courseStatus;
                courseTitle = (EditText) d.findViewById(R.id.course_title);
                courseStart = (EditText) d.findViewById(R.id.course_start);
                courseEnd = (EditText) d.findViewById(R.id.course_end);
                courseStatus = (Spinner) d.findViewById(R.id.course_status);
                courseNotes = (EditText) d.findViewById(R.id.course_notes);


                StringBuilder output = new StringBuilder();
                output.append(courseTitle.getText() + " ");
                output.append(courseStart.getText() + " ");
                output.append(courseEnd.getText() + " ");
                output.append(courseStatus.getSelectedItem().toString() + " ");
                output.append(" WHERE value is " + whereValue);


                Toast.makeText(d.getContext(), "Inserting the data: " + output, Toast.LENGTH_LONG).show();
                db.open();
                long course_id = db.addCourse(courseTitle.getText().toString(), courseStart.getText().toString(), courseEnd.getText().toString(), courseStatus.getSelectedItem().toString(), courseNotes.getText().toString(), whereValue);
                Toast.makeText(d.getContext(), "course_id created is: " + course_id, Toast.LENGTH_LONG).show();
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
        EditText courseTitle, courseStart, courseEnd, courseNotes;
        Spinner courseStatus;
        courseTitle = (EditText) v.findViewById(R.id.course_title);
        courseStart = (EditText) v.findViewById(R.id.course_start);
        courseEnd = (EditText) v.findViewById(R.id.course_end);
        courseStatus = (Spinner) v.findViewById(R.id.course_status);
        courseNotes = (EditText) v.findViewById(R.id.course_notes);

        db.open();
        Cursor result = db.getRow("courses",where);


        if (result.moveToFirst()) {
            courseTitle.setText(result.getString(1));
            courseStart.setText(result.getString(2));
            courseEnd.setText(result.getString(3));
            courseStatus.setSelection(((ArrayAdapter<String>) courseStatus.getAdapter()).getPosition(result.getString(4)));
            courseNotes.setText(result.getString(5));
        }

        db.close();
        builder.setView(v);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Dialog d = (Dialog) dialog;
                EditText courseTitle, courseStart, courseEnd, courseNotes;
                Spinner courseStatus;
                courseTitle = (EditText) d.findViewById(R.id.course_title);
                courseStart = (EditText) d.findViewById(R.id.course_start);
                courseEnd = (EditText) d.findViewById(R.id.course_end);
                courseStatus = (Spinner) d.findViewById(R.id.course_status);
                courseNotes = (EditText) d.findViewById(R.id.course_notes);



                StringBuilder output = new StringBuilder();
                output.append(courseTitle.getText() + " ");
                output.append(courseStart.getText() + " ");
                output.append(courseEnd.getText() + " ");
                output.append(courseStatus.getSelectedItem().toString() + " ");

                Toast.makeText(d.getContext(), "Updating the data: " + output, Toast.LENGTH_LONG).show();

                db.open();
                long course_id = db.updateCourse(courseTitle.getText().toString(), courseStart.getText().toString(), courseEnd.getText().toString(), courseStatus.getSelectedItem().toString(), courseNotes.getText().toString(), where);
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
        builder.setMessage("Edit a Course")
                .setTitle("Edit Course");
        dialog = builder.create();
        dialog.show();
    }

    public void buildAssessmentAdder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_add_assessment, null);

       /*
        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) dialogView.findViewById(R.id.assessment_photo_note);
                text.setText("Image added!");

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 1);


            }
        }); */
        builder.setView(dialogView);




        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Dialog d = (Dialog) dialog;
                EditText  assessmentDueDate, assessmentTitle;
                Spinner assessmentType;

                //TextView assessmentPhoto = (TextView) d.findViewById(R.id.assessment_photo_note);


                assessmentType = (Spinner) d.findViewById(R.id.assessment_type);
                assessmentDueDate = (EditText) d.findViewById(R.id.assessment_due_date);
                assessmentDueDate.setInputType(InputType.TYPE_NULL);

              //  long dateTime = assessmentDueDate.getCalendarView().getDate();
              //  Date date = new Date(dateTime);

                assessmentTitle = (EditText) d.findViewById(R.id.assessment_title);


                StringBuilder output = new StringBuilder();
                output.append(assessmentTitle.getText() + " ");
                output.append(assessmentType.getSelectedItem().toString() + " ");
                output.append(assessmentDueDate.getText() + " ");
                output.append(" WHERE value is " + whereValue);


                Toast.makeText(d.getContext(), "Inserting the data: " + output, Toast.LENGTH_LONG).show();
                db.open();
                long assessment_id = db.addAssessment(assessmentTitle.getText().toString(), assessmentType.getSelectedItem().toString(), assessmentDueDate.getText().toString(), "N/A", whereValue);
                Toast.makeText(d.getContext(), "assessment_id created is: " + assessment_id, Toast.LENGTH_LONG).show();
                db.close();
                populateListFromSql();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage("Add a new Assessment")
                .setTitle("Add Assessment");
        dialog = builder.create();
        dialog.show();
    }

    public void buildAssessmentEditor(String wh) {
        final String where = wh;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_add_assessment, null);
        EditText assessmentTitle, assessmentDueDate;
        Spinner assessmentType;

        assessmentTitle = (EditText) v.findViewById(R.id.assessment_title);
        assessmentType = (Spinner) v.findViewById(R.id.assessment_type);
        assessmentDueDate = (EditText) v.findViewById(R.id.assessment_due_date);

        db.open();
        Cursor result = db.getRow("assessments",where);

        Toast.makeText(this.getBaseContext(), "editor where is " + where, Toast.LENGTH_LONG).show();


        if (result.moveToFirst()) {
            assessmentTitle.setText(result.getString(1));
            assessmentType.setSelection(((ArrayAdapter<String>) assessmentType.getAdapter()).getPosition(result.getString(2)));
            assessmentDueDate.setText(result.getString(3));
        }

        db.close();
        builder.setView(v);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Dialog d = (Dialog) dialog;
                EditText  assessmentDueDate, assessmentTitle;
                Spinner assessmentType;

                //TextView assessmentPhoto = (TextView) d.findViewById(R.id.assessment_photo_note);


                assessmentType = (Spinner) d.findViewById(R.id.assessment_type);
                assessmentDueDate = (EditText) d.findViewById(R.id.assessment_due_date);
                assessmentDueDate.setInputType(InputType.TYPE_NULL);

                //  long dateTime = assessmentDueDate.getCalendarView().getDate();
                //  Date date = new Date(dateTime);

                assessmentTitle = (EditText) d.findViewById(R.id.assessment_title);


                StringBuilder output = new StringBuilder();
                output.append(assessmentTitle.getText() + " ");
                output.append(assessmentType.getSelectedItem().toString() + " ");
                output.append(assessmentDueDate.getText() + " ");
                output.append(" WHERE value is " + whereValue);


                Toast.makeText(d.getContext(), "Updating the data: " + output, Toast.LENGTH_LONG).show();
                db.open();
                long assessment_id = db.updateAssessment(assessmentTitle.getText().toString(), assessmentType.getSelectedItem().toString(), assessmentDueDate.getText().toString(), "N/A", where);
                Toast.makeText(d.getContext(), "assessment_id updated is: " + assessment_id, Toast.LENGTH_LONG).show();
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
        builder.setMessage("Edit an Assessment")
                .setTitle("Edit Assessment");
        dialog = builder.create();
        dialog.show();
    }

    public void buildMentorAdder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_mentor, null));

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Dialog d = (Dialog) dialog;
                EditText mentorName, mentorPhone, mentorEmail;
                mentorName = (EditText) d.findViewById(R.id.mentor_name);
                mentorPhone = (EditText) d.findViewById(R.id.mentor_phone);
                mentorEmail = (EditText) d.findViewById(R.id.mentor_email);

                StringBuilder output = new StringBuilder();
                output.append(mentorName.getText() + " ");
                output.append(mentorPhone.getText() + " ");
                output.append(mentorEmail.getText() + " ");
                output.append(" WHERE value is " + whereValue);


                Toast.makeText(d.getContext(), "Inserting the data: " + output, Toast.LENGTH_LONG).show();
                db.open();
                long mentor_id = db.addMentor(mentorName.getText().toString(), mentorPhone.getText().toString(), mentorEmail.getText().toString(), whereValue);
                Toast.makeText(d.getContext(), "mentor_id created is: " + mentor_id, Toast.LENGTH_LONG).show();
                db.close();
                populateListFromSql();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage("Add a new Mentor")
                .setTitle("Add Mentor");
        dialog = builder.create();
        dialog.show();
    }

    public void buildMentorEditor(String wh) {
        final String where = wh;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_mentor, null);

        EditText mentorName, mentorPhone, mentorEmail;
        mentorName = (EditText) dialogView.findViewById(R.id.mentor_name);
        mentorPhone = (EditText) dialogView.findViewById(R.id.mentor_phone);
        mentorEmail = (EditText) dialogView.findViewById(R.id.mentor_email);

        db.open();
        Cursor result = db.getRow("mentors",where);

        Toast.makeText(this.getBaseContext(), "editor where is " + where, Toast.LENGTH_LONG).show();


        if (result.moveToFirst()) {
            mentorName.setText(result.getString(1));
            mentorPhone.setText(result.getString(2));
            mentorEmail.setText(result.getString(3));
        }

        db.close();


        builder.setView(dialogView);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Dialog d = (Dialog) dialog;
                EditText mentorName, mentorPhone, mentorEmail;
                mentorName = (EditText) d.findViewById(R.id.mentor_name);
                mentorPhone = (EditText) d.findViewById(R.id.mentor_phone);
                mentorEmail = (EditText) d.findViewById(R.id.mentor_email);

                StringBuilder output = new StringBuilder();
                output.append(mentorName.getText() + " ");
                output.append(mentorPhone.getText() + " ");
                output.append(mentorEmail.getText() + " ");
                output.append(" WHERE value is " + whereValue);


                Toast.makeText(d.getContext(), "Inserting the data: " + output, Toast.LENGTH_LONG).show();
                db.open();
                long mentor_id = db.updateMentor(mentorName.getText().toString(), mentorPhone.getText().toString(), mentorEmail.getText().toString(), where);
                Toast.makeText(d.getContext(), "mentor_id update is: " + mentor_id, Toast.LENGTH_LONG).show();
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
        builder.setMessage("Edit a Mentor")
                .setTitle("Edit Mentor");
        dialog = builder.create();
        dialog.show();
    }

    public void deleteData(View v) {

     //       Intent intent = new Intent(this, DisplayListActivity.class);
     //       startActivity(intent);
            setMode(DELETE_MODE);
    }

    public void getDateDialog(View v) {
       // Toast.makeText(v.getContext(), "Clicked on edit text", Toast.LENGTH_LONG).show();
        final EditText callingText = (EditText) v;
        Calendar initialCalendar = Calendar.getInstance();


        DatePickerDialog d = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                String formatedDate = new SimpleDateFormat("MMMM dd yyyy").format(newDate.getTime());
                callingText.setText(formatedDate); /// USE TO GET LONG FROM DATABASE: new SimpleDateFormat("MMMM dd yyyy").parse(string);

            }
        },initialCalendar.get(Calendar.YEAR), initialCalendar.get(Calendar.MONTH), initialCalendar.get(Calendar.DAY_OF_MONTH));

        d.show();
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
            addButton.setText("Add");
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
                case "terms": intentExt = new String[] {"terms", "term_id", String.valueOf(e.rowid), " "}; //{"courses", "course_id", String.valueOf(e.rowid), " in term "};
                    break;
                case "courses": intentExt = new String[] {"courses", "course_id", String.valueOf(e.rowid), " "};//{"mentors", "mentor_id", String.valueOf(e.rowid), " in course "}; // {"assessments", "assessment_id", String.valueOf(e.rowid), " in course "};
                    break;
                case "assessments": intentExt = new String[] {"mentors", "mentor_id", String.valueOf(e.rowid), " in course "};
                    break;
                case "mentors": intentExt = new String[] {"terms", "term_id", String.valueOf(e.rowid), " in course "};
                    break;
            }


            //Intent intent = new Intent(this, DisplayListActivity.class);

            Intent intent = new Intent(this, DetailActivity.class);
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
                case "assessments": buildAssessmentEditor(String.valueOf(e.rowid));
                    break;
                case "mentors": buildMentorEditor(String.valueOf(e.rowid));
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