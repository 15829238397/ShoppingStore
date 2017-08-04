package com.example.shoppingstore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.example.shoppingstore.Bean.Page;
import com.example.shoppingstore.Bean.Ware;
import com.example.shoppingstore.Bean.FirstClassifyData;
import com.example.shoppingstore.Bean.SliderIndicator;
import com.example.shoppingstore.Contents;
import com.example.shoppingstore.Okhttp.OkhttpHelper;
import com.example.shoppingstore.Okhttp.loadingSpotsDialog;
import com.example.shoppingstore.R;
import com.example.shoppingstore.WareDetialActivity;
import com.example.shoppingstore.adapter.BaseAdapter;
import com.example.shoppingstore.adapter.ClassifyAdapter;
import com.example.shoppingstore.adapter.ClassifyWaresAdapter;
import com.example.shoppingstore.utils.Pager;
import com.example.shoppingstore.widget.MyDivider;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 博 on 2017/6/23.
 */

public class ClassifyFragment extends BaseFragment {

    private RecyclerView firstClassify ;
    private RecyclerView classifyWares ;
    private OkhttpHelper okhttpHelper = OkhttpHelper.getOkhttpHelper() ;
    private ClassifyAdapter classifyAdapter ;
    private SliderLayout sliderLayout ;
    private MaterialRefreshLayout materialRefreshLayout ;
    private List<Ware> mData ;
    private Pager pager ;
    private int totalCount = 1 ;
    private int curPage = 1 ;
    private int pageSize = 10 ;
    private int categoryId = 1 ;
    private int lastCategoryId = 1 ;
    private ClassifyWaresAdapter classifyWaresAdapter ;

    private static final int STATUS_NORMAL = 0 ;
    private static final int STATUS_FRESH = 1 ;
    private static final int STATUS_LOADMARE = 2 ;

