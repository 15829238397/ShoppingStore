package com.example.shoppingstore.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.shoppingstore.Contents;
import com.example.shoppingstore.UserLoginActivity;
import com.example.shoppingstore.application.MyApplication;

/**
 * Created by 博 on 2017/7/24.
 */

public class BaseFragment extends Fragment {

    protected void startActivityWithLogin(Intent intent , boolean isNeedLogin , int startIntentStype){

        if (isNeedLogin){

            if(MyApplication.getInstance().getUser() == null){

                Intent intent1 = new Intent(getActivity() , UserLoginActivity.class) ;

                if (MyApplication.START_FOR_RESULT == startIntentStype){
                    startActivityForResult(intent1 , Contents.REQUEST_CODE);
                }else if(MyApplication.START_NO_RESULT == startIntentStype){
                    MyApplication.getInstance().setIntent(intent);
                    startActivity(intent1);
                }

            }else {
                getActivity().startActivity(intent);
            }
        }else {
            getActivity().startActivity(intent);
        }

    }

}
