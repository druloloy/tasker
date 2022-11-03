package com.estiay.tasker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView toolbarTitle;
    //Bottom Navigation
    BottomNavigationView bottomNavigationView;
    Deque<Integer> integerDeque = new ArrayDeque<>(3);
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        toolbarTitle = findViewById(R.id.toolbr_title);
        toolbarTitle.setText("Tasker");

        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //home fragment in deque list
        integerDeque.push(R.id.bn_dashboard);

        //load home fragment
        loadFragment(new DashboardFragment());

        //home default
        bottomNavigationView.setSelectedItemId(R.id.bn_dashboard);

        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    //selected item id
                    int id = item.getItemId();
                    //condition
                    if (integerDeque.contains(id)){
                        //deque list contains selected id
                        //check condition
                        if (id == R.id.bn_dashboard){
                            //selected id is equal to home
                            //check condition
                            if (integerDeque.size() != 1){
                                //not equal to 1
                                //check condition
                                if (flag){
                                    //flag is true
                                    integerDeque.addFirst(R.id.bn_dashboard);
                                    //set flag to false
                                    flag = false;
                                }
                            }
                        }
                        //remove selected id from list
                        integerDeque.remove(id);
                    }
                    //push selected id in list
                    integerDeque.push(id);
                    //load fragment
                    loadFragment(getFragment(item.getItemId()));
                    //return true
                    return true;
                }
        );
    }

    private Fragment getFragment(int itemId) {
        switch (itemId){
            case R.id.bn_dashboard:
                //check dashboard
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                //return dashboard
                return new DashboardFragment();
            case R.id.bn_notification:
                //checked notification
                bottomNavigationView.getMenu().getItem(1).setChecked(true);

                return new NotificationFragment();
            //checked graph
            case R.id.bn_graph:
                //checked graph
                bottomNavigationView.getMenu().getItem(2).setChecked(true);

                return new GraphFragment();
        }
        //default home fragment
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        return new GraphFragment();
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment,fragment,fragment.getClass().getSimpleName())
                .commit();
    }
    @Override
    public void onBackPressed() {
        //pop to previous fragment
        integerDeque.pop();
        //condition
        if (!integerDeque.isEmpty()){
            //when deque fragment is not empty
            loadFragment(getFragment(integerDeque.peek()));
        }else{
            //finish activity
            finish();
        }
    }

}