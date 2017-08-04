package com.example.shoppingstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingstore.Bean.User;
import com.example.shoppingstore.MyView.MyEditText;
import com.example.shoppingstore.Okhttp.OkhttpHelper;
import com.example.shoppingstore.Okhttp.loadingSpotsDialog;
import com.example.shoppingstore.application.MyApplication;
import com.example.shoppingstore.msg.LoginRespMsg;
import com.example.shoppingstore.utils.DESUtil;
import com.example.shoppingstore.widget.PbToolbar;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 博 on 2017/7/21.
 */

public class UserLoginActivity extends BaseActivity {

    private PbToolbar pbToolbar ;
    private Button login_button ;
    private TextView forget_button ;
    private TextView register_button ;
    private MyEditText phone ;
    private MyEditText password ;
    private OkhttpHelper okhttpHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login_view);

        pbToolbar = (PbToolbar) findViewById(R.id.toolBar) ;
        login_button = (Button) findViewById(R.id.button_log) ;
        forget_button = (TextView) findViewById(R.id.forgetPass) ;
        register_button = (TextView) findViewById(R.id.register) ;
        phone = (MyEditText) findViewById(R.id.userId);
        password = (MyEditText) findViewById(R.id.password) ;
        okhttpHelper = OkhttpHelper.getOkhttpHelper() ;

        initView() ;
        initPbtoolbar() ;
        addListener() ;

    }

    private void initView(){

        phone.setInputType(InputType.TYPE_CLASS_NUMBER); //输入类型
        phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)}); //最大输入长度
        password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框

        Text() ;
    }

    private void addListener(){

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(v) ;
            }
        });

        forget_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("----" , "register------------------------------") ;
                UserLoginActivity.this.startActivity(new Intent(UserLoginActivity.this , RegisterActivity.class));
            }
        });

    }

    private void closeKeyMode(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(),0);
    }


    private void initPbtoolbar(){

        pbToolbar.setLeftButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyMode();
                finish();
            }
        });
    }

    private void doLogin( View v ){

        String phoneNum = phone.getText().toString() ;

        if ( phoneNum == null ){
            Toast.makeText( v.getContext() , "请输入手机号码" , Toast.LENGTH_SHORT).show();
            return;
        }else if(phoneNum.length() != 11){
            Toast.makeText( v.getContext() , "请输入正确的手机号码" , Toast.LENGTH_SHORT).show();
            return;
        }

        String pwd = password.getText().toString() ;
        if (pwd == null){
            Toast.makeText( v.getContext() , "请输入密码" , Toast.LENGTH_SHORT).show() ;
            return;
        }

        String uri = Contents.API.LOGIN ;
        Map< String , String > params = new HashMap<String, String>() ;
        params.put("phone" , phoneNum ) ;
        params.put("password" , DESUtil.encode(Contents.DES_KEY , pwd)) ;

        okhttpHelper.doPost(uri, new loadingSpotsDialog<LoginRespMsg<User>>(UserLoginActivity.this ) {
            @Override
            public void onErroe(Response response, int responseCode, Exception e) throws IOException {
                this.closeSpotsDialog();
            }

            @Override
            public void callBackSucces(Response response, LoginRespMsg<User> userLoginRespMsg) throws IOException {
                this.closeSpotsDialog();

                if(userLoginRespMsg.getStatus() == 1){

                    MyApplication.getInstance().putUser(userLoginRespMsg.getData() , userLoginRespMsg.getTocken());
                    closeKeyMode() ;

                    if (null == MyApplication.getInstance().getIntent()){
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        MyApplication.jumpToTargetoActivity(UserLoginActivity.this);
                        finish();
                    }

                }else {
                    showLoginErrorMsg() ;
                    phone.setText("");
                    password.setText("");
                }
            }
        }, params);
    }

    public void Text(){
        phone.setText("15829238397");
        password.setText("bb001314");
    }

    private void showLoginErrorMsg(){
        closeKeyMode();
        Toast.makeText(this, "密码错误" , Toast.LENGTH_LONG).show();
    }

}
