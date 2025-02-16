package com.gamzebilge.myapplication55;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.regex.Pattern;

public class kayit extends AppCompatActivity {
    EditText mailText, passwordText;
    Button kaydetButton;
    FirebaseAuth mAuth;
    ImageView showPassword;
    boolean isPasswordVisible = false;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kayit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mailText = findViewById(R.id.mailText);
        passwordText = findViewById(R.id.passwordText);
        kaydetButton = findViewById(R.id.kaydetbutton);
        showPassword = findViewById(R.id.showPassword);
        mAuth = FirebaseAuth.getInstance();

        kaydetButton.setOnClickListener(v -> kaydet());
        showPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPassword.setImageResource(R.drawable.eye);
            } else {
                passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordText.setTransformationMethod(null);
                showPassword.setImageResource(R.drawable.eyeoff);
            }
            isPasswordVisible = !isPasswordVisible;
            passwordText.setSelection(passwordText.length());
        });
    }

    private void kaydet() {
        String email = mailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Lütfen tüm alanları doldurun.");
            return;
        }

        if (!isEmailValid(email)) {
            showAlert("Lütfen e-posta adresinizi doğru formatta giriniz.");
            return;
        }

        if (!isPasswordValid(password)) {
            showAlert("Şifre en az 5 karakter uzunluğunda, en az bir büyük harf, bir küçük harf ve bir sayı içermelidir.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        showAlert("Kayıt başarılı!");
                        Log.d("Kayıt", "Kullanıcı başarıyla oluşturuldu.");
                        // Kullanıcıyı MainActivity sayfasına yönlendirin
                        Intent intent = new Intent(kayit.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            showAlert("Bu e-posta adresi zaten kayıtlı.");
                            Log.d("Kayıt", "E-posta adresi zaten kayıtlı: " + email);
                        } else {
                            showAlert("Kayıt başarısız: " + task.getException().getMessage());
                            Log.d("Kayıt", "Kayıt hatası: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 5) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasUppercase && hasLowercase && hasDigit;
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Tamam", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
