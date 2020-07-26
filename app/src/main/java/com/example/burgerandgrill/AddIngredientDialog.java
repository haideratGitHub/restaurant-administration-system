package com.example.burgerandgrill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;


public class AddIngredientDialog extends AppCompatDialogFragment {
    private EditText editIngredientQuantity;
    private EditText editIngredientName;
    private RadioGroup editIngredientQuantityUnit;
    RadioButton selectedRadioButton;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_ingredient_dialog, null);

        builder.setView(view)
                .setTitle("ADD INGREDIENT")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(checkInputs()){
                            String name = editIngredientName.getText().toString();
                            String quantity = editIngredientQuantity.getText().toString();
                            final RadioButton[] radioButton = new RadioButton[1];
                            editIngredientQuantityUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    radioButton[0] = (RadioButton)group.findViewById(checkedId);
                                }
                            });
                            int selectedId = editIngredientQuantityUnit.getCheckedRadioButtonId();
                            selectedRadioButton = (RadioButton)editIngredientQuantityUnit.findViewById(selectedId);
                            String unit = selectedRadioButton.getText().toString();


                            listener.applyTexts(name, quantity, unit);
                        }
                        else{
//TODO - diable add button and do not close dialog
                        }

                    }
                });

        editIngredientName = view.findViewById(R.id.ingredient_name);
        editIngredientQuantity = view.findViewById(R.id.ingredient_quantity);
        editIngredientQuantityUnit = view.findViewById(R.id.addingredient_groupradio);

        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String s, String name, String quantity);
    }


    public boolean checkInputs(){
        if(!TextUtils.isEmpty(editIngredientName.getText())){
            if (!TextUtils.isEmpty(editIngredientQuantity.getText())){
                if(editIngredientQuantityUnit.getCheckedRadioButtonId() != -1){
                    return true;
                }
                else{
                    editIngredientName.setError("Enter ingredient quantity unit!");
                    return false;
                }
            }
            else {
                editIngredientName.setError("Enter ingredient quantity available in stock!");
                return false;
            }
        }
        else{
            editIngredientName.setError("Enter ingredient name!");
            return false;
        }
    }
}