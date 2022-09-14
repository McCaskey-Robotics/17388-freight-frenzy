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

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;






import java.util.Locale;
import com.qualcomm.robotcore.hardware.GyroSensor;
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

public class AutonomousQuick_v2 extends LinearOpMode { //public class extends

    

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
    private DistanceSensor wallDistance;
    double speedMultiplier = 1; 
    boolean initialSetup = false;
    boolean shooterOn = false;
    boolean measured = false;
    int ringCount = 0;
    int conveyorEncoderSave = 0;
    double stackDistSum = 0;
    double stackDistAvg =0;
    double stackDistSum2 = 0;
    double stackDistAvg2 =0;
    char activeSquare;
    
    
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    // The IMU sensor object
    BNO055IMU imu;

    // State used for updating telemetry
    Orientation angles;
    Acceleration gravity;
    //Position position;

    //----------------------------------------------------------------------------------------------
    // Main logic
    //----------------------------------------------------------------------------------------------

    
    
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
        magnetFlicker = hardwareMap.get(TouchSensor.class, "magnetFlicker");
        sensorColorIntake = hardwareMap.get(ColorSensor.class, "sensorColorIntake");
        sensorDistanceIntake = hardwareMap.get(DistanceSensor.class, "sensorColorIntake");
        stackLaser = hardwareMap.get(DistanceSensor.class, "stackLaser");
        wallDistance = hardwareMap.get(DistanceSensor.class, "wallDistance");
        
        
        conveyor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        leftfrontMotor.setDirection(DcMotor.Direction.REVERSE);
        leftbackMotor.setDirection(DcMotor.Direction.REVERSE);
        elevator.setDirection(DcMotor.Direction.REVERSE);
        
        
        
        // Ensure the robot is stationary, then reset the encoders and calibrate the gyro.
        leftbackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightbackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftfrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftbackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        sleep(100);
        
        leftbackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightbackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftfrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftbackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        

        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        // Set up our telemetry dashboard
        composeTelemetry();



        // Wait for the game to start (driver presses PLAY)
        waitForStart();


        // Start the logging of measured acceleration
        imu.startAccelerationIntegration(new Position(), new Velocity(), 100);
     

 
            telemetry.addData("Status", "Running");
            telemetry.update();
            
        
  /*
            
            ringCount = 3;
            conveyorEncoderSave = conveyor.getCurrentPosition()-150;
            shooterAngle.setPosition(0.83);
            mecanumDrive(40);
            mecanumTurn(90);
            shooter.setPower(1.0);
            mecanumDrive(24.5);
            mecanumTurn(-3);
            mecanumDrive(11.5);
            fireRing();
            mecanumStrafe(7.5);
            mecanumTurn(-3);
            fireRing();
            mecanumStrafe(7.5);
            mecanumTurn(-3);
            fireRing();
            mecanumDrive(16);
            */
            
            /*double power =0.15;
            leftfrontMotor.setPower(power);
                rightfrontMotor.setPower(power);
                leftbackMotor.setPower(power);
                rightbackMotor.setPower(power);
                sleep(7000);
                leftfrontMotor.setPower(0);
                rightfrontMotor.setPower(0);
                leftbackMotor.setPower(0);
                rightbackMotor.setPower(0);
                */
           
            /**
             * ALL MEASUREMENTS RELATIVE TO THE FRONT RIGHT CORNER OF ROBOT ON THE PLEXIGLASS
             */
//------------------------------------------------------------------------------------------------------
//THE PART WHERE IT ACTUALY RUNS
//------------------------------------------------------------------------------------------------------
            //innitialization of variables and claw
            ringCount = 3;
            conveyorEncoderSave = conveyor.getCurrentPosition()-150;
            claw.setPosition(1);
            
            
            
            
            /*telemetry.addData("Motor:", "left Front Motor");
            telemetry.update();
            leftfrontMotor.setPower(1);
            sleep(3000);
            leftfrontMotor.setPower(0);
            
            
            /*telemetry.addData("Motor:", "left Back Motor");
            telemetry.update();
            leftbackMotor.setPower(1);
            sleep(3000);
            leftbackMotor.setPower(0);
            
            telemetry.addData("Motor:", "Right Front Motor");
            telemetry.update();
            rightfrontMotor.setPower(1);
            sleep(3000);
            rightfrontMotor.setPower(0);
            
            telemetry.addData("Motor:", "Right Back Motor");
            telemetry.update();
            rightbackMotor.setPower(1);
            sleep(3000);
            rightbackMotor.setPower(0);
            
            sleep(30000);
            */
            
