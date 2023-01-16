package com.example.mangacloud;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.example.mangacloud.Register.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Manga extends AppCompatActivity implements MangaAdapter.OnItemListener {

    private TextView tv_manga_NOMBRE, tv_manga_DESCRIPCION;
    private ImageView iv_manga_COVER, iv_manga_BACKGROUND;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ArrayList<Mangalist_item> MangaVolumensArrayList;
    MangaAdapter mangaAdapter;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        tv_manga_NOMBRE = (TextView) findViewById(R.id.manga_manga_titulo);
        tv_manga_DESCRIPCION = (TextView) findViewById(R.id.manga_manga_descripcion);
        iv_manga_COVER = (ImageView) findViewById(R.id.manga_manga_imagen);
        iv_manga_BACKGROUND = (ImageView) findViewById(R.id.manga_manga_bgimg);

        String manga_NOMBRE = getIntent().getStringExtra("NOMBRE");
        String manga_DECRIPCION = getIntent().getStringExtra("DESCRIPCION");
        String manga_COVER = getIntent().getStringExtra("COVER");
        String manga_ID = getIntent().getStringExtra("DOCUMENT_ID");

        tv_manga_NOMBRE.setText(manga_NOMBRE);
        tv_manga_DESCRIPCION.setText(manga_DECRIPCION);

        Glide.with(this).load(manga_COVER).override(700, 500).into(iv_manga_COVER);
        Glide.with(this)
                .load(manga_COVER)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().transition(withCrossFade())
                .into(iv_manga_BACKGROUND);

        recyclerView = findViewById(R.id.mangalist_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();

        MangaVolumensArrayList = new ArrayList<Mangalist_item>();
        mangaAdapter = new MangaAdapter(Manga.this,MangaVolumensArrayList, this);
        recyclerView.setAdapter(mangaAdapter);

        if(progressDialog.isShowing())
            progressDialog.dismiss();

        EventChangeListener(manga_ID);

    }

    private void EventChangeListener(String manga_ID) { //Brings manga an set it to adapter
        fStore.collection("MANGAS").document(manga_ID).collection("VOLUMENES").orderBy("VOLUMENES", Query.Direction.ASCENDING)/**<-- ahi se ordena*/
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(Manga.this, "error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();

                            Log.e("FireStore error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Log.d(TAG, dc.getDocument().getId());
                                MangaVolumensArrayList.add(dc.getDocument().toObject(Mangalist_item.class));
                            }
                            mangaAdapter.notifyDataSetChanged();

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }

                });
    }

    @Override
    public void onItemClick(int position) { //Change activity and pass the pdf selected
        String volumenes_id;
        Intent i = new Intent(getApplicationContext(), PDFreader.class);
        volumenes_id = MangaVolumensArrayList.get(position).getLINK();
        i.putExtra("VOLUMEN_ID", volumenes_id);
        //Log.d(TAG, "onItemClick: clicked: and show link" + MangaVolumensArrayList.get(position).getLINK());
        startActivity(i);
    }
}