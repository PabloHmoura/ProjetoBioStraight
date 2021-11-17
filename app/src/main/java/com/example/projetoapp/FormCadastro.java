package com.example.projetoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {

    private EditText nome_cadastro, cpf_cadastro, data_nascimento, num_celular, cadastre_email, cadastre_senha, confirme_senha;
    private Button btn_confirme_cadastro;
                        //   índice 0                        índice 1
    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};
    String usuarioID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();

        //Ação do botão "cadastrar"

        btn_confirme_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String nome = nome_cadastro.getText().toString();
               String cpf = cpf_cadastro.getText().toString();
               String datanascimento = data_nascimento.getText().toString();
               String celular = num_celular.getText().toString();
               String email = cadastre_email.getText().toString();
               String senha = cadastre_senha.getText().toString();
               String confirmesenha = confirme_senha.getText().toString();

                    // confirmação de preenchimento

                if (nome.isEmpty() || cpf.isEmpty() || datanascimento.isEmpty() || celular.isEmpty() || email.isEmpty() ||senha.isEmpty() || confirmesenha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
                else{
                    CadastrarUsuario(v);
                }
            }
        });
    }

    private void CadastrarUsuario(View v){

        String email = cadastre_email.getText().toString();
        String senha = cadastre_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    SalvarDadosUsuario();


                    Snackbar snackbar = Snackbar.make(v, mensagens[1],Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com no mínimo 6 caractéres";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Esta conta já está sendo utilizada";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "E-mail inválido";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuário";
                    }
                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }
            }
        });

    }
    private void SalvarDadosUsuario(){
        String nome = nome_cadastro.getText().toString();
        String cpf = cpf_cadastro.getText().toString();
        String datanascimento = data_nascimento.getText().toString();
        String celular = num_celular.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("cpf", cpf);
        usuarios.put("data nascimento", datanascimento);
        usuarios.put("Número de Celular", celular);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentreference = db.collection("Usuarios").document(usuarioID);
        documentreference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db","Sucesso ao salvar os dados");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error", "Erro ao salvar os dados" + e.toString());

                    }
                });

    }

    private void IniciarComponentes(){

        nome_cadastro = findViewById(R.id.nome_cadastro);
        cpf_cadastro = findViewById(R.id.cpf_cadastro);
        data_nascimento = findViewById(R.id.data_nascimento);
        num_celular = findViewById(R.id.num_celular);
        cadastre_email = findViewById(R.id.cadastre_email);
        cadastre_senha = findViewById(R.id.cadastre_senha);
        confirme_senha = findViewById(R.id.confirme_senha);
        btn_confirme_cadastro = findViewById(R.id.btn_confirme_cadastro);

    }
}