package com.micheal.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.micheal.rxgallery.RxGalleryFinal;
import com.micheal.rxgallery.RxGalleryFinalApi;
import com.micheal.rxgallery.entity.MediaEntity;
import com.micheal.rxgallery.imageloader.ImageLoaderType;
import com.micheal.rxgallery.rxbus.RxBusResultDisposable;
import com.micheal.rxgallery.rxbus.event.ImageMultipleResultEvent;
import com.micheal.rxgallery.rxbus.event.ImageRadioResultEvent;
import com.micheal.rxgallery.ui.RxGalleryListener;
import com.micheal.rxgallery.ui.activity.MediaActivity;
import com.micheal.rxgallery.ui.base.IMultiImageCheckedListener;
import com.micheal.rxgallery.ui.base.IRadioImageCheckedListener;
import com.micheal.rxgallery.utils.Logger;
import com.micheal.rxgallery.utils.PermissionCheckUtils;
import com.micheal.sample.imageloader.ImageLoaderActivity;
import com.yalantis.ucrop.model.AspectRatio;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * 示例
 *
 * @author KARL-dujinyang
 *         <p>
 *         openGallery 返回 void,如果想使用RxGalleryFinal对象，请在 openGallery() 之前返回 RxGalleryFinal 对象
 *         <p>
 *         <p>
 *         RxGalleryFinal radio = RxGalleryFinal
 *         with(MainActivity.this)
 *         image()
 *         radio();
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG ="main_activity";

    RadioButton mRbRadioIMG, mRbMutiIMG, mRbOpenC, mRbRadioVD, mRbMutiVD, mRbCropZD, mRbCropZVD;


    private Boolean openCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_image_loader).setOnClickListener(this);
        findViewById(R.id.btn_open_def_radio).setOnClickListener(this);
        findViewById(R.id.btn_open_def_multi).setOnClickListener(this);
        findViewById(R.id.btn_open_img).setOnClickListener(this);
        findViewById(R.id.btn_open_vd).setOnClickListener(this);
        findViewById(R.id.btn_open_crop).setOnClickListener(this);
        findViewById(R.id.btn_open_set_path).setOnClickListener(this);
        mRbRadioIMG = findViewById(R.id.rb_radio_img);
        mRbMutiIMG = findViewById(R.id.rb_muti_img);
        mRbRadioVD = findViewById(R.id.rb_radio_vd);
        mRbMutiVD = findViewById(R.id.rb_muti_vd);
        mRbOpenC = findViewById(R.id.rb_openC);
        mRbCropZD = findViewById(R.id.rb_radio_crop_z);
        mRbCropZVD = findViewById(R.id.rb_radio_crop_vz);
        //多选事件的回调
        RxGalleryListener
                .getInstance()
                .setMultiImageCheckedListener(
                        new IMultiImageCheckedListener() {
                            @Override
                            public void selectedImg(@NotNull Object t, boolean isChecked) {
                                Toast.makeText(getBaseContext(), isChecked ? "选中" : "取消选中", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void selectedImgMax(@NotNull Object t, boolean isChecked, int maxSize) {
                                Toast.makeText(getBaseContext(), "你最多只能选择" + maxSize + "张图片", Toast.LENGTH_SHORT).show();
                            }
                        });
        //裁剪图片的回调
        RxGalleryListener
                .getInstance()
                .setRadioImageCheckedListener(
                        new IRadioImageCheckedListener() {
                            @Override
                            public void cropAfter(@NotNull Object t) {
                                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public boolean isActivityFinish() {
                                return false;
                            }
                        });

        requestReadExternalPermission(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_image_loader:
                Intent intent = new Intent(v.getContext(), ImageLoaderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_open_def_radio:
                openRadio();
                break;
            case R.id.btn_open_def_multi:
                openMulti();
                break;
            case R.id.btn_open_img:
                openImageSelect();
                break;
            case R.id.btn_open_vd:
                openVideoSelect();
                break;
            case R.id.btn_open_crop:
//                openCrop();
                requestReadExternalPermission(true);
                break;
            case R.id.btn_open_set_path:
                setPath();
                break;
        }

    }


    @SuppressLint("NewApi")
    private void requestReadExternalPermission(Boolean openCrop) {
        this.openCrop=openCrop;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "READ permission IS NOT granted...");

            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Log.d(TAG, "11111111111111");
            } else {
                // 0 是自己定义的请求coude
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                Log.d(TAG, "222222222222");
            }
        } else {
            Log.d(TAG, "READ permission is granted...");
            if (this.openCrop){
                openCrop();
            }
        }
    }

    /**
     * 设置 照片路径 和 裁剪路径
     */
    private void setPath() {
        RxGalleryFinalApi.setImgSaveRxSDCard("dujinyang");
        RxGalleryFinalApi.setImgSaveRxCropSDCard("dujinyang/crop");//裁剪会自动生成路径；也可以手动设置裁剪的路径；
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.openCrop){
            openCrop();
        }
    }

    /**
     * 直接裁剪  or  拍照并裁剪( 查看 onActivityResult())
     */
    private void openCrop() {
        if (mRbCropZD.isChecked()) {
            //直接裁剪
            String inputImg = "";
            Toast.makeText(MainActivity.this, "没有图片演示，请选择‘拍照裁剪’功能", Toast.LENGTH_SHORT).show();
            //  RxGalleryFinalApi.cropScannerForResult(MainActivity.this, RxGalleryFinalApi.getModelPath(), inputImg);//调用裁剪.RxGalleryFinalApi.getModelPath()为模拟的输出路径
        } else {
            //            RxGalleryFinalApi.openZKCamera(MainActivity.this);

            SimpleRxGalleryFinal.init(
                    new SimpleRxGalleryFinal.RxGalleryFinalCropListener() {
                        @NonNull
                        @Override
                        public Activity getSimpleActivity() {
                            return MainActivity.this;
                        }

                        @Override
                        public void onCropCancel() {
                            Toast.makeText(getSimpleActivity(), "裁剪被取消", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCropSuccess(@Nullable Uri uri) {
                            Toast.makeText(getSimpleActivity(), "裁剪成功：" + uri, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCropError(@NonNull String errorMessage) {
                            Toast.makeText(getSimpleActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
            ).openCamera();
        }
    }

    /**
     * 视频
     * 单选 多选
     */
    private void openVideoSelect() {
        if (mRbRadioVD.isChecked()) {
            openVideoSelectRadioMethod();
        } else if (mRbMutiVD.isChecked()) {
            openVideoSelectMultiMethod(0);
        }
    }

    /**
     * 图片
     * 单选，多选，  直接打开相机
     */
    private void openImageSelect() {
        if (mRbRadioIMG.isChecked()) {
            openImageSelectRadioMethod(3);
        } else if (mRbMutiIMG.isChecked()) {
            openImageSelectMultiMethod(1);
        } else {
            if (PermissionCheckUtils.checkCameraPermission(this, "", MediaActivity.REQUEST_CAMERA_ACCESS_PERMISSION)) {
                RxGalleryFinalApi.openZKCamera(MainActivity.this);
            }
        }
    }

    private List<MediaEntity> list = null;

    /**
     * 自定义多选
     */
    private void openMulti() {
//        RxGalleryFinal.with(this).hidePreview();
        RxGalleryFinal rxGalleryFinal = RxGalleryFinal
                .with(MainActivity.this)
                .image()
                .multiple();
        if (list != null && !list.isEmpty()) {
            rxGalleryFinal
                    .selected(list);
        }
        rxGalleryFinal.maxSize(8)
                .imageLoader(ImageLoaderType.FRESCO)
                .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {

                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) {
                        list = imageMultipleResultEvent.getMediaResultList();
                        Toast.makeText(getBaseContext(),
                                "已选择" + Objects.requireNonNull(imageMultipleResultEvent.getMediaResultList()).size() + "张图片", Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Toast.makeText(getBaseContext(), "OVER", Toast.LENGTH_SHORT).show();
                    }
                })
                .openGallery();
    }

    /**
     * 自定义单选
     */
    private void openRadio() {
        RxGalleryFinal
                .with(MainActivity.this)
                .image()
                .radio()
                .cropAspectRatioOptions(0, new AspectRatio("3:3", 30, 10))
                .crop()
                .imageLoader(ImageLoaderType.FRESCO)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) {
                        Toast.makeText(getBaseContext(), "选中了图片路径："+imageRadioResultEvent.getImageCropEntity().getOriginalPath(), Toast.LENGTH_SHORT).show();
                    }
                })
                .openGallery();
    }

    /**
     * 视频多选回调
     */
    private void openVideoSelectMultiMethod(int type) {
        switch (type) {
            case 0:

                //使用默认的参数
                RxGalleryFinalApi
                        .getInstance(this)
                        .setVDMultipleResultEvent(
                                new RxBusResultDisposable<ImageMultipleResultEvent>() {
                                    @Override
                                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) {
                                        Logger.i("多选视频的回调");
                                    }
                                }).open();

                break;
            case 1:

                //使用自定义的参数
                RxGalleryFinalApi
                        .getInstance(this)
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_VIDEO, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_MULTI)
                        .setVDMultipleResultEvent(
                                new RxBusResultDisposable<ImageMultipleResultEvent>() {
                                    @Override
                                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) {
                                        Logger.i("多选视频的回调");
                                    }
                                }).open();

                break;
            case 2:

                //直接打开
                RxGalleryFinalApi
                        .openMultiSelectVD(this, new RxBusResultDisposable<ImageMultipleResultEvent>() {
                            @Override
                            protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) {
                                Logger.i("多选视频的回调");
                            }
                        });

                break;
        }
    }

    /**
     * 视频单选回调
     */
    private void openVideoSelectRadioMethod() {
        RxGalleryFinalApi
                .getInstance(MainActivity.this)
                .setType(RxGalleryFinalApi.SelectRXType.TYPE_VIDEO, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_RADIO)
                .setVDRadioResultEvent(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) {
                        Toast.makeText(getApplicationContext(), imageRadioResultEvent.getImageCropEntity().getOriginalPath(), Toast.LENGTH_SHORT).show();
                    }
                })
                .open();
    }

    /**
     * OPEN 图片多选实现方法
     * <p>
     * 默认使用 第三个 ，如果运行sample,可自行改变Type，运行Demo查看效果
     */
    private void openImageSelectMultiMethod(int type) {
        switch (type) {
            case 0:

                //使用默认的参数
                RxGalleryFinalApi
                        .getInstance(MainActivity.this)
                        .setImageMultipleResultEvent(
                                new RxBusResultDisposable<ImageMultipleResultEvent>() {
                                    @Override
                                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) {
                                        Logger.i("多选图片的回调");
                                    }
                                }).open();

                break;
            case 1:

                //使用自定义的参数
                RxGalleryFinalApi
                        .getInstance(MainActivity.this)
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_IMAGE, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_MULTI)
                        .setImageMultipleResultEvent(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                            @Override
                            protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) {
                                Logger.i("多选图片的回调");
                            }
                        }).open();

                break;
            case 2:

                //直接打开
                RxGalleryFinalApi.openMultiSelectImage(this, new RxBusResultDisposable<ImageMultipleResultEvent>() {
                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) {
                        Logger.i("多选图片的回调");
                    }
                });

                break;
        }

    }

    /**
     * OPEN 图片单选实现方法
     * <p>
     * 默认使用 第三个 ，如果运行sample,可自行改变Type，运行Demo查看效果
     */
    private void openImageSelectRadioMethod(int type) {
        RxGalleryFinalApi instance = RxGalleryFinalApi.getInstance(MainActivity.this);
        switch (type) {
            case 0:

                //打开单选图片，默认参数
                instance
                        .setImageRadioResultEvent(new RxBusResultDisposable<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) {
                                Logger.i("单选图片的回调");
                            }
                        }).open();

                break;
            case 1:

                //设置自定义的参数
                instance
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_IMAGE, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_RADIO)
                        .setImageRadioResultEvent(new RxBusResultDisposable<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) {
                                Logger.i("单选图片的回调");
                            }
                        }).open();

                break;
            case 2:

                //快速打开单选图片,flag使用true不裁剪
                RxGalleryFinalApi
                        .openRadioSelectImage(MainActivity.this, new RxBusResultDisposable<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent o) {
                                Logger.i("单选图片的回调");
                            }
                        }, true);

                break;
            case 3:

                //单选，使用RxGalleryFinal默认设置，并且带有裁剪
                instance
                        .openGalleryRadioImgDefault(
                                new RxBusResultDisposable<ImageRadioResultEvent>() {
                                    @Override
                                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) {
                                        Logger.i("只要选择图片就会触发");
                                    }
                                })
                        .onCropImageResult(
                                new IRadioImageCheckedListener() {
                                    @Override
                                    public void cropAfter(@NotNull Object t) {
                                        Logger.i("裁剪完成");
                                    }

                                    @Override
                                    public boolean isActivityFinish() {
                                        Logger.i("返回false不关闭，返回true则为关闭");
                                        return true;
                                    }
                                });

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SimpleRxGalleryFinal.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RxGalleryFinalApi.TAKE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            Logger.i("拍照OK，图片路径:" + RxGalleryFinalApi.fileImagePath.getPath());
//            //刷新相册数据库
//            RxGalleryFinalApi.openZKCameraForResult(MainActivity.this, new MediaScanner.ScanCallback() {
//                @Override
//                public void onScanCompleted(String[] strings) {
//                    Logger.i(String.format("拍照成功,图片存储路径:%s", strings[0]));
//                    Logger.d("演示拍照后进行图片裁剪，根据实际开发需求可去掉上面的判断");
//                    RxGalleryFinalApi.cropScannerForResult(MainActivity.this, RxGalleryFinalApi.getModelPath(), strings[0]);//调用裁剪.RxGalleryFinalApi.getModelPath()为默认的输出路径
//                }
//            });
//        } else {
//            Logger.i("失敗");
//        }
    }
}