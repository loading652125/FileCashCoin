package com.shinechain.filecashcoin.view;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.geetest.sdk.GTCallBack;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shinechain.filecashcoin.R;
import com.shinechain.filecashcoin.http.HttpInterceptor;
import com.shinechain.filecashcoin.http.JsonCallback;
import com.shinechain.filecashcoin.test.bean.Result;
import com.shinechain.filecashcoin.test.bean.ValidateCode;
import com.shinechain.filecashcoin.utils.MD5Utils;
import com.shinechain.filecashcoin.utils.PhoneEmailCheckUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class RegisterPhoneActivity extends AppCompatActivity {

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
    private static final String TAG = RegisterPhoneActivity.class.getSimpleName();

    private static final String registerGeetest = "http://192.168.16.113:8081/v1/validate/registerGeetest";
    private static final String validateGeetest = "http://192.168.16.113:8081/v1/validate/validateGeetest";
    private static final String sendCaptcha = "http://192.168.16.113:8081/v1/users/sendCaptcha";
    private static final String checkPhone = "http://192.168.16.113:8081/v1/users/checkPhone";
    private static final String checkUsername = "http://192.168.16.113:8081/v1/users/checkUsername";
    private static final String phoneRegister = "http://192.168.16.113:8081/v1/users/phoneRegister";

    private GT3GeetestUtilsBind gt3GeetestUtils;
    private AppCompatButton phoneGetCodeBtn;
    private TextInputEditText phone;
    private TextInputEditText verificationCode;
    private TextInputEditText password;
    private TextInputEditText comfirmPassword;
    private TextInputEditText invitation;
    private AppCompatButton registerNow;
    private TextInputEditText username;
    private NiceSpinner niceSpinner;
    private TextInputLayout phoneLayout;
    private List<String> areaList;
    //发送验证相关
    private String userId = "";
    private MyCountTimer countTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        initView();
        initGeetTest();

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG,editable.toString());
                String phoneNumber = phone.getText().toString().trim();
                //本地判断
                boolean isPhoneLegal = PhoneEmailCheckUtils.isPhoneLegal(phoneNumber);
                if ( !isPhoneLegal ) {
                    phoneLayout.setError("无效手机号码");
                }else {
                    //服务器检测是不是已经被注册
                    checkPhone(phoneNumber);
                }

            }
        });

        phoneGetCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (phoneLayout.getError() == null && !TextUtils.isEmpty(phone.getText().toString())) {
                   //改变按钮
                   countTimer = new MyCountTimer(60*1000, 1000);
                   countTimer.start();
                   //开始验证
                   startCustomVerify();
               }else {
                   Toast.makeText(RegisterPhoneActivity.this,"手机号码有误，请检查！",Toast.LENGTH_SHORT).show();
               }
            }
        });

        registerNow.setOnClickListener(new RegisterClickListener());

    }

    //检查手机号码
    private void checkPhone(String phoneString) {
        //验证手机
        JsonObject phoneJsonObject = new JsonObject();
        phoneJsonObject.addProperty("phone",phoneString);
        OkHttpUtils
                .postString()
                .url(checkPhone)
                .content(new Gson().toJson(phoneJsonObject))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new JsonCallback<Result>(Result.class){

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(Result response, int id) {
                        if (new Integer(200).equals(response.getStatus()) && new Integer(10000).equals(response.getCode())) {
                            //不做逻辑先
                            phoneLayout.setError(null);
                            phoneLayout.setErrorEnabled(false);
                        }else{

                            phoneLayout.setError(response.getMsg());
                            phoneLayout.setError(null);
                        }
                    }
                });
    }

    private void initGeetTest() {
        gt3GeetestUtils = new GT3GeetestUtilsBind(RegisterPhoneActivity.this);
        // 设置debug模式，开代理抓包可使用，默认关闭
        gt3GeetestUtils.setDebug(false);
        // 设置加载webview超时时间，单位毫秒，默认15000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3GeetestUtils.setTimeout(15000);
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3GeetestUtils.setWebviewTimeout(10000);
    }

    private void initView() {
        //后面根据地区代码来生成
        niceSpinner = (NiceSpinner) findViewById(R.id.spinner);
        areaList = new LinkedList<>(Arrays.asList("+86(China)", "+852(Hong Kong)","+886(Taiwan)"));
        niceSpinner.attachDataSource(areaList);
        phoneGetCodeBtn = findViewById(R.id.register_email_get_code);
        //获取
        phone = findViewById(R.id.register_phone_edittext);
        verificationCode = findViewById(R.id.register_phone_verification_edittext);
        username = findViewById(R.id.register_phone_username_edittext);
        password = findViewById(R.id.register_phone_password_edittext);
        comfirmPassword = findViewById(R.id.register_phone_confirm_edittext);
        invitation = findViewById(R.id.register_phone_invitation_edit);
        registerNow = findViewById(R.id.register_phone_register_btn);
        phoneLayout = findViewById(R.id.register_phone_phonelayout);
    }

    private boolean vaildEdittextString(String phoneString, String verificationString,String usernameString, String passwordString, String confirmString, String invitationString) {

        //验证用户名
        JsonObject usernameJsonObject  = new JsonObject();
        usernameJsonObject.addProperty("username",usernameString);
        OkHttpUtils
                .postString()
                .url(checkUsername)
                .content(new Gson().toJson(usernameJsonObject))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG,"用户名验证");
                        Log.i(TAG,response);
                    }
                });
        //验证其他

        return true;
    }

    private void startCustomVerify() {
        gt3GeetestUtils.showLoadingDialog(this, null);
        // 设置是否可以点击Dialog灰色区域关闭验证码
        gt3GeetestUtils.setDialogTouch(false);
        OkHttpUtils
                .get()
                .url(registerGeetest)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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
        gt3GeetestUtils.getGeetest(RegisterPhoneActivity.this, registerGeetest, validateGeetest, null, new GT3GeetestBindListener() {

            @Override
            public void gt3FirstResult(JSONObject jsonObject) {
                Log.i(TAG, "gt3FirstResult-->" + jsonObject);
                //这里拿取user_id
                try {
                    userId = jsonObject.getString("user_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                    //调用服务器接口发送验证码
                    sendVerificationCode(result);
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

    private void sendVerificationCode(String jsonString) {
        Log.i(TAG,"开始请求发送验证码" + jsonString);
        JSONObject codeJsonObject = null;
        try {
            codeJsonObject = new JSONObject(jsonString);
            if ( codeJsonObject != null ) {
                ValidateCode validateCode = new ValidateCode();
                //这里设置为1 为注册
                validateCode.setType(1);
                //拿取手机号码
                String area = areaList.get(niceSpinner.getSelectedIndex());
                String areaCode = area.substring(0,3);
                String currentPhone = areaCode + phone.getText().toString().trim();
                validateCode.setTo(currentPhone);
                //极验数据
                validateCode.setGeetestChallenge(codeJsonObject.getString("geetest_challenge"));
                validateCode.setGeetestValidate(codeJsonObject.getString("geetest_validate"));
                validateCode.setGeetestSeccode(codeJsonObject.getString("geetest_seccode"));
                //TODO 这里需要拿去App当时运行的语言环境
                validateCode.setLang(1);
                //这里去那设置
                validateCode.setGeetestUserId(userId);
                validateCode.setGtServerStatus("1");
                System.out.println(new Gson().toJson(validateCode));
                OkHttpUtils
                        .postString()
                        .url(sendCaptcha)
                        .content(new Gson().toJson(validateCode))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }
                            @Override
                            public void onResponse(String response, int id) {
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
                                //转化成map
                                Map<String,String> map = new Gson().fromJson(response,new TypeToken<HashMap<String,String>>(){}.getType());
                                if ("10000".equals(map.get("code"))) {
                                    //发送验证码成功，设置按钮倒计时
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countTimer.cancel();
        if (gt3GeetestUtils != null) {
            gt3GeetestUtils.gt3TestClose();
        }
    }

    class RegisterClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            String phoneString = phone.getText().toString();
            String verificationString = verificationCode.getText().toString();
            String usernameString = username.getText().toString();
            String passwordString = password.getText().toString();
            String confirmString = comfirmPassword.getText().toString();
            String invitationString = invitation.getText().toString();
            //密码MD5加密
            String passwordMD5 = MD5Utils.encode(passwordString);
            String confirmMD5 = MD5Utils.encode(confirmString);

            //验证手机,有错误或者为空时，不做其他操作
            if (phoneLayout.getError() != null || TextUtils.isEmpty(phone.getText().toString())) {
                return;
            }
            //验证用户名
            if (!TextUtils.isEmpty(usernameString)){
                //验证用户名
                JsonObject usernameJsonObject  = new JsonObject();
                usernameJsonObject.addProperty("username",usernameString);
                OkHttpUtils
                        .postString()
                        .url(checkUsername)
                        .content(new Gson().toJson(usernameJsonObject))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.i(TAG,"用户名验证");
                                Log.i(TAG,response);
                            }
                        });
            } else {
                return;
            }
            boolean vaildResult = vaildEdittextString(phoneString,verificationString,usernameString,passwordString,confirmString,invitationString);
            if (vaildResult) {
                //注册接口
                JsonObject registerJsonObject = new JsonObject();
                registerJsonObject.addProperty("areaCode","86");
                registerJsonObject.addProperty("phone",phoneString);
                registerJsonObject.addProperty("captcha",verificationString);
                registerJsonObject.addProperty("username",usernameString);
                registerJsonObject.addProperty("password",passwordMD5);
                registerJsonObject.addProperty("confirmPass",confirmMD5);
                Gson gson = new Gson();
                Log.i(TAG,gson.toJson(registerJsonObject));
                OkHttpUtils
                        .postString()
                        .url(phoneRegister)
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
                                Log.i(TAG,"注册响应");
                                Log.i(TAG,response);
                            }
                        });

            }
        }
    }

    class MyTextChangedListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            

        }
    }


    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            //保证整数
            int time = (int) (Math.round((double) l / 1000) - 1);
            phoneGetCodeBtn.setText(String.valueOf(time)+"s后重新发送");
            //设置倒计时中的按钮外观
            phoneGetCodeBtn.setClickable(false);//倒计时过程中将按钮设置为不可点击
            phoneGetCodeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            phoneGetCodeBtn.setBackgroundColor(Color.parseColor("#c7c7c7"));
        }

        @Override
        public void onFinish() {
            phoneGetCodeBtn.setClickable(true);//倒计时过程中将按钮设置为不可点击
            phoneGetCodeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            phoneGetCodeBtn.setText("获取验证码");
            phoneGetCodeBtn.setBackgroundColor(getResources().getColor(R.color.primary));
        }
    }
}
