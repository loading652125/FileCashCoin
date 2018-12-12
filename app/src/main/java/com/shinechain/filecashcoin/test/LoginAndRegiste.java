package com.shinechain.filecashcoin.test;

import com.shinechain.filecashcoin.utils.PhoneEmailCheckUtils;

/**
 * Created by zjs on 2018/12/08 11:01
 * Describe:
 */
public class LoginAndRegiste {

    public static void main(String[] args) {
//        HttpClient.phoneLogin("91239123", "56304e8b0f8e7e4f8c70ebb847493c47", new HttpCallback<PhoneLogin>() {
//            @Override
//            public void onSuccess(PhoneLogin phoneLogin) {
//                System.out.println(phoneLogin);
//            }
//
//            @Override
//            public void onFail(Exception e) {
//
//            }
//        });

//        HttpClient.registerGeetest(new HttpCallback<RegisterGeetest>() {
//            @Override
//            public void onSuccess(RegisterGeetest registerGeetest) {
//                //System.out.println(registerGeetest);
//            }
//
//            @Override
//            public void onFail(Exception e) {
//
//            }
//        });
        //System.out.println(PhoneEmailCheckUtils.isPhoneLegal("159860254861"));
        System.out.println(PhoneEmailCheckUtils.isEmailLegal("loading652125@163.com"));


    }


}
