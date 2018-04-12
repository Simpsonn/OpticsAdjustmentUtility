package com.analyticalneko.marksmanassistant;

import android.net.Uri;

/**
 * Created by Nathaniel Simpson on 2/7/2018.
 */

public class Target {

    String targetName, imagePath;
    public float height;
    public float width;
    Uri uri;

    public Target(String targetName, float height, float width, String imagePath, Uri uri) {
        this.targetName = targetName;
        this.height = height;
        this.width = width;
        this.imagePath = imagePath;
        this.uri = uri;
    }
}
