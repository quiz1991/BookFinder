package com.wilddynamos.bookapp.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wilddynamos.bookapp.R;
import com.wilddynamos.bookapp.ws.remote.action.Signup;

public class SignupActivity extends Activity {
	
	private EditText email;
	private EditText name;
	private EditText password;
	private EditText confirmation;
	private Button signup;
	private Context context;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        
        email = (EditText) findViewById(R.id.userEmail);
        name = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.userPassword);
        confirmation = (EditText) findViewById(R.id.userPasswordConfirm);
        
        signup = (Button) findViewById(R.id.signupButton);
        
        context = this;
        
        signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//check if password equals to the confirmed password
				if (password.getEditableText().toString().equals(confirmation.getEditableText().toString())) {
					//if confirmed, new a signup async task to connect to server
					Signup su = new Signup(SignupActivity.this, context);
					su.execute(new String[] {email.getEditableText().toString(), 
		        			name.getEditableText().toString(), 
		        			password.getEditableText().toString()});
				}

				else Toast.makeText(SignupActivity.this, "Password confirmation not match!", Toast.LENGTH_LONG).show();
			}
		});
    }


	public final static boolean isValidEmail(CharSequence target) { //email validation
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
}
