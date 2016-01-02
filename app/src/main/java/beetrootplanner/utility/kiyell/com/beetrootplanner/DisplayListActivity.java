package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplayListActivity extends ListActivity {


    ArrayList<String> results = new ArrayList<>();
    ArrayList<DBListEntry> dblist = new ArrayList<>();
    DBAdapter db;

    static int VIEW_MODE = 0;
    static int ADD_MODE = 1;
    static int DELETE_MODE = 2;
    static int EDIT_MODE = 3;
    int currentMode;

    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final Context c = this.getApplicationContext();
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
        dblist.clear();
        if (c.moveToFirst())
        {
            do {
                dblist.add(new DBListEntry(c.getString(1)+" : "+c.getString(2)+" - "+c.getString(3),c.getLong(0)));
              //  results.add("("+c.getString(0)+") "+c.getString(1)+" : "+c.getString(2)+" - "+c.getString(3));
            } while (c.moveToNext());
        }
        db.close();
    }

    private void populateList() {

        ArrayAdapter la = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
       // la.add("hello");

        for (DBListEntry item : dblist) {
            la.add(item);
        }
        setListAdapter(la);


       // getListView().setTextFilterEnabled(true);
       // getListView().addView();
        //android.R.layout.simple_list_item_1
    }

    public void populateDeleteList(View v) {
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, results));
      //  getListView().setTextFilterEnabled(true);
    }

    public void addData(View v) {

        if(currentMode == DELETE_MODE) {
            setMode(VIEW_MODE);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = this.getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_add_term, null));

            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Dialog d = (Dialog) dialog;
                    EditText termTitle,termStart,termEnd;
                    termTitle = (EditText) d.findViewById(R.id.term_title);
                    termStart = (EditText) d.findViewById(R.id.term_start);
                    termEnd = (EditText) d.findViewById(R.id.term_end);

                    StringBuilder output = new StringBuilder();
                    output.append(termTitle.getText()+ " ");
                    output.append(termStart.getText()+ " ");
                    output.append(termEnd.getText()+ " ");

                    Toast.makeText(d.getContext(), "Inserting the data: " + output, Toast.LENGTH_LONG).show();
                    db.open();
                    long term_id = db.addTerm(termTitle.getText().toString(), termStart.getText().toString(), termEnd.getText().toString());
                    db.close();
                    queryDatabase();
                    populateList();
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

    }

    public void deleteData(View v) {
        if (currentMode == DELETE_MODE) {
        // Alreadying in delete mode, time to delete specific data
        }else
        setMode(DELETE_MODE);
    }

    public void editData(View v) {

    }

    public void setMode(int mode) {
        currentMode = mode;

        if(mode == DELETE_MODE) {
            //change button to Confirm delete
            //Change layout
            Button deleteButton = (Button) findViewById(R.id.button_delete);
       //     deleteButton.setText("Confirm Delete");
            deleteButton.setEnabled(false);

            Button editButton = (Button) findViewById(R.id.button_edit);
            //     deleteButton.setText("Confirm Delete");
            editButton.setEnabled(false);

            Button cancelButton = (Button) findViewById(R.id.button_add);
            cancelButton.setText("Cancel");

            setListAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_checked, results));
            getListView().setTextFilterEnabled(true);
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


            setListAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, results));
            getListView().setTextFilterEnabled(true);
        }
    }


    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    //    Object o = this.getListAdapter().getItem(position-1);
     //   String selected = o.toString();
        DBListEntry e = (DBListEntry) this.getListAdapter().getItem(position-1);
        Toast.makeText(this, "You have chosen the term: " + " " + e.rowid, Toast.LENGTH_LONG).show();

        if (currentMode == VIEW_MODE) {
            //Enter into Data and display sub entries
        }

        if (currentMode == DELETE_MODE) {
            //Delete the entry
           // processDelete();
        }

     //   getListAdapter().
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