package es.queapps.quebar;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

import topoos.Objects.Image;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import es.queapps.quebar.internal.Constants;
import es.queapps.quebar.internal.PersistenceSQL;
import es.queapps.quebar.internal.Tapento;
import es.queapps.quebar.topoos.AccessInterface;
import es.queapps.quebar.topoos.POICategories;



/**
 * Contenido de la actividad Pestaña 1 del menú que realiza la insercion de una tapa y/o evento
 * Extiende de CustomActivity e implementa el interfaz OnClickListener  
 * @see es.queapps.quebar.CustomActivity
 * @see android.view.View.OnClickListener
 * @version 1.0
 * @author Victoria Marcos
 */
public class Pestana1 extends CustomActivity implements
		android.view.View.OnClickListener {


	
	private TextView marquee;



	private Button buttonT;
	

	private ImageView ImageView1;
	private static int TAKE_PICTURE = 1;
	private static final int SELECT_PICTURE = 2;
	private String nameCap = "";
	private String namePhoto ="";

	private topoos.Objects.POI mRegisteredPOI = null;

	private EditText t;


	// *****Posicion GPS*****//

	private Location m_DeviceLocation = null;
	private LocationManager mLocationManager;
	private ProgressDialog pd = null;

	// *****Imagen********//
	private byte[] bitmapdata;
	private ByteArrayOutputStream stream;

	public final int MESSAGE_ERROR = -1;
	public final int MESSAGE_OK = 1;
	private Handler handler = new Handler(new ResultMessageCallback());
	private int typeCategorie = 0; 
	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pestana1);
		
		
		
		//Localizacion 
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		CustomLocationListener customLocationListener = new CustomLocationListener();
		if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, customLocationListener);
        	m_DeviceLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, customLocationListener);
		m_DeviceLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		nameCap = Environment.getExternalStorageDirectory() + "/test.jpg";
		buttonT = (Button) findViewById(R.id.PublishBT);
		marquee = (TextView) findViewById(R.id.TapasList);
		buttonT.setEnabled(false);

		buttonT.setOnClickListener(this);
		ImageView1 = (ImageView) findViewById(R.id.ImageView1);
		
		t = (EditText) findViewById(R.id.DescriptionT);

		t.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			
				checkRegisterButtonStatus(); 
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

		});

		
 

		stream = new ByteArrayOutputStream();



		ImageView1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {						
					
						typeCategorie = POICategories.BAR;						
						GalleryCamSelection();

					}
				});

	}
	

	
	/**
	 * Clase privada que implementa LocationListener para la gestion de la localizacion
	 * @see android.location.LocationListener
	 */
	private class CustomLocationListener implements LocationListener{

		  public void onLocationChanged(Location argLocation) {
			  m_DeviceLocation = argLocation;
			  checkRegisterButtonStatus();
			  mLocationManager.removeUpdates(this);
		  }

		  public void onProviderDisabled(String provider) {}

		  public void onProviderEnabled(String provider) {}

		  public void onStatusChanged(String provider,
		    int status, Bundle extras) {}
	 }
	
	
	 /**
     * metodo que cambia el estado de los botones publicar a activos cuando tienen la información necesaria disponible
     */
    private void checkRegisterButtonStatus()
    {
 
    	
        
    	buttonT.setEnabled(bitmapdata != null && t.getText() != null && t.getText().toString().length() > 0 && m_DeviceLocation != null) ;
    	
        
    }



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TAKE_PICTURE) {
			
			if (data != null) {
			
				if (data.hasExtra("data")) {

					Bitmap bitmap = ((Bitmap) data.getParcelableExtra("data"));

				

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
				
					bitmap = Bitmap.createScaledBitmap(bitmap, 480, 480, false);
					
					bitmap.compress(CompressFormat.JPEG, 75, bos);
					
					bitmapdata = bos.toByteArray();
					ImageView iv = (ImageView) findViewById(R.id.ImageView1);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
					iv.setImageBitmap(bitmap);

				}
				/*
				 * De lo contrario es una imagen completa
				 */
			} else {
				/*
				 * A partir del nombre del archivo ya definido lo buscamos y
				 * creamos el bitmap para el ImageView
				 */
				ImageView iv = (ImageView) findViewById(R.id.ImageView1);
				iv.setImageBitmap(BitmapFactory.decodeFile(nameCap));
				// Guardar imagen en la galeria mediante mediascanner
				new MediaScannerConnectionClient() {
					private MediaScannerConnection msc = null;
					{
						msc = new MediaScannerConnection(
								getApplicationContext(), this);
						msc.connect();
					}

					public void onMediaScannerConnected() {
						msc.scanFile(nameCap, null);
					}

					public void onScanCompleted(String path, Uri uri) {
						msc.disconnect();
					}
				};
			}
		} else if (requestCode == SELECT_PICTURE) {
			Uri selectedImage = data.getData();

			String path = getRealPathFromURI(selectedImage);
			namePhoto = path;

			try {

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = (calculateInSampleSize(path, 480, 480));
				options.inDensity = 72;
				Bitmap bitmap = BitmapFactory.decodeFile(path, options);
				bitmap.compress(CompressFormat.JPEG, 75, bos);
				bitmapdata = bos.toByteArray();
				ImageView iv = (ImageView) findViewById(R.id.ImageView1);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
				iv.setImageBitmap(bitmap);

				
			} catch (Exception e) {

			}
		}
	

		checkRegisterButtonStatus();

	}

	public String getRealPathFromURI(Uri contentUri) {
		
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		android.database.Cursor cursor = managedQuery(contentUri, proj, // columns
																		// to
																		// return
				null, // clause rows
				null, // clause selection
				null); // order
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);

	}

	public static int calculateInSampleSize(String photoURI, int reqWidth,
			int reqHeight) {


		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoURI, options);

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}
	
	public static int calculateInSampleSize(Bitmap bitmap, int reqWidth,
			int reqHeight) {


		// Raw height and width of image
		final int height = bitmap.getHeight();
		final int width = bitmap.getWidth();
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	/**
	 * Clase privada implementa Runnable para lanzar la actividad de añadir Tapento al sistema, que podrá ser TAPA o EVENTO
	 * @param: tipo elemento, TAPA o EVENTO
	 */
	private class AddPOIWorkerTapa implements Runnable {
		
		private int typeElement;
		
		private AddPOIWorkerTapa(int type){
			typeElement=type;
		}

		@Override
		public void run() {
			int messageReturn = MESSAGE_OK;
			
			try {

				
				
				if(typeElement==POICategories.BAR)
				{

					Image image = null;
    				// Añadimos el POI a topoos

					UUID uuid = UUID.randomUUID();
					String randomUUIDString = uuid.toString();
					randomUUIDString = randomUUIDString.replace("-", "");
					if (randomUUIDString.length() > 10) randomUUIDString = randomUUIDString.substring(0,9); 
					randomUUIDString = randomUUIDString + ".jpg";
					

					image = topoos.Images.Operations.ImageUpload(Pestana1.this, bitmapdata,
							randomUUIDString);
				
					
					mRegisteredPOI = AccessInterface.RegisterTapento(Pestana1.this, POICategories.BAR, image.getFilename_unique(), t.getText().toString(), m_DeviceLocation);					
					Tapento newTapento = new Tapento(mRegisteredPOI.getId(), POICategories.BAR, t.getText().toString(), new Date(),namePhoto, 0,0, m_DeviceLocation.getLatitude(),m_DeviceLocation.getLongitude() );					
					PersistenceSQL.insertTapento(Pestana1.this, newTapento);
					
				}
				

			} catch (Exception e) {
				messageReturn = MESSAGE_ERROR;
				e.printStackTrace();
			}
			handler.sendEmptyMessage(messageReturn);
		}

	}

	/**
	 * Metodo para gestionar el click del boton de publicar Tapa/evento
	 */

	@Override
	public void onClick(View v) {

		if(typeCategorie==POICategories.BAR){
			pd = ProgressDialog.show(this, getString(R.string.INFORMACION),
					getString(R.string.PublicarTapa));
			Runnable poiWorker = new AddPOIWorkerTapa(POICategories.BAR);
			Thread thread = new Thread(poiWorker);
			thread.start();
			
		}


	}


	/**
	 * Metodo para lanzar la camara
	 */
	public void CaptureFoto() {

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, nameCap);
		Uri mCapturedImageURI = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent camaraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		startActivityForResult(camaraIntent, TAKE_PICTURE);
	}

	public void GallerySelection() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(intent, SELECT_PICTURE);
	}



	/**
	 * Metodo para limpiar la vista del usuario
	 */
	public void clean_view() {
		
		t.setText("");
		

		ImageView1.setImageBitmap(null);

	}


	/**
	 * Metodo que nos muestra las opciones del Dialog
	 */
	private void showOneDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.sharing_title))
				.setTitle("")
				.setCancelable(false)
				.setNegativeButton(getString(R.string.sharing_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								clean_view();
							}
						})
				.setPositiveButton(getString(R.string.sharing_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
						
								Intent sharingIntent = new Intent(Intent.ACTION_SEND);
								sharingIntent.setType("text/plain");
																	
								
									sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constants.TEXTO + mRegisteredPOI.getDescription()+ " .Se encuentra en " + mRegisteredPOI.getAddress());
									
															
								startActivity(Intent.createChooser(sharingIntent,"Compartir via"));


								clean_view();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Metodo para seleccionar la imagen de la galeria/camara
	 */
	private void GalleryCamSelection() {

		final String[] items = { "Camara", "Galeria" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Selección");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {// Camara
					CaptureFoto();
				} else if (item == 1) {// Galeria
					GallerySelection();
				}

			}
		});

		builder.create();
		builder.show();

	}

	/**
	 * Metodo para gestionar el resultado de la carga de la imagen
	 */
	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {

			pd.dismiss();

			switch (arg0.what) {
			case MESSAGE_ERROR:
				Toast.makeText(Pestana1.this, getString(R.string.NoPublicada),
						Toast.LENGTH_LONG).show();
				break;
			case MESSAGE_OK:
				

				// Llamamos a showondialog
				showOneDialog();
				break;
			}

			return true;
		}
	}



}
