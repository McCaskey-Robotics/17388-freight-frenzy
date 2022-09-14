/*
Copyright 2020 FIRST Tech Challenge Team FTC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode;

import java.util.Locale;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import android.graphics.Color;
import android.app.Activity;
import android.view.View;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Remove a @Disabled the on the next line or two (if present) to add this opmode to the Driver Station OpMode list,
 * or add a @Disabled annotation to prevent this OpMode from being added to the Driver Station
 */

@Autonomous

public class AutonomousTest extends LinearOpMode { //public class extends

    ModernRoboticsI2cGyro   gyro    = null;                    // Additional Gyro device

    private DcMotor leftbackMotor;
    private DcMotor rightbackMotor;
    private DcMotor leftfrontMotor;
    private DcMotor rightfrontMotor;
    private DcMotor elevator;
    private DcMotor conveyor;
    private DcMotor intake;
    private DcMotor shooter;
    private Servo claw;
    private Servo shooterAngle;
    private Servo stopper;
    private CRServo flicker;
    private TouchSensor elevatorTopButton;
    private TouchSensor elevatorBottomButton;
    private TouchSensor magnetSensor;
    private TouchSensor magnetFlicker;
    private ColorSensor sensorColorIntake;
    private DistanceSensor sensorDistanceIntake;
    private DistanceSensor stackLaser;
    double speedMultiplier = 1; 
    boolean initialSetup = false;
    boolean shooterOn = false;
    int ringCount = 0;
    int conveyorEncoderSave = 0;
    int stackDistSum = 0;
    int stackDistAvg;
    char activeSquare;
    
    @Override
    public void runOpMode() { //run opmode
        leftbackMotor = hardwareMap.get(DcMotor.class, "backLeft");
        rightbackMotor = hardwareMap.get(DcMotor.class, "backRight");
        leftfrontMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        rightfrontMotor = hardwareMap.get(DcMotor.class, "frontRight");
        elevator = hardwareMap.get(DcMotor.class, "elevator");
        conveyor = hardwareMap.get(DcMotor.class, "conveyor");
        intake = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        claw = hardwareMap.get(Servo.class, "claw");
        shooterAngle = hardwareMap.get(Servo.class, "shooterAngle");
        stopper = hardwareMap.get(Servo.class, "stopper");
        flicker = hardwareMap.get(CRServo.class, "flicker");
        elevatorTopButton = hardwareMap.get(TouchSensor.class, "elevatorTopButton");
        elevatorBottomButton = hardwareMap.get(TouchSensor.class, "elevatorBottomButton");
        magnetSensor = hardwareMap.get(TouchSensor.class, "magnetSensor");
        magnetFlicker = hardwareMap.get(TouchSensor.class, "magnetSensor");
        sensorColorIntake = hardwareMap.get(ColorSensor.class, "sensorColorIntake");
        sensorDistanceIntake = hardwareMap.get(DistanceSensor.class, "sensorColorIntake");
        stackLaser = hardwareMap.get(DistanceSensor.class, "stackLaser");
        gyro = (ModernRoboticsI2cGyro)hardwareMap.gyroSensor.get("gyro");
        //gyro = hardwareMap.get(ModerRoboticsI2cGyro.class, "gyro");
        
        leftfrontMotor.setDirection(DcMotor.Direction.REVERSE);
        leftbackMotor.setDirection(DcMotor.Direction.REVERSE);
        elevator.setDirection(DcMotor.Direction.REVERSE);
        

        // Ensure the robot is stationary, then reset the encoders and calibrate the gyro.
        leftbackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightbackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftfrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftbackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Send telemetry message to alert driver that we are calibrating;
        telemetry.addData(">", "Calibrating Gyro");    //
        telemetry.update();

        gyro.calibrate();

    // make sure the gyro is calibrated before continuing
        while (!isStopRequested() && gyro.isCalibrating())  {
            sleep(50);
            idle();
        }

        
        telemetry.addData(">", "Robot Ready.");    //
        telemetry.update();

        leftbackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightbackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftfrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftbackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    // Wait for the game to start (Display Gyro value), and reset gyro before we move..
        while (!isStarted()) {
            telemetry.addData(">", "Robot Heading = %d", gyro.getIntegratedZValue());
            telemetry.update();
        }

        gyro.resetZAxisIntegrator();












        

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive())  //while opmode is active
            telemetry.addData("Status", "Running");
            telemetry.update();
            
            /**
             * ALL MEASUREMENTS RELATIVE TO THE FRONT RIGHT CORNER OF ROBOT ON THE PLEXIGLASS
             */
            
