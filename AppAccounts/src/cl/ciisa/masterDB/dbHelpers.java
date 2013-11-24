/**
 * 
 */
/**
 * @author cfica
 *
 */
package cl.ciisa.masterDB;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Clase controladora de la base de datos.
 * @author FelipeDev
 */
public class dbHelpers {
	
	private static final String DATABASE_NAME = "appAccounts.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "table1"; 
	private Context context;
	private SQLiteDatabase db; 
	private SQLiteStatement insertStmtUser,insertStmtTypeAccounts,insertStmtCapital,insertStmtAccounts,insertStmtImgAccounts;
	private static final String USER = "INSERT INTO type_account(name,state) values (?,?)";
	private static final String TYPE_ACCOUNT = "INSERT INTO users(type,id_relation,name,phone,email,user,pass,created,state) values (?,?,?,?,?,?,?,?,?)";
	private static final String CAPITAL = "INSERT INTO capital(tot_capital,created,updated,state) values (?,?,?,?)";
	private static final String ACCOUNTS = "INSERT INTO img_account(id_account,name_img,url,state) values (?,?,?,?)";
	private static final String IMG_ACCOUNT = "INSERT INTO accounts(id_user,id_capital,id_type,title,description,cost_account,created,updated,state) values (?,?,?,?,?,?,?,?)";
	
	public dbHelpers(Context context) {
		this.context = context;
	    OpenHelper openHelper = new OpenHelper(this.context);
	    this.db = openHelper.getWritableDatabase();
	    //INITIALIZE
	    this.insertStmtUser = this.db.compileStatement(USER);
	    this.insertStmtTypeAccounts = this.db.compileStatement(TYPE_ACCOUNT);
	    this.insertStmtCapital = this.db.compileStatement(CAPITAL);
	    this.insertStmtAccounts = this.db.compileStatement(ACCOUNTS);
	    this.insertStmtImgAccounts = this.db.compileStatement(IMG_ACCOUNT);
	}
	
	/*INSERT*/
	//ContentValues nuevoRegistro = new ContentValues();
	//nuevoRegistro.put("codigo", "6");
	//nuevoRegistro.put("nombre","usuariopru");
	public long insertTAble(String table,ContentValues params) {
		//this.insertStmtTypeAccounts.bindString(1, name);
		//this.insertStmtTypeAccounts.bindLong(2, state);
		this.db.insert(table, null, params);
		return this.insertStmtTypeAccounts.executeInsert();
	}
	
	public long insertTypeAccount(String name,int state) {
		this.insertStmtTypeAccounts.bindString(1, name);
		this.insertStmtTypeAccounts.bindLong(2, state);
		return this.insertStmtTypeAccounts.executeInsert();
	}
	
	public long insertUser(String type,int id_rel,String name,String phone,String email,String user, String pass, String created, int state) {
		this.insertStmtUser.bindString(1, type);
		this.insertStmtUser.bindLong(2, id_rel);
		this.insertStmtUser.bindString(3, name);
		this.insertStmtUser.bindString(4, phone);
		this.insertStmtUser.bindString(5, email);
		this.insertStmtUser.bindString(6, user);
		this.insertStmtUser.bindString(7, pass);
		this.insertStmtUser.bindString(8, created);
		this.insertStmtUser.bindLong(9, state);
		return this.insertStmtUser.executeInsert();
	}
	
	public long insertCapital(long tot_capital,String created,String updated,int state) {
		this.insertStmtCapital.bindLong(1, tot_capital);
		this.insertStmtCapital.bindString(2, created);
		this.insertStmtCapital.bindString(3, updated);
		this.insertStmtCapital.bindLong(4, state);
		return this.insertStmtCapital.executeInsert();
	}
	
	public long insertImgAccounts(int id_account,String name_img,String url,int state) {
		this.insertStmtImgAccounts.bindLong(1, id_account);
		this.insertStmtImgAccounts.bindString(2, name_img);
		this.insertStmtImgAccounts.bindString(3, url);
		this.insertStmtImgAccounts.bindLong(4, state);
		return this.insertStmtImgAccounts.executeInsert();
	}
	
	public long insertAccounts(int id_user,int id_capital,int id_type,String title,String description,long cost_account,String created,String updated,int state) {
		this.insertStmtAccounts.bindLong(1, id_user);
		this.insertStmtAccounts.bindLong(2, id_capital);
		this.insertStmtAccounts.bindLong(3, id_type);
		this.insertStmtAccounts.bindString(4, title);
		this.insertStmtAccounts.bindString(5, description);
		this.insertStmtAccounts.bindLong(6, cost_account);
		this.insertStmtAccounts.bindString(7, created);
		this.insertStmtAccounts.bindString(8, updated);
		this.insertStmtAccounts.bindLong(9, state);
		return this.insertStmtAccounts.executeInsert();
	}
	
	//UPDATED
	//ContentValues valores = new ContentValues();
	//valores.put("nombre","usunuevo");
	public boolean updateTable(String table,ContentValues params,String _where) {
		this.db.update(table,params,_where,null);
		return true;
	}
	
	   
	/*SELECT*/	
	public List<String> selectAllOne(String _table,String _where, String[] rows,String _options) {
		List<String> list = new ArrayList<String>();
		if(_options=="" || _options==null) _options="id DESC";
		Cursor cursor = this.db.query(_table,rows, _where, null, null, null, _options);
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(0)); 
			} while (cursor.moveToNext());
		}
	    if (cursor != null && !cursor.isClosed()) {
	    	cursor.close();
	    }
	    return list;
	}
	
	public List<String> FindOne(String _table,String _where, String[] rows,String _options) {
		List<String> list = new ArrayList<String>();
		if(_options=="" || _options==null) _options="id DESC LIMIT 1";
		Cursor cursor = this.db.query(_table,rows,_where,null, null, null, _options);
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(0)); 
			} while (cursor.moveToNext());
		}
	    if (cursor != null && !cursor.isClosed()) {
	    	cursor.close();
	    }
	    return list;
	}
	
	//DELETE
	public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}
	
	
	/**
	 * Implementaci—n de SQLiteOpenHelper
	 */
	private static class OpenHelper extends SQLiteOpenHelper {	 
	      
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	 
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE type_account(id INTEGER PRIMARY KEY, name TEXT, state INTEGER)");
			db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY, type TEXT, id_relation INTEGER, name TEXT, phone TEXT, email TEXT, user TEXT, pass TEXT, created TEXT, state INTEGER)");
			db.execSQL("CREATE TABLE capital(id INTEGER PRIMARY KEY, tot_capital INTEGER, created TEXT, updated_cap TEXT, state INTEGER)");
			db.execSQL("CREATE TABLE img_account(id INTEGER PRIMARY KEY, id_account INTEGER, name_img TEXT, url TEXT, state INTEGER)");
			db.execSQL("CREATE TABLE accounts(id INTEGER PRIMARY KEY, id_user INTEGER, id_capital INTEGER, id_type INTEGER, title TEXT, description TEXT, cost_account INTEGER, created TEXT, updated_acc TEXT, state INTEGER)");
		    Log.d("Info","Created table success");
		}
	 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	        onCreate(db);
		}      
	}
	
}
