package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/**
 * this interface for owners have 2 special function
 * delete user and code from database
 */
public class ManageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        Button deletePlayer = findViewById(R.id.delete_player_button);
        deletePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent player = new Intent(ManageActivity.this,DeleteUserActivity.class);
                startActivity(player);
            }
        });

        Button btn =  findViewById(R.id.back_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(ManageActivity.this,MainActivity.class);
               // startActivity(intent);
                finish();
            }
        });

        Button deleteCodes = findViewById(R.id.delete_codes_button);
        deleteCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent code = new Intent(ManageActivity.this,DeleteCodesActivity.class);
                startActivity(code);
            }
        });
    }
}