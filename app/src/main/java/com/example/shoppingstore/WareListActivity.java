package com.example.shoppingstore;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.example.shoppingstore.Bean.Page;
import com.example.shoppingstore.Bean.Ware;
import com.example.shoppingstore.adapter.BaseAdapter;
import com.example.shoppingstore.adapter.CartAdapter;
import com.example.shoppingstore.adapter.ClassifyWaresAdapter;
import com.example.shoppingstore.adapter.HotAdapter;
import com.example.shoppingstore.fragment.HotFragment;
import com.example.shoppingstore.utils.CartProvider;
import com.example.shoppingstore.utils.Pager;
import com.example.shoppingstore.widget.MyDivider;
import com.example.shoppingstore.widget.PbToolbar;

import java.util.List;

/**
 * Created by 博 on 2017/7/19.
 */

public class WareListActivity extends BaseActivity{

    private TabLayout showWaresTabs ;
    private HotAdapter hotAdapter ;
    private MaterialRefreshLayout materialRefreshLayout ;
    private RecyclerView recyclerView ;
    private Pager pager ;
    private Intent intent ;
    private PbToolbar pbToolbar ;
    private TextView showWaresNum ;
    private ClassifyWaresAdapter classifyWaresAdapter ;
    private TabLayout.Tab tab1 ;
    private TabLayout.Tab tab2 ;
    private TabLayout.Tab tab3 ;

    public static final int SHOW_ORDER_DEFAUT = 0 ;
    public static final int SHOW_ORDER_PRICE = 2 ;
    public static final int SHOW_ORDER_SALES = 1 ;

    public static int orderType = SHOW_ORDER_DEFAUT ;

    public static final int SHOW_TYPE_LINER = 0 ;
    public static final int SHOW_TYPE_GRID = 1 ;

