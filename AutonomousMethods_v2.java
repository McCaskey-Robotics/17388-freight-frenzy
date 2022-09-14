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

public class AutonomousMethods_v2 extends LinearOpMode { //public class extends

    

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
        magnetFlicker = hardwareMap.get(TouchSensor.class, "magnetSensor");
        sensorColorIntake = hardwareMap.get(ColorSensor.class, "sensorColorIntake");
        sensorDistanceIntake = hardwareMap.get(DistanceSensor.class, "sensorColorIntake");
        stackLaser = hardwareMap.get(DistanceSensor.class, "stackLaser");
        
        
        rightfrontMotor.setDirection(DcMotor.Direction.REVERSE);
        rightbackMotor.setDirection(DcMotor.Direction.REVERSE);
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
            
          telemetry.addData("Position ", imu.getPosition());  
          telemetry.addData("Heading ", imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle); 
           telemetry.addData("Front-right", rightfrontMotor.getCurrentPosition());
        telemetry.addData("Front-left", leftfrontMotor.getCurrentPosition());
        telemetry.addData("Back-right", rightbackMotor.getCurrentPosition());
        telemetry.addData("Back-left", leftbackMotor.getCurrentPosition());
      
        telemetry.update();
        
/*
            
            leftfrontMotor.setPower(1);
                rightfrontMotor.setPower(1);
                leftbackMotor.setPower(1);
                rightbackMotor.setPower(1);
                int counter = 0;
            while(counter < 100){
                
                sleep(10);
                
                telemetry.addData("Position ", imu.getPosition());
                telemetry.addData("Heading ", imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle);
                telemetry.addData("Front-right", rightfrontMotor.getCurrentPosition());
        telemetry.addData("Front-left", leftfrontMotor.getCurrentPosition());
        telemetry.addData("Back-right", rightbackMotor.getCurrentPosition());
        telemetry.addData("Back-left", leftbackMotor.getCurrentPosition());
        
        telemetry.update();
        counter++;
            }
            leftfrontMotor.setPower(0);
                rightfrontMotor.setPower(0);
                leftbackMotor.setPower(0);
                rightbackMotor.setPower(0);
            */
            
            mecanumDrive(50.25);
            mecanumTurn(90);
            mecanumDrive(21.5);
            mecanumTurn(0);
            mecanumStrafe(7.5);
            sleep(1000);
            mecanumStrafe(7.5);
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
               
            
        
        
        
        telemetry.update();
        sleep(10000);
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
    public void mecanumDrive(double inches){
        double COUNTS_PER_INCH = 29.75;
        double methodMotorPower=1.0; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int moveCounts;
        int rightCurrentEncoder = rightbackMotor.getCurrentPosition();
        int reducingSpeedInches = 6;
        moveCounts = (int)(inches * COUNTS_PER_INCH);
        double minPower = 0.15;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking

        if(inches > 0){
        while (opModeIsActive() && rightbackMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts-reducingSpeedInches*COUNTS_PER_INCH)) {
            leftfrontMotor.setPower(methodMotorPower+minPower);
            rightfrontMotor.setPower(methodMotorPower+minPower);
            leftbackMotor.setPower(methodMotorPower+minPower);
            rightbackMotor.setPower(methodMotorPower+minPower);
        }
        while (opModeIsActive() && rightbackMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts)) {
            currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-((rightbackMotor.getCurrentPosition())-rightCurrentEncoder))/(reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
            leftfrontMotor.setPower(currentMotorPower+minPower);
            rightfrontMotor.setPower(currentMotorPower+minPower);
            leftbackMotor.setPower(currentMotorPower+minPower);
            rightbackMotor.setPower(currentMotorPower+minPower);
        }
        }
        else{
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts+reducingSpeedInches*COUNTS_PER_INCH)) {
                leftfrontMotor.setPower(-methodMotorPower+minPower);
                rightfrontMotor.setPower(-methodMotorPower+minPower);
                leftbackMotor.setPower(-methodMotorPower+minPower);
                rightbackMotor.setPower(-methodMotorPower+minPower);
            }
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-(rightbackMotor.getCurrentPosition()-rightCurrentEncoder))/(-reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
                leftfrontMotor.setPower(-currentMotorPower+minPower);
                rightfrontMotor.setPower(-currentMotorPower+minPower);
                leftbackMotor.setPower(-currentMotorPower+minPower);
                rightbackMotor.setPower(-currentMotorPower+minPower);
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }




