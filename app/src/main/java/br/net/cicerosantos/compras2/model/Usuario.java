package br.net.cicerosantos.compras2.model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.net.cicerosantos.compras2.Config.ConfigFirebase;
import br.net.cicerosantos.compras2.activity.LoginActivity;
import br.net.cicerosantos.compras2.helper.Alerta;

public class Usuario {

    static FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAuth();
    String id, nome, email, senha, foto;

    public Usuario() {
    }

    public static void salvar(Usuario usuario, final Activity activity){
        Alerta.getProgesso("Aguarde...", activity);
        try {
            firebaseAuth.createUserWithEmailAndPassword(
                    usuario.getEmail(),
                    usuario.getSenha()
            ).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Alerta.progressDialog.dismiss();
                        Toast.makeText(activity, "Usuario cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        firebaseAuth.signOut();
                        activity.finish();
                    }else{
                        String  result = "";
                        try {
                            throw task.getException();
                        }catch ( FirebaseAuthWeakPasswordException e){
                            result = "Password invalid!";
                        }catch ( FirebaseAuthInvalidCredentialsException e){
                            result = "Mail invalid!";
                        }catch ( FirebaseAuthUserCollisionException e){
                            result = "Mail already exists!";
                        }catch ( Exception e){
                            result = "Error: " + e.getMessage();
                        }
                        Alerta.progressDialog.dismiss();
                        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
