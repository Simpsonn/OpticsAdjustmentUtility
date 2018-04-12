package com.analyticalneko.marksmanassistant;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Nathaniel Simpson on 2/3/2018.
 */

public class ManualCalcActivity extends Fragment {

    private LinearLayout moaIncrementsLayout;
    private TextView elevationOutput, windageOutput, inCmTextView1, inCmTextView2;

    private boolean isMOASelected, isHigh, isLeft, isYards, isInches;
    private float moaIncrement, elevationInput, windageInput, distanceInput;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manual_calc_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Instantiate Spinners, EditTexts, LinearLayout, and TextViews
        Spinner moaMilsSpinner = view.findViewById(R.id.manual_calc_moa_or_mils_spinner);
        Spinner moaIncrementsSpinner = view.findViewById(R.id.manual_calc_moa_increments_spinner);
        Spinner inCmSpinner = view.findViewById(R.id.manual_calc_inches_cm_spinner);
        Spinner elevationSpinner = view.findViewById(R.id.manual_calc_elevation_spinner);
        Spinner windageSpinner = view.findViewById(R.id.manual_calc_windage_spinner);

        Spinner distanceSpinner = view.findViewById(R.id.manual_calc_distance_spinner);

        EditText elevationEditText = view.findViewById(R.id.manual_calc_elevation_edittext);
        EditText windageEditText = view.findViewById(R.id.manual_calc_windage_edittext);
        EditText distanceEditText = view.findViewById(R.id.manual_calc_distance_edittext);

        moaIncrementsLayout = view.findViewById(R.id.manual_calc_moa_increments_layout);

        elevationOutput = view.findViewById(R.id.manual_calc_elevation_output_textview);
        windageOutput = view.findViewById(R.id.manual_calc_windage_output_textview);
        inCmTextView1 = view.findViewById(R.id.manual_calc_inches_cm_textview1);
        inCmTextView2 = view.findViewById(R.id.manual_calc_inches_cm_textview2);

        //Attach listeners to Spinners
        moaMilsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    isMOASelected = true;
                    moaIncrementsLayout.setVisibility(View.VISIBLE);
                } else if(position == 1){
                    isMOASelected = false;
                    moaIncrementsLayout.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getActivity(),"Something went wrong.",Toast.LENGTH_SHORT).show();
                }
                showClicks();
                Log.d("MOA selected",String.valueOf(isMOASelected)); //DEBUG
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        moaIncrementsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().contains("/")){
                    moaIncrement = Float.parseFloat(parent.getItemAtPosition(position).toString());
                } else {
                    String[] ratio = parent.getItemAtPosition(position).toString().split("/");
                    moaIncrement = Float.parseFloat(ratio[0]) / Float.parseFloat(ratio[1]);
                }
                showClicks();
                Log.d("MOA INCREMENT",String.valueOf(moaIncrement)); //DEBUG
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        elevationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    isHigh = true;
                } else if(position == 1){
                    isHigh = false;
                } else {
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                showClicks();
                Log.d("isHigh", String.valueOf(isHigh)); //DEBUG
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        windageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    isLeft = true;
                } else if (position == 1){
                    isLeft = false;
                } else {
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                showClicks();
                Log.d("isLeft",String.valueOf(isLeft)); //DEBUG
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inCmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position == 0){
                    isInches = true;
                    inCmTextView1.setText(R.string.manual_calc_inches);
                    inCmTextView2.setText(R.string.manual_calc_inches);
                } else if (position == 1){
                    isInches = false;
                    inCmTextView1.setText(R.string.manual_calc_cm);
                    inCmTextView2.setText(R.string.manual_calc_cm);
                } else {
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                showClicks();
                Log.d("isInches",String.valueOf(isInches)); //DEBUG
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    isYards = true;
                } else if(position == 1){
                    isYards = false;
                } else{
                    Toast.makeText(getActivity(),"Something went wrong", Toast.LENGTH_SHORT).show();
                }
                showClicks();
                Log.d("isYards",String.valueOf(isYards)); //DEBUG
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Attach listeners to EditTexts
        elevationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    elevationInput = Float.parseFloat(s.toString());
                } catch (Exception e) {
                    elevationInput = 0;
                }
                showClicks();
                Log.d("Elevation Input",String.valueOf(elevationInput)); //DEBUG
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        windageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    windageInput = Float.parseFloat(s.toString());
                } catch (Exception e) {
                    windageInput = 0;
                }
                showClicks();
                Log.d("Windage Input",String.valueOf(windageInput)); //DEBUG
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        distanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    distanceInput = Float.parseFloat(s.toString());
                } catch (Exception e){
                    distanceInput = 0;
                }
                showClicks();
                Log.d("Distance Input",String.valueOf(distanceInput)); //DEBUG
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    public void showClicks(){

        try {
            int elevationAnswer = calculateClicks(elevationInput);
            int windageAnswer = calculateClicks(windageInput);

            //Elevation
            String elevationOutputString;
            if (isHigh)
                elevationOutputString = "Adjust elevation " + elevationAnswer + " clicks down";
            else
                elevationOutputString = "Adjust elevation " + elevationAnswer + " clicks up";

            //Windage
            String windageOutputString;
            if (isLeft)
                windageOutputString = "Adjust windage " + windageAnswer + " clicks right";
            else
                windageOutputString = "Adjust windage " + windageAnswer + " clicks left";

            //Output click values to user
            elevationOutput.setText(elevationOutputString);
            windageOutput.setText(windageOutputString);
            Log.v("OUTPUT","Adjustments output to fragment view.");
        } catch (Exception e) {
            Log.e("ERROR", "Something done bork with showClicks()");
        }
    }

    public int calculateClicks(float input){

        ZeroCalculator zeroCalc = null;

        try {
            if (isMOASelected) {
                zeroCalc = new ZeroCalculator(isMOASelected, isYards, isInches, moaIncrement, distanceInput, input);
            } else if (!isMOASelected) {
                zeroCalc = new ZeroCalculator(isMOASelected, isYards, isInches, distanceInput, input);
            }
            Log.v("calculatedClicks", "Adjustment calculated");
            return zeroCalc.calculate();
        } catch (Exception e) {
            return 0;
        }
    }

}
