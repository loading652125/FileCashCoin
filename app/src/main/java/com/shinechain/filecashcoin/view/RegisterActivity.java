package com.shinechain.filecashcoin.view;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.geetest.sdk.GTCallBack;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shinechain.filecashcoin.R;
import com.shinechain.filecashcoin.bean.CommonResult;
import com.shinechain.filecashcoin.http.JsonCallback;
import com.shinechain.filecashcoin.bean.Result;
import com.shinechain.filecashcoin.bean.ValidateCode;
import com.shinechain.filecashcoin.utils.MD5Utils;
import com.shinechain.filecashcoin.utils.PhoneEmailCheckUtils;
import com.shinechain.filecashcoin.utils.SPUtils;
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

import okhttp3.Call;
import okhttp3.MediaType;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private static final String registerGeetest = "http://192.168.16.113:8081/v1/validate/registerGeetest";
    private static final String validateGeetest = "http://192.168.16.113:8081/v1/validate/validateGeetest";
    private static final String sendCaptcha = "http://192.168.16.113:8081/v1/users/sendCaptcha";
    private static final String checkPhone = "http://192.168.16.113:8081/v1/users/checkPhone";
    private static final String checkUsername = "http://192.168.16.113:8081/v1/users/checkUsername";
    private static final String phoneRegister = "http://192.168.16.113:8081/v1/users/phoneRegister";
    private static final String emailRegister = "http://192.168.16.113:8081/v1/users/emailRegister";
    private static final String loginCallback = "http://192.168.16.113:8080/v1/users/loginCallback";

    private GT3GeetestUtilsBind gt3GeetestUtils;
    private TextView registerWayText;
    private AppCompatButton phoneGetCodeBtn;
    private TextInputEditText phone;
    private TextInputEditText verificationCode;
    private TextInputEditText password;
    private TextInputEditText comfirmPassword;
    private AppCompatButton registerNow;
    private TextInputEditText username;
    private NiceSpinner niceSpinner;
    private TextInputLayout phoneLayout;
    private List<String> areaList;
    //发送验证相关
    private String userId = "";
    private MyCountTimer countTimer;
    private String phoneString;
    private String verificationString;
    private String usernameString;
    private String passwordString;
    private String confirmString;

    //注册方式1手机注册,2邮箱注册
    private int registerFlag = 1;
    private ConstraintLayout phoneConstainLayout;
    private ConstraintLayout emialConstrainLayout;
    private TextInputEditText email;
    private String emailString;
    private CardView cardView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        bindListener();
        initGeetTest();
    }

    private void initView() {
        registerWayText = findViewById(R.id.register_way_textview);
        //后面根据地区代码来生成
        niceSpinner = findViewById(R.id.spinner);
        areaList = new LinkedList<>(Arrays.asList("+86(China)", "+852(Hong Kong)"));
        niceSpinner.attachDataSource(areaList);
        phoneGetCodeBtn = findViewById(R.id.register_get_code_button);
        //获取
        phone = findViewById(R.id.register_phone_edittext);
        verificationCode = findViewById(R.id.register_phone_verification_edittext);
        username = findViewById(R.id.register_username_edittext);
        password = findViewById(R.id.register_password_edittext);
        comfirmPassword = findViewById(R.id.register_confirm_edittext);
        registerNow = findViewById(R.id.register_register_button);
        phoneLayout = findViewById(R.id.register_phone_phonelayout);
        phoneConstainLayout = findViewById(R.id.register_phone_constrainlayout);
        emialConstrainLayout = findViewById(R.id.register_email_constrainlayout);
        email = findViewById(R.id.register_email_email_edittext);
        cardView = findViewById(R.id.register_cardview);
        toolbar = findViewById(R.id.reset_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void bindListener() {
        //区分注册方式
        registerWayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registerFlag == 1) {
                    registerFlag =2;
                    //当前是手机注册
                    registerWayText.setText("手机注册");
                }else if (registerFlag == 2) {
                    //当前是邮箱注册
                    registerFlag = 1;
                    registerWayText.setText("邮箱注册");
                }
                changeView();
            }
        });

        phone.addTextChangedListener(new PhoneTextChangedListener());

        phoneGetCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是手机还是邮箱
                phoneGetCodeBtn.setClickable(false);
                boolean isSendCode = false;
                //手机注册，不为空，没有错误提示
                if ( registerFlag == 1) {
                    if ( phoneLayout.getError() == null && !TextUtils.isEmpty(phone.getText().toString().trim()) ){
                        isSendCode = true;
                    }else {
                        backgroundThreadShortSnackbar(cardView,"手机号码有误，请检查！");
                        phoneGetCodeBtn.setClickable(true);
                        return;
                    }
                } else if (registerFlag == 2) {
                    String emailString = email.getText().toString().trim();
                    if ( !TextUtils.isEmpty(emailString) && PhoneEmailCheckUtils.isEmailLegal(emailString)) {
                        isSendCode = true;
                    } else {
                        backgroundThreadShortSnackbar(cardView,"邮箱有误，请检查！");
                        phoneGetCodeBtn.setClickable(true);
                        return;
                    }
                }
                if(isSendCode){
                    //开始验证
                    startCustomVerify();
                }
            }
        });

        registerNow.setOnClickListener(new RegisterClickListener());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gt3GeetestUtils != null) {
                    gt3GeetestUtils.gt3TestClose();
                }
                finish();
            }
        });

    }

    private void initGeetTest() {
        gt3GeetestUtils = new GT3GeetestUtilsBind(RegisterActivity.this);
        // 设置debug模式，开代理抓包可使用，默认关闭
        gt3GeetestUtils.setDebug(false);
        // 设置加载webview超时时间，单位毫秒，默认15000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3GeetestUtils.setTimeout(15000);
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3GeetestUtils.setWebviewTimeout(10000);
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
                        e.printStackTrace();
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
        gt3GeetestUtils.getGeetest(RegisterActivity.this, registerGeetest, validateGeetest, null, new GT3GeetestBindListener() {

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
                // TODO 这里使用提示
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
                if (registerFlag == 1) {
                    //拿取手机号码
                    String area = areaList.get(niceSpinner.getSelectedIndex());
                    String areaCode = area.substring(0,area.indexOf("("));
                    String currentPhone = areaCode + phone.getText().toString().trim();
                    validateCode.setTo(currentPhone);
                } else {
                    //拿取邮箱
                    String currentEmail = email.getText().toString().trim();
                    validateCode.setTo(currentEmail);
                }
                //极验数据
                validateCode.setGeetestChallenge(codeJsonObject.getString("geetest_challenge"));
                validateCode.setGeetestValidate(codeJsonObject.getString("geetest_validate"));
                validateCode.setGeetestSeccode(codeJsonObject.getString("geetest_seccode"));
                //TODO 这里需要拿去App当时运行的语言环境
                validateCode.setLang(1);
                //这里去那设置
                validateCode.setGeetestUserId(userId);
                validateCode.setGtServerStatus("1");
                Log.i(TAG,new Gson().toJson(validateCode));
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
                                    //改变按钮
                                    countTimer = new MyCountTimer(60*1000, 1000);
                                    countTimer.start();
                                    //发送验证码成功
                                    backgroundThreadShortSnackbar(cardView,"验证码发送成功");
                                }
                            }
                        });
            }
            else {
                Log.i(TAG,"无法发送验证码请求");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countTimer != null){
            countTimer.cancel();
        }
        if (gt3GeetestUtils != null) {
            gt3GeetestUtils.gt3TestClose();
        }
    }

    class RegisterClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            phoneString = phone.getText().toString().trim();
            verificationString = verificationCode.getText().toString().trim();
            usernameString = username.getText().toString().trim();
            passwordString = password.getText().toString().trim();
            confirmString = comfirmPassword.getText().toString().trim();
            emailString = email.getText().toString().trim();
            //验证手机,有错误或者为空时，不做其他操作
            if ( registerFlag == 1 && (phoneLayout.getError() != null || TextUtils.isEmpty(phoneString)) ) {
                return;
            }else if ( registerFlag == 2 && (TextUtils.isEmpty(emailString) || !PhoneEmailCheckUtils.isEmailLegal(emailString)) ){
                return;
            }
            //检查密码
            if (passwordString.equals(confirmString)) {
                boolean isPasswordLegal = PhoneEmailCheckUtils.isPasswordLegal(passwordString);
                if (!isPasswordLegal) {
                    backgroundThreadShortSnackbar(cardView,"密码不符合规范");
                    return;
                }
            }else {
                backgroundThreadShortSnackbar(cardView,"密码不一致");
                return;
            }
            //先做密码检测，再执行异步检查用户名
            checkUsernameLogic(usernameString);

        }
    }

    class PhoneTextChangedListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.i(TAG,editable.toString()+"触发了");
            String phoneNumber = phone.getText().toString().trim();
            //本地判断
            boolean isPhoneLegal = PhoneEmailCheckUtils.isPhoneLegal(phoneNumber);
            if ( !isPhoneLegal ) {
                phoneLayout.setError("无效手机号码");
            }else {
                //服务器检测是不是已经被注册
                checkPhoneLogic(phoneNumber);
            }
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

    //检查手机号码
    private void checkPhoneLogic(String phoneString) {
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

    private void checkUsernameLogic(String usernameString) {
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
                    .execute(new JsonCallback<Result>(Result.class) {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }
                        @Override
                        public void onResponse(Result response, int id) {
                            Log.i(TAG,"检测用户名 status:"+response.getStatus()+" code:"+response.getCode());
                            if ( new Integer(200).equals(response.getStatus()) && new Integer(10000).equals(response.getCode()) ) {
                                registerLogic();
                                Log.i(TAG,response.getMsg());
                            } else {
                                if (!TextUtils.isEmpty(response.getMsg())) {
                                    //表示用户名不对
                                    backgroundThreadShortSnackbar(cardView,response.getMsg());
                                    return;
                                }
                            }
                        }
                    });
        } else {
            return;
        }
    }

    private void registerLogic() {

        //密码MD5加密
        String passwordMD5 = MD5Utils.encode(passwordString);
        //String confirmMD5 = MD5Utils.encode(confirmString);
        //注册接口
        JsonObject registerJsonObject = new JsonObject();
        //注册链接区别
        String registerUrl = phoneRegister;
        if (registerFlag == 1) {
            //拿取地区区号,拿取85,852
            String currentAreaString = areaList.get(niceSpinner.getSelectedIndex());
            String areaCode = currentAreaString.substring(1,currentAreaString.indexOf("("));
            registerJsonObject.addProperty("areaCode",areaCode);
            registerJsonObject.addProperty("phone", phoneString);
            registerUrl = phoneRegister;
        } else {
            registerJsonObject.addProperty("email",emailString);
            registerUrl = emailRegister;
        }
        registerJsonObject.addProperty("captcha", verificationString);
        registerJsonObject.addProperty("username", usernameString);
        registerJsonObject.addProperty("password",passwordMD5);
        registerJsonObject.addProperty("confirmPass",passwordMD5);
        Log.i(TAG,new Gson().toJson(registerJsonObject));

        OkHttpUtils
                .postString()
                .url(registerUrl)
                .content(new Gson().toJson(registerJsonObject))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new JsonCallback<CommonResult>(CommonResult.class) {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(CommonResult response, int id) {
                        if ( new Integer(200).equals(response.getStatus()) && new Integer(10000).equals(response.getCode()) ) {
                            String uuid = response.getData();
                            Log.i(TAG,uuid);
                            //获取保存JWT
                            getJWT(uuid);
                        } else {

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
                                SPUtils.put(RegisterActivity.this,"jwt",data);
                                Log.i(TAG,"保存登录信息成功");
                                Log.i(TAG,SPUtils.get(RegisterActivity.this, "jwt", ""));
                                backgroundThreadShortSnackbar(cardView,"注册成功");
                                // TODO 延时跳转主页面
                            }else {

                            }
                        } else {
                            if (response.getDevIfno() != null){
                                backgroundThreadShortSnackbar(cardView,response.getDevIfno());
                            }else {
                                backgroundThreadShortSnackbar(cardView,response.getMsg());
                            }
                        }
                    }
                });
    }

    private void changeView(){
        if (registerFlag == 1) {
            //清空内容
            email.setText("");
            //手机方式，把邮箱隐藏，验证码框信息为输入验证码
            phoneConstainLayout.setVisibility(View.VISIBLE);
            emialConstrainLayout.setVisibility(View.GONE);
            //verificationCode.setHint("输入验证码");
        } else {
            //把spinner和对应图标隐藏
            phone.setText("");
            niceSpinner.setSelectedIndex(0);
            phoneLayout.setError(null);
            phoneLayout.setErrorEnabled(false);
            phoneConstainLayout.setVisibility(View.GONE);
            emialConstrainLayout.setVisibility(View.VISIBLE);
            //发送邮箱验证码
            //verificationCode.setHint("输入邮箱验证码");
        }
    }

    private void backgroundThreadShortSnackbar(final View view ,final String msg){
        if (view != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }
}
