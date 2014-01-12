package cl.ciisa.net;

/**
*
* @author Cristian Quezada
*/
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpConnect {

	public boolean checkConex(Context ctx) {
	    boolean bTieneConexion = false;
	    ConnectivityManager connec =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    //Con esto recogemos todas las redes que tiene el móvil (wifi, gprs...)
	    NetworkInfo[] redes = connec.getAllNetworkInfo();
	    
	    for(int i=0; i<2; i++){
	        //Si alguna tiene conexión ponemos el boolean a true
	        if (redes[i].getState() == NetworkInfo.State.CONNECTED){
	            bTieneConexion = true;
	        }
	    }
	    return bTieneConexion;
	}
}
