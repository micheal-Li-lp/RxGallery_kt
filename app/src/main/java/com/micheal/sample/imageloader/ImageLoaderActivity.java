package com.micheal.sample.imageloader;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.micheal.rxgallery.RxGalleryFinal;
import com.micheal.rxgallery.imageloader.ImageLoaderType;
import com.micheal.rxgallery.rxbus.RxBusResultDisposable;
import com.micheal.rxgallery.rxbus.event.ImageRadioResultEvent;
import com.micheal.rxgallery.utils.Logger;
import com.micheal.sample.R;


/**
 * by y on 2017/6/7.
 */

public class ImageLoaderActivity extends AppCompatActivity {

    private AppCompatCheckBox appCompatCheckBox;
    private RxGalleryFinal with;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageloader);
        appCompatCheckBox = findViewById(R.id.cb_gif);
        findViewById(R.id.btn_glide).setOnClickListener(v -> start(ImageLoaderType.GLIDE));
        findViewById(R.id.btn_picasso).setOnClickListener(v -> start(ImageLoaderType.PICASSO));
        findViewById(R.id.btn_fresco).setOnClickListener(v -> start(ImageLoaderType.FRESCO));
        findViewById(R.id.btn_universal).setOnClickListener(v -> start(ImageLoaderType.UNIVERSAL));
    }

    private void start(ImageLoaderType imageLoaderType) {
        switch (imageLoaderType) {
            case PICASSO:
            case UNIVERSAL:
                Toast.makeText(getApplicationContext(), imageLoaderType + "不支持Gif", Toast.LENGTH_SHORT).show();
                break;
        }
        if (with == null)
            with = RxGalleryFinal.with(this);
        with.image()
                .radio()
                .gif(appCompatCheckBox.isChecked())
                .imageLoader(imageLoaderType)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) {
                        Toast.makeText(getBaseContext(), "选中了图片路径：" + imageRadioResultEvent.getImageCropEntity().getOriginalPath(), Toast.LENGTH_SHORT).show();
                    }
                }).openGallery();
    }

    @Override
    protected void onDestroy() {
        if (with != null) {
            Logger.i("RxGalleryFinal == null");
            with = null;
        }
        super.onDestroy();
    }
}
