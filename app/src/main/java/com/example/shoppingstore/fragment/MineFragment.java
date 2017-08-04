package com.example.shoppingstore.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.shoppingstore.Bean.User;
import com.example.shoppingstore.MyOrderActivity;
import com.example.shoppingstore.R;
import com.example.shoppingstore.ShowConsigneeAdrActivity;
import com.example.shoppingstore.UserLoginActivity;
import com.example.shoppingstore.application.MyApplication;
import com.example.shoppingstore.widget.PbToolbar;

/**
 * Created by 博 on 2017/6/23.
 */

public class MineFragment extends BaseFragment {

    private PbToolbar pbToolbar ;
    private View mView ;
    private Button loginOut ;
    private TextView my_consignee ;
    private TextView my_favorite ;
    private TextView my_orderList ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView =  inflater.inflate(R.layout.mine_fragment , container , false ) ;
        pbToolbar = (PbToolbar) mView.findViewById(R.id.toolBar) ;
        loginOut = (Button) mView.findViewById(R.id.loginOut) ;
        my_consignee = (TextView)mView.findViewById(R.id.my_consignee) ;
        my_favorite = (TextView) mView.findViewById(R.id.my_favorite);
        my_orderList = (TextView) mView.findViewById(R.id.my_list) ;

        initPbtoolbar() ;
        addListener() ;
        return mView ;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initPbtoolbar(){

        pbToolbar.setUserPhotoClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity() , UserLoginActivity.class) ;
                startActivityWithLogin(intent , true , MyApplication.START_FOR_RESULT);
            }
        });

        User user = MyApplication.getInstance().getUser() ;
        showUser(user) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        User user = MyApplication.getInstance().getUser() ;
        showUser(user) ;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showUser(User user){
        if (user != null){
            pbToolbar.setUserPhotoIcon(this.getActivity() , user.getLogo_url() , R.drawable.default_head );
            pbToolbar.setUserNameText(user.getUsername());
            loginOut.setVisibility(View.VISIBLE);
            pbToolbar.setUserClickable(false);

        }else {

            pbToolbar.setUserNameText("点击登录");
            pbToolbar.setUserPhotoIcon(getContext() , R.drawable.default_head);
            loginOut.setVisibility(View.GONE);
            pbToolbar.setUserClickable(true);
        }
    }

    private void addListener(){

        my_orderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithLogin(new Intent(getContext() , MyOrderActivity.class) , true , MyApplication.START_NO_RESULT);
            }
        });

        loginOut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                loginOut() ;
            }
        });

        my_consignee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithLogin(new Intent(getContext() , ShowConsigneeAdrActivity.class ) , true , MyApplication.START_FOR_RESULT );
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loginOut(){

        MyApplication.getInstance().clearUser();
        User user = MyApplication.getInstance().getUser() ;
        showUser(user) ;
    }


}
