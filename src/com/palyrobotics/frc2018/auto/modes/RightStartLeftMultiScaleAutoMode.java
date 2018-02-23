package com.palyrobotics.frc2018.auto.modes;

import com.palyrobotics.frc2018.auto.AutoModeBase;
import com.palyrobotics.frc2018.behavior.ParallelRoutine;
import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.behavior.SequentialRoutine;
import com.palyrobotics.frc2018.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2018.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2018.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2018.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2018.behavior.routines.intake.IntakeDownRoutine;
import com.palyrobotics.frc2018.behavior.routines.intake.IntakeOpenRoutine;
import com.palyrobotics.frc2018.config.AutoDistances;
import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.util.trajectory.Path;
import com.palyrobotics.frc2018.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

public class RightStartLeftMultiScaleAutoMode extends AutoModeBase {

    private Alliance mAlliance;

    public RightStartLeftMultiScaleAutoMode(Alliance alliance) {
        this.mAlliance = alliance;
    }

    @Override
    public String toString() {
        return "Right Start Left Multi Scale Auto Mode";
    }


    public Routine driveToScaleAndScore() {
        List<Path.Waypoint> path = new ArrayList<>();
        path.add(new Path.Waypoint(new Translation2d(0.0, 0.0), 72.0));
        if(mAlliance == Alliance.BLUE) {
            path.add(new Path.Waypoint(new Translation2d(AutoDistances.kBlueScaleSwitchMidlineX - Constants.kRobotLengthInches/2.0,
                    -Constants.kRobotWidthInches/2.0 - AutoDistances.kBlueRightCornerOffset + AutoDistances.kBlueRightSwitchY/2.0), 72.0));
            path.add(new Path.Waypoint(new Translation2d(AutoDistances.kBlueScaleSwitchMidlineX - Constants.kRobotLengthInches/2.0,
                    AutoDistances.kFieldWidth - Constants.kRobotWidthInches/2.0 - AutoDistances.kBlueRightCornerOffset
                            - AutoDistances.kBlueLeftScaleY - Constants.kPlateWidth/2.0), 72.0));
            path.add(new Path.Waypoint(new Translation2d(AutoDistances.kBlueLeftScaleX - Constants.kRobotLengthInches,
                    AutoDistances.kFieldWidth - Constants.kRobotWidthInches/2.0 - AutoDistances.kBlueRightCornerOffset
                            - AutoDistances.kBlueLeftScaleY - Constants.kPlateWidth/2.0), 0.0));
        } else {
            path.add(new Path.Waypoint(new Translation2d(AutoDistances.kRedScaleSwitchMidlineX - Constants.kRobotLengthInches/2.0,
                    -Constants.kRobotWidthInches/2.0 - AutoDistances.kRedRightCornerOffset + AutoDistances.kRedRightSwitchY/2.0), 72.0));
            path.add(new Path.Waypoint(new Translation2d(AutoDistances.kRedScaleSwitchMidlineX - Constants.kRobotLengthInches/2.0,
                    AutoDistances.kFieldWidth - Constants.kRobotWidthInches/2.0 - AutoDistances.kRedRightCornerOffset
                            - AutoDistances.kRedLeftScaleY - Constants.kPlateWidth/2.0), 72.0));
            path.add(new Path.Waypoint(new Translation2d(AutoDistances.kRedLeftScaleX - Constants.kRobotLengthInches,
                    AutoDistances.kFieldWidth - Constants.kRobotWidthInches/2.0 - AutoDistances.kRedRightCornerOffset
                            - AutoDistances.kRedLeftScaleY - Constants.kPlateWidth/2.0), 0.0));
        }

        ArrayList<Routine> routines = new ArrayList<Routine>();

        //Reset sensors before
        routines.add(new DriveSensorResetRoutine());

        //Drive path while moving elevator up and moving intake down
        ArrayList<Routine> inTransitRoutines = new ArrayList<>();
        inTransitRoutines.add(new DrivePathRoutine(new Path(path), false));
        inTransitRoutines.add(new SequentialRoutine(new ArrayList<Routine>() {{
            new TimeoutRoutine(3); // wait 3 seconds before lifting the elevator
            new ElevatorCustomPositioningRoutine(Constants.kElevatorTopBottomDifferenceInches, 15);
        }}));
        inTransitRoutines.add(new IntakeDownRoutine());
        routines.add(new ParallelRoutine(inTransitRoutines));

        //Open when everything is done to score
        routines.add(new IntakeOpenRoutine());

        return new SequentialRoutine(routines);
    }

    @Override
    public void prestart() {

    }

    public DrivePathRoutine getPathToCubeOrBack() {
        List<Path.Waypoint> path = new ArrayList<>();
        path.add(new Path.Waypoint(new Translation2d(0.0, 0.0), 72.0));
        path.add(new Path.Waypoint(new Translation2d(107,30),0));

        return new DrivePathRoutine(new Path(path), false);
    }


    @Override
    public Routine getRoutine() {
        ArrayList<Routine> routines = new ArrayList<>();

        routines.add(driveToScaleAndScore());
        //        routines.add(new CascadingTurnAngle(Math.PI));
        routines.add(new ParallelRoutine(new ArrayList<Routine>() {{
            getPathToCubeOrBack();
            new ElevatorCustomPositioningRoutine(Constants.kElevatorBottomPositionInches, 5);
        }}));
//        routines.add(new IntakeUntilHasCubeRoutine());
//        routines.add(new CascadingTurnAngle(Math.PI));
        routines.add(new ParallelRoutine(new ArrayList<Routine>() {{
            getPathToCubeOrBack();
            new ElevatorCustomPositioningRoutine(Constants.kElevatorTopBottomDifferenceInches, 5);
        }}));

        routines.add(new IntakeOpenRoutine());

        return new SequentialRoutine(routines);
    }
}