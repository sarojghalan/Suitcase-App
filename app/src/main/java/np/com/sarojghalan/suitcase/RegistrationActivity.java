package np.com.sarojghalan.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import np.com.sarojghalan.suitcase.db.SuitcaseDatabase;
import np.com.sarojghalan.suitcase.db.user.User;
import np.com.sarojghalan.suitcase.db.user.UserDao;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputLayout emailTextField, fullNameTextField, addressTextField, passwordTextField, confirmPasswordTextField;
    private EditText emailEt, fullNameEt, addressEt, passwordEt, confirmPasswordEt;
    private MaterialButton materialButtonRegister;
    private ProgressDialog progressDialog;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailTextField = findViewById(R.id.til_registration_email);
        emailEt = emailTextField.getEditText();

        fullNameTextField = findViewById(R.id.til_registration_full_name);
        fullNameEt = fullNameTextField.getEditText();

        addressTextField = findViewById(R.id.til_registration_address);
        addressEt = addressTextField.getEditText();

        passwordTextField = findViewById(R.id.til_registration_password);
        passwordEt = passwordTextField.getEditText();

        confirmPasswordTextField = findViewById(R.id.til_registration_confirm_password);
        confirmPasswordEt = confirmPasswordTextField.getEditText();

        materialButtonRegister = findViewById(R.id.mb_register);

        progressDialog = new ProgressDialog(this);
        materialButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        loginLink = findViewById(R.id.login_link);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void validateData() {
        String email = emailEt.getText().toString().trim();
        String fullName = fullNameEt.getText().toString().trim();
        String address = addressEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String confirmPassword = confirmPasswordEt.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextField.setError("Please enter a valid email");
        } else if (fullName.isEmpty()) {
            fullNameTextField.setError("Please enter a valid name");
        } else if (address.isEmpty()) {
            addressTextField.setError("Please enter a valid address");
        } else if (password.isEmpty()) {
            passwordTextField.setError("Please enter a password");
        } else if (!password.equalsIgnoreCase(confirmPassword)) {
            confirmPasswordTextField.setError("Password didn't match");
        } else {
            progressDialog.setMessage("Registering user...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    insertUserInDatabase(email, fullName, address, password);
                }
            }, 1000);
        }
    }

    private void insertUserInDatabase(
            String email,
            String fullName,
            String address,
            String password
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = new User();
                    user.email = email.toLowerCase(Locale.ROOT);
                    user.fullName = fullName;
                    user.address = address;
                    user.password = password;
                    SuitcaseDatabase suitcaseDatabase = SuitcaseDatabase
                            .getInstance(getApplicationContext());
                    UserDao userDao = suitcaseDatabase.getUserDao();
                    userDao.insertUser(user);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyRegistrationSuccess();
                        }
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyRegistrationFailure();
                        }
                    });
                }
            }
        }).start();
    }

    private void notifyRegistrationSuccess() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(
                RegistrationActivity.this,
                "Registration Success",
                Toast.LENGTH_LONG
        ).show();
        startLoginActivity();
    }

    private void notifyRegistrationFailure() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(
                RegistrationActivity.this,
                "Registration Failure. Please try again...",
                Toast.LENGTH_LONG
        ).show();
//        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}