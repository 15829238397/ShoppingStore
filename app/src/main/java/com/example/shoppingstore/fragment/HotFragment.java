package com.example.shoppingstore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.example.shoppingstore.Bean.Page;
import com.example.shoppingstore.Bean.Ware;
import com.example.shoppingstore.Contents;
import com.example.shoppingstore.R;
import com.example.shoppingstore.WareDetialActivity;
import com.example.shoppingstore.adapter.BaseAdapter;
import com.example.shoppingstore.adapter.HotAdapter;
import com.example.shoppingstore.utils.CartProvider;
import com.example.shoppingstore.utils.Pager;
import com.example.shoppingstore.widget.MyDivider;

import java.util.List;

/**
 * Created by 博 on 2017/6/23.
 */

public class HotFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private View mView ;
    private MaterialRefreshLayout materialRefreshLayout ;
    private HotAdapter myAdapter ;
    private Pager pager ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView =  inflater.inflate(R.layout.hot_fragment, container , false ) ;
        this.recyclerView = (RecyclerView) mView.findViewById(R.id.recycleListView) ;
        this.materialRefreshLayout = (MaterialRefreshLayout) mView.findViewById(R.id.materialRefreshLayout);

        try {
            initMaterialRefrshLayoutListener() ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mView ;
    }

    public void initMaterialRefrshLayoutListener () throws Exception {

        String uri = Contents.API.HOT ;

        Pager.Builder builder = Pager.getBuilder()
                .setMaterialRefreshLayout(materialRefreshLayout)
                .putParams("curPage" , 1)
                .putParams("pageSize" , 10)
                .setUri(uri)
                .setOnPageListener(new Pager.OnPageListener<Ware>() {
                    @Override
                    public void loadNormal(List<Ware> mData, int totalPage, int pageSize) {
                        myAdapter =new HotAdapter(getContext() , mData ) ;
                        setItemlistenler() ;
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(HotFragment.this.getContext()));
                        recyclerView.addItemDecoration(new MyDivider());
                    }

                    @Override
                    public void loadMoreData(List<Ware> mData, int totalPage, int pageSize) {
                        myAdapter.addData(mData);
                    }

                    @Override
                    public void refData(List<Ware> mData, int totalPage, int pageSize) {
                        myAdapter.cleanData();
                        myAdapter.addData(mData);
                        recyclerView.scrollToPosition(0);
                    }
                }) ;

            pager = builder.build(getContext() , Page.class) ;
    }

    public void setItemlistenler (){

        myAdapter.setOnItemClickListener(new BaseAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                if(view.getId() == R.id.buyButton){
                    addToCart(position) ;
                }else {

                    Ware ware = myAdapter.getData(position);
                    Intent intent = new Intent(getActivity() , WareDetialActivity.class);
                    intent.putExtra(Contents.WARE , ware);

                    startActivity(intent);
                }

            }
        });

    }

    public void addToCart( int position ){

        Ware hotGoodsMsgPart = myAdapter.getData(position) ;

        CartProvider.getCartProvider(getContext()) .put( hotGoodsMsgPart );
        Toast.makeText( getContext() , "已添加到购物车" , Toast.LENGTH_SHORT ).show();

    }

}
