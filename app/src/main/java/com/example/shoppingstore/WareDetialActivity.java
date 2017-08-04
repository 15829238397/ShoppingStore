package com.example.shoppingstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.shoppingstore.Bean.ShoppingCart;
import com.example.shoppingstore.Bean.Ware;
import com.example.shoppingstore.application.MyApplication;
import com.example.shoppingstore.fragment.ShoppingBikeFragment;
import com.example.shoppingstore.utils.CartProvider;
import com.example.shoppingstore.utils.JsonUtil;
import com.example.shoppingstore.widget.PbToolbar;
import com.mob.commons.SHARESDK;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import dmax.dialog.SpotsDialog;

import static com.example.shoppingstore.fragment.ShoppingBikeFragment.ORDER_WARES;
import static com.example.shoppingstore.fragment.ShoppingBikeFragment.SUM_PRICE;

/**
 * Created by 博 on 2017/7/20.
 */

public class WareDetialActivity extends BaseActivity {


    private WebView webView ;
    private PbToolbar pbToolbar ;
    private Ware mWare ;
    private WebAppInterface mAppInterface ;
    private CartProvider cartProvider ;
    private SpotsDialog dialog ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ware_detial_layout);

        webView = (WebView) findViewById(R.id.webView) ;
        pbToolbar = (PbToolbar) findViewById(R.id.toolBar) ;

        Serializable serializable = getIntent().getSerializableExtra(Contents.WARE);
        if (serializable == null) {
            this.finish();
        }

        dialog = new SpotsDialog(this , "loading...");
        dialog.show();

        mWare = (Ware) serializable ;
        cartProvider = CartProvider.getCartProvider(this) ;

        initWebView() ;
        addBackButtonListener() ;
        addShareButtonListener() ;

    }

    private void addShareButtonListener(){
        pbToolbar.setRightButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare() ;
            }
        });
    }

    private void addBackButtonListener(){
        pbToolbar.setLeftButtonIcOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WareDetialActivity.this.finish();
            }
        });
    }

    class WC extends WebViewClient{

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (dialog!=null && dialog.isShowing()){
                dialog.dismiss();
            }

            mAppInterface.showDetail();

        }
    }


    private void initWebView(){

        WebSettings settings = webView.getSettings() ;

        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setAppCacheEnabled(true);

        webView.loadUrl(Contents.API.SHOW_WARE_DETIAL);
        mAppInterface = new WebAppInterface(this) ;
        webView.addJavascriptInterface(mAppInterface , "appInterface");
        webView.setWebViewClient(new WC());

    }

    class WebAppInterface{

        private Context mContext ;

        public WebAppInterface(Context context) {
            this.mContext = context ;
        }

        @JavascriptInterface
        public void showDetail(){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    webView.loadUrl("javascript:showDetail("+mWare.getId()+")");

                }
            });
        }

        @JavascriptInterface
        public void buy(long id){

            Toast.makeText(mContext , "立即购买" , Toast.LENGTH_SHORT).show();

            //去结算
            Intent intent = new Intent(WareDetialActivity.this , NewOrderActivity.class) ;

            Log.d("----" ,"--------------mWare.getPrice()---------------" + mWare.getPrice()) ;

            intent.putExtra( ShoppingBikeFragment.SUM_PRICE , Float.valueOf( mWare.getPrice() ) );
            intent.putExtra( ORDER_WARES ,getOrderWares() ) ;

            startActivityWithLogin(intent , true , MyApplication.START_NO_RESULT);

        }

        @JavascriptInterface
        public void addToCart(long id){
            cartProvider.put(mWare);

            Toast.makeText(mContext , "添加购物车" , Toast.LENGTH_SHORT).show();
        }

    }

    public String getOrderWares(){

        List<ShoppingCart> orderWares = new ArrayList<>() ;
        ShoppingCart shoppingCart = new ShoppingCart() ;

        shoppingCart.setCount(1);
        shoppingCart.setChecked(true);
        shoppingCart.setId(mWare.getId());
        shoppingCart.setImgUrl(mWare.getImgUrl());
        shoppingCart.setPrice(mWare.getPrice());

       orderWares.add(shoppingCart) ;

        return JsonUtil.toJSON(orderWares);
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mWare.getName());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath(mWare.getImgUrl());//确保SDcard下面存在此张图片
        oks.setImageUrl(mWare.getImgUrl());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("很好的商品哟");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }


}
