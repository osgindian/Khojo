package com.mmmut.khojo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class History extends AppCompatActivity {

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");

    SQLiteDatabase database;
    LinearLayout list;
    ImageButton deleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = this.openOrCreateDatabase("Khojo",MODE_PRIVATE,null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        deleteAll = (ImageButton) findViewById(R.id.deleteAll);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.execSQL("DELETE FROM `history`");
                Intent intent = new Intent(getApplicationContext(),Settings.class);
                startActivity(intent);
            }
        });

        list = (LinearLayout) findViewById(R.id.list);

        Cursor c1 = database.rawQuery("SELECT DISTINCT `date` FROM `history` ORDER BY `date` DESC",null);
        int c1_date = c1.getColumnIndex("date");
        c1.moveToFirst();
        LinearLayout.LayoutParams dateParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dateParam.setMargins(0,40,0,0);

        GradientDrawable dateBorder = new GradientDrawable();
        dateBorder.setCornerRadius(5);
        dateBorder.setStroke(1, 0xFF000000);
        String dt = "";
        do{
            try{
                dt = c1.getString(c1_date);
            }
            catch (Exception e){
                break;
            }
            TextView textView = new TextView(this);
            String dtf;
            try{
                dtf = dateFormat.format(new SimpleDateFormat("yyyyMMdd").parse(dt));
            }
            catch (Exception e){
                dtf = dt;
            }
            textView.setText(dtf);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(15,15,15,15);
            textView.setBackground(dateBorder);
            textView.setLayoutParams(dateParam);
            list.addView(textView);
            Cursor c2 = database.rawQuery("SELECT * FROM `history` WHERE `date`='"+dt+"' ORDER BY `time` DESC",null);
            int c2_title = c2.getColumnIndex("title");
            int c2_url = c2.getColumnIndex("url");
            c2.moveToFirst();
            do{
                String title = c2.getString(c2_title);
                String url = c2.getString(c2_url);
                Button link = new Button(this);
                link.setText(title + "\n" + url);
                link.setGravity(Gravity.LEFT);
                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("URL",url);
                        startActivity(intent);
                    }
                });
                list.addView(link);
            }while(c2.moveToNext());

        }while(c1.moveToNext());

    }
}