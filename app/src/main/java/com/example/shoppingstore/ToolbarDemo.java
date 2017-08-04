package com.example.shoppingstore;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.shoppingstore.MyView.NumControlerView;
import com.example.shoppingstore.widget.MyDivider;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

/**
 * Created by 博 on 2017/6/23.
 */

public class ToolbarDemo extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);
        NumControlerView numControlerView = (NumControlerView) findViewById(R.id.num);
        numControlerView.setValueChangeListener(new NumControlerView.onNumChangedListener() {
            @Override
            public void addValueListener(View v, int value) {
                Toast.makeText(v.getContext() , "加1" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void subValueListener(View v, int value) {
                Toast.makeText(v.getContext() , "减1" , Toast.LENGTH_SHORT).show();
            }


//            @Override
//            public void addValueListener(View v) {
//                Toast.makeText(v.getContext() , "加1" , Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void subValueListener(View v) {
//                Toast.makeText(v.getContext() , "减1" , Toast.LENGTH_SHORT).show();
//            }
        });
    }
}