            //get wobble goal down
            shooter.setPower(1);
            sleep(500);
            mecanumDrive(-22,0);
            sleep(500);
            shooter.setPower(0);
            
            //lift elevator
            elevator.setPower(-1);
            sleep(750);//one inch worth of time [FILL]
            elevator.setPower(0);
            
            sleep(500);
            
            
            
            //drive to rings
            //mecanumDrive(-8,0);
            shooterAngle.setPosition(0.5);
            //mecanumStrafe(-9);

            //mecanumDrive(-8,0);
            
            //slowly drive for scanning
            leftfrontMotor.setPower(-0.5);
            leftbackMotor.setPower(-0.5);
            rightfrontMotor.setPower(-0.5);
            rightbackMotor.setPower(-0.5);
            
                
            //scan distance to ring by taking an avg of the 2m laser sensor
            for(int i = 0; i < 13; i++){ //record measurements 40 times over 2 seconds
                
                   stackDistSum += stackLaser.getDistance(DistanceUnit.INCH);
                   sleep(2);
                }
            
            /*
            //calibrate distance sensor for flat ground
            double innitStackDistSum = 0;
            for(int i = 0; i < 5; i++){ 
                innitStackDistSum += stackLaser.getDistance(DistanceUnit.INCH);
                sleep(35);
            }
            double innitStackDistAvg = innitStackDistSum/5;//calculate avg. distance
            */
            
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            
            stackDistAvg = stackDistSum/13;//calculate avg. distance
            telemetry.addData("Avg. Distance: ", stackDistAvg);
            telemetry.update();
            
            
            
            //telemetry.addData("Avg. Flat Ground Distance: ", innitStackDistAvg);
            //telemetry.update();
             
            //
            //Arena:
            //[c]     4 rings
            //  [b]   1 ring
            //[a]     0 rings
            
            if(stackDistAvg<7.5)
            {
                activeSquare = 'c';
            }
            else if(stackDistAvg<9.81 && stackDistAvg>=7.5)
            {
                activeSquare = 'b';
            }
            else if(stackDistAvg>=9.81)
            {
                activeSquare = 'a';
            }
            
            
            
            
            //Pre-adjustment:
            //0 rings gives readings of: 30.5
            //Post-adjustment:
            //0 rings gives readings of: 31.3
            
            //MEASUREMENTS UNTESTED
            //guess number of rings in stack based on avg. distance from sensor
            /*
            if(stackDistAvg>=innitStackDistAvg-0.8)
            {
                activeSquare = 'a';
            }
            else if(stackDistAvg>innitStackDistAvg-2.3 && stackDistAvg<innitStackDistAvg-0.8)
            {
                activeSquare = 'b';
            }
            else if(stackDistAvg<=innitStackDistAvg-2.3)
            {
                activeSquare = 'c';
            }*/
            
            
            //telemetry.addData("Avg. Distances: ", "" + stackDistAvg +" vs flat dist of: "+innitStackDistAvg);
            telemetry.addData("Active Square: ", activeSquare);
            telemetry.addData("Avg. Distance: ", stackDistAvg);
            telemetry.update();
            
            /*
            while(!(elevatorBottomButton.isPressed()))
                {
                elevator.setPower(0.5);
                }
                elevator.setPower(0);
            sleep(30000);
            */

            
            //drive to predetermined point x, located between squares a and b and on the white line
            //mecanumTurn(0);
            mecanumStrafe(7); 
            mecanumDrive(-15,0); 
            //mecanumTurn(5);
            
            
            
            //
            //Arena:
            //[c]     4 rings
            //  [b]   1 ring
            //[a]     0 rings
            //
            //drive to the currently active square, stored in activeSquare
            
            

            //activeSquare = 'a'; //Remove this once ready for extra squares
            
