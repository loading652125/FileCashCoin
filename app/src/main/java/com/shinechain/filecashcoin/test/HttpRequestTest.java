package com.shinechain.filecashcoin.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shinechain.filecashcoin.http.HttpCallback;
import com.shinechain.filecashcoin.http.HttpClient;
import com.shinechain.filecashcoin.test.bean.UserVO;

/**
 * Created by zjs on 2018/11/22 16:40
 * Describe:
 */
public class HttpRequestTest {
	public static void main(String[] args) {
//		HttpClient.getAllUser();
//		HttpClient.getAllUser2Bean(new HttpCallback<UserVO>() {
//			@Override
//			public void onSuccess(UserVO userVO) {
//				System.out.println(userVO);
//			}
//
//			@Override
//			public void onFail(Exception e) {
//
//			}
//		});

		/*{
			"areaCode":"86",
				"phone":"15626164088",
				"captcha":"628742",
				"username":"asdasd",
				"password":"56304e8b0f8e7e4f8c70ebb847493c47",
				"confirmPass":"56304e8b0f8e7e4f8c70ebb847493c47"
		}*/
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("areaCode","86");
		jsonObject.addProperty("captcha","628742");
		jsonObject.addProperty("username","asdasd");
		jsonObject.addProperty("password","56304e8b0f8e7e4f8c70ebb847493c47");
		jsonObject.addProperty("confirmPass","56304e8b0f8e7e4f8c70ebb847493c47");
		jsonObject.addProperty("122",12);
		Gson gson = new Gson();
		System.out.println(gson.toJson(jsonObject));


	}
}
