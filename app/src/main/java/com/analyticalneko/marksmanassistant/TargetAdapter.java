package com.analyticalneko.marksmanassistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Nathaniel Simpson on 2/7/2018.
 */

public class TargetAdapter extends ArrayAdapter<Target> {

    private Context mContext;
    private int mLayoutResourceId;
    private Target mData[] = null;

    TargetAdapter(Context context, int resource, Target[] data) {
        super(context, resource, data);

        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mData = data;
    }

    @Override
    public Target getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View row = convertView;
        PlaceHolder holder;

        if(row == null) {
            //Create new View
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new PlaceHolder();

            holder.nameView = row.findViewById(R.id.target_row_title_text_view);
            holder.descriptionView = row.findViewById(R.id.target_row_description_text_view);
            holder.imageView = row.findViewById(R.id.target_row_image_view);

            row.setTag(holder);
        } else {
            //Otherwise use an existing one
            holder = (PlaceHolder) row.getTag();
        }

        //Getting data from the data array
        Target target = mData[position];

        //Setup and reuse the same listener for each row
        Integer rowPosition = position;
        holder.imageView.setTag(rowPosition);

        //Setting the view to reflect the data we need to display
        holder.nameView.setText(target.targetName);
        //Log.e("TARGET ADAPTER: ", holder.nameView.getText().toString());
        String description = String.valueOf(target.width) + "\"W x " +
                String.valueOf(target.height) + "\"H";
        holder.descriptionView.setText(description);
        //Log.e("TARGET ADAPTER: ", holder.descriptionView.getText().toString());

        //For getting the image
        int rotation = getCameraPhotoOrientation(getContext(), target.uri, target.imagePath);
        holder.imageView.setRotation(rotation);
        setPic(holder.imageView, target.imagePath);
        //holder.imageView.setImageURI(target.uri);
        //Log.e("TARGET ADAPTER: ", holder.imageView.toString());

        return row;
    }

    private static class PlaceHolder {

        TextView nameView;
        TextView descriptionView;
        ImageView imageView;

    }

    private void setPic(ImageView imageView, String imagePath) {
        // Get the dimensions of the View
        /*int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();*/

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int pixels = dpToPx();
        int scaleFactor = Math.min(photoW/pixels, photoH/pixels);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
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

    private int dpToPx() {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float fPixels = metrics.density * (float) 80;
        return (int) (fPixels + 0.5f);
    }

}
