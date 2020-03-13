package br.net.cicerosantos.compras2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import br.net.cicerosantos.compras2.config.ConfigFirebase;
import br.net.cicerosantos.compras2.config.Permissao;
import br.net.cicerosantos.compras2.R;
import br.net.cicerosantos.compras2.helper.Alerta;
import br.net.cicerosantos.compras2.model.Usuario;
import br.net.cicerosantos.compras2.model.Validar;

public class PerfilActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAuth();
    StorageReference storageReference = ConfigFirebase.getStorageReference();

    EditText nome, email, senha, senha2;
    ImageView imgUser;
    Button btnCadastrar, btnEntrar, btnAtualizar;
    ImageView imageView;

    String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    static final int SELECAO_GALERIA = 200;
    static Bitmap imgBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Meu Pefil");

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        nome = findViewById(R.id.edtNome);
        email = findViewById(R.id.edtEmail);
        senha = findViewById(R.id.edtSenha);
        senha2 = findViewById(R.id.edtSenha2);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnAtualizar = findViewById(R.id.btnAtualizar);
        imageView = findViewById(R.id.imgUsuario);

        recuperarDadosUsuario();
        recuperarDadosFirebaseAuth();

        Permissao.validarPermissoes( permissoesNecessarias, PerfilActivity.this, 1 );

    }

    private void recuperarDadosFirebaseAuth() {
        if (firebaseAuth.getCurrentUser() != null){
            nome.setText(firebaseAuth.getCurrentUser().getDisplayName());
            email.setText(firebaseAuth.getCurrentUser().getEmail());
            if (firebaseAuth.getCurrentUser().getPhotoUrl() != null){
                Picasso.with(this).load(firebaseAuth.getCurrentUser().getPhotoUrl().toString()).into(imageView);
            }
        }else {
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void recuperarDadosUsuario() {
        String editar = getIntent().getStringExtra("editar");
        if (editar != null && editar.equals("editar")){

            senha.setVisibility(View.GONE);
            senha2.setVisibility(View.GONE);
            btnCadastrar.setVisibility(View.GONE);
            btnEntrar.setVisibility(View.GONE);
            email.setEnabled(false);

        }else{

            nome.setVisibility(View.GONE);
            btnAtualizar.setVisibility(View.VISIBLE);

        }
    }

    public void onClickNovoUsuario(View view) {
        if (Validar.getVEmail(email)){
            if (Validar.getVCampo(senha)){
                if (Validar.getVCampo(senha2) && senha2.getText().toString().equals(senha.getText().toString())){

                    Usuario usuario = new Usuario();
                    usuario.setNome(nome.getText().toString());
                    usuario.setEmail(email.getText().toString().trim());
                    usuario.setSenha(senha.getText().toString());
                    usuario.getSalvarUsuario(usuario, PerfilActivity.this);

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

    public void onClickEditarUsuario(View view) {
        if (Validar.getVCampo(nome)){

            Usuario usuario = new Usuario();
            usuario.setNome(nome.getText().toString());
            usuario.getAtulizaNome(nome.getText().toString(), PerfilActivity.this);

        }else {
            nome.requestFocus();
            nome.setError("Nome inválido!");
        }
    }

    public void onClickAlterarFoto(View view){
        Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
        if ( i.resolveActivity( getPackageManager() ) != null ) {
            startActivityForResult( i, SELECAO_GALERIA );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            Alerta.getProgesso("Carregando sua foto de perffil...", this);

            try {

                switch ( requestCode ){
                    case SELECAO_GALERIA:
                        Uri localImgSelected = data.getData();
                        imgBitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), localImgSelected );
                        break;
                }

                if ( imgBitmap != null){

                    ByteArrayOutputStream boas = new ByteArrayOutputStream();
                    imgBitmap.compress(Bitmap.CompressFormat.JPEG, 60, boas);
                    byte[] dataImageBaos = boas.toByteArray();

                    StorageReference imageRef = storageReference.child("images")
                            .child("profile")
                            .child( firebaseAuth.getCurrentUser().getUid() );

                    UploadTask uploadTask = imageRef.putBytes(dataImageBaos);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Alerta.progressDialog.dismiss();
                            Alerta.getToast("Não foi possivel atualizar sua foto!", PerfilActivity.this);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Alerta.progressDialog.dismiss();

                            Uri uri = taskSnapshot.getDownloadUrl();
                            Usuario usuario = new Usuario();
                            usuario.getAtualizaFotoUsuario( uri );

                            //exibe imagem carregado no perfil
                            imageView.setImageBitmap( imgBitmap );
                            Alerta.getToast("Foto atualizad com sucesso", PerfilActivity.this);

                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            //menssagem de erro
            Alerta.progressDialog.dismiss();
            Alerta.getToast(" It was not possible to update the photo, try again!", PerfilActivity.this);

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permisaoResultado : grantResults ){
            if ( permisaoResultado == PackageManager.PERMISSION_DENIED){
                alertaPermissaoNegada();
            }
        }
    }

    public void alertaPermissaoNegada(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Permissão negada!");
        dialog.setMessage("Você precisa conseder permissão para continuar");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Permissao.validarPermissoes( permissoesNecessarias, PerfilActivity.this, 1 );
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.create();
        dialog.show();
    }

    public void onClickEntrar(View view){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
