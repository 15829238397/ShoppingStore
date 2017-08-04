package com.example.shoppingstore;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shoppingstore.Bean.ShoppingCart;
import com.example.shoppingstore.Bean.SubmitOrderMsg;
import com.example.shoppingstore.Okhttp.OkhttpHelper;
import com.example.shoppingstore.Okhttp.loadingSpotsDialog;
import com.example.shoppingstore.adapter.OrderWareMsgAdapter;
import com.example.shoppingstore.application.MyApplication;
import com.example.shoppingstore.fragment.ShoppingBikeFragment;
import com.example.shoppingstore.utils.CartProvider;
import com.example.shoppingstore.utils.JsonUtil;
import com.example.shoppingstore.widget.MyDivider;
import com.example.shoppingstore.widget.PbToolbar;
import com.google.gson.reflect.TypeToken;
import com.pingplusplus.android.PaymentActivity;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 博 on 2017/7/24.
 */

public class NewOrderActivity extends BaseActivity {

    private RecyclerView recyclerView ;
    private OrderWareMsgAdapter orderWareMsgAdapter ;
    private CartProvider cartProvider ;
    private List<ShoppingCart> shoppingCarts ;
    private float sumPrice ;
    private TextView sumPrices ;
    private PbToolbar pbToolbar ;
    private RadioButton alipayButton ;
    private RadioButton wechatButton ;
    private RadioButton dbButton ;
    private RelativeLayout alipay ;
    private RelativeLayout wechat ;
    private ImageButton consigneeMsg ;
    private RelativeLayout db ;
    private List<OrderItem>  OrderItems;
    private String orderNum ;
    private static OkhttpHelper okhttpHelper = OkhttpHelper.getOkhttpHelper();
    private Button submit ;
    private TextView userMsg ;
    private TextView address ;
    private ImageButton toOrderButton ;

    private static final int CHECK_ALIPAY = 0 ;
    private static final int CHECK_WECHECK = 1 ;
    private static final int CHECK_DB = 2 ;

    public static int ORDER_SUCCESS = 1 ;
    public static int ORDER_FAIL = -1 ;
    public static int ORDER_GONE = -2 ;


