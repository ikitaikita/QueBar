package es.queapps.quebar.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;


/**
 * Clase para la gestion de la carga de la imagen a traves de una URL. 
 * @version 1.0
 * @author Victoria Marcos
 */
public class ImageThreadLoader {

	// cache global de imagenes
	// Usando SoftReference para permitir, si es necesario recolector de basura para limpiar caché
	private ConcurrentHashMap<URL, SoftReference<Bitmap>> cache = new ConcurrentHashMap<URL, SoftReference<Bitmap>>();

	private ConcurrentLinkedQueue<QueueItem> queue = new ConcurrentLinkedQueue<QueueItem>();

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private Handler handler = new Handler(); // Assumes that this is started from the main (UI) thread

	//Define una interfaz para una devolución de llamada que se encargará de las respuestas  
	//a la hora de cargarse la imagen que se haga
	
	public interface ImageLoadedListener {

		public void imageLoaded(Bitmap imageBitmap);

	}

	private final class QueueItem {

		public URL url;
		public ImageLoadedListener listener;

	}

	private class NotifyImageLoadedTask implements Runnable {

		private ImageLoadedListener listener;
		private Bitmap bitmap;

		public NotifyImageLoadedTask(ImageLoadedListener listener, Bitmap bitmap) {
			this.listener = listener;
			this.bitmap = bitmap;
		}

		@Override
		public void run() {
			listener.imageLoaded(bitmap);
		}

	}


	/**
	 * Proporciona una clase Ejecutable para manejar la carga de la imagen de la URL y de ajuste del ImageView en el subproceso de 
	 * interfaz de usuario
	 */
	private class QueueRunner implements Runnable {

		@Override
		public void run() {
			QueueItem item = queue.poll();

			SoftReference<Bitmap> ref = cache.get(item.url);

			Bitmap bitmap = null;
			if (ref != null)
				bitmap = ref.get();

			// If in the cache, return that copy and be done
			if (bitmap != null) {
				if (item.listener != null)
					// Use a handler to get back onto the UI thread for the update
					handler.post(new NotifyImageLoadedTask(item.listener, bitmap));
			} else {
				bitmap = readBitmapFromNetwork(item.url);
				if (bitmap != null)
					cache.put(item.url, new SoftReference<Bitmap>(bitmap));
				if (item.listener != null)
					// Use a handler to get back onto the UI thread for the update
					handler.post(new NotifyImageLoadedTask(item.listener, bitmap));
			}
		}
	}


    /**
     * Pone en cola una URI para cargar una imagen desde una vista de la imagen dada.
     * @param uri. El URI origen de la imagen
     * @param callback. La clase de oyente a llamar cuando la imagen esté cargada
     * @throws MalformedURLException. Si el URI proporcionado esta mal hecho o no se puede analizar
     * @return Una imagen de mapa de bits si la imagen está en la caché, de lo contrario devuelve nulo.
     * 
     */
	@SuppressWarnings("unused")
	public Bitmap loadImage(String uriStr, ImageLoadedListener listener) throws MalformedURLException {
		// If it's in the cache, just get it and quit it

		URL url = new URL(uriStr);
		SoftReference<Bitmap> ref = cache.get(url);
		if (ref != null) {
			Bitmap bitmap = ref.get();
			if (bitmap != null)
				return bitmap;
			else 
			{
				//Fix para que intente utilizar las imagenes desde el disco
				//bitmap = GalleryOps.getCachePhotoThumbnail(ctx, post);
				if (bitmap != null)
				{
					return bitmap;
				}
			}
		}

		QueueItem item = new QueueItem();
		item.url = url;
		item.listener = listener;

		queue.offer(item);

		executor.execute(new QueueRunner());

		return null;
	}

	/**
	 * Método para obtener una imagen de mapa de bits a partir de una URL en la red.
	 * @param url. La URL desde donde se lee el bitmap 
	 * @return devuelve una imagen de mapa de bits o nulo en el caso de error 
	 */
	
	public static Bitmap readBitmapFromNetwork(URL url) {
		InputStream is = null;
		BufferedInputStream bis = null;
		Bitmap bmp = null;
		try {
	
			bmp = BitmapFactory.decodeStream(url.openStream());
		} catch (MalformedURLException e) {
		
		} catch (IOException e) {
			
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (is != null)
					is.close();
			} catch (IOException e) {

			}
		}
		return bmp;
	}

}