package com.example.burgerandgrill;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

public class AddCouponCodeDialog extends AppCompatDialogFragment {
    private EditText newCouponCode;
    private NumberPicker discountPercentage;
    private IExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.DialogeTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_coupon_code_dialog, null);



        builder.setView(view)
                .setTitle("ADD COUPON CODE")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(checkInputs()){
                            String couponCode = newCouponCode.getText().toString();
                            listener.applyQuantity(couponCode,discountPercentage.getValue());
                        }
                        else{
                            makeToast();
                        }

                    }
                } );

        newCouponCode = view.findViewById(R.id.new_coupon_code);
        discountPercentage = view.findViewById(R.id.discount_percentage);
        discountPercentage.setMinValue(0);
        discountPercentage.setMaxValue(100);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (IExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface IExampleDialogListener {
        void applyQuantity(String newPrice, int discount);
    }


    public boolean checkInputs(){
        if(!TextUtils.isEmpty(newCouponCode.getText())){
            return true;
        }
        else{
            return false;
        }
    }
    public void makeToast(){
        Toast.makeText(getContext(),"No new coupon is added!!",Toast.LENGTH_SHORT).show();
    }
}
