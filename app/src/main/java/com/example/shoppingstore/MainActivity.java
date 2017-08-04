package com.example.shoppingstore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingstore.Bean.TabIndicator;
import com.example.shoppingstore.fragment.ClassifyFragment;
import com.example.shoppingstore.fragment.HomeFragment;
import com.example.shoppingstore.fragment.MineFragment;
import com.example.shoppingstore.fragment.HotFragment;
import com.example.shoppingstore.fragment.ShoppingBikeFragment;

import java.util.ArrayList;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends BaseActivity {

    private FragmentTabHost fragmentTabHost ;
    private ImageView tabPhoto ;
    private TextView tabTitle ;
    private ShoppingBikeFragment shoppingBikeFragment ;

    private ArrayList<TabIndicator> tabIndicators = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_main_view);

        //设置真实调用的Fragment
        initTab();
    }

    /**
     * 初始化tab信息
     */
    public void initTab (){

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost) ;
        fragmentTabHost.setup(this , getSupportFragmentManager() , R.id.realTabContent);

        TabIndicator HomeTab = new TabIndicator(R.string.tab_home , R.drawable.tab_home_image, HomeFragment.class) ;
        TabIndicator SearchTab = new TabIndicator(R.string.tab_search , R.drawable.tab_search_image, HotFragment.class) ;
        TabIndicator ClassifyTab = new TabIndicator(R.string.tab_type , R.drawable.tab_classify_image, ClassifyFragment.class) ;
        TabIndicator ShoppingBikeTab = new TabIndicator(R.string.shopping_bike , R.drawable.tab_shoppingbike_image, ShoppingBikeFragment.class) ;
        TabIndicator MineTab = new TabIndicator(R.string.tab_mine , R.drawable.tab_user_image, MineFragment.class) ;

        tabIndicators.add(HomeTab) ;
        tabIndicators.add(SearchTab) ;
        tabIndicators.add(ClassifyTab) ;
        tabIndicators.add(ShoppingBikeTab) ;
        tabIndicators.add(MineTab) ;

        for(TabIndicator tabIndicator : tabIndicators){
            View view = LayoutInflater.from(this).inflate(R.layout.tab_indicator , null ) ;
            tabPhoto = (ImageView) view.findViewById(R.id.tab_photo);
            tabTitle = (TextView) view.findViewById(R.id.tab_title) ;

            tabPhoto.setBackgroundResource(tabIndicator.getTabPhoto());
            tabTitle.setText(tabIndicator.getTabTitle());

            fragmentTabHost.addTab(fragmentTabHost.newTabSpec(getString(tabIndicator.getTabTitle())).setIndicator(view) , tabIndicator.getFragment() , null );
        }

        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if(tabId == getString(R.string.shopping_bike)){

                    if (shoppingBikeFragment == null){

                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.shopping_bike)) ;

                    if (fragment != null ){
                        shoppingBikeFragment = (ShoppingBikeFragment) fragment;
                        shoppingBikeFragment.refRecyclerView();
                    }
                 }else {
                        shoppingBikeFragment.refRecyclerView();
                    }
                }
            }
        });
    }
}
