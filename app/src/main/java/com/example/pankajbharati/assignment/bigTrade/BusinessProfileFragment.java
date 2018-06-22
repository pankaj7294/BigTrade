package com.example.pankajbharati.assignment.bigTrade;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pankajbharati.assignment.R;

public class BusinessProfileFragment extends Fragment {


    public BusinessProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_bussiness_profile, container, false);
    }

}
