package cl.ciisa.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cl.ciisa.tableModel.User;
import android.util.Log;

public class Login {

	private static String url_login = "http://appacounts.co.nf/index.php/users/getUser";
	private static final String TAG_SUCCESS = "success";
	
	private JSONParser jParser;
	private String email;
	private List<NameValuePair> params;
	private int success = 0;
		
	private User u;
	
	public Login(String email){
		this.email = email;
		jParser = new JSONParser();
		params = new ArrayList<NameValuePair>();
		u = new User();
	}
	
	public int getLogin(){
		params.add(new BasicNameValuePair("email", this.email));
		JSONObject json = jParser.makeHttpRequest(url_login, "POST", params);
		Log.d("response json", json.toString());
		
		try {
			success = json.getInt(TAG_SUCCESS);
			if(success == 1){
				u.setId(json.getInt("id"));
				u.setName(json.getString("name"));
				u.setPass(json.getString("pass"));
				u.setPhone(json.getString("phone"));
				u.setEmail(json.getString("email"));
				u.setState(json.getInt("state"));
				u.setCreated(json.getString("created"));
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return success;
		
	}
	
	public User getUser(){
		return this.u;
	}
	
	

}
