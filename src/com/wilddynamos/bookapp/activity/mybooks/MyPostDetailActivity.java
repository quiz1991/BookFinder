package com.wilddynamos.bookapp.activity.mybooks;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wilddynamos.bookapp.R;
import com.wilddynamos.bookapp.activity.BaseBookDetailActivity;

public class MyPostDetailActivity extends BaseBookDetailActivity {
	
	private TextView requesterNum;
	private Button seeRequest;
	
	private boolean sOrR;
	private boolean per;
	private int duration;
	private String description;
	
	@Override
	protected void createFunctionSpecificView() {
		
		((LinearLayout) findViewById(R.id.bookDetail_myPost_requestInfo))
				.setVisibility(LinearLayout.VISIBLE);
		((LinearLayout) findViewById(R.id.bookDetail_myPost_buttons))
		.setVisibility(LinearLayout.VISIBLE);
		
		requesterNum = (TextView) findViewById(R.id.bookDetail_myPost_requesterNum);
		seeRequest = (Button) findViewById(R.id.bookDetail_myPost_seeRequesters);
	}
	
	@Override
	protected String[] getParams() {
		return new String[]{id + "", "isMine"};
	}

	@Override
	protected void fillFunctionSpecificView(JSONObject jo) {
		
		try {
			
			if(jo.getBoolean("sOrR")) {
				title.setText("I am selling this book");
				this.sOrR = true;
			} else {
				title.setText("I am renting this book");
				this.sOrR = false;
			}
			
			this.per = jo.getBoolean("per");
			this.duration = jo.getInt("availableTime");
			this.description = jo.getString("description");
			
			requesterNum.setText(jo.getString("requesterNum"));
			if(jo.getInt("requesterNum") == 0)
				seeRequest.setClickable(false);
			else
				seeRequest.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								MyPostDetailActivity.this, RequesterListActivity.class);
						
						intent.putExtra("id", id);
						
						MyPostDetailActivity.this.startActivity(intent);
					}
				});
			like.setClickable(false);
			
		} catch (JSONException e) {
		}
		
	}
	
	public void editPost(View view){
		
		Intent intent = new Intent(this, PostOrEditBookActivity.class);
		
		intent.putExtra("id", id);
		intent.putExtra("isPost", false);
		intent.putExtra("name", name.getText().toString());
		intent.putExtra("price", price.getText().toString()
				.substring(0, price.getText().toString().indexOf(' ')));
		intent.putExtra("sOrR", sOrR);
		if(!sOrR) {
			intent.putExtra("per", per);
			intent.putExtra("duration", duration);
		}
		intent.putExtra("description", description);
		
		startActivity(intent);
	}
	
	public void deletePost(View view){
		//TODO
	}
}
