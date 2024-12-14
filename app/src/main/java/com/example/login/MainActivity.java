package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.login.newsActivity.NewsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo DrawerLayout và NavigationView
        drawerLayout = findViewById(R.id.main); // Đảm bảo ID chính xác
        navigationView = findViewById(R.id.nav_view); // Đảm bảo ID chính xác

        // Thiết lập ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav){
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if(item != null && item.getItemId() == android.R.id.home){
                    if (drawerLayout.isDrawerOpen((GravityCompat.END))) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.END);
                    }
                }
                return false;
            }
        };



        // Firebase và các hành động khác
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");


        // Xử lý sự kiện click cho NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                } else if (id == R.id.nav_infor) {
                    Intent accountIntent = new Intent(MainActivity.this, activity_account.class);
                    accountIntent.putExtra("username", username);
                    accountIntent.putExtra("password", password);
                    startActivity(accountIntent);
                } else if (id == R.id.nav_theme) {
                    Intent themeIntent = new Intent(MainActivity.this, activity_account.class);
                    startActivity(themeIntent);
                } else if (id == R.id.nav_cal) {
                    Intent calculatorIntent = new Intent(MainActivity.this, Calculator_activity.class);
                    startActivity(calculatorIntent);
                } else if (id == R.id.nav_news) {
                    Intent newsIntent = new Intent(MainActivity.this, NewsActivity.class);
                    startActivity(newsIntent);
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        findViewById(R.id.Card_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.Card_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent accountIntent = new Intent(MainActivity.this, activity_account.class);
                accountIntent.putExtra("username", username);
                accountIntent.putExtra("password", password);
                startActivity(accountIntent);
            }
        });

        findViewById(R.id.Card_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddTransaction = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(intentAddTransaction);
            }
        });

        findViewById(R.id.Card_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHistory = new Intent(MainActivity.this, TransactionHistoryActivity.class);
                startActivity(intentHistory);
            }
        });

        findViewById(R.id.Card_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAnalysis = new Intent(MainActivity.this, AnalysisActivity.class);
                startActivity(intentAnalysis);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
