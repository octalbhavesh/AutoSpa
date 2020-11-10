package com.poshwash.driver.views.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.TouchImageView;

public class FullScreenImageActivity extends AppCompatActivity {

    private Context mContext;
    private ImageView imgClose;
    private String path = "";
    private TouchImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_image_layout);
        initView();

    }

    private void initView() {
        mContext = this;
        path = getIntent().getStringExtra("photos");
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageView = (TouchImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ImageLoader.getInstance().displayImage(path, imageView, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}