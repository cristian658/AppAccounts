package cl.ciisa.appaccounts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cl.ciisa.appaccounts.RegisterActivity.CreateUser;
import cl.ciisa.masterDB.*;
import cl.ciisa.net.HttpConnect;
import cl.ciisa.net.Login;
import cl.ciisa.net.RegisterUser;
import cl.ciisa.tableModel.User;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListAccountActivity extends Activity {

	private LinearLayout lny;
	private LinearLayout lnyHijo;
	private DBHelpers dh;
	protected ListView lstv;
	protected TextView txtv;
	protected TextView saldo;
	protected TextView gastado;

	protected static final String PREFS_NAME = "AppAccounts";
	private int id_user = 0;
	private int id_capital = 0;
	private int totalCapital = 0;
	private Integer totalGastado = 0; // saldo gastado

	private ArrayAdapter list;
	private Integer[] id_accounts;
	private static int position;

	/**
	 * Share
	 */
	private Button addShareButton;
	private EditText emailShareText;
	private SimpleDateFormat sdf;

	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_account);
		SharedPreferences settings = getSharedPreferences(
				ListAccountActivity.PREFS_NAME, 0);
		id_user = settings.getInt("id", 0);
		id_capital = settings.getInt("id_capital", 0);
		lny = (LinearLayout) findViewById(R.id.listview);

		dh = new DBHelpers(this);
		String[] rows = { "tot_capital" };
		List<String> capitals = dh.FindOne("capital", "id = " + id_capital,
				rows, "");
		totalCapital = Integer.parseInt(capitals.get(0));

		gastado = (TextView) findViewById(R.id.gastado);
		saldo = (TextView) findViewById(R.id.saldo);
		addShareButton = (Button) findViewById(R.id.buttonAddShare);
		emailShareText = (EditText) findViewById(R.id.editTextShare);

		refreshList();

		addListenerButton();
		sdf = new SimpleDateFormat("d/MM/yyyy");
	}

	public void refreshList(){
		ContentValues cn = new ContentValues();

		String[] column = { "accounts.id", "accounts.title",
				"accounts.cost_account" };
		String sql = "SELECT accounts.id,accounts.title, accounts.cost_account "
				+ "FROM accounts "
				+ "INNER JOIN users ON users.id=accounts.id_user "
				+ "INNER JOIN capital ON capital.id=accounts.id_capital "
				+ "INNER JOIN type_account ON type_account.id=accounts.id_type "
				+ "WHERE users.id = " + id_user + " ORDER BY accounts.id ASC";
		Log.d("sql", sql);
		ArrayList<ArrayList<String>> names = dh.selectJoin(sql, column);

		String[] title = new String[names.size()];
		id_accounts = new Integer[names.size()];
		for (int i = 0; i < names.size(); i++) {
			String[] row = { "mail" };
			List<String> share = dh.FindOne("share_account", "id_account = " + names.get(i).get(0).toString(),
					row, "");
			Log.d("cantidad", String.valueOf(share.size()));
			//String mail = share.get(0);
			if(share.size() <1){
				totalGastado = totalGastado + Integer.parseInt(names.get(i).get(2));
				title[i] = names.get(i).get(1).toString() + "    |     $ "+ names.get(i).get(2);
			}else{
				int costo = Integer.parseInt(names.get(i).get(2))/2;
				totalGastado = totalGastado + costo;
				title[i] = names.get(i).get(1).toString() + "    |     $ "+ costo;
			}
			

			id_accounts[i] = Integer.parseInt(names.get(i).get(0));
		}
		Log.d("total gastado", String.valueOf(totalGastado));
		totalCapital = totalCapital - totalGastado;
		saldo.setText("$" + totalCapital);
		gastado.setText("$" + totalGastado);
		lstv = new ListView(this);
		list = new ArrayAdapter(this, android.R.layout.simple_list_item_1, title);
		lstv.setAdapter(list);
		lny.removeAllViews();
		lny.addView(lstv);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.list_account, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
	public void sincronizar(){
		Toast t = Toast.makeText(this, "Sincronizando Datos...", Toast.LENGTH_LONG);
		t.show();
	}

	public void addListenerButton() {
		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int id_account = ListAccountActivity.this
						.getIdListview(position);
				Intent myintent = new Intent(ListAccountActivity.this,
						DetailsAccountActivity.class);
				myintent.putExtra("key", String.valueOf(id_account));
				ListAccountActivity.this.startActivity(myintent);
			}
		});
		lstv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				ListAccountActivity.position = pos;
				final Dialog dialog = new Dialog(ListAccountActivity.this);
				dialog.setContentView(R.layout.dialog_share);
				dialog.setTitle(R.string.share);
				addShareButton = (Button) dialog
						.findViewById(R.id.buttonAddShare);
				emailShareText = (EditText) dialog
						.findViewById(R.id.editTextShare);
				addShareButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						/**
						 * share_account(id INTEGER PRIMARY KEY AUTOINCREMENT,
						 * id_user INTEGER, id_user_share INTEGER, id_account
						 * Integer mail TEXT, created TEXT, synchronized
						 * INTEGER)
						 */
						HttpConnect httpConn = new HttpConnect();
						if(httpConn.checkConex(ListAccountActivity.this)){
							new ConsultUser().execute();
						}else{
							Toast t = Toast.makeText(ListAccountActivity.this, "No hay conexión a internet, Verifique su Conexión",Toast.LENGTH_LONG);
							t.show();
						}
						
						dialog.dismiss();
						

					}
				});
				dialog.show();
				return false;
			}
		});
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
		SharedPreferences settings = getSharedPreferences(
				LoginActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
		//dh.deleteAll("");
		Intent myIntent = new Intent(ListAccountActivity.this,
				LoginActivity.class);
		ListAccountActivity.this.startActivity(myIntent);
	}

	class ConsultUser extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListAccountActivity.this);
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
			cvShared.put("id_account", ListAccountActivity.this
					.getIdListview(ListAccountActivity.position));
			cvShared.put("mail", emailShareText.getText().toString());
			cvShared.put("created", sdf.format(new Date()));
			cvShared.put("synchronized", 0);
			ListAccountActivity.this.dh.insertTAble("share_account", cvShared);
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
