package com.tafh.tugasakhir.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tafh.tugasakhir.MainActivity;
import com.tafh.tugasakhir.R;
import com.tafh.tugasakhir.adapter.AkunAdapter;
import com.tafh.tugasakhir.adapter.ChatsAdapter;
import com.tafh.tugasakhir.adapter.KeluargaAdapter;
import com.tafh.tugasakhir.adapter.KeluargaKeluarAdapter;
import com.tafh.tugasakhir.akun.ListDataAkunActivity;
import com.tafh.tugasakhir.keluarga.ListDataKeluargaActivity;
import com.tafh.tugasakhir.login.gmail.LoginGmailActivity;
import com.tafh.tugasakhir.model.ModelKeluarga;
import com.tafh.tugasakhir.model.ModelKeluargaNonAktif;
import com.tafh.tugasakhir.model.ModelUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class KeluargaKeluarFragment extends Fragment {

    private TextView txtDataKosong;
    //Deklarasi Variable untuk RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnTambah;
    //Deklarasi Variable Database Reference dan ArrayList dengan Parameter Class Model kita.
    private FirebaseAuth auth;
    private DatabaseReference databaseKeluargaKeluar;

    private ArrayList<ModelKeluargaNonAktif> dataKeluargaKeluar;

    private ProgressBar progressBar;

    private Context mContext;

    private String myUserId;



    public KeluargaKeluarFragment(String myUserId) {
        // Required empty public constructor
        this.myUserId = myUserId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_keluarga_keluar, container, false);

        auth = FirebaseAuth.getInstance();

        txtDataKosong = view.getRootView().findViewById(R.id.txt_data_kosong_keluar);
        progressBar = view.getRootView().findViewById(R.id.progress_bar);
        recyclerView = view.getRootView().findViewById(R.id.rv_keluarga_keluar);

        mContext = view.getContext();

        MyRecyclerView();
        getAllUsers();

        return view;

    }

    private void getAllUsers() {
        //Berisi baris kode untuk mengambil data dari Database dan menampilkannya kedalam Adapter
        progressBar.setVisibility(View.VISIBLE);
//        Toast.makeText(getApplicationContext(),"Mohon Tunggu Sebentar...", Toast.LENGTH_LONG).show();
        databaseKeluargaKeluar = FirebaseDatabase.getInstance().getReference("Keluarga_NonAktif");

        databaseKeluargaKeluar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Inisialisasi ArrayList
                dataKeluargaKeluar = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Mapping data pada DataSnapshot ke dalam objek akun
                    ModelKeluargaNonAktif keluargaKeluar = snapshot.getValue(ModelKeluargaNonAktif.class);

                    if (databaseKeluargaKeluar == null) {
                        txtDataKosong.setVisibility(View.VISIBLE);
                    } else {
                        txtDataKosong.setVisibility(View.INVISIBLE);
                    }

                    //Mengambil Primary Key, digunakan untuk proses Update dan Delete
                    keluargaKeluar.setKeluargaNonAktifId(snapshot.getKey());
                    dataKeluargaKeluar.add(keluargaKeluar);
                }

                //Inisialisasi Adapter dan data akun dalam bentuk Array
                adapter = new KeluargaKeluarAdapter(mContext, dataKeluargaKeluar, myUserId);
                //Memasang Adapter pada RecyclerView
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.INVISIBLE);
//                        Toast.makeText(getApplicationContext(),"Data Berhasil Dimuat", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                        /*
                        Kode ini akan dijalankan ketika ada error dan
                        pengambilan data error tersebut lalu memprint error nya
                        ke LogCat
                        */
                Toast.makeText(getActivity().getApplicationContext(),"Data Gagal Dimuat", Toast.LENGTH_LONG).show();
                Log.e("KeluargaKeluarFragment", databaseError.getDetails()+" "+databaseError.getMessage());

            }
        });
    }

    @Override
    public void onStart() {

        checkUserStatus();
        super.onStart();
    }

    private void checkUserStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null ){
//            Toast.makeText(getContext(), "userid : "+userId,Toast.LENGTH_SHORT).show();
        }
        else {
            startActivity(new Intent(getContext(), LoginGmailActivity.class));
            getActivity().finish();
        }
    }

    private void MyRecyclerView() {
        //Menggunakan Layout Manager, Dan Membuat List Secara Vertical
        layoutManager = new LinearLayoutManager(getLayoutInflater().getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //Membuat Underline pada Setiap Item Didalam List
        DividerItemDecoration itemDecoration = new DividerItemDecoration( getActivity().getApplicationContext()
                , DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat
                .getDrawable(getActivity().getApplicationContext(), R.drawable.line));
        recyclerView.addItemDecoration(itemDecoration);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    //inflate option menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        //inflating menu
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        //searchView
        MenuItem item = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_sortnama);
        menu.findItem(R.id.action_sortnomor);
        SearchView searchView = new SearchView(((ListDataKeluargaActivity) getContext()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setActionView(item, searchView);
        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button from keyboard
                //if search query is not empty then searc
                if (!TextUtils.isEmpty(s.trim())) {
                    //search text contains text, search it
//                    Toast.makeText(getContext(), "Joss"+s,Toast.LENGTH_SHORT).show();
                    searchUsers(s);
                }
                else {
                    getAllUsers();
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called wheneever user press any single letter
                //if search query is not empty then searc
                if (!TextUtils.isEmpty(s.trim())) {
                    //search text contains text, search it
                    searchUsers(s);
                }
                else {
                    getAllUsers();
                }
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        //get item id
        int id = item.getItemId();

        if (id == R.id.action_sortnama) {
//            layoutManager.setStackFromEnd(false);
//            layoutManager.setReverseLayout(false);

            dataKeluargaKeluar.clear();
            final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            final String getEmailUser = fUser.getEmail();
            //get path of database named "Users" containing user info
            final DatabaseReference drKeluarga = FirebaseDatabase.getInstance().getReference("Keluarga_NonAktif");
//        get all data from path
            drKeluarga.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        ModelKeluargaNonAktif modelKeluargaNonAktif = snapshot.getValue(ModelKeluargaNonAktif.class);
                        modelKeluargaNonAktif.setKeluargaId(snapshot.getKey());
                        dataKeluargaKeluar.add(modelKeluargaNonAktif);

                        Collections.sort(dataKeluargaKeluar, new Comparator<ModelKeluargaNonAktif>() {
                            @Override
                            public int compare(ModelKeluargaNonAktif model, ModelKeluargaNonAktif t1) {
                                return model.getNamaKK().compareTo(t1.getNamaKK());
                            }
                        });
                        adapter = new KeluargaKeluarAdapter(mContext, dataKeluargaKeluar, myUserId);
                        //set adapter to recycler view

                        recyclerView.setAdapter(adapter);


                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "error : "+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(mContext, "Urut sesuai Nama", Toast.LENGTH_SHORT).show();
//            checkUserStatus();
        }
        if (id == R.id.action_sortnomor) {
//            layoutManager.setStackFromEnd(false);
//            layoutManager.setReverseLayout(false);

            dataKeluargaKeluar.clear();
            final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            final String getEmailUser = fUser.getEmail();
            //get path of database named "Users" containing user info
            final DatabaseReference drKeluarga = FirebaseDatabase.getInstance().getReference("Keluarga_NonAktif");
//        get all data from path
            drKeluarga.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        ModelKeluargaNonAktif modelKeluargaNonAktif = snapshot.getValue(ModelKeluargaNonAktif.class);
                        modelKeluargaNonAktif.setKeluargaId(snapshot.getKey());
                        dataKeluargaKeluar.add(modelKeluargaNonAktif);

                        Collections.sort(dataKeluargaKeluar, new Comparator<ModelKeluargaNonAktif>() {
                            @Override
                            public int compare(ModelKeluargaNonAktif model, ModelKeluargaNonAktif t1) {
                                return new BigDecimal(model.getNoRumah()).compareTo(new BigDecimal(t1.getNoRumah()));
                            }
                        });
                        adapter = new KeluargaKeluarAdapter(mContext, dataKeluargaKeluar, myUserId);
                        //set adapter to recycler view

                        recyclerView.setAdapter(adapter);


                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(mContext, "error : "+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(mContext, "Urut sesuai No Rumah", Toast.LENGTH_SHORT).show();
//            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }

    private void searchUsers(final String query) {
        Log.w("Nilai", ""+query);

//        get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        final String getEmailUser = fUser.getEmail();
        //get path of database named "Users" containing user info
        final DatabaseReference drUsers = FirebaseDatabase.getInstance().getReference("Keluarga_NonAktif");
        //get all data from path
        drUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                dataUsers2.clear();
//                dataUsers1.clear();


//                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                    ModelUser user = snapshot.getValue(ModelUser.class);
////                    user.setUserid(snapshot.getKey());
//                    dataUsers1.add(user);
//                }
//                int n = dataUsers1.size();
//                for (int i=0; i<n; i++) {
//                    String gmail = dataUsers1.get(i).getGmail();
//                    if (gmail.equals(getEmailUser)) {
//                        getUserChatId = dataUsers1.get(i).getUserid();
////                        coba.setText(dataUsers.get(i).getUserid());
//                    }
//                }
                dataKeluargaKeluar.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    ModelKeluargaNonAktif modelKeluargaNonAktif = snapshot.getValue(ModelKeluargaNonAktif.class);
                    modelKeluargaNonAktif.setKeluargaNonAktifId(snapshot.getKey());

                    if (modelKeluargaNonAktif.getNamaKK().toLowerCase().contains(query.toLowerCase()) ||
                            modelKeluargaNonAktif.getNoRumah().toLowerCase().contains(query.toLowerCase()) ||
                            modelKeluargaNonAktif.getNoKK().toLowerCase().contains(query.toLowerCase())) {

                        dataKeluargaKeluar.add(modelKeluargaNonAktif);

                        adapter = new KeluargaKeluarAdapter(getContext(), dataKeluargaKeluar, myUserId);
                        //set adapter to recycler view
                        recyclerView.setAdapter(adapter);

                    }
//
//                    if (!modelUser.getUserid().equals(myUserId)) {
//
//
//                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "error : "+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}
