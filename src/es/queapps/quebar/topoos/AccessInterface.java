package es.queapps.quebar.topoos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import topoos.AccessTokenOAuth;
import topoos.Exception.TopoosException;
import topoos.Objects.POI;
import android.content.Context;
import android.location.Location;
import android.util.Log;

/**
 * Clase para acceso a plataforma Topoos y diferentes operaciones que se realizan con esta plataforma 
 * @version 1.0
 * @author Victoria Marcos
 */
public class AccessInterface {
	
	public static final String TOPOOS_ADMIN_APP_TOKEN = "1a2699e6-4ecf-4ccd-886e-1e90b15f451e";


	public static final int SEARCH_RADIUS_METERS = 5000;
	public static final int SEARCH_RADIUS_METERS_ADDRESS = 10000;
	public static final int total = 30;// Variable que indica el numero de
	// bares a descargar para op getNear y
	// getWhere

	/**
     * acceso a Topoos con un token valido
     * @param context
     */
    public static void initializeTopoosSession(Context ctx) {
    	AccessTokenOAuth token = topoos.AccessTokenOAuth.GetAccessToken(ctx);
    	if (token == null || !token.isValid())
    	{
    		token = new AccessTokenOAuth(TOPOOS_ADMIN_APP_TOKEN);
    		token.save_Token(ctx);
    	}
    }
    
    /**
     * obtener los POIs registrados por cualquier usuario cerca de la ubicacion especificada
     * 
     * @param ctx
     * @param location
     * @return
     * @throws IOException
     * @throws TopoosException
     */
    public static List<POI> GetUserTapentosNear(Context ctx, double latitud, double longitud) throws IOException, TopoosException
    {

    	
    	Integer[] categories = new Integer[1];
		categories[0] = POICategories.USER;
		List<POI> pois = topoos.POI.Operations.GetNear(ctx, latitud, longitud, SEARCH_RADIUS_METERS, categories, total);
		
		
		return pois;
    	
    }
    public static List<POI> GetUserTapentosNearAddress(Context ctx, double latitud, double longitud) throws IOException, TopoosException
    {

    	
    	Integer[] categories = new Integer[1];
		categories[0] = POICategories.BAR;
		List<POI> pois = topoos.POI.Operations.GetNear(ctx, latitud, longitud, SEARCH_RADIUS_METERS_ADDRESS, categories, total);
		
		
		return pois;
    	
    }
    /**
     * Metodo que obtiene un objeto POI registrado 
     * @param ctx
     * @param poiid
     * @return
     * @throws IOException
     * @throws TopoosException
     */
    public static POI getPOI (Context ctx, int poiid) throws IOException, TopoosException
    {
    	
    	POI p = topoos.POI.Operations.Get(ctx, poiid);
    
    	return p;
    	
    }
    

    /**
     * obtiene lista de POIs registrados por cualquier usuario mediante un criterio
     * 
     * @param ctx
     * @param Integer[] lista de ids
     * @return
     * @throws IOException
     * @throws TopoosException
     */
    public static List<POI> GetUserTapentosWhere(Context ctx, Integer[] ids) throws IOException, TopoosException
    {
    	Integer[] categories = new Integer[1];
		categories[0] = POICategories.USER;
    	

    	List<POI> pois = topoos.POI.Operations.GetWhere(ctx, categories, ids, null, null, null, null,total);
    	//List<POI> pois = topoos.POI.Operations.GetNear(ctx, 40.4167754, -3.70379019999, 6000, categories, total);
    	
    	
    	return pois;
    }
    

    
    /**
     * Registra un nuevo POI del usuario
     * @param context
     * @param poiType
     * @param imagen
     * @param description
     * @param location
     * @return POI
     * @throws TopoosException 
     * @throws IOException 
     */
    public static POI RegisterTapento(Context ctx, int poiType, String image, String description, Location location) throws IOException, TopoosException
    {
    	//prepara las categorias para el nuevo POI
		Integer[] categories = new Integer[2];
		categories[0] = poiType;
		categories[1] = POICategories.USER;
		
		JSONObject ret = getLocationInfo(location.getLatitude(), location.getLongitude()); 
		JSONObject locationJ;
		String location_string="";
	
		
		try {
		    //Get JSON Array called "results" and then get the 0th complete object as JSON        
		    locationJ = ret.getJSONArray("results").getJSONObject(0); 
		    // Get the value of the attribute whose name is "formatted_string"
		    location_string = locationJ.getString("formatted_address");
		   
		    //Log.d("test", "formattted address:" + location_string);
		    //Log.i("location_string:", location_string);
		} catch (JSONException e1) {
		    e1.printStackTrace();

		}
		

		
		POI newPoi = topoos.POI.Operations.Add(ctx, image, location.getLatitude(), location.getLongitude(), null, categories, (double)0, (double)0, (double)0, description, location_string, null, null, null, null, null, null);
		return newPoi;
    }
    
    /**
     * metodo que se usa al añadir un voto negativo
     * @param ctx
     * @param id
     * @throws IOException
     * @throws TopoosException
     */
    public static void AddNegativeVote(Context ctx, int id) throws IOException, TopoosException
    {

    	topoos.POIWarning.Operations.AddClosed(ctx,id);

    }
    /**
     * Metodo que se usa al añadir un voto positivo
     * @param ctx
     * @param id
     * @throws IOException
     * @throws TopoosException
     */
    public static void AddPositiveVote(Context ctx, int id) throws IOException, TopoosException
    {
    	topoos.POIWarning.Operations.AddDuplicated(ctx,id);

    			
    }
    
    private static JSONObject getLocationInfo( double lat, double lng) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    
    



}
