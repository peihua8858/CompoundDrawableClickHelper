package com.peihua.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fz.compoundtext.ClickDrawableTextView;
import com.fz.compoundtext.DoubleEndDrawableEditText;
import com.fz.compoundtext.DrawablePosition;
import com.fz.compoundtext.OnDrawableClickListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ClickDrawableTextView view = findViewById(R.id.cdtv_test);
        view.setEndDrawableVisible(true);
        view.setOnDrawableClickListener(new OnDrawableClickListener() {
            @Override
            public void onDrawableClick(View view, DrawablePosition position) {
                if (position == DrawablePosition.END) {
                    Toast.makeText(MainActivity.this, "sssss", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DoubleEndDrawableEditText drawableEditText = findViewById(R.id.et_new_password);
       drawableEditText.setOnDoubleDrawableClickListener((view1, position) -> {
           if (position == DrawablePosition.END && drawableEditText.getShowDrawableType()
                   == DoubleEndDrawableEditText.DRAWABLE_FIRST) {
               drawableEditText.clearFocus();
               Toast.makeText(MainActivity.this, "It is recommended that you don’t use the same passwords as on other websites.", Toast.LENGTH_SHORT).show();
           }
       });
    }
}
