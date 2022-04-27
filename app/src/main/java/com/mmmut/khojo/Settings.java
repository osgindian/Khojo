package com.mmmut.khojo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    LinearLayout hpRow,hpEditRow;
    TextView oldHp;
    ImageButton hpEdit,hpSave;
    Button viewHistory,viewDeveloper;
    EditText newHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = this.getSharedPreferences("com.mmmut.khojo", Context.MODE_PRIVATE);

        hpRow = (LinearLayout) findViewById(R.id.hpRow);
        hpEditRow = (LinearLayout) findViewById(R.id.hpEditRow);
        oldHp = (TextView) findViewById(R.id.oldHp);
        hpEdit = (ImageButton) findViewById(R.id.hpEdit);
        newHp = (EditText) findViewById(R.id.newHp);
        hpSave = (ImageButton) findViewById(R.id.hpSave);
        hpEditRow.setVisibility(View.GONE);

        oldHp.setText(sharedPreferences.getString("homePage", "https://google.com"));
        hpEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newHp.setText(oldHp.getText());
                hpRow.setVisibility(View.GONE);
                hpEditRow.setVisibility(View.VISIBLE);
            }
        });
        hpSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = String.valueOf(newHp.getText());
                sharedPreferences.edit().putString("homePage",url).apply();
                oldHp.setText(url);
                hpRow.setVisibility(View.VISIBLE);
                hpEditRow.setVisibility(View.GONE);
            }
        });

        viewHistory = (Button) findViewById(R.id.viewHistory);
        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),History.class);
                startActivity(intent);
            }
        });

        viewDeveloper = (Button) findViewById(R.id.viewDeveloper);
        viewDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Developer.class);
                startActivity(intent);
            }
        });



    }

}