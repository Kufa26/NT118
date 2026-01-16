package com.example.cashmate.account;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CustomCredential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.cashmate.MainActivity;
import com.example.cashmate.R;
import com.example.cashmate.database.User.User;
import com.example.cashmate.database.User.UserHandle;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin, btnGoogle;
    ImageButton btnBack;
    ImageView btnShowPassword;
    TextView tvSignup, tvForgotPassword;

    UserHandle userHandle;
    boolean isShowPassword = false;

    private FirebaseAuth auth;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        auth = FirebaseAuth.getInstance();
        buttonn();

        credentialManager = CredentialManager.create(this);
        btnGoogle.setOnClickListener(v -> startGoogleSignIn());

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {

                        String uid = auth.getCurrentUser().getUid();
                        userHandle.updatePassword(uid, password);
                        getSharedPreferences("USER_SESSION", MODE_PRIVATE)
                                .edit()
                                .putString("email", email)
                                .apply();

                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("open_menu", true);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Sai email hoặc mật khẩu",
                                Toast.LENGTH_SHORT).show();
                    });
        });

    }

    public void buttonn() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        tvSignup = findViewById(R.id.tv_Signup);
        btnGoogle = findViewById(R.id.btnGoogle);
        userHandle = new UserHandle(this);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        if (btnShowPassword != null && edtPassword != null) {
            final boolean[] isShow = {false};
            btnShowPassword.setOnClickListener(v -> {
                if (isShow[0]) {
                    edtPassword.setInputType(
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                    btnShowPassword.setImageResource(R.drawable.ic_eye_close);
                } else {
                    edtPassword.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    btnShowPassword.setImageResource(R.drawable.ic_eye_open);
                }
                edtPassword.setSelection(edtPassword.getText().length());
                isShow[0] = !isShow[0];
            });
        }

        tvForgotPassword.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            forgotPassword(email);
        });

        tvSignup.setOnClickListener(v ->
                startActivity(new Intent(Login.this, SignUp.class))
        );
    }

    private void startGoogleSignIn() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false).setServerClientId(getString(R.string.default_web_client_id)).build();
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();

        credentialManager.getCredentialAsync(this,request,null,ContextCompat.getMainExecutor(this),new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse response) {
                        handleGoogleResult(response);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        Toast.makeText(Login.this,"Google login failed: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void handleGoogleResult(GetCredentialResponse response) {
        Credential credential = response.getCredential();

        if (credential instanceof CustomCredential) {
            CustomCredential custom = (CustomCredential) credential;

            if (TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(custom.getType())) {
                GoogleIdTokenCredential googleCred = GoogleIdTokenCredential.createFrom(custom.getData());

                String idToken = googleCred.getIdToken();

                // FIREBASE SIGN IN
                AuthCredential firebaseCredential =
                        GoogleAuthProvider.getCredential(idToken, null);

                auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                String email = auth.getCurrentUser().getEmail();
                                String uid = auth.getCurrentUser().getUid();

                                // LƯU SESSION
                                getSharedPreferences("USER_SESSION", MODE_PRIVATE).edit().putString("email", email).apply();

                                // INSERT SQLITE NẾU CHƯA CÓ
                                if (!userHandle.isUserExist(email)) {
                                    User user = new User(
                                            uid,
                                            null,       // fullName
                                            email,
                                            "google",   // password
                                            null,
                                            null,
                                            null,
                                            null,
                                            null
                                    );
                                    userHandle.insertUserFromFirebase(
                                            uid,
                                            null,           // fullName
                                            email,
                                            "google",       // password
                                            null            // avatarUrl
                                    );
                                }

                                Toast.makeText(Login.this,"Firebase login success: " + auth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("open_menu", true);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(Login.this,"Firebase login failed: " + task.getException(),Toast.LENGTH_LONG).show();
                            }
                        });

                return;
            }
        }
        Toast.makeText(this, "Không nhận được Google ID Token", Toast.LENGTH_SHORT).show();
    }

    private void forgotPassword(String email) {
        if (email.isEmpty()) {
            Toast.makeText(Login.this,
                    "Vui lòng nhập email",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
                    Toast.makeText(Login.this,"Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra email.",Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Login.this,"Email không tồn tại hoặc không hợp lệ",Toast.LENGTH_SHORT).show();
                });
    }



}
