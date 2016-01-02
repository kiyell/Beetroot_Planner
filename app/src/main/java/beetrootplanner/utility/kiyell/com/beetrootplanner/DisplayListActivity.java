package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplayListActivity extends ListActivity {


    ArrayList<String> results = new ArrayList<String>();
    DBAdapter db;

    static int VIEW_MODE = 0;
    static int ADD_MODE = 1;
    static int DELETE_MODE = 2;
    int currentMode;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_list);
        db = new DBAdapter(this);
        TextView tView = new TextView(this);
        tView.setText("Displaying Term data");
        getListView().addHeaderView(tView);
        queryDatabase();
        populateList();
    }

    private void queryDatabase() {


        //---add a contact---
        db.open();
      //  long term_id = db.addTerm("Term 1", "July 1st", "August 1st");

        Cursor c = db.getAllTerms();
        results.clear();
        if (c.moveToFirst())
        {
            do {
                results.add(c.getString(1));
            } while (c.moveToNext());
        }
        db.close();
    }

    private void populateList() {


        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, results));
        getListView().setTextFilterEnabled(true);
       // getListView().addView();
    }

    public void populateDeleteList(View v) {
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, results));
        getListView().setTextFilterEnabled(true);
    }

    public void addData(View v) {

        if(currentMode == DELETE_MODE) {
            setMode(VIEW_MODE);
        } else {
            db.open();
            long term_id = db.addTerm("Term 3", "August 1st", "September 1st");
            db.close();
            queryDatabase();
            populateList();
        }

    }

    public void deleteData(View v) {
        if (currentMode == DELETE_MODE) {
        // Alreadying in delete mode, time to delete specific data
        }else
        setMode(DELETE_MODE);
    }

    public void setMode(int mode) {
        currentMode = mode;

        if(mode == DELETE_MODE) {
            //change button to Confirm delete
            //Change layout
            Button deleteButton = (Button) findViewById(R.id.button_delete);
            deleteButton.setText("Confirm Delete");

            Button cancelButton = (Button) findViewById(R.id.button_add);
            cancelButton.setText("Cancel");

            setListAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_checked, results));
            getListView().setTextFilterEnabled(true);
        }
        if(mode == VIEW_MODE) {
            Button deleteButton = (Button) findViewById(R.id.button_delete);
            deleteButton.setText("Delete");

            Button addButton = (Button) findViewById(R.id.button_add);
            addButton.setText("Add Term");


            setListAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, results));
            getListView().setTextFilterEnabled(true);
        }
    }


    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Object o = this.getListAdapter().getItem(position-1);
        String selected = o.toString();
        Toast.makeText(this, "You have chosen the term: " + " " + selected, Toast.LENGTH_LONG).show();
    }
}