package com.app.cardholder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
 * SignUpActivity is activity class for user sign up action
 */
public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText = null;
    private EditText passwordEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * LoginTextView's on click handler for launching LoginActivity
     *
     * @param view - View object
     */
    public void onLoginTextViewClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean firstrun = getIntent().getBooleanExtra("firstrun", false);
        if (firstrun == true) {
            showTapTargetPrompt();
        }
    }

    /**
     * SignUpButton's on click handler for sign up action
     *
     * @param view - View object
     */
    public void onSignUpButtonClick(View view) {
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
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification();
                                    final AlertDialog.Builder alertDialogBuilder
                                            = new AlertDialog.Builder(SignUpActivity.this);
                                    alertDialogBuilder.setTitle(R.string.success)
                                            .setMessage(
                                                    getResources().getString(R.string.emailConfirm))
                                            .setPositiveButton(
                                                    android.R.string.yes,
                                                    new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(
                                                            SignUpActivity.this,
                                                            LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .show();
                                    ChocoBar.builder().setActivity(SignUpActivity.this)
                                            .setText(
                                                    getResources().getString(R.string.welcome)
                                                            + user.getEmail())
                                            .setDuration(ChocoBar.LENGTH_SHORT)
                                            .green()
                                            .show();
                                } else {
                                    ChocoBar.builder().setActivity(SignUpActivity.this)
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
     * Show TapTarget dialog for SignUpActivity
     */
    private void showTapTargetPrompt() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MaterialTapTargetPrompt.Builder(SignUpActivity.this)
                        .setTarget(R.id.signUpImageView)
                        .setPrimaryText(getResources().getString(R.string.signup))
                        .setSecondaryText(getResources().getString(R.string.signUpContent))
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .showFor(3000);

            }
        }, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MaterialTapTargetPrompt.Builder(SignUpActivity.this)
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
                new MaterialTapTargetPrompt.Builder(SignUpActivity.this)
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
                new MaterialTapTargetPrompt.Builder(SignUpActivity.this)
                        .setTarget(R.id.signUpButton)
                        .setPrimaryText(getResources().getString(R.string.signUpButton))
                        .setSecondaryText(getResources().getString(R.string.signUpButtonContent))
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .showFor(3000);
            }
        }, 9000);
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

}
