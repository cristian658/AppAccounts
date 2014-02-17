/**
 * 
 */
/**
 * @author cfica
 *
 */
package cl.fragment; 

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import cl.ciisa.appaccounts.DetailsAccountActivity;
import cl.ciisa.appaccounts.MainFragmentActivity;
import cl.ciisa.appaccounts.R;
import cl.ciisa.masterDB.DBHelpers;
import cl.ciisa.tableModel.ListAccount;
import cl.ciisa.tableModel.ListAccountAdapter;


public class FragmentListAccount extends Fragment {
	   public final static String KEY_TEXT = "key_text"; 
	   public static int iduser = 0; 
	   public static int idcapital = 0;
	   
	    //private LinearLayout lny;
		private LinearLayout lnyHijo;
		protected TextView txtv;
		protected TextView saldo;
		protected TextView gastado;
		protected ListView lstv;
		protected Button btn_start,btn_end;
		
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
	    protected View v;
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) { 
	        //mText = getArguments().getString(KEY_TEXT);
	        v = inflater.inflate(R.layout.frgmnt_list_account, container, false);
	        //TextView tv = (TextView) v.findViewById(R.id.tv_fragment);
	        //tv.setText(mText);
	         
	         Spinner spinner = (Spinner) v.findViewById(R.id.period_list_account);
		     ArrayAdapter<CharSequence> adaptr = ArrayAdapter.createFromResource(MainFragmentActivity.cnt, R.array.planets_array, android.R.layout.simple_spinner_item);
		     adaptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		     spinner.setAdapter(adaptr);

			gastado = (TextView) v.findViewById(R.id.gastado);
			btn_start = (Button) v.findViewById(R.id.btn_start);
			btn_end = (Button) v.findViewById(R.id.btn_end);
			saldo = (TextView) v.findViewById(R.id.saldo);
			addShareButton = (Button) v.findViewById(R.id.buttonAddShare);
			emailShareText = (EditText) v.findViewById(R.id.editTextShare);
			lstv = (ListView) v.findViewById(R.id.listView2);      
	        ArrayList<ListAccount> itemsCompra = obtenerItems();    
	        ListAccountAdapter adapter = new ListAccountAdapter(MainFragmentActivity.cnt, itemsCompra);       
	        lstv.setAdapter(adapter);
	        
			addListenerButton();
			sdf = new SimpleDateFormat("d/MM/yyyy"); 			
	        return v;
	    }
	    
	    
	    private ArrayList<ListAccount> obtenerItems() {
	    	ArrayList<ListAccount> items = new ArrayList<ListAccount>();
            ContentValues cn = new ContentValues();
			
			String[] column = { "accounts.id", "accounts.title","accounts.cost_account","type_account.name"};
			String sql = "SELECT accounts.id,accounts.title, accounts.cost_account,type_account.name "
					+ "FROM accounts "
					+ "INNER JOIN users ON users.id=accounts.id_user "
					+ "INNER JOIN capital ON capital.id=accounts.id_capital "
					+ "INNER JOIN type_account ON type_account.id=accounts.id_type "
					+ "WHERE users.id = " + iduser + " ORDER BY accounts.id ASC";
			Log.d("sql", sql);
			ArrayList<ArrayList<String>> names = MainFragmentActivity.dh.selectJoin(sql, column);

			String[] title = new String[names.size()];
			id_accounts = new Integer[names.size()];
			for (int i = 0; i < names.size(); i++) {
				String[] row = { "mail" };
				List<String> share = MainFragmentActivity.dh.FindOne("share_account", "id_account = " + names.get(i).get(0).toString(),row, "");
				Log.d("cantidad", String.valueOf(share.size()));
				//String mail = share.get(0);
				if(share.size() <1){
					totalGastado = totalGastado + Integer.parseInt(names.get(i).get(2));
					items.add(new ListAccount(Integer.parseInt(names.get(i).get(0)), names.get(i).get(1).toString(), names.get(i).get(3).toString(),Integer.parseInt(names.get(i).get(2))));
				}else{
					int costo = Integer.parseInt(names.get(i).get(2))/2;
					totalGastado = totalGastado + costo;
					//title[i] = names.get(i).get(1).toString() + "    |     $ "+ costo;
					items.add(new ListAccount(Integer.parseInt(names.get(i).get(0)), names.get(i).get(1).toString(), names.get(i).get(3).toString(),costo));
				}
				

				id_accounts[i] = Integer.parseInt(names.get(i).get(0));
			}
			
			Log.d("total gastado", String.valueOf(totalGastado));
			
			String[] rows = { "tot_capital" };
			List<String> capitals = MainFragmentActivity.dh.FindOne("capital", "id = " + idcapital,rows, "");
			totalCapital = Integer.parseInt(capitals.get(0));
			
			totalCapital = totalCapital - totalGastado;
			saldo.setText("$" + totalCapital);
			gastado.setText("$" + totalGastado);
			
			//list = new ArrayAdapter(ListAccountActivity.cnt, android.R.layout.simple_list_item_1, title);
			//lstv.setAdapter(list);
			
	    	return items;
	    }
	    
		 
		public void addListenerButton() {
			lstv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					int id_account = getIdListview(position);
					Intent myintent = new Intent(MainFragmentActivity.cnt,DetailsAccountActivity.class);
					myintent.putExtra("key", String.valueOf(id_account));
					MainFragmentActivity.cnt.startActivity(myintent);
				}
			});
			
			btn_start.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v){
					final Dialog diag = new Dialog(MainFragmentActivity.cnt);
					diag.setContentView(R.layout.view_dialog_date_start);
					diag.setTitle("Fecha Desde");
					diag.show();
				}
			});
			
			btn_end.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v){
					final Dialog diag = new Dialog(MainFragmentActivity.cnt);
					diag.setContentView(R.layout.view_dialog_date_end);
					diag.setTitle("Fecha Hasta");
					diag.show();
				}
			});
			/*
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
							
							 //* share_account(id INTEGER PRIMARY KEY AUTOINCREMENT,
							// * id_user INTEGER, id_user_share INTEGER, id_account
							// * Integer mail TEXT, created TEXT, synchronized
							// * INTEGER)
							 
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
			});*/
		}
		
		public int getIdListview(int position) {
			System.out.println(id_accounts[position]);
			return id_accounts[position];
		}
	    
}
