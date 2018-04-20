package com.elshadsm.organic.bio.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.models.CategoryView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.RecyclerViewHolder> {

    private ArrayList<CategoryView> categoryViewList = new ArrayList<>();

    public CategoryViewAdapter() {
        categoryViewList.add(new CategoryView(R.drawable.ic_food, "Foods"));
        categoryViewList.add(new CategoryView(R.drawable.ic_spice, "Spices"));
        categoryViewList.add(new CategoryView(R.drawable.ic_medication, "Medication"));
        categoryViewList.add(new CategoryView(R.drawable.ic_beauty, "Beauty"));
        categoryViewList.add(new CategoryView(R.drawable.ic_home, "Home"));
        categoryViewList.add(new CategoryView(R.drawable.ic_others, "Others"));
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context viewContext = viewGroup.getContext();
        int layoutIdForCategoryView = R.layout.category_view_item;
        LayoutInflater inflater = LayoutInflater.from(viewContext);
        View view = inflater.inflate(layoutIdForCategoryView, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.textView.setText(categoryViewList.get(position).getTitle());
        holder.imageView.setImageResource(categoryViewList.get(position).getIconSource());
    }

    @Override
    public int getItemCount() {
        return categoryViewList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.categoryViewTitle)
        TextView textView;
        @BindView(R.id.categoryViewIcon)
        ImageView imageView;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            // ***
        }
    }
}
