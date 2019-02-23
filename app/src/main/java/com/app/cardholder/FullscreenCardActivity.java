package com.app.cardholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Activity class for displaying barcode in fullscreen
 *
 */
public class FullscreenCardActivity extends AppCompatActivity {

    private ConstraintLayout fullscreenConstraintLayout;
    private TextView cardNameEditText;
    private ImageView barCodeImageView=null;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_fullscreen_card);
        fullscreenConstraintLayout=findViewById(R.id.fullscreenConstraintLayout);
        barCodeImageView=findViewById(R.id.barCodeImageView);
        cardNameEditText=findViewById(R.id.cardNameEditText);
        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        String barcode=intent.getStringExtra("barcode");
        String color=intent.getStringExtra("color");
        cardNameEditText.setText(name);
        fullscreenConstraintLayout.setBackgroundColor(Color.parseColor(color));
        String barCode,barCodeType;
        String temp[] = barcode.split(" /| ");
        barCodeType=temp[0];
        barCode=temp[2];

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            int pxHeight=dipToPixels(getApplicationContext(),300);
            int pxWidth=dipToPixels(getApplicationContext(),500);
            BarcodeFormat format=BarcodeFormat.valueOf(barCodeType);
            BitMatrix bitMatrix = multiFormatWriter.encode(barCode,format,pxWidth,pxHeight);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void onEscapeFullscreenImageView(View view){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}

    }

    /**
     * Convert dip value to pixel value
     *
     * @param context - app context
     * @param dipValue - dip value
     * @return pixel value
     */
    public static int dipToPixels(Context context,int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

}
