package com.analyticalneko.marksmanassistant;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nathaniel Simpson on 2/2/2018.
 */

public class TargetActivity extends Fragment {

    //Intent key
    final int ADD_TARGET_INTENT = 1;

    //RadioGroup targetBtns;
    LayoutInflater layoutInflater;
    FrameLayout frameLayout;
    FloatingActionButton floatingActionButton;

    //boolean hasView = false;

    //Bundle arguments from ScopeActivity
    boolean isMoa;
    final String RECEIVE_BOOLEAN_FROM_SCOPE = "moa boolean target";
    float moaIncrements;
    final String RECEIVE_FLOAT_FROM_SCOPE = "moa float target";

    //Bundle arguments to CommonTargetSubActivity
    final String SEND_BOOLEAN_TO_COMMON_TARGET = "moa boolean common target";
    final String SEND_FLOAT_TO_COMMON_TARGET = "moa float common target";

    String targetsFileName = "targets.txt";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if(savedInstanceState == null) {*/
            Bundle bundle = getArguments();
                isMoa = bundle.getBoolean(RECEIVE_BOOLEAN_FROM_SCOPE);
                moaIncrements = bundle.getFloat(RECEIVE_FLOAT_FROM_SCOPE);
            //Toast.makeText(getActivity(), "Target: isMOA: " + String.valueOf(isMoa) + ", moaIncrements: "
            //+ String.valueOf(moaIncrements), Toast.LENGTH_SHORT).show();
        /*} else {
            isMoa = savedInstanceState.getBoolean("best boolean");
            moaIncrements = savedInstanceState.getFloat("best float");
        }*/
        File file = new File(getActivity().getFilesDir(), targetsFileName);
        if(!file.exists()) {
            try {
                if(file.createNewFile()) {
                    Toast.makeText(getActivity(), "Targets file created", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.target_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        frameLayout = view.findViewById(R.id.target_info_frame);
        floatingActionButton = view.findViewById(R.id.target_fragment_fab);

        layoutInflater = LayoutInflater.from(getActivity());

        //targetBtns = view.findViewById(R.id.targets_radio_btns);

        Bundle bundle = new Bundle();
        bundle.putBoolean(SEND_BOOLEAN_TO_COMMON_TARGET, isMoa);
        bundle.putFloat(SEND_FLOAT_TO_COMMON_TARGET, moaIncrements);

        Fragment fragment = new CustomTargetSubActivity();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.target_info_frame, fragment).commit();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTargetActivity.class);
                startActivityForResult(intent, ADD_TARGET_INTENT);
            }
        });

        /*targetBtns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.common_targets_radio_btn){
                    if(hasView)
                        frameLayout.removeView(customTargetLayout);

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SEND_BOOLEAN_TO_COMMON_TARGET, isMoa);
                    bundle.putFloat(SEND_FLOAT_TO_COMMON_TARGET, moaIncrements);

                    Fragment fragment = new CommonTargetSubActivity();
                    fragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.target_info_frame, fragment).commit();

                    frameLayout.addView(commonTargetsLayout);

                    hasView = true;
                } else if(checkedId == R.id.custom_target_radio_btn){
                    if(hasView)
                        frameLayout.removeView(commonTargetsLayout);

                    frameLayout.addView(customTargetLayout);

                    hasView = true;
                } else {
                    Toast.makeText(getActivity(),"Something went wrong.",Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_TARGET_INTENT){
            Bundle bundle = new Bundle();
            bundle.putBoolean(SEND_BOOLEAN_TO_COMMON_TARGET, isMoa);
            bundle.putFloat(SEND_FLOAT_TO_COMMON_TARGET, moaIncrements);

            Fragment fragment = new CustomTargetSubActivity();
            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.target_info_frame, fragment).commit();
        }
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("best boolean", isMoa);
        outState.putFloat("best float", moaIncrements);
    }*/

}
