package br.net.cicerosantos.compras2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import br.net.cicerosantos.compras2.R;
import br.net.cicerosantos.compras2.model.Usuario;
import br.net.cicerosantos.compras2.model.Validar;

public class PerfilActivity extends AppCompatActivity {

    EditText nome, email, senha, senha2;
    ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        nome = findViewById(R.id.edtNome);
        email = findViewById(R.id.edtEmail);
        senha = findViewById(R.id.edtSenha);
        senha2 = findViewById(R.id.edtSenha2);

        recuperarDadosUsuario();

    }

    private void recuperarDadosUsuario() {
        String editar = getIntent().getStringExtra("editar");
        if (editar != null && !editar.isEmpty() && editar.equals("editar")){

        }else{
            nome.setVisibility(View.GONE);
        }
    }

    public void onClickEntrar(View view){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void onClickSalvar(View view){
        if (Validar.getVEmail(email)){
            if (Validar.getVCampo(senha)){
                if (Validar.getVCampo(senha2) && senha2.getText().toString().equals(senha.getText().toString())){
                    Usuario usuario = new Usuario();
                    usuario.setNome(nome.getText().toString());
                    usuario.setEmail(email.getText().toString().trim());
                    usuario.setSenha(senha.getText().toString());
                    usuario.salvar(usuario, this);
                }else{
                    senha2.requestFocus();
                    senha2.setError("Senhas não conferem!");
                }
            }else{
                senha.requestFocus();
                senha.setError("Senha inválida");
            }
        }else {
            email.requestFocus();
            email.setError("E-mail inálido!");
        }
    }
}
