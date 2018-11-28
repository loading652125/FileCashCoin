package com.shinechain.filecashcoin.http;

import android.os.Build;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zjs on 2018/11/22 15:54
 * Describe:
 */
public class HttpInterceptor implements Interceptor {

	private static final String UA = "User-Agent";
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request()
				.newBuilder()
				.addHeader(UA,makeUA())
				.build();
		return chain.proceed(request);
	}

	//创建UserAgent
	private String makeUA() {
		return Build.BOARD + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE;
	}
}
