package com.shinechain.filecashcoin.view;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.geetest.sdk.GTCallBack;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shinechain.filecashcoin.R;
import com.shinechain.filecashcoin.bean.Result;
import com.shinechain.filecashcoin.bean.ValidateCode;
import com.shinechain.filecashcoin.http.JsonCallback;
import com.shinechain.filecashcoin.utils.MD5Utils;
import com.shinechain.filecashcoin.utils.PhoneEmailCheckUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();

    private static final String registerGeetest = "http://192.168.16.113:8081/v1/validate/registerGeetest";
    private static final String validateGeetest = "http://192.168.16.113:8081/v1/validate/validateGeetest";
    private static final String sendCaptcha = "http://192.168.16.113:8081/v1/users/sendCaptcha";
    private static final String checkPhone = "http://192.168.16.113:8081/v1/users/checkPhone";
    public static final String  checkEmail = "http://192.168.16.113:8081/v1/users/checkEmail";
    public static final String resetPassword = "http://192.168.16.113:8081/v1/users/resetPassword";

    private GT3GeetestUtilsBind gt3GeetestUtils;
    private TextInputEditText phone;
    private TextInputEditText verification;
    private TextInputEditText password;
    private TextInputEditText confirm;
    private AppCompatButton submitBtn;
    //默认手机
    private int resetFlag = 1;
    //发送验证码还是提交修改 默认发送验证码
    private int checkFlag = 1;
    private AppCompatButton verificationBtn;
    private Toolbar toolbar;
    private CardView cardView;
    private String userId;
    private TextView resetWayText;
    private ConstraintLayout phoneConstraint;
    private ConstraintLayout emailConstraint;
    private NiceSpinner niceSpinner;
    private List<String> areaList;
    private TextInputEditText email;
    private MyCountTimer countTimer;
    private String phoneString;
    private String emailString;
    private String verificationString;
    private String passwordString;
    private String confirmString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
        bindListener();
        initGeetTest();
    }

    private void initView() {
        phone = findViewById(R.id.reset_phone_edittext);
        email = findViewById(R.id.reset_email_edittext);
        verification = findViewById(R.id.reset_verification_edittext);
        verificationBtn = findViewById(R.id.reset_verification_button);
        password = findViewById(R.id.reset_password_edittext);
        confirm = findViewById(R.id.reset_confirm_edittext);
        submitBtn = findViewById(R.id.reset_submit_button);
        toolbar = findViewById(R.id.reset_toolbar);
        cardView = findViewById(R.id.reset_cardview);
        resetWayText = findViewById(R.id.reset_way_textview);
        //手机找回
        phoneConstraint = findViewById(R.id.reset_phone_contraintlayout);
        emailConstraint = findViewById(R.id.reset_email_contraintlayout);
        niceSpinner = findViewById(R.id.reset_spinner);
        //初始化niceSpinner的数据
        areaList = new LinkedList<>(Arrays.asList("+86(China)", "+852(Hong Kong)"));
        niceSpinner.attachDataSource(areaList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindListener() {
        //区分找回方式
        resetWayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resetFlag == 1) {
                    resetFlag = 2;
                    resetWayText.setText("手机找回");
                }else if(resetFlag == 2) {
                    resetFlag = 1;
                    resetWayText.setText("邮箱找回");
                }
                changeView();
            }
        });
        verificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificationBtn.setClickable(false);
                //获取手机或者邮箱
                String userText = "";
                if (resetFlag == 1) {
                    userText = phone.getText().toString().trim();
                }else if (resetFlag == 2) {
                    userText = email.getText().toString().trim();
                }
                if (TextUtils.isEmpty(userText)) {
                    backgroundThreadShortSnackbar(cardView,resetFlag ==1 ? "手机号码不能为空！" : "邮箱不能为空！");
                    verificationBtn.setClickable(true);
                    return;
                }
                //检测是否已经注册，未注册不发送发送验证码
                checkUserByNet(userText,1);

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gt3GeetestUtils != null) {
                    gt3GeetestUtils.gt3TestClose();
                }
                finish();
            }
        });

        //提交
        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //获取数据
                submitBtn.setClickable(false);
                phoneString = phone.getText().toString().trim();
                emailString = email.getText().toString().trim();
                verificationString = verification.getText().toString().trim();
                passwordString = password.getText().toString().trim();
                confirmString = confirm.getText().toString().trim();

                if (resetFlag == 1 && TextUtils.isEmpty(phoneString)) {
                    backgroundThreadShortSnackbar(cardView,"手机号不能为空");
                    submitBtn.setClickable(true);
                    return;
                }
                if (resetFlag == 2 && TextUtils.isEmpty(emailString)) {
                    backgroundThreadShortSnackbar(cardView,"邮箱不能为空");
                    submitBtn.setClickable(true);
                    return;
                }
                if (TextUtils.isEmpty(verificationString)) {
                    backgroundThreadShortSnackbar(cardView,"验证码不能为空");
                    submitBtn.setClickable(true);
                    return;
                }
                //检测数据
                if (TextUtils.isEmpty(passwordString)) {
                    backgroundThreadShortSnackbar(cardView,"密码不能为空");
                    submitBtn.setClickable(true);
                    return;
                }
                if (!passwordString.equals(confirmString)) {
                    backgroundThreadShortSnackbar(cardView,"两次密码不一致");
                    submitBtn.setClickable(true);
                    return;
                }
                if (!PhoneEmailCheckUtils.isPasswordLegal(passwordString)) {
                    backgroundThreadShortSnackbar(cardView,"密码格式不对");
                }
                if (resetFlag == 1) {
                    checkUserByNet(phoneString,2);
                }else {
                    checkUserByNet(emailString,2);
                }

            }
        });
    }

    /**
     * 改变显示的内容
     */
    private void changeView() {
        if (resetFlag == 1) {
            email.setText("");

            phoneConstraint.setVisibility(View.VISIBLE);
            emailConstraint.setVisibility(View.GONE);
        } else {
            phone.setText("");
            niceSpinner.setSelectedIndex(0);
            phoneConstraint.setVisibility(View.GONE);
            emailConstraint.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * @param userString 手机号码或者邮箱
     * @param flag 1.送验证码  2.提交
     */
    private void checkUserByNet(String userString, final int flag) {

        JsonObject jsonObject = new JsonObject();
        String checkUrl = checkPhone;
        //如果是手机
        if (resetFlag == 1) {
            //检验手机
            jsonObject.addProperty("phone",userString);
            checkUrl = checkPhone;
        } else {
            //检测邮箱
            jsonObject.addProperty("email",userString);
            checkUrl = checkEmail;
        }
        Log.i(TAG,new Gson().toJson(jsonObject));
        OkHttpUtils
                .postString()
                .url(checkUrl)
                .content(new Gson().toJson(jsonObject))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new JsonCallback<Result>(Result.class) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Result response, int id) {
                        if (new Integer(200).equals(response.getStatus()) && new Integer(10004).equals(response.getCode())) {
                            //表示用户已注册，可以发送验证码
                            //sendVerificationCode();
                            if (flag == 1) {
                                startCustomVerify();
                            }else if (flag == 2) {
                                //提交逻辑
                                submitLogic();
                            }
                        }else {
                            //未注册或者格式不对
                            verificationBtn.setClickable(true);
                            submitBtn.setClickable(true);
                            backgroundThreadShortSnackbar(cardView,"未注册或格式不对");
                        }
                    }
                });
    }

    private void submitLogic() {
        //密码MD5加密
        String passwordMD5 = MD5Utils.encode(passwordString);
        JsonObject submitJsonObject = new JsonObject();
        if (resetFlag == 1 ) {
            String areaString = areaList.get(niceSpinner.getSelectedIndex());
            String areaCode = areaString.substring(0, areaString.indexOf("("));
            submitJsonObject.addProperty("key",areaCode+phoneString);
        }else if (resetFlag == 2){
            submitJsonObject.addProperty("key",emailString);
        }
        submitJsonObject.addProperty("captcha",verificationString);
        submitJsonObject.addProperty("password",passwordMD5);
        OkHttpUtils
                .postString()
                .url(resetPassword)
                .content(new Gson().toJson(submitJsonObject))
                .mediaType(MediaType.parse("application/json;charset=utf-8"))
                .build()
                .execute(new JsonCallback<Result>(Result.class) {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(Result response, int id) {
                        if (new Integer(200).equals(response.getStatus()) && new Integer(10000).equals(response.getCode())) {
                            backgroundThreadShortSnackbar(cardView,response.getMsg());
                        }
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
                validateCode.setType(4);
                if (resetFlag == 1) {
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
                        .execute(new JsonCallback<Result>(Result.class) {

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Result response, int id) {
                                Log.i(TAG,response.getMsg());
                                if (new Integer(200).equals(response.getStatus()) && new Integer(10000).equals(response.getCode())) {
                                    gt3GeetestUtils.gt3TestFinish();
                                    backgroundThreadShortSnackbar(cardView,"验证码发送成功");
                                    //开始倒计时
                                    countTimer = new MyCountTimer(60*1000, 1000);
                                    countTimer.start();
                                } else {
                                    gt3GeetestUtils.gt3TestClose();
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
        gt3GeetestUtils.getGeetest(ResetPasswordActivity.this, registerGeetest, validateGeetest, null, new GT3GeetestBindListener() {

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


    private void initGeetTest() {
        gt3GeetestUtils = new GT3GeetestUtilsBind(ResetPasswordActivity.this);
        // 设置debug模式，开代理抓包可使用，默认关闭
        gt3GeetestUtils.setDebug(false);
        // 设置加载webview超时时间，单位毫秒，默认15000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3GeetestUtils.setTimeout(15000);
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3GeetestUtils.setWebviewTimeout(10000);
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            //保证整数
            int time = (int) (Math.round((double) l / 1000) - 1);
            verificationBtn.setText(String.valueOf(time)+"s后重新发送");
            //设置倒计时中的按钮外观
            verificationBtn.setClickable(false);//倒计时过程中将按钮设置为不可点击
            verificationBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            verificationBtn.setBackgroundColor(Color.parseColor("#c7c7c7"));
        }

        @Override
        public void onFinish() {
            verificationBtn.setClickable(true);//倒计时过程中将按钮设置为不可点击
            verificationBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            verificationBtn.setText("获取验证码");
            verificationBtn.setBackgroundColor(getResources().getColor(R.color.primary));
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
