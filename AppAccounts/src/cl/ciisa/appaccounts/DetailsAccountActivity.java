package cl.ciisa.appaccounts;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import cl.ciisa.appaccounts.util.ResizeImage;
import cl.ciisa.masterDB.DBHelpers;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


/**id INTEGER PRIMARY KEY AUTOINCREMENT, 
 * id_user INTEGER, 
 * id_capital INTEGER, 
 * id_type INTEGER, 
 * id_img_account INTEGER,
 * id_share_account INTEGER, 
 * title TEXT, 
 * description TEXT, 
 * cost_account INTEGER, 
 * created TEXT, 
 * updated_acc TEXT, 
 * state INTEGER, 
 * synchronized INTEGER
 */
public class DetailsAccountActivity extends Activity {

	
	private DBHelpers dh;
	private int id_acc;
	private String name;
	private String type;
	private int id_img;
	private String description;
	private int cost_account;
	private String created;
	private String path;
	
	
	/**
	 * View
	 */
	private TextView nameView;
	private TextView typeView;
	private TextView descriptionView;
	private TextView costView;
	private TextView createdView;
	private ImageView imageView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_account);
		
		Intent myIntent = getIntent();
		Log.d("id_acc",  myIntent.getStringExtra("key"));
		id_acc = Integer.parseInt(myIntent.getStringExtra("key"));
		this.getViews();
		dh = new DBHelpers(this);
		this.getDatos();
		this.setView();
		
		
		//TextView text = (TextView) findViewById(R.id.news2);
		//text.setText(myintent.getStringExtra("key"));
		
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details_account, menu);
		return true;
	}*/
	
	public void getViews(){
		nameView = (TextView) findViewById(R.id.textViewName);
		typeView = (TextView) findViewById(R.id.TextViewType);
		descriptionView = (TextView) findViewById(R.id.textViewDescription);
		costView = (TextView) findViewById(R.id.textViewCost);
		createdView = (TextView) findViewById(R.id.textViewCreated);
		imageView = (ImageView) findViewById(R.id.imageView1);
	}
	
	public void getDatos(){
		//String[] rows = {"id_type","id_img_account","title","description","cost_account","created"};
		//List<String> account = dh.FindOne("accounts", "id = "+id_acc, rows,"");
		String[] column={"accounts.title","accounts.description","accounts.cost_account","accounts.created", "type_account.name","img_account.path"};
	    String sql = "SELECT accounts.id_type, accounts.title, accounts.description, accounts.cost_account, accounts.created, type_account.name, img_account.path "  +
	    	    "FROM accounts INNER JOIN users ON users.id=accounts.id_user " +
	    	    "INNER JOIN type_account ON type_account.id=accounts.id_type " +
	    	    "INNER JOIN img_account ON img_account.id=accounts.id_img_account " +
	    	    "WHERE accounts.id = "+id_acc+" " +
	    	    "ORDER BY accounts.id ASC limit 1";
	    ArrayList<ArrayList<String>> account = dh.selectJoin(sql,column);
	    for (ArrayList<String> acc : account){
	    	name = acc.get(0);
	    	description = acc.get(1);
	    	cost_account = Integer.parseInt(acc.get(2));
	    	created = acc.get(3);
	    	type = acc.get(4);
	    	path = acc.get(5);
	    	
	    	System.out.println(name+" - "+description+" - "+cost_account+" - "+created+" - "+type+" - "+path );
	    	
	    }
	    
	    
	    Log.d("sql", sql);
	}
	public void setView(){
		nameView.setText(name);
		typeView.setText(type);
		descriptionView.setText(description);
		costView.setText(String.valueOf(cost_account));
		createdView.setText(created);
		File image = new File (path);
		if(image.exists()){
			Uri uri = Uri.fromFile(image);
			imageView.setImageBitmap(ResizeImage.resizeImage(uri));
			//image.setImageBitmap(ResizeImage.resizeImage(fileUri));
			
		}
		
	}

}
