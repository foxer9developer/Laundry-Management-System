package com.example.laundry1app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
public class UserDashboardActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private TabLayout homeTabs, ordersTabs, accountTabs;
    private TabLayout.Tab tab;
    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        homeTabs = findViewById(R.id.homeTabs);
        ordersTabs = findViewById(R.id.ordersTabs);
        accountTabs = findViewById(R.id.accountTabs);
        homeTabs.setVisibility(View.VISIBLE);
        ordersTabs.setVisibility(View.GONE);
        accountTabs.setVisibility(View.GONE);
        tab = homeTabs.getTabAt(0);
        if (tab != null) {
            tab.select();
        }
        tab = ordersTabs.getTabAt(0);
        if (tab != null) {
            tab.select();
        }
        tab = accountTabs.getTabAt(0);
        if (tab != null) {
            tab.select();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AllProductsFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        homeTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AllProductsFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        break;
                    case 1 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new drycleanFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        break;

                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        ordersTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MyOrdersFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        break;
                    case 1 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OngoingOrdersFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        break;
                    case 2 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PreviousOrdersFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        accountTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MyAccountFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return onOptionsItemSelected(menuItem);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.mipmap.laundrylogo);
        }
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
            }
            @Override
            public void onDrawerOpened(@NonNull View view) {
            }
            @Override
            public void onDrawerClosed(@NonNull View view) {
            }
            @Override
            public void onDrawerStateChanged(int i) {
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.home :
                homeTabs.setVisibility(View.VISIBLE);
                ordersTabs.setVisibility(View.GONE);
                accountTabs.setVisibility(View.GONE);
                tab = homeTabs.getTabAt(0);
                if (tab != null) {
                    tab.select();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AllProductsFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                return true;
            case R.id.my_orders :
                homeTabs.setVisibility(View.GONE);
                ordersTabs.setVisibility(View.VISIBLE);
                accountTabs.setVisibility(View.GONE);
                tab = ordersTabs.getTabAt(0);
                if (tab != null) {
                    tab.select();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MyOrdersFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                return true;
            case R.id.my_account :
                homeTabs.setVisibility(View.GONE);
                ordersTabs.setVisibility(View.GONE);
                accountTabs.setVisibility(View.VISIBLE);
                tab = accountTabs.getTabAt(0);
                if (tab != null) {
                    tab.select();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MyAccountFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                return true;
            case R.id.sign_out :
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
                            finish();
                        }
                    }
                });
                return super.onOptionsItemSelected(item);
            default :
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please Click BACK Again To Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(getBaseContext(), AuthenticationActivity.class));
            finish();
        }
    }
}
