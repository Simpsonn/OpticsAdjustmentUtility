package com.analyticalneko.marksmanassistant;

import android.util.Log;

/**
 * Created by Nathaniel Simpson on 2/4/2018.
 */

class ZeroCalculator {

    private boolean isMOA, isYards, isInches;
    private float increments, distance, offset;

    //Calculation uses yards
    private final static float MOA_SIGHT_DISTANCE = 100f;
    private final static float MILS_SIGHT_DISTANCE = 109.36132983377f;

    private final static float MILS_INCH_VALUE = 0.393701f;

    private final static float METERS_TO_YARDS = 1.0936132983377f;
    private final static float CM_TO_INCHES = 0.393701f;

    // Construction for MOA
    ZeroCalculator(boolean isMOA, boolean isYards, boolean isInches, float increments, float distance, float offset) {
        this.isMOA = isMOA;
        this.isYards = isYards;
        this.isInches = isInches;
        this.increments = increments;
        this.distance = distance;
        this.offset = offset;
    }

    // Construction for Mils
    ZeroCalculator(boolean isMOA, boolean isYards, boolean isInches, float distance, float offset) {
        this.isMOA = isMOA;
        this.isYards = isYards;
        this.isInches = isInches;
        this.distance = distance;
        this.offset = offset;
    }

    int calculate(){

        float sightDistance;
        if(!isMOA) {
            increments = MILS_INCH_VALUE;
            sightDistance = MILS_SIGHT_DISTANCE;
        } else {
            sightDistance = MOA_SIGHT_DISTANCE;
        }

        if(!isYards)
            distance = metersToYards(distance);

        if(!isInches)
            offset = cmToInches(offset);

        if(distance == 0) {
            return 0;
        } else {
            Log.d("CALCULATION", String.valueOf(sightDistance / distance / increments * offset)); //DEBUG
            return Math.round(sightDistance / distance / increments * offset);
        }
    }

    private float metersToYards(float meters){
        return meters * METERS_TO_YARDS;
    }

    private float cmToInches(float cm) {
        return cm * CM_TO_INCHES;
    }

}
