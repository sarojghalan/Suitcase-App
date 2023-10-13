package np.com.sarojghalan.suitcase.dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;

import np.com.sarojghalan.suitcase.LoginActivity;
import np.com.sarojghalan.suitcase.R;
import np.com.sarojghalan.suitcase.db.user.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private User loggedInUser;
    private ImageButton ibLogOut;
    private TextInputLayout emailTextField, fullNameTextField, addressTextField;
    private EditText emailEt, fullNameEt, addressEt;

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle argument = new Bundle();
        argument.putSerializable("logged_user", user);
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ibLogOut = view.findViewById(R.id.ib_logout);

        emailTextField = view.findViewById(R.id.til_profile_email);
        emailEt = emailTextField.getEditText();

        fullNameTextField = view.findViewById(R.id.til_profile_full_name);
        fullNameEt = fullNameTextField.getEditText();

        addressTextField = view.findViewById(R.id.til_profile_address);
        addressEt = addressTextField.getEditText();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.loggedInUser = (User) requireArguments().getSerializable("logged_user");
        ibLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutAlertDialog();
            }
        });

        if (loggedInUser != null) {
            fullNameEt.setText(loggedInUser.fullName);
            emailEt.setText(loggedInUser.email);
            addressEt.setText(loggedInUser.address);
        }
    }

    private void showLogoutAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                .setTitle("LogOut")
                .setMessage("Are you sure want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        clearSharedPreferences();
                        startHomeScreenActivity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(
                "user_pref",
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void startHomeScreenActivity() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

}