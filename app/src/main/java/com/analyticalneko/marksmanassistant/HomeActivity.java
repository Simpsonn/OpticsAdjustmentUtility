package com.analyticalneko.marksmanassistant;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Nathaniel Simpson on 2/2/2018.
 */

public class HomeActivity extends Fragment {

    Button beginZeroBtn, manualCalcBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate Buttons
        beginZeroBtn = view.findViewById(R.id.begin_zero_btn);
        manualCalcBtn = view.findViewById(R.id.manual_calc_btn);

        // Set listeners for Buttons
        beginZeroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                        R.animator.enter_from_left, R.animator.exit_to_right)
                        .replace(R.id.content_frame, new ScopeActivity())
                        .addToBackStack(null)
                        .commit();
            }
        });

        manualCalcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                        R.animator.enter_from_left, R.animator.exit_to_right)
                        .replace(R.id.content_frame, new ManualCalcActivity())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
