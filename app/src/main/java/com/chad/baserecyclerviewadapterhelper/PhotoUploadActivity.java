package com.chad.baserecyclerviewadapterhelper;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.baserecyclerviewadapterhelper.util.Constant;
import com.chad.baserecyclerviewadapterhelper.util.UriToPath;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoUploadActivity extends AppCompatActivity implements View.OnClickListener{

	private Button UploadBn, ChooseBn, RecoBn;
	private EditText NAME;
	private ImageView imgView;

	private final int IMG_REQUEST = 1;
	private Bitmap bitmap;
	private final String TAG = "uploadIMG";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_upload);

		UploadBn = findViewById(R.id.uploadBn);
		ChooseBn = findViewById(R.id.chooseBn);
		RecoBn = findViewById(R.id.recoBn);
		NAME = findViewById(R.id.name);
		imgView = findViewById(R.id.imageView);
		ChooseBn.setOnClickListener(this);
		UploadBn.setOnClickListener(this);
		RecoBn.setOnClickListener(this);
	}


	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.chooseBn:
				selectImage();
				break;
			case R.id.uploadBn:
				if (imagePath.equals("")){
					Toast.makeText(this,"请先选择图片",Toast.LENGTH_SHORT).show();
					break;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						doPost(imagePath);
					}
				}).start();
				break;
			case R.id.recoBn:
				Log.d("image path", "hellohellohello");
				if (imagePath.equals("")){
					Log.d("image path", imagePath);
					Toast.makeText(this,"请先选择图片",Toast.LENGTH_SHORT).show();
					break;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						Log.d("image path", imagePath);
						String string = doReco();
						Log.d("servermsg",string);
//						Toast.makeText(PhotoUploadActivity.this,string,Toast.LENGTH_SHORT);
					}
				}).start();
				break;
		}
	}


	private String doReco()  {
		String result = "error";
		GetExample getExample = new GetExample();
		try {
//			result = getExample.run(Constant.BASE_URL+"/Recognition.do?image="+imagePath);
			result = getExample.run(Constant.BASE_URL+"/Recognition.do");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	private String doPost(String imagePath) {
		OkHttpClient mOkHttpClient = new OkHttpClient();
		String result = "error";
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.addFormDataPart("image", imagePath,
				RequestBody.create(MediaType.parse("image/jpeg"), new File(imagePath)));
		RequestBody requestBody = builder.build();
		Request.Builder reqBuilder = new Request.Builder();
		Request request = reqBuilder
				.url(Constant.BASE_URL + "/Upload.do")
				.post(requestBody)
				.build();

		Log.d(TAG, "请求地址 " + Constant.BASE_URL + "/Upload.do");
		try{
			Response response = mOkHttpClient.newCall(request).execute();
			Log.d(TAG, "响应码 " + response.code());
			if (response.isSuccessful()) {
				String resultValue = response.body().string();
				Log.d(TAG, "响应体 " + resultValue);

				//在子线程更新UI
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 更新UI的操作
						imgView.setVisibility(View.GONE);
						NAME.setVisibility(View.GONE);
						Toast.makeText(PhotoUploadActivity.this,"上传成功",Toast.LENGTH_SHORT).show();

					}
				});

				return resultValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	public void selectImage() {

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, IMG_REQUEST);
	}


	private String imagePath = "";
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
			Uri path = data.getData();
			imagePath = UriToPath.getPathFromUri(this,path);
			try {
//				Log.d("image path", imagePath);
				bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
				imgView.setImageBitmap(bitmap);
				imgView.setVisibility(View.VISIBLE);
				NAME.setVisibility(View.VISIBLE);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
