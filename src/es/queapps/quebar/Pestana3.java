package es.queapps.quebar;

import java.util.List;

import topoos.Objects.POI;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import es.queapps.quebar.internal.ImageThreadLoader;
import es.queapps.quebar.internal.PostImageLoadedListener;
import es.queapps.quebar.internal.Utils;
import es.queapps.quebar.topoos.AccessInterface;


/**
 * Contenido de la actividad Pestaña 3 del menú que realiza la visualizacion en el mapa de las tapas y eventos cercanos al usuario
 * Extiende de CustomMapActivity e implementa el interfaz OnMarkerClickListener 
 * @see es.queapps.quebar.CustomMapActivity
 * @see com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
 * @version 1.0
 * @author Victoria Marcos
 */
public class Pestana3 extends CustomMapActivity implements OnMarkerClickListener{
	
	final int RQS_GooglePlayServices = 1;
	

	private ImageThreadLoader imageLoader = new ImageThreadLoader();


	public final int MESSAGE_ERROR = -1;
	public final int MESSAGE_OK = 1;

	private LocationManager mLocationManager;
	private Location m_DeviceLocation = null;
	private SupportMapFragment fm;
	
	private GoogleMap mMap = null;
	private Handler handler = new Handler(new ResultMessageCallback());


	private ProgressDialog pDialog = null;
	
	
	private List<POI> m_pois = null;

		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// hide titlebar of application must be before setting the layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.pestana3);			
		


		//obtenemos mapa
		fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview); 
	    mMap = fm.getMap();
	    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //establecemos tipo de mapa       
	    mMap.setMyLocationEnabled(true);

	    mMap.getUiSettings().setZoomControlsEnabled(true);
	    mMap.getUiSettings().setCompassEnabled(true);


        mMap.setOnMarkerClickListener(this);

        mMap.setInfoWindowAdapter(new InfoWindowAdapter(){ //adaptador de la ventana que sale al pulsar el marcador

			@Override
			public View getInfoContents(Marker marker) {
				// TODO Auto-generated method stub
				View popup =getLayoutInflater().inflate(R.layout.popup_layout, null);

				ImageView imagen = (ImageView)popup.findViewById(R.id.image);
					
				
				
				TextView tv=(TextView)popup.findViewById(R.id.title);
				TextView ts=(TextView)popup.findViewById(R.id.snippet);
					 		    
			    ts.setText(marker.getSnippet());		
				if(marker.getTitle().equals("evento")){
					 int id = getResources().getIdentifier("evento", "drawable", getPackageName());						      
			    	 imagen.setImageResource(id);
			    	 tv.setText(marker.getTitle());
				}else {
					
					tv.setText("bar");
					imagen.setImageBitmap(null);
					
					
					Bitmap cachedImage = null;
					try {

						PostImageLoadedListener pill = new PostImageLoadedListener(
								imagen);
						
					
						
						cachedImage = imageLoader
								.loadImage(
										topoos.Images.Operations
												.GetImageURIThumb(
														marker.getTitle(),
														topoos.Images.Operations.SIZE_SMALL),
										pill);



					} catch (Exception e) {
						
					}
					if (cachedImage != null) {
						imagen.setImageBitmap(cachedImage);
					}
				}
				return popup;
			}

			@Override
			public View getInfoWindow(Marker arg0) {
				
				return null;
			}
        	
        }       
        
        );
    
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

		
	}


	 

	/**
	 * clase privada para la gestion y actualizacion de la localizacion
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

	

	
	 @Override
	 protected void onResume() {
	
	  super.onResume();

	  int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
	  
	  if (resultCode == ConnectionResult.SUCCESS){
	   Toast.makeText(getApplicationContext(), 
	     "isGooglePlayServicesAvailable SUCCESS", 
	     Toast.LENGTH_LONG).show();
	  }else{
	   GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
	  }
	  
	 }

	    @Override
	    protected void onPause() {
	        fm.onPause();
	        super.onPause();
	    }

	   /* @Override
	    protected void onDestroy() {
	        fm.onDestroy();
	        super.onDestroy();
	    }*/

	    @Override
	    public void onLowMemory() {
	        super.onLowMemory();
	        fm.onLowMemory();
	    }


	


	/**
	 * Añade los marcadores de las tapas y eventos en el mapa después de cargarlas
	 */
	private void drawTapas() {

		
		if (m_pois != null) {
			for (final POI p : m_pois) {
				
				LatLng markerPosition = new LatLng (p.getLatitude() ,p.getLongitude());	
				
		
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,11));				
				String descrip =""; //para la descripcion de la tapa y/o evento
				if(p.getName().equals("evento")) descrip = p.getDescription()+ " publicado el dia " +Utils.getLegibleDate(p.getRegistertime());
				else descrip = p.getDescription();
				//añade el marcador al mapa
				mMap.addMarker(new MarkerOptions()			        
			        .position(markerPosition)
			        .title(p.getName())
			        .snippet(descrip)
			        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_mapa))
			        .anchor(0.5f, 0.5f));
				
			}
		}
		
	}
	

	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {
			pDialog.dismiss();

			switch (arg0.what) {
			case MESSAGE_ERROR:
				Toast.makeText(Pestana3.this,
						getString(R.string.ErrorCarga), Toast.LENGTH_LONG)
						.show();
				break;
			case MESSAGE_OK:

				
				drawTapas();
				break;
			}

			return true; // lo marcamos como procesado
		}
	}




	/**
	 * clase privada para la carga de tapas y eventos en segundo plano o background
	 * implementa Runnable
	 * @see  java.lang.Runnable
	 */
	private class LoadTapentosWorker implements Runnable {

		public void run() {

			int messageReturn = MESSAGE_OK;

			try {
				Thread.sleep(1000);
				m_pois = AccessInterface.GetUserTapentosNear(Pestana3.this,
						m_DeviceLocation.getLatitude(),m_DeviceLocation.getLongitude() );
				


			} catch (Exception e) {
				messageReturn =  MESSAGE_ERROR;
				Log.e("Upload", e.getMessage());
			}
           
			handler.sendEmptyMessage(messageReturn);
		}
	}


	@Override
	public boolean onMarkerClick(Marker marcador) {

	  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marcador.getPosition(),17));
      marcador.showInfoWindow();
      
	  return true;
	}
	

}


