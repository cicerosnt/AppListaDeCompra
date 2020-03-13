package br.net.cicerosantos.compras2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import br.net.cicerosantos.compras2.adapter.AdapterItem;
import br.net.cicerosantos.compras2.config.ConfigFirebase;
import br.net.cicerosantos.compras2.R;
import br.net.cicerosantos.compras2.helper.Alerta;
import br.net.cicerosantos.compras2.model.Item;
import br.net.cicerosantos.compras2.model.Validar;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAuth();
    DatabaseReference databaseReference = ConfigFirebase.getDatabaseReference();

    String strDescription, idUser;
    List<Item> listaItens = new ArrayList<>();
    List<Item> listaItensPesquisa = new ArrayList<>();
    public boolean search = false;
    Item item;
    AdapterItem adapterItem;

    MaterialSearchView searchView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializaComponentes();

        //startActivity(new Intent(this, PerfilActivity.class));
    }

    private void inicializaComponentes() {
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recyclerView);
        isLogado();
    }

    public void onClickLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onClickPerfil(View view){
        startActivity(new Intent(this, PerfilActivity.class));
    }

    private void isLogado() {
        if (firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
            firebaseAuth.signOut();
            finish();
        }else {
            getRecuperaItens();
        }
    }

    private void getRecuperaItens() {
        Alerta.getProgesso("Carregando itens...",  this);
        final DatabaseReference itensRef = databaseReference.child("shopping_list").child(firebaseAuth.getUid());
        itensRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaItens.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    item = ds.getValue(Item.class);
                    item.setId(ds.getKey());
                    listaItens.add(item);
                }

                Alerta.progressDialog.dismiss();
                getConfigRecycler(listaItens);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getConfigRecycler(List lista){
        //configurando o adapter
        adapterItem = new AdapterItem(lista, this );

        //configurando o recyclear
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize( true );
        //capturaScrolled();
        recyclerView.setAdapter( adapterItem );

        getConfigSwaip();

    }

    public void getConfigSwaip(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                int draFlags = ItemTouchHelper.ACTION_STATE_DRAG;
                int swipeFlags = ItemTouchHelper.RIGHT;

                return makeMovementFlags(draFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                isDeletarItem( viewHolder );
            }
        };

        new ItemTouchHelper( itemTouch ).attachToRecyclerView( recyclerView );
    }

    public void isDeletarItem(final RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Êeepa!");
        dialog.setMessage("Deseja mesmo excluir este item?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int position = viewHolder.getAdapterPosition();
                item = new Item();
                if (search == true){
                    item.setId(listaItensPesquisa.get(position).getId());
                }else {
                    item.setId(listaItens.get(position).getId());
                }

                if (item.getDeletarItem(item.getId())){
                    Alerta.getToast("Item deleted succesfully!", MainActivity.this);
                    getRecuperaItens();
                }else {
                    Alerta.getToast("Error deleted the item!", MainActivity.this);
                    adapterItem.notifyItemRemoved( position );
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                adapterItem.notifyDataSetChanged();
            }
        });
        dialog.create();
        dialog.show();

    }

    private void addNovoItem(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_item_lista);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText edtItem = dialog.findViewById(R.id.edtItem);

        dialog.findViewById(R.id.btnSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validar.getVCampo(edtItem)){
                    item = new Item();
                    item.setDescricao(edtItem.getText().toString());
                    item.setPosicao("0");
                    item.setId(firebaseAuth.getUid());
                    item.getSalvarItem(item);
                    dialog.dismiss();
                }else{
                    edtItem.requestFocus();
                    edtItem.setError("Descrição inválida!");
                }
            }
        });
        dialog.findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.search_view);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.search_view:
                break;
            case R.id.novoItem:
                addNovoItem();
                break;
            case R.id.perfil:
                Intent intent = new Intent(this, PerfilActivity.class);
                intent.putExtra("editar", "editar");
                startActivity(intent);
                break;
            case R.id.exportar:

                break;
            case R.id.sair:
                    firebaseAuth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
