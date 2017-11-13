package es.queapps.quebar.internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase para la gestion de la base de datos. Extiende de SQLiteOpenHelper
 * Crea las tablas de datos correspondientes
 * @see android.database.sqlite.SQLiteOpenHelper
 * @version 1.0
 * @author Victoria Marcos
 */
public class PersistenceSQLiteHelper extends SQLiteOpenHelper {

    //sentencias para crear las tablas de Poi y TABLE_VOTO
    String sqlCreate_MYPOIS = "CREATE TABLE Poi (id INTEGER, type INTEGER, desc TEXT, date TEXT, urlFoto TEXT, votomas INTEGER, votomenos INTEGER, latitud DOUBLE, longitud DOUBLE); ";

	String sqlCreateIndex_pois_date = "CREATE INDEX index_pois_date ON Poi ( date ASC );";
	
	String CREATE_VOTO_TABLE = "CREATE TABLE TABLE_VOTO (id INTEGER PRIMARY KEY);";
    
 
    public PersistenceSQLiteHelper(Context contexto, String nombre,
                               CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCreate_MYPOIS);       
        db.execSQL(sqlCreateIndex_pois_date); 
        db.execSQL(CREATE_VOTO_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

     
        db.execSQL( "DROP TABLE IF EXISTS Poi ; ");
        db.execSQL("DROP TABLE IF EXISTS TABLE_VOTO ;");
 

        onCreate(db);
    }
}
