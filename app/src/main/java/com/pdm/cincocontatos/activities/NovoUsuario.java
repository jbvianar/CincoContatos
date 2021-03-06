package com.pdm.cincocontatos.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.material.textfield.TextInputEditText;
import com.pdm.cincocontatos.R;
import com.pdm.cincocontatos.model.User;

public class NovoUsuario extends AppCompatActivity {

    boolean primeiraVezNome = true;
    boolean primeiraVezUser = true;
    boolean primeiraVezSenha = true;
    boolean primeiraVezEmail = true;
    TextInputEditText edUser,edPass,edNome,edEmail;
    Button btCriar;
    Switch swLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario);

        btCriar = (Button) findViewById(R.id.btCriar);
        edUser = (TextInputEditText) findViewById(R.id.edT_Login2);
        edPass = (TextInputEditText) findViewById(R.id.edt_Pass2);
        edNome = (TextInputEditText) findViewById(R.id.edtNome);
        edEmail = (TextInputEditText) findViewById(R.id.edEmail);
        swLogado = (Switch) findViewById(R.id.swLogado);

        //Evento de limpar Componente - User
        edUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezUser) {
                    primeiraVezUser = false;
                    edUser.setText("");
                }
                return false;
            }
        });

        //Evento de limpar Componente - Senha
        edPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezSenha) {
                    primeiraVezSenha = false;
                    edPass.setText("");
                    edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                return false;
            }
        });

        //Evento de limpar Componente - E-mail
        edEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezEmail) {
                    primeiraVezEmail = false;
                    edEmail.setText("");
                }
                return false;
            }
        });

        //Evento de limpar Componente - Nome
        edNome.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezNome) {
                    primeiraVezNome = false;
                    edNome.setText("");
                }
                return false;
            }
        });

        btCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Evento do Botão Criar
                String nome, login, senha, email;
                boolean manterLogado;
                nome = edNome.getText().toString();
                login = edUser.getText().toString();
                senha = edPass.getText().toString();
                email = edEmail.getText().toString();
                manterLogado = swLogado.isChecked();

                User user= new User(nome, login,senha,email);


                SharedPreferences salvaUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
                SharedPreferences.Editor escritor = salvaUser.edit();

                escritor.putString("nome", nome);
                escritor.putString("senha", senha);
                escritor.putString("login", login);
                //Salvando o E-mail
                escritor.putString("email", email);
                //Salvando o manterLogado
                escritor.putBoolean("manterLogado", manterLogado);

                escritor.commit(); //Salva em Disco


                Intent intent = new Intent(NovoUsuario.this, Pick_Contacts.class);
                intent.putExtra("usuario",user);
                startActivity(intent);

                //Mesmo após chamar de um startActivity, o método continuará execuntando
                //Por exemplo, aqui mataremos a Activity atual, porém a Pick_Contacts é que será exibida
                Log.v("PDMv2", "passei do StartActivity");
                finish();//mata a tela de criar novo usuário
            }
        });
    }
}