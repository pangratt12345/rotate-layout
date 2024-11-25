package com.github.rongi.rotate_layout.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.rongi.rotate_layout.layout.RotateLayout;

import butterknife.ButterKnife;

public class MainActivity extends Activity {

//	@BindView(R.id.form1_container) RotateLayout form1RotateLayout;
//	@Bind(R.id.form2_container) RotateLayout form2RotateLayout;
//	@Bind(R.id.form3_container) RotateLayout form3RotateLayout;
	RotateLayout form1RotateLayout, form2RotateLayout, form3RotateLayout;
	Button button1, button2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		ButterKnife.bind(this);

		final Context context = this.getApplicationContext();
		button1 = findViewById(R.id.button1);
		button2 = findViewById(R.id.button2);

		button1.setOnClickListener(
			new View.OnClickListener() {
				public void onClick(View view) {
					Toast.makeText(
							context,
							"Ok",
							Toast.LENGTH_SHORT
					).show();
				}
		});

		button2.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						Toast.makeText(
								context,
								"Cancel",
								Toast.LENGTH_SHORT
						).show();
					}
				});

		form1RotateLayout = findViewById(R.id.form1_container);
		form2RotateLayout = findViewById(R.id.form2_container);
		form3RotateLayout = findViewById(R.id.form3_container);

		form1RotateLayout.setOnClickListener( new View.OnClickListener() {
			public void onClick(View view) {
				RotateLayout rotateLayout = (RotateLayout) view;
				int newAngle = rotateLayout.getAngle() + 90;
				rotateLayout.setAngle(newAngle);
			}
		});

		form2RotateLayout.setOnClickListener( new View.OnClickListener() {
			public void onClick(View view) {
				RotateLayout rotateLayout = (RotateLayout) view;
				int newAngle = rotateLayout.getAngle() + 90;
				rotateLayout.setAngle(newAngle);
			}
		});

		form3RotateLayout.setOnClickListener( new View.OnClickListener() {
			public void onClick(View view) {
				RotateLayout rotateLayout = (RotateLayout) view;
				int newAngle = rotateLayout.getAngle() + 90;
				rotateLayout.setAngle(newAngle);
			}
		});
	}

//	/**
//	 * Clicking on a form will rotate it
//	 */
//	@OnClick({R.id.form1_container, R.id.form2_container, R.id.form3_container}) void onForm1ContainerClick(RotateLayout rotateLayout) {
//		int newAngle = rotateLayout.getAngle() + 90;
//		rotateLayout.setAngle(newAngle);
//	}

}
