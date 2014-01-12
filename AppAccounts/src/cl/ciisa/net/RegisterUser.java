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
import cl.ciisa.tableModel.Capital;
import cl.ciisa.tableModel.User;

public class RegisterUser {
	
	private static final String url_register = "http://appacounts.co.nf/index.php/users/insertUser";
	private static final String url_register_capital = "http://appacounts.co.nf/index.php/capital/insertCapitalByUser";
	private static final String TAG_SUCCESS = "success";
	
	private JSONParser jParser;
	private List<NameValuePair> paramsUser;
	private List<NameValuePair> paramsCapital;
	private int success = 0;
	private ErrorNet err;
	
	private User user;
	private Capital capital;
	
	
	public RegisterUser(User user,Capital cap){
		this.user = user;
		this.capital = cap;
		jParser = new JSONParser();
		paramsUser = new ArrayList<NameValuePair>();
		paramsCapital = new ArrayList<NameValuePair>();
	}
	public int registerUser(){
		paramsUser.add(new BasicNameValuePair("name", user.getName()));
		paramsUser.add(new BasicNameValuePair("phone", user.getPhone()));
		paramsUser.add(new BasicNameValuePair("email", user.getEmail()));
		paramsUser.add(new BasicNameValuePair("pass", user.getPass()));
		paramsUser.add(new BasicNameValuePair("state", String.valueOf(user.getState())));
		paramsUser.add(new BasicNameValuePair("created", user.getCreated()));
		
		JSONObject json = jParser.makeHttpRequest(url_register, "POST", paramsUser);
		Log.d("response json Usuario", json.toString());
		try {
			this.success = json.getInt(TAG_SUCCESS);
			if(this.success == 1){
				user.setId(json.getInt("id"));
				paramsCapital.add(new BasicNameValuePair("id_user", String.valueOf(user.getId())));
				paramsCapital.add(new BasicNameValuePair("tot_capital", String.valueOf(capital.getTot_capital())));
				paramsCapital.add(new BasicNameValuePair("created", capital.getCreated()));
				paramsCapital.add(new BasicNameValuePair("updated_cap", capital.getUpdatedCap()));
				paramsCapital.add(new BasicNameValuePair("state", String.valueOf(capital.getState())));
				JSONObject jsonCapital = jParser.makeHttpRequest(url_register_capital, "POST", paramsCapital);
				Log.d("response json Usuario", jsonCapital.toString());
				int successCapital = jsonCapital.getInt(TAG_SUCCESS);
				if(successCapital == 1){
					capital.setId(jsonCapital.getInt("id"));
				}
			}else{
				err = new ErrorNet();
				err.setMensaje(json.getString("message"));
				err.setFecha(new Date());
			}
			
			
		} catch (JSONException e) {
			err = new ErrorNet();
			err.setMensaje(e.getMessage());
			e.printStackTrace();
			this.success = 0;
		}
		return this.success;
	}
	
	public User getUser(){
		return this.user;
	}
	
	public Capital getCapital(){
		return this.capital;
	}
	
	public ErrorNet getError(){
		return this.err;
	}

}
