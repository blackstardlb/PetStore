package nl.blackstardlb.petstore.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.viewmodels.MainActivityViewModel;
import nl.blackstardlb.petstore.views.base.BaseActivity;
import nl.blackstardlb.petstore.views.base.BaseFragment;
import nl.blackstardlb.petstore.views.petlist.FilteredPetListFragment;
import nl.blackstardlb.petstore.views.petlist.PetListFragment;

public class MainActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    @BindView(R.id.navigation)
    protected NavigationView navigationView;

    private MainActivityViewModel viewModel;
    private String filter;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        viewModel = getViewModel(MainActivityViewModel.class);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        }

        navigationView.inflateHeaderView(R.layout.navigation_header);
        setupDrawerContent(navigationView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = menuItem -> {
            selectDrawerItem(menuItem, navigationView);
            return true;
        };
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        if (viewModel.getPreviousActionId() == null) {
            viewModel.setPreviousActionId(R.id.action_dogs);
        }

        onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(viewModel.getPreviousActionId()));
    }

    public void selectDrawerItem(MenuItem menuItem, NavigationView navigationView) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        BaseFragment fragment = null;
        Class fragmentClass;
        String filter = null;
        switch (menuItem.getItemId()) {
            case R.id.action_dogs:
                fragmentClass = FilteredPetListFragment.class;
                filter = "dog";
                break;
            case R.id.action_cats:
                fragmentClass = FilteredPetListFragment.class;
                filter = "cat";
                break;
            case R.id.action_other:
                fragmentClass = PetListFragment.class;
                break;
            case R.id.action_add_pet:
                fragmentClass = AddPetFragment.class;
                break;
            case R.id.action_add_pet_store:
                fragmentClass = AddPetStoreFragment.class;
                break;
            default:
                fragmentClass = LoginFragment.class;
        }

        try {
            fragment = (BaseFragment) fragmentClass.newInstance();

            if (fragment instanceof FilteredPetListFragment) {
                FilteredPetListFragment listFragment = (FilteredPetListFragment) fragment;
                listFragment.setFilter(filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = menuItem.getTitle().toString();
        viewModel.setCurrentTag(tag);
        fragmentManager.beginTransaction().replace(R.id.fragment_mount, fragment, tag).commit();

        if (viewModel.getPreviousActionId() != null) {
            MenuItem item = navigationView.getMenu().findItem(viewModel.getPreviousActionId());
            item.setChecked(false);
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        viewModel.setPreviousActionId(menuItem.getItemId());

        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

}
