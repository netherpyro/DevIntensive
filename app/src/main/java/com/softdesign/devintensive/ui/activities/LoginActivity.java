package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.login_login_button)
    Button mSignIn;
    @BindView(R.id.login_remember_label)
    TextView mRememberPassword;
    @BindView(R.id.login_email_et)
    EditText mLogin;
    @BindView(R.id.login_password_et)
    EditText mPassword;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_button:
                signIn();
                break;
            case R.id.login_remember_label:
                rememberPassword();
                break;
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccess(Response<UserModelRes> response) {
        mDataManager.getPreferencesManager().saveAuthToken(response.body().getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(response.body().getData().getUser().getId());

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void signIn() {
        Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
        call.enqueue(new Callback<UserModelRes>() {
            @Override
            public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                if (response.code() == 200) {
                    loginSuccess(response);
                } else if (response.code() == 404) {
                    showSnackBar("Invalid credentials");
                } else {
                    showSnackBar("Something going wrong");
                }
            }

            @Override
            public void onFailure(Call<UserModelRes> call, Throwable t) {
                //// TODO: 10.07.16 обработать ошибки ретрофита
            }
        });
    }
}