//Strafe leftward or rightward a certain number of inches
    public void mecanumStrafe(double inches){
        double COUNTS_PER_INCH = 29.75;
        double methodMotorPower=1.0; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int moveCounts;
        int rightCurrentEncoder = rightbackMotor.getCurrentPosition();
        int reducingSpeedInches = 6;
        moveCounts = (int)(inches * COUNTS_PER_INCH);
        double minPower = 0.15;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking

        if(inches > 0){
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts-reducingSpeedInches*COUNTS_PER_INCH)) {
                leftfrontMotor.setPower(methodMotorPower+minPower);
                rightfrontMotor.setPower(-methodMotorPower+minPower);
                leftbackMotor.setPower(-methodMotorPower+minPower);
                rightbackMotor.setPower(methodMotorPower+minPower);
            }
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-((rightbackMotor.getCurrentPosition())-rightCurrentEncoder))/(reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
                leftfrontMotor.setPower(currentMotorPower+minPower);
                rightfrontMotor.setPower(-currentMotorPower+minPower);
                leftbackMotor.setPower(-currentMotorPower+minPower);
                rightbackMotor.setPower(currentMotorPower+minPower);
            }
        }
        else{
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts+reducingSpeedInches*COUNTS_PER_INCH)) {
                leftfrontMotor.setPower(-methodMotorPower+minPower);
                rightfrontMotor.setPower(methodMotorPower+minPower);
                leftbackMotor.setPower(methodMotorPower+minPower);
                rightbackMotor.setPower(-methodMotorPower+minPower);
            }
            while (opModeIsActive() && rightbackMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((moveCounts-((rightbackMotor.getCurrentPosition())-rightCurrentEncoder))/(-reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.05;
                leftfrontMotor.setPower(-currentMotorPower+minPower);
                rightfrontMotor.setPower(currentMotorPower+minPower);
                leftbackMotor.setPower(currentMotorPower+minPower);
                rightbackMotor.setPower(-currentMotorPower+minPower);
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }






    //rotate to a certain heading
    public void mecanumTurn(int degrees){
        double methodMotorPower=0.66; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int reducingSpeedDegrees = 30;
        double minPower = 0.15;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking
        
        if(degrees > imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1){
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 <= (degrees-reducingSpeedDegrees)) {
                leftfrontMotor.setPower(methodMotorPower+minPower);
                rightfrontMotor.setPower(-methodMotorPower+minPower);
                leftbackMotor.setPower(methodMotorPower+minPower);
                rightbackMotor.setPower(-methodMotorPower+minPower);
            }
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 <= (degrees)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((degrees-imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1)/(reducingSpeedDegrees),1.1)+0.05;
                leftfrontMotor.setPower(currentMotorPower+minPower);
                rightfrontMotor.setPower(-currentMotorPower+minPower);
                leftbackMotor.setPower(currentMotorPower+minPower);
                rightbackMotor.setPower(-currentMotorPower+minPower);
            }
        }
        else{
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 >= (degrees+reducingSpeedDegrees)) {
            leftfrontMotor.setPower(-methodMotorPower+minPower);
            rightfrontMotor.setPower(methodMotorPower+minPower);
            leftbackMotor.setPower(-methodMotorPower+minPower);
            rightbackMotor.setPower(methodMotorPower+minPower);
            }
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 >= (degrees)) {
                currentMotorPower=(methodMotorPower-0.05)*Math.pow((degrees-imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1)/(-reducingSpeedDegrees),1.1)+0.05;
                leftfrontMotor.setPower(-currentMotorPower+minPower);
                rightfrontMotor.setPower(currentMotorPower+minPower);
                leftbackMotor.setPower(-currentMotorPower+minPower);
                rightbackMotor.setPower(currentMotorPower+minPower);
            }
        }
        leftfrontMotor.setPower(0);
        rightfrontMotor.setPower(0);
        leftbackMotor.setPower(0);
        rightbackMotor.setPower(0);
    }





}//public class extends
