package com.example.shoppingstore;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingstore.MyView.MyEditText;
import com.example.shoppingstore.application.MyApplication;
import com.example.shoppingstore.widget.PbToolbar;
import com.mob.MobSDK;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.IdentifyNumPage;
import cn.smssdk.gui.RegisterPage;
import cn.smssdk.gui.SmartVerifyPage;
import cn.smssdk.utils.SMSLog;

/**
 * Created by 博 on 2017/7/24.
 */

public class RegisterActivity extends BaseActivity {

    private TextView country ;
    private PbToolbar pbToolbar ;
    private TextView countryNum ;
    private MyEditText phone ;
    private MyEditText password ;
    private SMSEvenHandler smsEvenHandler ;
    private String countryCode ;
    private String phoneCode ;
    private String passWord ;

    private static final String DEFAUT_COUNTRY_CODE = "42" ;
    public static final String PHONE = "phone" ;
    public static final String PASSWORD = "passWord" ;
    public static final String COUNTRY_CODE = "countryCode" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.register1_view);

        country = (TextView) findViewById(R.id.country) ;
        pbToolbar = (PbToolbar) findViewById(R.id.toolBar) ;
        countryNum = (TextView) findViewById(R.id.countryNum) ;
        phone = (MyEditText) findViewById(R.id.phone) ;
        password = (MyEditText) findViewById(R.id.password) ;

        phone.setInputType(InputType.TYPE_CLASS_NUMBER); //输入类型
        phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)}); //最大输入长度

        smsEvenHandler = new SMSEvenHandler() ;
        SMSSDK.registerEventHandler(smsEvenHandler);


        String[] countryMsg = SMSSDK.getCountry(DEFAUT_COUNTRY_CODE) ;
        if(countryMsg != null){
            country.setText(countryMsg[0]);
            countryNum.setText("+"+countryMsg[1]);
        }

        addListener() ;

    }



    private void addListener(){

        /**
         * 返回上一页
         */
        pbToolbar.setLeftButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * 点击下一步，进入服务器获取信息，发送验证码
         */
        pbToolbar.setRightButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
    }

    private void JumpToRegister2(){
        Intent intent = new Intent(this , Register2Activity.class) ;

        intent.putExtra(PHONE , phoneCode) ;
        intent.putExtra(PASSWORD , passWord) ;
        intent.putExtra(COUNTRY_CODE , countryCode) ;

        startActivityWithLogin(intent , false , MyApplication.START_NO_RESULT);
    }

    private boolean getCode(){

        phoneCode = phone.getText().toString().trim() ;
        countryCode = countryNum.getText().toString().trim() ;
        passWord = password.getText().toString().trim()  ;

        if(!requestIdentifyCode(phoneCode , countryCode ,passWord)){
            return false;
        }

        SMSSDK.getVerificationCode(countryCode , phoneCode );
        return true ;

    }

    private boolean requestIdentifyCode(String phoneCode , String countryCode ,String passWord ){

        if (countryCode.startsWith("+")){
            countryCode = countryCode.substring(1) ;
        }

        if (TextUtils.isEmpty(phoneCode)){
            Toast.makeText(this , "请输入手机号码" , Toast.LENGTH_SHORT).show();
            return false;
        }

        if (countryCode == "86"){
            if (phoneCode.length() !=11 ){
                Toast.makeText(this , "手机号码长度错误" , Toast.LENGTH_SHORT).show();
                return false ;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}" ;
        Pattern p = Pattern.compile(rule) ;
        Matcher m = p.matcher(phoneCode) ;

        if (!m.matches()){
            Toast.makeText(this , "手机号码格式错误" , Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(passWord) || passWord.length()<6 ){
            Toast.makeText(this , "请填写超过6位即6位以上的密码" , Toast.LENGTH_SHORT).show();
            return false;
        }

        return true ;
    }

    private void afterVerificationCodeRequested(boolean smart) {

        Toast.makeText(this ,smart ? "获取验证码失败" : "获取验证码成功" , Toast.LENGTH_SHORT).show();
        if(smart){
            JumpToRegister2() ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(smsEvenHandler);
    }

    class SMSEvenHandler extends EventHandler{
        @Override
        public void afterEvent(final int event, final int result, final Object data) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (data instanceof Throwable) {
                            Throwable throwable = (Throwable)data;
                            String msg = throwable.getMessage();
                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                            return ;
                        }else {

                        if (result == SMSSDK.RESULT_COMPLETE){
                            if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){

                            }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){

                                //请求验证码之后，进行操作
                                afterVerificationCodeRequested((Boolean) data);

                            }else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                            }
                        }}
                    }
                });
            }
        }
    }

