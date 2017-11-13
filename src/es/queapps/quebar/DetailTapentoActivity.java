package es.queapps.quebar;


import topoos.Objects.POI;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import es.queapps.quebar.internal.Constants;
import es.queapps.quebar.internal.ImageThreadLoader;
import es.queapps.quebar.internal.PersistenceSQL;
import es.queapps.quebar.internal.PostImageLoadedListener;
import es.queapps.quebar.internal.Utils;
import es.queapps.quebar.topoos.AccessInterface;


/**
 * Clase de actividad para la pantalla del detalle de la tapa y/o evento. 
 * Extiende de CustomActiviy e implementa OnClickListener para los botones de votacion y sharing, compartir
 * @see es.queapps.quebar.CustomActivity
 * @see android.view.View.OnClickListener
 * @version 1.0
 * @author Victoria Marcos
 */
public class DetailTapentoActivity extends CustomActivity implements
		OnClickListener {

	private static final int WORKER_MSG_OK = 1;
	private static final int WORKER_MSG_ERROR = -1;

	private ImageThreadLoader imageLoader = new ImageThreadLoader();
	private Handler handler = new Handler(new ResultMessageCallback());
	private ProgressDialog progressDialog;

	//layout
	private ImageView photo;
	private TextView description;
	private ImageView pointPlus;
	private ImageView pointLess;
	private ImageView share;
	private TextView nVotesPlus;
	private TextView nVotesMinus;
	private RatingBar ratingBar;
	
	private ImageButton gotoMap;
	
	private Activity activity;
	private topoos.Objects.POI mPOI;
	private float puntos_rating;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity = this;
		setContentView(R.layout.detail_tapento_layout);
		photo = (ImageView) findViewById(R.id.tapaPhoto);
		description = (TextView) findViewById(R.id.tapaDescription);
		pointPlus = (ImageView) findViewById(R.id.votesPlus);
		pointLess = (ImageView) findViewById(R.id.votesMinus);
		pointLess.setOnClickListener(this);
		pointPlus.setOnClickListener(this);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		puntos_rating=0;
		

		share = (ImageView) findViewById(R.id.share);
		share.setOnClickListener(this);
		nVotesPlus = (TextView) findViewById(R.id.textPlus);
		nVotesMinus = (TextView) findViewById(R.id.textMinus);

		mPOI = (topoos.Objects.POI) getIntent().getExtras().getSerializable("poi");
		
		

		gotoMap = (ImageButton) findViewById(R.id.gotoMap);

		gotoMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			

				Intent intent = new Intent(getApplicationContext(),
						DetailMapTapaActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("poi", mPOI);			

				startActivity(intent);
				overridePendingTransition(R.anim.scale_from_corner,
						R.anim.scale_to_corner);
				activity.finish();

			}
		});

	
		description.setMovementMethod(ScrollingMovementMethod.getInstance());

		Bundle extras = getIntent().getExtras();
		if (extras != null) {          
		
		
		}
		
		if (mPOI != null) {

			int votosPositivos = mPOI.getWarningcount().getDuplicated();
			int votosNegativos = mPOI.getWarningcount().getClosed();
			
			
			nVotesPlus.setText(String.valueOf(votosPositivos));//obtenemos los votos positivos de los POIs

			nVotesMinus.setText(String.valueOf(votosNegativos));//obtenemos los votos negativos de los POIs
			
			puntos_rating = obtenerRating(votosPositivos, votosNegativos);
		
			ratingBar.setRating(puntos_rating);
			

			if(mPOI.getName().equals("evento"))
			{
				int id = getResources().getIdentifier("evento", "drawable", getPackageName());		
				photo.setAdjustViewBounds(true);
		    	photo.setImageResource(id);
		    	description.setText("el siguiente evento publicado el dia " + Utils.getLegibleDate(mPOI.getRegistertime()));
		    	
			}else{
				photo.setImageBitmap(null);
				Bitmap cachedImage = null;
			
				try {
					

					PostImageLoadedListener pill = new PostImageLoadedListener(
							photo);
	
					cachedImage = imageLoader.loadImage(topoos.Images.Operations
							.GetImageURIThumb(mPOI.getName(),
								topoos.Images.Operations.SIZE_LARGE), pill);
					description.setText(mPOI.getDescription()+ ". Direccion: " + mPOI.getAddress());


				} catch (Exception e) {
					
				}
				if (cachedImage != null) {
					photo.setImageBitmap(cachedImage);
				}
			}

			boolean voted = PersistenceSQL.isVotedTV(mPOI.getId(), this);
			setVoteButtonsStatus(voted);

		}
	}

	

	/**
	 * metodo que establece el estado de los botones de votacion
	 * @param boolean isVoted
	 */
	private void setVoteButtonsStatus(boolean isVoted) {
		if (isVoted) {
			pointLess.setEnabled(false);
			pointPlus.setEnabled(false);

		} else {
			pointLess.setEnabled(true);
			pointPlus.setEnabled(true);

		}
	}
	
	
	/**
	 * metodo para obtener los puntos y poder generar posteriormente el nivel
	 * @param POI
	 * @return puntos
	 */
	private static float obtenerRating(int pos, int neg)
	{
		float retorno = 0;
		int puntosTotales = pos - neg;		
	    if(puntosTotales<=0)retorno = 0;
	    else if(puntosTotales<10)retorno=1;
	    else if(puntosTotales<20)retorno=2;
	    else if(puntosTotales<30)retorno=3;
	    else if(puntosTotales>40)retorno=4;
	
		return retorno;
		
	
	}//END puntosPOI
    /**
     * Clase privada para la gestion de la actividad DetailTapentoActivity
     * @author Victoria Marcos
     *
     */
	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {

			// Cerramos la pantalla de progreso

			switch (arg0.what) {
			case WORKER_MSG_ERROR:
				Toast.makeText(DetailTapentoActivity.this, "Error",
						Toast.LENGTH_LONG).show();
				break;
			case WORKER_MSG_OK:
				setVoteButtonsStatus(true);
				break;
			}

			progressDialog.dismiss();
			return true; 
		}
	}

	/**
	 * metodo que añade voto negativo 
	 */
	private class AddNegativeVoteBackground implements Runnable {
		
		public void run() {

			int messageReturn = WORKER_MSG_OK;
			try {

				AccessInterface.AddNegativeVote(DetailTapentoActivity.this, mPOI.getId());
				PersistenceSQL.addVoteTV(mPOI.getId(), DetailTapentoActivity.this);

		
			} catch (Exception e) {
				messageReturn = WORKER_MSG_ERROR;
			}

			runOnUiThread(new Runnable() {
				public void run() {
					nVotesMinus.setText(String.valueOf(mPOI.getWarningcount()
							.getClosed() + 1));

				}
			});

			handler.sendEmptyMessage(messageReturn);
		}
	}

	/**
	 * metodo que añade voto positivo
	 */
	private class AddPositiveVoteBackground implements Runnable {
		
		public void run() {

			int messageReturn = WORKER_MSG_OK;
			try {

				AccessInterface.AddPositiveVote(DetailTapentoActivity.this, mPOI.getId());

				PersistenceSQL.addVoteTV(mPOI.getId(), DetailTapentoActivity.this);
				
			} catch (Exception e) {
				messageReturn = WORKER_MSG_ERROR;
			}

			runOnUiThread(new Runnable() {
				public void run() {
					nVotesPlus.setText(String.valueOf(mPOI.getWarningcount()
							.getDuplicated() + 1));


				}
			});

			handler.sendEmptyMessage(messageReturn);
		}
	}
	

	
	


	@Override
	public void onClick(View v) {
		Thread thread;
		switch (v.getId()) {

		case R.id.votesPlus:
			progressDialog = ProgressDialog.show(this,
					this.getString(R.string.espere),
					this.getString(R.string.evotacion));
			thread = new Thread(new AddPositiveVoteBackground());
			thread.start();
			break;
		case R.id.votesMinus:
			progressDialog = ProgressDialog.show(this,
					this.getString(R.string.espere),
					this.getString(R.string.evotacion));
			thread = new Thread(new AddNegativeVoteBackground());
			thread.start();
			break;

		case R.id.share:
			showOneDialog();
		default:
			break;
		}

	}

	/**
	 * Metodo para mostrar cuadro de dialogo de informacion compartir 
	 */
	private void showOneDialog() {

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);	
		
		sharingIntent.setType("text/plain");
		
											
		
	
		if(mPOI.getName().equals("evento"))
		{ 
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "evento: " + mPOI.getDescription());
		}else 
			{
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constants.TEXTO + mPOI.getDescription()+ " .Se encuentra en " + mPOI.getAddress());
			    
				
			}
			
		
		
		
		startActivity(Intent.createChooser(sharingIntent,"Compartir via"));


	}

}