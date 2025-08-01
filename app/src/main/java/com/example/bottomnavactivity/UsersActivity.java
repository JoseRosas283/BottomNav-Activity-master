package com.example.bottomnavactivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users);

        // Intents
        FloatingActionButton AddUser = findViewById(R.id.btnAddUser);
        MaterialButton UserBackProfile = findViewById(R.id.btnUserBackProfile);

        AddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        UserBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        EditText searchInput = findViewById(R.id.searchInput);

// Ocultar la X al iniciar la app
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

// Listener para detectar cambios en el texto
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Mostrar la X
                    searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    // Quitar la X
                    searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

// Detectar clic en el drawable (la X)
        searchInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // índice del drawableEnd
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable[] drawables = searchInput.getCompoundDrawables();
                if (drawables[DRAWABLE_RIGHT] != null) {
                    int drawableStart = searchInput.getRight() - searchInput.getPaddingRight()
                            - drawables[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= drawableStart) {
                        searchInput.setText(""); // Borrar texto
                        return true;
                    }
                }
            }
            return false;
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_reloj) {
                startActivity(new Intent(getApplicationContext(), RelojActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;

            } else if (itemId == R.id.bottom_profile) {
                return true;
            }

            return false;
        });
    }
}