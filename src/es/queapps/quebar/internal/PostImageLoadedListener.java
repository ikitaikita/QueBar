package es.queapps.quebar.internal;

import android.graphics.Bitmap;
import android.widget.ImageView;
import es.queapps.quebar.internal.ImageThreadLoader.ImageLoadedListener;

/**
 * Clase para la gestion del oyente de la carga de imagenes
 * Implementa la interfaz ImageLoadedListener
 * @see es.queapps.quebar.internal.ImageThreadLoader.ImageLoadedListener
 * @version 1.0
 * @author Victoria Marcos
 */
public class PostImageLoadedListener implements ImageLoadedListener {
	private ImageView m_image = null;

	public PostImageLoadedListener(ImageView image) {
		m_image = image;
	}

	@Override
	public void imageLoaded(Bitmap imageBitmap) {
		m_image.setImageBitmap(imageBitmap);
	}
}
