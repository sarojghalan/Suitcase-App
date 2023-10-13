package np.com.sarojghalan.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

import np.com.sarojghalan.suitcase.dashboard.DashboardActivity;
import np.com.sarojghalan.suitcase.db.SuitcaseDatabase;
import np.com.sarojghalan.suitcase.db.user.User;
import np.com.sarojghalan.suitcase.db.user.UserDao;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailTextField, passwordTextField;
    private EditText etEmail, etPassword;
    private MaterialButton materialButtonLogin;
    private ProgressDialog progressDialog;

    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextField = findViewById(R.id.til_login_email);
        etEmail = emailTextField.getEditText();
        passwordTextField = findViewById(R.id.til_login_password);
        etPassword = passwordTextField.getEditText();

        materialButtonLogin = findViewById(R.id.mb_login);

        registerLink = findViewById(R.id.register_link);

        progressDialog = new ProgressDialog(this);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        LoginActivity.this,
                        RegistrationActivity.class
                );
                startActivity(intent);
            }
        });

        materialButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 validateLogin();
            }
        });
    }

    private void validateLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextField.setError("Please enter a valid email");
        } else if (password.isEmpty()) {
            passwordTextField.setError("Please enter a valid password");
        } else {
            progressDialog.setMessage("Logging in user...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    authenticateUserInDatabase(email, password);
                }
            }, 2000);
        }
    }

    private void authenticateUserInDatabase(String email, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SuitcaseDatabase suitcaseDatabase = SuitcaseDatabase
                            .getInstance(getApplicationContext());
                    UserDao userDao = suitcaseDatabase.getUserDao();
                    User user = userDao.getUserByLoginCredentials(
                            email.toLowerCase(Locale.ROOT),
                            password
                    );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleLoginResultFromDatabase(user);
                        }
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleFailureResultFromDatabase();
                        }
                    });
                }
            }
        }).start();
    }

    private void handleLoginResultFromDatabase(User user) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (user != null) {
            Toast.makeText(
                    LoginActivity.this,
                    "Login Success",
                    Toast.LENGTH_LONG
            ).show();
            saveUserLoggedStateInSharedPreferences(user);
            startDashboardActivity();
        } else {
            Toast.makeText(
                    LoginActivity.this,
                    "User doesn't exist...",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void saveUserLoggedStateInSharedPreferences(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                "user_pref",
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("logged_user_email", user.email);
        editor.putString("logged_user_full_name", user.fullName);
        editor.putString("logged_user_address", user.address);
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    private void startDashboardActivity() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleFailureResultFromDatabase() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        emailTextField.setError("Please enter a valid email");
        passwordTextField.setError("Please enter a valid password");
        Toast.makeText(
                LoginActivity.this,
                "Login Failed. Please try after sometime...",
                Toast.LENGTH_LONG
        ).show();
    }
}