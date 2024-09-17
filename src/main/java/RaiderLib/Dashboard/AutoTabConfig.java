package RaiderLib.Dashboard;

public class AutoTabConfig {
  /*
   * Constraints
   * 1. start - ran at the start of every auto
   * 2. drive - command that follows a choreo trajectory, takes a drive subsystem and trajectory
   * 3. action - other commands to be ran during auto, e.g. intake, shoot; specificed through characters in auto path
   * 4. end - ran at the end of every auto
   *
   * All start and end commands will be ran in the order they appear in the list
   *
   * First character must correspond to a valid starting position
   *
   * Poses are represented in the string by uppercase letters, actions by single-digit numbers (as characters)
   *
   * Sequential operations are expressed as words separated by spaces
   * Actions within the same word will be ran simultaneously as a ParallelCommandGroup
   * There should be only up to one pose per word; any additional will be ignored
   * If present, the pose should be the first character in the word
   */
}
