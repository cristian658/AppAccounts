package cl.ciisa.appaccounts;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ListAccountActivity extends Activity {

	private Button agregarView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_account);
		agregarView = (Button)findViewById(R.id.agregarView);
		addListenerButton();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_account, menu);
		return true;
	}
	
	public void addListenerButton(){
		agregarView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(ListAccountActivity.this,AddActivity.class);
				ListAccountActivity.this.startActivity(myIntent);
			}
		});
	}

}
