package es.queapps.quebar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import topoos.Objects.POI;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.os.StrictMode;
import android.text.Editable;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import es.queapps.quebar.internal.ImageThreadLoader;
import es.queapps.quebar.internal.PostImageLoadedListener;
import es.queapps.quebar.internal.Utils;
import es.queapps.quebar.topoos.AccessInterface;


/**
 * Contenido de la actividad Pestaña 4 del menú que realiza la visualizacion de la lista de las tapas y eventos del usuario
 * Extiende de AnalyticsListActivity
 * @see es.queapps.quebar.analytics.AnalyticsListActivity
 * @version 1.0
 * @author Victoria Marcos
 */
public class Pestana4Nueva extends ListActivity implements OnClickListener {

	private ProgressDialog pDialog;

	private ArrayList<POI> mList;
	
	public final int MESSAGE_ERROR = -1;
	public final int MESSAGE_OK = 1;
	public final int MESSAGE_NOCITY = -2;
	
	private Handler handler = new Handler(new ResultMessageCallback());

	//private AsyncTaskDialog task;
	private int level;
	private TextView levelTapa;
	private String leveltp;
	private TextView marquee;
	private ListView lv;
	private EditText t;
	private Button buttonBuscar;
	private ImageThreadLoader imageLoader = new ImageThreadLoader();
	private double newLat;
	private double newLng;
	
	// *****Posicion GPS*****//

