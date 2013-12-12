package cl.ciisa.net;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import cl.ciisa.err.ErrorNet;
import cl.ciisa.tableModel.User;

public class RegisterUser {
	
	private static final String url_register = "http://appacounts.co.nf/index.php/users/insertUser";
	private static final String TAG_SUCCESS = "success";
	
	private JSONParser jParser;
	private List<NameValuePair> params;
	private int success = 0;
	private ErrorNet err;
	
	private User user;
	
	
	public RegisterUser(User user){
		this.user = user;
		jParser = new JSONParser();
		params = new ArrayList<NameValuePair>();
	}
	public int registerUser(){
		params.add(new BasicNameValuePair("name", user.getName()));
		params.add(new BasicNameValuePair("phone", user.getPhone()));
		params.add(new BasicNameValuePair("email", user.getEmail()));
		params.add(new BasicNameValuePair("pass", user.getPass()));
		params.add(new BasicNameValuePair("state", String.valueOf(user.getState())));
		params.add(new BasicNameValuePair("created", user.getCreated()));
		JSONObject json = jParser.makeHttpRequest(url_register, "POST", params);
		Log.d("response json", json.toString());
		try {
			this.success = json.getInt(TAG_SUCCESS);
			if(this.success == 1){
				user.setId(json.getInt("id"));
			}else{
				err = new ErrorNet();
				err.setMensaje(json.getString("message"));
				err.setFecha(new Date());
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this.success;
	}
	
	public User getUser(){
		return this.user;
	}
	
	public ErrorNet getError(){
		return this.err;
	}

}
