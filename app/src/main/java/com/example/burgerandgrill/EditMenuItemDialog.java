package com.example.burgerandgrill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

public class EditMenuItemDialog extends AppCompatDialogFragment {
    private EditText newPrice;
    private IExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.DialogeTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_edit_menu_item_dialog, null);


        builder.setView(view)
                .setTitle("EDIT")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(checkInputs()){
                            String updatedPrice = newPrice.getText().toString();
                            listener.applyQuantity(updatedPrice);
                        }
                        else{
                            makeToast();
                        }

                    }
                });

        newPrice = view.findViewById(R.id.new_price_edit_menu_item_dialog);
        Bundle args = getArguments();
        newPrice.setText(String.valueOf(args.getInt("currentPrice")));

        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (EditMenuItemDialog.IExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface IExampleDialogListener {
        void applyQuantity(String newPrice);
    }


    public boolean checkInputs(){
        if(!TextUtils.isEmpty(newPrice.getText())){
            return true;
        }
        else{
            return false;
        }
    }
    public void makeToast(){
        Toast.makeText(getContext(),"Enter new price or press cancel!!",Toast.LENGTH_SHORT).show();
    }
}
