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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDialogFragment;

public class AddQuantityInventoryDialog extends AppCompatDialogFragment {

    private EditText addQuantity;
    private IExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogeTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_quantity_inventory_dialog, null);

        builder.setView(view)
                .setTitle("ADD STOCK")
                .setNegativeButton("SUBTRACT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(checkInputs()){
                            String updatedQuantity = addQuantity.getText().toString();
                            listener.applyQuantity(updatedQuantity,false);
                        }
                        else{
                        }
                    }
                })
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(checkInputs()){
                            String updatedQuantity = addQuantity.getText().toString();
                            listener.applyQuantity(updatedQuantity,true);
                        }
                        else{
                        }

                    }
                });

        addQuantity = view.findViewById(R.id.add_quantity_inventory);

        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddQuantityInventoryDialog.IExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface IExampleDialogListener {
        void applyQuantity(String updatedQuantity, boolean isAdd);
    }


    public boolean checkInputs(){
        if(!TextUtils.isEmpty(addQuantity.getText())){
            return true;
        }
        else{
            return false;
        }
    }
}
