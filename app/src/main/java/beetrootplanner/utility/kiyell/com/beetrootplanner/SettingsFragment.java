package beetrootplanner.utility.kiyell.com.beetrootplanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SettingsFragment extends PreferenceFragment {

    DBAdapter db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        db = new DBAdapter(getActivity());

        Preference deleteDb = (Preference) findPreference("deleteDB");
        deleteDb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Delete entire database");
                builder.setMessage("Are you sure you would like to delete the entire database? This action cannot be undone.");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the database
                        deleteDatabase();
                        Toast.makeText(getActivity(), "Deleted the database!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();



                return true;
            }
        });

        Preference createDb = (Preference) findPreference("createDB");
        createDb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Create Example database");
                builder.setMessage("Are you sure you would like to delete the entire database, and then populate the database with example data? Note: Deleting the database cannot be undone!");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the database
                        createExampleDb();

                        dialog.dismiss();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();



                return true;
            }
        });
    }

    public void deleteDatabase() {
        //delete db code
        db.open();
        db.deleteTableInfo();
        db.close();
    }

    public void createExampleDb() {
        deleteDatabase();

        try {
            String destPath = "/data/data/" + getActivity().getPackageName() +
            "/databases";
            File f = new File(destPath);
            if (!f.exists()) {
                f.mkdirs();
                f.createNewFile();
            }
            CopyDB(getActivity().getAssets().open("MyDB"),
                    new FileOutputStream(destPath + "/MyDB"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //copy db code

        Toast.makeText(getActivity(), "Created an example database!", Toast.LENGTH_LONG).show();


    }

    public void CopyDB(InputStream inputStream,
                       OutputStream outputStream) throws IOException {
        //---copy 1K bytes at a time---
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
}
