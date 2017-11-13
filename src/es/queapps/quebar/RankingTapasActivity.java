package es.queapps.quebar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;



/**
 * Clase para representar la actividad de la pantalla de entrada a la aplicacion. 
 * Extiende de AnalyticsActivity
 * @see es.queapps.quebar.analytics.AnalyticsActivity
 * @version 1.0
 * @author Victoria Marcos
 */
public class RankingTapasActivity extends Activity {

	private static final int SLEEP_TIME = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.splash);
		
		Thread thread = new Thread(new SleepWorker());
		thread.start();
	}
	
	private void goToMainActivity()
	{
		Intent intent = new Intent(this, MainActivity.class);
		this.startActivity(intent);
		this.finish();
	}
	
	private class SleepWorker implements Runnable {
			
			public void run(){
				try {
					Thread.sleep(SLEEP_TIME * 1000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
				
				goToMainActivity();
			}
		}
	
}
