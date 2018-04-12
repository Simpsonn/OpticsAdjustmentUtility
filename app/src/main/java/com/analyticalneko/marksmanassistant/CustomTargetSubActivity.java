package com.analyticalneko.marksmanassistant;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by Nathaniel Simpson on 2/7/2018.
 */

public class CustomTargetSubActivity extends Fragment{

    //Bundle from TargetActivity
    boolean isMoa;
    final String RECEIVE_BOOLEAN_FROM_TARGET = "moa boolean common target";
    float moaIncrements;
    final String RECEIVE_FLOAT_FROM_TARGET = "moa float common target";

    String targetsFileName = "targets.txt";
    String fileInput;
    Target[] myTargetsArray = new Target[1];

    int count = 0;

    //Bundle arguments to DistanceActivity
    String targetName, imagePath;
    float height;
    float width;
    Uri uri;
    final String SEND_BOOLEAN_TO_DISTANCE = "moa boolean distance";
    final String SEND_FLOAT_TO_DISTANCE = "moa float distance";
    final String SEND_NAME_TO_DISTANCE = "target name distance";
    final String SEND_HEIGHT_TO_DISTANCE = "height distance";
    final String SEND_WIDTH_TO_DISTANCE = "width distance";
    final String SEND_IMAGE_PATH_TO_DISTANCE = "image path distance";
    final String SEND_URI_TO_DISTANCE = "uri distance";

    //Bundle arguments for refreshing target list
    final String SEND_BOOLEAN_TO_TARGET = "moa boolean target";
    final String SEND_FLOAT_TO_TARGET = "moa float target";
    /*final String REFRESH_BOOLEAN = "refresh boolean custom target";
    final String REFRESH_FLOAT = "refresh float custom target";*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        isMoa = bundle.getBoolean(RECEIVE_BOOLEAN_FROM_TARGET);
        moaIncrements = bundle.getFloat(RECEIVE_FLOAT_FROM_TARGET);
        //Toast.makeText(getActivity(), "Common Targets: isMOA: " + String.valueOf(isMoa) + ", moaIncrements: "
        //+ String.valueOf(moaIncrements), Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.target_custom_target_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.target_custom_targets_list_view);

        fileInput = readFromFile(getActivity());
        Log.e("FILE INPUT: ", fileInput);
        if(!Objects.equals(fileInput, "")) {
            PopulateTargetArray();
            final TargetAdapter targetAdapter = new TargetAdapter(getActivity(), R.layout.target_row, myTargetsArray);

            if (listView != null) {

                listView.setAdapter(targetAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        targetName = myTargetsArray[position].targetName;
                        height = myTargetsArray[position].height;
                        width = myTargetsArray[position].width;

                        imagePath = myTargetsArray[position].imagePath;
                        uri = myTargetsArray[position].uri;

                        /*String toast = "Name: " + targetName + ", H: " + height + ", W: " + width;
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();*/

                        Bundle bundle = new Bundle();
                        bundle.putBoolean(SEND_BOOLEAN_TO_DISTANCE, isMoa);
                        bundle.putFloat(SEND_FLOAT_TO_DISTANCE, moaIncrements);
                        bundle.putString(SEND_NAME_TO_DISTANCE, targetName);
                        bundle.putFloat(SEND_HEIGHT_TO_DISTANCE, height);
                        bundle.putFloat(SEND_WIDTH_TO_DISTANCE, width);
                        bundle.putString(SEND_URI_TO_DISTANCE, uri.toString());
                        bundle.putString(SEND_IMAGE_PATH_TO_DISTANCE, imagePath);

                        Fragment fragment = new DistanceActivity();
                        fragment.setArguments(bundle);

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.enter_from_right,
                                R.animator.exit_to_left, R.animator.enter_from_left, R.animator.exit_to_right);
                        fragmentTransaction.addToBackStack(null).replace(R.id.content_frame, fragment).commit();
                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        targetName = myTargetsArray[position].targetName;
                        height = myTargetsArray[position].height;
                        width = myTargetsArray[position].width;

                        imagePath = myTargetsArray[position].imagePath;
                        uri = myTargetsArray[position].uri;

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getActivity());
                        }
                        builder.setTitle("Delete Target")
                                .setMessage("Do you want to delete this target?")
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteEntryInFile(targetName, height, width, imagePath, uri);
                                        refreshTargetList();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                        return true;
                    }
                });

                Log.e("TARGET ARRAY: ", "TARGET ARRAY: " + Arrays.deepToString(myTargetsArray));
                //Toast.makeText(getActivity(), "isMOA: " + String.valueOf(isMoa) + ", moaIncrements: " +
                //        String.valueOf(moaIncrements), Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(getActivity(), "Please add a target", Toast.LENGTH_LONG).show();
        }
    }

    private void PopulateTargetArray() {
        Scanner scanner = new Scanner(fileInput).useDelimiter("\\|");
        while(scanner.hasNext()) {
            String targetName = scanner.next();
            float height = Float.parseFloat(scanner.next());
            float width = Float.parseFloat(scanner.next());
            String imagePath = scanner.next();
            Uri uri = Uri.parse(scanner.next());
            if(myTargetsArray.length == count+1 && scanner.hasNext()){
                increaseArraySize();
            }
            myTargetsArray[count] = new Target(targetName, height, width, imagePath, uri);
            Log.e("TARGET ENTRIES: ",
                    String.valueOf(myTargetsArray[count].targetName) + ", "
                    + String.valueOf(myTargetsArray[count].height) +
                    ", " + String.valueOf(myTargetsArray[count].width) +
                    ", " + String.valueOf(myTargetsArray[count].imagePath +
                    ", " + String.valueOf(myTargetsArray[count].uri)));
            count++;
        }
        scanner.close();
    }

    public void increaseArraySize() {
        Target[] temp = new Target[myTargetsArray.length + 1];
        System.arraycopy(myTargetsArray, 0, temp, 0, myTargetsArray.length);
        myTargetsArray = temp;
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(targetsFileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("FILE INPUT", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("FILE INPUT", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void deleteEntryInFile(String targetName, float height, float width, String imagePath, Uri uri) {
        final String entryToDelete = targetName + "|" + height + "|" + width + "|" + imagePath + "|" + uri + "|";
        try {
            String data = readFromFile(getActivity());
            if (data.contains(entryToDelete)) {
                String newData = data.replace(entryToDelete, "");
                overwriteFile(newData);
                deleteImage(imagePath);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Unable to delete target", Toast.LENGTH_SHORT).show();
        }
    }

    private void overwriteFile(String newFileData) {
        File file = new File(getActivity().getFilesDir(), targetsFileName);
        if(file.exists()) {
            try {
                FileOutputStream fileOutputStream = getActivity().openFileOutput(targetsFileName, Context.MODE_PRIVATE);
                fileOutputStream.write(newFileData.getBytes());
                fileOutputStream.close();
                Toast.makeText(getActivity(), "Target deleted", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Unable to locate targets file", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteImage(String imagePath) {
        File file = new File(imagePath);
        if(file.exists()){
            if(file.delete()) {
                Toast.makeText(getActivity(), "Target image file deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Unable to delete target image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Unable to locate target image file", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshTargetList() {

        Bundle bundle = new Bundle();
        bundle.putBoolean(SEND_BOOLEAN_TO_TARGET, isMoa);
        bundle.putFloat(SEND_FLOAT_TO_TARGET, moaIncrements);

        Fragment fragment = new TargetActivity();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment).commit();
    }
}
