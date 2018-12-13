package com.shinechain.filecashcoin.view;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
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
import com.shinechain.filecashcoin.http.JsonCallback;
import com.shinechain.filecashcoin.bean.CommonResult;
import com.shinechain.filecashcoin.bean.LoginResult;
import com.shinechain.filecashcoin.utils.MD5Utils;
import com.shinechain.filecashcoin.utils.PhoneEmailCheckUtils;
import com.shinechain.filecashcoin.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

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

    public static final String TAG = LoginActivity.class.getSimpleName();
    private static final String registerGeetest = "http://192.168.16.113:8081/v1/validate/registerGeetest";
    private static final String validateGeetest = "http://192.168.16.113:8081/v1/validate/validateGeetest";
    private static final String phoneLogin = "http://192.168.16.113:8081/v1/users/phoneLogin";
    private static final String emailLogin = "http://192.168.16.113:8081/v1/users/emailLogin";
    private static final String loginCallback = "http://192.168.16.113:8080/v1/users/loginCallback";

    private GT3GeetestUtilsBind gt3GeetestUtils;
    private JSONObject jsonObject;
    private TextInputEditText username;
    private TextInputEditText password;
    private AppCompatButton loginBtn;
    private AppCompatButton register;
    private CardView cardView;
    private boolean isPhoneString;
    private boolean isEmailString;
    private String usernameString;
    private String passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        //
        gt3GeetestUtils = new GT3GeetestUtilsBind(LoginActivity.this);
        // 设置debug模式，开代理抓包可使用，默认关闭，TODO 生产环境务必设置为false
        gt3GeetestUtils.setDebug(true);
        // 设置加载webview超时时间，单位毫秒，默认15000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3GeetestUtils.setTimeout(15000);
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3GeetestUtils.setWebviewTimeout(10000);
        username = findViewById(R.id.login_username_edittext);
        password = findViewById(R.id.login_password_edittext);
        loginBtn = findViewById(R.id.login_btn);
        register = findViewById(R.id.register_btn);
        cardView = findViewById(R.id.login_cardview);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检验数据
                usernameString = username.getText().toString().trim();
                passwordString = password.getText().toString().trim();
                if (!TextUtils.isEmpty(usernameString) && !TextUtils.isEmpty(passwordString)) {
                    //判断手机还是密码登录
                    isPhoneString = PhoneEmailCheckUtils.isChinaPhoneLegal(usernameString);
                    isEmailString = PhoneEmailCheckUtils.isEmailLegal(usernameString);
                    if (!isPhoneString && !isEmailString) {
                        Snackbar.make(cardView,"请输入正确手机号码或者邮箱",Snackbar.LENGTH_INDEFINITE).show();
                        return;
                    }
                    //极验验证
                    startCustomVerify();
                } else {
                    //用户名或者密码为空
                    Snackbar.make(cardView,"用户名或者密码为空",Snackbar.LENGTH_SHORT).show();
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
        gt3GeetestUtils.getGeetest(LoginActivity.this, registerGeetest, validateGeetest, null, new GT3GeetestBindListener() {

            /*@Override
            public void gt3FirstResult(JSONObject jsonObject) {
                Log.i(TAG, "gt3FirstResult-->" + jsonObject);
            }
            //数据统计
            @Override
            public void gt3GeetestStatisticsJson(JSONObject jsonObject) {
                Log.i(TAG, "gt3GeetestStatisticsJson-->" + jsonObject);
            }*/
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
                    loginLogic(result);
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
                Snackbar.make(cardView,"验证失败，请重试",Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private void loginLogic(String jsonString) {
        JsonObject loginJson = new JsonObject();
        String encodePassword = MD5Utils.encode(passwordString);
        loginJson.addProperty("password",encodePassword);
        String loginUrl = "";
        if (isPhoneString){
            loginJson.addProperty("phone",usernameString);
            loginUrl = phoneLogin;
        }else if (isEmailString) {
            loginJson.addProperty("email",usernameString);
            loginUrl = emailLogin;
        }
        OkHttpUtils.postString()
                    .url(loginUrl)
                    .content(new Gson().toJson(loginJson))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new JsonCallback<LoginResult>(LoginResult.class) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            gt3GeetestUtils.gt3TestFinish();
                            gt3GeetestUtils.gt3TestClose();
                            Snackbar.make(cardView,"登录失败",Snackbar.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onResponse(LoginResult response, int id) {
                            Log.i(TAG,"开始登录登录");
                            //判断状态
                            if (new Integer(200).equals(response.getStatus()) && new Integer(10000).equals(response.getCode())) {
                                //成功登录的状态
                                String uuid = response.getData().getUuid();
                                //关闭操作
                                if (!TextUtils.isEmpty(uuid)) {
                                    gt3GeetestUtils.gt3TestFinish();
                                    gt3GeetestUtils.setGtCallBack(new GTCallBack() {
                                        @Override
                                        public void onCallBack() {
                                            // 跳转其他页面操作等
                                        }
                                    });
                                    //拿取JWT
                                    Log.i(TAG,uuid);
                                    getJWT(uuid);
                                } else {
                                    gt3GeetestUtils.gt3TestClose();
                                }
                            } else {
                                if (response.getDevIfno() != null){
                                    Snackbar.make(cardView,response.getDevIfno(),Snackbar.LENGTH_SHORT).show();
                                }else {
                                    Snackbar.make(cardView,response.getMsg(),Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
    }

    private void getJWT(String uuid) {
        OkHttpUtils
                .get()
                .url(loginCallback)
                .addParams("code",uuid)
                .build()
                .execute(new JsonCallback<CommonResult>(CommonResult.class){
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(CommonResult response, int id) {
                        if (new Integer(200).equals(response.getStatus()) && new Integer(10000).equals(response.getCode())) {
                            String data = response.getData();
                            if (!TextUtils.isEmpty(data)) {
                                SPUtils.put(LoginActivity.this,"jwt",data);
                                Log.i(TAG,"保存登录信息成功");
                                System.out.println(SPUtils.get(LoginActivity.this, "jwt", String.class));
                            }else {

                            }
                        } else {
                            if (response.getDevIfno() != null){
                                Snackbar.make(cardView,response.getDevIfno(),Snackbar.LENGTH_SHORT).show();
                            }else {
                                Snackbar.make(cardView,response.getMsg(),Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
