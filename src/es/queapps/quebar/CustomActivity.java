package es.queapps.quebar;

import com.google.analytics.tracking.android.EasyTracker;

import topoos.AccessTokenOAuth;
import android.app.Activity;
import android.os.Bundle;
import es.queapps.quebar.topoos.AccessInterface;

/**
 * Clase de la que extienden ciertas actividades.
 * Configura el acceso a Topoos y configura la plataforma Analytics 
 * Extiende de clase Activity
 * @see android.app.Activity
 * @version 1.0
 * @author Victoria Marcos
 */
public class CustomActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 
			topoos.AccessTokenOAuth token = new AccessTokenOAuth(AccessInterface.TOPOOS_ADMIN_APP_TOKEN);
		 	token.save_Token(this);
	
	}
	

	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); 
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

}
