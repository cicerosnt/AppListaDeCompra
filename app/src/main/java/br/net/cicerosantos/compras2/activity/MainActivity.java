package br.net.cicerosantos.compras2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import br.net.cicerosantos.compras2.R;
import br.net.cicerosantos.compras2.model.Validar;

public class MainActivity extends AppCompatActivity {

    MaterialSearchView searchView;

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
    }

    private void addNovoItem(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_item_lista);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText item = dialog.findViewById(R.id.edtItem);

        dialog.findViewById(R.id.btnSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validar.getVCampo(item)){
                    Toast.makeText(MainActivity.this, "Inserir aqui o codigo para salvar!", Toast.LENGTH_SHORT).show();
                    //codificação para salvar o item na lista
                }else{
                    item.requestFocus();
                    item.setError("Descrição inválida!");
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

                break;
            case R.id.exportar:

                break;
            case R.id.sair:
                    finish();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