    private int status = STATUS_NORMAL ;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.classify_fragment , container , false ) ;

        firstClassify = (RecyclerView) mView.findViewById(R.id.classfyRecylerView);
        sliderLayout = (SliderLayout) mView.findViewById(R.id.classfySlidLayout) ;
        materialRefreshLayout = (MaterialRefreshLayout) mView.findViewById(R.id.materialRefreshLayout);
        classifyWares = (RecyclerView) mView.findViewById(R.id.classfyDataRecylerView) ;

        curPage = 1 ;
        categoryId = 1 ;

        initFirstClassify() ;
        initSlider() ;
        try {
            initMaterialRefrshLayoutListener() ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("onCreateView" , "------------------------------------") ;

        return mView ;
    }

    public void initMaterialRefrshLayoutListener () throws Exception {

        String uri = Contents.API.WARE_LIST;

        final Pager.Builder builder = Pager.getBuilder()
                .setMaterialRefreshLayout(materialRefreshLayout)
                .putParams("categoryId" , 1)
                .putParams("curPage" , 1)
                .putParams("pageSize" , 10)
                .setUri(uri)
                .setOnPageListener(new Pager.OnPageListener<Ware>() {
                    @Override
                    public void loadNormal(List<Ware> mData, int totalPage, int pageSize) {

                        if( mData!=null && mData.size() > 0 ){
                            if(classifyWaresAdapter == null){

                                classifyWaresAdapter = new ClassifyWaresAdapter(getContext(), mData);
                                classifyWares.setAdapter(classifyWaresAdapter);
                                classifyWares.setLayoutManager(new GridLayoutManager(getContext() , 2));
                                classifyWares.addItemDecoration(new MyDivider());
                                addClassifyWaresAdapterListener() ;

                            }else{

                                classifyWaresAdapter.cleanData();
                                classifyWaresAdapter.addData(mData);

                            }
                        }else {
                            Toast.makeText(getContext() , "该类别暂无商品" , Toast.LENGTH_SHORT) .show();
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
                        classifyWares.scrollToPosition(0);
                    }
                }) ;

        pager = builder.build(getContext() , Page.class) ;
    }


    private void addClassifyWaresAdapterListener(){
        classifyWaresAdapter.setOnItemClickListener(new BaseAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position) throws Exception {

                    Ware ware = classifyWaresAdapter.getData(position);
                    Intent intent = new Intent(getActivity() , WareDetialActivity.class);
                    intent.putExtra(Contents.WARE , ware);

                    startActivity(intent);

            }
        });
    }

    public void initFirstClassify(){
        requestFirstClassifyData() ;
    }

    public void requestFirstClassifyData(){

        String uri = Contents.API.CATEGORY_LIST ;

        okhttpHelper.doGet(uri, new loadingSpotsDialog<List<FirstClassifyData>>(getContext()) {

            @Override
            public void onErroe(Response response, int responseCode, Exception e) throws IOException {
                this.closeSpotsDialog();
            }

            @Override
            public void callBackSucces(Response response, List<FirstClassifyData> firstClassifyDatas) throws IOException {
                this.closeSpotsDialog();
                showFirstClassifyData(firstClassifyDatas);
            }

        });
    }

    public void showFirstClassifyData(List<FirstClassifyData> firstClassifyDatas) {

        classifyAdapter = new ClassifyAdapter(getContext() , firstClassifyDatas ) ;

        firstClassify.setAdapter(classifyAdapter);
        firstClassify.setLayoutManager(new LinearLayoutManager(getContext()));
        firstClassify.addItemDecoration(new DividerItemDecoration(getContext() , DividerItemDecoration.VERTICAL));

        addFirstClassifyItemListener(firstClassifyDatas) ;
    }

    public void addFirstClassifyItemListener(final List<FirstClassifyData> firstClassifyDatas){
        classifyAdapter.setOnItemClickListener(new BaseAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position) throws Exception {

                lastCategoryId = categoryId ;
                categoryId = position + 1 ;
                pager.changeParamsInUri("categoryId" , categoryId );
                pager.changeParamsInUri("curPage" , 1);
                pager.getData();

            }
        });
    }

    public void initSlider () {
        OkhttpHelper okhttpHelper = OkhttpHelper.getOkhttpHelper();

        Map<String , String> params = new HashMap<String , String>() ;
        params.put("type" , 1+"") ;

        okhttpHelper.doGet(Contents.API.QUERY_SLIDER, new loadingSpotsDialog<List<SliderIndicator>>(getContext()) {

            @Override
            public void onFailure(Request request, IOException e) {
                this.closeSpotsDialog();
                Log.e("发送请求数据异常" , "---------->"+e) ;
                initSlider(getWoringWebConectList());
            }

            @Override
            public void onErroe(Response response, int responseCode, Exception e) throws IOException {
                this.closeSpotsDialog();
                Log.e("接收数据异常" , "---------->"+e) ;
                initSlider(getWoringWebConectList());
            }

            @Override
            public void callBackSucces(Response response, List<SliderIndicator> sliderIndicator) throws IOException {
                this.closeSpotsDialog();
                initSlider(sliderIndicator);
            }
        } , params);
    }

    public List<SliderIndicator> getWoringWebConectList(){
        List<SliderIndicator> sliderWebConectlist = new ArrayList<SliderIndicator>() ;
        sliderWebConectlist.add(new SliderIndicator("网络连接错误" , "File://R/web_wrong/web_wrong")) ;
        return sliderWebConectlist ;
    }

    public void initSlider(List<SliderIndicator> sliderIndicators ){
        //添加图片控件
        sliderLayout.removeAllSliders();
        for(SliderIndicator sliderIndicator : sliderIndicators){
            addTextSliderView(sliderIndicator) ;
        }

        //设置指示器的位置
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        //设置图片的切换效果
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        //添加textView动画特效
        //设置切换时长2000 ,时长越小，切换速度越快
        sliderLayout.setDuration(3000);
    }

    public void addTextSliderView (SliderIndicator sliderIndicator){
        DefaultSliderView SliderView=new DefaultSliderView(getContext());
        SliderView.image(sliderIndicator.getImgUrl()).error(R.drawable.web_wrong);//设置图片的网络地址
        SliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);//设置图片的缩放效果;
        //添加到布局中显示
        sliderLayout.addSlider(SliderView);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStop" , "------------------------------------") ;
        classifyWaresAdapter = null ;
    }

}
