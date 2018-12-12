package com.shinechain.filecashcoin.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.geetest.sdk.GTCallBack;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shinechain.filecashcoin.R;
import com.shinechain.filecashcoin.http.HttpInterceptor;
import com.shinechain.filecashcoin.utils.MD5Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Created by zjs on 2018/12/10 14:13
 * Describe:
 */
public class RegisterEmailActivity extends AppCompatActivity {

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

    public static final String TAG = RegisterPhoneActivity.class.getSimpleName();

    private static final String registerGeetest = "http://192.168.16.113:8081/v1/validate/registerGeetest";
    private static final String validateGeetest = "http://192.168.16.113:8081/v1/validate/validateGeetest";
    private static final String sendCaptcha = "http://192.168.16.113:8081/v1/users/sendCaptcha";
    public static final String checkEmail = "http://192.168.16.113:8081/v1/users/checkEmail";
    public static final String checkUsername = "http://192.168.16.113:8081/v1/users/checkUsername";
    public static final String emailRegister = "http://192.168.16.113:8081/v1/users/emailRegister";

    private GT3GeetestUtilsBind gt3GeetestUtils;
    private JSONObject jsonObject = null;
    private AppCompatButton emailGetCode;
    private TextInputEditText email;
    private TextInputEditText verificationCode;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextInputEditText comfirmPassword;
    private TextInputEditText invitation;
    private AppCompatButton registerNow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        gt3GeetestUtils = new GT3GeetestUtilsBind(RegisterEmailActivity.this);
        // 设置debug模式，开代理抓包可使用，默认关闭
        gt3GeetestUtils.setDebug(false);
        // 设置加载webview超时时间，单位毫秒，默认15000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3GeetestUtils.setTimeout(15000);
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3GeetestUtils.setWebviewTimeout(10000);

