package eli.cabinetdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eli on 3/29/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "catalog.db";
    private static final int VERSION = 1;

    private static final String TABLE_CABINET = "cabinet";
    private static final String COLUMN_CABINET_WIDTH = "width";
    private static final String COLUMN_CABINET_HEIGHT = "height";
    private static final String COLUMN_CABINET_DEPTH = "depth";
    private static final String COLUMN_CABINET_TYPE = "type";
    private static final String COLUMN_CABINET_DESIGN_FILE = "design_file";
    private static final String COLUMN_CABINET_MODEL_NUMBER = "model_number";

    //private static final String TABLE_DESIGN = "design";
    //private static final String COLUMN_DESIGN_ID = "id";
    //private static final String COLUMN_DESIGN_IMAGE_FILE = "image_file";

    private static final String TABLE_CATALOG = "catalog";
    private static final String COLUMN_CATALOG_NAME = "name";
    private static final String COLUMN_CATALOG_MATERIAL = "material";
    private static final String COLUMN_CATALOG_YEAR = "year";

    //private static final String TABLE_SECTION = "section";
    //private static final String COLUMN_SECTION_TYPE = "type";

    public DBHelper(Context context){
        super(context,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){


        //Create Cabinet Table
        db.execSQL("create table "+ TABLE_CABINET + " ("+COLUMN_CABINET_MODEL_NUMBER+
                " text primary key, " + COLUMN_CABINET_WIDTH+ " integer, "+ COLUMN_CABINET_HEIGHT+
                " integer, "+ COLUMN_CABINET_DEPTH+ " integer, "+ COLUMN_CABINET_TYPE+
                " text, "+ COLUMN_CABINET_DESIGN_FILE+
                " text, catalog_name text references "
                + TABLE_CATALOG+"("+ COLUMN_CATALOG_NAME+"))" );

        //Create Catalog Table
        db.execSQL("create table "+ TABLE_CATALOG+ " ("+COLUMN_CATALOG_NAME+" text primary key, "+COLUMN_CATALOG_MATERIAL+" text, "+COLUMN_CATALOG_YEAR+" integer)" );

        populateCatalogs();

        populateCabinets();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //schema changes and data massage here on upgrade
    }

    private void populateCatalogs(){
        ContentValues cv = new ContentValues();
        //Insert catalogs
        //Steel catalog
        cv.put(COLUMN_CATALOG_NAME,"Research Collection");
        cv.put(COLUMN_CATALOG_MATERIAL,"Steel");
        cv.put(COLUMN_CATALOG_YEAR,2015);
        getWritableDatabase().insert(TABLE_CATALOG,null,cv);

        //Wood catalog
        cv.put(COLUMN_CATALOG_NAME,"Signature Series");
        cv.put(COLUMN_CATALOG_MATERIAL,"Wood");
        cv.put(COLUMN_CATALOG_YEAR,2015);
        getWritableDatabase().insert(TABLE_CATALOG,null,cv);

    }

    private void populateCabinets(){
        ContentValues cv = new ContentValues();
        //insert cabinets
            //e43cl
        cv.put(COLUMN_CABINET_MODEL_NUMBER,"e43c352230l");
        cv.put(COLUMN_CABINET_WIDTH,30);
        cv.put(COLUMN_CABINET_HEIGHT,35);
        cv.put(COLUMN_CABINET_DEPTH,22);
        cv.put(COLUMN_CABINET_TYPE,"Standing");
        cv.put(COLUMN_CABINET_DESIGN_FILE,"e43cl.bmp");

        cv.put(COLUMN_CABINET_MODEL_NUMBER,"e43c352236l");
        cv.put(COLUMN_CABINET_WIDTH,36);
        cv.put(COLUMN_CABINET_HEIGHT,35);
        cv.put(COLUMN_CABINET_DEPTH,22);
        cv.put(COLUMN_CABINET_TYPE,"Standing");
        cv.put(COLUMN_CABINET_DESIGN_FILE,"e43cl.bmp");

    }

}