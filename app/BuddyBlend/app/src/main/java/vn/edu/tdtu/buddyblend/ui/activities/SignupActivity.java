package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.A;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.business.AuthBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.ActivitySignupBinding;
import vn.edu.tdtu.buddyblend.dto.request.SignUpRequestDTO;
import vn.edu.tdtu.buddyblend.dto.response.SignUpResponseData;
import vn.edu.tdtu.buddyblend.utils.KeyBoardUtils;
import vn.edu.tdtu.buddyblend.utils.PasswordEncoder;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.StringUtils;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener,
        CompoundButton.OnCheckedChangeListener {
    private ActivitySignupBinding binding;
    private AuthBusiness authBusiness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void initObjects(){
        authBusiness = AuthBusiness.getInstance();
    }

    private void init(){
        initObjects();

        binding.txtSignIn.setOnClickListener(this);
        binding.mainLayout.setOnTouchListener(this);
        binding.btnInputType.setOnCheckedChangeListener(this);
        binding.btnSignUp.setOnClickListener(this);

        binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private boolean validate(){
        String firstName = binding.edtFirstName.getText().toString();
        String middleName = binding.edtMiddleName.getText().toString();
        String lastName = binding.edtLastName.getText().toString();
        String email  = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();
        String rePassword = binding.edtRePassword.getText().toString();
        RadioButton checkedButton = findViewById(binding.rdgGender.getCheckedRadioButtonId());
        String gender = checkedButton.getText().toString();

        if(firstName.isEmpty()
                || middleName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || rePassword.isEmpty()
                || gender.isEmpty()
        ){
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!StringUtils.isValidEmail(email)){
            Toast.makeText(this, "Vui lòng nhập email hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(rePassword)){
            Toast.makeText(this, "Nhập lại mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == binding.txtSignIn){
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if(view == binding.btnSignUp){
            if(validate()){
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Chờ một chút...");
                progressDialog.show();

                String firstName = binding.edtFirstName.getText().toString();
                String middleName = binding.edtMiddleName.getText().toString();
                String lastName = binding.edtLastName.getText().toString();
                String email  = binding.edtEmail.getText().toString();
                String password = binding.edtPassword.getText().toString();
                RadioButton checkedButton = findViewById(binding.rdgGender.getCheckedRadioButtonId());
                String gender = checkedButton.getText().toString();

                SignUpRequestDTO requestDTO = new SignUpRequestDTO();
                requestDTO.setEmail(email);
                requestDTO.setBio(null);
                requestDTO.setPassword(PasswordEncoder.md5Hash(password));
                requestDTO.setGender(gender);
                requestDTO.setFirstName(firstName);
                requestDTO.setLastName(lastName);
                requestDTO.setMiddleName(middleName);
                requestDTO.setProfilePicture(null);

                authBusiness.signUpUser(requestDTO, new ActionCallback<SignUpResponseData>() {
                    @Override
                    public void onSuccess(SignUpResponseData signUpResponseData) {
                        KeyBoardUtils.hideKeyboard(SignupActivity.this);
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        KeyBoardUtils.hideKeyboard(SignupActivity.this);
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        KeyBoardUtils.hideKeyboard(SignupActivity.this);
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view == binding.mainLayout){
            KeyBoardUtils.hideKeyboard(SignupActivity.this);
        }
        return false;
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