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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_contatos);

        bnv = (BottomNavigationView) findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);

        lv = (ListView) findViewById(R.id.listView1);
        preencherListaDeContatos(); //Montagem do ListView
        //AULA ANTERIOR À ANTERIOR (20/08)
        //Dados da Intent Anterior
        /*Intent quemChamou = this.getIntent();//recupera a intent que chamou essa activity
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuário
                User u1 = (User) params.getSerializable("usuario");//recupera a chave usuario
                if (u1 != null) {
                    Log.v("PDMUSER", u1.getNome());
                    Log.v("PDMUSER", u1.getLogin());
                    Log.v("PDMUSER", u1.getSenha());
                    Log.v("PDMUSER", u1.getEmail());
                }
            }
        }*/
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

    protected void preencherListaDeContatos() {
        //EXEMPLO DO INÍCIO DA AULA ANTERIOR (25/08)
        //Vamos montar o ListView
        /*Contato c1, c2;
        c1 = new Contato();
        c1.setNome("Neymar");
        c1.setNumero("tel:88888888");
        c2 = new Contato();
        c2.setNome("Messi");
        c2.setNumero("tel:777777777");

        final ArrayList<Contato> contatos;
        contatos = new ArrayList<Contato>();
        contatos.add(c1);
        contatos.add(c2);*/


        SharedPreferences recuperarContatos = getSharedPreferences("contatos2", Activity.MODE_PRIVATE);
        int num = recuperarContatos.getInt("numContatos", 0);
        final ArrayList<Contato> contatos = new ArrayList<Contato>();//final = variável constante, para ser vista nas inner classes

        Contato contato;
        for (int i = 1; i <= num; i++) {
            //objeto serializado pego no SharedPreferences
            String objSel = recuperarContatos.getString("contato" + i, "");
            //dentro da String objSel está um array de bytes codificado em ISO 8859-1
            //que pode ser usado para recuperar um objeto Contato
            if (objSel.compareTo("") != 0) {
                try {
                    //InputStream = para leitura
                    //objeto serializado transformado num ByteArrayInputStream
                    ByteArrayInputStream bis = new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    //para transformar esse array de bytes em objeto
                    //ler esse array de bytes como um objeto
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    //a partir do array de bytes, readObject retorna uma instância de objeto
                    //*dentro desse array, tem especificando qual é a classe, por isso a máquina virtual consegue
                    //recuperar esses dados como se fosse um Contato*
                    contato = (Contato) ois.readObject();//contato recuperado
                    if (contato != null) {
                        contatos.add(contato);
                        //Log.v("PDM", contato.getNome());
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
            final String[] nomesSP, telsSP;
            nomesSP = new String[contatos.size()];
            Contato c;
            for (int i = 0; i < contatos.size(); i++) {
                nomesSP[i] = contatos.get(i).getNome();
            }
            ArrayAdapter<String> adaptador;
            adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomesSP);
            lv.setAdapter(adaptador);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (checarPermissaoPhone_SMD()) {
                        //não se consegue enxergar a variável "contatos" na inner class a menos que ela seja "final"
                        Uri uri = Uri.parse(contatos.get(i).getNumero());
                        //Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);//só disca
                        Intent itLigar = new Intent(Intent.ACTION_CALL, uri);//realiza a ligação
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

    protected boolean checarPermissaoPhone_SMD() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Log.v("SMD", "Tenho permissão");
            return true;
        } else {
            //este método retorna verdadeiro quando é a 1ª vez ou falso quando é a 1ª vez?
            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                Log.v("SMD", "Primeira Vez");
                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão aparecerá.";
                String titulo = "Permissão de acesso a chamadas";
                int codigo = 1;
                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, codigo);
                //serve para fazer um método callback
                //quando apertar ok na janela de explicação, retornará essa informação para um método da Activity
                mensagemPermissao.onAttach((Context)this);//passa uma referência para essa classe; essa classe pede uma instância de Context
                mensagemPermissao.show(getSupportFragmentManager(),"primeiravez2");
            } else {
                Log.v("SMD", "Primeira Vez");
                String mensagem = "De novo: nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão aparecerá.";
                String titulo = "Permissão de acesso a chamadas 2";
                int codigo = 1;
                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, codigo);
                mensagemPermissao.onAttach((Context)this);
                mensagemPermissao.show(getSupportFragmentManager(),"segundavez2");
                Log.v("SMD", "Outra vez");
            }
        }

        return false;
    }

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2222:
                //Toast.makeText(this, "VOLTOU DA JANELA DE PERMISSÃO", Toast.LENGTH_LONG).show();
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "CONCEDIDO", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "NEGADO", Toast.LENGTH_LONG).show();
                    String mensagem = "Pela 2ª vez, este app serve para ligar, mas se negar a permissão, aí fica inútil, né, meu consagrado?";
                    String titulo = "Por que precisamos desta permissão?";
                    UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, 2);
                    mensagemPermissao.onAttach((Context) this);
                    mensagemPermissao.show(getSupportFragmentManager(), "segundavez");
                }
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //FUNCIONARIA SE NÃO FOSSE A LINHA DO getSerializable DE PICK_CONTACTS
        //Log.v("PDM3", item.toString());
        //checa se o Item selecionado é o do Perfil
        if (item.getItemId() == R.id.anvPerfil) {
            //abre a tela de Alterar Usuário
            Intent intent = new Intent(this, AlterarUsuario.class);
            intent.putExtra("usuario", user);
            //startActivity(intent);
            startActivityForResult(intent, 1111);
        }

        //checa se o Item selecionado é o de Mudar
        if (item.getItemId() == R.id.anvMudar) {
            //abre a tela de Mudar Contatos
            Intent intent = new Intent(this, Pick_Contacts.class);
            intent.putExtra("usuario", user);
            startActivityForResult(intent, 1112);
        }

        return true;//TROCADO DE FALSE
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //caso seja um Voltar ou Sucesso, selecionar o item Ligar
        if (requestCode == 1111) {//retorno de Mudar Perfil
            bnv.setSelectedItemId(R.id.anvLigar);
        }
        if (requestCode == 1112) {//retorno de Mudar Contatos
            bnv.setSelectedItemId(R.id.anvLigar);
            preencherListaDeContatos();
        }
    }


    protected ArrayList<Contato> montarListaDeContatosPorSerializacaoJava() {
        SharedPreferences salvaContatos = getSharedPreferences("contatos", Activity.MODE_PRIVATE);
        int num = salvaContatos.getInt("numContatos", 0);
        ArrayList<Contato> contatos = new ArrayList<>();
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

    @Override
    public void onDialogPositiveClick(int codigo) {
        if (codigo == 1){
            String[] permissions = {Manifest.permission.CALL_PHONE};
            requestPermissions(permissions, 2222);
        }
        //Log.v("SMD","Clicou no OK");
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