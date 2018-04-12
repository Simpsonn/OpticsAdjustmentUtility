package com.analyticalneko.marksmanassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Nathaniel Simpson on 2/11/2018.
 */

public class ZeroActivity extends Activity {

    //Bundle from CommonTargetSubActivity
    String targetName, imagePath;
    boolean isMoa;
    float moaIncrements;
    float height;
    float width;
    Uri uri;
    boolean isYards;
    float distance;
    final String RECEIVE_NAME_FROM_DISTANCE = "target name zero";
    final String RECEIVE_ISMOA_FROM_DISTANCE = "isMoa zero";
    final String RECEIVE_INCREMENTS_FROM_DISTANCE = "moa increments zero";
    final String RECEIVE_HEIGHT_FROM_DISTANCE = "height zero";
    final String RECEIVE_WIDTH_FROM_DISTANCE = "width zero";
    final String RECEIVE_IMAGE_PATH_FROM_DISTANCE = "image path zero";
    final String RECEIVE_URI_FROM_DISTANCE = "uri zero";
    final String RECEIVE_ISYARDS_FROM_DISTANCE = "isYards zero";
    final String RECEIVE_DISTANCE_FROM_DISTANCE = "distance zero";

    LinearLayout linearLayout;
    TextView elevationOutput, windageOutput, instructionOutput, resetOutput;
    DrawView zeroView;
    ViewGroup parent, parent2;
    TouchImageView imageView;
    ImageView croppedView;
    Button lockBtn, finishedBtn;

    boolean toggleActive = false;

    boolean POASelected = false;
    boolean POISelected = false;

    int pixelWidth, pixelHeight;
    int croppedWidth, croppedHeight;
    int imagePixelWidth, imagePixelHeight;

    float POALocX, POALocY;
    float POILocX, POILocY;

    float offsetX;
    float offsetY;
    boolean isHigh, isLeft;

    int clicksX, clicksY;

    float zoom;

