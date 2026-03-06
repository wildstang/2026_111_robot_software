package org.wildstang.sample.auto.Programs;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public final class AutoPos {
    
    public static final Pose2d lowStart = new Pose2d(3.4, 2.4, new Rotation2d(Math.toRadians(40.9)));
    public static final Pose2d lowPostJump = new Pose2d(6.0, 2.6, new Rotation2d(Math.toRadians(40.9)));
    public static final Pose2d lowPreIntake = new Pose2d(7.2, 0.8, new Rotation2d(Math.toRadians(90)));
    public static final Pose2d lowFinishIntake = new Pose2d(7.7, 3.4, new Rotation2d(Math.toRadians(90)));
    public static final Pose2d lowShootPos = new Pose2d(3.0, 2.4, new Rotation2d(Math.toRadians(40.9)));
    public static final Pose2d lowLoopA = new Pose2d(6.8, 4.0, new Rotation2d(Math.toRadians(90)));
    public static final Pose2d lowLoopB = new Pose2d(5.9, 4.0, new Rotation2d(Math.toRadians(230)));
    public static final Pose2d lowPreJump2 = new Pose2d(5.9, 2.4, new Rotation2d(Math.toRadians(220.9)));
    public static final Pose2d lowShootPos2 = new Pose2d(3.0, 2.4, new Rotation2d(Math.toRadians(220.9)));
    
    public static final Pose2d highStart = new Pose2d(3.63, 5.54, new Rotation2d(Math.toRadians(0)));
    public static final Pose2d highStart2 = new Pose2d(3.63, 5.54, new Rotation2d(Math.toRadians(319.1)));
    public static final Pose2d highPostJump = new Pose2d(6.2, 8.08 - lowPostJump.getY(), new Rotation2d(Math.toRadians(360 - lowPostJump.getRotation().getDegrees())));
    public static final Pose2d highPreIntake = new Pose2d(7.2, 8.08 - lowPreIntake.getY(), new Rotation2d(Math.toRadians(360 - lowPreIntake.getRotation().getDegrees())));
    public static final Pose2d highFinishIntake = new Pose2d(7.7, 8.08 - lowFinishIntake.getY(), new Rotation2d(Math.toRadians(360 - lowFinishIntake.getRotation().getDegrees())));
    public static final Pose2d highShootPos = new Pose2d(3.0, 8.08 - lowShootPos.getY(), new Rotation2d(Math.toRadians(360 - lowShootPos.getRotation().getDegrees())));
    public static final Pose2d highLoopA = new Pose2d(6.8, 8.08 - lowLoopA.getY(), new Rotation2d(Math.toRadians(360 - lowLoopA.getRotation().getDegrees())));
    public static final Pose2d highLoopB = new Pose2d(5.9, 8.08 -lowLoopB.getY(), new Rotation2d(Math.toRadians(360 -lowLoopB.getRotation().getDegrees())));
    public static final Pose2d highPreJump2 = new Pose2d(5.9, 8.08 -lowPreJump2.getY(), new Rotation2d(Math.toRadians(360 -lowPreJump2.getRotation().getDegrees())));
    public static final Pose2d highShootPos2 = new Pose2d(3.0, 8.08 -lowShootPos2.getY(), new Rotation2d(Math.toRadians(360 -lowShootPos2.getRotation().getDegrees())));

    public static final Pose2d highPreDepot = new Pose2d(0.5, 7.4, new Rotation2d(Math.toRadians(260)));
    public static final Pose2d highPostDepot = new Pose2d(0.5, 6.1, new Rotation2d(Math.toRadians(260)));

    public static final Pose2d highJumpToNeutral = new Pose2d(10.0, 5.68, new Rotation2d(Math.toRadians(319.1)));
    public static final Pose2d highJumpToAlliance = new Pose2d(-10.0, 5.68, new Rotation2d(Math.toRadians(319.1)));
    public static final Pose2d highJumpToAlliance2 = new Pose2d(-10.0, 5.68, new Rotation2d(Math.toRadians(139.1)));
    public static final Pose2d lowJumpToNeutral = new Pose2d(10.0, 2.4, new Rotation2d(Math.toRadians(40.9)));
    public static final Pose2d lowJumpToAlliance = new Pose2d(-10.0, 2.4, new Rotation2d(Math.toRadians(40.9)));
    public static final Pose2d highIntake = new Pose2d(8.4, 4.8, new Rotation2d(Math.toRadians(319.1)));
    public static final Pose2d lowIntake = new Pose2d(8.4, 3.28, new Rotation2d(Math.toRadians(40.9)));

    public static final Pose2d preIntake = new Pose2d(7.1, 6.9, new Rotation2d(Math.toRadians(310)));
    public static final Pose2d loopA = new Pose2d(7.6, 5.43, new Rotation2d(Math.toRadians(290)));
    public static final Pose2d loopB = new Pose2d(7.8, 4.0, new Rotation2d(Math.toRadians(270)));
    public static final Pose2d loopC = new Pose2d(6.5, 4.4, new Rotation2d(Math.toRadians(180)));
    public static final Pose2d loopD = new Pose2d(6.0, 3.8, new Rotation2d(Math.toRadians(139.1)));
    public static final Pose2d loopE = new Pose2d(6.2, 2.6, new Rotation2d(Math.toRadians(139.1)));
    
    public static final Pose2d outpostA = new Pose2d(1.75, 5.3, new Rotation2d(Math.toRadians(270)));
    public static final Pose2d outpostB = new Pose2d(1.75, 2.8, new Rotation2d(Math.toRadians(270)));
    public static final Pose2d outpostC = new Pose2d(0.7, 0.7, new Rotation2d(Math.toRadians(180)));
}
