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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
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
	private RadioGroup rdg_typ_account;
	private String[] ini_fixed_account=new String[4];
	private Uri fileUri;
	private RadioButton radioButton;
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
		rdg_typ_account=(RadioGroup) findViewById(R.id.radioGroupaccount);
		this.updateSpinnerType();
		this.addListener();
		sdf = new SimpleDateFormat("d/MM/yyyy H:m:s");
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
		
		rdg_typ_account.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) { 
	            radioButton = (RadioButton) findViewById(checkedId);
	            final Dialog dialog = new Dialog(AddActivity.this);
				dialog.setContentView(R.layout.view_dialog_accountfixed);
				final Button button_ok;
				final Button button_cancel;
				dialog.setTitle("Cuenta Fija");
				
			    TextView edt0= (TextView) dialog.findViewById(R.id.text_month_ini);
			    TextView edt1= (TextView) dialog.findViewById(R.id.text_year_ini);
			    TextView edt2=(TextView) dialog.findViewById(R.id.text_cuot);
			    TextView edt3=(TextView) dialog.findViewById(R.id.text_val);
				if(ini_fixed_account[0]==null && ini_fixed_account[1]==null){
					edt0.setText("Enero / ");
					edt1.setText("Año Actual");
					edt2.setText("0");
					edt3.setText("0");
				}else{
					edt0.setText(ini_fixed_account[0]+" / ");
					edt1.setText(ini_fixed_account[1]);
					edt2.setText(ini_fixed_account[2]);
					edt3.setText(ini_fixed_account[3]);
				}
				
				//add month     
				     final Spinner spnnmonthini=(Spinner) dialog.findViewById(R.id.monthini);
				     ArrayAdapter<CharSequence> adpmonthini=ArrayAdapter.createFromResource(AddActivity.this, R.array.month_list, android.R.layout.simple_spinner_item);
				     adpmonthini.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				     spnnmonthini.setAdapter(adpmonthini);			     
				     /*spnnmonthini.setOnItemSelectedListener(new OnItemSelectedListener(){
				            @Override
				            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
				              // TODO Auto-generated method stub
				            }	
				            @Override
				            public void onNothingSelected(AdapterView<?> arg0) {
				              // TODO Auto-generated method stub
				            }
				    });*/
				     
					 final Spinner spnnyearini=(Spinner) dialog.findViewById(R.id.yearini);
				     ArrayAdapter<CharSequence> adpyearini=ArrayAdapter.createFromResource(AddActivity.this, R.array.year_list, android.R.layout.simple_spinner_item);
				     adpyearini.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				     spnnyearini.setAdapter(adpyearini);
				

				
				if(radioButton.getText().equals("Fija")){
					button_ok = (Button) dialog.findViewById(R.id.button_ok);
					button_cancel = (Button) dialog.findViewById(R.id.button_cancel);
					final EditText edt00 = (EditText) dialog.findViewById(R.id.editTextCuot);
					final EditText edt01 = (EditText) dialog.findViewById(R.id.editTextVal);
				   //nameTypeText = (EditText) dialog.findViewById(R.id.editTextType);
	               //Toast.makeText(AddActivity.this, "" + radioButton.getText(), 2000).show(); 
					button_ok.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v){
							//private String[] ini_fixed_account,end_fixed_account;	
							ini_fixed_account[0]=spnnmonthini.getSelectedItem().toString();
							ini_fixed_account[1]=spnnyearini.getSelectedItem().toString();
							ini_fixed_account[2]=edt00.getText().toString();
							ini_fixed_account[3]=edt01.getText().toString();
							//Toast.makeText(AddActivity.this,"Finaliza: "+end_fixed_account[0]+" Año: "+end_fixed_account[1], 2000).show();						
							dialog.dismiss();
						}
					});
					
					button_cancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							//ContentValues cv = new ContentValues();
							//cv.put("name", nameTypeText.getText().toString());
							//cv.put("state", 1);
							//cv.put("id_user", id_user);
							//cv.put("synchronized", 0);
							//db.insertTAble("type_account", cv);
							//AddActivity.this.updateSpinnerType();
							dialog.dismiss();
						}
					});
					
			    	dialog.show();
				}else{
					dialog.hide(); 
				}
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
				addTypeButton = (Button) dialog.findViewById(R.id.buttonAddTypeDialog);
				nameTypeText = (EditText) dialog.findViewById(R.id.editTextType);

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
				//add type account
				if(radioButton.getText().equals("Fija")){
					if(ini_fixed_account[0]!=null && ini_fixed_account[1]!=null){
						cv.put("typ_accnt",radioButton.getText().toString());
						cv.put("typ_dt_start",ini_fixed_account[0]+"/"+ini_fixed_account[1]); 
						cv.put("typ_cuot",ini_fixed_account[2]); 
						cv.put("typ_val_cout",ini_fixed_account[3]); 
					}
				}else{
					cv.put("typ_accnt","Variable");
				}		
				
				cv.put("created", sdf.format(new Date()));
				cv.put("id_capital", id_capital);
				cv.put("state", 1);
				cv.put("synchronized", 0);
				Long id_account = db.insertTAble("accounts", cv);
				if(id_account > 0 ){
					Toast.makeText(AddActivity.this, "La cuenta fue guardado con exito", Toast.LENGTH_LONG).show();
					Intent myIntent = new Intent(AddActivity.this,MainFragmentActivity.class);
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
