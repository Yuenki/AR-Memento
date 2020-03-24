package com.example.ar_memento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //this is needed to clear all activity and refresh it to the main page
        //Most of the logic is done in MainActivity line 86 -100 that responds to this code

        View view =inflater.inflate(R.layout.fragment_login,container,false);
        return view;
    }
}
