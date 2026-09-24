package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printxpress.adapter.ProductAdapter;
import com.example.printxpress.model.Product;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton menuBtn, notificationsBtn;
    private ImageView profileImg, filterBtn;
    private TextView userNameText, welcomeText, filterResultText, productsTitle;
    private EditText searchEdit;
    private MaterialCardView catBusinessCards, catFlyers, catBanners, catTShirts, catMugs, catStickers;
    private LinearLayout categoriesSection, offersSection;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private String userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        initViews();
        setupRecyclerView();
        getUserData();
        setupNavigationDrawer();
        setupCategoryListeners();
        setupFilterListeners();
        setupSearchListener();
        

        menuBtn.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        

        notificationsBtn.setOnClickListener(v -> {
            startActivity(new Intent(HomePageActivity.this, NotificationsActivity.class));
        });


        loadProducts();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuBtn = findViewById(R.id.menuBtn);
        notificationsBtn = findViewById(R.id.notificationsBtn);
        profileImg = findViewById(R.id.profileImg);
        userNameText = findViewById(R.id.userNameText);
        welcomeText = findViewById(R.id.welcomeText);
        filterResultText = findViewById(R.id.filterResultText);
        productsTitle = findViewById(R.id.productsTitle);
        searchEdit = findViewById(R.id.searchEdit);
        filterBtn = findViewById(R.id.filterBtn);
        
        categoriesSection = findViewById(R.id.categoriesSection);
        offersSection = findViewById(R.id.offersSection);
        
        catBusinessCards = findViewById(R.id.categoryBusinessCards);
        catFlyers = findViewById(R.id.categoryFlyers);
        catBanners = findViewById(R.id.categoryBanners);
        catTShirts = findViewById(R.id.categoryTShirts);
        catMugs = findViewById(R.id.categoryMugs);
        catStickers = findViewById(R.id.categoryStickers);
        
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
    }


    private void setupRecyclerView() {
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productList = new ArrayList<>();
        

        productAdapter = new ProductAdapter(productList, product -> {
            Intent intent = new Intent(HomePageActivity.this, ProductDetailsActivity.class);
            intent.putExtra("PRODUCT_NAME", product.getName());
            intent.putExtra("PRODUCT_PRICE_STR", product.getPriceString());
            intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
            intent.putExtra("PRODUCT_IMAGE_RES", product.getImageResource());
            startActivity(intent);
        });
        productsRecyclerView.setAdapter(productAdapter);
    }


    private void loadProducts() {
        productList.clear();
        productList.add(new Product("1", "Premium Business Card", "Premium Shine · Front & Back · 500 Units", "Rs. 16,000.00", "Rs. 20,000.00", "Business Cards", R.drawable.img1));

        productList.add(new Product("2", "Premium Flyer", "Premium Luxury · Double Side · 500 Units", "Rs. 35,400.00", "Flyers", R.drawable.img2));

        productList.add(new Product("3", "Premium Roll-up Banner", "Medium (6*3ft) · Vinyl · Matte · 10 Units", "Rs. 8,100.00", "Banners", R.drawable.img3));

        productList.add(new Product("4", "Premium Cotton T-Shirt", "100% Cotton · Men Style · Professional · 1 Unit", "Rs. 1,550.00", "T-Shirts", R.drawable.img4));

        productList.add(new Product("5", "Premium Ceramic Mug", "Classic Ceramic · 350ml · One Side · 2 Units", "Rs. 1,080.00", "Mugs", R.drawable.img5));

        productList.add(new Product("6", "Premium Vinyl Sticker", "Logo Pack · Vinyl · Circle · Glossy · 50 Units", "Rs. 1,000.00", "Stickers", R.drawable.img6));

        productAdapter.updateData(new ArrayList<>(productList));
    }

    private void setupSearchListener() {
        if (searchEdit != null) {
            searchEdit.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().toLowerCase().trim();
                    productAdapter.search(query);

                    if (query.isEmpty()) {
                        productsTitle.setText("POPULAR PRODUCTS");
                        categoriesSection.setVisibility(View.VISIBLE);
                        offersSection.setVisibility(View.VISIBLE);
                        setAllCategoriesVisible(true);
                    } else {
                        productsTitle.setText("SEARCH RESULTS");
                        offersSection.setVisibility(View.GONE);
                        filterCategoryCards(query);
                    }
                }
                @Override public void afterTextChanged(android.text.Editable s) {}
            });
        }
    }


    private void setAllCategoriesVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        catBusinessCards.setVisibility(visibility);
        catFlyers.setVisibility(visibility);
        catBanners.setVisibility(visibility);
        catTShirts.setVisibility(visibility);
        catMugs.setVisibility(visibility);
        catStickers.setVisibility(visibility);
    }


    private boolean filterCategoryCards(String query) {
        boolean m1 = "business cards".contains(query);
        boolean m2 = "flyers".contains(query);
        boolean m3 = "banners".contains(query);
        boolean m4 = "t-shirts".contains(query);
        boolean m5 = "mugs".contains(query);
        boolean m6 = "stickers".contains(query);

        catBusinessCards.setVisibility(m1 ? View.VISIBLE : View.GONE);
        catFlyers.setVisibility(m2 ? View.VISIBLE : View.GONE);
        catBanners.setVisibility(m3 ? View.VISIBLE : View.GONE);
        catTShirts.setVisibility(m4 ? View.VISIBLE : View.GONE);
        catMugs.setVisibility(m5 ? View.VISIBLE : View.GONE);
        catStickers.setVisibility(m6 ? View.VISIBLE : View.GONE);

        return m1 || m2 || m3 || m4 || m5 || m6;
    }


    private void setupCategoryListeners() {
        catBusinessCards.setOnClickListener(v -> startActivity(new Intent(this, BusinesscardActivity.class)));
        catFlyers.setOnClickListener(v -> startActivity(new Intent(this, FlyerActivity.class)));
        catBanners.setOnClickListener(v -> startActivity(new Intent(this, BannerActivity.class)));
        catTShirts.setOnClickListener(v -> startActivity(new Intent(this, TshirtActivity.class)));
        catMugs.setOnClickListener(v -> startActivity(new Intent(this, MugActivity.class)));
        catStickers.setOnClickListener(v -> startActivity(new Intent(this, StickerActivity.class)));
    }


    private void setupFilterListeners() {
        filterBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, filterBtn);
            popupMenu.getMenu().add("All Categories");
            popupMenu.getMenu().add("Business Cards");
            popupMenu.getMenu().add("Flyers");
            popupMenu.getMenu().add("Banners");
            popupMenu.getMenu().add("T-Shirts");
            popupMenu.getMenu().add("Mugs");
            popupMenu.getMenu().add("Stickers");
            popupMenu.setOnMenuItemClickListener(item -> {
                String cat = item.getTitle().toString();
                if (cat.equals("All Categories")) productAdapter.filter("");
                else productAdapter.filter(cat);
                return true;
            });
            popupMenu.show();
        });
    }

    private void getUserData() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userName = prefs.getString("FULL_NAME", "PrintXpress User");
        userEmail = prefs.getString("USER_EMAIL", "user@example.com");
        userNameText.setText(userName);
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_header_name);
        TextView navEmail = headerView.findViewById(R.id.nav_header_email);
        
        navName.setText(userName);
        navEmail.setText(userEmail);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (id == R.id.nav_orders) {
                startActivity(new Intent(this, MyOrdersActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, MyProfileActivity.class));
            } else if (id == R.id.nav_tracking) {
                startActivity(new Intent(this, PublicTrackingActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        

        if (profileImg != null) {
            profileImg.setOnClickListener(v -> startActivity(new Intent(this, MyProfileActivity.class)));
        }
    }
}
