package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Launcher;
import org.wildstang.sample.subsystems.Launcher.GameStates;
import org.wildstang.framework.core.Core;

public class AutoStowLauncherStep extends AutoStep{

    Launcher launcher;

    @Override
    public void initialize() {
       launcher = (Launcher) Core.getSubsystemManager().getSubsystem(WsSubsystems.LAUNCHER);
    }

    @Override
    public void update() {
        launcher.setLauncherState(GameStates.STOW);
        setFinished();
    }

    @Override
    public String toString() {
       return "Auto Stow Launcher Step";
    }
    
    
}
