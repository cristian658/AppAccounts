package cl.ciisa.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cl.ciisa.tableModel.Capital;
import cl.ciisa.tableModel.User;
import android.util.Log;

public class Login {

	private static String url_login = "http://appacounts.co.nf/index.php/users/getUser";
	private static String url_capital = "http://appacounts.co.nf/index.php/capital/getCapitalByUser";
	private static final String TAG_SUCCESS = "success";
	
	private JSONParser jParser;
	private String email;
	private List<NameValuePair> paramsUser;
	private List<NameValuePair> paramsCap;
	private int success = 0;
		
	private User u;
	private Capital cap;
	
	public Login(String email){
		this.email = email;
		jParser = new JSONParser();
		paramsUser = new ArrayList<NameValuePair>();
		paramsCap = new ArrayList<NameValuePair>();
		u = new User();
		cap = new Capital();
	}
	
	public int getLogin(){
		paramsUser.add(new BasicNameValuePair("email", this.email));
		JSONObject json = jParser.makeHttpRequest(url_login, "POST", paramsUser);
		Log.d("response json User", json.toString());
		
		try {
			success = json.getInt(TAG_SUCCESS);
			if(success == 1){
				this.u.setId(json.getInt("id"));
				this.u.setName(json.getString("name"));
				this.u.setPass(json.getString("pass"));
				this.u.setPhone(json.getString("phone"));
				this.u.setEmail(json.getString("email"));
				this.u.setState(json.getInt("state"));
				this.u.setCreated(json.getString("created"));
				paramsCap.add(new BasicNameValuePair("id_user", String.valueOf(u.getId())));
				JSONObject jsonCapital = jParser.makeHttpRequest(url_capital, "POST", paramsCap);
				Log.d("response json Capital", jsonCapital.toString());
				int successCap = jsonCapital.getInt("success");
				if(successCap == 1){
					this.cap.setId(jsonCapital.getInt("id"));
					this.cap.setId_user(jsonCapital.getInt("id_user"));
					this.cap.setState(jsonCapital.getInt("state"));
					this.cap.setSynchronize(1);
					this.cap.setTot_capital(jsonCapital.getInt("tot_capital"));
				}
				
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return success;
		
	}
	
	public User getUser(){
		return this.u;
	}
	
	public Capital getCapital(){
		return this.cap;
	}
	
	

}
