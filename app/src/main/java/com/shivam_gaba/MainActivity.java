package com.shivam_gaba;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {


    RelativeLayout root_layout;
    TextView tvLogin, tvForgot;
    EditText etEmail, etPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvLogin = findViewById(R.id.tvLogin);
        tvForgot = findViewById(R.id.tvForgot);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        root_layout = findViewById(R.id.root_layout);


        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        mAuth = FirebaseAuth.getInstance();

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });
    }


    private void showForgotPasswordDialog() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.forgot_password, null);

        final EditText etEmailForgot = login_layout.findViewById(R.id.etEmailForgot);

        dialog.setView(login_layout);

        dialog.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //check validation of user
                if (TextUtils.isEmpty(etEmailForgot.getText().toString())) {
                    Snackbar.make(findViewById(R.id.root_layout), "Please enter email address", Snackbar.LENGTH_LONG).show();
                    return;
                }

                dialogInterface.dismiss();

                final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();

                mAuth.sendPasswordResetEmail(etEmailForgot.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(findViewById(R.id.root_layout), "Password reset email sent", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(findViewById(R.id.root_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });

                waitingDialog.dismiss();
            }
        })

                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        dialog.show();
    }


    private void loginUser() {
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            // here we use a snack bar and show it in root layout(MainActivity)
            Snackbar.make(root_layout, "Please enter email address", Snackbar.LENGTH_LONG).show();
            return;
        }


        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            // here we use a snack bar and show it in root layout(MainActivity)
            Snackbar.make(root_layout, "Please enter password", Snackbar.LENGTH_LONG).show();
            return;
        }

        tvLogin.setEnabled(false);
        // Login authentication

        final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
        waitingDialog.show();
        mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, MapHome.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(root_layout, "FAILED " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        tvLogin.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}