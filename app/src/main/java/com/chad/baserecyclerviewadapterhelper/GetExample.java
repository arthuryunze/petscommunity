package com.chad.baserecyclerviewadapterhelper;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;

/**
 * Created by yunze on 2019/8/23 21:02
 * Email: arthur.yunze@gmail.com
 */

public class GetExample {
	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}
}
