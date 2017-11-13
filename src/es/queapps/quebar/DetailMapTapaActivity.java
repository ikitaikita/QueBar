package es.queapps.quebar;

import topoos.Objects.POI;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import es.queapps.quebar.internal.Utils;


/**
 * Clase para representar el punto de localizacion de la tapa y/o evento en el mapa.
 * Extiende de CustomMapActivity
 * @see es.queapps.quebar.CustomMapActivity
 * @version 1.0
 * @author Victoria Marcos
 */
public class DetailMapTapaActivity extends CustomMapActivity {
	private ImageButton gotoDetail; 
	private Activity activity;
	private POI mPOI;
	final int RQS_GooglePlayServices = 1;

	private GoogleMap mMap = null;

	

	private SupportMapFragment fm;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		activity=this;
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_maptapento_layout);		
		mPOI = (topoos.Objects.POI)getIntent().getExtras().getSerializable("poi"); 

		fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapviewdetail);
		

		mMap = fm.getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);
		LatLng position = new LatLng(mPOI.getLatitude(), mPOI.getLongitude()); 
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,17));
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.getUiSettings().setCompassEnabled(true);
		
		String descrip ="";
		String title ="";
		if( mPOI.getName().equals("evento")) 
			{
				descrip =  mPOI.getDescription()+ " publicado el dia " +Utils.getLegibleDate( mPOI.getRegistertime());
				title = mPOI.getName();
			}
		else 
			{
				descrip =  mPOI.getDescription();
				title = "bar";
			}
		
		 mMap.addMarker(new MarkerOptions() 
	        .position(position)
	        .title(title)
	        .snippet(descrip)
	        .icon(BitmapDescriptorFactory
	               .fromResource(R.drawable.icono_mapa))
	        .anchor(0.5f, 0.5f));
		

				
		gotoDetail = (ImageButton) findViewById(R.id.gotoDetail);
		gotoDetail.setOnClickListener(new OnClickListener() {

		
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),DetailTapentoActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("poi",mPOI);
				startActivity(intent);
				overridePendingTransition(R.anim.scale_from_corner, R.anim.scale_to_corner);
				activity.finish();
						
			}
		});

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


}
