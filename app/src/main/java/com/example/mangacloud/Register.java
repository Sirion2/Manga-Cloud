package com.example.mangacloud;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText et1, et2, et3, et4;
    private Button btn1;
    private TextView tv1, tv2;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et1 = findViewById(R.id.email);
        et2 = findViewById(R.id.password);
        et3 = findViewById(R.id.usuario);
        et4 = findViewById(R.id.mangafav);
        btn1 = findViewById(R.id.btn_register);
        tv1 = findViewById(R.id.link_register);
        tv2 = findViewById(R.id.link_recoverp);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        
        btn1.setOnClickListener(new View.OnClickListener() { //Registro
            @Override
            public void onClick(View v) {
                String email = et1.getText().toString().trim();
                String password = et2.getText().toString();
                String usuario = et3.getText().toString();
                String manga_favorito = et3.getText().toString();

                if(TextUtils.isEmpty(email)){
                    et1.setError("Introduzca su E-mail");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    et2.setError("Introduzca su Password");
                    return;
                }

                if(password.length() < 6){
                    et2.setError("Password debe ser min(6) caracteres");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //Query
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registro Exitoso!", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("USUARIOS").document(userID);
                            Map<String, Object> USUARIO = new HashMap<>();
                            USUARIO.put("EMAIL", email);
                            USUARIO.put("PASSWORD", password);
                            USUARIO.put("USUARIO", usuario);
                            USUARIO.put("MANGA_FAVORITO", manga_favorito);
                            USUARIO.put("IMG", null);

                            documentReference.set(USUARIO).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSucccess: Usuario creado para Id: " + userID);
                                    String mail = et1.getText().toString();
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        tv2.setOnClickListener(new View.OnClickListener(){ //Password recovery
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
                                Toast.makeText(Register.this, "Se ha enviado un correo a tu bandeja de entrada",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "Error al enviar correo" + e.getMessage(),Toast.LENGTH_SHORT).show();
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

        tv1.setOnClickListener(new View.OnClickListener(){ //Login
            public void onClick(View v){
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });
    }
}