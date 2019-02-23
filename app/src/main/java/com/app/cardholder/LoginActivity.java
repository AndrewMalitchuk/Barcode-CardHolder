package com.app.cardholder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pd.chocobar.ChocoBar;

import java.util.regex.Pattern;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

/**
 * LoginActivity is activity class for user login action
 */
public class LoginActivity extends AppCompatActivity {

    private SharedPreferences prefs = null;
    private Intent signUpActivity = null;
    private Intent cardListActivity = null;
    private EditText emailEditText = null;
    private EditText passwordEditText = null;
    private FirebaseAuth mAuth;

    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String STORAGE_PREF = "storage_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        prefs = getSharedPreferences("com.app.cardholder", MODE_PRIVATE);
        signUpActivity = new Intent(getApplicationContext(), SignUpActivity.class);
        cardListActivity = new Intent(getApplicationContext(), CardListActivity.class);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (getFromPref(this, ALLOW_KEY)) {
                showSettingsAlert();
            } else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showAlert();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_STORAGE);
                }
            }
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            boolean isNull = currentUser == null;
            String ans = new Boolean(isNull).toString();
            if (currentUser != null) {
                Intent intent = new Intent(this, CardListActivity.class);
                //I do not have any idea why putExtra doesn't works
                //I guess, it's 'cos it doesn't implements Serializable
                CardListActivity.auth = mAuth;
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).commit();
            signUpActivity.putExtra("firstrun", true);
            cardListActivity.putExtra("firstrun", true);
            showIntro();
            showTapTargetPrompt();
        }
    }

    /**
     * SignUpTextView's On click handler method for launching SignUpActivity
     *
     * @param view - View object
     */
    public void onSignUpTextViewClick(View view) {
        startActivity(signUpActivity);
    }


    /**
     * LoginButton's On click handler method for launching LoginActivity
     *
     * @param view - View object
     */
    public void onLoginButtonClick(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.equals("") || email.length() == 0 || email.equals(" ") || !checkEmail(email)) {
            ViewTooltip
                    .on(emailEditText)
                    .autoHide(true, 5000)
                    .clickToHide(false)
                    .corner(30)
                    .color(Color.parseColor("#263238"))
                    .align(ViewTooltip.ALIGN.CENTER)
                    .position(ViewTooltip.Position.TOP)
                    .text(getResources().getString(R.string.emailValidation))
                    .show();
        } else {
            if (password.equals("") || password.equals(" ") || password.length() < 6) {
                ViewTooltip
                        .on(passwordEditText)
                        .autoHide(true, 5000)
                        .clickToHide(false)
                        .corner(30)
                        .color(Color.parseColor("#263238"))
                        .align(ViewTooltip.ALIGN.CENTER)
                        .position(ViewTooltip.Position.TOP)
                        .text(getResources().getString(R.string.passwordValidation))
                        .show();
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    CardListActivity.auth = mAuth;
                                    startActivity(cardListActivity);
                                } else {
                                    ChocoBar.builder().setActivity(LoginActivity.this)
                                            .setText(task.getException().getMessage())
                                            .setDuration(ChocoBar.LENGTH_INDEFINITE)
                                            .setActionText(android.R.string.ok)
                                            .red()
                                            .show();
                                }

                            }
                        });
            }
        }
    }

    /**
     * loginForgotPasswordTextView's On click handler method for sending password reset letter
     *
     * @param view - View object
     */
    public void onLoginForgotPasswordTextViewClick(View view) {
        String email = emailEditText.getText().toString();
        if (email.equals("") || email.length() == 0 || email.equals(" ") || !checkEmail(email)) {
            ViewTooltip
                    .on(emailEditText)
                    .autoHide(true, 5000)
                    .clickToHide(false)
                    .corner(30)
                    .color(Color.parseColor("#263238"))
                    .align(ViewTooltip.ALIGN.CENTER)
                    .position(ViewTooltip.Position.TOP)
                    .text(getResources().getString(R.string.emailValidation))
                    .show();
        } else {
            mAuth.sendPasswordResetEmail(email);
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle(R.string.loginForgotPasswordDialogWindowTitle)
                    .setMessage(R.string.loginForgotPasswordDialogWindowContent)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * Show TapTarget dialog for LoginActivity
     */
    private void showTapTargetPrompt() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MaterialTapTargetPrompt.Builder(LoginActivity.this)
                        .setTarget(R.id.loginImageView)
                        .setPrimaryText(getResources().getString(R.string.login))
                        .setSecondaryText(getResources().getString(R.string.loginTapTarget))
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .showFor(3000);
            }
        }, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MaterialTapTargetPrompt.Builder(LoginActivity.this)
                        .setTarget(R.id.emailMaterailTextField)
                        .setPrimaryText(getResources().getString(R.string.emailField))
                        .setSecondaryText(getResources().getString(R.string.emailTapTarget))
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .showFor(3000);
            }
        }, 3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MaterialTapTargetPrompt.Builder(LoginActivity.this)
                        .setTarget(R.id.passwordMaterailTextField)
                        .setPrimaryText(getResources().getString(R.string.passwordField))
                        .setSecondaryText(getResources().getString(R.string.passwordTapTarget))
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .showFor(3000);
            }
        }, 6000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MaterialTapTargetPrompt.Builder(LoginActivity.this)
                        .setTarget(R.id.loginButton)
                        .setPrimaryText(getResources().getString(R.string.loginButton))
                        .setSecondaryText(getResources().getString(R.string.loginButtonTapTarget))
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .showFor(3000);
            }
        }, 9000);
    }

    /**
     * Show app intro activity
     */
    private void showIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    /**
     * Pattern for checking correct email address
     */
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    /**
     * Checking email address
     *
     * @param email - email address
     * @return true if email address is correct
     */
    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (STORAGE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (STORAGE_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    /**
     * Show storage access dialog
     */
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(getResources().getString(R.string.storagePermission));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_STORAGE);
                    }
                });
        alertDialog.show();
    }

    /**
     * Show storage alert access dialog
     */
    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(getResources().getString(R.string.storagePermission));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(LoginActivity.this);

                    }
                });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale
                                        (this, permission);
                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            saveToPreferences(LoginActivity.this, ALLOW_KEY, true);
                        }
                    }
                }
            }
        }
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }
}
