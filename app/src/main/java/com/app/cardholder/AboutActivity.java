package com.app.cardholder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.marcoscg.licenser.Library;
import com.marcoscg.licenser.License;
import com.marcoscg.licenser.LicenserDialog;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

/**
 * AboutActivity is activity class for showing about program information
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.drawable.helix)
                .setCover(R.mipmap.profile_cover)
                .setName(R.string.devName)
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
//                .addGooglePlayStoreLink("XXX")
                .addGitHubLink(R.string.githubLink)
//                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
//                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .addEmailLink(R.string.devName)
                .addLicenseAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLicense();
                    }
                })
//                .addMoreFromMeAction("TODO")
                .setSubTitle(R.string.subTitle)
                .build();
        addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
    }

    /**
     * Show license dialog (for Apache License)
     */
    private void showLicense() {
        new LicenserDialog(this)
                .setTitle(R.string.licenseTitle)
                .setLibrary(new Library(
                        getResources().getString(R.string.app_name),
                        getResources().getString(R.string.githubRepoLink),
                        License.APACHE))
                .setLibrary(new Library(
                        getResources().getString(R.string.MaterialTextField),
                        getResources().getString(R.string.MaterialTextFieldLink),
                        License.APACHE))
                .setLibrary(new Library(
                        getResources().getString(R.string.MaterialTapTargetPrompt),
                        getResources().getString(R.string.MaterialTapTargetPromptLink),
                        License.APACHE))
                .setLibrary(new Library(
                        getResources().getString(R.string.AppIntro),
                        getResources().getString(R.string.AppIntroLink),
                        License.APACHE))
                .setLibrary(new Library(
                        getResources().getString(R.string.ViewTooltip),
                        getResources().getString(R.string.ViewTooltipLink),
                        License.APACHE))
                .setLibrary(new Library(
                        getResources().getString(R.string.CodeScanner),
                        getResources().getString(R.string.CodeScannerLink),
                        License.MIT))
                .setLibrary(new Library(
                        getResources().getString(R.string.ZXingAndroidEmbedded),
                        getResources().getString(R.string.ZXingAndroidEmbeddedLink),
                        License.APACHE))
                .setLibrary(new Library(
                        getResources().getString(R.string.ColorPicker),
                        getResources().getString(R.string.ColorPickerLink),
                        License.APACHE))
                .setLibrary(new Library(
                        getResources().getString(R.string.MaterialAbout),
                        getResources().getString(R.string.MaterialAboutLink),
                        License.MIT))
                .setLibrary(new Library(
                        getResources().getString(R.string.ChocoBar),
                        getResources().getString(R.string.ChocoBarLink),
                        License.MIT))
                .setLibrary(new Library(
                        getResources().getString(R.string.Licenser),
                        getResources().getString(R.string.LicenserLink),
                        License.MIT))
                .setLibrary(new Library(
                        getResources().getString(R.string.NoNet),
                        getResources().getString(R.string.NoNetLink),
                        License.APACHE))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
}
