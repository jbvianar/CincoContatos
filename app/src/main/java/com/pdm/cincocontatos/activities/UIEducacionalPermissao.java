package com.pdm.cincocontatos.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class UIEducacionalPermissao extends DialogFragment {
    String mensagem;
    String titulo;
    int codigo;//código do Dialog
    public UIEducacionalPermissao(String mensagem, String titulo, int codigo){
        this.mensagem = mensagem;
        this.titulo = titulo;
        this.codigo = codigo;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.mensagem).setTitle(this.titulo);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                //User clicou no botão OK
                listener.onDialogPositiveClick(codigo);
            }
        });
        AlertDialog adialog = builder.create();
        return adialog;
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(int codigo);
    }

    //Usar esta instância da interface para entregar action events
    NoticeDialogListener listener;

    //Faz um Override do método Fragment.onAttach para instanciar o NoticeDialogListener
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        //Verifica se a host Activity implementa a interface de callback
        try {
            //Instancia o NoticeDialogListener para que se envie eventos para o host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            //Se a Activity não implementa a interface, gera uma mensagem de exception (throw)
            throw new ClassCastException(getActivity().toString() + " must implement NoticeDialogListener");
        }
    }
}
