package es.queapps.quebar.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import es.queapps.quebar.R;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;



/**
 * Clase con utilidades del c�digo
 * @version 1.0
 * @author Victoria Marcos
 * 
 */
public class Utils {
	private static File cacheDirNameMedia;
	private static String UrlMedia;


	/**
	 * Carga una imagen en background
	 * 
	 * @param url
	 * @param iv
	 * @param activity
	 */
	public static void LoadImageBG(final String url, ImageView iv,
			final Activity activity) {
		System.gc();
		Runtime.getRuntime().gc();
		new AsyncTask<ImageView, Integer, Drawable>() {
			ImageView imageview;
			int attempts = 3;

			@Override
			protected Drawable doInBackground(ImageView... params) {
				// TODO Auto-generated method stub
				imageview = params[0];
				Drawable drawable = null;
				File downloadPath = createDownloadsPath(activity, url.split("/"));
				do {
					drawable = Utils.LoadImageFromWebOperations(url,
							downloadPath);
					attempts--;
				} while (attempts > 0 && drawable == null);
				return drawable;
			}

			protected void onPostExecute(final Drawable result) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						if (result!=null){
							imageview.setImageDrawable(result);
						}else{
							imageview.setImageResource(R.drawable.icono_ranking);
						}
					}
				});
			}
		}.execute(iv);
	};

	/**
	 * Carga un imagen de la web y devuelve un drawable para mostrarlo.
	 * 
	 * @param url
	 * @param cacheDir
	 * @return
	 */
	public static Drawable LoadImageFromWebOperations(String url, File cacheDir) {
		try {
			Drawable d = null;
			String[] urlDir = url.split("/");
			int sizeMax=600;
			try {
				// File cacheDir = new File(ruta+File.separator+ urlDir[8]);
				if (!cacheDir.exists()) {
					cacheDir.mkdirs();

				}
				File cacheDirName = new File(cacheDir + File.separator
						+ urlDir[urlDir.length - 1]);

				if (!cacheDirName.exists() || cacheDirName.length() == 0) {
					cacheDirName.createNewFile();
					InputStream is = (InputStream) new URL(url).getContent();
					Bitmap bit = BitmapFactory.decodeStream(is);
					//Alto
					Bitmap bit2=null;
					FileOutputStream fo = new FileOutputStream(cacheDirName);
					// Drawable d = Drawable.createFromStream(is, "src name");
					if(bit.getHeight()>sizeMax){
						bit2=Bitmap.createScaledBitmap(bit,(sizeMax*bit.getWidth()/bit.getHeight()), sizeMax, false);
						bit2.compress(CompressFormat.JPEG, 95, fo);
					}else if(bit.getWidth()>sizeMax){
						bit2=Bitmap.createScaledBitmap(bit,sizeMax, (sizeMax* bit.getHeight()/bit.getWidth()), false);
						bit2.compress(CompressFormat.JPEG, 95, fo);
					}else{
						bit.compress(CompressFormat.JPEG, 95, fo);
					}

					if(Constants.DEBUG) Log.v(Constants.TAG,"Url: "+cacheDir + File.separator
							+ urlDir[urlDir.length - 1]);
					if(Constants.DEBUG) Log.v(Constants.TAG,"Ancho y alto: "+(bit.getWidth()+" "+bit.getHeight()));
					if(Constants.DEBUG) Log.v(Constants.TAG,"Size1: "+(bit.getRowBytes()*bit.getHeight()));
					bit.recycle();
					if(bit2!=null){
						if(Constants.DEBUG) Log.v(Constants.TAG,"Size2: "+(bit2.getRowBytes()*bit2.getHeight()));
						bit2.recycle();
					}
				}
				System.gc();
				Runtime.getRuntime().gc();
				d = Drawable.createFromPath(cacheDirName.getPath());
			} catch (OutOfMemoryError e) {
				System.gc();
				Runtime.getRuntime().gc();
			}
			//

			return d;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Almacena una imagen en la cach�
	 * 
	 * @param url
	 * @param cacheDir
	 */
	public void saveImage(String url, File cacheDir) {
		File cacheDirName = null;
		try {

			String[] urlDir = url.split("/");

			// File cacheDir = new File(ruta+File.separator+ urlDir[8]);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();

			}
			cacheDirName = new File(cacheDir + File.separator
					+ urlDir[urlDir.length - 1]);

			if (!cacheDirName.exists() || cacheDirName.length() == 0) {

				cacheDirName.createNewFile();

				InputStream is = (InputStream) new URL(url).getContent();

				Rect rect = new Rect();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				options.inJustDecodeBounds = true;
				Bitmap bit = BitmapFactory.decodeStream(is, rect, options);

				// Bitmap bit = BitmapFactory.decodeStream(is);
				FileOutputStream fo = new FileOutputStream(cacheDirName);
				// Drawable d = Drawable.createFromStream(is, "src name");

				bit.compress(CompressFormat.JPEG, 100, fo);
				bit.recycle();

			}
			System.gc();
			Runtime.getRuntime().gc();
			// cacheDirName

		} catch (Exception e) {
			if (!cacheDirName.exists())
				cacheDirName.delete();
			System.out.println(url + e.getMessage());
		}

	}

	/**
	 * Se usa cuando se llaman a contenidos de video o audio
	 * 
	 * @param url
	 * @return
	 */
	public static Uri UriParse(String url) {
		try {
			UrlMedia = url;
			File path = null;
			File cacheDir;
			String[] urlDir = url.split("/");
			if (sdAvailableWrite()) {
				path = Environment.getExternalStorageDirectory();
				cacheDir = new File(path + File.separator + "miramusei"
						+ File.separator + "cache" + File.separator
						+ urlDir[urlDir.length - 2]);
			} else {
				path = Environment.getDataDirectory();
				cacheDir = new File(path + File.separator + "data"
						+ File.separator + "com.miramusei.museum"
						+ File.separator + "cache" + File.separator
						+ urlDir[urlDir.length - 2]);
			}
			// File cacheDir = new File(ruta+File.separator+ urlDir[8]);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();

			}
			cacheDirNameMedia = new File(cacheDir + File.separator
					+ urlDir[urlDir.length - 1]);

			if (!cacheDirNameMedia.exists() || cacheDirNameMedia.length() == 0) {

				Thread downloadFile = new Thread() {
					@Override
					public void run() {
						try {
							super.run();
							cacheDirNameMedia.createNewFile();

							InputStream is = (InputStream) new URL(UrlMedia)
									.getContent();
							// Uri jose=Uri.parse(url);
							FileOutputStream fo = new FileOutputStream(
									cacheDirNameMedia);
							// Drawable d = Drawable.createFromStream(is,
							// "src name");
							byte[] buffer = new byte[2048];
							int length;
							while ((length = is.read(buffer)) > 0) {
								fo.write(buffer, 0, length);
							}

							fo.close();

						} catch (Exception e) {
							cacheDirNameMedia.deleteOnExit();
							System.out.println("Error cacheando imagen"
									+ e.getMessage());

						}
					}
				};
				downloadFile.start();
				return Uri.parse(url);

			} else {
				return Uri.fromFile(cacheDirNameMedia);

			}

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Almacena los media files en la cach�
	 * 
	 * @param url
	 */
	public void saveMedia(String url) {
		try {
			UrlMedia = url;
			File path = null;
			File cacheDir;
			String[] urlDir = url.split("/");
			if (sdAvailableWrite()) {
				path = Environment.getExternalStorageDirectory();
				cacheDir = new File(path + File.separator + "miramusei"
						+ File.separator + "cache" + File.separator
						+ urlDir[urlDir.length - 2]);
			} else {
				path = Environment.getDataDirectory();
				cacheDir = new File(path + File.separator + "data"
						+ File.separator + "com.miramusei.museum"
						+ File.separator + "cache" + File.separator
						+ urlDir[urlDir.length - 2]);
			}
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();

			}
			cacheDirNameMedia = new File(cacheDir + File.separator
					+ urlDir[urlDir.length - 1]);

			if (!cacheDirNameMedia.exists() || cacheDirNameMedia.length() == 0) {

				cacheDirNameMedia.createNewFile();

				InputStream is = (InputStream) new URL(UrlMedia).getContent();
				// Uri jose=Uri.parse(url);
				FileOutputStream fo = new FileOutputStream(cacheDirNameMedia);
				// Drawable d = Drawable.createFromStream(is, "src name");
				byte[] buffer = new byte[2048];
				int length;
				while ((length = is.read(buffer)) > 0) {
					fo.write(buffer, 0, length);
				}

				fo.close();

			}

		} catch (Exception e) {
			cacheDirNameMedia.deleteOnExit();
		}
	}

	/**
	 * Obtiene una fecha de un string
	 * 
	 * @param dateAsString
	 * @return
	 */
	public static Date GetDateObjectFromString(String dateAsString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date tempDate = null;
		try {
			if (dateAsString != null && !dateAsString.equalsIgnoreCase("null"))
				tempDate = sdf.parse(dateAsString);
		} catch (Exception e) {
			// do some error reporting here
			e.printStackTrace();
		}
		return tempDate;
	}

	/**
	 * Obtiene una fecha de un string
	 * 
	 * @param dateAsString
	 * @return
	 */
	public static Date GetDateObjectFromStringUpdate(String dateAsString) {
		SimpleDateFormat lv_formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		lv_formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date tempDate = null;
		try {
			if (dateAsString != null && !dateAsString.equalsIgnoreCase("null"))
				tempDate = lv_formatter.parse(dateAsString);
		} catch (Exception e) {
			// do some error reporting here
			e.printStackTrace();
		}
		return tempDate;
	}
	/**
	 * metodo que devuelve un formato legible de fecha
	 * @param date
	 * @return String con el contenido de la fecha
	 */
	public static String getLegibleDate(Date date) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy"); // "d MMM yyyy hh:mm aaa"
		String dateInit = simpleDateFormat.format(date);

	

		return dateInit;
	}

	/**
	 * Devuelve una fecha como string
	 * 
	 * @param dateAsString
	 * @return
	 */
	public static String GetStringObjectFromDate(Date date) {
		String lv_dateFormateInUTC = null;

		try {
			SimpleDateFormat lv_formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'");
			lv_formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

			lv_dateFormateInUTC = lv_formatter.format(date);

		} catch (Exception e) {
			// do some error reporting here
			e.printStackTrace();
		}
		return lv_dateFormateInUTC;
	}

	/**
	 * Devuelve True si la cadena es vacia o null
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean IsStringNullOrEmpty(String value) {
		return value == null || value == "" || value.length() == 0;
	}

	/**
	 * Escribe la fecha actual
	 * 
	 * @param inicio
	 * @param texto
	 */
	public static void WriteTime(Date init, String text) {
		Date after = new Date();
		long diffInMs = after.getTime() - init.getTime();
		if(Constants.DEBUG) Log.i(Constants.TAG, text + diffInMs);
	}

	/**
	 * Comprueba si la sd esta disponible para la escritura de datos
	 * 
	 * @return
	 */
	public static boolean sdAvailableWrite() {
		boolean sdAccessWrite = false;

		// Comprobamos el estado de la memoria externa (tarjeta SD)
		String state = Environment.getExternalStorageState();

		if (state.equals(Environment.MEDIA_MOUNTED)) {
			sdAccessWrite = true;
		} else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			sdAccessWrite = false;
		} else {
			sdAccessWrite = false;
		}
		return sdAccessWrite;
	}

	public static void CreateToast(Context context, String text) {

		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}


	public static boolean haveInternet(Context context) {
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}

		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
			return true;
		}
		return true;
	}

	/**
	 * Comprueba si se tiene la wi-fi activada
	 * 
	 * @param context
	 * @return
	 */
	public static boolean haveInternetWifi(Context context) {
		NetworkInfo networkInfo = (NetworkInfo) ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (networkInfo == null || !networkInfo.isConnected()) {
			return false;
		}
		if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
				&& networkInfo.isConnected()) {
			return true;
		}

		return false;
	}

	/**
	 * Crea una ruta para la descarga de los datos
	 * 
	 * @param activity
	 * @param urlDir
	 * @return
	 */
	public static File createDownloadsPath(Activity activity, String[] urlDir) {
		File path = null;
		File cacheDir = null;
		if (sdAvailableWrite()) {
			path = activity
					.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
			cacheDir = new File(path + File.separator + "santos"
					+ File.separator + "cache" + File.separator
					+ urlDir[urlDir.length - 2]);
		} else {
			path = Environment.getDataDirectory();
			cacheDir = new File(path + File.separator + "data" + File.separator
					+ "com.es.santosinocentes" + File.separator + "cache"
					+ File.separator + urlDir[urlDir.length - 2]);
		}
		return cacheDir;
	}

	/**
	 * Se encarga de borrar los meda files
	 * 
	 * @param activity
	 * @param urlDir
	 */

	public static void eraseMediaFile(Activity activity, String urlDir) {
		File file = null;
		File path = null;
		if (sdAvailableWrite()) {
			path = activity
					.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
			file = new File(path + urlDir);
		} else {
			path = Environment.getDataDirectory();
			file = new File(path + urlDir);
		}

		if (file.exists()) {
			file.delete();
		}

	}


}
