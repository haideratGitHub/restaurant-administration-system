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

public class ApplyDiscountDialog extends AppCompatDialogFragment {
    private EditText discountCode;
    private ApplyDiscountDialog.IExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_apply_discount_dialog, null);

        builder.setView(view)
                .setTitle("DISCOUNT")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(checkInputs()){
                            String code = discountCode.getText().toString();
                            listener.applyQuantity(code);
                        }
                        else{
                            makeToast();
                        }

                    }
                });

        discountCode = view.findViewById(R.id.coupon_code_for_discount);

        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ApplyDiscountDialog.IExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface IExampleDialogListener {
        void applyQuantity(String newPrice);
    }


    public boolean checkInputs(){
        if(!TextUtils.isEmpty(discountCode.getText())){
            return true;
        }
        else{
            return false;
        }
    }
    public void makeToast(){
        Toast.makeText(getContext(),"Enter discount code or press cancel!!",Toast.LENGTH_SHORT).show();
    }
}
