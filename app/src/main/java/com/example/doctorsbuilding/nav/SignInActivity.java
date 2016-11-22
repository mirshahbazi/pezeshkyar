package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/18/2016.
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewFlipper viewFlipper;
    private Button forgetButton;
    private Button backButton;
    private Button btnSignIn;
    private EditText txtUserName;
    private EditText txtPassword;
    private SharedPreferences settings;
    AsyncCallLoginWS loginWS;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_in);
        settings = G.getSharedPreferences();
        initViews();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(loginWS != null){
            loginWS.cancel(true);
        }
    }

    private void initViews() {
        viewFlipper = (ViewFlipper) findViewById(R.id.login_viewFlipper);
        backButton = (Button) viewFlipper.findViewById(R.id.login_btnBack);
        forgetButton = (Button) viewFlipper.findViewById(R.id.login_btn_forget);
        btnSignIn = (Button) viewFlipper.findViewById(R.id.login_btn_signIn);
        txtUserName = (EditText) viewFlipper.findViewById(R.id.login_userName);
        txtPassword = (EditText) viewFlipper.findViewById(R.id.login_password);
        forgetButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_forget:
                showNext();
                break;
            case R.id.login_btnBack:
                showPrevious();
                break;
            case R.id.login_btn_signIn:
                signIn();
                break;
        }

    }

    private void signIn() {
        if(checkField()) {
            loginWS = new AsyncCallLoginWS();
            loginWS.execute();
        }
    }
    private boolean checkField(){
        if(txtUserName.getText().toString().trim().equals("")){
            new MessageBox(SignInActivity.this, "لطفا نام کاربری را وارد نمایید .").show();
            return false;
        }
        if(txtPassword.getText().toString().trim().equals("")){
            new MessageBox(SignInActivity.this, "لطفا کلمه عبور را وارد نمایید .").show();
            return false;
        }
        return true;
    }
    private void showPrevious() {
        viewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_left);
        viewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_right);
        viewFlipper.showNext();
    }

    private void showNext() {
        viewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_right);
        viewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_left);
        viewFlipper.showPrevious();
    }

    private class AsyncCallLoginWS extends AsyncTask<String, Void, Void> {
        private String result = null;
        private UserType userType = UserType.None;
        String msg = null;
        ProgressDialog dialog;
        String username, password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(SignInActivity.this, "", "در حال ارسال اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            username = txtUserName.getText().toString().trim();
            password = txtPassword.getText().toString().trim();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeNewLoginWS(username, password);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(SignInActivity.this, msg).show();
            } else {
                dialog.dismiss();
                if (result != null) {
                    if (result.toUpperCase().equals("OK")) {
//                        userType = UserType.values()[2];
//                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                        intent.putExtra("menu", userType);
//                        switch (userType) {
//                            case None:
//                                new MessageBox(SignInActivity.this, "نام کاربری یا کلمه عبور اشتباه می باشد .").show();
//                                break;
//                            case User:
//                                UserType.User.attachTo(intent);
//                                save();
//                                break;
//                            case Dr:
//                                UserType.Dr.attachTo(intent);
//                                save();
//                                break;
//                            case secretary:
//                                UserType.Dr.attachTo(intent);
//                                save();
//                                break;
//                            default:
//                                break;
                        save();

                    } else {
                        new MessageBox(SignInActivity.this, result).show();
                    }
                }
            }
        }

        private void save() {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("user", txtUserName.getText().toString());
            editor.putString("pass", txtPassword.getText().toString());
//            editor.putInt("role", 2);
            editor.apply();
            finish();
        }

    }
}
