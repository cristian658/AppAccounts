package cl.ciisa.appaccounts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cl.ciisa.appaccounts.RegisterActivity.CreateUser;
import cl.ciisa.masterDB.*;
import cl.ciisa.net.HttpConnect;
import cl.ciisa.net.Login;
import cl.ciisa.net.RegisterUser;
import cl.ciisa.tableModel.User;
import cl.fragment.FragmentHome;
import cl.fragment.FragmentListAccount;
import cl.fragment.FragmentProjection;
import cl.fragment.FragmentReport;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


//public class ListAccountActivity extends Activity {
//drawer
public class MainFragmentActivity extends Activity {
	private LinearLayout lny;
	private LinearLayout lnyHijo; 
	
	protected ListView lstv;
	protected TextView txtv;
	protected TextView saldo;
	protected TextView gastado;

	public static final String PREFS_NAME = "AppAccounts";
	public static DBHelpers dh;
	public int id_user = 0;
	private int id_capital = 0;
	public int totalCapital = 0;
	public Integer totalGastado = 0; // saldo gastado

	private ArrayAdapter list;
	public Integer[] id_accounts;
	private static int position;

	/**
	 * Share
	 */
	private Button addShareButton;
	private EditText emailShareText;
	private SimpleDateFormat sdf;
	public ProgressDialog pDialog;
	public static Context cnt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_fragment);

		dh = new DBHelpers(this);
		//---
		SharedPreferences settings = getSharedPreferences(MainFragmentActivity.PREFS_NAME, 0);
		id_user = settings.getInt("id_us", 0);
		id_capital = settings.getInt("id_cap", 0);
			
		cnt=this;
		onInitializeDrawer();
      
	}
	
//-----------------------------------------------------------------------------------------------------------------------
	private ListView navList;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
    
	protected void onInitializeDrawer(){
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.navList = (ListView) findViewById(R.id.left_drawer);
        final String[] names = getResources().getStringArray(R.array.nav_options);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, names);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new DrawerItemClickListener());
       
        mTitle = mDrawerTitle = getTitle();
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
           
        	public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Seleccione..");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        //initialize home drawerlayout
        drawerHome();
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(navList);
        //menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
	protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
   }
	 
	
   public void onConfigurationChanged(Configuration newConfig) {
	        // Called by the system when the device configuration changes while your
	        // activity is running
	        super.onConfigurationChanged(newConfig);
	        mDrawerToggle.onConfigurationChanged(newConfig);
   }
	    
   public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else{
        	// Handle item selection
    		switch (item.getItemId()) {
    		case R.id.itemAdd:
    			this.addAccount();
    			return true;
    		case R.id.itemClose:
    			this.close();
    		case R.id.itemSinC:
    			this.sincronizar();
    		default:
    			return super.onOptionsItemSelected(item);
    		}
        }
        // Handle your other action bar items...
      //  return super.onOptionsItemSelected(item);
   }
   
   private class DrawerItemClickListener implements ListView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		   selectItem(position);
		}
	}
	
	/** Swaps fragments in the main content view */
	private void selectItem(int position){
		mTitle = getResources().getStringArray(R.array.nav_options)[position];
		String opt=convertCharSequenceString(mTitle);
		
		if(opt.equals("Home"))
			drawerHome();	
        if(opt.equals("Informes"))
        	drawerReport();     
        if(opt.equals("Cuentas"))
        	drawerListAccount();    
        if(opt.equals("Proyección"))
        	drawerProjection();

		navList.setItemChecked(position, true);
		getActionBar().setTitle(mTitle);
		mDrawerLayout.closeDrawer(navList);
	}
	
	//=============================LIST DRAWERLAYOUT========================================
		private void drawerHome(){
			Fragment fragment = new FragmentHome();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
		
		private void drawerProjection(){
			Fragment fragment = new FragmentProjection();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
		
		private void drawerReport(){
			Fragment fragment = new FragmentReport();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
		
		private void drawerListAccount(){
			Fragment fragment = new FragmentListAccount();
			Bundle args = new Bundle();
			args.putString(FragmentListAccount.KEY_TEXT, mTitle.toString());
			fragment.setArguments(args);
			
			FragmentListAccount.iduser=id_user;
			FragmentListAccount.idcapital=id_capital;
			//args.putInt(FragmentListAccount.ID_USER,id_user);
			//fragment.setArguments(args);
			
			//args.putInt(FragmentListAccount.ID_CAPITAL,id_capital);
			//fragment.setArguments(args);
			
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
//--------------------------------------------------------------------------------------------------------------------
		
	private String convertCharSequenceString(CharSequence nm){
		final StringBuilder sb = new StringBuilder(nm.length());
		sb.append(nm);
		return sb.toString();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.list_account, menu);
		return true;
	}

	
	public void sincronizar(){
		Toast t = Toast.makeText(this, "Sincronizando Datos...", Toast.LENGTH_LONG);
		t.show();
	}



	public void addAccount() {
		Intent myIntent = new Intent(this, AddActivity.class);
		this.startActivity(myIntent);
	}

	public int getIdListview(int position) {
		System.out.println(id_accounts[position]);
		return id_accounts[position];
	}

	public void close() {
		SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
		//dh.deleteAll("");
		Intent myIntent = new Intent(MainFragmentActivity.this,LoginActivity.class);
		MainFragmentActivity.this.startActivity(myIntent);
	}

	class ConsultUser extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainFragmentActivity.this);
			pDialog.setMessage("Compartiendo...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Login l = new Login(emailShareText.getText().toString());
			l.getLogin();
			User u = l.getUser();
			ContentValues cvShared = new ContentValues();
			cvShared.put("id_user", id_user);
			cvShared.put("id_user_share", u.getId());
			cvShared.put("id_account", MainFragmentActivity.this
					.getIdListview(MainFragmentActivity.position));
			cvShared.put("mail", emailShareText.getText().toString());
			cvShared.put("created", sdf.format(new Date()));
			cvShared.put("synchronized", 0);
			MainFragmentActivity.this.dh.insertTAble("share_account", cvShared);
			// finish();
			//ListAccountActivity.this.list.notifyDataSetChanged();
			return "";
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();

		}

	}

}
