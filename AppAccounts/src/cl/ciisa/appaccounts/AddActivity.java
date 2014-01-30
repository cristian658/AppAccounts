package cl.ciisa.appaccounts;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cl.ciisa.appaccounts.util.ResizeImage;
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

	private EditText nameAccountsView;
	private EditText descriptionView; 
	private EditText costView;
	private Spinner typeSpinnerView;
	private Button photo;
	private Button typeViewButton;
	private Button AddAccViewButton;
	private ImageView image;
	private Uri fileUri;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	private SimpleDateFormat sdf;

	protected static final String PREFS_NAME = "AppAccounts";

	/**
	 * Formulario de agregar tipos
	 */
	private Button addTypeButton;
	private EditText nameTypeText;
	private int id_user = 0;
	private int id_capital = 0;

	/**
	 * Image
	 */
	private static String namePhoto;
	static File mediaStorageDir;

	/**
	 * Database
	 */
	private DBHelpers db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		SharedPreferences settings = getSharedPreferences(AddActivity.PREFS_NAME, 0);
		id_user = settings.getInt("id_us", 0);
		db = new DBHelpers(AddActivity.this);

		photo = (Button) findViewById(R.id.buttonPhotoView);
		image = (ImageView) findViewById(R.id.imageViewPhoto);
		typeViewButton = (Button) findViewById(R.id.buttonAddType);
		AddAccViewButton = (Button) findViewById(R.id.buttonSaveAcc);
		nameAccountsView = (EditText) findViewById(R.id.editTextName);
		descriptionView = (EditText) findViewById(R.id.editTextDescription);
		costView = (EditText) findViewById(R.id.editTextCost);
		this.updateSpinnerType();
		this.addListener();
		sdf = new SimpleDateFormat("d/MM/yyyy");
		String[] rows = {"id"};
		List<String> capitals = db.FindOne("capital", "id_user = "+id_user, rows,"");
		id_capital = Integer.parseInt(capitals.get(0));

	}

	private void addListener() {
		/**
		 * Boton para capturar foto
		 */
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); 
				
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
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
				dialog.setContentView(R.layout.view_dialog_type);
				dialog.setTitle(R.string.addType);
				addTypeButton = (Button) dialog
						.findViewById(R.id.buttonAddTypeDialog);
				nameTypeText = (EditText) dialog
						.findViewById(R.id.editTextType);

				addTypeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ContentValues cv = new ContentValues();
						cv.put("name", nameTypeText.getText().toString());
						cv.put("state", 1);
						cv.put("id_user", id_user);
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
				/**
				 * Imagen
				 */
				//validar si es null ya que se cae la app
				ContentValues cv = new ContentValues();
				//cv.put("name_img", namePhoto);
				cv.put("name_img", "ACCOUNTS_20140126_010500.jpg");
				//cv.put("path", mediaStorageDir.getAbsolutePath()+File.separator+namePhoto);
				cv.put("path", "/storage/sdcard0/Pictures/AppAccounts/ACCOUNTS_20140126_010500.jpg");
				cv.put("state",1); 
				cv.put("synchronized", 0);
				Long id_imagen = db.insertTAble("img_account", cv);
				/**
				 * Cuenta
				 */
				cv = new ContentValues();
				String selectedItem = typeSpinnerView.getSelectedItem().toString();
				cv.put("id_user", id_user);
				String[] field = { "id" };
				cv.put("id_type", db.FindOne("type_account", "name = '"+selectedItem+"'", field, "").get(0));
				cv.put("id_img_account", id_imagen);
				cv.put("title", nameAccountsView.getText().toString());
				cv.put("description", descriptionView.getText().toString());
				cv.put("cost_account", costView.getText().toString());
				cv.put("created", sdf.format(new Date()));
				cv.put("id_capital", id_capital);
				cv.put("state", 1);
				cv.put("synchronized", 0);
				Long id_account = db.insertTAble("accounts", cv);
				if(id_account > 0 ){
					Toast.makeText(AddActivity.this, "La cuenta fue guardado con exito", Toast.LENGTH_LONG).show();
					Intent myIntent = new Intent(AddActivity.this,ListAccountActivity.class);
					AddActivity.this.startActivity(myIntent);
				}

			}
		});
	}

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image */
	private static File getOutputMediaFile(int type) {

		mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AppAccounts");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("AppAccounts", "Fallo al crear el directorio");
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			namePhoto = "ACCOUNTS_" + timeStamp + ".jpg";
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + namePhoto);
		} else {
			return null;
		}

		return mediaFile;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				image.setImageBitmap(ResizeImage.resizeImage(fileUri));

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}
	}


	public void updateSpinnerType() {
		String[] field = { "name" };
		List<String> result = db.selectAllOne("type_account", "id_user = "+id_user, field, "");
		if (result.size() > 0) {

			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_dropdown_item, result);
			typeSpinnerView = (Spinner) findViewById(R.id.spinnerType);
			typeSpinnerView.setAdapter(spinnerArrayAdapter);

		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}*/

}
