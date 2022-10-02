package com.edu.compumovil.taller2.activities;

import android.content.Intent;
import android.os.Bundle;

import com.edu.compumovil.taller2.databinding.ActivityMainBinding;


public class MainActivity extends BasicActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.imageButtonCamera.setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));

    }
}