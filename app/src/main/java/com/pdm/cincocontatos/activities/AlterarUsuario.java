package com.pdm.cincocontatos.activities;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.pdm.cincocontatos.R;

public class AlterarUsuario extends AppCompatActivity {
    //FUNCIONANDO PERFEITAMENTE ATÉ INSERIR A LINHA DO getSerializable DO PICK_CONTACTS
    boolean primeiraVezNome = true;
    boolean primeiraVezUser = true;
    boolean primeiraVezSenha = true;
    boolean primeiraVezEmail = true;
    TextInputEditText edUser,edPass,edNome,edEmail;
    Button btAlterar;
    Switch swLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_usuario);
        Log.v("PDMv2", "alterar");
        btAlterar = (Button) findViewById(R.id.btCriar);
        edUser = (TextInputEditText) findViewById(R.id.edT_Login2);
        edPass = (TextInputEditText) findViewById(R.id.edt_Pass2);
        edNome = (TextInputEditText) findViewById(R.id.edtNome);
        edEmail = (TextInputEditText) findViewById(R.id.edEmail);
        swLogado = (Switch) findViewById(R.id.swLogado);

        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String nomeSalvo = temUser.getString("nome", "");
        String loginSalvo = temUser.getString("login", "");
        String senhaSalva = temUser.getString("senha", "");
        String emailSalvo = temUser.getString("email", "");
        //boolean manterLogadoSalvo = temUser.getBoolean("manterLogado", false);
        //Tudo preenchido para ser alterado
        edNome.setText(nomeSalvo);
        edUser.setText(loginSalvo);
        edPass.setText(senhaSalva);
        edEmail.setText(emailSalvo);
        /*
        //Evento de limpar Componente
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
        //Evento de limpar Componente

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
        });*/

        btAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Evento do Botão Alterar
                String nome, login, senha, email;
                boolean manterLogado;
                nome = edNome.getText().toString();
                login = edUser.getText().toString();
                senha = edPass.getText().toString();
                email = edEmail.getText().toString();
                manterLogado = swLogado.isChecked();

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

                Toast.makeText(AlterarUsuario.this, "Usuário alterado com sucesso", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(AlterarUsuario.this, ListaDeContatos_ListView.class);
                //startActivity(intent);
                //NÃO PASSA DAQUI POR CAUSA DA LINHA DO getSerializable DE PICK_CONTACTS
                //Mesmo após a chamar de um startActivity o método continuará execuntando
                //Matando a Activity atual ao passar para a Pick_Contacts
                Log.v("PDMv2", "passei do StartActivity");
                finish();//mata a tela de alterar usuário
            }
        });
    }
}