            //grab wobble goal, lift elevator 1 inch
            claw.setPosition(0);
            elevator.setPower(1);
            sleep(0);//one inch worth of time [FILL]
            elevator.setPower(0);
            
            
            //jolt forward and back quickly to shake the cradle down
            
            mecanumDrive(-1);
            mecanumDrive(1);
            
            //drive to rings 
            
            mecanumStrafe(0); //line up with rings
            mecanumDrive(0);//drive up to some point some distance from rings
            
            //detect rings (2m laser) and set acTiveSquare variable
            
            leftfrontMotor.setPower(-0.33);
            leftbackMotor.setPower(-0.33);
            rightfrontMotor.setPower(-0.33);
            rightbackMotor.setPower(-0.33);
            for(int i = 0; i < 40; i++){ //record measurements 40 times over 2 seconds
                stackDistSum += stackLaser.getDistance(DistanceUnit.INCH);
                sleep(50);
            }
            stackDistAvg = stackDistSum/40;//calculate avg. distance
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            //Telemetry.addData("Avg. Distance", stackLaser.getDistance(DistanceUnit.INCH));
            
            if(stackDistAvg>0)//guess number of rings in stack based on avg. distance from sensor
            {
                activeSquare = 'a';
            }
            else if(stackDistAvg>0)
            {
                activeSquare = 'b';
            }
            else
            {
                activeSquare = 'c';
            }
            
            //drive to predetermined point x, located between squares a and b and on the white line
            
            mecanumStrafe(0); 
            mecanumDrive(0); 
            
            
            //drive to the currently active square, stored in activeSquare
            //if()
            //drop off wobble goal
            if(activeSquare == 'a'){
                mecanumStrafe(0);
                mecanumDrive(0);
            }
            else if(activeSquare == 'b'){
                mecanumStrafe(0);
                mecanumDrive(0);
            }
            else{
                mecanumStrafe(0);
                mecanumDrive(0);
            }
            
            claw.setPosition(0.57);
            
            //drive back to point x 
            
            mecanumDrive(0);
            mecanumStrafe(0);
            
            
            //shoot rings at power shot targets
            
            mecanumStrafe(0);
            mecanumDrive(0); //get to firing position
            
            mecanumTurn(0); //align with powershot 1
            //shoot
            mecanumTurn(0); //align with powershot 2
            //shoot
            mecanumTurn(0); //align with powershot 3
            //shoot
            
            
            //drive to start 
            mecanumTurn(0);//realign with wall
            mecanumStrafe(0);
            mecanumDrive(0);
            
            
            //pick up 2nd wobble goal
                
            while(!(elevatorTopButton.isPressed()))//bottom out elevator
            {
                elevator.setPower(-1);
            }
            elevator.setPower(0);
            claw.setPosition(0);

            elevator.setPower(1);
            sleep(200); //inch amount of time [FILL]
            elevator.setPower(0);
            
            
            //drive to x 
            mecanumStrafe(0);
            mecanumDrive(0);
            
            //drive to currently active square
            if(activeSquare == 'a')
            {
                mecanumStrafe(0);
                mecanumDrive(0);
            }
            else if(activeSquare == 'b')
            {
                mecanumStrafe(0);
                mecanumDrive(0);
            }
            else
            {
                mecanumStrafe(0);
                mecanumDrive(0);
            }
            
            //drop off wobble goal
            claw.setPosition(0.57);
            
            //drive to point x/finish line 
            mecanumStrafe(0);
            mecanumDrive(0);
            
            //stop
            //while(true){};
            
        } //while opmode is active
     //run opmode
    





//move forward or backward a certain number of inches
    public void mecanumDrive(double inches){
        double COUNTS_PER_INCH = 31.1;
        double methodMotorPower=1.0; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int moveCounts;
        int rightCurrentEncoder = rightfrontMotor.getCurrentPosition();
        int reducingSpeedInches = 6;
        moveCounts = (int)(inches * COUNTS_PER_INCH);

        if(inches > 0){
        while (opModeIsActive() && rightfrontMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts-reducingSpeedInches*COUNTS_PER_INCH)) {
            leftfrontMotor.setPower(methodMotorPower);
            rightfrontMotor.setPower(methodMotorPower);
            leftbackMotor.setPower(methodMotorPower);
            rightbackMotor.setPower(methodMotorPower);
        }
        while (opModeIsActive() && rightfrontMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts)) {
            currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-(rightfrontMotor.getCurrentPosition()-rightCurrentEncoder))/(reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
            leftfrontMotor.setPower(currentMotorPower);
            rightfrontMotor.setPower(currentMotorPower);
            leftbackMotor.setPower(currentMotorPower);
            rightbackMotor.setPower(currentMotorPower);
        }
        }
        else{
            while (opModeIsActive() && rightfrontMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts+reducingSpeedInches*COUNTS_PER_INCH)) {
                leftfrontMotor.setPower(-methodMotorPower);
                rightfrontMotor.setPower(-methodMotorPower);
                leftbackMotor.setPower(-methodMotorPower);
                rightbackMotor.setPower(-methodMotorPower);
            }
            while (opModeIsActive() && rightfrontMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-(rightfrontMotor.getCurrentPosition()-rightCurrentEncoder))/(-reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
                leftfrontMotor.setPower(-currentMotorPower);
                rightfrontMotor.setPower(-currentMotorPower);
                leftbackMotor.setPower(-currentMotorPower);
                rightbackMotor.setPower(-currentMotorPower);
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }




