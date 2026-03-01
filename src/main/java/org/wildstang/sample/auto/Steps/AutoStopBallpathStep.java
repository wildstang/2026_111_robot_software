package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.Ballpath;
import org.wildstang.sample.subsystems.Ballpath.GameState;
import org.wildstang.framework.core.Core;


public class AutoStopBallpathStep extends AutoStep{

        Ballpath ballpath;
        
    
        @Override
        public void initialize() {
            ballpath = (Ballpath) Core.getSubsystemManager().getSubsystem(WsSubsystems.BALLPATH);
           
        }
    
        @Override
        public void update() {
            ballpath.setBallpathState(GameState.STOP);
            setFinished();
    }

    @Override
    public String toString() {
        return "Auto Stop Ballpath";
    }
    
}
