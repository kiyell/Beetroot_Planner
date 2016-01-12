package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


    }

    public void DisplayTerm(Cursor c)
    {
        Toast.makeText(this,
                "term_id: " + c.getString(0) + "\n" +
                        "term_title: " + c.getString(1) + "\n" +
                        "term_start: " + c.getString(2) + "\n" +
                        "term_end: " + c.getString(3),
                Toast.LENGTH_LONG).show();
    }

    public void startListActivity(View v) {
        Intent intent = new Intent(this, DisplayListActivity.class);
        intent.putExtra("table","terms");
        intent.putExtra("header_sub"," ");
        intent.putExtra("where_pk","term_id");
        intent.putExtra("where_value","");
        startActivity(intent);
    }

    public void deleteAlarms(View v) {
        AlarmManager am = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(),AlarmNotifier.class);
        Intent notificationIntent = new Intent(this, AlarmNotifier.class);
        notificationIntent.putExtra(AlarmNotifier.NOTIFICATION_ID, 1);
        PendingIntent p = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(p);
        p.cancel();


        Toast.makeText(this.getBaseContext(), "All Notification Alerts have been deleted!", Toast.LENGTH_LONG).show();
    }

    public void viewProgress(View v) {
        DBAdapter db = new DBAdapter(this);

        db.open(); // Get count of courses that have progress

        Cursor c = null; Cursor c2 = null;
        c = db.getCourseProgress("'COMPLETED'");
        c2 = db.getAll("courses");

        if (c.moveToFirst() && c2.moveToFirst())
        {
            Toast.makeText(this.getBaseContext(), c.getCount()+" Course(s) have been completed out of "+c2.getCount(), Toast.LENGTH_LONG).show();
        } else {
            // no courses found
            Toast.makeText(this.getBaseContext(), "No courses have been completed", Toast.LENGTH_LONG).show();
        }

        db.close();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
