package org.wildstang.sample.subsystems.targeting;

public final class ShotData {

    private static double[] distances = new double[]
        {};
    private static double[] flywheelPower = new double[]
        {};
    private static double[] hoodAngle = new double[]
        {};
    private static double[] tof = new double[]
        {};

    public static double getTOF(double shotDistance){
        return lookupTable(shotDistance, tof);
    }
    public static double getFlywheelPower(double shotDistance){
        return lookupTable(shotDistance, flywheelPower);
    }
    public static double getHoodAngle(double shotDistance){
        return lookupTable(shotDistance, hoodAngle);
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
