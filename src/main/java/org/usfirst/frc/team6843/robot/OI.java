/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6843.robot;

import org.usfirst.frc.team6843.robot.commands.DriveTillCancelled;
import org.usfirst.frc.team6843.robot.commands.DriveTo;
import org.usfirst.frc.team6843.robot.commands.LowerFront;
import org.usfirst.frc.team6843.robot.commands.PneumaticsOff;
import org.usfirst.frc.team6843.robot.commands.RaiseFront;
import org.usfirst.frc.team6843.robot.commands.RaiseRear;
import org.usfirst.frc.team6843.robot.commands.LowerRear;
import org.usfirst.frc.team6843.robot.commands.RotateTo;
import org.usfirst.frc.team6843.robot.commands.ToggleLimitTest;
import org.usfirst.frc.team6843.robot.commands.OpenJaws;
import org.usfirst.frc.team6843.robot.commands.CloseJaws;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	/** Used to enforce drive stick dead zones. */
	public static final double DEAD_ZONE = 0.1;
	public static final int DRIVE_AXIS = 1;
	public static final int CURVE_AXIS = 4;

	private final XboxController driver = new XboxController(0); 
	// The above line ^ creates a new controller on USB port 0, or the first controller plugged in
	private final Button driverY = new JoystickButton(driver, 4);
	private final Button driverB = new JoystickButton(driver, 2);
	private final Button driverA = new JoystickButton(driver, 1);
	private final Button driverX = new JoystickButton(driver, 3);
	private final Button driverBumperLeft = new JoystickButton(driver, 5);
	private final Button driverBumperRight = new JoystickButton(driver, 6);
	private final Button driverBack = new JoystickButton(driver, 7);
	private final Button driverStart = new JoystickButton(driver, 8);

	private final XboxController secondary = new XboxController(1);
	private final Button secondaryY = new JoystickButton(driver, 4);
	private final Button secondaryB = new JoystickButton(driver, 2);
	private final Button secondaryA = new JoystickButton(driver, 1);
	private final Button secondaryX = new JoystickButton(driver, 3);
	private final Button secondaryBumperLeft = new JoystickButton(driver, 5);
	private final Button secondaryBumperRight = new JoystickButton(driver, 6);
	private final Button secondaryBack = new JoystickButton(driver, 7);
	private final Button secondaryStart = new JoystickButton(driver, 8);

	public OI() { // this is where you assign commands to buttons
		driverY.whenPressed(new RotateTo(0.0));
		driverB.whenPressed(new RotateTo(90.0));
		driverA.whenPressed(new RotateTo(180.0));
		driverX.whenPressed(new RotateTo(-90.0));
		driverBumperLeft.whileHeld(new DriveTillCancelled(true));
		driverBumperRight.whileHeld(new DriveTillCancelled(false));
		driverBack.whenPressed(new DriveTo(-100));
		driverStart.whenPressed(new DriveTo(100));
		
		//below this line is for secondary controller
		secondaryY.whenPressed(new RaiseFront());
		secondaryB.whenPressed(new LowerFront());
		secondaryA.whenPressed(new ToggleLimitTest());
		secondaryX.whenPressed(new PneumaticsOff());
		secondaryBumperLeft.whenPressed(new RaiseRear());
		secondaryBumperRight.whenPressed(new LowerRear());
		secondaryBack.whenPressed(new OpenJaws());
		secondaryStart.whenPressed(new CloseJaws());

	}

	/**
	 * @return the drive power using cubed inputs.
	 */
	public double getDrivePower() {
		double drivePower = -driver.getRawAxis(DRIVE_AXIS);
		if (Math.abs(drivePower) < DEAD_ZONE)
		{
			drivePower = 0.0;
		}
		return Math.pow(drivePower, 3.0);
	}

	/**
	 * @return the drive power using inputs to the fifth.
	 */
	public double getCurvePower() {
		double curvePower = driver.getRawAxis(CURVE_AXIS);
		if (Math.abs(curvePower) < DEAD_ZONE)
		{
			curvePower = 0.0;
		}
		return Math.pow(curvePower, 5.0);
	}
}
