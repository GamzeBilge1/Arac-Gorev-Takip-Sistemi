package com.gamzebilge.myapplication55;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainActivity extends AppCompatActivity {
    EditText mailText, passwordText;
    CheckBox checkBox;
    ImageView showPassword;

    SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mailText = findViewById(R.id.mailText);
        showPassword = findViewById(R.id.showPassword);
        passwordText = findViewById(R.id.passwordText);
        checkBox = findViewById(R.id.checkBox);
        sharedPreferences = this.getSharedPreferences("com.gamzebilge.proje78", Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        boolean beniHatirla = sharedPreferences.getBoolean("beniHatirla", false);
        checkBox.setChecked(beniHatirla);
        if (beniHatirla) {
            String savedMail = sharedPreferences.getString("savedMail", "");
            mailText.setText(savedMail);
        }

        showPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordText.setInputType(129);
                showPassword.setImageResource(R.drawable.eye);
            } else {
                passwordText.setInputType(144); // textVisiblePassword
                showPassword.setImageResource(R.drawable.eyeoff);
            }
            isPasswordVisible = !isPasswordVisible;
            passwordText.setSelection(passwordText.length());
        });
    }

    public void giris(View view) {
        String mail = mailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (mail.isEmpty()) {
            showAlert("Hata", "Lütfen 'Mail' alanını boş bırakmayınız.");
        } else if (password.isEmpty()) {
            showAlert("Hata", "Lütfen 'Şifre' alanını boş bırakmayınız.");
        } else {
            mAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            boolean beniHatirla = checkBox.isChecked();

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (beniHatirla) {
                                editor.putString("savedMail", mail);
                                editor.putBoolean("beniHatirla", true);
                            } else {
                                editor.remove("savedMail");
                                editor.putBoolean("beniHatirla", false);
                            }
                            editor.apply();

                            Intent intent = new Intent(MainActivity.this, form.class);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                showAlert("Hata", "Kullanıcı bulunamadı.");
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                showAlert("Hata", "Lütfen giriş bilgilerinizi kontrol edin.");
                            } else {
                                showAlert("Hata", "Giriş başarısız: " + task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
              //  .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void kayitOl(View view) {
        Intent intent = new Intent(MainActivity.this, kayit.class);
        startActivity(intent);
    }
}
