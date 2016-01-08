package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.content.Intent;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetailActivity extends AppCompatActivity  implements
        ShareActionProvider.OnShareTargetSelectedListener{

    String dataTitle;
    String wherePK;
    String whereValue;
    String previousTitle;

    String[] intentExt;
    ArrayList<String> results = new ArrayList<>();
    DBAdapter db;
    TextView title,start, end;
    String beginTime, endTime, eventTitle;
    private ShareActionProvider mShareActionProvider;
    private Intent shareIntent=new Intent(Intent.ACTION_SEND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataTitle= getIntent().getStringExtra("table");
        wherePK = getIntent().getStringExtra("where_pk");
        whereValue = getIntent().getStringExtra("where_value");
        db = new DBAdapter(this);
        beginTime = "invalid";
        endTime = "invalid";

        switch (dataTitle) {
            case "terms": setupTermView();
                break;
            case "courses": setupCourseView();
                break;
            case "assessments": setupAssessmentView();
                break;
            case "mentors": setupMentorView();
                break;
        }
      //  this.getSupportActionBar().setHomeButtonEnabled(true);
      //  this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void openList(View v) {

        switch (dataTitle) {
            case "terms": intentExt = new String[] {"courses", "course_id", whereValue, " in term: "+previousTitle}; //{"courses", "course_id", String.valueOf(e.rowid), " in term "};
                break;
            case "courses": {

                if (v.getId() == R.id.button_mentors) {
                    intentExt = new String[] {"mentors", "mentor_id", whereValue, " in course: "+previousTitle}; // {"assessments", "assessment_id", String.valueOf(e.rowid), " in course "};
                }
                if (v.getId() == R.id.button_assessments) {
                    intentExt = new String[] {"assessments", "assessment_id", whereValue, " in course: "+previousTitle}; // {"assessments", "assessment_id", String.valueOf(e.rowid), " in course "};
                }

            }
                break;
            case "assessments": //intentExt = new String[] {"mentors", "mentor_id", whereValue, " in course "};
                break;
            case "mentors": //intentExt = new String[] {"terms", "term_id", whereValue, " in course "};
                break;
        }


        Intent intent = new Intent(this, DisplayListActivity.class);

        intent.putExtra("table", intentExt[0]);
        intent.putExtra("where_pk",intentExt[1]);
        intent.putExtra("where_value", intentExt[2]);
        intent.putExtra("header_sub", intentExt[3]);
        finish();
        startActivity(intent);
    }

    public void setAlertStart (View v) {
        if (!beginTime.equals("")) {
            Calendar cal = Calendar.getInstance();
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            Date formatSdf = null;
            try {
                formatSdf = new SimpleDateFormat("MMMM dd yyyy").parse(beginTime);
            } catch (Exception e) {
                Toast.makeText(this.getBaseContext(), "Unable to parse (MMMM dd yyyy)", Toast.LENGTH_LONG).show();
            }
            intent.putExtra("beginTime", (formatSdf.getTime()));
            intent.putExtra("allDay", true);
            intent.putExtra("title", eventTitle+" starts");
            startActivity(intent);
        } else {
            Toast.makeText(this.getBaseContext(), "Unable to set alert, no date value available", Toast.LENGTH_LONG).show();
        }
    }

    public void setAlertEnd (View v) {
        if (!endTime.equals("")) {
            Calendar cal = Calendar.getInstance();
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            Date formatSdf = null;
            try {
                formatSdf = new SimpleDateFormat("MMMM dd yyyy").parse(endTime);
            } catch (Exception e) {
                Toast.makeText(this.getBaseContext(), "Unable to parse (MMMM dd yyyy)", Toast.LENGTH_LONG).show();
            }
            intent.putExtra("beginTime", (formatSdf.getTime()));
            intent.putExtra("allDay", true);
            if (dataTitle.equals("assessments")) {
                intent.putExtra("title", eventTitle+" is due");
            } else {
                intent.putExtra("title", eventTitle+" ends");
            }


            startActivity(intent);
        } else {
            Toast.makeText(this.getBaseContext(), "Unable to set alert, no date value available", Toast.LENGTH_LONG).show();
        }
    }

    public void setupTermView() {
        setContentView(R.layout.detail_term);
        this.setTitle("Term Details");
        db.open();
        Cursor c = db.getRow(dataTitle, whereValue);
        if (c.moveToFirst()) {
            previousTitle = c.getString(1);
            title = (TextView) findViewById(R.id.text_term_name); title.setText(c.getString(1)); eventTitle = c.getString(1);
            start = (TextView) findViewById(R.id.start_date); start.setText(c.getString(2)); beginTime = c.getString(2);
            end = (TextView) findViewById(R.id.end_date); end.setText(c.getString(3)); endTime = c.getString(3);
        }
        db.close();
    }

    public void setupCourseView() {
        setContentView(R.layout.detail_course);
        this.setTitle("Course Details");
        db.open();
        Cursor c = db.getRow(dataTitle, whereValue);
        if (c.moveToFirst()) {
            previousTitle = c.getString(1);
            title = (TextView) findViewById(R.id.text_course_name); title.setText(c.getString(1)); eventTitle = c.getString(1);
            start = (TextView) findViewById(R.id.start_date); start.setText(c.getString(2)); beginTime = c.getString(2);
            end = (TextView) findViewById(R.id.end_date); end.setText(c.getString(3)); endTime = c.getString(3);
            TextView status = (TextView) findViewById(R.id.text_course_status); status.setText(c.getString(4));
            TextView notes = (TextView) findViewById(R.id.text_course_notes); notes.setText("Notes: "+c.getString(5));


            // Format share action
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "Course: "+c.getString(1)+" Start: "+c.getString(2)+" End: "+c.getString(3)+" Status: "+c.getString(4)+" Notes:"+c.getString(5));
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Notes for "+c.getString(1)+":"+c.getString(5));
        }
        db.close();
    }

    public void setupMentorView() {
        setContentView(R.layout.detail_mentor);
        this.setTitle("Mentor Details");
        db.open();
        Cursor c = db.getRow(dataTitle, whereValue);
        if (c.moveToFirst()) {
            previousTitle = c.getString(1);
            TextView name = (TextView) findViewById(R.id.text_mentor_name); name.setText(c.getString(1));
            TextView phone = (TextView) findViewById(R.id.text_phone); phone.setText(c.getString(2));
            TextView email = (TextView) findViewById(R.id.text_email); email.setText(c.getString(3));;


            // Format share action
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "Course: "+c.getString(1)+" Start: "+c.getString(2)+" End: "+c.getString(3)+" Status: "+c.getString(4)+" Notes:"+c.getString(5));
            //shareIntent.setType("text/plain");
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "Notes for "+c.getString(1)+":"+c.getString(5));
        }
        db.close();
    }

    public void setupAssessmentView() {
        setContentView(R.layout.detail_assessment);
        this.setTitle("Assessment Details");

        db.open();
        Cursor c = db.getRow(dataTitle, whereValue);
        if (c.moveToFirst()) {
            previousTitle = c.getString(1);
            TextView assessmentName = (TextView) findViewById(R.id.text_assessment_name); assessmentName.setText(c.getString(1)); eventTitle = c.getString(1);
            TextView type = (TextView) findViewById(R.id.text_assessment_type); type.setText(c.getString(2));
            TextView dueDate = (TextView) findViewById(R.id.end_date); dueDate.setText(c.getString(3)); endTime = c.getString(3);


            // Format share action
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "Course: "+c.getString(1)+" Start: "+c.getString(2)+" End: "+c.getString(3)+" Status: "+c.getString(4)+" Notes:"+c.getString(5));
            //shareIntent.setType("text/plain");
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "Notes for "+c.getString(1)+":"+c.getString(5));
        }
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(dataTitle.equals("courses")||dataTitle.equals("assessments")) {
            getMenuInflater().inflate(R.menu.share_menu, menu);
            MenuItem item = menu.findItem(R.id.menu_item_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            mShareActionProvider.setOnShareTargetSelectedListener(this);


            mShareActionProvider.setShareIntent(shareIntent);
        }
       // Return true to display menu

        return(super.onCreateOptionsMenu(menu));
        //return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {

      //  Toast.makeText(this, intent.getComponent().toString(),
      //          Toast.LENGTH_LONG).show();

        return false;
    }
}
