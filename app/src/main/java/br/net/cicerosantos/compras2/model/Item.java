package br.net.cicerosantos.compras2.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import br.net.cicerosantos.compras2.config.ConfigFirebase;

public class Item {
    private static DatabaseReference databaseReference = ConfigFirebase.getDatabaseReference();
    private static FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAuth();
    private String id, descricao, prioridade, posicao;

    public Item() {
    }

    public static void getSalvarItem(Item item) {
        databaseReference.child("compras_list")
                .child(item.getId())
                .push()
                .setValue(item);
    }

    public static boolean getDeletarItem(String id){
        try {
            databaseReference.child("compras_list")
                    .child( firebaseAuth.getCurrentUser().getUid())
                    .child( id ).removeValue();
            return true;
        }catch (Exception e){

            return false;
        }
    }

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }
}
