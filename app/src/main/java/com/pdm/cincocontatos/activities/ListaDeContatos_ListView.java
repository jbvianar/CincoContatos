package com.pdm.cincocontatos.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pdm.cincocontatos.R;
import com.pdm.cincocontatos.model.Contato;
import com.pdm.cincocontatos.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class ListaDeContatos_ListView extends AppCompatActivity implements UIEducacionalPermissao.NoticeDialogListener, BottomNavigationView.OnNavigationItemSelectedListener {

    ListView lv;
    //AULA ANTERIOR À ANTERIOR (20/08)
    /*String[] itens = {"Filha", "Filho", "Netinho"};
    String[] numeros = {"tel:000000003435", "tel:2000348835", "tel:1003435888"};*/

    BottomNavigationView bnv;
    User user;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_contatos);

        bnv = (BottomNavigationView) findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);

        lv = (ListView) findViewById(R.id.listView1);
        preencherListaDeContatos(); //Montagem do ListView

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

    protected void preencherListaDeContatos() {//RECUPERA OS CONTATOS PADRÃO COLOCADOS NO CÓDIGO: Jaspion, Jiraiya, Jiban
        SharedPreferences recuperarContatos = getSharedPreferences("contatos2", Activity.MODE_PRIVATE);
        int num = recuperarContatos.getInt("numContatos", 0);
        final ArrayList<Contato> contatos = new ArrayList<Contato>();//final = variável constante, para ser vista nas inner classes
        Contato contato;
        for (int i = 1; i <= num; i++) {
            String objSel = recuperarContatos.getString("contato" + i, "");//pega cada contato do array de contatos salvos no editor
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    contato = (Contato) ois.readObject();//contato recuperado
                    if (contato != null) {
                        contatos.add(contato);
                        Log.v("PDM", contato.getNome());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }

        if (contatos != null) {
            final String[] nomesSP;
            nomesSP = new String[contatos.size()];
            for (int i = 0; i < contatos.size(); i++) {
                nomesSP[i] = contatos.get(i).getNome();
            }
            ArrayAdapter<String> adaptador;//ligação entre a View e os dados (tomada)
            adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomesSP);
            lv.setAdapter(adaptador);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//O DENIED VOCÊ JÁ TEM POR PADRÃO
                    uri = Uri.parse(contatos.get(i).getNumero());
                    if (checarPermissaoPhone_SMD()) {//se a permissão foi concedida, a ligação será feita

                        //Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);//só disca
                        Intent itLigar = new Intent(Intent.ACTION_CALL, uri);//realiza a ligação -- esta linha não pode ser chamada sem a permissão
                        startActivity(itLigar);
                    }
                }
            });
        }


        /*AULA ANTERIOR À ANTERIOR (20/08)
        ArrayAdapter<String> adapter;//criou um array adapter
        //cria um array adapter que precisa de um contexto, layout e um conjunto de Strings
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itens);
        lv.setAdapter(adapter);//coloca as informações acima no ListView
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {//Evento não só no list, mas dentro do adapter
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //i diz qual foi o item que foi clicado
                //Toast.makeText(ListaDeContatos_ListView.this, "Clicou no Item: "+i, Toast.LENGTH_LONG).show();
                Intent intent;

                Uri uri = Uri.parse(numeros[i]);
                intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });*/
    }

    //Método da Interface UIEducacionalPermissao.NoticeDialogListener
    //Método responsável por abrir a janela de conceder permissão de ligar para contato
    @Override
    public void onDialogPositiveClick(int codigo) {
        if (codigo == 1) {
            String[] permissions = {Manifest.permission.CALL_PHONE};
            requestPermissions(permissions, 2222);
        }
        Log.v("SMD","Clicou no OK");

        if (codigo == 2) { //O cara negou e a gente explicou
            Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);//só disca

            startActivity(itLigar);
        }
    }

    //Método chamado quando a janela de conceder permissão de ligar para um contato é respondida
    //Serve para mostrar o feedback da resposta na tela
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2222://CÓDIGO DE RETORNO DA JANELA DE PERMISSÃO DE LIGAR PARA CONTATO
                //Toast.makeText(this, "VOLTOU DA JANELA DE PERMISSÃO", Toast.LENGTH_LONG).show();
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "PERMISSÃO PARA LIGAR CONCEDIDA", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "PERMISSÃO PARA LIGAR NEGADA", Toast.LENGTH_LONG).show();
                    //Mensagem que será mostrada na UIEducacional quando a permissão para ligar para um contato é negada
                    String mensagem = "Este app é para fazer ligação, se voce negar a permissão, aí fica sem utilidade. Favor permitir";
                    String titulo = "Por que precisamos desta permissão?";//Título da UIEducacional em questão
                    //Monta uma UIEducacional após a janela de permissão ser fechada com resposta negativa
                    UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, 2);
                    mensagemPermissao.onAttach((Context) this);
                    mensagemPermissao.show(getSupportFragmentManager(), "segundavez");// contém onDialogPositiveClick
                }
                break;
        }
    }

    //Método que checa se a permissão para ligar para um contato já foi concedida
    //Retorna true se sim e false se não
    protected boolean checarPermissaoPhone_SMD() {
        //MÉTODO QUE CHECA SE PERMISSÃO JÁ ESTÁ CONCEDIDA
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Log.v("SMD", "Tenho permissão CALL_PHONE");
            return true;//PERMISSÃO JÁ CONCEDIDA
        } else {//PERMISSÃO NÃO ESTÁ CONCEDIDA
            //>este método retorna verdadeiro quando é a 1ª vez ou falso quando é a 1ª vez?<
            /*According to my understanding shouldShowRequestPermissionRationale() method returns false in three cases:
            1. If we call this method very first time before asking permission;
            2. If user selects "Don't ask again" and deny permission;
            3. If the device policy prohibits the app from having that permission.
             */
            //O método a seguir deve ser chamado quando checkSelfPermission retorna PERMISSION_DENIED, ou seja, permissão não concedida ainda
            //serve para mostrar uma UI Educacional antes de abrir a janela de permissão
            /*if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {*///true se esta permissão não foi concedida
            Log.v("SMD", "Não tenho permissão CALL_PHONE");
            String mensagem = "Este App precisa acessar o telefone para discagem automática. Uma Solicitação de permissão aparecerá.";
            String titulo = "Permissão de acesso a chamadas";
            int codigo = 1;//CÓDIGO DO DIÁLOGO DE CONCEDER PERMISSÃO
            //Monta uma UIEducacional antes de abrir a janela de permissão
            UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, codigo);
            //serve para fazer um método callback
            //quando apertar ok na janela de explicação, retornará essa informação para um método da Activity
            mensagemPermissao.onAttach((Context) this);//passa uma referência para essa classe; essa classe pede uma instância de Context
            //MOSTRA A JANELA DE PERMISSÃO - contém onDialogPositiveClick
            mensagemPermissao.show(getSupportFragmentManager(), "primeiravez2");
            /*} else {//POR ENQUANTO ESTA PARTE DO CÓDIGO NÃO É CHAMADA
                Log.v("SMD", "Segunda Vez");
                String mensagem = "De novo: nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão aparecerá.";
                String titulo = "Permissão de acesso a chamadas 2";
                int codigo = 1;//CÓDIGO DO DIÁLOGO DE CONCEDER PERMISSÃO
                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, codigo);
                mensagemPermissao.onAttach((Context)this);
                mensagemPermissao.show(getSupportFragmentManager(),"segundavez2");
                Log.v("SMD", "Outra vez");
            }*/
        }
        return false;
    }
    /*
    protected boolean checarPermissaoPhone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Log.v("SMD", "PERMISSÃO CONCEDIDA");
            return true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                //Retorna TRUE
                Log.v("SMD", "1ª vez que pergunto");
                String mensagem = "Amigão, tô pedindo pela 1ª vez, não tem como ligar se não tiver permissão.";
                String titulo = "Permissão de acesso a chamadas";
                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, 1);
                mensagemPermissao.onAttach((Context) this);
                mensagemPermissao.show(getSupportFragmentManager(), "primeiravez");
            } else {
                Log.v("SMD", "Já perguntei antes");
                //Requisitando novamente
                String[] permissions = {Manifest.permission.CALL_PHONE};
                this.requestPermissions(permissions, 1212);
            }
            String[] permissoes = {Manifest.permission.CALL_PHONE};
            ActivityCompat.requestPermissions(this, permissoes, 1212);
        }
        return false;
    }*/

    //Abre uma nova Activity dependendo do Item selecionado na BottomNavigationBar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //FUNCIONARIA SE NÃO FOSSE A LINHA DO getSerializable DE PICK_CONTACTS
        //Log.v("PDM3", item.toString());
        //checa se o Item selecionado é o de Ligar
        /*if(item.getItemId() == R.id.anvLigar){
            //Intent intent = new Intent(this, ListaDeContatos_ListView.class);
            Log.v("PDMv2", "Já está na tela de Lista de Contatos");
        }*/
        //checa se o Item selecionado é o de Alterar Usuário
        if (item.getItemId() == R.id.anvPerfil) {
            //abre a tela de Alterar Usuário
            Intent intent = new Intent(this, AlterarUsuario.class);
            intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            //startActivity(intent);
            startActivityForResult(intent, 1111);//abre com código 1111 (Alterar Usuário) uma Activity filha da qual se espera dados
        }
        //checa se o Item selecionado é o de Mudar Contatos
        if (item.getItemId() == R.id.anvMudar) {
            //abre a tela de Mudar Contatos
            Intent intent = new Intent(this, Pick_Contacts.class);
            intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            startActivityForResult(intent, 1112);//abre com código 1112 (Mudar Contatos) uma Activity filha da qual se espera dados
        }
        return true;//TROCADO DE FALSE
    }

    //Método callback que lida com os dados recebidos da Activity filha quando ela se fecha
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //caso seja um Voltar ou Sucesso, selecionar o item Ligar
        if (requestCode == 1111) {//código de retorno de Alterar Usuário
            bnv.setSelectedItemId(R.id.anvLigar);//deixa o botãozinho de Ligar selecionado
        }
        if (requestCode == 1112) {//código de retorno de Mudar Contatos
            bnv.setSelectedItemId(R.id.anvLigar);//deixa o botãozinho de Ligar selecionado
            preencherListaDeContatos();//atualiza a Lista de Contatos
        }
    }

    //ÚTIL POSTERIORMENTE
    protected ArrayList<Contato> montarListaDeContatosPorSerializacaoJava() {
        SharedPreferences salvaContatos = getSharedPreferences("contatos", Activity.MODE_PRIVATE);
        int num = salvaContatos.getInt("numContatos", 0);
        ArrayList<Contato> contatos = new ArrayList<Contato>();
        Contato c;
        for (int i = 1; i <= num; i++) {
            String objSel = salvaContatos.getString("contato" + i, "");
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream dis = new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream ois = new ObjectInputStream(dis);
                    c = (Contato) ois.readObject();
                    if (c != null) {
                        contatos.add(c);
                        //Log.v("PDM", c.getNome());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return contatos;
    }


    /*
    protected int mostrarListaDeContatos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) {
            Log.v("PDM", "Pedir permissão");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS});
            return 0;
        }
    }*/


}