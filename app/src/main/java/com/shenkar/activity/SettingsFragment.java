package com.shenkar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ex2.shenkar.todolist.R;
import com.ex2.shenkar.todolist.Welcome;

/**
 * Created by shnizle on 3/10/2016.
 */
public class SettingsFragment extends Fragment {

    View rootView;
    Spinner syncIntervalInput;
    Welcome mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_settings, container, false);

        syncIntervalInput = (Spinner) rootView.findViewById(R.id.settings_intervalSpinner);

        mainActivity = (Welcome) getActivity();

        /**
         * update current sync value
         */
        int currDelay = mainActivity.getUser().getSyncIntervalDelay();
        syncIntervalInput.setSelection(((ArrayAdapter<String>) syncIntervalInput.getAdapter()).getPosition(
                String.valueOf(currDelay)));

        /**
         * catch update
         */
        syncIntervalInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mainActivity.getUser().setSyncIntervalDelay(Integer.valueOf(syncIntervalInput.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;

    }
}
