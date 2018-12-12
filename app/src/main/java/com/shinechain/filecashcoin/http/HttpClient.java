package com.shinechain.filecashcoin.http;

import android.support.annotation.NonNull;

import com.shinechain.filecashcoin.test.bean.PhoneLogin;
import com.shinechain.filecashcoin.test.bean.RegisterGeetest;
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
	static {
		//超时时间为10秒
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(10000, TimeUnit.MILLISECONDS)
				.readTimeout(10000, TimeUnit.MILLISECONDS)
				.writeTimeout(10000, TimeUnit.MILLISECONDS)
				.addInterceptor(new HttpInterceptor())
				.build();
		OkHttpUtils.initClient(okHttpClient);
	}

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


	public static void registerGeetest(final HttpCallback<RegisterGeetest> callback){
	    OkHttpUtils
                .get()
                .url("http://192.168.16.113:8081/v1/validate/registerGeetest")
                .build()
                .execute(new JsonCallback<RegisterGeetest>(RegisterGeetest.class){

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(RegisterGeetest response, int id) {
                        callback.onSuccess(response);
                    }
                });
    }
	public static void registerGeetest2(final HttpCallback<String> callback){
		OkHttpUtils
				.get()
				.url("http://192.168.16.113:8081/v1/validate/registerGeetest")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String response, int id) {
						callback.onSuccess(response);
					}
				});
	}

}