            if(activeSquare == 'a'){
                //drop off wobble goal
                sleep(200);
                claw.setPosition(0.4);
                while(!(elevatorBottomButton.isPressed()))
                {
                elevator.setPower(0.5);
                }
                elevator.setPower(0);
                sleep(200);
                
                //drive to shooting position
                mecanumDrive(20,0);
                sleep(500);
                mecanumStrafe(-6);
            }
            else if(activeSquare == 'b'){
                //drop off wobble goal
                mecanumStrafe(-31);
                mecanumDrive(-19,0);
                sleep(200);
                claw.setPosition(0.4);
                while(!(elevatorBottomButton.isPressed()))
                {
                elevator.setPower(0.5);
                }
                elevator.setPower(0);
                sleep(200);
                
                //drive to shooting position
                mecanumDrive(39,0);
                sleep(500);
                mecanumStrafe(16);
                
            }
            else{
                //drop off wobble goal
                mecanumDrive(-54,0);
                sleep(200);
                claw.setPosition(0.4);
                while(!(elevatorBottomButton.isPressed()))
                {
                elevator.setPower(0.5);
                }
                elevator.setPower(0);
                sleep(200);
                
                //drive to shooting position
                mecanumDrive(75,0);
                sleep(500);
                mecanumStrafe(-6);
            }
            
            
            shooter.setPower(0.9);
            
            //line up with power shots
            mecanumTurn(160);
            /*if(wallDistance.getDistance(DistanceUnit.INCH)<50){
            while(wallDistance.getDistance(DistanceUnit.INCH)<36.3){
                leftfrontMotor.setPower(0.5);
                rightfrontMotor.setPower(-0.5);
                leftbackMotor.setPower(-0.5);
                rightbackMotor.setPower(0.5);
            }
            while(wallDistance.getDistance(DistanceUnit.INCH)>36.7){
                leftfrontMotor.setPower(-0.5);
                rightfrontMotor.setPower(0.5);
                leftbackMotor.setPower(0.5);
                rightbackMotor.setPower(-0.5);
            }
            }*/
            
            //drive to line
            mecanumDrive(12,165);
            
            
            //shoot rings at power shot targets
            
            
            //align with powershot 1
            fireRing();
            sleep(200);
            //align with powershot 2
            fireRing();
            sleep(200);
            //align with powershot 3
            fireRing();
            sleep(200);
            
            
            //drive to start 
            mecanumDrive(10,180);
            
