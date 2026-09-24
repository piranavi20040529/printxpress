package com.example.printxpress;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.Firebase.FirebaseHelper;
import com.example.printxpress.adapter.OrderAdapter;
import com.example.printxpress.model.Order;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private EditText etSearch;
    private ImageView filterBtn;
    private ChipGroup filterChipGroup;
    private String currentStatusFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        orderList = new ArrayList<>();

        initViews();
        setupToolbar();
        setupRecyclerView();
        fetchOrdersFromRealtimeDatabase();
        setupFilterLogic();
        setupSearch();
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rvOrders);
        etSearch = findViewById(R.id.etSearchOrders);
        filterBtn = findViewById(R.id.filterBtn);
        filterChipGroup = findViewById(R.id.filterChipGroup);
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Orders");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    private void setupRecyclerView() {
        adapter = new OrderAdapter(orderList, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }


    private void fetchOrdersFromRealtimeDatabase() {
        if (!FirebaseHelper.isLoggedIn()) return;

        String userId = FirebaseHelper.getCurrentUserId();


        FirebaseHelper.getOrdersRef().orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Order order = dataSnapshot.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }

                        Collections.sort(orderList, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                        
                        applyCurrentFilters();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyOrdersActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setupFilterLogic() {
        if (filterChipGroup != null) {
            filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    currentStatusFilter = "All";
                } else {
                    int checkedId = checkedIds.get(0);
                    if (checkedId == R.id.chipAll) currentStatusFilter = "All";
                    else if (checkedId == R.id.chipActive) currentStatusFilter = "Active";
                    else if (checkedId == R.id.chipCompleted) currentStatusFilter = "Completed";
                    else if (checkedId == R.id.chipCancelled) currentStatusFilter = "Cancelled";
                }
                applyCurrentFilters();
            });
        }


        if (filterBtn != null) {
            filterBtn.setOnClickListener(v -> showSortMenu(v));
        }
    }


    private void applyCurrentFilters() {
        if (adapter != null) {
            adapter.filter(etSearch.getText().toString(), currentStatusFilter);
        }
    }


    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyCurrentFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }


    private void showSortMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Newest First");
        popup.getMenu().add("Oldest First");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals("Newest First")) {
                Collections.sort(orderList, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
            } else if (title.equals("Oldest First")) {
                Collections.sort(orderList, (o1, o2) -> Long.compare(o1.getTimestamp(), o2.getTimestamp()));
            }
            adapter.notifyDataSetChanged();
            applyCurrentFilters();
            return true;
        });
        popup.show();
    }
}
