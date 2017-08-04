package com.example.shoppingstore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.shoppingstore.Bean.ShoppingCart;
import com.example.shoppingstore.NewOrderActivity;
import com.example.shoppingstore.Okhttp.OkhttpHelper;
import com.example.shoppingstore.R;
import com.example.shoppingstore.adapter.BaseAdapter;
import com.example.shoppingstore.adapter.CartAdapter;
import com.example.shoppingstore.application.MyApplication;
import com.example.shoppingstore.utils.CartProvider;
import com.example.shoppingstore.utils.JsonUtil;
import com.example.shoppingstore.widget.MyDivider;
import com.example.shoppingstore.widget.PbToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 博 on 2017/6/23.
 */

public class ShoppingBikeFragment extends BaseFragment {

    private RecyclerView recyclerView ;
    private CartAdapter cartAdapter ;
    private List<ShoppingCart> shoppingCarts ;
    private CartProvider cartProvider ;
    private TextView sumPrice ;
    private CheckBox allChooseCheckBox ;
    private PbToolbar pbToolbar ;
    private Button pay_button ;
    private static OkhttpHelper okhttpHelper;
    private float sumPrices ;

    private final static int STATUS_EDIT = 0 ;
    private final static int STATUS_NORNAL = 1 ;
    private static int statua = STATUS_NORNAL ;
    public static final String ORDER_WARES = "orderWares" ;
    public static final String SUM_PRICE = "sumPrices" ;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView =  inflater.inflate(R.layout.shoppingbike_fragment , container , false ) ;
        recyclerView = (RecyclerView) mView.findViewById(R.id.recycleView) ;
        sumPrice = (TextView) mView.findViewById(R.id.txt_total) ;
        allChooseCheckBox = (CheckBox) mView.findViewById(R.id.checkbox_all) ;
        pbToolbar = (PbToolbar) mView.findViewById(R.id.toolBar) ;
        pay_button = (Button) mView.findViewById(R.id.pay_button) ;

        okhttpHelper = OkhttpHelper.getOkhttpHelper() ;

        cartProvider = CartProvider.getCartProvider(getActivity()) ;
        initRecyclerView() ;

        setAllSeletedListener() ;

        setOncontrolDataListener() ;

