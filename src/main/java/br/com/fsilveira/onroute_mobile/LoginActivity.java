package br.com.fsilveira.onroute_mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Button loginBtn = (Button) findViewById(R.id.login_btn);

		loginBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				
				Intent myIntent = new Intent(v.getContext(), VehicleActivity.class);
                startActivityForResult(myIntent, 0);
//				overridePendingTransition(R.a, exitAnim);
			}
		});

	}

	public void onLogin(View view) {
		// EditText email = (EditText) findViewById(R.id.email);
		// EditText password = (EditText) findViewById(R.id.password);

	}

}
