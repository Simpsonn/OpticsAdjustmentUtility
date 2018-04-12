package com.analyticalneko.marksmanassistant;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by Nathaniel Simpson on 2/2/2018.
 */

public class ScopeActivity extends Fragment {

    LinearLayout scopeIncrementsInfoLayout;

    Spinner moaMilsSpinner, moaIncrementsSpinner;
    Button nextBtn;

    //Bundle arguments to TargetActivity
    final String SEND_BOOLEAN_TO_TARGET = "moa boolean target";
    final String SEND_FLOAT_TO_TARGET = "moa float target";
    boolean isMOA;
    float moaIncrements;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scope_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing Spinners, Layout, and Button

        moaMilsSpinner = view.findViewById(R.id.scope_moa_or_mils_spinner);
        moaIncrementsSpinner = view.findViewById(R.id.moa_increments_spinner);

        scopeIncrementsInfoLayout = view.findViewById(R.id.scope_increments_info_layout);

        nextBtn = view.findViewById(R.id.to_targets_btn);

        moaMilsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    scopeIncrementsInfoLayout.setVisibility(View.VISIBLE);

                    moaIncrementsSpinner.setEnabled(true);
                    moaIncrementsSpinner.setClickable(true);

                    isMOA = true;
                } else if (position == 1){
                    scopeIncrementsInfoLayout.setVisibility(View.INVISIBLE);

                    moaIncrementsSpinner.setEnabled(false);
                    moaIncrementsSpinner.setClickable(false);

                    isMOA = false;
                }
                Log.i("ISMOA", String.valueOf(isMOA));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        moaIncrementsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().contains("/")){
                    moaIncrements = Float.parseFloat(parent.getItemAtPosition(position).toString());
                } else {
                    String[] ratio = parent.getItemAtPosition(position).toString().split("/");
                    moaIncrements = Float.parseFloat(ratio[0]) / Float.parseFloat(ratio[1]);
                }
                Log.i("MOA INCREMENT", String.valueOf(moaIncrements));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putBoolean(SEND_BOOLEAN_TO_TARGET, isMOA);
                bundle.putFloat(SEND_FLOAT_TO_TARGET, moaIncrements);

                Fragment fragment = new TargetActivity();
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                        R.animator.enter_from_left, R.animator.exit_to_right)
                        .addToBackStack(null)
                        .replace(R.id.content_frame, fragment)
                        .commit();
            }
        });

    }
}
