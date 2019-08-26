package com.chad.baserecyclerviewadapterhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chad.baserecyclerviewadapterhelper.util.CommonUtil;


public class LoginActivity extends Activity {

	private Button btn_login;
	private EditText et_password;
	private EditText et_username;
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);


		//强制在主线程发送请求
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}


		btn_login = (Button)findViewById(R.id.btn_login);
		et_username = (EditText)findViewById(R.id.et_username);
		et_password= (EditText)findViewById(R.id.et_password);

		btn_login.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {

						boolean isNetConnected = CommonUtil.isNetworkAvailable(LoginActivity.this);
						if (!isNetConnected) {
							//在子线程更新UI
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// 更新UI的操作
									Toast.makeText(LoginActivity.this, "当前网络不可用,请检查网络设置", Toast.LENGTH_SHORT);
								}
							});

//							ToastUtil.ShortToast("当前网络不可用,请检查网络设置");
							return;
						}
						login();
					}
				}).start();
			}
		});

	}

	private void login() {
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(LoginActivity.this, "用户名不能为空",Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(LoginActivity.this, "密码不能为空",Toast.LENGTH_SHORT).show();
			return;
		}

		String res = null;
		try {
//			res = run("http://148.70.41.175:8080/MyApp/Login.do?username=admin&password=admin");
//			res = run("http://192.168.0.100:8080/MyApp/Login.do?username=admin&password=admin");

			res = "success";
			if (res.equals("success")){
				startActivity(new Intent(LoginActivity.this, PullToRefreshUseActivity.class));
				finish();
			}else{
				Toast.makeText(LoginActivity.this, "用户名或密码错误",Toast.LENGTH_SHORT).show();
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}


	}

}
