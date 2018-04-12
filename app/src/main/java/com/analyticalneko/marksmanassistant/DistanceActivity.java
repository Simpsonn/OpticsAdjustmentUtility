package com.analyticalneko.marksmanassistant;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Nathaniel Simpson on 2/10/2018.
 */

public class DistanceActivity extends Fragment {

    //Bundle from CommonTargetSubActivity
    String targetName, imagePath;
    boolean isMoa;
    float moaIncrements;
    float height;
    float width;
    Uri uri;
    final String RECEIVE_NAME_FROM_COMMON = "target name distance";
    final String RECEIVE_BOOLEAN_FROM_COMMON = "moa boolean distance";
    final String RECEIVE_FLOAT_FROM_COMMON = "moa float distance";
    final String RECEIVE_HEIGHT_FROM_COMMON = "height distance";
    final String RECEIVE_WIDTH_FROM_COMMON = "width distance";
    final String RECEIVE_IMAGE_PATH_FROM_COMMON = "image path distance";
    final String RECEIVE_URI_FROM_COMMON = "uri distance";

    //Bundle to ZeroActivity
    boolean isYards;
    float distance;
    final String SEND_NAME_TO_ZERO = "target name zero";
    final String SEND_ISMOA_TO_ZERO = "isMoa zero";
    final String SEND_INCREMENTS_TO_ZERO = "moa increments zero";
    final String SEND_HEIGHT_TO_ZERO = "height zero";
    final String SEND_WIDTH_TO_ZERO = "width zero";
    final String SEND_IMAGE_PATH_TO_ZERO = "image path zero";
    final String SEND_URI_TO_ZERO = "uri zero";
    final String SEND_ISYARDS_TO_ZERO = "isYards zero";
    final String SEND_DISTANCE_ZERO = "distance zero";

    Spinner yardsOrMetersSpinner;
    EditText distanceEditText;
    TextView yardsOrMetersTextview;
    Button nextBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        targetName = bundle.getString(RECEIVE_NAME_FROM_COMMON);
        isMoa = bundle.getBoolean(RECEIVE_BOOLEAN_FROM_COMMON);
        moaIncrements = bundle.getFloat(RECEIVE_FLOAT_FROM_COMMON);
        height = bundle.getFloat(RECEIVE_HEIGHT_FROM_COMMON);
        width = bundle.getFloat(RECEIVE_WIDTH_FROM_COMMON);
        imagePath = bundle.getString(RECEIVE_IMAGE_PATH_FROM_COMMON);
        uri = Uri.parse(bundle.getString(RECEIVE_URI_FROM_COMMON));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.distance_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*Toast.makeText(getActivity(), "targetName: " + targetName + ", isMOA: " + String.valueOf(isMoa) +
                ", moaIncrements: " + String.valueOf(moaIncrements), Toast.LENGTH_SHORT).show();

        Toast.makeText(getActivity(), "Height: " + String.valueOf(height) + ", Width: " +
                String.valueOf(width), Toast.LENGTH_SHORT).show();*/

        yardsOrMetersSpinner = view.findViewById(R.id.distance_fragment_yards_or_meters);
        distanceEditText = view.findViewById(R.id.distance_fragment_distance_edit_text);
        yardsOrMetersTextview = view.findViewById(R.id.distance_fragment_unit_edit_text);
        nextBtn = view.findViewById(R.id.distance_fragment_next_button);

        yardsOrMetersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    isYards = true;
                    yardsOrMetersTextview.setText(R.string.distance_fragment_yards);
                } else {
                    isYards = false;
                    yardsOrMetersTextview.setText(R.string.distance_fragment_meters);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        distanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    distance = Float.parseFloat(s.toString());
                } catch (Exception e) {
                    distance = 0;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        distanceEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(distance == 0){
                        Toast.makeText(getActivity(), "Please enter the distance to the target.", Toast.LENGTH_LONG).show();
                    } else if(distance < 0) {
                        Toast.makeText(getActivity(), "Distance cannot be negative.", Toast.LENGTH_LONG).show();
                    } else {

                        Intent intent = new Intent(getActivity(), ZeroActivity.class);
                        intent.putExtra(SEND_NAME_TO_ZERO, targetName);
                        intent.putExtra(SEND_ISMOA_TO_ZERO, isMoa);
                        intent.putExtra(SEND_INCREMENTS_TO_ZERO, moaIncrements);
                        intent.putExtra(SEND_HEIGHT_TO_ZERO, height);
                        intent.putExtra(SEND_WIDTH_TO_ZERO, width);
                        intent.putExtra(SEND_IMAGE_PATH_TO_ZERO, imagePath);
                        intent.putExtra(SEND_URI_TO_ZERO, uri.toString());
                        intent.putExtra(SEND_ISYARDS_TO_ZERO, isYards);
                        intent.putExtra(SEND_DISTANCE_ZERO, distance);

                        startActivity(intent);
                    }
                }
                return false;
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(distance == 0){
                    Toast.makeText(getActivity(), "Please enter the distance to the target.", Toast.LENGTH_LONG).show();
                } else if(distance < 0) {
                    Toast.makeText(getActivity(), "Distance cannot be negative.", Toast.LENGTH_LONG).show();
                } else {

                    Intent intent = new Intent(getActivity(), ZeroActivity.class);
                    intent.putExtra(SEND_NAME_TO_ZERO, targetName);
                    intent.putExtra(SEND_ISMOA_TO_ZERO, isMoa);
                    intent.putExtra(SEND_INCREMENTS_TO_ZERO, moaIncrements);
                    intent.putExtra(SEND_HEIGHT_TO_ZERO, height);
                    intent.putExtra(SEND_WIDTH_TO_ZERO, width);
                    intent.putExtra(SEND_IMAGE_PATH_TO_ZERO, imagePath);
                    intent.putExtra(SEND_URI_TO_ZERO, uri.toString());
                    intent.putExtra(SEND_ISYARDS_TO_ZERO, isYards);
                    intent.putExtra(SEND_DISTANCE_ZERO, distance);

                    startActivity(intent);

                    /*Fragment fragment = new ZeroActivity();
                    fragment.setArguments(intent);

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,
                            R.animator.enter_from_left, R.animator.exit_to_right)
                            .addToBackStack(null)
                            .replace(R.id.content_frame, fragment)
                            .commit();*/
                }
            }
        });
    }
}
