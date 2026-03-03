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
    public static final Translation2d CENTER_OF_HUB = new Translation2d(182.11*inToM, 158.32*inToM);
    public static final double ALLIANCE_ZONE = 181.56*inToM;
    public static final double halfFieldY = 158.85*inToM;
    public static final Translation2d lowFeedPos = new Translation2d(1.56, 1.5);
    public static final Translation2d lowCenterFeed = new Translation2d(1.56, 3.32);
    public static final Translation2d highFeedPos = new Translation2d(1.56, 6.58);
    public static final Translation2d highCenterFeed = new Translation2d(1.56, 4.76);
    public static final double turretOffset = 145; //in degrees

}