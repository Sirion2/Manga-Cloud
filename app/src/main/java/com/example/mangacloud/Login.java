package com.example.mangacloud;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private EditText et1, et2;
    private Button btn1;
    private TextView tv1, tv2;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        et1 = (EditText) findViewById(R.id.email);
        et2 = (EditText) findViewById(R.id.password);
        btn1 = findViewById(R.id.btn_login);
        tv1 = findViewById(R.id.link_recoverp);
        tv2 = findViewById(R.id.link_register);

        if(fAuth.getCurrentUser() != null){ //Validate if theres any person already logged in
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        btn1.setOnClickListener(new View.OnClickListener() { //Login
            @Override
            public void onClick(View view) {

                email = et1.getText().toString().trim();
                password = et2.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    et1.setError("Introduzca su E-mail");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    et2.setError("Introduzca su Password");
                    return;
                }

                /**auth user */
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()) {
                          Toast.makeText(Login.this, "Inicio de sesion exitoso!", Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(getApplicationContext(),MainActivity.class));
                      } else {
                          Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                      }
                    }
                });
            }
        });

        tv1.setOnClickListener(new View.OnClickListener(){ //password reset
            public void onClick(View view){
                EditText email = new EditText(view.getContext());
                @SuppressLint("ResourceType") AlertDialog.Builder PasswordResetDialog = new AlertDialog.Builder(view.getContext());
                PasswordResetDialog.setTitle("Reestablecer Password");
                PasswordResetDialog.setMessage("Introduzca su email");
                PasswordResetDialog.setView(email);

                PasswordResetDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = email.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Login.this, "Se ha enviado un correo a tu bandeja de entrada",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error al enviar correo" + e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                PasswordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener()  {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No option
                    }
                });
                PasswordResetDialog.create().show();
            }
        });

        tv2.setOnClickListener(new View.OnClickListener(){ // register
            public void onClick(View v){
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });
    }
}