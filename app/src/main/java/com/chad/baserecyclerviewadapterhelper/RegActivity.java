package com.chad.baserecyclerviewadapterhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chad.baserecyclerviewadapterhelper.util.CommonUtil;
import com.chad.baserecyclerviewadapterhelper.util.Constant;


public class RegActivity extends Activity {


    private Button reg;
    private EditText reg_username;
    private EditText reg_password;




    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        reg = findViewById(R.id.regreg);
        reg_username = findViewById(R.id.reg_username);
        reg_password = findViewById(R.id.reg_password);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        boolean isNetConnected = CommonUtil.isNetworkAvailable(RegActivity.this);
                        if (!isNetConnected) {
                            //在子线程更新UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 更新UI的操作
                                    Toast.makeText(RegActivity.this, "当前网络不可用,请检查网络设置", Toast.LENGTH_SHORT);
                                }
                            });

//							ToastUtil.ShortToast("当前网络不可用,请检查网络设置");
                            return;
                        }
                        userreg();
                    }
                }).start();
            }
            });


    }

        private void userreg() {
            String name = reg_username.getText().toString();
            String password = reg_password.getText().toString();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(RegActivity.this, "用户名不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegActivity.this, "密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }

            String res = null;
            GetExample getExample = new GetExample();
            try {
                res = getExample.run(Constant.BASE_URL+"/Register.do?username="+name+"&password="+password);
			Log.d("reglog", res);
                if (res.equals("register succsed\n")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 更新UI的操作
                            Toast.makeText(RegActivity.this, "注册成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                    startActivity(new Intent(RegActivity.this, LoginActivity.class));
                    finish();
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 更新UI的操作
                            Toast.makeText(RegActivity.this, "错误,请重新输入或返回",Toast.LENGTH_SHORT).show();
                        }
                    });
                    reg_password.setText("");
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }


        }

    }
