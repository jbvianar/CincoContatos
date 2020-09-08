package com.pdm.cincocontatos.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.pdm.cincocontatos.R;
import com.pdm.cincocontatos.model.User;

public class AlterarUsuario extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //boolean primeiraVezNome = true;
   // boolean primeiraVezUser = true;
    //boolean primeiraVezSenha = true;
   // boolean primeiraVezEmail = true;
    TextInputEditText edUser,edPass,edNome,edEmail;
    Button btAlterar;
    Switch swLogado;

    BottomNavigationView bnv;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_usuario);

        bnv = (BottomNavigationView) findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.anvPerfil);//deixa selecionado o ícone de Perfil
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
                //Salvando as informacoes do usuario
                escritor.putString("nome", nome);
                escritor.putString("senha", senha);
                escritor.putString("login", login);
                escritor.putString("email", email);
                escritor.putBoolean("manterLogado", manterLogado);

                escritor.commit(); //Salva em Disco - NÃO ESQUECER

                Toast.makeText(AlterarUsuario.this, "Usuário alterado com sucesso", Toast.LENGTH_LONG).show();

                Log.v("PDMv2", "passei do StartActivity");
                finish();//mata a tela de alterar usuário
            }
        });

        //Dados da Intent anterior
        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuário
                user = (User) params.getSerializable("usuario");
                if (user != null) {
                    Log.v("pdm", user.getNome());
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //checa se o Item selecionado é o de Ligar
        if(item.getItemId() == R.id.anvLigar){
            //Intent intent = new Intent(this, ListaDeContatos_ListView.class);
            Toast.makeText(this, "Selecione um contato para ligar", Toast.LENGTH_LONG).show();
            Log.v("PDMv2", "Fechar a activity Alterar Usuário");
            finish();
        }
        //checa se o Item selecionado é o de Alterar Usuário - NÃO TEM FUNÇÃO NESTA ACTIVITY
        /*if (item.getItemId() == R.id.anvPerfil) {
            //abre a tela de Alterar Usuário
            Toast.makeText(this, "RESET", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "Você já está na tela de Alterar Usuário", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, AlterarUsuario.class);
            intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            //startActivity(intent);
            Log.v("PDMv2", "Criando nova Activity Alterar Usuário - Reset");
            startActivityForResult(intent, 1111);//abre com código 1111 (Alterar Usuário) uma Activity filha da qual se espera dados
            Log.v("PDMv2", "Matando Activity Alterar Usuário anterior - Reset");
            finish();
        }*/
        //checa se o Item selecionado é o de Definir Contatos
        if (item.getItemId() == R.id.anvMudar) {
            //abre a tela de Pegar os Contatos
            Intent intent = new Intent(this, Pick_Contacts.class);
            intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            startActivityForResult(intent, 1112);//abre com código 1112 (Mudar Contatos) uma Activity filha da qual se espera dados
            Log.v("PDMv2", "Matou Alterar Usuário");
            finish();
        }

        return true;//TROCADO DE FALSE
    }
}