package cl.ciisa.appaccounts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cl.ciisa.masterDB.DBHelpers;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddActivity extends Activity {
	
	private Spinner typeSpinnerView;
	private Button photo;
	private Button typeViewButton;
	private Button AddAccViewButton;
	private ImageView image;
	private Uri fileUri;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	protected static final String PREFS_NAME = "AppAccounts";
	
	
	/**
	 * Formulario de agregar tipos
	 */
	private Button addTypeButton;
	private EditText nameTypeText;
	private int id = 0;

	/**
	 * Database
	 */
	private DBHelpers db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		id = settings.getInt("id", 0);
		Log.d("-------->", String.valueOf(id));
		db = new DBHelpers(AddActivity.this);
		
		photo = (Button)findViewById(R.id.buttonPhotoView);
		image = (ImageView) findViewById(R.id.imageViewPhoto);
		typeViewButton = (Button) findViewById(R.id.buttonAddType);
		AddAccViewButton = (Button) findViewById(R.id.buttonSaveAcc);
		
		this.updateSpinnerType();
		this.addListener();
		
	}
	
	
	private void addListener(){
		/**
		 * Boton para capturar foto
		 */
		photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
				
			    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
			    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});
		/**
		 * Boton para agregar tipos de cuentas
		 */
		typeViewButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(AddActivity.this);
				dialog.setContentView(R.layout.dialog_type);
				dialog.setTitle(R.string.addType);
				addTypeButton = (Button) dialog.findViewById(R.id.buttonAddTypeDialog);
				nameTypeText = (EditText)dialog.findViewById(R.id.editTextNameTypeDialog);
				
				addTypeButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ContentValues cv = new ContentValues();
						cv.put("name",nameTypeText.getText().toString());
						cv.put("state", 1);
						cv.put("id_user", id);
						cv.put("synchronized", 0);
						db.insertTAble("type_account", cv);
						AddActivity.this.updateSpinnerType();
						dialog.dismiss();
					}
				});
				dialog.show();
				
				
			}
		});
		AddAccViewButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
	}
	
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	
	/** Create a File for saving an image */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "AppAccounts");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("AppAccounts", "failed to create directory");
	            return null;
	        }
	    }
	    
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "ACCOUNTS_"+ timeStamp + ".jpg");
	    }else {
	        return null;
	    }

	    return mediaFile;
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Image saved to:\n" +
	            		fileUri.getPath(), Toast.LENGTH_LONG).show();
	            //Bitmap myBitmap = BitmapFactory.decodeFile(fileUri.getPath());
	            //image.setImageURI(fileUri);
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}

	protected void onStart(){
		super.onStart();
		if(fileUri != null){
			image.setImageURI(fileUri);
		}
		
	}
	public void updateSpinnerType(){
		String[] field = {"name"}; 
		List<String> result = db.selectAllOne("type_account", "", field, "");
		if(result.size() > 0){
			
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, result);
			typeSpinnerView = (Spinner) findViewById(R.id.spinnerType);
			typeSpinnerView.setAdapter(spinnerArrayAdapter);
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

}
