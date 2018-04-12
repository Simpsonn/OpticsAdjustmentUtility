package com.analyticalneko.marksmanassistant;

/**
 * Created by Nathaniel Simpson on 2/11/2018.
 */

class OffsetCalculator {

    private int pixel;
    private float inch, poa, poi;
    private float zoom = 1;

    OffsetCalculator(float inch, int pixel, float poa, float poi, float zoom) {
        this.inch = inch;
        this.pixel = pixel;
        this.poa = poa;
        this.poi = poi;
        this.zoom = zoom;
    }

    float calculateOffset() {
        float ppi = pixelsPerInch(inch, pixel);
        float pixelOffset = poa - poi;
        return pixelOffset / ppi / zoom;
    }

    private float pixelsPerInch(float inch, int pixel) {
        return pixel / inch;
    }

}