            /*
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
            
        
        
        telemetry.update();
        sleep(10000);
        /**/
    }//run opmode
    




    //----------------------------------------------------------------------------------------------
    // Telemetry Configuration
    //----------------------------------------------------------------------------------------------

    void composeTelemetry() {

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() { @Override public void run()
                {
                // Acquiring the angles is relatively expensive; we don't want
                // to do that in each of the three items that need that info, as that's
                // three times the necessary expense.
                angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                gravity  = imu.getGravity();
                //position = imu.getPosition();
                }
            });

        telemetry.addLine()
            .addData("status", new Func<String>() {
                @Override public String value() {
                    return imu.getSystemStatus().toShortString();
                    }
                })
            .addData("calib", new Func<String>() {
                @Override public String value() {
                    return imu.getCalibrationStatus().toString();
                    }
                });

        telemetry.addLine()
            .addData("heading", new Func<String>() {
                @Override public String value() {
                    return formatAngle(angles.angleUnit, angles.firstAngle);
                    }
                })
            .addData("roll", new Func<String>() {
                @Override public String value() {
                    return formatAngle(angles.angleUnit, angles.secondAngle);
                    }
                })
            .addData("pitch", new Func<String>() {
                @Override public String value() {
                    return formatAngle(angles.angleUnit, angles.thirdAngle);
                    }
                });

        /*telemetry.addLine()
            .addData("position", new Func<String>() {
                @Override public String value() {
                    return position.toString();
                    }
                });*/

        telemetry.addLine()
            .addData("grvty", new Func<String>() {
                @Override public String value() {
                    return gravity.toString();
                    }
                })
            .addData("mag", new Func<String>() {
                @Override public String value() {
                    return String.format(Locale.getDefault(), "%.3f",
                            Math.sqrt(gravity.xAccel*gravity.xAccel
                                    + gravity.yAccel*gravity.yAccel
                                    + gravity.zAccel*gravity.zAccel));
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    // Formatting
    //----------------------------------------------------------------------------------------------

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }










//move forward or backward a certain number of inches
//Use an input of 200 to move in the existing direction
    public void mecanumDrive(double inches, double inputHeading){
        double COUNTS_PER_INCH = 9.1954;
        double methodMotorPower=1.0; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int moveCounts;
        int rightCurrentEncoder = rightbackMotor.getCurrentPosition();
        int reducingSpeedInches = 12;
        moveCounts = (int)(inches * COUNTS_PER_INCH);
        double minPower = 0.25;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking
        double recordedHeading = inputHeading + 180;
        if(inputHeading == 200){
                        recordedHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
        }

        if(inches > 0){
        while (opModeIsActive() && rightbackMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts-reducingSpeedInches*COUNTS_PER_INCH)) {
            sleep(100);
            double currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
            if(recordedHeading-currentHeading>180){//account for discontinuity between -180 and 180
                currentHeading=1000;
            }
            else if(currentHeading-recordedHeading>180){
                currentHeading=currentHeading*-1;
            }
            if(currentHeading<recordedHeading){//adjust for drift by turning a bit leftward
                leftfrontMotor.setPower(0.8*(methodMotorPower+minPower));
                rightfrontMotor.setPower(methodMotorPower+minPower);
                leftbackMotor.setPower(0.8*(methodMotorPower+minPower));
                rightbackMotor.setPower(methodMotorPower+minPower);
            }
            else if(currentHeading>recordedHeading){//adjust for drift by turning a bit rightward
                leftfrontMotor.setPower(methodMotorPower+minPower);
                rightfrontMotor.setPower(0.8*(methodMotorPower+minPower));
                leftbackMotor.setPower(methodMotorPower+minPower);
                rightbackMotor.setPower(0.8*(methodMotorPower+minPower));
            }
            else{//if no issues exist then stay the course
                leftfrontMotor.setPower(methodMotorPower+minPower);
                rightfrontMotor.setPower(methodMotorPower+minPower);
                leftbackMotor.setPower(methodMotorPower+minPower);
                rightbackMotor.setPower(methodMotorPower+minPower);
            }
        }
        while (opModeIsActive() && rightbackMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts)) {
            currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-((rightbackMotor.getCurrentPosition())-rightCurrentEncoder))/(reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
            sleep(100);
            double currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
            if(recordedHeading-currentHeading>180){//account for discontinuity between -180 and 180
                currentHeading=1000;
            }
            else if(currentHeading-recordedHeading>180){
                currentHeading=currentHeading*-1;
            }
            if(currentHeading<recordedHeading){
                leftfrontMotor.setPower(0.8*(currentMotorPower+minPower));
                rightfrontMotor.setPower(currentMotorPower+minPower);
                leftbackMotor.setPower(0.8*(currentMotorPower+minPower));
                rightbackMotor.setPower(currentMotorPower+minPower);
            }
            else if(currentHeading>recordedHeading){
                leftfrontMotor.setPower(currentMotorPower+minPower);
                rightfrontMotor.setPower(0.8*(currentMotorPower+minPower));
                leftbackMotor.setPower(currentMotorPower+minPower);
                rightbackMotor.setPower(0.8*(currentMotorPower+minPower));
            }
            else{
                leftfrontMotor.setPower(currentMotorPower+minPower);
                rightfrontMotor.setPower(currentMotorPower+minPower);
                leftbackMotor.setPower(currentMotorPower+minPower);
                rightbackMotor.setPower(currentMotorPower+minPower);
            }
        }
        }
        else{
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts+reducingSpeedInches*COUNTS_PER_INCH)) {
                sleep(100);
                double currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
                if(recordedHeading-currentHeading>180){//account for discontinuity between -180 and 180
                    currentHeading=1000;
                }
                else if(currentHeading-recordedHeading>180){
                    currentHeading=currentHeading*-1;
                }
                if(currentHeading>recordedHeading){
                    leftfrontMotor.setPower(-0.8*(methodMotorPower+minPower));
                    rightfrontMotor.setPower(-methodMotorPower-minPower);
                    leftbackMotor.setPower(-0.8*(methodMotorPower+minPower));
                    rightbackMotor.setPower(-methodMotorPower-minPower);
                }
                else if(currentHeading<recordedHeading){
                    leftfrontMotor.setPower(-methodMotorPower-minPower);
                    rightfrontMotor.setPower(-0.8*(methodMotorPower+minPower));
                    leftbackMotor.setPower(-methodMotorPower-minPower);
                    rightbackMotor.setPower(-0.8*(methodMotorPower+minPower));
                }
                else{
                    leftfrontMotor.setPower(-methodMotorPower-minPower);
                    rightfrontMotor.setPower(-methodMotorPower-minPower);
                    leftbackMotor.setPower(-methodMotorPower-minPower);
                    rightbackMotor.setPower(-methodMotorPower-minPower);
                }
            }
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-(rightbackMotor.getCurrentPosition()-rightCurrentEncoder))/(-reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
            sleep(100);
            double currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
            if(recordedHeading-currentHeading>180){//account for discontinuity between -180 and 180
                currentHeading=1000;
            }
            else if(currentHeading-recordedHeading>180){
                currentHeading=currentHeading*-1;
            }
            if(currentHeading>recordedHeading){
                leftfrontMotor.setPower(-0.8*(currentMotorPower+minPower));
                rightfrontMotor.setPower(-currentMotorPower-minPower);
                leftbackMotor.setPower(-0.8*(currentMotorPower+minPower));
                rightbackMotor.setPower(-currentMotorPower-minPower);
            }
            else if(currentHeading<recordedHeading){
                leftfrontMotor.setPower(-currentMotorPower-minPower);
                rightfrontMotor.setPower(-0.8*(currentMotorPower+minPower));
                leftbackMotor.setPower(-currentMotorPower-minPower);
                rightbackMotor.setPower(-0.8*(currentMotorPower+minPower));
            }
            else{
                leftfrontMotor.setPower(-currentMotorPower-minPower);
                rightfrontMotor.setPower(-currentMotorPower-minPower);
                leftbackMotor.setPower(-currentMotorPower-minPower);
                rightbackMotor.setPower(-currentMotorPower-minPower);
            }
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }




