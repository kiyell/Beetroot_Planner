package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    DBAdapter db;
    TextView title,start, end;
    String beginTime, endTime, eventTitle;
    private ShareActionProvider mShareActionProvider;
    private Intent shareIntent=new Intent(Intent.ACTION_SEND);

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    SharedPreferences sharedPref;
    Boolean useCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataTitle= getIntent().getStringExtra("table");
        wherePK = getIntent().getStringExtra("where_pk");
        whereValue = getIntent().getStringExtra("where_value");
        db = new DBAdapter(this);
        beginTime = "invalid";
        endTime = "invalid";
        mCurrentPhotoPath = "no photo path set";

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
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        useCalendar = sharedPref.getBoolean("pref__key_calendar", false);
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

            if (useCalendar) {

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
                Date formatSdf = null;
                try {
                    formatSdf = new SimpleDateFormat("MMMM dd yyyy").parse(beginTime);
                } catch (Exception e) {
                    Toast.makeText(this.getBaseContext(), "Unable to parse (MMMM dd yyyy)", Toast.LENGTH_LONG).show();
                }

                scheduleNotification(getNotification(eventTitle+" starts"), formatSdf.getTime());
            }

        } else {
            Toast.makeText(this.getBaseContext(), "Unable to set alert, no date value available", Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleNotification(Notification notification, long future) {

        Calendar cal = Calendar.getInstance();

        Intent notificationIntent = new Intent(this, AlarmNotifier.class);
        notificationIntent.putExtra(AlarmNotifier.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(AlarmNotifier.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //long futureInMillis = SystemClock.elapsedRealtime() + delay;
        long futureInMillis = SystemClock.elapsedRealtime() + future - cal.getTimeInMillis();
        if (futureInMillis < 0) {
            Toast.makeText(this.getBaseContext(), "Unable to set alert, the date has already arrived.", Toast.LENGTH_LONG).show();
        } else {
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

            Toast.makeText(this.getBaseContext(), "Notification Alert has been set", Toast.LENGTH_LONG).show();
        }

    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Beetroot Planner");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.stat_notify_error);
        return builder.getNotification();
    }

    public void setAlertEnd (View v) {
        if (!endTime.equals("")) {

            if (useCalendar) {
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
                    intent.putExtra("title", eventTitle + " is due");
                } else {
                    intent.putExtra("title", eventTitle + " ends");
                }
                startActivity(intent);
            } else
            {
                Date formatSdf = null;
                try {
                    formatSdf = new SimpleDateFormat("MMMM dd yyyy").parse(endTime);
                } catch (Exception e) {
                    Toast.makeText(this.getBaseContext(), "Unable to parse (MMMM dd yyyy)", Toast.LENGTH_LONG).show();
                }
                String ending = " ends";
                if (dataTitle.equals("assessments")) {
                    ending = " is due";
                }
                scheduleNotification(getNotification(eventTitle+ending), formatSdf.getTime());
            }

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
            end = (TextView) findViewById(R.id.end_date);
            end.setText(c.getString(3)); endTime = c.getString(3);
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
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "Mento Notes for "+c.getString(1)+"");
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
            ImageView iv = (ImageView) findViewById(R.id.photo_note_assessment);

            mCurrentPhotoPath = c.getString(4);
            File imageFile = new File(mCurrentPhotoPath);
            if(imageFile.exists()) {
                //DEBUGCOMMENT Toast.makeText(this, "I see the image file path at "+mCurrentPhotoPath, Toast.LENGTH_LONG).show();

                Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                iv.setImageBitmap(myBitmap);

                shareIntent.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                File f = new File(Environment.getExternalStorageDirectory().getPath() + File.separator +"AssessmentShareImage.jpg");

                FileOutputStream fo = null;
                try {
                    f.createNewFile();
                    fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    try {
                        fo.close();
                    } catch (Exception e) {
                        //Catch fo close
                    }

                }
                //DEBUGCOMMENT Toast.makeText(this, "uri: " + Uri.fromFile(f).toString(), Toast.LENGTH_LONG).show();
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                //startActivity(Intent.createChooser(shareIntent, "Share Image"));
                //shareIntent.putExtra(Intent.EXTRA_TEXT, "Assessment Notes for " + c.getString(1) + "");

            } else {
                //DEBUGCOMMENT  Toast.makeText(this, "I don't see any image file path, checked:"+mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            }



            // Format share action
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "Course: "+c.getString(1)+" Start: "+c.getString(2)+" End: "+c.getString(3)+" Status: "+c.getString(4)+" Notes:"+c.getString(5));

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

    public void processImage(View v) {

        // if its the add button then create an image, save the file path, reload imageview with that filepath
        if (v.getId() == R.id.button_add_image) {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }

        }

        // if its the remove button then delete, reload imageview with blank image
        if (v.getId() == R.id.button_remove_image) {
            Toast.makeText(this, "Deleting: "+mCurrentPhotoPath, Toast.LENGTH_LONG).show();

            TextView assessmentTitle = (TextView) findViewById(R.id.text_assessment_name);
            TextView assessmentType = (TextView) findViewById(R.id.text_assessment_type);
            TextView assessmentDueDate = (TextView) findViewById(R.id.end_date);
            ImageView iv = (ImageView) findViewById(R.id.photo_note_assessment);
            iv.setVisibility(View.GONE);

            File imageFile = new File(mCurrentPhotoPath);
            if(imageFile.exists()) {
                imageFile.delete();
            }


            db.open();
            long assessment_id = db.updateAssessment(assessmentTitle.getText().toString(), assessmentType.getText().toString(), assessmentDueDate.getText().toString(), "", whereValue);
            db.close();
            mCurrentPhotoPath = "n/a";
           // recreate();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PhotoNoteJPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
      //  mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
    //       Bundle extras = data.getExtras();
    //        Bitmap imageBitmap = (Bitmap) extras.get("data");
    //        ImageView photoNote = (ImageView) findViewById(R.id.photo_note_assessment);

            //DEBUGCOMMENT Toast.makeText(this, mCurrentPhotoPath, Toast.LENGTH_LONG).show();

            TextView assessmentTitle = (TextView) findViewById(R.id.text_assessment_name);
            TextView assessmentType = (TextView) findViewById(R.id.text_assessment_type);
            TextView assessmentDueDate = (TextView) findViewById(R.id.end_date);

            db.open();
            long assessment_id = db.updateAssessment(assessmentTitle.getText().toString(), assessmentType.getText().toString(), assessmentDueDate.getText().toString(), mCurrentPhotoPath, whereValue);
            db.close();
            recreate();
        }
    }
}
