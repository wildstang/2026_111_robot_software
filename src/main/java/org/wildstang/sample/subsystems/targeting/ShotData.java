package org.wildstang.sample.subsystems.targeting;

public final class ShotData {

    private static double mToIn = 39.37;

    private static double[] distances = new double[]
        {43.5,  53.5,  71.5,  102.5, 140.5, 178.5, 222.5, 262.5, 293.5};
        //bumpers from edge of hub
    //  0       10     28      59     97     135    179    219    250
    private static double[] flywheelPower = new double[]
        {0.415, 0.415, 0.425, 0.480, 0.530, 0.580, 0.650, 0.700, 0.750};
    private static double[] hoodAngle = new double[]
        {1.30,  2.11,  2.71,  3.37,  3.98,  4.56,  5.06,  5.52,  5.84};
    private static double[] tof = new double[]
        {0.9,   0.9,   0.98,  1.08,  1.18,  1.28,  1.38,  1.44,  1.60};

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
