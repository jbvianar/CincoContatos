package com.pdm.cincocontatos.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.pdm.cincocontatos.R;
import com.pdm.cincocontatos.model.Contato;
import com.pdm.cincocontatos.model.User;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class Pick_Contacts extends AppCompatActivity {

    User user;
    TextView tv;
    private TextInputEditText edtNome ;
    boolean primeiraVezNome = true;

    //INÍCIO DA AULA ANTERIOR (25/08)
    //Button btSalvar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);
        tv = (TextView) findViewById(R.id.MessageIntent);
        edtNome = (TextInputEditText) findViewById(R.id.edtBusca);
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
            //Recuperando o usuário
            user = (User) params.getSerializable("usuario");//<------------linha problema
            if (user != null) {
                tv.setText(user.getNome());
            }

        }
        /*INÍCIO DA AULA ANTERIOR (25/08)
        btSalvar = (Button) findViewById(R.id.btSalvar);
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }

    public void cliquedoSalvar(View v) {
        Contato c, k, z;
        c = new Contato();
        c.setNumero("tel:+123456789");
        c.setNome("Jaspion");
        k = new Contato();
        k.setNumero("tel:+123456780");
        k.setNome("Jiraiya");
        z = new Contato();
        z.setNumero("tel:+123456781");
        z.setNome("Jiban");

        SharedPreferences salvaContatos = getSharedPreferences("contatos2", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = salvaContatos.edit();
        editor.putInt("numContatos", 3);

        try {//OutputStream = escrever
            ByteArrayOutputStream dt = new ByteArrayOutputStream();
            //permite salvar dentro de um ByteArrayOutputStream um conjunto de um objeto que seja serializável
            ObjectOutputStream oos = new ObjectOutputStream(dt);
            oos.writeObject(c);//coloca dentro do ByteArrayOutputStream uma instância de objeto, vai transformar isso num array de bytes
            //transformando esse vetor de bytes em uma String usando a codificação ISO
            String contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato1", contatoSerializado);
            //-------------------------------------------------------
            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(k);
            contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato2", contatoSerializado);

            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(z);
            contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato3", contatoSerializado);

        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
        finish();
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

    public void onClickBuscar(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            Log.v("PDM", "Pedir permissão");
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
            //colocar caixa de diálogo educacional aqui
            return;//pedir a permissão e sair; da próxima ver que clicar, já tem a permissão
        }
        Log.v("PDM", "Tenho permissão");
        //para acessar o provedor de conteúdo
        ContentResolver cr = getContentResolver();
        String consulta = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";//dizer qual campo quer consultar
        String[] argumentosConsulta = {"%" + edtNome.getText() + "%"};
        Cursor cursor = cr.query//permite fazer consultas na tabela de contatos, retorna um cursor
                (ContactsContract.Contacts.CONTENT_URI, null,
                        consulta, argumentosConsulta, null);

        Contato c = new Contato();
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
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
                Log.v("PDM", " Telefone " + j + ": " + number);
            }
            phones.close();//sempre fechar cursor depois de usar
        }
        cursor.close();//sempre fechar cursor depois de usar
        SharedPreferences salvaContatos = getSharedPreferences("contatos2", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = salvaContatos.edit();
        editor.putInt("numContatos", 4); // alterou o nº de contatos, tem que lidar com isso
        // ou colocar sempre 5 e ver qual o contato que está sendo alterado

        try {
            ByteArrayOutputStream dt = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(dt);
            oos.writeObject(c);
            String contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato4", contatoSerializado);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mostrarListaDeContatos();
    }

    protected int mostrarListaDeContatos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            Log.v("PDM", "Pedir permissão");
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
            return 0;
        }
        Log.v("PDM", "Tenho permissão");
        //Projection, os campos que você quer
        String[] projection = new String[]
                {
                        ContactsContract.Profile._ID,
                        ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
                        ContactsContract.Profile.LOOKUP_KEY,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI
                };
        String selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
        String[] selectionArguments = {"K%"};//{"%" + edtNome.getText() + "%"};
        ContentResolver cr = getContentResolver();
        //Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArguments, null);
        int indexDisplayName = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            //String given = cursor.getString(indexGivenName);
            //String family = cursor.getString(indexFamilyName);
            String display = cursor.getString(indexDisplayName);
            //Ler nome
            String contatoNome = cursor.getString(indexDisplayName);
            Log.v("PDM", "Nome do Contato: " + contatoNome);//+ ", " + given + " " + family);
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //
            //Set all phone numbers
            //
            Cursor phones = cr.query
                    (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone._ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here
                        Log.v("PDM", "Tel do (" + contactId + "): " + contatoNome + ": " + number);
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        // do something with the Mobile number here
                        Log.v("PDM", "Tel do (" + contactId + "): " + contatoNome + ": " + number);
                        break;
                }
            }
            phones.close();//sempre fechar cursor depois de usar
        }
        cursor.close();//sempre fechar cursor depois de usar
        return 1;
    }
}