package com.analyticalneko.marksmanassistant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Nathaniel Simpson on 2/12/2018.
 */

public class AddTargetActivity extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_EDITOR = 2;

    String mCurrentPhotoPath;
    File imageFile = null;
    Uri pictureUri;
    String targetName;
    float height, width;

    String targetsFileName = "targets.txt";

    ImageView imageView;
    int imageViewDefaultHeight;
    int imageViewDefaultWidth;

    EditText nameEditText, heightEditText, widthEditText;
    Button takePicBtn, addTargetBtn, cancelBtn;

    boolean isTargetAdded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_target_activity);



        imageView = findViewById(R.id.add_target_fragment_image_view);
        imageViewDefaultHeight = imageView.getLayoutParams().height;
        imageViewDefaultWidth = imageView.getLayoutParams().width;

        nameEditText = findViewById(R.id.add_target_name_edit_text);
        heightEditText = findViewById(R.id.add_target_height_edit_text);
        widthEditText = findViewById(R.id.add_target_width_edit_text);

        takePicBtn = findViewById(R.id.add_target_fragment_take_picture_btn);
        addTargetBtn = findViewById(R.id.add_target_add_btn);
        cancelBtn = findViewById(R.id.add_target_cancel_btn);

        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageFile != null)
                    deleteImage(imageFile);

                dispatchTakePictureIntent(v);
            }
        });

        addTargetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageFile == null || nameEditText.getText().toString().matches("") ||
                        heightEditText.getText().toString().matches("") ||
                        widthEditText.getText().toString().matches("")) {
                    if (imageFile == null)
                        Toast.makeText(getApplicationContext(), "Image is empty", Toast.LENGTH_SHORT).show();
                    if (nameEditText.getText().toString().matches(""))
                        Toast.makeText(getApplicationContext(), "Target Name is empty", Toast.LENGTH_SHORT).show();
                    if (heightEditText.getText().toString().matches(""))
                        Toast.makeText(getApplicationContext(), "Height is empty", Toast.LENGTH_SHORT).show();
                    if (widthEditText.getText().toString().matches(""))
                        Toast.makeText(getApplicationContext(), "Width is empty", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        if(containsDuplicate(nameEditText.getText().toString())){
                            Toast.makeText(getApplicationContext(), "Target already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            targetName = nameEditText.getText().toString();
                            height = Float.parseFloat(heightEditText.getText().toString());
                            width = Float.parseFloat(widthEditText.getText().toString());
                            addTargetToFile(targetName, height, width, imageFile.getAbsolutePath(), pictureUri);
                            isTargetAdded = true;
                            onBackPressed();
                            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //startActivity(intent);
                            /*Fragment fragment = new TargetActivity();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fragment)
                                    .commit();*/
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void addTargetToFile(String name, float height, float width, String imagePath, Uri uri) throws IOException {
        final String data = name + "|" + height + "|" + width + "|" + imagePath + "|" + uri + "|";

        File file = new File(getApplicationContext().getFilesDir(), targetsFileName);
        if(!file.exists()) {
            boolean fileCreated = file.createNewFile();
            Log.i("FILE", "Target file created: " + fileCreated);
            Toast.makeText(getApplicationContext(), "Target File created", Toast.LENGTH_SHORT).show();
        }

        FileOutputStream fileOutputStream = openFileOutput(targetsFileName, Context.MODE_APPEND);
        fileOutputStream.write(data.getBytes());
        fileOutputStream.close();
        Toast.makeText(getApplicationContext(), "Target File updated", Toast.LENGTH_SHORT).show();
    }

    private boolean containsDuplicate(String duplicate) throws IOException {

        try {
            String data = readFromFile(getApplicationContext());
            return data.contains(duplicate);
        }catch (Exception e){
            return false;
        }
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
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK) {
                int rotation = getCameraPhotoOrientation(this, pictureUri, imageFile.getAbsolutePath());
                //imageView.setRotation(rotation);
                setImageViewParameters(rotation);

                setPic();

                try {
                    dispatchEditPictureIntent(pictureUri);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Photo Editor not detected", Toast.LENGTH_SHORT).show();
                }
            } else {
                deleteImage(imageFile);
            }
        }

        if(requestCode == REQUEST_IMAGE_EDITOR) {
            int rotation = getCameraPhotoOrientation(this, pictureUri, imageFile.getAbsolutePath());
            //imageView.setRotation(rotation);
            setImageViewParameters(rotation);

            setPic();
        }

        /*if(requestCode == REQUEST_CROP_IMAGE) {
            int rotation = getCameraPhotoOrientation(this, pictureUri, imageFile.getAbsolutePath());
            //imageView.setRotation(rotation);
            setImageViewParameters(rotation);

            setPic();
        }*/

        Log.e("FILE PATH", mCurrentPhotoPath);
    }

    /*private void performCrop(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        int width = options.outWidth;
        int height = options.outHeight;
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(Uri.fromFile(imageFile), "image*//*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", width/height);
            cropIntent.putExtra("aspectY", height/width);
            cropIntent.putExtra("outputX", width - 1000);
            cropIntent.putExtra("outputY", height - 1000);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_CROP_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onBackPressed() {
        if(imageFile != null && !isTargetAdded) {
            deleteImage(imageFile);
        }
        super.onBackPressed();
    }

    public void deleteImage(File file) {
        if(file.exists() && file.delete()) {
            imageView.setImageDrawable(null);
            pictureUri = null;
            Toast.makeText(this, "Picture was deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Picture not detected", Toast.LENGTH_SHORT).show();
        }
    }

    public void dispatchEditPictureIntent(Uri uri) {
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        int flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION;

        editIntent.setDataAndType(uri, "image/*");
        editIntent.addFlags(flags);
        editIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(editIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, flags);
        }

        startActivityForResult(Intent.createChooser(editIntent, null), REQUEST_IMAGE_EDITOR);
    }

    public void dispatchTakePictureIntent(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            imageFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to create image file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        pictureUri = Uri.fromFile(imageFile);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        } else {
            //imageFile = new File(pictureUri.getPath());
            pictureUri = FileProvider.getUriForFile(this,
                    this.getPackageName() + ".provider", imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        }

        //cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    private File createImageFile() throws IOException {
        //Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SCOPEZEROASSISTANT_" + timeStamp + "_";

        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir     /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("FILE PATH", mCurrentPhotoPath);
        return image;
    }

    /*
    Adds the pic to the gallery
     */
    /*private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(pictureUri);
        this.sendBroadcast(mediaScanIntent);
    }*/

    private void setImageViewParameters(int rotation) {
        if (rotation == 90 || rotation == 270){
            int newWidth = imageView.getHeight();
            int newHeight = imageView.getWidth();
            imageView.getLayoutParams().width = newWidth;
            imageView.getLayoutParams().height = newHeight;
        } else {
            imageView.getLayoutParams().width = imageViewDefaultWidth;
            imageView.getLayoutParams().height = imageViewDefaultHeight;
        }
        imageView.setRotation(rotation);
    }

    private int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    /*
    Reduces the amount of dynamic heap used by expanding the JPEG into a memory array that's already
    scaled to match the size of the destination view
     */
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);

        //Toast.makeText(this, "ImageView set", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
