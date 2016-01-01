package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplayListActivity extends ListActivity {


    ArrayList<String> results = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_list);
        queryDatabase();
        populateList();
    }

    private void queryDatabase() {
        DBAdapter db = new DBAdapter(this);

        //---add a contact---
        db.open();
      //  long term_id = db.addTerm("Term 1", "July 1st", "August 1st");

        Cursor c = db.getAllTerms();
        if (c.moveToFirst())
        {
            do {
                results.add(c.getString(1));
            } while (c.moveToNext());
        }
        db.close();
    }

    private void populateList() {
        TextView tView = new TextView(this);
        tView.setText("Displaying Term data");
      //  ListView list = (ListView) findViewById(R.id.listView);
        getListView().addHeaderView(tView);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, results));
        getListView().setTextFilterEnabled(true);
       // getListView().addView();
    }

    public static void PopulateCallback(ArrayList<String> r) {

    }


    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Object o = this.getListAdapter().getItem(position);
        String pen = o.toString();
        Toast.makeText(this, "You have chosen the pen: " + " " + pen, Toast.LENGTH_LONG).show();
    }
}