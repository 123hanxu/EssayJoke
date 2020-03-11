package com.example.essayjoke;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baselibrary.CheckNet;
import com.example.baselibrary.OnClick;
import com.example.baselibrary.ViewById;
import com.example.baselibrary.ViewUntil;


public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.mButton)
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUntil.inject(this);
        button.setText("快下班了");

    }

    @OnClick({R.id.mButton,R.id.mTextView})
    @CheckNet(R.id.mButton)
    public void onClick(View view){
        Toast.makeText(this,"点击成功",Toast.LENGTH_SHORT).show();
    }

}
