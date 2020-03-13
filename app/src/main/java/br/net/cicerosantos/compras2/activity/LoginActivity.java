package br.net.cicerosantos.compras2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import br.net.cicerosantos.compras2.config.ConfigFirebase;
import br.net.cicerosantos.compras2.R;
import br.net.cicerosantos.compras2.helper.Alerta;
import br.net.cicerosantos.compras2.model.Usuario;
import br.net.cicerosantos.compras2.model.Validar;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAuth();

    EditText email, senha;
    static final String ARQUIVO_PRFERENCIA = "ArquivoPreferencia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        email = findViewById(R.id.edtEmail);
        senha = findViewById(R.id.edtSenha);

        SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO_PRFERENCIA, 0);
        if (sharedPreferences.contains("email")){
            email.setText(sharedPreferences.getString("email", ""));
        }
    }

    public void onClickEntrar(View view){
        if (Validar.getVEmail(email)){
            if (Validar.getVCampo(senha)){

                Usuario usuario = new Usuario();
                usuario.setEmail(email.getText().toString().trim());
                usuario.setSenha(senha.getText().toString());
                logar(usuario);

            }else{
                senha.requestFocus();
                senha.setError("Senha inválida!");
            }
        }else{
            email.requestFocus();
            email.setError("E-mail inválido!");
        }
    }

    private void logar(final Usuario usuario){
        Alerta.getProgesso("Aguarde...", this);
        firebaseAuth.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    getSalvarNasPreferencias(usuario.getEmail());
                    Alerta.progressDialog.dismiss();
                    Alerta.getToast("You just log in!", LoginActivity.this);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                }else {
                    String  execao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        execao = "Usuario e senha não corresponde!";
                    }catch (FirebaseAuthInvalidUserException e){
                        execao = "Usuário não cadastrado!";
                    }catch ( Exception e){
                        execao = "Error: " + e.getMessage();
                    }
                    Alerta.progressDialog.dismiss();
                    Alerta.getToast( execao, LoginActivity.this);
                }
            }
        });

    }

    private void getSalvarNasPreferencias(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO_PRFERENCIA, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.commit();
    }

    public void onCickCadastrar(View view){
        startActivity(new Intent(this, PerfilActivity.class));
        finish();
    }
}