        emailGetCode = findViewById(R.id.register_email_get_code);
        emailGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCustomVerify();
            }
        });

        //获取资源文件
        email = findViewById(R.id.register_email_email_edittext);
        verificationCode = findViewById(R.id.register_email_verification_edittext);
        username = findViewById(R.id.register_email_username_edittext);
        password = findViewById(R.id.register_email_password_edittext);
        comfirmPassword = findViewById(R.id.register_email_confirm_edittext);
        invitation = findViewById(R.id.register_email_invitation_edit);
        registerNow = findViewById(R.id.register_email_register_btn);

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                String verificationString = verificationCode.getText().toString();
                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();
                String confirmString = comfirmPassword.getText().toString();
                String invitationString = invitation.getText().toString();
                //密码MD5加密
                String passwordMD5 = MD5Utils.encode(passwordString);
                String confirmMD5 = MD5Utils.encode(confirmString);
                Log.i(TAG,passwordMD5);
                Log.i(TAG,confirmMD5);
                //验证
                boolean vaildResult = vaildEdittextString(emailString,verificationString,usernameString,passwordString,confirmString,invitationString);
                if (vaildResult) {
                    //注册接口
                    JsonObject registerJsonObject = new JsonObject();
                    registerJsonObject.addProperty("email",emailString);
                    registerJsonObject.addProperty("captcha",verificationString);
                    registerJsonObject.addProperty("username",usernameString);
                    registerJsonObject.addProperty("password",passwordMD5);
                    registerJsonObject.addProperty("confirmPass",confirmMD5);
                    Log.i(TAG,new Gson().toJson(registerJsonObject));
                    OkHttpUtils
                            .postString()
                            .url(emailRegister)
                            .content(new Gson().toJson(registerJsonObject))
                            .mediaType(MediaType.parse("application/json; charset=utf-8"))
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    //
                                    Log.i(TAG,"邮箱注册响应");
                                    Log.i(TAG,response);
                                }
                            });

                }
            }
        });

    }

    private void startCustomVerify() {
        gt3GeetestUtils.showLoadingDialog(this, null);
        // 设置是否可以点击Dialog灰色区域关闭验证码
        gt3GeetestUtils.setDialogTouch(false);
        OkHttpUtils.get().url(registerGeetest).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject != null) {
                        continueVerify(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void continueVerify(JSONObject jsonObject) {
        gt3GeetestUtils.gtSetApi1Json(jsonObject);
        gt3GeetestUtils.getGeetest(RegisterEmailActivity.this, registerGeetest, validateGeetest, null, new GT3GeetestBindListener() {

            @Override
            public void gt3FirstResult(JSONObject jsonObject) {
                Log.i(TAG, "gt3FirstResult-->" + jsonObject);
            }

            //数据统计
            @Override
            public void gt3GeetestStatisticsJson(JSONObject jsonObject) {
                Log.i(TAG, "gt3GeetestStatisticsJson-->" + jsonObject);
            }
            //自定义二次验证
            @Override
            public boolean gt3SetIsCustom() {
                Log.i(TAG, "gt3SetIsCustom");
                return true;
            }

            //获取数据验证的结果
            @Override
            public void gt3GetDialogResult(boolean status, String result) {
                Log.i(TAG, "gt3GetDialogResult-->status: " + status + "result: " + result);
                if (status) {
                    try {
                        // 1.取出该接口返回的三个参数用于自定义二次验证
                        JSONObject jsonObject = new JSONObject(result);
                        Map<String, String> validateParams = new HashMap<>();
                        validateParams.put("geetest_challenge", jsonObject.getString("geetest_challenge"));
                        validateParams.put("geetest_validate", jsonObject.getString("geetest_validate"));
                        validateParams.put("geetest_seccode", jsonObject.getString("geetest_seccode"));
                        //调用服务器接口发送验证码
                        sendVerificationCode(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    gt3GeetestUtils.gt3TestClose();
                }
            }

            //默认不走这里
            @Override
            public void gt3DialogSuccessResult(String result) {
                Log.i(TAG, "gt3DialogSuccessResult-->" + result);
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");
                        if ("success".equals(status)) {
                            gt3GeetestUtils.gt3TestFinish();
                            // 设置loading消失回调
                            gt3GeetestUtils.setGtCallBack(new GTCallBack() {
                                @Override
                                public void onCallBack() {
                                    // 跳转其他页面操作等
                                    //先不做操作这里
                                }
                            });
                        } else {
                            gt3GeetestUtils.gt3TestClose();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    gt3GeetestUtils.gt3TestClose();
                }
            }

            @Override
            public void gt3DialogOnError(String error) {
                Log.i(TAG, "gt3DialogOnError-->" + error);
            }

        });

    }

    private boolean vaildEdittextString(String emailString, String verificationString, String usernameString, String passwordString, String confirmString, String invitationString) {

        //检查邮箱
        JsonObject emailJsonObject = new JsonObject();
        emailJsonObject.addProperty("email",emailString);
        OkHttpUtils.postString().url(checkEmail).content(new Gson().toJson(emailJsonObject)).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG,"检查邮箱格式结果");
                Log.i(TAG,response);
            }
        });
        //检查用户名
        JsonObject usernameJsonObject = new JsonObject();
        usernameJsonObject.addProperty("username",usernameString);
        OkHttpUtils.postString().url(checkUsername).content(new Gson().toJson(usernameJsonObject)).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
            @Override
            public void onResponse(String response, int id) {
                Log.i(TAG,"检查邮箱格式结果");
                Log.i(TAG,response);
            }
        });

        return true;
    }

    private void sendVerificationCode(String jsonString) {
        Log.i(TAG,"开始请求发送邮箱验证码");
        //
        JSONObject codeJsonObject = null;
        try {
            codeJsonObject = new JSONObject(jsonString);
//            System.out.println(codeJsonObject.getString("geetest_challenge"));
//            System.out.println(codeJsonObject.getString("geetest_validate"));
//            System.out.println(codeJsonObject.getString("geetest_seccode"));
            if ( codeJsonObject != null ) {

                JsonObject validCode = new JsonObject();
                validCode.addProperty("type",1);
                validCode.addProperty("to","loading652125@163.com");
                validCode.addProperty("lang",3);
                validCode.addProperty("geetestChallenge",codeJsonObject.getString("geetest_challenge"));
                validCode.addProperty("geetestValidate",codeJsonObject.getString("geetest_validate"));
                validCode.addProperty("geetestSeccode",codeJsonObject.getString("geetest_seccode"));
                validCode.addProperty("gtServerStatus","1");
                validCode.addProperty("geetestUserId","fcc-geetest");

                System.out.println(new Gson().toJson(validCode));
                OkHttpUtils
                        .postString()
                        .url(sendCaptcha)
                        .content(new Gson().toJson(validCode))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }
                            @Override
                            public void onResponse(String response, int id) {
                                Log.i( TAG, response );
                                if (!TextUtils.isEmpty(response)) {
                                    gt3GeetestUtils.gt3TestFinish();
                                    gt3GeetestUtils.setGtCallBack(new GTCallBack() {
                                        @Override
                                        public void onCallBack() {
                                            // 跳转其他页面操作等
                                        }
                                    });
                                } else {
                                    gt3GeetestUtils.gt3TestClose();
                                }
                            }
                        });
            }
            else {
                System.out.println("无法发送验证码请求");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
