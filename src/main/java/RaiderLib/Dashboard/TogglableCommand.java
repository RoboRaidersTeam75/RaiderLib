// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package RaiderLib.Dashboard;

import edu.wpi.first.wpilibj2.command.Command;

public class TogglableCommand extends Command {
  private Runnable toRun;
  private String toggleName;

  public TogglableCommand(Runnable toRun, String toggleName) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.toRun = toRun;
    this.toggleName = toggleName;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (DebugTab.getOrAddDebugToggle(toggleName)) {
      toRun.run();
    }
  }
}
