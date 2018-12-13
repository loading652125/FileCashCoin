package com.shinechain.filecashcoin.http;

import android.support.annotation.NonNull;

import com.shinechain.filecashcoin.bean.PhoneLogin;
import com.shinechain.filecashcoin.bean.RegisterGeetest;
import com.shinechain.filecashcoin.test.bean.UserVO;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by zjs on 2018/11/22 16:01
 * Describe:
 */
public class HttpClient {

	//测试请求
	public static void getAllUser(){
		OkHttpUtils
				.get()
				.url("http://192.168.16.166:8080/user/all")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String response, int id) {
						System.out.println(response);
					}
				});
	}

	public static void getAllUser2Bean(@NonNull final HttpCallback<UserVO> callback){
		OkHttpUtils
				.get()
				.url("http://192.168.16.166:8080/user/all")
				.build()
				.execute(new JsonCallback<UserVO>(UserVO.class) {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(UserVO response, int id) {
						callback.onSuccess(response);
					}
				});
	}

	//具体请求

	public static void phoneLogin(String phone,String password,final HttpCallback<PhoneLogin> callback) {
		OkHttpUtils
				.post()
				.url("http://192.168.16.113:8081/v1/users/phoneLogin")
				.addParams("phone",phone)
				.addParams("password",password)
				.build()
				.execute(new JsonCallback<PhoneLogin>(PhoneLogin.class){

					@Override
					public void onError(Call call, Exception e, int id) {

					}
					@Override
					public void onResponse(PhoneLogin response, int id) {
						callback.onSuccess(response);
					}
				});
	}

}
