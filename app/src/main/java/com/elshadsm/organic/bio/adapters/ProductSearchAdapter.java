package com.elshadsm.organic.bio.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.models.ProductSearchResult;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.MyViewHolder> implements Filterable {

    private List<ProductSearchResult> productSearchResultList = new ArrayList<>();
    private ProductSearchAdapterListener listener;

    public ProductSearchAdapter(ProductSearchAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_search_result_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ProductSearchResult productSearchResult = productSearchResultList.get(position);
        holder.name.setText(productSearchResult.getName());
    }

    @Override
    public int getItemCount() {
        return productSearchResultList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                productSearchResultList.clear();
                if (!charString.isEmpty()) {
                    productSearchResultList.add(new ProductSearchResult(123, "Test 1"));
                    productSearchResultList.add(new ProductSearchResult(123, "Test 2"));
                    productSearchResultList.add(new ProductSearchResult(123, "Test 3"));
                    productSearchResultList.add(new ProductSearchResult(123, "Test 4"));
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = productSearchResultList;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productSearchResultList = (ArrayList<ProductSearchResult>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ProductSearchAdapterListener {
        void onProductSelected(ProductSearchResult contact);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.product_search_result_item_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onProductSelected(productSearchResultList.get(getAdapterPosition()));
                }
            });
        }
    }
}