    int rotation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zero_activity);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            targetName = extras.getString(RECEIVE_NAME_FROM_DISTANCE);
            isMoa = extras.getBoolean(RECEIVE_ISMOA_FROM_DISTANCE);
            moaIncrements = extras.getFloat(RECEIVE_INCREMENTS_FROM_DISTANCE);
            height = extras.getFloat(RECEIVE_HEIGHT_FROM_DISTANCE);
            width = extras.getFloat(RECEIVE_WIDTH_FROM_DISTANCE);
            imagePath = extras.getString(RECEIVE_IMAGE_PATH_FROM_DISTANCE);
            uri = Uri.parse(extras.getString(RECEIVE_URI_FROM_DISTANCE));
            isYards = extras.getBoolean(RECEIVE_ISYARDS_FROM_DISTANCE);
            distance = extras.getFloat(RECEIVE_DISTANCE_FROM_DISTANCE);
        }

        /*Toast.makeText(this, "targetName: " + targetName + ", isMOA: " + String.valueOf(isMoa) +
                ", moaIncrements: " + String.valueOf(moaIncrements), Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "Height: " + String.valueOf(height) + ", Width: " +
                String.valueOf(width), Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "isYards: " + String.valueOf(isYards) + ", Distance: " +
                distance, Toast.LENGTH_SHORT).show();*/

        linearLayout = findViewById(R.id.zero_fragment_output_layout);
        elevationOutput = findViewById(R.id.zero_fragment_elevation_output_text);
        windageOutput = findViewById(R.id.zero_fragment_windage_output_text);
        instructionOutput = findViewById(R.id.zero_fragment_instruction_text_view);
        resetOutput = findViewById(R.id.zero_fragment_reset_text);
        resetOutput.setVisibility(View.INVISIBLE);

        imageView = findViewById(R.id.zero_fragment_image_view);
        croppedView = findViewById(R.id.zero_fragment_finalized_image_view);

        zeroView = findViewById(R.id.zero_fragment_zero_view);
        parent = (ViewGroup) zeroView.getParent();
        parent.removeView(zeroView);

        lockBtn = findViewById(R.id.zero_fragment_lock_btn);
        finishedBtn = findViewById(R.id.zero_fragment_finish_btn);

        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                int height = imageView.getMeasuredHeight();
                int width = imageView.getMeasuredWidth();

                setImagePixelDimensions(width, height);

                Log.e("DIMENSIONS", "Image H: " + height + ", Image W: " + width);//DEBUG
                return true;
            }
        });

        ViewTreeObserver viewTreeObserver1 = croppedView.getViewTreeObserver();
        viewTreeObserver1.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                croppedView.getViewTreeObserver().removeOnPreDrawListener(this);

                int height = croppedView.getMeasuredHeight();
                int width = croppedView.getMeasuredWidth();

                setCroppedViewDimensions(width, height);

                Log.e("DIMENSIONS", "Cropped H: " + height + ", Cropped W: " + width);//DEBUG
                return true;
            }
        });

        ViewTreeObserver viewTreeObserver2 = zeroView.getViewTreeObserver();
        viewTreeObserver2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                zeroView.getViewTreeObserver().removeOnPreDrawListener(this);

                int height = zeroView.getMeasuredHeight();
                int width = zeroView.getMeasuredWidth();

                rotation = getCameraPhotoOrientation(getApplicationContext(), uri,
                        imagePath);

                setZeroViewParameters(rotation, width, height);

                setPixelDimensions(width, height);
                normalizeZeroViewDimensions();

                Log.e("DIMENSIONS", "Zero H: " + height + ", Zero W: " + width);//DEBUG

                return true;
            }
        });

        rotation = getCameraPhotoOrientation(getApplicationContext(), uri, imagePath);
        //setImageViewParameters(rotation);
        setPic();
        imageView.setRotation(rotation);
        croppedView.setRotation(rotation);
        //imageView.setImageURI(uri);

        zeroView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int imageX = (int) event.getX();
                int imageY = (int) event.getY();

                if(toggleActive) {

                    if(!POASelected) {
                        setPOAXY(imageX, imageY);
                        POASelected = true;
                        instructionOutput.setText(R.string.zero_fragment_select_poi);
                        zeroView.setStep(2);
                        Log.e("COORDS", "POA: X: " + imageX + " Y: " + imageY);//DEBUG
                    } else if (!POISelected) {
                        setPOIXY(imageX, imageY);
                        POISelected = true;

                        OffsetCalculator offsetCalculatorX;
                        //Toast.makeText(getApplicationContext(), "Width: " + String.valueOf(width), Toast.LENGTH_SHORT).show();
                        offsetCalculatorX = new OffsetCalculator(width, pixelWidth, POALocX, POILocX, zoom);
                        offsetX = offsetCalculatorX.calculateOffset();
                        if (offsetX < 0) {
                            isLeft = false;
                            offsetX = Math.abs(offsetX);
                        } else {
                            isLeft = true;
                        }

                        OffsetCalculator offsetCalculatorY;
                        //Toast.makeText(getApplicationContext(), "Height: " + String.valueOf(height), Toast.LENGTH_SHORT).show();
                        offsetCalculatorY = new OffsetCalculator(height, pixelHeight, POALocY, POILocY, zoom);
                        offsetY = offsetCalculatorY.calculateOffset();
                        if (offsetY < 0) {
                            isHigh = false;
                            offsetY = Math.abs(offsetY);
                        } else {
                            isHigh = true;
                        }

                        ZeroCalculator zeroCalculatorX;
                        zeroCalculatorX = new ZeroCalculator(isMoa, isYards, true, moaIncrements, distance, offsetX);
                        clicksX = zeroCalculatorX.calculate();

                        ZeroCalculator zeroCalculatorY;
                        zeroCalculatorY = new ZeroCalculator(isMoa, isYards, true, moaIncrements, distance, offsetY);
                        clicksY = zeroCalculatorY.calculate();

                        instructionOutput.setText("");
                        setInstructionOutput(clicksX, clicksY, isHigh, isLeft);

                        //instructionOutput.setText(R.string.zero_fragment_adjustments_calculated);
                        resetOutput.setVisibility(View.VISIBLE);
                        zeroView.setStep(3);
                        //linearLayout.setBackground(getResources().getDrawable(R.drawable.output_result_background));
                        Log.e("COORDS", "POI: X: " + imageX + " Y: " + imageY);//DEBUG
                        Log.e("CALCULATION", "OFFSETX: " + offsetX + " OFFSETY: " + offsetY);
                        Log.e("OUTPUT", "CLICKSX: " + clicksX + " CLICKSY: " + clicksY);
                    } else {
                        POASelected = false;
                        POISelected = false;
                        windageOutput.setText("");
                        elevationOutput.setText("");
                        instructionOutput.setText(R.string.zero_fragment_select_poa);
                        resetOutput.setVisibility(View.INVISIBLE);
                        zeroView.setStep(1);
                        //linearLayout.setBackground(getResources().getDrawable(R.drawable.output_null_background));
                        Log.e("COORDS", "RESET");//DEBUG
                    }

                }

                return false;
            }

        });

        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toggleActive) {
                    instructionOutput.setText("");

                    zeroView.setStep(1);
                    POASelected = false;
                    POISelected = false;

                    normalizeViewDimensions();
                    Log.i("NORMALIZE VIEW", "New Width: " + String.valueOf(width) +
                            ", New Height: " + String.valueOf(height));
                    /*Toast.makeText(getApplicationContext(), "New Width: " + String.valueOf(width) +
                            ", New Height: " + String.valueOf(height), Toast.LENGTH_SHORT).show();*/

                    zoom = imageView.getCurrentZoom();
                    Log.i("ZOOM", "Zoom level: " + zoom);
                    //Toast.makeText(getApplicationContext(), "Zoom: " + String.valueOf(zoom), Toast.LENGTH_SHORT).show();

                    lockBtn.setText(R.string.zero_fragment_unlock);
                    instructionOutput.setText(R.string.zero_fragment_select_poa);

                    croppedView.setImageBitmap(setBitmap(imageView));
                    parent.addView(zeroView);
                    zeroView.setToggle(true);
                    zeroView.setStep(1);

                    parent2 = (ViewGroup) imageView.getParent();
                    parent2.removeView(imageView);

                    toggleActive = true;
                } else {
                    resetOutput.setVisibility(View.INVISIBLE);
                    croppedView.setImageDrawable(null);
                    parent.removeView(zeroView);
                    parent2.addView(imageView);

                    //linearLayout.setBackground(getResources().getDrawable(R.drawable.output_null_background));

                    imageView.resetZoom();

                    windageOutput.setText("");
                    elevationOutput.setText("");
                    lockBtn.setText(R.string.zero_fragment_lock);
                    instructionOutput.setText(R.string.zero_fragment_set_zoom);

                    toggleActive = false;
                }
            }
        });

        finishedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    public void setImagePixelDimensions(int x, int y) {
        this.imagePixelWidth = x;
        this.imagePixelHeight = y;
    }

    public void setCroppedViewDimensions(int x, int y) {
        this.croppedWidth = x;
        this.croppedHeight = y;
    }

    public void setPixelDimensions(int x, int y) {
        this.pixelWidth = x;
        this.pixelHeight = y;
    }

    public void setPOAXY(int x, int y) {
        this.POALocX = x;
        this.POALocY = y;
    }

    public void setPOIXY(int x, int y) {
        this.POILocX = x;
        this.POILocY = y;
    }

    public void setInstructionOutput(int windage, int elevation, boolean isHigh, boolean isLeft) {
        if(isLeft) {
            String windageText;
            if(windage == 1) {
                windageText = "Adjust windage " + windage + " click right";
            } else {
                windageText = "Adjust windage " + windage + " clicks right";
            }
            windageOutput.setText(windageText);
        } else {
            String windageText;
            if(windage == 1) {
                windageText = "Adjust windage " + windage + " click left";
            } else {
                windageText = "Adjust windage " + windage + " clicks left";
            }
            windageOutput.setText(windageText);
        }

        if(isHigh) {
            String elevationText;
            if(elevation == 1) {
                elevationText = "Adjust elevation " + elevation + " click down";
            } else {
                elevationText = "Adjust elevation " + elevation + " clicks down";
            }
            elevationOutput.setText(elevationText);
        } else {
            String elevationText;
            if(elevation == 1) {
                elevationText = "Adjust elevation " + elevation + " click up";
            } else {
                elevationText = "Adjust elevation " + elevation + " clicks up";
            }
            elevationOutput.setText(elevationText);
        }
    }

    private void normalizeZeroViewDimensions() {
        if(rotation == 90 || rotation == 270) {
            float tempHeight = pixelHeight;
            pixelHeight = pixelWidth;
            float term = Float.parseFloat(String.valueOf(pixelWidth)) / tempHeight;
            pixelWidth = (int) (term * Float.parseFloat(String.valueOf(pixelWidth)));
        }
    }

    private void normalizeViewDimensions() {
        if(this.width < this.height) {
            this.width = this.imagePixelWidth / (this.imagePixelHeight / this.height);
        } else if (this.width > this.height) {
            this.height = this.imagePixelHeight / (this.imagePixelWidth / this.width);
        }
    }

    private void setZeroViewParameters(int rotation, int width, int height) {
        ViewGroup.LayoutParams params = zeroView.getLayoutParams();
        if(rotation == 90 || rotation == 270) {
            params.width = height;
            params.height = width;
        } else {
            params.width = width;
            params.height = height;
        }
        zeroView.setLayoutParams(params);
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

    private void setPic() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private Bitmap setBitmap(ImageView view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);

        return Bitmap.createScaledBitmap(imageView.getDrawingCache(true),
                croppedWidth, croppedHeight, true);
    }

}
