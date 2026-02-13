package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.sample.auto.Steps.AutoSetupStep;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class SampleAutoProgram extends AutoProgram{

    @Override
    protected void defineSteps() {
        addStep(new AutoSetupStep(0, 0, 180.0, Alliance.Blue));
    }

    @Override
    public String toString() {
        return "Sample";
    }
    
}
