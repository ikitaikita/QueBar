package es.queapps.quebar.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Clase para la gestion del campo de texto de las descripciones de la tapa y el evento a compartir
 * Extiende de EditText 
 * @see android.widget.EditText
 * @version 1.0
 * @author Victoria Marcos
 */
public class ExtendedEditText extends EditText
{
	private Paint p1;
	private Paint p2;
	
	public ExtendedEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		inicialization();
	}
	
	public ExtendedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		inicialization();
	}
	
	public ExtendedEditText(Context context) {
		super(context);
		
		inicialization();
	}
	/**
	 * metodo de inicializacion
	 */
	private void inicialization()
	{
		p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p1.setColor(Color.BLACK);
		p1.setStyle(Style.FILL);
		
		p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p2.setColor(Color.WHITE);
	}
	
	@Override
	public void onDraw(Canvas canvas) 
	{
		
	
		super.onDraw(canvas);
	
		canvas.drawRect(this.getWidth()-40, 5, 
				this.getWidth()-5, 30, p1);
		
		int numCharactersWrite=this.getText().toString().length();
		int numCharacters=140;//Max longitud
		int restOfCharacters=numCharacters-numCharactersWrite;
	
		canvas.drawText("" + restOfCharacters, 
				this.getWidth()-38, 27, p2);
	}
}
