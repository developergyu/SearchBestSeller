package com.example.searchbestseller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private ArrayList<ItemListData> mItemList;
//    private ImageView imageView;
//    private TextView textView;

    @NonNull
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.ViewHolder holder, int position) {

        ItemListData item = mItemList.get(position);
        //holder.title.setText(item.getTitle());
       // holder.onBind(mItemList.get(position));
        Glide.with(holder.itemView.getContext())
                .load(item.getImg_url())
                .into(holder.image_url);
    }
    public void setItemList(ArrayList<ItemListData> list){
        this.mItemList = list;
       // notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image_url;
        //TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_url = (ImageView) itemView.findViewById(R.id.ImageView_bookimage);
            //title = (TextView) itemView.findViewById(R.id.textView_title);
        }
        void onBind(ItemListData item){
            //image_url.setText(item.getImg_url());
           // title.setText(item.getTitle());
        }
    }
}
