package com.example.shoppingstore;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.shoppingstore.Bean.OrderMsg;
import com.example.shoppingstore.Okhttp.OkhttpHelper;
import com.example.shoppingstore.Okhttp.loadingSpotsDialog;
import com.example.shoppingstore.adapter.MyOrderAdapter;
import com.example.shoppingstore.application.MyApplication;
import com.example.shoppingstore.widget.MyDivider;
import com.example.shoppingstore.widget.PbToolbar;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridViewAdapter;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 博 on 2017/7/28.
 */

public class MyOrderActivity extends BaseActivity {

    private TabLayout show_order_tab ;
    private TabLayout.Tab showAllTab ;
    private TabLayout.Tab showSuccessTab ;
    private TabLayout.Tab showFailTab ;
    private TabLayout.Tab showWaitPayTab ;
    private RecyclerView recyclerView ;
    private MyOrderAdapter myAdapter ;
    private PbToolbar pbToolbar ;

    private OkhttpHelper okhttp = OkhttpHelper.getOkhttpHelper() ;

    private static final int SHOWALL = 2 ;
    private static final int SHOWSUCC = 1 ;
    private static final int SHOWWAIT = 0 ;
    private static final int SHOWFAIL = -1 ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.myorder_view);

        show_order_tab = (TabLayout) findViewById(R.id.show_order_tab) ;
        recyclerView = (RecyclerView) findViewById(R.id.recycleView) ;
        pbToolbar = (PbToolbar) findViewById(R.id.toolBar) ;

        initTabLayout();
        addListener() ;
    }

    private void addListener() {

        pbToolbar.setLeftButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initTabLayout() {

        showAllTab = show_order_tab.newTab().setText("全部订单").setTag(SHOWALL) ;
        showSuccessTab = show_order_tab.newTab().setText("支付成功").setTag(SHOWSUCC) ;
        showWaitPayTab = show_order_tab.newTab().setText("等待支付").setTag(SHOWWAIT) ;
        showFailTab = show_order_tab.newTab().setText("支付失败").setTag(SHOWFAIL) ;

        show_order_tab.addTab(showAllTab);
        show_order_tab.addTab(showSuccessTab);
        show_order_tab.addTab(showWaitPayTab);
        show_order_tab.addTab(showFailTab);

        getData(SHOWALL) ;

        show_order_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getData((int)tab.getTag()) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private void getData(int status) {

        Map<String , String> params = new HashMap<String , String>() ;

        params.put("user_id" , MyApplication.getInstance().getUser().getId()+"") ;
        params.put("status" , status + "" ) ;

        okhttp.doGet(Contents.API.GET_ORDER_LIST, new loadingSpotsDialog<List<OrderMsg>>(this) {
            @Override
            public void onErroe(Response response, int responseCode, Exception e) throws IOException {
                this.closeSpotsDialog();
            }

            @Override
            public void callBackSucces(Response response, List<OrderMsg> orderMsgs) throws IOException {
                this.closeSpotsDialog();

                showOrderData(orderMsgs) ;


            }

        }, params);

    }

    private void showOrderData(List<OrderMsg> orderMsgs) {

        if (orderMsgs != null && orderMsgs.size() > 0 ){

            if (myAdapter == null ){

                myAdapter = new MyOrderAdapter(this , orderMsgs ) ;
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.addItemDecoration(new MyDivider());

            }else{
                myAdapter.cleanData();
                myAdapter.addData(orderMsgs);
            }
        }else if (myAdapter != null){
            myAdapter.cleanData();
        }

    }


}
