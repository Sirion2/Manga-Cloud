package com.example.mangacloud;

import static com.example.mangacloud.Register.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnItemListener {

    RecyclerView recyclerView;
    TextView tv1;
    ArrayList<Mangalist_item> MangalistArrayList;
    ArrayList<String> DocumentIdArrayList = new ArrayList<>();
    ProgressDialog progressDialog;
    MyAdapter myAdapter;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    String userID;
    float x1, x2, y1, y2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        tv1 = findViewById(R.id.usuario);

        recyclerView = findViewById(R.id.mangalist_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        MangalistArrayList = new ArrayList<Mangalist_item>();
        myAdapter = new MyAdapter(MainActivity.this,MangalistArrayList,this);
        recyclerView.setAdapter(myAdapter);

        userID = fAuth.getCurrentUser().getUid();
        getUser();
        EventChangeListener();
    }

   private void EventChangeListener() {
        fStore.collection("MANGAS").orderBy("NOMBRE")//.startAt(this.start).endAt(this.end) /*<-- ahi se ordena*/ //Query
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();

                        Log.e("FireStore error", error.getMessage());
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            MangalistArrayList.add(dc.getDocument().toObject(Mangalist_item.class));
                            DocumentIdArrayList.add(dc.getDocument().getId());
                        }
                        myAdapter.notifyDataSetChanged();

                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }

            });
   }

   @Override
   public void onItemClick(int position) { //Se items to manga Activity
        String document_id;
        Intent i = new Intent(this, Manga.class);
        i.putExtra("NOMBRE",MangalistArrayList.get(position).getNOMBRE());
        i.putExtra("DESCRIPCION",MangalistArrayList.get(position).getDESCRIPCION());
        i.putExtra("COVER",MangalistArrayList.get(position).getCOVER());
        i.putExtra("VOLUMENES",MangalistArrayList.get(position).getVOLUMENES());
        i.putExtra("POSTION", Integer.toString(position));
        document_id = DocumentIdArrayList.get(position);
        i.putExtra("DOCUMENT_ID", document_id);
        startActivity(i);
   }

   public boolean onTouchEvent(MotionEvent touchEvent) { //Slide effect
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();

                if (x1 < x2) {
                    Intent i = new Intent(this, Anime_wpage.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (x1 > x2) {
                    Intent i = new Intent(getApplicationContext(), Profile.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                    finish();
                }
                break;
        }
        return false;
   }


    public void getUser() { //get user to display in Main Activity
        fStore.collection("USUARIOS").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot dc = task.getResult();
                        if(task.isSuccessful()) {
                            tv1.setText("Hola, " + dc.getString("USUARIO"));
                        } else {
                            Log.d(TAG, "Error al traer los datos: " + task.getException());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error al traer los datos: " + e.getMessage());
                    }
                });
   }
}