package itp341.liu.haomei.finalprojecthaomeiliu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_search:
                        Toast.makeText(HomeActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_chat:
                        Toast.makeText(HomeActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_group:
                        Toast.makeText(HomeActivity.this, "Group", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }

        });
    }
}
