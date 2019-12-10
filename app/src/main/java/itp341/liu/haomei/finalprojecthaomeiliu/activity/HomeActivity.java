package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import itp341.liu.haomei.finalprojecthaomeiliu.R;

public class HomeActivity extends AppCompatActivity
        implements HomeFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        ChatFragment.OnFragmentInteractionListener, MeFragment.OnFragmentInteractionListener{

    private List<String> permissions;
    private BottomNavigationView bottomBar;
    final private Fragment fragmentHome = new HomeFragment();
    final private Fragment fragmentSearch = new SearchFragment();
    final private Fragment fragmentChat = new ChatFragment();
    final private Fragment fragmentMe = new MeFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentHome;
    public final static String EXTRA_SEARCH_TIME = HomeActivity.class.getPackage().getName()+"extra_time";
    public final static String EXTRA_SEARCH_LOCATION = HomeActivity.class.getPackage().getName()+"extra_location";
    public final static String EXTRA_SEARCH_KEY = HomeActivity.class.getPackage().getName()+"extra_key";



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestPermission();


        //TODO: Badge for chats

        bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        fm.beginTransaction().hide(active).show(fragmentHome).commit();
                        active = fragmentHome;
                        break;
                    case R.id.bottom_search:
                        Toast.makeText(HomeActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        fm.beginTransaction().hide(active).show(fragmentSearch).commit();
                        active = fragmentSearch;
                        break;
                    case R.id.bottom_chat:
                        Toast.makeText(HomeActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                        fm.beginTransaction().hide(active).show(fragmentChat).commit();
                        active = fragmentChat;
                        break;
                    case R.id.botton_me:
                        Toast.makeText(HomeActivity.this, "Me", Toast.LENGTH_SHORT).show();
                        fm.beginTransaction().hide(active).show(fragmentMe).commit();
                        active = fragmentMe;

                        break;
                }
                return true;
            }

        });

        fm.beginTransaction()
                .add(R.id.home_fragment, fragmentHome, "Home")
                .add(R.id.home_fragment, fragmentSearch, "Search")
                .hide(fragmentSearch)
                .add(R.id.home_fragment, fragmentChat, "Chat")
                .hide(fragmentChat)
                .add(R.id.home_fragment, fragmentMe, "Me")
                .hide(fragmentMe)
                .commit();
    }


    private void requestPermission() {
        permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        if (AndPermission.hasPermission(this, permissions)) {
            return;
        }else {
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.RECORD_AUDIO)
                    .callback(listener)
                    .start();
        }
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // Success
            if(requestCode == 100) {
                Toast.makeText(HomeActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // Failed
            Toast.makeText(HomeActivity.this, "Failed", Toast.LENGTH_SHORT).show();

        }
    };


}
