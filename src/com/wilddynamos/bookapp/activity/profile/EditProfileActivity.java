package com.wilddynamos.bookapp.activity.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wilddynamis.bookapp.utils.TakePhoto;
import com.wilddynamos.bookapp.R;
import com.wilddynamos.bookapp.activity.MultiWindowActivity;
import com.wilddynamos.bookapp.activity.SignupActivity;
import com.wilddynamos.bookapp.model.User;
import com.wilddynamos.bookapp.ws.remote.action.profile.EditMyProfile;

public class EditProfileActivity extends Activity {
	

	ImageView profileImage;
	EditText name;
	EditText gender;
	EditText campus;
	EditText contact;
	EditText address;
	
	Button takePhoto;
	Button choosePhoto;
	Button save;
	Button cancel;
	
	private User user;
	private Context context;
	
	/***take photo ***/	
	private static final int ACTION_TAKE_PHOTO = 1;
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private Bitmap mImageBitmap;
	private String mCurrentPhotoPath;

	TakePhoto takePhotoAction;
	/***take photo ***/
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
    	public void handleMessage(Message msg){
    		
    		if(msg.what < 0)
    			Toast.makeText(EditProfileActivity.this, "Oops!", Toast.LENGTH_SHORT).show();
    		else if(msg.what == 1) {
    			Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
    			save();
    		}
    		else
    			Toast.makeText(EditProfileActivity.this, "What happened?", Toast.LENGTH_SHORT).show();
    	}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_editprofile);   
        
        profileImage = (ImageView) findViewById(R.id.editprofile_image);
		name = (EditText) findViewById(R.id.edit_name);
		gender = (EditText) findViewById(R.id.edit_gender);
		campus = (EditText) findViewById(R.id.edit_campus);
		contact = (EditText) findViewById(R.id.edit_contact);
		address = (EditText) findViewById(R.id.edit_address);
		takePhoto = (Button) findViewById(R.id.editprofile_take_photo_button);
		choosePhoto = (Button) findViewById(R.id.editprofile_choose_photo_button);
		save = (Button) findViewById(R.id.editprofile_saveButton);
		cancel = (Button) findViewById(R.id.editprofile_cancelButton);
		
		context = this;
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				new EditMyProfile(EditProfileActivity.this, context, user)
//					.start();
			}
		});
		/***take photo ***/
		mImageBitmap = null;
		takePhotoAction = new TakePhoto(this,mCurrentPhotoPath, profileImage, takePhoto);
		takePhotoAction.start();
		/***take photo ***/
    }
	
	/* save button*/
	public void save(){
		Intent intent = new Intent(this, MultiWindowActivity.class);
		intent.putExtra(MultiWindowActivity.TAB_SELECT, 2);
		startActivity(intent);
	}
	
	/* cancel button*/
	public void cancel(View view){
		Intent intent = new Intent(this, MultiWindowActivity.class);
		intent.putExtra(MultiWindowActivity.TAB_SELECT, 2);
		startActivity(intent);
	}
	
	public Handler getHandler() {
		return handler;
	}
	/***take photo ***/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ACTION_TAKE_PHOTO: {
				if (resultCode == RESULT_OK) {
					takePhotoAction.handleCameraPhoto( );
				}
				break;
			}
			default:
			Toast.makeText(EditProfileActivity.this, "SMS not delivered",
	                      Toast.LENGTH_SHORT).show();
		} // switch
	}

	// Some lifecycle callbacks so that the image can survive orientation change

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		profileImage.setImageBitmap(mImageBitmap);
		profileImage.setVisibility(
				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);
	}
	/***take photo ***/
} 

