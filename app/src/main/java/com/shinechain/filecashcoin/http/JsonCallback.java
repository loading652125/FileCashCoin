package com.shinechain.filecashcoin.http;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zjs on 2018/11/22 15:32
 * Describe: 序列化json到具体类
 */
public abstract class JsonCallback<T> extends Callback<T> {
	private Class<T> clazz;
	private Gson gson;

	public JsonCallback(Class<T> clazz) {
		this.clazz = clazz;
		this.gson = new Gson();
	}

	@Override
	public T parseNetworkResponse(Response response, int id) throws IOException {
		String jsonString = response.body().string();
		//通过gson序列化
		return gson.fromJson(jsonString,clazz);
	}
}
