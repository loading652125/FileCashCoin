package com.shinechain.filecashcoin.test;

import com.shinechain.filecashcoin.http.HttpCallback;
import com.shinechain.filecashcoin.http.HttpClient;
import com.shinechain.filecashcoin.test.bean.UserVO;

/**
 * Created by zjs on 2018/11/22 16:40
 * Describe:
 */
public class HttpRequestTest {
	public static void main(String[] args) {
		HttpClient.getAllUser();
		HttpClient.getAllUser2Bean(new HttpCallback<UserVO>() {
			@Override
			public void onSuccess(UserVO userVO) {
				System.out.println(userVO);
			}

			@Override
			public void onFail(Exception e) {

			}
		});
	}
}
