package eli.cabinetdatabase;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CabinetResults extends ActionBarActivity {
    protected static ArrayList<Cabinet> cabinets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabinet_results);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cabinet_results, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_cabinet_results, container, false);

            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null)
            {
                String sqlMaterial = extras.getString("sqlMaterial");
                ArrayList<String> materialCols = extras.getStringArrayList("materialVals");
                String[] materialVals = getArgArray(materialCols);

                ArrayList<String> cols = extras.getStringArrayList("Selection");
                ArrayList<String> colVals = extras.getStringArrayList("SelectionArgs");

                String selection = null;
                String[] selectionArgs = null;
                if (cols != null && cols.size() > 0)
                    selection = getQueryableColString(cols);
                if (colVals != null && colVals.size() > 0)
                    selectionArgs = getArgArray(colVals);

                //Create database object
                SQLiteDatabase db;

                DBHelper DB = new DBHelper(rootView.getContext());
                db = DB.open();

                if (db != null) {
                    Cursor cursor = db.query(DBHelper.TABLE_CABINET, null, selection, selectionArgs, null, null, null);
                    Cursor materialCursor = null;
                    Map<String, String> matName = new HashMap<>();
                    if (sqlMaterial == null) {
                        sqlMaterial = "Select * from " + DBHelper.TABLE_CATALOG;
                    }
                    materialCursor = db.rawQuery(sqlMaterial, materialVals);

                    if (materialCursor.moveToFirst())
                    {
                        matName.put(materialCursor.getString(materialCursor.getColumnIndex(DBHelper.COLUMN_CATALOG_NAME)),
                                materialCursor.getString(materialCursor.getColumnIndex(DBHelper.COLUMN_CATALOG_MATERIAL)));
                        while (materialCursor.moveToNext())
                        {
                            matName.put(materialCursor.getString(materialCursor.getColumnIndex(DBHelper.COLUMN_CATALOG_NAME)),
                                    materialCursor.getString(materialCursor.getColumnIndex(DBHelper.COLUMN_CATALOG_MATERIAL)));
                        }
                    }
                    //Cursor cursor = db.rawQuery(sqlSelect, selectionArgs);

                    //Create array of cabinet objects to store values retrieved from database

                    //if there were rows retrieved
                    if (cursor.moveToFirst()) {
                        //Create arraylist of cabinets, one for each cabinet retrieved
                        cabinets = new ArrayList<>(cursor.getCount());
                        for (int i = 0; i < cursor.getCount(); i++) {
                            if (matName.isEmpty()) {
                                cabinets.add(new Cabinet(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_MODEL_NUMBER)),
                                        cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_WIDTH)),
                                        cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_HEIGHT)),
                                        cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_DEPTH)),
                                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_TYPE)),
                                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_DESIGN_FILE)),
                                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CATALOG_NAME))));
                            } else {
                                //Make new cabinet object with material
                                String catalogName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_FK_CATALOG_NAME));
                                if (matName.containsKey(catalogName)) {
                                    cabinets.add(new Cabinet(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_MODEL_NUMBER)),
                                            cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_WIDTH)),
                                            cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_HEIGHT)),
                                            cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_DEPTH)),
                                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_TYPE)),
                                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CABINET_DESIGN_FILE)),
                                            catalogName,
                                            matName.get(catalogName)));
                                }
                            }
                            cursor.moveToNext();
                        }

                        if (cabinets.size() > 0) {
                            String[] displayText = getDisplayText(cabinets);

                            final Uri[] imgId = getImageIds(cabinets);

                            CustomList adapter = new CustomList(this.getActivity(), displayText, imgId);
                            ListView displayList = (ListView) rootView.findViewById(R.id.listView);
                            displayList.setAdapter(adapter);
                            cursor.close();
                            displayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    //Load cabinetDetail fragment here!
                                    //Toast.makeText(rootView.getContext(), "You Clicked on Cabinet 1", Toast.LENGTH_SHORT).show();

                                    Intent in = new Intent(view.getContext(), CabinetDetail.class);
                                    in.putExtra("imgFile",imgId[position].toString());
                                    in.putExtra("position",position);

                                    startActivity(in);
                                }
                            });
                        }
                        else
                        {
                            CustomList adapter = new CustomList(this.getActivity(), new String[]{"No rows found\nPlease refine your search!"}, new Uri[]{});
                            ListView displayList = (ListView) rootView.findViewById(R.id.listView);
                            displayList.setAdapter(adapter);
                            cursor.close();
                        }
                    }
                    else
                    {
                        CustomList adapter = new CustomList(this.getActivity(), new String[]{"No rows found\nPlease refine your search!"}, new Uri[]{});
                        ListView displayList = (ListView) rootView.findViewById(R.id.listView);
                        displayList.setAdapter(adapter);
                        cursor.close();
                    }
                }
                else
                {
                    Log.d(Search.testTag, "Database Not Found!");
                }
            }

            return rootView;
        }

        private String getQueryableColString(ArrayList<String> cols)
        {
            String retVal = "";
            for (int i = 0; i<cols.size(); i++)
            {
                if (cols.get(i).contains("OR"))
                {
                    retVal = retVal + cols.get(i);
                }
                else {
                    retVal = retVal + cols.get(i) + " AND ";
                }
            }

            return retVal.substring(0, retVal.length() - 5);
        }

        private String[] getArgArray(ArrayList<String> cols)
        {
            if (cols == null)
                return null;
            else {
                String[] retVal = new String[cols.size()];

                for (int i = 0; i < cols.size(); i++) {
                    retVal[i] = cols.get(i);
                }
                return retVal;
            }
        }

        private Uri[] getImageIds(ArrayList<Cabinet> cabinets)
        {
            Uri[] retVal = new Uri[cabinets.size()];

            for (int i = 0; i < cabinets.size(); i++)
            {
                try {
                    String fullPath = cabinets.get(i).getDesignFile();
                    String fileName = fullPath.substring(0, fullPath.indexOf('.'));
                    retVal[i] = Uri.parse("android.resource://eli.cabinetdatabase/drawable/" + fileName);
                }
                catch (Exception ex)
                {
                    retVal[i] = null;
                }
            }

            return retVal;
        }

        private String[] getDisplayText(ArrayList<Cabinet> cabinets)
        {
            int size = cabinets.size();
            String[] retVal = new String[size];
            for (int i = 0; i < size; i++)
            {
                String modNo = cabinets.get(i).getModelNum();
                String type = cabinets.get(i).getType();
                String material = cabinets.get(i).getMaterial();
                String temp = "Model Number : \n1? \nType : \n2? \nMaterial : \n3? \n";
                temp = temp.replace("1?", modNo);
                temp = temp.replace("2?", type);
                temp = temp.replace("3?", material);
                retVal[i] = temp;
            }

            return retVal;
        }

    }
}
