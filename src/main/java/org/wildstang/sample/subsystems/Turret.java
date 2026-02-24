package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.outputs.WsTalon;
import org.wildstang.sample.robot.WsOutputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.sample.subsystems.targeting.WsPose;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret implements Subsystem{

    WsPose pose;
    
    //I think we only need one homing state
    public double desiredTurretAngle, actualAngle = 0;
    public SwerveDrive swerve;

    private final double turretStartOffset = 145 * 43.5/360;

    private WsTalon turretMotor;
    private boolean hadWrapped = false;

    double turretStart = 0;

    @Override
    public void inputUpdate(Input source) {
    }

    @Override
    public void init() {
        turretMotor = (WsTalon) WsOutputs.TURRET.get();
        turretMotor.initClosedLoop(0.16, 0.0, 0.0, 0.03, 0.0, true);
        turretMotor.setCurrentLimit(40,40);

        turretStart = turretMotor.getPosition()-(turretStartOffset);
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        //returns staic angle of turret, robot centric, wrapped [0,360)
        desiredTurretAngle = pose.fromFieldToRobotAngle(pose.angleOfTurret());
        actualAngle = rotateTurret();
        
        turretMotor.setPosition(actualAngle*43.5/360+turretStart);
        SmartDashboard.putNumber("Turret position", turretMotor.getPosition());
        SmartDashboard.putNumber("Turret target", actualAngle*43.5/360+turretStart);
        SmartDashboard.putNumber("Turret robot centric target", desiredTurretAngle);
        SmartDashboard.putBoolean("Turret good to fire", goodToFire());
        SmartDashboard.putNumber("Turret angleofturret", pose.angleOfTurret());

    }

    public double rotateTurret(){
        //note that desiredAngle will probably only be 0-360
        //this seems to assume it'll be up to 420

        // if(turretState == GameStates.FIRING){

        //      if((desiredTurretAngle <= 240) && (desiredTurretAngle >= 0)){
        //         actualAngle = desiredTurretAngle;
        //     }else{
        //         turretState = GameStates.HOMING;
        //     }
            // doing logic to find out which level to go to (example: if you are above 360 and want to go to the lower coterminal angle)
            // if there is a coterminal angle, you decide whether to rotate ccw or cw
            //this will apply to both cases - see if actual angle being in the 360-420
            //range is better, and then do the +360
            /*if(desiredTurretAngle <= 90){
                if((Math.abs(turretMotor.getPosition() - desiredTurretAngle)) 
                    > (Math.abs(turretMotor.getPosition() - (desiredTurretAngle+360)))){
                        actualAngle = desiredTurretAngle + 360;
                }else{
                        actualAngle = desiredTurretAngle;
                }    
            
            }else{
                actualAngle = desiredTurretAngle;
            }*/
        // }else if(turretState == GameStates.HOMING){

            if((desiredTurretAngle <= 360) && (desiredTurretAngle >= 0)){
                return desiredTurretAngle;
            }else if(desiredTurretAngle <= 270){
                hadWrapped = true;
                return 240;
            }else if(desiredTurretAngle >= 330){
                hadWrapped = true;
                return 0;
            }
            //determine which hardstop we are further from and go there (it gives us more wiggle room)
             /*if(desiredTurretAngle < 30 && desiredTurretAngle >= 0){
                //always rotate to higher coterminal
                actualAngle = desiredTurretAngle + 360;
             }else if(desiredTurretAngle < 60){
                if((Math.abs(turretMotor.getPosition() - desiredTurretAngle)) 
                    > (Math.abs(turretMotor.getPosition() - (desiredTurretAngle+360)))){
                        actualAngle = desiredTurretAngle + 360;
                }else{
                        actualAngle = desiredTurretAngle;
                } 
             }else{
                actualAngle = desiredTurretAngle;
             }*/
                
     //*/
             
        // }
            hadWrapped = true;
        return actualAngle;
    }


    public boolean goodToFire(){
        // if (desiredTurretAngle > 240) return false;
        //good
        double wiggle = Math.abs(rotateTurret()*43.5/360+turretStart - turretMotor.getPosition());
        return wiggle < 1.0;
    }
    public boolean keepFiring(){
        return true;
        // if (!hadWrapped) return true;
        // if (Math.abs(rotateTurret()*43.5/360+turretStart - turretMotor.getPosition()) < 1.0){
        //     hadWrapped = false;
        //     return true;
        // }
        // return false;
    }

    @Override
    public void resetState() {
    }

    @Override
    public void initSubsystems() {
        pose = (WsPose) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_POSE);
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }

    @Override
    public String getName() {
        return "Turret";
    }
    
}
