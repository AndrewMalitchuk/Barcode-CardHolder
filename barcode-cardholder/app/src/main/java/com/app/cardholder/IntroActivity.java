package com.app.cardholder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

/**
 * Class for launching app's intro
 *
 */
public class IntroActivity extends AppIntro {

    private View decorView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle(getResources().getString(R.string.sliderPageTitle1));
        sliderPage1.setDescription(getResources().getString(R.string.sliderPageDescription1));
        sliderPage1.setImageDrawable(R.drawable.ic_credit_card_black_24dp);
        sliderPage1.setBgColor(Color.parseColor("#FFC400"));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getResources().getString(R.string.sliderPageTitle2));
        sliderPage2.setDescription(getResources().getString(R.string.sliderPageDescription2));
        sliderPage2.setImageDrawable(R.drawable.ic_barcode);
        sliderPage2.setBgColor(Color.parseColor("#00e676"));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getResources().getString(R.string.sliderPageTitle3));
        sliderPage3.setDescription(getResources().getString(R.string.sliderPageDescription3));
        sliderPage3.setImageDrawable(R.drawable.ic_camera_enhance_black_24dp);
        sliderPage3.setBgColor(Color.parseColor("#2196f3"));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle(getResources().getString(R.string.sliderPageTitle4));
        sliderPage4.setDescription(getResources().getString(R.string.sliderPageDescription4));
        sliderPage4.setImageDrawable(R.drawable.ic_barcode_scanner);
        sliderPage4.setBgColor(Color.parseColor("#7c4dff"));
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        setBarColor(Color.parseColor("#263238"));
        setSeparatorColor(Color.parseColor("#FFFFFF"));
        setProgressButtonEnabled(true);
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(this, LoginActivity.class);
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
