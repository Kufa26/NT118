package com.example.cashmate.group.chooseGroup;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashmate.R;
import com.example.cashmate.group.chooseIcon.ChooseIcon;


public class AddGroupBudget extends AppCompatActivity {

    private ImageView imgGroupIcon;
    private ImageView btnBack;
    private int selectedIcon = R.drawable.ic_food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group);

        // ===== bind view =====
        imgGroupIcon = findViewById(R.id.imgGroupIcon);
        btnBack = findViewById(R.id.btnBack);

        // ===== click mũi tên → quay về =====
        btnBack.setOnClickListener(v -> finish());

        // ===== click icon → mở choose_icon.xml =====
        imgGroupIcon.setOnClickListener(v -> {
            ChooseIcon fragment = new ChooseIcon(icon -> {
                selectedIcon = icon;
                imgGroupIcon.setImageResource(icon);
            });
            fragment.show(getSupportFragmentManager(), "ChooseIcon");
        });
    }
}
