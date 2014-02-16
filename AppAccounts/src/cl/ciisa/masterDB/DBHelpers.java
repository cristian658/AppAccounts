package cl.ciisa.masterDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Clase controladora de la base de datos.
 * @author cfica
 */
public class DBHelpers {
	
	private static final String DATABASE_NAME = "appAccounts.db";
	private static final int DATABASE_VERSION = 1;
	private Context context;
	private SQLiteDatabase db; 

	public DBHelpers(Context context) {
		this.context = context;
	    OpenHelper openHelper = new OpenHelper(this.context);
	    this.db = openHelper.getWritableDatabase();
	}
	
	/*INSERT*/
	//ContentValues nuevoRegistro = new ContentValues();
	//nuevoRegistro.put("codigo", "6");
	//nuevoRegistro.put("nombre","usuariopru");
	public long insertTAble(String table,ContentValues params) {
		return this.db.insert(table, null, params);
	}
	
	
	//UPDATED
	//ContentValues valores = new ContentValues();
	//valores.put("nombre","usunuevo");
	public long updateTable(String table,ContentValues params,String _where) {	
		return this.db.update(table,params,_where,null);
	}
	
	//DELETE
		public void deleteAll(String table) {
			this.db.delete(table, null, null);
		}
	
	//private final String MY_QUERY = "SELECT * FROM table_a a INNER JOIN table_b b ON a.id=b.other_id WHERE b.property_id=?";
	//db.rawQuery(MY_QUERY, new String[]{String.valueOf(propertyId)});
	public ArrayList<ArrayList<String>> selectJoin(String query, String[] column) {
		   ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
			Cursor cursor = this.db.rawQuery(query,null);
			if (cursor.moveToFirst()) {
				int c=0;
				do{
					list.add(new ArrayList<String>());
						for (int j = 0 ; j < column.length ; j++){
							list.get(c).add(cursor.getString(cursor.getColumnIndex(column[j])));
					    }
				   c+=1;
				}while (cursor.moveToNext());
		    }
		    if (cursor != null && !cursor.isClosed()) {
		   	cursor.close();
		    }
		    return list;
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
	
	
	/**
	 * Implementación de SQLiteOpenHelper
	 */
	private static class OpenHelper extends SQLiteOpenHelper {	 
	      
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	 
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE type_account(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT,id_user INTEGER, state INTEGER, synchronized INTEGER)");
			db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, id_relation INTEGER, name TEXT, phone TEXT, email TEXT, user TEXT, pass TEXT, created TEXT, state INTEGER)");
			db.execSQL("CREATE TABLE capital(id INTEGER PRIMARY KEY AUTOINCREMENT,id_user INTEGER, tot_capital INTEGER, created TEXT, updated_cap TEXT, state INTEGER, synchronized INTEGER)");
			db.execSQL("CREATE TABLE img_account(id INTEGER PRIMARY KEY AUTOINCREMENT, name_img TEXT, path TEXT, state INTEGER, synchronized INTEGER)");
			db.execSQL("CREATE TABLE accounts(id INTEGER PRIMARY KEY AUTOINCREMENT, id_user INTEGER, id_capital INTEGER, id_type INTEGER, id_img_account INTEGER,id_share_account INTEGER, title TEXT, description TEXT, cost_account INTEGER, typ_accnt TEXT, typ_dt_start TEXT, typ_cuot TEXT, typ_val_cout TEXT,created TEXT, updated_acc TEXT, state INTEGER, synchronized INTEGER)");
			db.execSQL("CREATE TABLE share_account(id INTEGER PRIMARY KEY AUTOINCREMENT, id_user INTEGER, id_user_share INTEGER,id_account INTEGER, mail TEXT, created TEXT, synchronized INTEGER)");
		    Log.d("Info","Created table success");
		}
	 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	        //onCreate(db);
		}      
	}
	
}
