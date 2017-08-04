package com.example.shoppingstore.adapter;

import android.content.Context;
import android.widget.TextView;

import com.example.shoppingstore.Bean.FirstClassifyData;
import com.example.shoppingstore.R;

import java.util.List;

/**
 * Created by 博 on 2017/7/13.
 */

public class ClassifyAdapter extends SimpleAdapter<FirstClassifyData> {

    private TextView name ;

    public ClassifyAdapter(Context context, List<FirstClassifyData> mDatas) {
        super(context, mDatas, R.layout.classify_first_layout);
    }

    @Override
    public void bindData(BaseViewHolder holder, FirstClassifyData firstClassifyData , int position) {

        name = holder.findTextView(R.id.text) ;

        name.setText(firstClassifyData.getName());

    }
}
