package br.net.cicerosantos.compras2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.net.cicerosantos.compras2.R;
import br.net.cicerosantos.compras2.model.Validar;

public class LoginActivity extends AppCompatActivity {

    EditText email, senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        email = findViewById(R.id.edtEmail);
        senha = findViewById(R.id.edtSenha);
    }

    public void onClickEntrar(View view){
        if (Validar.getVEmail(email)){
            if (Validar.getVCampo(senha)){

            }else{
                senha.requestFocus();
                senha.setError("Senha inválida!");
            }
        }else{
            email.requestFocus();
            email.setError("E-mail inválido!");
        }
    }

    public void onCickCadastrar(View view){
        startActivity(new Intent(this, PerfilActivity.class));
        finish();
    }
}
