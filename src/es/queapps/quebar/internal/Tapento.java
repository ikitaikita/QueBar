package es.queapps.quebar.internal;

import java.util.Date;

/**
 * Clase Tapento que contiene la informacion necesaria. Tapa y evento son objetos de esta clase. 
 * @version 1.0
 * @author Victoria Marcos
 */
public class Tapento {

	private int PoiId;
	private int Type;
	private String Desc;
	private Date DateRegister;
	private String urlFoto;
	private int votoplus;
	private int votominus;
	private double latitud;
	private double longitud;
	
	public Tapento(int poi_id, int type, String desc, Date date, String url, int votom, int votoe, double lat, double lon)
	{
		PoiId = poi_id;
		Type = type;
		Desc = desc;
		DateRegister = date;
		urlFoto = url;
		votoplus =votom;
		votominus=votoe;
		latitud=lat;
		longitud=lon;
		
	}
	
	public int getPoiId()
	{
		return PoiId;
	}
	
	public int getType()
	{
		return Type;
	}
	
	public String getDescription()
	{
		return Desc;
	}
	
	public Date getDate()
	{
		return DateRegister;
	}



	public String getUrlFoto() {
		return urlFoto;
	}
	public int getVotoPlus(){
		return votoplus;
	}
	public int getVotoMinus(){
		return votominus;
	}


	public double getLatitud() {
		return latitud;
	}



	public double getLongitud() {
		return longitud;
	}
}
