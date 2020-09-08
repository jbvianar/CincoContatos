package com.pdm.cincocontatos.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.pdm.cincocontatos.R;
import com.pdm.cincocontatos.model.Contato;
import com.pdm.cincocontatos.model.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Pick_Contacts extends AppCompatActivity implements UIEducacionalPermissao.NoticeDialogListener, BottomNavigationView.OnNavigationItemSelectedListener {

    User user;
    TextView tv;
    TextInputEditText edtNome;
    boolean primeiraVezNome = true;
    ListView lv;
    BottomNavigationView bnv;

    //INÍCIO DA AULA ANTERIOR (25/08)
    //Button btSalvar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);

        bnv = (BottomNavigationView) findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.anvMudar);//deixa selecionado o ícone de Mudar
        tv = (TextView) findViewById(R.id.MessageIntent);
        lv = (ListView) findViewById(R.id.listView2);
        edtNome = (TextInputEditText ) findViewById(R.id.edtBusca);
        //Evento de limpar Componente
        edtNome.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezNome) {
                    primeiraVezNome = false;
                    edtNome.setText("");
                }
                return false;
            }
        });
        //Pegando parâmetros, dados do Intent anterior
        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o usuário
                user = (User) params.getSerializable("usuario");
                if (user != null) {
                    tv.setText(user.getNome());//Substitui o texto do TextView pelo nome do User
                }
            }
        }
    }



    //ÚTIL POSTERIORMENTE
    protected int mostrarListaDeContatos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            Log.v("PDM", "Pedir permissão");
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
            return 0;
        }
        final ArrayList<Contato> contatosCel = new ArrayList<Contato>();/////////////////////////////////////////////////////////
        Contato c;// = new Contato();/////////////////////////////////////////////////////////////////////////////////////////////////
        Log.v("PDM", "Tenho permissão");
        //Projection, os campos que você quer
        String[] projection = new String[]
                {
                        ContactsContract.Profile._ID,
                        ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
                        ContactsContract.Profile.LOOKUP_KEY,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI
                };
        String selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";//consulta
        String[] selectionArguments = {"%" + edtNome.getText() + "%"};//{"K%"};//{"%" + edtNome.getText() + "%"};//argumentosConsulta
        ContentResolver cr = getContentResolver();
        //Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArguments, null);
        int indexDisplayName = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);//indiceNome
        while (cursor.moveToNext()) {
            c = new Contato();/////////////////////////////////////////////
            //String given = cursor.getString(indexGivenName);
            //String family = cursor.getString(indexFamilyName);
            String display = cursor.getString(indexDisplayName);
            //Ler nome
            String contatoNome = cursor.getString(indexDisplayName);
            c.setNome(contatoNome);//////////////////////////////////////////////////////
            Log.v("PDM", "Nome do Contato: " + contatoNome);//+ ", " + given + " " + family);
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //
            //Set all phone numbers
            //ContactsContract.CommonDataKinds.Phone._ID + " = " + contactId;<---antes
            Cursor phones = cr.query
                    (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                Log.v("PDM", "entrei");
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here
                        Log.v("PDM", "Tipo: " + ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
                        c.setNumero("tel:+" + number);////////////////////////////////////////////////
                        Log.v("PDM", "Tel do (" + contactId + "): " + contatoNome + ": " + number);
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        // do something with the Mobile number here
                        Log.v("PDM", "Tipo: " + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                        c.setNumero("tel:+" + number);////////////////////////////////////////////////
                        Log.v("PDM", "Tel do (" + contactId + "): " + contatoNome + ": " + number);
                        break;
                }
                contatosCel.add(c);////////////////////
            }
            phones.close();//sempre fechar cursor depois de usar
        }
        cursor.close();//sempre fechar cursor depois de usar
        return 1;
    }

    //Busca um contato na lista de contatos do cel, mas por enquanto está salvando esse contato na lista de contatos do app, precisa tratar
    public void onClickBuscar(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            Log.v("PDM", "Pedir permissão contatos");
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
            //colocar caixa de diálogo educacional aqui
            return;//pedir a permissão e sair; da próxima ver que clicar, já tem a permissão
        }
        Log.v("PDM", "Tenho permissão contatos");
        SharedPreferences escritorContatos = getSharedPreferences("listaCel", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = escritorContatos.edit();
        ArrayList<Contato> contatosCel = new ArrayList<Contato>();
        Contato c;
        //para acessar o provedor de conteúdo
        ContentResolver cr = getContentResolver();
        String consulta = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";//dizer qual campo quer consultar
        String[] argumentosConsulta = {"%" + edtNome.getText() + "%"};
        Cursor cursor = cr.query//permite fazer consultas na tabela de contatos, retorna um cursor
                (ContactsContract.Contacts.CONTENT_URI, null,
                        consulta, argumentosConsulta, null);
        try {
            Log.v("PDM", "Primeiro try");
            ByteArrayOutputStream dt;
            ObjectOutputStream oos;
            int i = 0;
            while (cursor.moveToNext()) {
                i++;
                Log.v("PDM", "Primeiro while de cursor");
                dt = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(dt);
                c = new Contato();
                //índice da coluna Nome - apenas o parâmetro DISPLAY_NAME
                int indiceNome = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);//retorna a coluna desejada abaixo
                //cursor aponta para uma tupla, precisa dar o nome da coluna em que se vai fazer consulta
                String contatoNome = cursor.getString(indiceNome);//passa-se o inteiro que identifica essa coluna
                Log.v("PDM", "Contato " + i + " - Nome: " + contatoNome);
                c.setNome(contatoNome);

                //retorna o índice do ID do contato
                int indiceContatoID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
                //precisa dizer a coluna onde está o contato
                String contactID = cursor.getString(indiceContatoID);//pega o valor do índice do ID
                //precisa recuperar o ID desse contato que se está percorrendo
                String consultaPhone = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;//monta a consulta
                //pesquisa na tabela de telefones, mas precisa dizer de quem
                Cursor phones = cr.query//consulta a tabela de telefones com esse ID
                        (ContactsContract.CommonDataKinds.Phone.CONTENT_URI,//cursor que tem todos os telefones desse user específico
                                null, consultaPhone, null, null);
                int j = 0;
                while (phones.moveToNext()) {
                    j++;
                    //Log.v("PDM", " Telefone " + j);
                    //usa o índice do CommonDataKinds.Phone.NUMBER
                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    c.setNumero("tel:+" + number);
                    contatosCel.add(c);
                    Log.v("PDM", " Telefone " + j + ": " + number);
                }
                phones.close();//sempre fechar cursor depois de usar
                oos.writeObject(contatosCel.get(i - 1));
                String contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
                //Log.v("PDM", "Contato serializado: " + contatoSerializado);
                editor.putString("contato" + i, contatoSerializado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        editor.commit();
        cursor.close();
        Cursor cursor2 = cr.query//permite fazer consultas na tabela de contatos, retorna um cursor
                (ContactsContract.Contacts.CONTENT_URI, null,
                        consulta, argumentosConsulta, null);
        SharedPreferences leitorContatos = getSharedPreferences("listaCel", Activity.MODE_PRIVATE);
        final ArrayList<Contato> contacts = new ArrayList<Contato>();
        Contato contato;
        try {
            Log.v("PDM", "Segundo try");
            ByteArrayInputStream bis;
            ObjectInputStream ois;
            int k = 0;
            while (cursor2.moveToNext()) {
                k++;
                Log.v("PDM", "While do cursor2");
                String objSel = leitorContatos.getString("contato" + k, "");//pega cada contato do array de contatos salvos no editor
                if (objSel.compareTo("") != 0) {
                    bis = new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ois = new ObjectInputStream(bis);
                    contato = (Contato) ois.readObject();//contato recuperado
                    if (contato != null) {
                        contacts.add(contato);
                        Log.v("PDM", contato.getNome());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor2.close();//sempre fechar cursor depois de usar
        if (contacts != null) {
            final String[] nomeTel;
            final int[] numContatos;
            nomeTel = new String[contacts.size()];
            numContatos = new int[1];
            for (int i = 0; i < contacts.size(); i++) {
                nomeTel[i] = contacts.get(i).getNome() + " - " + contacts.get(i).getNumero();
            }
            ArrayAdapter<String> adaptador;//ligação entre a View e os dados (tomada)
            adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomeTel);
            lv.setAdapter(adaptador);
            numContatos[0] = 0;
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (numContatos[0] < 5) {
                        try {
                            numContatos[0]++;
                            SharedPreferences salvaContatos = getSharedPreferences("contatos2", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = salvaContatos.edit();

                            editor.putInt("numContatos", numContatos[0]); // alterou o nº de contatos, tem que lidar com isso
                            // ou colocar sempre 5 e ver qual o contato que está sendo alterado
                            ByteArrayOutputStream dt = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(dt);

                            //editor.putInt("numContatos", numContatos[0]);
                            oos.writeObject(contacts.get(i));
                            String contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
                            Log.v("PDM", "i = " + i);
                            Log.v("PDM", "Número de contatos: " + numContatos[0]);
                            Log.v("PDM", contacts.get(i).getNome());
                            Log.v("PDM", contacts.get(i).getNumero());
                            editor.putString("contato" + numContatos[0], contatoSerializado);
                            Toast.makeText(Pick_Contacts.this, "Contato adicionado", Toast.LENGTH_LONG).show();

                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(Pick_Contacts.this, "Sua lista de contatos está cheia", Toast.LENGTH_LONG).show();
                    }
                }
                //}
            });
        }
    }


    //Por enquanto está salvando uma lista de contatos padrão - precisa tratar
    public void cliquedoSalvar(View v) {
        final ArrayList<Contato> contatos = new ArrayList<Contato>();
        final ArrayList<String> numeros = new ArrayList<String>();
        final ArrayList<String> nomes = new ArrayList<String>();
        numeros.add("tel:+123456789");
        nomes.add("Jaspion");
        numeros.add("tel:+123456780");
        nomes.add("Jiraiya");
        numeros.add("tel:+123456781");
        nomes.add("Jiban");
        Contato c;
        for (int k = 1; k <= 3; k++) {
            c = new Contato();
            c.setNumero(numeros.get(k - 1));
            c.setNome(nomes.get(k - 1));
            contatos.add(c);
        }
        for (int j = 1; j <= 3; j++) {
            Log.v("PDM", contatos.get(j - 1).getNome() + " - " + contatos.get(j - 1).getNumero());
        }
        //SALVA OS CONTATOS PADRÃO COLOCADOS NO CÓDIGO: Jaspion, Jiraiya, Jiban - precisa tratar
        SharedPreferences salvaContatos = getSharedPreferences("contatos2", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = salvaContatos.edit();
        editor.putInt("numContatos", 0);

        try {//OutputStream = escrever
            ByteArrayOutputStream dt;
            ObjectOutputStream oos;
            for (int i = 1; i <= 5; i++) {
                dt = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(dt);
                oos.writeObject(null);
                //oos.writeObject(contatos.get(i - 1));
                String contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
                //Log.v("PDM", "Contato Serializado" + contatoSerializado);
                editor.putString("contato" + i, contatoSerializado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
        Toast.makeText(Pick_Contacts.this, "Sua lista de 5 contatos foi limpa", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("PDM", "Matando a Activity Lista de Contatos");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("PDM", "Matei a Activity Lista de Contatos");
    }


    //Método que checa se a permissão para ler contatos do celular já foi concedida
    //Retorna true se sim e false se não
    protected boolean checarPermissaoContacts() {
        //MÉTODO QUE CHECA SE PERMISSÃO JÁ ESTÁ CONCEDIDA
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Log.v("SMD", "Tenho permissão READ_CONTACTS");
            return true;//PERMISSÃO JÁ CONCEDIDA
        } else {//PERMISSÃO NÃO ESTÁ CONCEDIDA
            Log.v("SMD", "Primeira Vez READ_CONTACTS");
            String mensagem = "Nossa aplicação precisa acessar seus contatos para registrá-los. Uma janela de permissão aparecerá.";
            String titulo = "Permissão de acesso a contatos";
            int codigo = 3;//CÓDIGO DO DIÁLOGO DE CONCEDER PERMISSÃO
            //Monta uma UIEducacional antes de abrir a janela de permissão
            UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, codigo);
            //serve para fazer um método callback
            //quando apertar ok na janela de explicação, retornará essa informação para um método da Activity
            mensagemPermissao.onAttach((Context) this);//passa uma referência para essa classe; essa classe pede uma instância de Context
            mensagemPermissao.show(getSupportFragmentManager(), "primeiravez2");
            /*} else {//POR ENQUANTO ESTA PARTE DO CÓDIGO NÃO É CHAMADA
                Log.v("SMD", "Segunda Vez");
                String mensagem = "De novo: nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão aparecerá.";
                String titulo = "Permissão de acesso a contatos 2";
                int codigo = 2;//CÓDIGO DO DIÁLOGO DE CONCEDER PERMISSÃO
                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, codigo);
                mensagemPermissao.onAttach((Context)this);
                mensagemPermissao.show(getSupportFragmentManager(),"segundavez2");
                Log.v("SMD", "Outra vez");
            }*/
        }
        return false;
    }

    //Método chamado quando a janela de conceder permissão de ler contatos é respondida
    //Serve para mostrar o feedback da resposta na tela
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 4444://CÓDIGO DE RETORNO DA JANELA DE PERMISSÃO DE LER CONTATOS
                //Toast.makeText(this, "VOLTOU DA JANELA DE PERMISSÃO", Toast.LENGTH_LONG).show();
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "PERMISSÃO PARA LER CONCEDIDA", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "PERMISSÃO PARA LER NEGADA", Toast.LENGTH_LONG).show();
                    //Mensagem que será mostrada na UIEducacional quando a permissão para ligar para um contato é negada
                    String mensagem = "Se não tivermos acesso a seus contatos, não poderemos adicioná-los à lista de contatos de emergência.";
                    String titulo = "Por que precisamos desta permissão?";//Título da UIEducacional em questão
                    //Monta uma UIEducacional após a janela de permissão ser fechada com resposta negativa
                    UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, 4);
                    mensagemPermissao.onAttach((Context) this);
                    mensagemPermissao.show(getSupportFragmentManager(), "segundavez");
                }
                break;
        }
    }

    //Método da Interface UIEducacionalPermissao.NoticeDialogListener
    //Método responsável por abrir a janela de conceder permissão de ler contatos do cel
    @Override
    public void onDialogPositiveClick(int codigo) {
        if (codigo == 3) {
            String[] permissions = {Manifest.permission.READ_CONTACTS};
            requestPermissions(permissions, 4444);
        }
        Log.v("SMD", "Clicou no OK");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //checa se o Item selecionado é o de Ligar
        if (item.getItemId() == R.id.anvLigar) {
            //Intent intent = new Intent(this, ListaDeContatos_ListView.class);
            Toast.makeText(this, "Selecione um contato para ligar", Toast.LENGTH_LONG).show();
            Log.v("PDMv2", "Matou Mudar Contatos");
            finish();
        }
        //checa se o Item selecionado é o de Alterar Usuário
        if (item.getItemId() == R.id.anvPerfil) {
            //abre a tela de Alterar Usuário
            Intent intent = new Intent(this, AlterarUsuario.class);
            intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            //startActivity(intent);
            startActivityForResult(intent, 1111);//abre com código 1111 (Alterar Usuário) uma Activity filha da qual se espera dados
            Log.v("PDMv2", "Matou Mudar Contatos");
            finish();
        }
        //checa se o Item selecionado é o de Mudar Contatos
        /*if (item.getItemId() == R.id.anvMudar) {
            //abre a tela de Mudar Contatos
            Toast.makeText(this, "Você já está na tela de Mudar Contatos", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(this, Pick_Contacts.class);
            //intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            //Log.v("PDMv2", "Criando nova Activity Mudar Contatos - Reset");
            //startActivityForResult(intent, 1112);//abre com código 1112 (Mudar Contatos) uma Activity filha da qual se espera dados
            //Log.v("PDMv2", "Matando Activity Mudar Contatos anterior - Reset");
            //finish();
        }*/
        //Log.v("PDMv2", "Em direção à morte de Mudar Contatos");
        //finish();
        return true;//TROCADO DE FALSE
    }

}