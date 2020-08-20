package com.example.burgerandgrill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CurrentOrderListAdapter extends RecyclerView.Adapter<CurrentOrderListAdapter.ExampleViewHolder> {
    private ArrayList<OrderModel> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView mCountView;
        public TextView mNameView;
        public TextView mPriceView;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mCountView = itemView.findViewById(R.id.count);
            mNameView = itemView.findViewById(R.id.name_current_order_list_item);
            mPriceView = itemView.findViewById(R.id.price_current_order_list_item);

        }
    }


    public CurrentOrderListAdapter(ArrayList<OrderModel> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public CurrentOrderListAdapter.ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_order_list_item, parent, false);
        CurrentOrderListAdapter.ExampleViewHolder evh = new CurrentOrderListAdapter.ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(CurrentOrderListAdapter.ExampleViewHolder holder, int position) {
        OrderModel currentItem = mExampleList.get(position);

        if(currentItem.getProductType().equals("")){
            /**
             * means the call is coming from CouponCode
             */
            holder.mNameView.setText("CODE: " + currentItem.getProductName());
            holder.mPriceView.setText("DISCOUNT: " + currentItem.getPrice() + " %");
        }else{
            if(currentItem.getProductType().equals("on_going_order")){
                /**
                 * means the call is from user dashboard to display
                 * on-going order list
                 */
                holder.mNameView.setText(currentItem.getProductName());
                holder.mPriceView.setText("Rs: " + currentItem.getPrice());
            }
            else if (currentItem.getProductType().equals("on_going_delivery")){
                holder.mNameView.setText(currentItem.getProductName());
                holder.mPriceView.setText(currentItem.getPrice());
            }else{
                if(!currentItem.getProductType().equals("None")){
                    /**
                     * means the call is from TakeOrder (order list)
                     */
                    holder.mNameView.setText(currentItem.getProductName() + " - " + currentItem.getProductType());
                }
                else {
                    holder.mNameView.setText(currentItem.getProductName());
                }
                if(currentItem.getPrice().equals("admin") || currentItem.getPrice().equals("Employee")){
                    holder.mPriceView.setText(currentItem.getPrice());
                }else{
                    holder.mPriceView.setText("Rs. " + currentItem.getPrice());
                }
            }

        }
        holder.mCountView.setText(currentItem.getCount());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
