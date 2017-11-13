package es.queapps.quebar;

import java.util.ArrayList;
import java.util.List;

import topoos.Objects.POI;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import es.queapps.quebar.internal.ImageThreadLoader;
import es.queapps.quebar.internal.PersistenceSQL;
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
public class Pestana4 extends ListActivity {

	private ProgressDialog pDialog;

	private ArrayList<POI> mList;

	private AsyncTaskDialog task;
	private int level;
	private TextView levelTapa;
	private String leveltp;
	private TextView marquee;
	private ListView lv;
	
	private ImageThreadLoader imageLoader = new ImageThreadLoader();
	
	// *****Posicion GPS*****//

	private Location m_DeviceLocation = null;
	private LocationManager mLocationManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pestana4);
		

		
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
				
		
		
		pDialog = new ProgressDialog(Pestana4.this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setTitle(getString(R.string.INFORMACION));
		pDialog.setMessage(getString(R.string.cargandoTapas));
		pDialog.setCancelable(true);
		pDialog.setMax(15);

	
		task = new AsyncTaskDialog();
		task.execute();
		
		
		lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
               
				Intent intent= new Intent(Pestana4.this,DetailTapentoActivity.class); 
					
				intent.putExtra("poi", mList.get(arg2));					
				startActivity(intent);
				
				
              
			}
		});
		
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
	private class AsyncTaskDialog extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				

				//Obtenemos los IDS de la base de datos
				ArrayList<Integer> idarr = PersistenceSQL.getallIds(Pestana4.this);
			
				
				//Metemos los IDS en un vector de enteros para obtener posteriormente los POIS 
							
				Integer[] ids = (Integer[])idarr.toArray(new Integer[idarr.size()]);
				
				if(ids!=null && ids.length > 0){
				
					mList = (ArrayList<POI>) AccessInterface.GetUserTapentosWhere(Pestana4.this, ids);					
			
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
				setListAdapter(new Adapter(Pestana4.this, R.layout.lista_item, mList));

			
			}
		}

		@Override
		protected void onCancelled() {
			
		}
	}



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
	
	
}