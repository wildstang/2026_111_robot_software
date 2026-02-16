package org.wildstang.sample.subsystems.targeting;

public final class ShotData {

    private static double mToIn = 39.37;

    private static double[] distances = new double[]
        {102,  112,  124,   133,  142,   167,   180,    205,    230,   260,    290};
    private static double[] flywheelPower = new double[]
        {0.65, 0.65, 0.65,  0.65, 0.65,  0.65,  0.65,   0.7,    0.725, 0.75,   0.775};
    private static double[] hoodAngle = new double[]
        {1.76, 2.01, 2.352, 2.38, 2.467, 2.718, 3.0605, 3.2075, 3.483, 3.9775, 4.70};
    private static double[] tof = new double[]
        {1.4,  1.4,  1.405, 1.425,1.487, 1.45,  1.4,    1.51,   1.58,  1.61,   1.55};

    public static double getTOF(double shotDistance){
        return lookupTable(shotDistance*mToIn, tof);
    }
    public static double getFlywheelPower(double shotDistance){
        return lookupTable(shotDistance*mToIn, flywheelPower);
    }
    public static double getHoodAngle(double shotDistance){
        return lookupTable(shotDistance*mToIn, hoodAngle);
    }
    private static double lookupTable(double distanceValue, double[] data){
        if (distanceValue < distances[0]) return data[0];
        for (int i = 1; i < distances.length; i++){
            if (distanceValue < distances[i]){
                return data[i-1] + (data[i]-data[i-1]) * 
                    (distanceValue - distances[i-1]) / (distances[i] - distances[i-1]);
            }
        }
        return data[distances.length-1] + (distanceValue - distances[distances.length-1]) * 
            (data[distances.length-1] - data[distances.length-2]) / (distances[distances.length-1] - distances[distances.length-2]);
    }
}