	private Location m_DeviceLocation = null;
	private LocationManager mLocationManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pestana4n);
		

		
		//Localizacion
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		CustomLocationListener customLocationListener = new CustomLocationListener();
		if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, customLocationListener);
        	m_DeviceLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, customLocationListener);
		m_DeviceLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        

	
		marquee=(TextView) findViewById(R.id.Title1);
		t = (EditText) findViewById(R.id.inputdir);
		buttonBuscar = (Button)findViewById(R.id.filtro); 
		buttonBuscar.setEnabled(true);
		
		
		pDialog = new ProgressDialog(Pestana4Nueva.this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setTitle(getString(R.string.INFORMACION));
		pDialog.setMessage(getString(R.string.cargandoTapas));
		pDialog.setCancelable(true);
		pDialog.setMax(15);

	
		//task = new AsyncTaskDialog();
		//task.execute();
		
		 
		 //Thread thread = new Thread(new LoadTapentosWorker());
		// thread.start();

		
		
		lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
               
				Intent intent= new Intent(Pestana4Nueva.this,DetailTapentoActivity.class); 
					
				intent.putExtra("poi", mList.get(arg2));					
				startActivity(intent);
				
				
              
			}
		});
		
		buttonBuscar.setOnClickListener(this);

		
	}

	private class LoadTapentosWorker implements Runnable {

		public void run() {

			int messageReturn = MESSAGE_OK;

			try {
				
				//Thread.sleep(1000);
				if(newLat==0 && newLng==0) messageReturn = MESSAGE_NOCITY;
				//Thread.sleep(1000);
				
				mList = (ArrayList<POI>)AccessInterface.GetUserTapentosNearAddress(Pestana4Nueva.this,
						newLat,newLng );


			} catch (Exception e) {
				messageReturn =  MESSAGE_ERROR;
			
			}

			handler.sendEmptyMessage(messageReturn);
		}
	}
	
	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {
			pDialog.dismiss();
			

			switch (arg0.what) {
			case MESSAGE_ERROR:
				Toast.makeText(Pestana4Nueva.this,
						getString(R.string.ErrorCarga), Toast.LENGTH_LONG)
						.show();
				break;
			case  MESSAGE_OK:
			
				setListAdapter(new Adapter(Pestana4Nueva.this,R.layout.lista_item, mList));			
				break;
			case  MESSAGE_NOCITY:
				Toast.makeText(Pestana4Nueva.this,
						getString(R.string.NoCity), Toast.LENGTH_LONG)
						.show();
				setListAdapter(new Adapter(Pestana4Nueva.this,R.layout.lista_item, mList));			
				break;

			}

			return true; // lo marcamos como procesado
		}
	}

	/**
	 * actualiza la localizacion
	 * implementa LocationListener
	 * @see android.location.LocationListener
	 */
	private class CustomLocationListener implements LocationListener{

		  public void onLocationChanged(Location argLocation) {
			  m_DeviceLocation = argLocation;
			  mLocationManager.removeUpdates(this);
		  }

		  public void onProviderDisabled(String provider) {}

		  public void onProviderEnabled(String provider) {}

		  public void onStatusChanged(String provider,
		    int status, Bundle extras) {}
	 }
	

	/**
	 * Clase privada para realizar la tarea de obtener la lista de ids de tapas y eventos del usuario
	 * extiende de AsyncTask
	 * @see android.os.AsyncTask<Void, Integer, Boolean>
	 */
	/*private class AsyncTaskDialog extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				

				//Obtenemos los IDS de la base de datos
				ArrayList<Integer> idarr = PersistenceSQL.getallIds(Pestana4Nueva.this);
			
				
				//Metemos los IDS en un vector de enteros para obtener posteriormente los POIS 
							
				Integer[] ids = (Integer[])idarr.toArray(new Integer[idarr.size()]);
				
				if(ids!=null && ids.length > 0){
				
					mList = (ArrayList<POI>) AccessInterface.GetUserTapentosWhere(Pestana4Nueva.this, ids);					
			
				} else {
					mList = new ArrayList<POI>();
				
				}
								

			}  catch (Exception e) {
				
				e.printStackTrace();
			}

			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progress = values[0].intValue();

			pDialog.setProgress(progress);
		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					AsyncTaskDialog.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

		//obtenemos nivel de tapa de usuario
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				pDialog.dismiss();
				setListAdapter(new Adapter(Pestana4Nueva.this, R.layout.lista_item, mList));

			
			}
		}

		@Override
		protected void onCancelled() {
			
		}
	}*/



	/**
	 * Clase adaptador para mostrar la lista de tapas y eventos
	 * extiende de ArrayAdapter
	 * @see android.widget.ArrayAdapter<POI>
	 */
	private class Adapter extends ArrayAdapter<POI> {

		private ArrayList<POI> items;

		public Adapter(Context context, int textViewResourceId,
				List<POI> items) {
			super(context, textViewResourceId, items);
			this.items = (ArrayList<POI>) items;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.lista_item, null);
			}

			final POI p = items.get(position);
			
	
			if (p != null) {
				if(p.getName().equals("evento")) v.setBackgroundColor(Color.parseColor("#5BB974"));
				else v.setBackgroundColor(Color.parseColor("#8DC1C1"));

				final TextView des = (TextView) v
						.findViewById(R.id.description);
				final ImageView image = (ImageView) v
						.findViewById(R.id.image);

				final TextView plus1 = (TextView) v.findViewById(R.id.votesplus1);
				final TextView minus1 = (TextView) v
						.findViewById(R.id.votesminus1);

				if (plus1 != null) {
					plus1.setText("+"
							+ p.getWarningcount().getDuplicated().toString());
				}

				if (minus1 != null) {
					minus1.setText("-"
							+ p.getWarningcount().getClosed().toString());
				}

				if (des != null) {
					if(p.getName().equals("evento"))
					{
					 des.setText(p.getDescription()+ " publicado el dia " + Utils.getLegibleDate(p.getRegistertime()));
					}else	
						
						des.setText(p.getDescription() + ". Direccion: " + p.getAddress());
				}

				if (image != null) {
					
					    if(p.getName().equals("evento"))
					    {
					    	 
					    	 int id = getResources().getIdentifier("evento", "drawable", getPackageName());						      
					    	 image.setImageResource(id);
					    	
					    
					    }else
					    {
					    	image.setImageBitmap(null);
							Bitmap cachedImage = null;
							try {

								PostImageLoadedListener pill = new PostImageLoadedListener(
										image);
							
								cachedImage = imageLoader
										.loadImage(
												topoos.Images.Operations
														.GetImageURIThumb(
																p.getName(),
																topoos.Images.Operations.SIZE_SMALL),
												pill);


							} catch (Exception e) {
								
							}
							if (cachedImage != null) {
								image.setImageBitmap(cachedImage);
							}
						
					    }


				}

			}

			return v;

		}

	}



	/**
	 * metodo para obtener los puntos y poder generar posteriormente el nivel
	 * @param POI
	 * @return puntos
	 */
	private static int puntosPOI(POI p)
	{
		int retorno = -1;
		if (p == null)
		{
			return retorno;
		}else
		{
			int neg = p.getWarningcount().getClosed();
			int pos = p.getWarningcount().getDuplicated();
			retorno = pos + neg;
			return retorno;
			
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.filtro:
			mostrarCiudad();		
			break;
		
			
		default:
			break;
		}
		
	}
	
	private void mostrarCiudad()
	{
		Editable s = t.getEditableText();
		if(!s.equals(""))
		{
			
			ArrayList<String> newPoint = obtenerLatLongFromAddress(s);
			newLat = Double.parseDouble(newPoint.get(0));
			newLng = Double.parseDouble(newPoint.get(1));
		
			
			 
			 Thread thread = new Thread(new LoadTapentosWorker());
			 thread.start();
			
			
		}else 
		{
			Toast toast = Toast.makeText(this, "ES NADA, ASI QUE" + s,  Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private ArrayList<String> obtenerLatLongFromAddress(Editable s)
	{
		ArrayList<String> npoint = new ArrayList<String>();
		String dir = s.toString().trim().replace(" ", ""); 
		
		String lat, lng = "0";
	
		//StringBuilder json = new StringBuilder();
		 String stringUrl ="https://maps.googleapis.com/maps/api/geocode/json?address="+dir;
		 try{
			 
			 int SDK_INT = android.os.Build.VERSION.SDK_INT;
			    if (SDK_INT > 8) 
			    {
			        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			                .permitAll().build();
			        StrictMode.setThreadPolicy(policy);
			        //your codes here

			    }
			   HttpGet httpGet = new HttpGet(stringUrl);
			   HttpClient httpClient = new DefaultHttpClient();
			   HttpResponse response = (HttpResponse)httpClient.execute(httpGet);
			   HttpEntity entity = response.getEntity();
			   BufferedHttpEntity buffer = new BufferedHttpEntity(entity);
			   InputStream iStream = buffer.getContent();
			                    
			   String aux = "";
			            
			   BufferedReader r = new BufferedReader(new InputStreamReader(iStream));
			   new StringBuilder();
			   String line;
			   while ((line = r.readLine()) != null) {
			     aux += line;
			   }
			 //  Log.i("AAAAAAAAAAAAAAAAAAAAAAAUUUUUUUUUUUUX: ",aux);
			   JSONObject jsonObject = new JSONObject(aux);
			   JSONArray resultados = jsonObject.getJSONArray("results");
			   JSONObject resultado = resultados.getJSONObject(0);
			   JSONObject geometries = resultado.getJSONObject("geometry");
			   JSONObject locationN = geometries.getJSONObject("location");
			   lat = locationN.getString("lat");
			   lng = locationN.getString("lng");
			   
			 
			   
		   npoint.add(0, lat);
		   npoint.add(1,lng);
		return npoint;
		
	}
		 catch(IOException e){
			// Log.i("ERROR", "Error connecting to service", e);
			 e.printStackTrace();
	           
		 } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return npoint;
	}
	
	
}