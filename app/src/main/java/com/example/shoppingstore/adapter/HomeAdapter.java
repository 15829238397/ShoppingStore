package com.example.shoppingstore.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shoppingstore.Bean.ResyslerViewIndicator;
import com.example.shoppingstore.R;
import com.example.shoppingstore.widget.GlideCircleTransform;
import com.example.shoppingstore.widget.GlideRoundTransform;

import java.util.List;

/**
 * Created by 博 on 2017/7/12.
 */

public class HomeAdapter extends SimpleAdapter<ResyslerViewIndicator> {

    private static int viewTypeNum = 0 ;
    private ImageView imageViewBig ;
    private ImageView imageViewSmallBottom ;
    private ImageView imageViewSmallTop ;
    private TextView title ;

    public HomeAdapter(Context context, List<ResyslerViewIndicator> mDatas) {

        super(context, mDatas, (viewTypeNum ++ % 2 == 0) ? R.layout.card_view_left : R.layout.card_view_right );
    }

    @Override
    public void bindData(BaseViewHolder holder, ResyslerViewIndicator resyslerViewIndicator , int position) {

        imageViewBig = holder.findImageView(R.id.imageview_big) ;
        imageViewSmallTop = holder.findImageView(R.id.imageview_small_top);
        imageViewSmallBottom = holder.findImageView(R.id.imageview_small_bottom) ;
        title = holder.findTextView(R.id.cardView_title) ;

        this.title.setText(resyslerViewIndicator.getTitle());
//
        //将图片加载到指定view
        Glide.with(context).load(resyslerViewIndicator.getCpTwo().getImgUrl()).asBitmap().into(this.imageViewSmallBottom);
        Glide.with(context).load(resyslerViewIndicator.getCpOne().getImgUrl()).asBitmap().into(this.imageViewBig);
        Glide.with(context).load(resyslerViewIndicator.getCpThree().getImgUrl()).asBitmap().into(this.imageViewSmallTop);

    }



}