//Strafe leftward or rightward a certain number of inches
    public void mecanumStrafe(double inches){
        double COUNTS_PER_INCH = 31.1;
        double methodMotorPower=1.0; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int moveCounts;
        int rightCurrentEncoder = rightfrontMotor.getCurrentPosition();
        int reducingSpeedInches = 6;
        moveCounts = (int)(inches * COUNTS_PER_INCH);

        if(inches > 0){
            while (opModeIsActive() && rightfrontMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts-reducingSpeedInches*COUNTS_PER_INCH)) {
                leftfrontMotor.setPower(methodMotorPower);
                rightfrontMotor.setPower(-methodMotorPower);
                leftbackMotor.setPower(-methodMotorPower);
                rightbackMotor.setPower(methodMotorPower);
            }
            while (opModeIsActive() && rightfrontMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-(rightfrontMotor.getCurrentPosition()-rightCurrentEncoder))/(reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
                leftfrontMotor.setPower(currentMotorPower);
                rightfrontMotor.setPower(-currentMotorPower);
                leftbackMotor.setPower(-currentMotorPower);
                rightbackMotor.setPower(currentMotorPower);
            }
        }
        else{
            while (opModeIsActive() && rightfrontMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts+reducingSpeedInches*COUNTS_PER_INCH)) {
                leftfrontMotor.setPower(-methodMotorPower);
                rightfrontMotor.setPower(methodMotorPower);
                leftbackMotor.setPower(methodMotorPower);
                rightbackMotor.setPower(-methodMotorPower);
            }
            while (opModeIsActive() && rightfrontMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-(rightfrontMotor.getCurrentPosition()-rightCurrentEncoder))/(-reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
                leftfrontMotor.setPower(-currentMotorPower);
                rightfrontMotor.setPower(currentMotorPower);
                leftbackMotor.setPower(currentMotorPower);
                rightbackMotor.setPower(-currentMotorPower);
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }






    //rotate to a certain heading
    public void mecanumTurn(int degrees){
        double methodMotorPower=1.0; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int reducingSpeedDegrees = 15;
        
        if(degrees > gyro.getIntegratedZValue()){
            while (opModeIsActive() && gyro.getIntegratedZValue() <= (degrees-reducingSpeedDegrees)) {
                leftfrontMotor.setPower(methodMotorPower);
                rightfrontMotor.setPower(-methodMotorPower);
                leftbackMotor.setPower(methodMotorPower);
                rightbackMotor.setPower(-methodMotorPower);
            }
            while (opModeIsActive() && gyro.getIntegratedZValue() <= (degrees)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((degrees-gyro.getIntegratedZValue())/(reducingSpeedDegrees),1.1)+0.05;
                leftfrontMotor.setPower(currentMotorPower);
                rightfrontMotor.setPower(-currentMotorPower);
                leftbackMotor.setPower(currentMotorPower);
                rightbackMotor.setPower(-currentMotorPower);
            }
        }
        else{
            while (opModeIsActive() && gyro.getIntegratedZValue() >= (degrees+reducingSpeedDegrees)) {
            leftfrontMotor.setPower(-methodMotorPower);
            rightfrontMotor.setPower(methodMotorPower);
            leftbackMotor.setPower(-methodMotorPower);
            rightbackMotor.setPower(methodMotorPower);
            }
            while (opModeIsActive() && gyro.getIntegratedZValue() >= (degrees)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((degrees-gyro.getIntegratedZValue())/(-reducingSpeedDegrees),1.1)+0.05;
                leftfrontMotor.setPower(-currentMotorPower);
                rightfrontMotor.setPower(currentMotorPower);
                leftbackMotor.setPower(-currentMotorPower);
                rightbackMotor.setPower(currentMotorPower);
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }





}//public class extends
