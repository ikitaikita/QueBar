package es.queapps.quebar.internal;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Clase para la gestion de la base de datos 
 * @version 1.0
 * @author Victoria Marcos
 */
public class PersistenceSQL {

	private static final String DBNAME = "DBQBAR"; 
	
	
	private static int CURRENT_BBDD_VERSION = 1;


	public static boolean isVotedTV(int id, Context context) {
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(
				context, DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getReadableDatabase();


		Cursor cursor = db.rawQuery("select * from TABLE_VOTO where id="+ id, null);

		if (cursor != null) {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				db.close();
				return true;
			} else {
				db.close();
				return false;
			}
		} else{
			db.close();
			return false;}

	}

	/**
	 * Inserta un nuevo elemento Tapento en la tabla Poi
	 * @param context
	 * @param tapento, objeto tapendo a insertar
	 */
	public static void insertTapento(Context context, Tapento d) {

		
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(context,
				DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getWritableDatabase();

		// Si hemos abierto correctamente la base de datos
		if (db != null) {
			db.beginTransaction();
			try {
					ContentValues cv = new ContentValues();
					
				

					cv.put("id", d.getPoiId());
					cv.put("type", d.getType());
					cv.put("desc", d.getDescription());
					cv.put("date", getDatetimeSQLiteFormat(d.getDate()));
					cv.put("urlFoto", d.getUrlFoto());
					cv.put("votomas", d.getVotoPlus());
					cv.put("votomenos", d.getVotoMinus());
					cv.put("latitud", d.getLatitud());
					cv.put("longitud", d.getLongitud());
					
					db.insert("Poi", null, cv);

					db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

			// Cerramos la base de datos
			db.close();
		}
	}

	
	/*
	 * metodo que añade un voto a la tabla de votos
	 * @param poiId
	 * @param contexto
	 */
	public static void addVoteTV(int poiId, Context context) {

		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(context,
				DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("id", poiId);	
		db.insert("TABLE_VOTO", null, values);		
	
		db.close();

	}

	/*
	 * metodo para obtener la lista de ids de los elementos de la tabla Poi que son las tapas y/o eventos insertados por el usuario
	 * @param contexto
	 * @return lista de identificadores
	 */
	public static ArrayList<Integer> getallIds(Context ctx) {
		// Creamos una lista de enteros
		
		PersistenceSQLiteHelper usdbh = new PersistenceSQLiteHelper(
				ctx, DBNAME, null, CURRENT_BBDD_VERSION);

		SQLiteDatabase db = usdbh.getReadableDatabase();
		ArrayList<Integer> IDsList = new ArrayList<Integer>();

		// Selcccion de todas las Query
		Cursor cursor = db.rawQuery("select id from Poi ", null);

		if (cursor.moveToFirst()) {
			do {
				Integer id = cursor.getInt(0);
				IDsList.add(id);
			} while (cursor.moveToNext());
		}
		db.close();

		return IDsList;

	}
	

	
	/**
	 * Return "YYYY-MM-DD HH:MM:SS.SSS"
	 * 
	 * @param cal
	 * @return
	 */
	private static String getDatetimeSQLiteFormat(Date cal) {


		return String.format("%4s", cal.getYear() + 1900).replace(' ', '0')
				+ "-"
				+ String.format("%2s", cal.getMonth() + 1).replace(' ',
						'0')
				+ "-"
				+ String.format("%2s", cal.getDate()).replace(
						' ', '0')
				+ " "
				+ String.format("%2s", cal.getHours()).replace(
						' ', '0')
				+ ":"
				+ String.format("%2s", cal.getMinutes()).replace(' ',
						'0')
				+ ":"
				+ String.format("%2s", cal.getSeconds()).replace(' ',
						'0')
				+ "."
				+ String.format("%3s", 0)
						.replace(' ', '3').substring(3);

	}


}
