package com.palyrobotics.frc2018.config;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.util.DriveSignal;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Commands represent the desired setpoints and subsystem states for the robot. <br />
 * Store Requests (enum) for each subsystem and setpoints {@link Setpoints} <br />
 * Variables are public and have default values to prevent NullPointerExceptions
 * @author Nihar
 *
 */
public class Commands {

	private static Commands instance = new Commands();
	public static Commands getInstance() {
		return instance;
	}

	private Commands() {}

	public ArrayList<Routine> wantedRoutines = new ArrayList<Routine>();

	// Store WantedStates for each subsystem state machine
	public Drive.DriveState wantedDriveState = Drive.DriveState.NEUTRAL;

	public void addWantedRoutine(Routine wantedRoutine) {
		for(Routine routine : wantedRoutines) {
			if(routine.getClass().equals(wantedRoutine.getClass())) {
				Logger.getInstance().logRobotThread(Level.WARNING, "tried to add duplicate routine", routine.getName());
				return;
			}
		}
		wantedRoutines.add(wantedRoutine);
	}

	public static void reset() {
		instance = new Commands();
	}

	/**
	 * Stores numeric setpoints
	 * @author Nihar
	 */
	public static class Setpoints {
		public static final Optional<Double> NULLOPT = Optional.empty();
		
		public Optional<DriveSignal> drivePowerSetpoint = Optional.empty();

		/**
		 * Resets all the setpoints
		 */
		public void reset() {
			drivePowerSetpoint = Optional.empty();
		}
	}
	// All robot setpoints
	public Setpoints robotSetpoints = new Setpoints();
	
	// Allows you to cancel all running routines
	public boolean cancelCurrentRoutines = false;

	/**
	 * @return a copy of these commands
	 */
	public Commands copy() {
		Commands copy = new Commands();
		copy.wantedDriveState = this.wantedDriveState;

		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;

		for (Routine r : this.wantedRoutines) {
			copy.wantedRoutines.add(r);
		}
		
		// Copy robot setpoints
		copy.robotSetpoints = new Setpoints();
		// Copy optionals that are present
		robotSetpoints.drivePowerSetpoint.ifPresent((DriveSignal signal) -> copy.robotSetpoints.drivePowerSetpoint = Optional.of(signal));
		return copy;
	}

	@Override
	public String toString() {
		String log = "";
		String wantedRoutineName = "";
		for(Routine r : this.wantedRoutines) {
			wantedRoutineName += r.getName() + " ";
		}
		log+="Wanted Routines: "+wantedRoutineName+"\n";

		return log;
	}
}