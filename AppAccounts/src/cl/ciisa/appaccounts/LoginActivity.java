package cl.ciisa.appaccounts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import cl.ciisa.masterDB.DBHelpers;
import cl.ciisa.net.HttpConnect;
import cl.ciisa.net.Login;
import cl.ciisa.tableModel.Capital;
import cl.ciisa.tableModel.User;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"cristian.68@gmail.com:paula", "c.fica.fica@gmail.com:lala" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "cl.ciisa.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private DBHelpers db;
	//login net
	private Login log;
	private int successNet;
	private User user;
	private Capital capital;
	protected static final String PREFS_NAME = "AppAccounts";
	private SimpleDateFormat sdf;
	
	//public static int id_usr=0; 
	//public static int id_capt=0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		this.listAccounts();
		
		db = new DBHelpers(this);
		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		sdf = new SimpleDateFormat("dd/MM/yyyy");
	}

	protected void onStart(){
		super.onStart();
		this.listAccounts();
		
	}
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	*/
	public void listAccounts(){
		SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		boolean connect = settings.getBoolean("connect", false);
		if(connect){
			Intent myIntent = new Intent(LoginActivity.this,MainFragmentActivity.class);
			this.startActivity(myIntent);
		}
		
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			Log.d("mensaje", getString(R.string.error_invalid_email));
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
;			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			HttpConnect httpConn = new HttpConnect();
			if(httpConn.checkConex(this)){
				mAuthTask.execute((Void) null);
			}else{
				showProgress(false);
				Toast t = Toast.makeText(this, "No hay conexiÃ³n a internet, Verifique su ConexiÃ³n",Toast.LENGTH_LONG);
				t.show();
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			log = new Login(mEmail);
			successNet = log.getLogin();
			if(successNet == 0){
				return false;
			}
			user = log.getUser();
			capital = log.getCapital();
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);
			Log.d("success", String.valueOf(successNet));
			if(successNet == 0){
				Intent myIntent = new Intent(LoginActivity.this,RegisterActivity.class);
				myIntent.putExtra("email", mEmail); 
				LoginActivity.this.startActivity(myIntent);
			}else if (mPassword.equals(user.getPass())){
				/**
				 * Agregando al usuario a la base de datos local
				 */
				
				String[] rows = { "id" };
				List<String> capitals = db.FindOne("users", "email = '" + user.getEmail()+"'",rows,"");
				int id = Integer.parseInt(capitals.get(0));
				if(id==0){ //Carlos Fica -> esta validación era necesaria ya que siempre que inicia sesión ingresa aquí y da un error por que se trata de insertar un valor que ya está en la base de datos local
					ContentValues addUser = new ContentValues();
					addUser.put("id", user.getId());
					addUser.put("name", user.getName());
					addUser.put("email", user.getEmail());
					addUser.put("pass", user.getPass());
					addUser.put("phone", user.getPhone());
					addUser.put("created", user.getCreated());
					addUser.put("state", user.getState());
					addUser.put("created", user.getCreated());
					db.insertTAble("users", addUser);
					ContentValues addCapital = new ContentValues();
					addCapital.put("id", capital.getId());
					addCapital.put("id_user", capital.getId_user());
					addCapital.put("state", capital.getState());
					addCapital.put("synchronized", capital.getSynchronize());
					addCapital.put("tot_capital", capital.getTot_capital());
					addCapital.put("created", capital.getCreated());
					db.insertTAble("capital", addCapital);
				}
				
				
				SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("connect",true);
				editor.putInt("id_us", user.getId());
			    //id_usr=user.getId();
				//id_capt=capital.getId();
				editor.putInt("id_cap", capital.getId());
				editor.commit();
				Intent myIntent = new Intent(LoginActivity.this,MainFragmentActivity.class);
				LoginActivity.this.startActivity(myIntent);
			} else {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