    private static int radio_check = CHECK_ALIPAY ;
    private static String pay_channel  ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.new_order_view);

        pbToolbar = (PbToolbar) findViewById(R.id.toolBar) ;
        recyclerView = (RecyclerView) findViewById(R.id.wareMsg) ;
        sumPrices = (TextView) findViewById(R.id.warePrice) ;
        alipayButton = (RadioButton) findViewById(R.id.checkbox_alipay) ;
        wechatButton = (RadioButton) findViewById(R.id.checkbox_wechat) ;
        dbButton = (RadioButton) findViewById(R.id.check_bd) ;
        alipay = (RelativeLayout) findViewById(R.id.Alipay) ;
        wechat = (RelativeLayout) findViewById(R.id.wechat) ;
        db = (RelativeLayout) findViewById(R.id.bd) ;
        submit = (Button) findViewById(R.id.btn_submit) ;
        consigneeMsg = (ImageButton) findViewById(R.id.consigneeMsg) ;
        userMsg = (TextView) findViewById(R.id.userMsg) ;
        address = (TextView) findViewById(R.id.address) ;
        toOrderButton = (ImageButton) findViewById(R.id.toOrderButton) ;

        cartProvider = CartProvider.getCartProvider(this) ;

        initView() ;
    }

    private void initView(){

        if(MyApplication.getInstance().getUser().getDefauteConsigen() == null){
            userMsg.setText("点击右侧箭头添加收货人信息");
            address.setText("");
        }else {
            userMsg.setText(MyApplication.getInstance().getUser().getDefauteConsigen().getConsignee()
                            + "(" +
                            MyApplication.getInstance().getUser().getDefauteConsigen().getPhone().substring(0 ,3 )
                            +  "*****"+
                            MyApplication.getInstance().getUser().getDefauteConsigen().getPhone().substring(8)
                            + ")");
            address.setText(MyApplication.getInstance().getUser().getDefauteConsigen().getAddr());
        }

        getData() ;
        initRecyclerView();
        initRadioButton() ;
        sumPrices.setText("￥" + sumPrice);

        addListener() ;
    }

    private void submitOrder(){

        submit.setClickable(false);

        OrderItems = new ArrayList<OrderItem>() ;
        for (int i = 0; i <shoppingCarts.size() ; i++) {
            OrderItem o = new OrderItem((int) shoppingCarts.get(i).getId(),shoppingCarts.get(i).getCount() ) ;
            OrderItems.add(o) ;
        }

        Map<String , String> params = new HashMap<String , String>() ;
        params.put("user_id" , MyApplication.getInstance().getUser().getId()+"") ;
        params.put("item_json" , JsonUtil.toJSON(OrderItems)) ;
        params.put("amount" , ((int)sumPrice)+"") ;
        params.put("addr_id" , "1") ;
        params.put("pay_channel" , pay_channel ) ;

        okhttpHelper.doPost(Contents.API.SUBMIT_ORDER, new loadingSpotsDialog<SubmitOrderMsg>(this) {
            @Override
            public void onErroe(Response response, int responseCode, Exception e) throws IOException {
                this.closeSpotsDialog();
                submit.setClickable(true);
            }

            @Override
            public void callBackSucces(Response response, SubmitOrderMsg submitOrderMsg) throws IOException {
                this.closeSpotsDialog();

                submit.setClickable(true);
                if(submitOrderMsg.getStatus() == 1) {
                    orderNum = submitOrderMsg.getData().getOrderNum() ;
                    Object charge = submitOrderMsg.getData().getCharge() ;

                    openPaymentAcitivity(JsonUtil.toJSON(charge)) ;
                    Log.d("----" , "----------------提交的订单--------------"+orderNum + "J"+JsonUtil.toJSON(charge)) ;
                }
            }

            @Override
            public void onTokenError(Response response, int responseCode) {
                super.onTokenError(response, responseCode);
                this.closeSpotsDialog();
                submit.setClickable(true);
            }
        } , params);
    }

    private void openPaymentAcitivity(String charge){
        Intent intent = new Intent();
        String packageName = getPackageName() ;
        ComponentName componentName = new ComponentName(packageName , packageName + ".wxapi.WXPayEntryActivity") ;
        intent.setComponent(componentName) ;
        intent.putExtra(PaymentActivity.EXTRA_CHARGE , charge) ;
        startActivityForResult(intent , Contents.REQUEST_ORDER_CODE);
        Log.d("----" , "-------------------------------------打开了支付页面") ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        Log.d("----" , "-------------------------------------支付返回requestCode" + requestCode) ;
//        Log.d("----" , "-------------------------------------支付返回resultCode" + resultCode) ;
//        Log.d("----" , "-------------------------------------支付返回REQUEST_ORDER_CODE" + Contents.REQUEST_ORDER_CODE) ;
//        Log.d("----" , "-------------------------------------支付返回RESULT_OK" + Activity.RESULT_OK) ;
//        Log.d("----" , "-------------------------------------支付返回data.getExtras()." +  data.getExtras().getString("pay_result")) ;

        if (requestCode == Contents.REQUEST_ORDER_CODE){
            if (resultCode == Activity.RESULT_OK){

                String result = data.getExtras().getString("pay_result") ;
                Log.d("----" , "-------------------------------------支付返回" + result) ;
                if (result.equals("success"))
                    changeOrderStatus(1);
                else if (result.equals("fail"))
                    changeOrderStatus(-1);
                else if (result.equals("cancel"))
                    changeOrderStatus(-2);
                else
                    changeOrderStatus(0);

            }
        }else if (requestCode == Contents.REQUEST_ORDER_CONSIGNEE){

            if(MyApplication.getInstance().getUser().getDefauteConsigen() == null){
                userMsg.setText("点击右侧箭头添加收货人信息");
                address.setText("");
            }else {
                userMsg.setText(MyApplication.getInstance().getUser().getDefauteConsigen().getConsignee()
                        + "(" +
                        MyApplication.getInstance().getUser().getDefauteConsigen().getPhone().substring(0 ,2 )
                        +  "*****"+
                        MyApplication.getInstance().getUser().getDefauteConsigen().getPhone().substring(8)
                        + ")");
                address.setText(MyApplication.getInstance().getUser().getDefauteConsigen().getAddr());
            }

        }
    }

    private void changeOrderStatus(int status){

        Log.d("----" , "-------------------------------------修改订单状态" + status ) ;
        Map<String , String> params = new HashMap<String ,String>() ;
        params.put("order_num" , orderNum) ;
        params.put("status" , status+"" );

        okhttpHelper.doPost(Contents.API.CHANGE_ORDER_STATUS, new loadingSpotsDialog<SubmitOrderMsg>(this) {
            @Override
            public void onErroe(Response response, int responseCode, Exception e) throws IOException {
                this.closeSpotsDialog();

                toPayResule(-1) ;
            }

            @Override
            public void callBackSucces(Response response, SubmitOrderMsg submitOrderMsg) throws IOException {
                this.closeSpotsDialog();

                int status = submitOrderMsg.getStatus() ;
                toPayResule(status) ;
            }

        }, params);

    }

    private void toPayResule(int status){

        Intent intent = new Intent(this , PayResultActivity.class) ;

        if(status == 1){
            for (int i = 0; i < shoppingCarts.size() ; i++) {
                cartProvider.delete(shoppingCarts.get(i));
            }
        }

        intent.putExtra("status" ,status ) ;

        startActivity(intent);
        finish();

    }



    private void addListener(){

        toOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewOrderActivity.this , MyOrderActivity.class));
            }
        });

        consigneeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(NewOrderActivity.this , ShowConsigneeAdrActivity.class ) , Contents.REQUEST_ORDER_CONSIGNEE );

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitOrder() ;
            }
        });

        pbToolbar.setLeftButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( radio_check != CHECK_ALIPAY ){
                    radio_check = CHECK_ALIPAY ;
                    initRadioButton() ;
                }
            }
        });

        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( radio_check != CHECK_WECHECK){
                    radio_check = CHECK_WECHECK ;
                    initRadioButton() ;
                }

            }
        });

        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( radio_check != CHECK_DB){
                    radio_check = CHECK_DB ;
                    initRadioButton() ;
                }
            }
        });

    }

    private void getData(){

        Intent intent = getIntent() ;
        Type type = new TypeToken<List<ShoppingCart>>(){}.getType() ;
        shoppingCarts = JsonUtil.fromJson(intent.getStringExtra(ShoppingBikeFragment.ORDER_WARES) , type ) ;
        sumPrice = intent.getFloatExtra(ShoppingBikeFragment.SUM_PRICE , 0) ;

    }

    public void initRecyclerView (){

        getData () ;

        if(cartProvider!=null){

            orderWareMsgAdapter = new OrderWareMsgAdapter(this , shoppingCarts) ;
            recyclerView.setAdapter(orderWareMsgAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));
            recyclerView.addItemDecoration(new MyDivider());

        }else {
            Log.d("----" , "获得的值为null--------------------------------------");
        }
    }

    private void initRadioButton (){

        switch (radio_check){
            case CHECK_ALIPAY:

                alipayButton.setChecked(true);
                dbButton.setChecked(false);
                wechatButton.setChecked(false);
                pay_channel = Contents.ALIPAY ;

                break;
            case CHECK_DB:

                alipayButton.setChecked(false);
                dbButton.setChecked(true);
                wechatButton.setChecked(false);
                pay_channel = Contents.BD ;

                break;
            case CHECK_WECHECK:

                alipayButton.setChecked(false);
                dbButton.setChecked(false);
                wechatButton.setChecked(true);

                pay_channel = Contents.WX ;

                break;
        }

    }

    class OrderItem {

        private int ware_id ;
        private int amount ;

        public OrderItem(int ware_id , int amout) {
            super();
            this.amount = amout ;
            this.ware_id = ware_id ;
        }
    }


}
