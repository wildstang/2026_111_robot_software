package org.wildstang.sample.auto.Programs;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public final class AutoPos {
    
    public static final Pose2d lowStart = new Pose2d(3.4, 2.4, new Rotation2d(Math.toRadians(40.9)));
    public static final Pose2d lowPostJump = new Pose2d(6.0, 2.4, new Rotation2d(Math.toRadians(40.9)));
    public static final Pose2d lowPreIntake = new Pose2d(7.2, 1.4, new Rotation2d(Math.toRadians(60)));
    public static final Pose2d lowFinishIntake = new Pose2d(7.7, 3.4, new Rotation2d(Math.toRadians(90)));
    public static final Pose2d lowShootPos = new Pose2d(3.0, 2.4, new Rotation2d(Math.toRadians(40.9)));
    public static final Pose2d lowLoopA = new Pose2d(6.8, 4.0, new Rotation2d(Math.toRadians(90)));
    public static final Pose2d lowLoopB = new Pose2d(5.9, 4.0, new Rotation2d(Math.toRadians(230)));
    public static final Pose2d lowPreJump2 = new Pose2d(5.9, 2.4, new Rotation2d(Math.toRadians(220.9)));
    public static final Pose2d lowShootPos2 = new Pose2d(3.0, 2.4, new Rotation2d(Math.toRadians(220.9)));
    
    public static final Pose2d highStart = new Pose2d(3.4, 8.08 - lowStart.getY(), new Rotation2d(Math.toRadians(360 - lowStart.getRotation().getDegrees())));
    public static final Pose2d highPostJump = new Pose2d(3.4, 8.08 - lowPostJump.getY(), new Rotation2d(Math.toRadians(360 - lowPostJump.getRotation().getDegrees())));
    public static final Pose2d highPreIntake = new Pose2d(3.4, 8.08 - lowPreIntake.getY(), new Rotation2d(Math.toRadians(360 - lowPreIntake.getRotation().getDegrees())));
    public static final Pose2d highFinishIntake = new Pose2d(3.4, 8.08 - lowFinishIntake.getY(), new Rotation2d(Math.toRadians(360 - lowFinishIntake.getRotation().getDegrees())));
    public static final Pose2d highShootPos = new Pose2d(3.4, 8.08 - lowShootPos.getY(), new Rotation2d(Math.toRadians(360 - lowShootPos.getRotation().getDegrees())));
    public static final Pose2d highLoopA = new Pose2d(3.4, 8.08 - lowLoopA.getY(), new Rotation2d(Math.toRadians(360 - lowLoopA.getRotation().getDegrees())));
    public static final Pose2d highLoopB = new Pose2d(3.4, 8.08 -lowLoopB.getY(), new Rotation2d(Math.toRadians(360 -lowLoopB.getRotation().getDegrees())));
    public static final Pose2d highPreJump2 = new Pose2d(3.4, 8.08 -lowPreJump2.getY(), new Rotation2d(Math.toRadians(360 -lowPreJump2.getRotation().getDegrees())));
    public static final Pose2d highShootPos2 = new Pose2d(3.4, 8.08 -lowShootPos2.getY(), new Rotation2d(Math.toRadians(360 -lowShootPos2.getRotation().getDegrees())));

    public static final Pose2d highPreDepot = new Pose2d(0.5, 7.4, new Rotation2d(Math.toRadians(260)));
    public static final Pose2d highPostDepot = new Pose2d(0.5, 6.1, new Rotation2d(Math.toRadians(260)));
}
