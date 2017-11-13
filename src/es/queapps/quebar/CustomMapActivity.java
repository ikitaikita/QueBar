package es.queapps.quebar;



import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.GooglePlayServicesUtil;


import es.queapps.quebar.topoos.AccessInterface;

/**
 * Clase de la que extienden las actividades relacionadas con los mapas.
 * Configura acceso a plataforma Topoos y plataforma Analytics
 * @see android.support.v4.app.FragmentActivity
 * @version 1.0
 * @author Victoria Marcos
 */
public abstract class CustomMapActivity extends android.support.v4.app.FragmentActivity{




	//Acceso a Plataforma Topoos y configuración Analytics
	@Override
	public void onStart() {
		super.onStart();

		EasyTracker.getInstance(this).activityStart(this); 
		AccessInterface.initializeTopoosSession(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); 
	}



	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	  // Inflate the menu; this adds items to the action bar if it is present.
	  getMenuInflater().inflate(R.menu.activity_main, menu);
	  return true;
	 }

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	  switch (item.getItemId()) {
	     case R.id.menu_settings:
	      String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
	        getApplicationContext());
	      AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(CustomMapActivity.this);
	      LicenseDialog.setTitle("Legal Notices");
	      LicenseDialog.setMessage(LicenseInfo);
	      LicenseDialog.show();
	         return true;
	     }
	  return super.onOptionsItemSelected(item);
	 }
	 

	
}
