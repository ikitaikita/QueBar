package es.queapps.quebar;

import java.util.ArrayList;
import java.util.List;

import topoos.Objects.POI;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
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
import android.widget.Toast;



//import es.queapps.quebar.analytics.AnalyticsListActivity;
import es.queapps.quebar.internal.ImageThreadLoader;
import es.queapps.quebar.internal.PostImageLoadedListener;
import es.queapps.quebar.internal.Utils;
import es.queapps.quebar.topoos.AccessInterface;
import es.queapps.quebar.widget.PullToRefreshListView;
import es.queapps.quebar.widget.PullToRefreshListView.OnRefreshListener;



/**
 * Contenido de la actividad Pestaña 2 del menú que realiza la visualizacion de la lista de las tapas y eventos cercanos al usuario
 * Extiende de AnalyticsListActivity   
 * @see  es.queapps.quebar.analytics.AnalyticsListActivity
 * @version 1.0
 * @author Victoria Marcos
 */
public class Pestana2 extends AnalyticsListActivity {


	private ImageThreadLoader imageLoader = new ImageThreadLoader();

	public final int MESSAGE_ERROR = -1;
	public final int MESSAGE_OK = 1;
	private Handler handler = new Handler(new ResultMessageCallback());
	
	private List<POI> m_pois = null;
	private ProgressDialog pDialog = null;
	


	private Location m_DeviceLocation = null;
	private LocationManager mLocationManager;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.pestana2);

		
		//Localizacion
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		CustomLocationListener customLocationListener = new CustomLocationListener();
		if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, customLocationListener);
        	m_DeviceLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, customLocationListener);
		m_DeviceLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


		
		 pDialog = ProgressDialog.show(this, getString(R.string.INFORMACION), getString(R.string.cargandoTapas));
		
		
		 
		 Thread thread = new Thread(new LoadTapentosWorker());
		 thread.start();

		ListView lv=  getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
               
				int fixedpos = arg2 - 1;
				
				
				Intent intent= new Intent(Pestana2.this,DetailTapentoActivity.class); 

				intent.putExtra("poi", m_pois.get(fixedpos));

                startActivity(intent);
                               
			}
		});


	}
	
	/**
	 * actualiza la localizacion. Implementa LocationListener
	 * @see android.location.LocationListener
	 *
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

	
	public void onStart(){
		super.onStart();
		((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Se actualiza la lista de tapas y eventos                
            	new GetDataTask().execute();
            }
        });	
	}
	



	/**
	 * Carga las Tapas y eventos en background. Implementa Runnable
	 * @see java.lang.Runnable
	 * 
	 */
	private class LoadTapentosWorker implements Runnable {

		public void run() {

			int messageReturn = MESSAGE_OK;

			try {
				Thread.sleep(1000);
				m_pois = (ArrayList<POI>)AccessInterface.GetUserTapentosNear(Pestana2.this,
						m_DeviceLocation.getLatitude(),m_DeviceLocation.getLongitude() );


			} catch (Exception e) {
				messageReturn =  MESSAGE_ERROR;
			
			}

			handler.sendEmptyMessage(messageReturn);
		}
	}

	/**
	 * Metodo resultCalback LoadTapentosWorker
	 */
	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {
			pDialog.dismiss();
			

			switch (arg0.what) {
			case MESSAGE_ERROR:
				Toast.makeText(Pestana2.this,
						getString(R.string.ErrorCarga), Toast.LENGTH_LONG)
						.show();
				break;
			case  MESSAGE_OK:
			
				setListAdapter(new Adapter(Pestana2.this,R.layout.lista_item, m_pois));			
				break;

			}

			return true; // lo marcamos como procesado
		}
	}



	/**
	 * Clase Adaptador para mostrar los items como deseamos
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
			if(p.getName().equals("evento")) v.setBackgroundColor(Color.parseColor("#2BB14F"));
			else v.setBackgroundColor(Color.parseColor("#8DC1C1"));

			if (p != null) {

				final TextView des = (TextView) v
						.findViewById(R.id.description);
				final ImageView imagen = (ImageView) v
						.findViewById(R.id.image);

				final TextView mas1 = (TextView) v.findViewById(R.id.votesplus1);
				final TextView menos1 = (TextView) v
						.findViewById(R.id.votesminus1);

				if (mas1 != null) {
					mas1.setText("+"
							+ p.getWarningcount().getDuplicated().toString());
				}

				if (menos1 != null) {
					menos1.setText("-"
							+ p.getWarningcount().getClosed().toString());
				}

				if (des != null) {
					if(p.getName().equals("evento"))
					{
					 des.setText(p.getDescription() + " publicado el dia " + Utils.getLegibleDate(p.getRegistertime()));
					}else	
					//des.setText("POIid " + p.getId() + " " + p.getDescription());
					des.setText(p.getDescription() + ". Direccion: " + p.getAddress());
				}

				if (imagen != null) {
					
					    if(p.getName().equals("evento"))
					    {
					    	 
					    	 int id = getResources().getIdentifier("evento", "drawable", getPackageName());						      
					    	 imagen.setImageResource(id);
					    	
					    	
					    }else
					    {
					    	imagen.setImageBitmap(null);
							Bitmap cachedImage = null;
							try {

								PostImageLoadedListener pill = new PostImageLoadedListener(
										imagen);
								
								cachedImage = imageLoader
										.loadImage(topoos.Images.Operations.GetImageURIThumb(p.getName(),topoos.Images.Operations.SIZE_SMALL),
												pill);



							} catch (Exception e) {
								
							}
							if (cachedImage != null) {
								imagen.setImageBitmap(cachedImage);
							}
						
					    }


				}

			}

			return v;

		}

	}


	/**
	 * Clase privada GetDataTask para la tarea asincrona de cargar la lista 
	 * extiende de AsyncTask
	 * @see android.os.AsyncTask<Void, Void, List<POI>>
	 */
	private class GetDataTask extends AsyncTask<Void, Void, List<POI>> {
		

	        @Override
	       protected List<POI> doInBackground(Void...params) {

	             	
	        	try {
	        		m_pois = AccessInterface.GetUserTapentosNear(Pestana2.this,
							m_DeviceLocation.getLatitude(),m_DeviceLocation.getLongitude() );
					
	        		
	        			
	        		
				} catch (Exception e) {
					e.printStackTrace();
				}

	        	return m_pois;
	        	
	        }

	        @Override
	        protected void onPostExecute(List<POI> result) {
	        	
	        
	        	
	        	setListAdapter(new Adapter(Pestana2.this, //Modificamos la lista mediante el adaptador
   					R.layout.lista_item, m_pois));
	        	
	        	((PullToRefreshListView) getListView()).onRefreshComplete();
	        		        	
	        	 super.onPostExecute(result);
	        }
	    }
	
}
