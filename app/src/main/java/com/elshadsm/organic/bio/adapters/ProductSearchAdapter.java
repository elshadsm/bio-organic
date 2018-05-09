package com.elshadsm.organic.bio.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.MyViewHolder> implements Filterable {

    private static final String LOG_TAG = ProductSearchAdapter.class.getSimpleName();

    private List<Product> productList = new ArrayList<>();
    private List<Product> filteredProductList = new ArrayList<>();
    private ProductSearchAdapterListener listener;

    public ProductSearchAdapter(ProductSearchAdapterListener listener) {
        this.listener = listener;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
        Query query = databaseReference.orderByChild("category");
        fetchData(query);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_search_result_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Product productSearchResult = filteredProductList.get(position);
        holder.name.setText(productSearchResult.getName());
    }

    @Override
    public int getItemCount() {
        return filteredProductList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                List<Product> result = new ArrayList<>();
                if (!charString.isEmpty() && charString.length() > 2) {
                    filterProductList(charString, result);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = result;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredProductList = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void filterProductList(String charString, List<Product> result) {
        for (Product product : productList) {
            if (product.getName().toLowerCase().startsWith(charString.toLowerCase())) {
                result.add(product);
            }
        }
    }

    private void fetchData(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    productList = new ArrayList<>();
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        assert product != null;
                        productList.add(product);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    public interface ProductSearchAdapterListener {
        void onProductSelected(Product contact);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_search_result_item_name)
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onProductSelected(filteredProductList.get(getAdapterPosition()));
                }
            });
        }
    }
}
