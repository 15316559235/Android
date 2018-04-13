package com.example.sc.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class SecondActivity extends AppCompatActivity {

    private Button button=null;
    private TextView textView =null;
    //设置类ButtonListener实现接口,OnClickListener,在其中可以指定不同id的button对应不同的点击事件
    //可以借此将代码抽出来，提高代码的可阅读性
    private class ButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button:
                    Intent intent =getIntent();
                    //这里使用bundle绷带来传输数据
                    Bundle bundle =new Bundle();
                    //传输的内容仍然是键值对的形式
                    bundle.putString("second","hello world from secondActivity!");//回发的消息,hello world from secondActivity!
                    intent.putExtras(bundle);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
        }
    }

    //初始化View
    public void initView(){
        button= (Button) findViewById(R.id.button);
        textView= (TextView) findViewById(R.id.textView);
        button.setOnClickListener(
                new ButtonListener()
        );
        Intent intent =getIntent();
        String text =intent.getStringExtra(MainActivity.Intent_key);
        textView.setText(text);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
    }
}
