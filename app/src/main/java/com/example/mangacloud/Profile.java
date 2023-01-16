package com.example.mangacloud;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.example.mangacloud.Register.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Profile extends AppCompatActivity {

    private Button btn1, btn2, btn3, btn4;
    private ImageView iv1, iv2;
    private TextView tv1, tv2, tv3;

    float x1, x2, y1, y2;
    private String userID;
    private Uri ImageUri;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private StorageReference storageReference;

    private  static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv1 = findViewById(R.id.usuario);
        tv2 = findViewById(R.id.email);
        tv3 = findViewById(R.id.mangafav);
        btn1 = findViewById(R.id.btn_chooseimg);
        btn2 = findViewById(R.id.btn_uploadimg);
        btn3 = findViewById(R.id.btn_changep);
        btn4 = findViewById(R.id.btn_logout);
        iv1 = findViewById(R.id.user_img);
        iv2 = findViewById(R.id.manga_profile_bgimg);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReference();

        userID = fAuth.getCurrentUser().getUid();

        getUserData();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                @SuppressLint("ResourceType") AlertDialog.Builder PasswordResetDialog = new AlertDialog.Builder(view.getContext());
                PasswordResetDialog.setTitle("Reestablecer Password");
                PasswordResetDialog.setMessage("Se ha envido un enlace de cambios de Password al correo");
                PasswordResetDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = tv2.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Profile.this, "Se ha enviado un correo a tu bandeja de entrada",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "Error al enviar correo" + e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                PasswordResetDialog.create().show();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


    }

    public void getUserData(){
        fStore.collection("USUARIOS").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot dc = task.getResult();

                            Glide.with(Profile.this)
                                    .load(dc.getString("IMG"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop().transition(withCrossFade())
                                    .into(iv2);

                            Glide.with(Profile.this)
                                    .load(dc.getString("IMG"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(new CenterCrop(), new RoundedCorners(24))
                                    .transition(withCrossFade())
                                    .override(1920, 1080)
                                    .into(iv1);
                            tv1.setText(dc.getString("USUARIO"));
                            tv2.setText(dc.getString("EMAIL"));
                            tv3.setText(dc.getString("MANGA_FAVORITO"));
                        } else {
                            Log.d(TAG, "Error al traer los datos en:", task.getException());
                        }
                    }
                });
    }

    private void openFileChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            ImageUri = data.getData();
            Glide.with(this)
                    .load(ImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .transition(withCrossFade())
                    .override(1920, 1080)
                    .into(iv1);
        }
    }

    private void uploadImage() {
        if(ImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo Imagen...");
            progressDialog.show();

            StorageReference ref = storageReference.child("PROFILE_IMGS/" + userID.toString());
            ref.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            StorageReference refg = FirebaseStorage.getInstance().getReference("PROFILE_IMGS/");
                            refg.child(userID).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            fStore.collection("USUARIOS").document(userID)
                                                    .update("IMG", uri.toString())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d(TAG, "campo IMG actualizada en FIRESTORE");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "ERROR actualizando el campo IMG error:", e);
                                                        }
                                                    });
                                                    Log.d(TAG, "onSuccess: " + uri.toString());
                                                }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("error",  e.getMessage());
                                        }
                                    });
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Imagen Actualizada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Actualizando: " + (int)progress + "%");
                        }
                    });
        }
    }

    public void logout(){
        finishAffinity();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,Login.class));

    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();

                if (x1 < x2) {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();
                }
                else if (x1 > x2){
                    Intent i = new Intent(this, Anime_wpage.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                }
                break;
        }
        return false;
    }
}