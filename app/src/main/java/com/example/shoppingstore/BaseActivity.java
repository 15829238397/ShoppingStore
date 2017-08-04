package com.example.shoppingstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.example.shoppingstore.application.MyApplication;

/**
 * Created by 博 on 2017/7/24.
 */

public class BaseActivity extends AppCompatActivity {

    protected void startActivityWithLogin(Intent intent , boolean isNeedLogin , int startIntentStype){

        if (isNeedLogin){

            if(MyApplication.getInstance().getUser() == null){

                Intent intent1 = new Intent(this , UserLoginActivity.class) ;

                if (MyApplication.START_FOR_RESULT == startIntentStype){
                    startActivityForResult(intent1 , Contents.REQUEST_CODE);
                }else if(MyApplication.START_NO_RESULT == startIntentStype){
                    MyApplication.getInstance().setIntent(intent);
                    startActivity(intent1);
                }

            }else {
                this.startActivity(intent);
            }
        }else {
            this.startActivity(intent);
        }

    }
}
