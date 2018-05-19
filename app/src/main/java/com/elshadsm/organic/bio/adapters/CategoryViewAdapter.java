package com.elshadsm.organic.bio.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.activities.ProductListActivity;
import com.elshadsm.organic.bio.models.CategoryView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elshadsm.organic.bio.models.Constants.*;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.RecyclerViewHolder> {

    private ArrayList<CategoryView> categoryViewList = new ArrayList<>();

    public CategoryViewAdapter(Context context) {
        categoryViewList.add(new CategoryView(R.drawable.ic_food,
                context.getResources().getString(R.string.category_foods), CATEGORY_FOODS));
        categoryViewList.add(new CategoryView(R.drawable.ic_spice,
                context.getResources().getString(R.string.category_spices), CATEGORY_SPICES));
        categoryViewList.add(new CategoryView(R.drawable.ic_medication,
                context.getResources().getString(R.string.category_medication), CATEGORY_MEDICATION));
        categoryViewList.add(new CategoryView(R.drawable.ic_beauty,
                context.getResources().getString(R.string.category_beauty), CATEGORY_BEAUTY));
        categoryViewList.add(new CategoryView(R.drawable.ic_home,
                context.getResources().getString(R.string.category_home), CATEGORY_HOME));
        categoryViewList.add(new CategoryView(R.drawable.ic_others,
                context.getResources().getString(R.string.category_others), CATEGORY_OTHERS));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context viewContext = viewGroup.getContext();
        int layoutIdForCategoryView = R.layout.category_view_item;
        LayoutInflater inflater = LayoutInflater.from(viewContext);
        View view = inflater.inflate(layoutIdForCategoryView, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
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
        public void onClick(View view) {
            Context context = view.getContext();
            int clickedPosition = getAdapterPosition();
            Intent intent = new Intent( context, ProductListActivity.class);
            intent.putExtra(CATEGORY_EXTRA_NAME, categoryViewList.get(clickedPosition));
            context.startActivity(intent);
        }
    }
}
