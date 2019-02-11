/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6843.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Add your docs here.
 */
public class HatchPanelSubsystem extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private Compressor AlwaysOff = new Compressor(24);
  private DoubleSolenoid HatchJaws = new DoubleSolenoid(0,1);

  public HatchPanelSubsystem(){
    //Turns off the compressor because we have no compressor on the compressor port of this PCM
    AlwaysOff.setClosedLoopControl(false);
  }
  
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public void jawsOff(){
    HatchJaws.set(Value.kOff);
  }

  public void openJaws() {
    HatchJaws.set(Value.kForward);
  }

  public void closeJaws() {
    HatchJaws.set(Value.kReverse);
  }
}