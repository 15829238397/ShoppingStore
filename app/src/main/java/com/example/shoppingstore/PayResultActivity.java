package com.example.shoppingstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 博 on 2017/7/26.
 */

public class PayResultActivity extends BaseActivity {

    private TextView resultText ;
    private ImageView resultButton ;
    private Button button ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pay_result_view);

        resultText = (TextView) findViewById(R.id.resultText) ;
        resultButton = (ImageView) findViewById(R.id.resultImg);
        button = (Button) findViewById(R.id.backHome) ;

        initView() ;
        addButtonListener() ;

    }

    private void addButtonListener(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent( v.getContext() , MainActivity.class));
                finish();

            }
        });
    }

    private void initView(){

        Intent intent = getIntent() ;
        if (intent != null){

            int status = intent.getIntExtra("status" , 0) ;
            if(status == 1){
                resultText.setText("支付成功");
                resultButton.setImageResource(R.drawable.icon_success_128);
            }else if(status == -1){
                resultText.setText("支付失败");
                resultButton.setImageResource(R.drawable.icon_cancel_128);
            }else if (status == -2){
                resultText.setText("用户已取消支付");
                resultButton.setImageResource(R.drawable.icon_cancel_128);
            }
        }

    }
}
