package frc.robot.commands.IntakeFeeder;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.IntakeSubsystem.IntakeState;

public class DeployIntakeCommand extends CommandBase {
    private IntakeSubsystem m_intake;
    private IntakeState m_newIntakeState;

    public DeployIntakeCommand(IntakeSubsystem intakeSub, IntakeState state) {
        m_intake = intakeSub;
        m_newIntakeState = state;
        addRequirements(intakeSub);

        m_intake.updateIntakeState(m_newIntakeState);
    }

    @Override
    public void execute() {
        // Since this code needs to execute just once, the isFinished() should return true so that
        // execute doesn't get called more than once.
        
    }

    public boolean isFinished() {
        // Since we just want the command to execute just one.
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        // There is nothing to clean up.
        m_intake.updateIntakeState(IntakeState.UP);
    }
}