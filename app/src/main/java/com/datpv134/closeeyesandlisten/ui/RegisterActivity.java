package com.datpv134.closeeyesandlisten.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.ActivityRegisterBinding;
import com.datpv134.closeeyesandlisten.model.MyUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
        });
    }

    private void registerUser() {
        String name = binding.etSignUpName.getText().toString().trim();
        String email = binding.etSignUpUserName.getText().toString().trim();
        String password = binding.etSignUpPassWord.getText().toString().trim();
        String rePassword = binding.etRePassWord.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etSignUpName.setError("H??? t??n kh??ng ???????c b??? tr???ng");
        } else if (!email.matches(emailPattern)) {
            binding.etSignUpUserName.setError("Email kh??ng h???p l???");
        } else if (password.isEmpty() || password.length() < 6) {
            binding.etSignUpPassWord.setError("M???t kh???u kh??ng h???p l??? (T??? 6 k?? t??? tr??? l??n)");
        } else if (!password.equals(rePassword)) {
            binding.etRePassWord.setError("M???t kh???u kh??ng kh???p");
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        MyUser myUser = new MyUser(name, email, password, "https://scontent.fhan2-1.fna.fbcdn.net/v/t39.30808-6/292177621_1485404865228560_6062959396408473695_n.jpg?_nc_cat=100&ccb=1-7&_nc_sid=174925&_nc_ohc=xX0420A3tVMAX__FW2j&_nc_ht=scontent.fhan2-1.fna&oh=00_AfAoy7LxiL9ne4g4uvqmfbcXfv3zJQNg957RyWCrlTQL9w&oe=637CA967", "-1;", "");
                        String id = task.getResult().getUser().getUid();
                        database.getReference().child("Users").child(id).setValue(myUser);

                        Toast.makeText(getBaseContext(), "????ng k?? t??i kho???n th??nh c??ng", Toast.LENGTH_SHORT);
                        sendUserToNextActivity();
                    } else {
                        Toast.makeText(getBaseContext(), "C?? l???i x???y ra, vui l??ng th??? l???i sau", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}