    public static int showType = SHOW_TYPE_GRID ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ware_list_activity);

        showWaresTabs = (TabLayout) findViewById(R.id.show_wares_tab);
        recyclerView = (RecyclerView) findViewById(R.id.recycleListView) ;
        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.materialRefreshLayout);
        pbToolbar = (PbToolbar) findViewById(R.id.toolBar) ;
        showWaresNum = (TextView) findViewById(R.id.show_wares_num) ;

        tab1 = showWaresTabs.newTab().setText(R.string.show_type_defaut).setTag(SHOW_ORDER_DEFAUT) ;
        tab2 = showWaresTabs.newTab().setText(R.string.show_type_price).setTag(SHOW_ORDER_PRICE) ;
        tab3 = showWaresTabs.newTab().setText(R.string.show_type_sales).setTag(SHOW_ORDER_SALES) ;

        showWaresTabs.addTab(tab1);
        showWaresTabs.addTab(tab2);
        showWaresTabs.addTab(tab3);

        intent = this.getIntent() ;

        addToobarListener() ;
        addTabItemListener() ;
        try {
            InitShowOrder() ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        addShowStyleChengeListener() ;

    }



    /**
     * 设置商品总数展示
     */
    private void initShowWaresNum (){
        showWaresNum.setText( "一共有"+pager.getTotalCount()+"件商品" );
    }

    /**
     * 点击返回按钮返回主菜单
     */
    public void addToobarListener(){
        pbToolbar.setLeftButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WareListActivity.this.finish();
            }
        });
    }


    /**
     * 设置tab按钮切换排序规则
     */
    private void addTabItemListener(){
        showWaresTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if ((int)tab.getTag() != orderType){
                    orderType =(int)tab.getTag() ;
                    changeShowOrder(orderType);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void InitShowOrder() throws Exception {
        initMaterialRefrshLayoutListener() ;
    }

    private void changeShowOrder(int orderType){
        switch (orderType){
            case SHOW_ORDER_DEFAUT :
                pager.changeParamsInUri("orderBy" , 0);
                pager .changeParamsInUri("curPage" , 1);
                pager.getData();
                break;
            case SHOW_ORDER_SALES :
                pager.changeParamsInUri("orderBy" , 1);
                pager .changeParamsInUri("curPage" , 1);
                pager.getData();
                break;
            case SHOW_ORDER_PRICE :
                pager.changeParamsInUri("orderBy" , 2);
                pager .changeParamsInUri("curPage" , 1);
                pager.getData();
                break;
        }
    }

    private int getCampaignId (){
       return intent.getIntExtra("campaignId" , SHOW_ORDER_DEFAUT ) ;
    }

    /**
     * 展示格式切换
     */
    private void addShowStyleChengeListener(){
        pbToolbar.setRightImgeButtonIcOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Toast.makeText(WareListActivity.this , "切换格式"+showType , Toast.LENGTH_SHORT).show();
                try {
                    changeShowType();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeShowType() throws Exception {
        switch (showType){
            case SHOW_TYPE_LINER :
                initMaterialRefrshLayoutListener() ;
                pbToolbar.setRightButtonIcon(R.drawable.icon_grid_32);
                showType = SHOW_TYPE_GRID ;
                break;
            case SHOW_TYPE_GRID :
                initGridMaterialRefrshLayoutListener() ;
                pbToolbar.setRightButtonIcon(R.drawable.icon_list_32);
                showType = SHOW_TYPE_LINER ;
                break;
        }
    }

    /**
     * Grid布局展示商品
     * @throws Exception
     */
    public void initGridMaterialRefrshLayoutListener () throws Exception {

        String uri = "http://112.124.22.238:8081/course_api/wares/campaign/list" ;

        Pager.Builder builder = Pager.getBuilder()
                .setMaterialRefreshLayout(materialRefreshLayout)
                .putParams("campaignId" , getCampaignId())
                .putParams("orderBy" , orderType)
                .putParams("curPage" , 1)
                .putParams("pageSize" , 10)
                .setUri(uri)
                .setOnPageListener(new Pager.OnPageListener<Ware>() {
                    @Override
                    public void loadNormal(List<Ware> mData, int totalPage, int pageSize) {

                        if( mData!=null && mData.size() > 0 ){
                            if(classifyWaresAdapter == null){

                                classifyWaresAdapter = new ClassifyWaresAdapter(WareListActivity.this , mData);
                                recyclerView.setAdapter(classifyWaresAdapter);
                                recyclerView.setLayoutManager(new GridLayoutManager(WareListActivity.this , 2));
                                recyclerView.addItemDecoration(new MyDivider());
                                setClassifyWaresAdapterItemlistenler() ;
                            }else{

                                classifyWaresAdapter.cleanData();
                                classifyWaresAdapter.addData(mData);
                                recyclerView.setAdapter(classifyWaresAdapter);
                                recyclerView.setLayoutManager(new GridLayoutManager(WareListActivity.this , 2));

                            }
                        }else {
                            Toast.makeText(WareListActivity.this , "该类别暂无商品" , Toast.LENGTH_SHORT) .show();
                        }

                    }

                    @Override
                    public void loadMoreData(List<Ware> mData, int totalPage, int pageSize) {
                        classifyWaresAdapter.addData(mData);
                    }

                    @Override
                    public void refData(List<Ware> mData, int totalPage, int pageSize) {
                        classifyWaresAdapter.cleanData();
                        classifyWaresAdapter.addData(mData);
                        recyclerView.scrollToPosition(0);
                    }
                }) ;

        pager = builder.build(WareListActivity.this , Page.class) ;
    }

    /**
     * list布局展示商品
     * @throws Exception
     */
    public void initMaterialRefrshLayoutListener () throws Exception {

        Log.d("----" , "-----------------" + "List") ;

        String uri = "http://112.124.22.238:8081/course_api/wares/campaign/list" ;

        Pager.Builder builder = Pager.getBuilder()
                .setMaterialRefreshLayout(materialRefreshLayout)
                .putParams("campaignId" , getCampaignId())
                .putParams("orderBy" , orderType)
                .putParams("curPage" , 1)
                .putParams("pageSize" , 10)
                .setUri(uri)
                .setOnPageListener(new Pager.OnPageListener<Ware>() {
                    @Override
                    public void loadNormal(List<Ware> mData, int totalPage, int pageSize) {
                        if( mData!=null && mData.size() > 0 ){
                            if(hotAdapter == null) {
                                hotAdapter = new HotAdapter(WareListActivity.this, mData);
                                setHotAdapterItemlistenler();
                                recyclerView.setAdapter(hotAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(WareListActivity.this));
                            }else {
                                hotAdapter.cleanData();
                                hotAdapter.addData(mData);
                                recyclerView.setAdapter(hotAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(WareListActivity.this));
                            }
                        }else {
                            Toast.makeText(WareListActivity.this , "该类别暂无商品" , Toast.LENGTH_SHORT) .show();
                        }
                        initShowWaresNum();
                    }

                    @Override
                    public void loadMoreData(List<Ware> mData, int totalPage, int pageSize) {
                        hotAdapter.addData(mData);
                    }

                    @Override
                    public void refData(List<Ware> mData, int totalPage, int pageSize) {
                        hotAdapter.cleanData();
                        hotAdapter.addData(mData);
                        recyclerView.scrollToPosition(0);
                    }
                }) ;

        pager = builder.build(this , Page.class) ;
    }

    public void setClassifyWaresAdapterItemlistenler (){

        classifyWaresAdapter.setOnItemClickListener(new BaseAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                Ware ware = classifyWaresAdapter.getData(position);
                Intent intent = new Intent(WareListActivity.this , WareDetialActivity.class);
                intent.putExtra(Contents.WARE , ware);

                startActivity(intent);

            }
        });

    }

    public void setHotAdapterItemlistenler (){

        hotAdapter.setOnItemClickListener(new BaseAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                if(view.getId() == R.id.buyButton){
                    addToCart(position) ;
                }else {

                    Ware ware = hotAdapter.getData(position);
                    Intent intent = new Intent(WareListActivity.this , WareDetialActivity.class);
                    intent.putExtra(Contents.WARE , ware);

                    startActivity(intent);
                }

            }
        });

    }

    public void addToCart( int position ){

        Ware hotGoodsMsgPart = hotAdapter.getData(position) ;

        CartProvider.getCartProvider(this) .put( hotGoodsMsgPart.toShoppinfCart() );
        Toast.makeText( this , "已添加到购物车" , Toast.LENGTH_SHORT ).show();

    }

}
