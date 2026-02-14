package org.wildstang.sample.subsystems.targeting;

import java.util.List;


import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.geometry.Pose2d;

public class VisionConsts {

    public static final double inToM = 1/39.37;
    public final double mToIn = 39.37;
    public static final Transform3d camTransform = new Transform3d(new Translation3d(0.199778, -0.193031, 1.020310), new Rotation3d(0, -32 * Math.PI / 180, 10 * Math.PI / 180));
    public static final double[] CENTER_OF_HUB = {182.11*inToM, 158.32*inToM};
    public static final double ALLIANCE_ZONE = 181.56*inToM;
    public static final double halfFieldY = 158.85*inToM;
    public static final double[] lowFeedPos = {79*inToM,76*inToM}; 
    public static final double[] highFeedPos = {79*inToM,214.7*inToM}; 
    public static final double robotGyroConstantThingy = 0; //Where 0 is on the robot based on what we make it
    public static final double turretOffset = 1; //in degrees

}