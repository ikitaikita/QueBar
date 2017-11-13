package es.queapps.quebar;


import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import es.queapps.quebar.R.color;






import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;




/**
 * Clase para la actividad principal de la aplicación y carga las pestanyas del menú de navegación. 
 * Extiende de TabActivity
 * @see android.app.TabActivity
 * @version 1.0
 * @author Victoria Marcos
 */
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	
	//private AdView mAdView;

	private static final int MENU_ABOUT = Menu.FIRST;
	private static final int MENU_QUIT = Menu.FIRST + 1;
	 
	//private static final String AD_UNIT_ID = "ca-app-pub-6628319787253203/4339611373";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		

		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
	
		//mAdView = new AdView(this);
		//mAdView.setAdSize(AdSize.BANNER);
		//mAdView.setAdUnitId(AD_UNIT_ID);
	

	

		final TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

	        @Override
	        public void onTabChanged(String tabId) {
	       
	        }

		});

		Intent intent;
		Resources res = getResources();

		// Pestaña1
		intent = new Intent().setClass(this, Pestana1.class);
		spec = tabHost
				.newTabSpec("Pestana1")
				//.setIndicator("",res.getDrawable(R.drawable.pestana1_style))
				.setIndicator(this.getString(R.string.tab1))
				.setContent(intent);
	
				
		tabHost.addTab(spec);

		// Pestaña2
		intent = new Intent().setClass(this, Pestana2.class).addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

		spec = tabHost
				.newTabSpec("Pestana2")
				//.setIndicator("",res.getDrawable(R.drawable.pestana2_style))
				.setIndicator(this.getString(R.string.tab2))
				.setContent(intent);
		
		tabHost.addTab(spec);
		
		// Pestaña3
		intent = new Intent().setClass(this, Pestana3.class).addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
		spec = tabHost
				.newTabSpec("Pestana3")
				//.setIndicator("",res.getDrawable(R.drawable.pestana3_style))
				.setIndicator(this.getString(R.string.tab3))
				.setContent(intent);
		tabHost.addTab(spec);
		

		// Pestaña4
		intent = new Intent().setClass(this, Pestana4Nueva.class).addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

		spec = tabHost
				.newTabSpec("Pestana4")
				//.setIndicator("",res.getDrawable(R.drawable.pestana4_style))
				.setIndicator(this.getString(R.string.tab4))
				.setContent(intent);
		tabHost.addTab(spec);
		

	
		
		//background color tabHost
		 getTabHost().getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.pestana1_drawable_shape);//Pestaña1
		// getTabHost().getTabWidget().getChildAt(0).setBackgroundColor(color.tit);
		 getTabHost().getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.pestana2_drawable_shape);//Pestaña2
		 //getTabHost().getTabWidget().getChildAt(1).setBackgroundColor(color.tit);
		 getTabHost().getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.pestana3_drawable_shape);//Pestaña3
		 //getTabHost().getTabWidget().getChildAt(2).setBackgroundColor(color.tit);
		 getTabHost().getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.pestana4_drawable_shape);//Pestaña4		
		// getTabHost().getTabWidget().getChildAt(3).setBackgroundColor(color.tit);
		 
	
		// LinearLayout layout = (LinearLayout) findViewById(R.id.layfinal);
		 //layout.addView(mAdView);
	
		 //AdRequest adRequest = new AdRequest.Builder().build();
		 
	
		 //mAdView.loadAd(adRequest);
	}
	

	public void quit(){
		
		setResult(RESULT_OK);
		finish();
		
	}
	
	public void AcercaDe(){
		Intent intent = new Intent(this, AboutActivity.class);
		this.startActivity(intent);						
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ABOUT, 0, getString(R.string.about));
		menu.add(0, MENU_QUIT, 0, getString(R.string.exit));
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ABOUT:
				AcercaDe();
				return true;
			case MENU_QUIT:
				quit();
				return true;
		}
		return false;
	}
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); 
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); 
	}

}