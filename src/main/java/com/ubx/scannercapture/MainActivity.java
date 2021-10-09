package com.ubx.scannercapture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText showScanResult;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showScanResult = (EditText) findViewById(R.id.scan_result);
        imageView = (ImageView) findViewById(R.id.imageView1);
    }

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action  = intent.getAction();
            Log.d("DECODE", "action " + action);
            if(action.equals("scanner_capture_image_result")) {
                byte[] bmp = intent.getByteArrayExtra("bitmapBytes");
                if(bmp != null)
                    Log.d("DECODE", "size " + bmp.length);
                if(bmp != null && bmp.length > 1) {
                    //showScanResult.setText("size " + bmp.length);
                    // Display image
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bmp, 0, bmp.length).copy(Bitmap.Config.RGB_565, true);
                    int[] bounds = intent.getIntArrayExtra("bounds");
                    if(bounds != null) {
                        Log.d("DECODE", "bounds size " + bounds.length);
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        paint.setColor(getResources().getColor(R.color.colorAccent));
                        paint.setStrokeWidth(10.0f);
                        paint.setStyle(Paint.Style.STROKE);
                        //Rect border = new Rect(iCropLeft, iCropTop, iCropWidth, iCropHeight);
                        Rect border = new Rect(2, 2, bitmap.getWidth() - 2, bitmap.getHeight() - 2);
                        canvas.drawRect(border, paint);
                        paint.setColor(getResources().getColor(R.color.result_points));
                        paint.setStrokeWidth(15.0f);
                        //canvas.drawPoint(bundsbuf[8], bundsbuf[9], paint);
                        canvas.drawPoint(bounds[0], bounds[1], paint);
                        paint.setColor(getResources().getColor(R.color.possible_result_points));
                        canvas.drawPoint(bounds[2], bounds[3], paint);
                        paint.setColor(getResources().getColor(R.color.colorAccent));
                        canvas.drawPoint(bounds[4], bounds[5], paint);
                        paint.setColor(getResources().getColor(R.color.colorPrimary));
                        canvas.drawPoint(bounds[6], bounds[7], paint);
                        if(bounds.length == 10 && bounds[8] > 0 && bounds[9] > 0) {
                            paint.setColor(getResources().getColor(R.color.purple_500));
                            canvas.drawPoint(bounds[8], bounds[9], paint);
                        }
                    }
                    imageView.setImageBitmap(bitmap);
                }else {
                    Toast.makeText(MainActivity.this, "获取图片失败 " , Toast.LENGTH_SHORT).show();
                }
            } else if("android.intent.ACTION_DECODE_DATA".equals(action)){
                //showScanResult.setText("barcode= " + intent.getIntExtra("barcodeType", 0));
                Bundle bundle = intent.getExtras();
                Intent intentImage = new Intent("action.scanner_capture_image");
                //intentImage.putExtra("saveLastDecImage",true);//42T存图片
                //intentImage.putExtra("rotate",180);
                //default g_nImageWidth= 832/2 g_nImageHeight= 640/2
                //图片太大影响数据传输
                /*intentImage.putExtra("ImageWidth",1280);
                intentImage.putExtra("ImageHeight",800);*/
                byte[] bytes = intent.getByteArrayExtra("barcode");
                if(bytes != null)
                showScanResult.setText(new String(bytes));
                //sendBroadcast(intentImage);
            }

        }

    };
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.ACTION_DECODE_DATA");
        filter.addAction("scanner_capture_image_result");
        registerReceiver(mScanReceiver, filter);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Log.d("DECODE", "onKeyUp====================="+keyCode);
        if(keyCode == 522 || keyCode == 520|| keyCode == 521|| keyCode == 120) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intentImage = new Intent("action.scanner_capture_image");
                    //intentImage.putExtra("saveLastDecImage",true);//42T存图片
                    //intentImage.putExtra("rotate",180);
                    //default g_nImageWidth= 832/2 g_nImageHeight= 640/2
                    //图片太大影响数据传输
                    /*intentImage.putExtra("ImageWidth",1280);
                    intentImage.putExtra("ImageHeight",800);*/
                    sendBroadcast(intentImage);
                }
            }, 50);
        }
        return super.onKeyUp(keyCode, event);
    }
    Handler handler = new Handler();
}