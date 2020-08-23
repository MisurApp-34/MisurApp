package it.uniba.di.misurapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MisurApp";
    public static final String DETECTION_TABLE = "Strumenti";
    public static final String DETECTION_TABLE1 = "Misura";

    public static final String ID = "_id";
    public static final String PREFERENCE = "preference";
    public static final String NAME_TOOL = "name_tool";

    public static final String ID1 = "_id1";
    public static final String SAVING_NAME = "saving_name";
    public static final String DATETIME = "datetime";
    public static final String VALUE = "value";
    public static final String ID_TOOL = "id_tool";


    //tabella Strumenti
    public static final String CREATE_TABLE =
            "CREATE TABLE  " + DETECTION_TABLE + "(" +
                    ID1 + " integer primary key, " +
                    NAME_TOOL + " String not null, " +
                    PREFERENCE + " boolean not null " +
                    ") ";

    //tabella Misura
    private static final String CREATE_TABLE1 =
            "CREATE TABLE " + DETECTION_TABLE1 + "(" +
                    ID + " integer primary key, " +
                    SAVING_NAME + " String not null, " +
                    ID_TOOL + " integer not null, " +

                    DATETIME + " varchar(50) not null, " +
                    NAME_TOOL + " String not null, " +
                    VALUE + " decimal(10,2) not null, "+
                    " FOREIGN KEY ("+ID_TOOL+") REFERENCES "+DETECTION_TABLE+"("+ID1+"));";



    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE1);

        //inizializzo tabella strumenti --- non verrà più modificata

        //0 = false prefernece 1= true preference. Di default non ci sono preferenze.
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Bussola', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Campo Magnetico', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Intensità Luminosa', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Intensitò Sonora', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Pressione', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Temperatura', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Altitudine', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Gps', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Livella', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Sensore Prossimità', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Gravità', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Velocità', 0)");
        db.execSQL("INSERT INTO "+DETECTION_TABLE+" (name_tool, preference ) VALUES ('Accelerazione', 0)");





    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersione) {

        db.execSQL("DROP  TABLE IF EXISTS " + DETECTION_TABLE1);
        db.execSQL("DROP  TABLE IF EXISTS " + DETECTION_TABLE);

        onCreate(db);
    }
    public boolean addData( String saving_name, String name_tool, String value) {
        SQLiteDatabase db = this.getWritableDatabase();

        //questo serve a “mappare” un dato nella rispettiva colonna.
        ContentValues contentValues = new ContentValues();
        contentValues.put(SAVING_NAME, saving_name);

        String id_tool=null;

        //determino l'id del dello strumento presente nella tabella Strumenti
        switch(name_tool)
        {

            case "Bussola": id_tool="1"; break;
            case "Campo Magnetico": id_tool="2"; break;
            case "Intensità Luminosa": id_tool="3"; break;
            case "Intensità sonora": id_tool="4"; break;
            case "Pressione": id_tool="5"; break;
            case "Temperatura": id_tool="6"; break;
            case "Altitudine": id_tool="7"; break;
            case "Gps": id_tool="8"; break;
            case "Livella": id_tool="9"; break;
            case "Sensore Prossimità": id_tool="10"; break;
            case "Gravità": id_tool="11"; break;
            case "Velocità": id_tool="12"; break;
            case "Accelerazione": id_tool="13"; break;
        }

        contentValues.put(ID_TOOL, id_tool);


        //calcolo data in gmt - universal date
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String formattedDate = df.format(c);


        contentValues.put(DATETIME, formattedDate);
        contentValues.put(NAME_TOOL, name_tool);
        contentValues.put(VALUE, value);





        //inserisco nel db le informazioni
        long result = db.insert(DETECTION_TABLE1, null, contentValues);
db.close();
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


}