package com.wilddynamos.bookapp.activity.mybooks;


import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.wilddynamos.bookapp.R;
import com.wilddynamos.bookapp.activity.MultiWindowActivity;
import com.wilddynamos.bookapp.activity.ZoomInOutActivity;
import com.wilddynamos.bookapp.activity.profile.EditProfileActivity;
import com.wilddynamos.bookapp.utils.BitmapWorkerTask;
import com.wilddynamos.bookapp.utils.TakePhoto;
import com.wilddynamos.bookapp.utils.ZoomInOutAction;
import com.wilddynamos.bookapp.ws.remote.action.mybooks.PostOrEditBook;


public class PostOrEditBookActivity extends Activity {
	
	private TextView title;
	private EditText name;
	private ImageView cover;
	private EditText price;
	private TextView wordPer;
	Spinner per;
	private Boolean perValue;
	private LinearLayout rentOnly;
	private EditText duration;
	private TextView durationUnit;
	private EditText description;
	private Button postOrSave;
	private Button takePhoto;
	private Button choosePhoto;
	private boolean isPost;
	private boolean sOrR;
	private Integer id = null;
	
    /***take photo ***/        
    private static final int ACTION_TAKE_PHOTO = 1;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    public static Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    
    TakePhoto takePhotoAction;

    /***choose photo ***/
     final int ACTIVITY_SELECT_IMAGE = 5;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybooks_create_edit);
		
		title = (TextView) findViewById(R.id.createOrEditMyBookTitle);
		name = (EditText) findViewById(R.id.createOrEditMyBookName);
		cover = (ImageView) findViewById(R.id.createOrEditMyBookCover);
	    
		price = (EditText) findViewById(R.id.createOrEditMyBookPrice);
		description = (EditText) findViewById(R.id.createOrEditMyBookDescription);
		postOrSave = (Button) findViewById(R.id.createOrEditMyBookSubmit);
		
		takePhoto = (Button) findViewById(R.id.createOrEditTakePhoto);
		choosePhoto = (Button) findViewById(R.id.createOrEditChoosePhoto);
		
		isPost = getIntent().getExtras().getBoolean("isPost");
		sOrR = getIntent().getExtras().getBoolean("sOrR");
		id = getIntent().getExtras().getInt("id");

		
		 byte[] bytes = getIntent().getByteArrayExtra("BMP");
	     Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	    if(bitmap != null)
	    	cover.setImageBitmap(bitmap);
	    
	    /***take photo ***/
        mImageBitmap = null;
        takePhotoAction = new TakePhoto(this,mCurrentPhotoPath, cover, takePhoto);
        takePhotoAction.start();
        
		if(isPost) {
			postOrSave.setText("Post");
			
			if(!sOrR)
				title.setText("Post a new book for RENT");
			else
				title.setText("Post a new book for SELL");
			
		} else {
			title.setText("Edit a book");
			postOrSave.setText("Save");
		}
		
		wordPer = (TextView) findViewById(R.id.createOrEditMyBookPer);
		rentOnly = (LinearLayout) findViewById(R.id.createOrEditMyBookPerRentOnly);
		per = (Spinner) findViewById(R.id.rentPriceUnitSelection);
		if(!sOrR) {
			duration = (EditText) findViewById(R.id.rentDuration);
			durationUnit = (TextView) findViewById(R.id.rentDurationUnit);
			durationUnit.setText("week");
			
			per.setAdapter(new ArrayAdapter<String>(
					this, 
					R.layout.mybooks_spinneritem, 
					new String[]{"week", "month"}));
			per.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0:
						perValue = false;
						durationUnit.setText("weeks");
						break;
						
					case 1:
						perValue = true;
						durationUnit.setText("months");
						break;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
		} else {
			wordPer.setVisibility(TextView.INVISIBLE);
			per.setVisibility(Spinner.INVISIBLE);
			rentOnly.setVisibility(LinearLayout.INVISIBLE);
		}
		
		
		if(!isPost)
			fill();
		
		
       
	}
	
	private void fill() {
		name.setText(getIntent().getExtras().getString("name"));
		//TODO  read cover form sd card sqlite
		price.setText(getIntent().getExtras().getString("price"));
		if(!sOrR) {
			per.setSelection(getIntent().getExtras().getBoolean("per") ? 1 : 0);
			duration.setText(getIntent().getExtras().getInt("duration") + "");
		}
		description.setText(getIntent().getExtras().getString("description"));
	}

    /****choose Photo**/
	public void choosePhoto(View view){
		Intent i = new Intent(Intent.ACTION_PICK,
                   android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);                       
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE); 
	}
	public void save(View view) {
		
		if("".equals(name.getText().toString())
				|| "".equals(price.getText().toString())) {
			
			Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!sOrR
				&& "".equals(duration.getText().toString())) {
			Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String[] params = null;
		
		if(!sOrR)
			params = new String[] {
					!sOrR + "", id == null ? null : id + "",
					name.getText().toString(), price.getText().toString(),
					description.getText().toString(),
					perValue + "", duration.getText().toString()
				};
		else
			params = new String[] {
					!sOrR + "", id == null ? null : id + "",
					name.getText().toString(), price.getText().toString(),
					description.getText().toString()
				};
		
		PostOrEditBook submit = new PostOrEditBook(this);
		submit.execute(params);
	}
	
	public void cancel(View view) {
		
		new AlertDialog.Builder(this)
			.setTitle("Abort?")
			.setPositiveButton("Yes", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(PostOrEditBookActivity.this, 
							MultiWindowActivity.class);
					intent.putExtra(MultiWindowActivity.TAB_SELECT, 1);
					startActivity(intent);
				}
			})
			.setNegativeButton("Back", null)
			.show();
	}
	
	 /***take photo ***/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                    /** take photo **/
                    case ACTION_TAKE_PHOTO: {
                            if (resultCode == RESULT_OK) {
                            	mCurrentPhotoPath = takePhotoAction.getPath();
                            	 BitmapWorkerTask bitmapworker = new BitmapWorkerTask(mCurrentPhotoPath,cover);
                                 bitmapworker.execute();
                                 System.out.println(mCurrentPhotoPath);
                                 takePhotoAction.galleryAddPic(mCurrentPhotoPath);
                                
                            }
                            break;
                    }
                    /** choose photo **/
                    case ACTIVITY_SELECT_IMAGE:{
                     if(resultCode == RESULT_OK){  
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        mCurrentPhotoPath = cursor.getString(columnIndex);
                        cursor.close();

                        BitmapWorkerTask bitmapworker = new BitmapWorkerTask(mCurrentPhotoPath,cover);
                        bitmapworker.execute();
                    }
                             break;
                    }
                    default:
                    Toast.makeText(PostOrEditBookActivity.this, "SMS not delivered",
                          Toast.LENGTH_SHORT).show();
            } // switch
    }

    // Some lifecycle callbacks so that the image can survive orientation change

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    		Drawable drawable = cover.getDrawable();
    		BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
    		mImageBitmap = bitmapDrawable.getBitmap();
            outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
            outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
            super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
            cover.setImageBitmap(mImageBitmap);
            cover.setVisibility(
                            savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
                                            ImageView.VISIBLE : ImageView.INVISIBLE
            );
    }
    
    /****image zoom in and out***/
    public void zoomInOut(View view){
    	ZoomInOutAction.action(this,cover);
	}
    
} 

