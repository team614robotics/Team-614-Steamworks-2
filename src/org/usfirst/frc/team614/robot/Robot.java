
package org.usfirst.frc.team614.robot;

import org.team708.robot.util.Gamepad;
import org.usfirst.frc.team614.robot.commands.ExampleCommand;
import org.usfirst.frc.team614.robot.subsystems.ExampleSubsystem;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	public static PowerDistributionPanel pdp = new PowerDistributionPanel();
	
	public static Gamepad gamepad = new Gamepad(0);

	public CANTalon talonMaster = new CANTalon(1);
	public CANTalon talonSlave = new CANTalon(3);
	public VictorSP victorFeeder = new VictorSP(4);
	

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		oi = new OI();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		
		double kP = 0.10;
		double kI = 0.0005;
		double kD = 0;
		double kF = 0.0404;

		talonMaster.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);

		talonMaster.changeControlMode(TalonControlMode.Speed);
		
		talonMaster.setVoltageRampRate(36.0);
		talonMaster.setStatusFrameRateMs(StatusFrameRate.Feedback, 10);
		talonMaster.clearStickyFaults();

		talonMaster.reverseSensor(false);
		talonMaster.reverseOutput(true);

        talonMaster.setProfile(1);
        
        talonMaster.setP(kP);
        talonMaster.setI(kI);
        talonMaster.setD(kD);
        talonMaster.setF(kF);
        
		talonMaster.set(0.0);
		
        talonMaster.enable();
        
        
		talonSlave.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);

		talonSlave.changeControlMode(TalonControlMode.Speed);
		
		talonSlave.setVoltageRampRate(36.0);
		talonSlave.setStatusFrameRateMs(StatusFrameRate.Feedback, 10);
		talonSlave.clearStickyFaults();
		

        talonSlave.setProfile(1);
        
        talonSlave.setP(kP);
        talonSlave.setI(kI);
        talonSlave.setD(kD);
        talonSlave.setF(kF);
        
		talonSlave.set(0.0);
		
        talonSlave.enable();
        
        

		talonSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
		talonSlave.set(talonMaster.getDeviceID());
		talonSlave.reverseOutput(true);
        
		victorFeeder.set(0);

		SmartDashboard.putNumber("Shooter CAN Talon Setpoint", SmartDashboard.getNumber("Shooter CAN Talon Setpoint", 0));
		SmartDashboard.putNumber("Shooter Feeder Speed", SmartDashboard.getNumber("Shooter Feeder Speed", 0));
        SmartDashboard.putNumber("Shooter CAN Talon Speed", 0);
        SmartDashboard.putNumber("Shooter CAN Talon Error", 0);
        SmartDashboard.putNumber("Shooter Amperage", 0);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		
//		talonMaster.changeControlMode(TalonControlMode.PercentVbus);
//		talonMaster.set(1.0);

		if (gamepad.getRawButton(Gamepad.button_A)) {
			talonMaster.changeControlMode(TalonControlMode.Speed);
			talonMaster.set(SmartDashboard.getNumber("Shooter CAN Talon Setpoint", 1000));
		}
//		
//		// Child Mode
//		else if (gamepad.getRawButton(Gamepad.button_Y)) {
//			talonMaster.changeControlMode(TalonControlMode.Speed);
//			talonMaster.set(20.0 * 4220 / 100); // Value Subject To Change.
//		}
//		
		else {
			talonMaster.changeControlMode(TalonControlMode.PercentVbus);
			talonMaster.set(0.0);
		}

//		if (gamepad.getRawButton(Gamepad.button_X)) {
//			talonMaster.changeControlMode(TalonControlMode.PercentVbus);
//			talonMaster.set(SmartDashboard.getNumber("Shooter CAN Talon Setpoint", 1000));
//		}
//		else {
//			talonMaster.changeControlMode(TalonControlMode.PercentVbus);
//			talonMaster.set(0.0);
//		}
		if (gamepad.getRawButton(Gamepad.button_B)) {
			victorFeeder.set(SmartDashboard.getNumber("Shooter Feeder Speed", 0.5));
		}
		
		else {
			victorFeeder.set(0);
		}
		
        SmartDashboard.putNumber("Shooter CAN Talon Speed", talonMaster.getSpeed());
        SmartDashboard.putNumber("Shooter CAN Talon Error", talonMaster.getSpeed() - talonMaster.getSetpoint());
        SmartDashboard.putNumber("Shooter Amperage", pdp.getCurrent(3));
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
