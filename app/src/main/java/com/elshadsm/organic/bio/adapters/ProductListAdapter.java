package com.elshadsm.organic.bio.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.activities.ProductDetailsActivity;
import com.elshadsm.organic.bio.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elshadsm.organic.bio.models.Constants.PRODUCT_EXTRA_NAME;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.RecyclerViewHolder> {

    private List<Product> productList;

    public void setData(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context viewContext = viewGroup.getContext();
        int layoutId = R.layout.product_list_item;
        LayoutInflater inflater = LayoutInflater.from(viewContext);
        View view = inflater.inflate(layoutId, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.ratingBar.setRating(product.getRating());
        holder.price.setText(String.format("%s $", product.getPrice()));
        Picasso.get()
                .load(product.getImageSrc())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(productList == null){
            return 0;
        }
        return productList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.product_list_item_image)
        ImageView imageView;
        @BindView(R.id.product_list_item_name)
        TextView name;
        @BindView(R.id.product_list_item_rating_bar)
        RatingBar ratingBar;
        @BindView(R.id.product_list_item_price)
        TextView price;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            int clickedPosition = getAdapterPosition();
            Intent intent = new Intent( context, ProductDetailsActivity.class);
            intent.putExtra(PRODUCT_EXTRA_NAME, productList.get(clickedPosition));
            context.startActivity(intent);
        }
    }
}
