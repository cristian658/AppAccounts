package cl.ciisa.appaccounts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import cl.ciisa.err.ErrorNet;
import cl.ciisa.masterDB.DBHelpers;
import cl.ciisa.net.RegisterUser;
import cl.ciisa.tableModel.Capital;
import cl.ciisa.tableModel.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private EditText nameView;
	private EditText mailView;
	private EditText passwordView;
	private EditText phoneView;
	private EditText capitalView;
	private Button saveView;

	private ProgressDialog pDialog;

	private ContentValues addUser;
	private ContentValues addCapital;
	private User user;
	private Capital capital;

	private DBHelpers db;
	private String email;
	private SimpleDateFormat sdf;
	private RegisterUser regUser;
	private ErrorNet err;
	
	
	protected static final String PREFS_NAME = "AppAccounts";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Intent myIntent = getIntent();
		email = myIntent.getStringExtra("email");

		db = new DBHelpers(this);
		nameView = (EditText) findViewById(R.id.editTextNameReg);
		mailView = (EditText) findViewById(R.id.editTextMailReg);
		passwordView = (EditText) findViewById(R.id.editTextPassReg);
		phoneView = (EditText) findViewById(R.id.editTextPhoneReg);
		capitalView = (EditText) findViewById(R.id.EditTextCapital);
		
		saveView = (Button) findViewById(R.id.buttonSaveReg);
		mailView.setText(email);
		addUser = new ContentValues();
		addCapital = new ContentValues();
		addListener();
		sdf = new SimpleDateFormat("d/MM/yyyy");

	}

	private void addListener() {
		saveView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				user = RegisterActivity.this.getUser();
				capital = RegisterActivity.this.getCapital();
				new CreateUser().execute();
				

			}
		});
	}

	private User getUser() {
		User u = new User();
		u.setName(nameView.getText().toString());
		u.setEmail(mailView.getText().toString());
		u.setPass(passwordView.getText().toString());
		u.setPhone(phoneView.getText().toString());
		u.setCreated(sdf.format(new Date()));
		u.setState(1);

		return u;
	}
	private Capital getCapital(){
		Capital cap = new Capital();
		
		cap.setTot_capital(Integer.parseInt(capitalView.getText().toString()));
		cap.setState(1);
		cap.setSynchronize(0);
		cap.setCreated(sdf.format(new Date()));
		return cap;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	class CreateUser extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("Guardando Usuario..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			regUser = new RegisterUser(user,capital);
			int success = regUser.registerUser();
			user = regUser.getUser();
			capital = regUser.getCapital();
			if(success == 1){
				addUser.put("id",user.getId());
				addUser.put("name", user.getName()); 
				addUser.put("email", user.getEmail()); 
				addUser.put("pass", user.getPass());
				addUser.put("phone", user.getPhone()); 
				addUser.put("created", user.getCreated()); 
				addUser.put("state", user.getState());
				db.insertTAble("users", addUser);
				/**
				 * id INTEGER PRIMARY KEY AUTOINCREMENT,id_user INTEGER, 
				 * tot_capital INTEGER, created TEXT, updated_cap TEXT, 
				 * state INTEGER, synchronized INTEGER
				 */
				addCapital.put("id", capital.getId());
				addCapital.put("id_user", user.getId());
				addCapital.put("tot_capital", capital.getTot_capital());
				addCapital.put("created", capital.getCreated());
				addCapital.put("state", capital.getState());
				addCapital.put("synchronized", capital.getSynchronize());
				db.insertTAble("capital", addCapital);
				
				SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("connect",true);
				editor.putInt("id", user.getId());
				editor.putInt("id_capital", capital.getId());
				editor.commit();
				Intent myIntent = new Intent(RegisterActivity.this,ListAccountActivity.class);
				RegisterActivity.this.startActivity(myIntent);
				finish();
			} else {
				err = regUser.getError();
			}
			return "";
		}
		
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if(err != null){
            	Toast.makeText(RegisterActivity.this, err.getMensaje(), Toast.LENGTH_LONG).show();
            }
        }

	}

}