//Strafe leftward or rightward a certain number of inches
    public void mecanumStrafe(double inches){
        double COUNTS_PER_INCH = 13.7245;
        double methodMotorPower=1.0; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int moveCounts;
        int rightCurrentEncoder = rightbackMotor.getCurrentPosition();
        int reducingSpeedInches = 9;
        moveCounts = (int)(inches * COUNTS_PER_INCH);
        double minPower = 0.25;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking

        if(inches > 0){
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts-reducingSpeedInches*COUNTS_PER_INCH)) {
                leftfrontMotor.setPower(methodMotorPower+minPower);
                rightfrontMotor.setPower(-methodMotorPower-minPower);
                leftbackMotor.setPower(-methodMotorPower-minPower);
                rightbackMotor.setPower(methodMotorPower+minPower);
            }
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-((rightbackMotor.getCurrentPosition())-rightCurrentEncoder))/(reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
                leftfrontMotor.setPower(currentMotorPower+minPower);
                rightfrontMotor.setPower(-currentMotorPower-minPower);
                leftbackMotor.setPower(-currentMotorPower-minPower);
                rightbackMotor.setPower(currentMotorPower+minPower);
            }
        }
        else{
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts+reducingSpeedInches*COUNTS_PER_INCH)) {
                leftfrontMotor.setPower(-methodMotorPower-minPower);
                rightfrontMotor.setPower(methodMotorPower+minPower);
                leftbackMotor.setPower(methodMotorPower+minPower);
                rightbackMotor.setPower(-methodMotorPower-minPower);
            }
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-((rightbackMotor.getCurrentPosition())-rightCurrentEncoder))/(-reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
                leftfrontMotor.setPower(-currentMotorPower-minPower);
                rightfrontMotor.setPower(currentMotorPower+minPower);
                leftbackMotor.setPower(currentMotorPower+minPower);
                rightbackMotor.setPower(-currentMotorPower-minPower);
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }






    //rotate to a certain heading between -180 and 180 degrees
    public void mecanumTurn(int degrees){
        double methodMotorPower=0.8; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int reducingSpeedDegrees = 55;
        double minPower = 0.25;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking
        
        if(degrees > imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1){
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 < (degrees-reducingSpeedDegrees)) {
                leftfrontMotor.setPower(methodMotorPower+minPower);
                rightfrontMotor.setPower(-methodMotorPower-minPower);
                leftbackMotor.setPower(methodMotorPower+minPower);
                rightbackMotor.setPower(-methodMotorPower-minPower);
            }
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 < (degrees) && Math.abs(degrees - imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1) < 340) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((degrees-imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1)/(reducingSpeedDegrees),1.1)+0.05;
                leftfrontMotor.setPower(currentMotorPower+minPower);
                rightfrontMotor.setPower(-currentMotorPower-minPower);
                leftbackMotor.setPower(currentMotorPower+minPower);
                rightbackMotor.setPower(-currentMotorPower-minPower);
            }
        }
        else{
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 > (degrees+reducingSpeedDegrees)) {
            leftfrontMotor.setPower(-methodMotorPower-minPower);
            rightfrontMotor.setPower(methodMotorPower+minPower);
            leftbackMotor.setPower(-methodMotorPower-minPower);
            rightbackMotor.setPower(methodMotorPower+minPower);
            }
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 > (degrees) && Math.abs(degrees - imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1) < 340) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((degrees-imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1)/(-reducingSpeedDegrees),1.1)+0.05;
                leftfrontMotor.setPower(-currentMotorPower-minPower);
                rightfrontMotor.setPower(currentMotorPower+minPower);
                leftbackMotor.setPower(-currentMotorPower-minPower);
                rightbackMotor.setPower(currentMotorPower+minPower);
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }




    //fire a ring - NOTE: make sure you set the shooter power on prior to using this
    public void fireRing(){
        conveyor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        if(shooter.getPower() > 0.5 && (ringCount > 0)){
            
                intake.setPower(0);
                stopper.setPosition(0.4);
                sleep(200);
                flicker.setPower(1);
                while(magnetFlicker.isPressed() == false){}
                flicker.setPower(0);
                //while(magnetFlicker.isPressed() == false){}
                //sleep(600);
                //sleep(380);
                //flicker.setPower(0);
                //sleep(200);
                
                stopper.setPosition(0.9);
                //sleep(0);
                
                if(ringCount == 3){
                    while(conveyor.getCurrentPosition() < conveyorEncoderSave + 215){
                        conveyor.setPower(1);
                    }
                    //flicker.setPower(1);
                    flicker.setPower(1);
                    
                    
                
                    
                    while(magnetSensor.isPressed() == false){
                        conveyor.setPower(1);    
                    }//magSwitchA isnt on
                    conveyor.setPower(0);
                    while(magnetFlicker.isPressed() == true){}
                    flicker.setPower(0);
                    //sleep(450);
                    //flicker.setPower(0);
                        conveyorEncoderSave = conveyor.getCurrentPosition();
                        while(conveyor.getCurrentPosition() < conveyorEncoderSave + 38){
                        conveyor.setPower(0.3);
                    }//getting peg to the ring
                    conveyor.setPower(0);
                    while(conveyor.getCurrentPosition() < conveyorEncoderSave + 50){
                        intake.setPower(-1);
                        sleep(50);
                        conveyor.setPower(0.3);
                    }//getting ring out of intake
                    
                    intake.setPower(0); 
                
                while(conveyor.getCurrentPosition() < conveyorEncoderSave + 150){
                    conveyor.setPower(1);
                }//to halfway through conveyor channel
                
                conveyor.setPower(0);
                }
                else if(ringCount == 2){
                    while(conveyor.getCurrentPosition() < conveyorEncoderSave + 215){
                        conveyor.setPower(1);
                    }
                    //flicker.setPower(1);
                    flicker.setPower(1);
                    
                    
                
                    
                    while(magnetSensor.isPressed() == false){
                        conveyor.setPower(1);    
                    }//magSwitchA isnt on
                    conveyor.setPower(0);
                    while(magnetFlicker.isPressed() == true){}
                    flicker.setPower(0);
                    //sleep(450);
                    //flicker.setPower(0);
                }
                else{
                    
                    
                //    while(magnetFlicker.isPressed() == true){
                //    flicker.setPower(1);
                //}
                //flicker.setPower(1);
                //while(magnetFlicker.isPressed() == false){}
                //sleep(450);
                //flicker.setPower(0);
                }
                intake.setPower(-0.2);
                //flicker.setPosition(0);
                //if(conveyor.getCurrentPosition() < conveyorEncoderSave + 265){}
                
                ringCount -= 1;
                
             }//shooting the rings */
    }
    



}//public class extends