        return mView ;
    }

    private void setAdatapterListener(){
        cartAdapter.setOnItemClickListener(new BaseAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                if( view.getId() == R.id.checkbox ){

                    shoppingCarts.get(position).setChecked(!shoppingCarts.get(position).getChecked());
                    cartProvider.update(shoppingCarts.get(position));
                    initSumChekBox();
                    getSum();
                }

                else {
                    Log.d("----" , "点击了其他控件") ;
                }

            }
        });

        cartAdapter.setNumControlerListener(new CartAdapter.NumControlerListener() {

            @Override
            public void onAdd(int position , int value) {
                Log.d("----" , "点击了+++控件") ;
                if (shoppingCarts == null)
                    shoppingCarts = cartProvider.getAll();
                    shoppingCarts.get(position).setCount(value);
                    cartProvider.update(shoppingCarts.get(position));
                    getSum();

            }

            @Override
            public void onSub(int position , int value) {
                Log.d("----" , "点击了---控件") ;
                if (shoppingCarts == null)
                    shoppingCarts = cartProvider.getAll();
                    shoppingCarts.get(position).setCount(value);
                    cartProvider.update(shoppingCarts.get(position));
                    getSum();

            }
        });
    }

    private void initSumChekBox(){

        if (shoppingCarts == null)
            shoppingCarts = cartProvider.getAll() ;

        for (int i = 0; i < shoppingCarts.size() ; i++) {
           if( !shoppingCarts.get(i).getChecked() ){
                allChooseCheckBox.setChecked(false);
                return;
           }
        }

        allChooseCheckBox.setChecked(true);
    }

    private void initRecyclerView(){

        shoppingCarts = cartProvider.getAll() ;

        for (int i = 0; i < shoppingCarts.size() ; i++) {
            shoppingCarts.get(i).setChecked(true);
            cartProvider.update(shoppingCarts.get(i));
        }

        if (shoppingCarts != null && shoppingCarts.size() > 0){

                cartAdapter = new CartAdapter(getContext() , shoppingCarts) ;
                Log.d("----" , ""+shoppingCarts.get(0).getName()) ;
                recyclerView.setAdapter(cartAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.addItemDecoration(new MyDivider());

                setAdatapterListener() ;
        }

        getSum() ;
        initSumChekBox() ;
    }

    public void refRecyclerView(){

        if(cartAdapter == null){
            return;
        }

        Log.e("----" , "刷新购物车-------------------------------------------------------------------------------------") ;

        cartAdapter.cleanData();
        shoppingCarts = cartProvider.getAll() ;
        cartAdapter.addData(shoppingCarts);

        getSum() ;
        initSumChekBox() ;
    }

    public void getSum(){

        shoppingCarts = cartProvider.getAll() ;

        float sumPriceData = (float) 0.0;

        for (int i = 0; i < shoppingCarts.size() ; i++) {

            if (shoppingCarts.get(i).getChecked()){
                sumPriceData  += Double.valueOf( shoppingCarts.get(i).getPrice() ) *  shoppingCarts.get(i).getCount() ;
            }
        }
        sumPrices = sumPriceData ;
        sumPrice.setText("合计 ￥"+ sumPriceData) ;
    }

    private void setAllSeletedListener(){

        allChooseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    showSelectAll() ;
                }else{
                    showSelectNone() ;
                }
            }
        });
    }

    private void showSelectAll(){
        shoppingCarts = cartProvider.getAll() ;
        if(shoppingCarts != null && shoppingCarts.size() > 0){
            boolean isNotAllSelcte = false ;

            for (int i = 0; i <shoppingCarts.size() ; i++) {
                if ( !shoppingCarts.get(i).getChecked() ){
                    shoppingCarts.get(i).setChecked(true);
                    isNotAllSelcte = true ;
                    cartProvider.update(shoppingCarts.get(i));
                }
            }
            if(isNotAllSelcte){

                getSum();
                refRecyclerView() ;
            }
        }
    }

    private void showSelectNone(){
        shoppingCarts = cartProvider.getAll() ;
        if(shoppingCarts != null && shoppingCarts.size() > 0){
            boolean isAllSelcte = false ;

            for (int i = 0; i <shoppingCarts.size() ; i++) {
                if ( shoppingCarts.get(i).getChecked() ){
                    shoppingCarts.get(i).setChecked(false);
                    isAllSelcte = true ;
                    cartProvider.update(shoppingCarts.get(i));
                }
            }
            if(isAllSelcte){

                getSum();
                refRecyclerView() ;
            }
        }
    }

    private void setOncontrolDataListener(){

        pbToolbar.setRightButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statua == STATUS_NORNAL){

                    showEditView() ;
                }else if (statua == STATUS_EDIT){

                    showNormalView() ;
                }
            }
        });

        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statua == STATUS_NORNAL){
                    //去结算
                    Intent intent = new Intent(getActivity() , NewOrderActivity.class) ;

                    getOrderWares() ;
                    intent.putExtra(SUM_PRICE , sumPrices) ;
                    intent.putExtra(ORDER_WARES , getOrderWares()) ;

                    startActivityWithLogin(intent , true , MyApplication.START_NO_RESULT);

                }else if (statua == STATUS_EDIT){
                    deleteCartData() ;
                }
            }
        });
    }


    private void showEditView(){

        pbToolbar.setRightButtonText("完成");
        showSelectNone() ;
        sumPrice.setVisibility(View.GONE);
        pay_button.setText("删除");
        statua = STATUS_EDIT ;

    }

    private void showNormalView(){

        pbToolbar.setRightButtonText("编辑");
        showSelectAll() ;
        sumPrice.setVisibility(View.VISIBLE);
        pay_button.setText("去结算");
        statua = STATUS_NORNAL ;
        refRecyclerView();

    }

    private void deleteCartData(){

        shoppingCarts = cartProvider.getAll() ;
        if (shoppingCarts !=null && shoppingCarts.size()>0){

            for (int i = 0; i < shoppingCarts.size() ; i++) {
                if(shoppingCarts.get(i).getChecked()){
                    cartProvider.delete(shoppingCarts.get(i));
                }
            }

            refRecyclerView();

        }

    }

    public String getOrderWares(){

        if (shoppingCarts == null || shoppingCarts.size() == 0){
            return null ;
        }

        List<ShoppingCart> orderWares = new ArrayList<>() ;

        for (int i = 0; i < shoppingCarts.size() ; i++) {
            if(shoppingCarts.get(i).getChecked()){
                orderWares.add(shoppingCarts.get(i)) ;
            }
        }

        return JsonUtil.toJSON(orderWares);
    }

}
