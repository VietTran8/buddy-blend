package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Base64;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.business.AuthBusiness;
import vn.edu.tdtu.buddyblend.business.UserBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.ActivityLoginBinding;
import vn.edu.tdtu.buddyblend.dto.response.RegistrationIdResDTO;
import vn.edu.tdtu.buddyblend.dto.response.SignInResponseData;
import vn.edu.tdtu.buddyblend.utils.KeyBoardUtils;
import vn.edu.tdtu.buddyblend.utils.PasswordEncoder;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener,
        CompoundButton.OnCheckedChangeListener {
    private ActivityLoginBinding binding;
    private AuthBusiness authBusiness;
    private SharedPreferences sharedPreferences;
    private UserBusiness userBusiness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void initObjects(){
        userBusiness = UserBusiness.getInstance();
        authBusiness = AuthBusiness.getInstance();
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
    }

    private void init(){
        initObjects();
        loggedInFilter();

        binding.btnSignUp.setOnClickListener(this);
        binding.mainLayout.setOnTouchListener(this);
        binding.btnInputType.setOnCheckedChangeListener(this);
        binding.btnSignIn.setOnClickListener(this);

        binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private boolean validate(){
        String password = binding.edtPassword.getText().toString();
        String email = binding.edtEmail.getText().toString();

        if(password.isEmpty() || email.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void saveUserSharedPrefs(SignInResponseData signInResponse){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SharedPreferenceKeys.USER_ACCESS_TOKEN, signInResponse.getTokenType() + " " + signInResponse.getToken());
        editor.putString(SharedPreferenceKeys.USER_ID, signInResponse.getId());
        editor.putString(SharedPreferenceKeys.USER_EMAIL, signInResponse.getUsername());
        editor.putString(SharedPreferenceKeys.USER_NAME, signInResponse.getUserFullName());
        editor.putString(SharedPreferenceKeys.USER_IMAGE, signInResponse.getUserAvatar());

        editor.apply();
    }

    private void loggedInFilter(){
        String accessToken = sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, null);
        if(accessToken != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnSignUp){
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        }else if(view == binding.btnSignIn){
            if(validate()){
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Chờ một xíu...");
                progressDialog.show();

                String email = binding.edtEmail.getText().toString();
                String password = binding.edtPassword.getText().toString();
                password = PasswordEncoder.md5Hash(password);

                authBusiness.loginUser(email, password, new ActionCallback<SignInResponseData>() {
                    @Override
                    public void onSuccess(SignInResponseData signInResponse) {
                        saveUserSharedPrefs(signInResponse);
                        saveUserRegistrationId(signInResponse.getTokenType() + " " +signInResponse.getToken(), progressDialog);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }
    }

    private void saveUserRegistrationId(String accessToken, ProgressDialog progressDialog) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM_TOKEN_MSG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        userBusiness.saveUserRegistrationId(accessToken, token, new ActionCallback<RegistrationIdResDTO>() {
                            @Override
                            public void onSuccess(RegistrationIdResDTO dto) {
                                progressDialog.dismiss();
                                Log.d("REGISTRATION_ID_RESULT", "onSuccess: " + dto.getRegistrationId());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            @Override
                            public void onFailure(Integer status, String message) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                                Log.d("REGISTRATION_ID_RESULT", "onFailure: " + message);
                            }
                        });
                    }
                });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view == binding.mainLayout){
            KeyBoardUtils.hideKeyboard(LoginActivity.this);
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton == binding.btnInputType){
            if(b){
                binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.btnInputType.setBackgroundDrawable(getDrawable(R.drawable.baseline_visibility_off_16));
            }
            else{
                binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.btnInputType.setBackgroundDrawable(getDrawable(R.drawable.baseline_visibility_16));
            }
        }
    }
}