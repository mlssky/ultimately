package com.xcleans;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.LinkedList;

public class MainActivity extends Activity {

    private static final String        TAG = "MMMMM";
    private              CameraManager cameraManager;
    String cameraId = null;
    private ImageReader mImageReader;
    private int         mRoate;

    private void requestPermissionsV2(String[] strArr) {
        try {
//            Intent intent = new Intent("huawei.intent.action.REQUEST_PERMISSIONS");
//            intent.setPackage("com.huawei.systemmanager");
//            intent.putExtra("KEY_HW_PERMISSION_ARRAY", strArr);
//            intent.putExtra("KEY_HW_PERMISSION_PKG", getPackageName());

            Intent intent = new Intent("com.android.packageinstaller.permission.ui.GrantPermissionsActivity");
            intent.setPackage("com.android.packageinstaller");
            try {
                ActivityCompat.requestPermissions(this, strArr, 1357);
                startActivityForResult(intent, 1357);
                return;
            } catch (Exception e) {
            }
        } catch (Exception e2) {
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_actiivty);


        ViewGroup viewGroup = new FrameLayout(this, null);

        FrameLayout c1 = new FrameLayout(this, null);
        viewGroup.addView(c1);

        FrameLayout c2 = new FrameLayout(this, null);
        c2.addView(new FrameLayout(this, null));//c3
        c1.addView(c2);


        LinkedList<View> views = new LinkedList<>();
        views.offer(viewGroup);

        int h = 1;
        while (!views.isEmpty()) {
            for (int i = 0, n = views.size(); i < n; i++) {
                View top = views.poll();
                Log.d(TAG, "[[[" + top.toString());
                if (top instanceof ViewGroup && ((ViewGroup) top).getChildCount() >= 0) {
                    for (int j = 0; j < ((ViewGroup) top).getChildCount(); j++) {
                        views.offer(((ViewGroup) top).getChildAt(j));
                    }
                }
            }
            h++;
        }

        Log.e(TAG, h + "====");

//        Camera sl;
//
//        CameraCharacteristics cameraCharacteristics = null;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
//            try {
//                String[] camIdList = cameraManager != null ? cameraManager.getCameraIdList() : new String[0];
//
//                for (int i = 0, n = camIdList.length; i < n; i++) {
//
//                    cameraCharacteristics = cameraManager.getCameraCharacteristics(camIdList[i]);
//                    //如果是前置摄像头
//                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
//                        cameraId = camIdList[i];
//                        mRoate = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
//                        break;
//                    }
//                }
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (cameraId != null) {
//            StreamConfigurationMap streamConfigurationMap = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//                Size[] sizes = streamConfigurationMap.getOutputSizes(SurfaceHolder.class);
//                //设置预览大小
//                Size mPreviewSize = sizes[0];
//                //imageReader初始化
//                mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.JPEG, 2);
//
//                mImageReader.setOnImageAvailableListener(reader -> {
//
//                    Image image = reader.acquireNextImage();
//                    int format = image.getFormat();
//                    Image.Plane[] planes = image.getPlanes();
//                    ByteBuffer bb = planes[0].getBuffer();
//                    byte[] data = new byte[bb.remaining()];
//                    bb.get(data);
//                    image.close();
//                }, null);
//
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//            }
//
//        }
//
//        SurfaceView surfaceView = findViewById(R.id.tt);
//        SurfaceHolder surfaceHolder = surfaceView.getHolder();
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            private CaptureRequest.Builder mPreviewBuilder;
//
//            @SuppressLint("MissingPermission")
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    try {
//                        cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
//                            @Override
//                            public void onOpened(@NonNull CameraDevice camera) {
//
//                                Surface surface = holder.getSurface();
//                                try {
//                                    // 设置捕获请求为预览，这里还有拍照啊，录像等
//                                    mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
//                                    mPreviewBuilder.addTarget(surface);
//                                    mPreviewBuilder.addTarget(mImageReader.getSurface());
//
//                                    camera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
//                                            new CameraCaptureSession.StateCallback() {
//
//                                        @Override
//                                        public void onConfigured(@NonNull CameraCaptureSession session) {
//                                            try {
//                                                session.setRepeatingRequest(mPreviewBuilder.build(), null, null);
//                                            } catch (CameraAccessException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//
//                                        }
//                                    }, null);
//                                } catch (CameraAccessException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onDisconnected(@NonNull CameraDevice camera) {
//
//                            }
//
//                            @Override
//                            public void onError(@NonNull CameraDevice camera, int error) {
//
//                            }
//                        }, null);
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });

//        requestPermissionsV2(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE});

    }
}
