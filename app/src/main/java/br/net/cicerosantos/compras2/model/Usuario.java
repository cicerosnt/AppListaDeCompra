package br.net.cicerosantos.compras2.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;

import br.net.cicerosantos.compras2.config.ConfigFirebase;
import br.net.cicerosantos.compras2.activity.LoginActivity;
import br.net.cicerosantos.compras2.helper.Alerta;

public class Usuario {

    static FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAuth();
    String id, nome, email, senha, foto;

    public Usuario() {
    }

    public static void getSalvarUsuario(final Usuario usuario, final Activity activity){
        Alerta.getProgesso("Aguarde...", activity);
        try {
            firebaseAuth.createUserWithEmailAndPassword(
                    usuario.getEmail(),
                    usuario.getSenha()
            ).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        getAtulizaNome(usuario.getNome(), activity);
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

    public static void getAtulizaNome(final String nome, final Activity activity){

        firebaseAuth = ConfigFirebase.getFirebaseAuth();

        try {
            UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName( nome )
                    .build();
            firebaseAuth.getCurrentUser().updateProfile( perfil ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( task.isSuccessful() ){
                        Alerta.getToast("Nome atualizao!", activity);
                    }else{
                        Alerta.getToast("Erro ao atualizar nome", activity);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Log.d("tet","tet:" + e.getMessage().toString());
        }
    }

    public static boolean getAtualizaFotoUsuario(Uri url){
        try {
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri( url )
                    .build();
            ConfigFirebase.getFirebaseAuth().getCurrentUser().updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
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
