package com.example.burgerandgrill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * When MenuListAdapter is called for user dashboard(Take Order), the edit and delete options will be hided
 * When it is called for admin dashboard(Menu), then edit and delete will be available
 */

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ExampleViewHolder> {
    private ArrayList<MenuItem> mExampleList;
    private OnItemClickListener mlistener;


    public interface OnItemClickListener{
        void onItemClick(int position);
        void onEditMenuItemClick(int position);
//        void onDeleteMenuItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public Button editMenuItem;
        public Button deleteMenuItem;
        public Button addToOrderList;
        public Button removeFromOrderList;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            editMenuItem = itemView.findViewById(R.id.edit_menu_item);
//            deleteMenuItem = itemView.findViewById(R.id.delete_menu_item);
            addToOrderList = itemView.findViewById(R.id.add_to_order);
            removeFromOrderList = itemView.findViewById(R.id.remove_from_order);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }

                }
            });
            editMenuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onEditMenuItemClick(position);
                        }
                    }
                }
            });
//            deleteMenuItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null){
//                        int position = getAdapterPosition();
//                        if(position != RecyclerView.NO_POSITION){
//                            listener.onDeleteMenuItemClick(position);
//                        }
//                    }
//                }
//            });
        }
    }


    public MenuListAdapter(ArrayList<MenuItem> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v,mlistener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        MenuItem currentItem = mExampleList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText("Rs. " + currentItem.getText2() + " (tap for details)");
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}