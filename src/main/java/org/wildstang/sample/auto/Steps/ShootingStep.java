package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Ballpath;
import org.wildstang.sample.subsystems.Intake;
import org.wildstang.sample.subsystems.Launcher;
import org.wildstang.sample.subsystems.Ballpath.GameState;
import org.wildstang.sample.subsystems.Intake.DeployState;
import org.wildstang.sample.subsystems.Intake.IntakeState;
import org.wildstang.sample.subsystems.Launcher.GameStates;

import edu.wpi.first.wpilibj.Timer;

public class ShootingStep extends AutoStep{

    private Launcher launcher;
    private Ballpath ballpath;
    private Intake intake;
    private double time;
    private boolean firstRun = true;
    private Timer timer = new Timer();

    public ShootingStep(double time){
        this.time = time;
    }

    @Override
    public void initialize() {
        launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WsSubsystems.LAUNCHER);
        ballpath = (Ballpath) Core.getSubsystemManager().getSubsystem(WsSubsystems.BALLPATH);
        intake = (Intake) Core.getSubsystemManager().getSubsystem(WsSubsystems.INTAKE);
    }

    @Override
    public void update() {
        if (firstRun){
            launcher.setLauncherState(GameStates.SHOOT);
            ballpath.setBallpathState(GameState.READYING);
            intake.setDeployState(DeployState.STOWED);
            intake.setIntakeState(IntakeState.SLOW);
            timer.start();
            firstRun = false;
        }
        if (timer.hasElapsed(time)){
            ballpath.setBallpathState(GameState.STOP);
            setFinished();
        }
    }

    @Override
    public String toString() {
        return "Shooting Step";
    }
    
}
