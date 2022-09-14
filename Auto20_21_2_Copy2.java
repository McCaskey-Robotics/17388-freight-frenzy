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
import com.qualcomm.robotcore.hardware.DcMotorEx;
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

public class Auto20_21_2_Copy2 extends LinearOpMode { //public class extends

    

    private DcMotor blMotor;
    private DcMotor brMotor;
    private DcMotor flMotor;
    private DcMotor frMotor;
    private ColorSensor colorSensor;
    private DistanceSensor distanceSensor;
    private DcMotor elbow;
    private ColorSensor elementDetector;
    private CRServo intake;
    private DcMotorEx spinner;
    double speedMultiplier = 1; 
    boolean initialSetup = false;
    int elementPosA_DistSum = 0;
    int elementPosB_DistSum = 0;
    int elementPosC_DistSum = 0;
    int elementPosA_DistAvg;
    int elementPosB_DistAvg;
    int elementPosC_DistAvg;
    char elementPosition;
    
    
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
        blMotor = hardwareMap.get(DcMotor.class, "blMotor");
        brMotor = hardwareMap.get(DcMotor.class, "brMotor");
        flMotor = hardwareMap.get(DcMotor.class, "flMotor");
        frMotor = hardwareMap.get(DcMotor.class, "frMotor");
        this.spinner = (DcMotorEx)this.hardwareMap.get(DcMotorEx.class, "spinner");
        this.elbow = (DcMotor)this.hardwareMap.get(DcMotor.class, "elbow");
        this.intake = (CRServo)this.hardwareMap.get(CRServo.class, "intake");
        this.colorSensor = (ColorSensor)this.hardwareMap.get(ColorSensor.class, "colorSensor");
        
        frMotor.setDirection(DcMotor.Direction.REVERSE);
        brMotor.setDirection(DcMotor.Direction.REVERSE);
        
        
        
        // Ensure the robot is stationary, then reset the encoders and calibrate the gyro.
        blMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        brMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        blMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        sleep(100);
        
        blMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        brMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        blMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        

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
           telemetry.addData("Front-right", frMotor.getCurrentPosition());
        telemetry.addData("Front-left", flMotor.getCurrentPosition());
        telemetry.addData("Back-right", brMotor.getCurrentPosition());
        telemetry.addData("Back-left", blMotor.getCurrentPosition());
      
        telemetry.update();
        
        
        
        
        
        
        
/*
            
            flMotor.setPower(1);
                frMotor.setPower(1);
                blMotor.setPower(1);
                brMotor.setPower(1);
                int counter = 0;
            while(counter < 100){
                
                sleep(10);
                
                telemetry.addData("Position ", imu.getPosition());
                telemetry.addData("Heading ", imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle);
                telemetry.addData("Front-right", frMotor.getCurrentPosition());
        telemetry.addData("Front-left", flMotor.getCurrentPosition());
        telemetry.addData("Back-right", brMotor.getCurrentPosition());
        telemetry.addData("Back-left", blMotor.getCurrentPosition());
        
        telemetry.update();
        counter++;
            }
            flMotor.setPower(0);
                frMotor.setPower(0);
                blMotor.setPower(0);
                brMotor.setPower(0);
            */
            
            
            mecanumDrive(5);
            
            
            
            /*double power =0.15;
            flMotor.setPower(power);
                frMotor.setPower(power);
                blMotor.setPower(power);
                brMotor.setPower(power);
                sleep(7000);
                flMotor.setPower(0);
                frMotor.setPower(0);
                blMotor.setPower(0);
                brMotor.setPower(0);
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
        inches = -inches; //flip + or - if going opposit of what's expected
        double COUNTS_PER_INCH = 1739.28;
        double methodMotorPower=0.5; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int moveCounts;
        int rightCurrentEncoder = brMotor.getCurrentPosition();
        int reducingSpeedInches = 24;
        moveCounts = (int)(inches * COUNTS_PER_INCH);
        double minPower = 0.10;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking
        double recordedHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;

        if(inches > 0){
        while (opModeIsActive() && brMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts-reducingSpeedInches*COUNTS_PER_INCH)) {
            sleep(100);
            double currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
            if(recordedHeading-currentHeading>180){
                recordedHeading=recordedHeading*-1;
            }
            else if(currentHeading-recordedHeading>180){
                currentHeading=currentHeading*-1;
            }
            if(currentHeading<recordedHeading){
                flMotor.setPower(0.8*(methodMotorPower+minPower));
                frMotor.setPower(methodMotorPower+minPower);
                blMotor.setPower(0.8*(methodMotorPower+minPower));
                brMotor.setPower(methodMotorPower+minPower);
            }
            else if(currentHeading>recordedHeading){
                flMotor.setPower(methodMotorPower+minPower);
                frMotor.setPower(0.8*(methodMotorPower+minPower));
                blMotor.setPower(methodMotorPower+minPower);
                brMotor.setPower(0.8*(methodMotorPower+minPower));
            }
            else{
                flMotor.setPower(methodMotorPower+minPower);
                frMotor.setPower(methodMotorPower+minPower);
                blMotor.setPower(methodMotorPower+minPower);
                brMotor.setPower(methodMotorPower+minPower);
            }
        }
        while (opModeIsActive() && brMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts)) {
            currentMotorPower=(methodMotorPower-0.01)*Math.pow((moveCounts-((brMotor.getCurrentPosition())-rightCurrentEncoder))/(reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.01;
            flMotor.setPower(currentMotorPower+minPower);
            frMotor.setPower(currentMotorPower+minPower);
            blMotor.setPower(currentMotorPower+minPower);
            brMotor.setPower(currentMotorPower+minPower);
        }
        }
        else{
            while (opModeIsActive() && brMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts+reducingSpeedInches*COUNTS_PER_INCH)) {
                sleep(100);
                double currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
                if(recordedHeading-currentHeading>180){
                    recordedHeading=recordedHeading*-1;
                }
                else if(currentHeading-recordedHeading>180){
                    currentHeading=currentHeading*-1;
                }
                if(currentHeading<recordedHeading){
                    flMotor.setPower(-0.8*(methodMotorPower+minPower));
                    frMotor.setPower(-methodMotorPower-minPower);
                    blMotor.setPower(-0.8*(methodMotorPower+minPower));
                    brMotor.setPower(-methodMotorPower-minPower);
                }
                else if(currentHeading>recordedHeading){
                    flMotor.setPower(-methodMotorPower-minPower);
                    frMotor.setPower(-0.8*(methodMotorPower+minPower));
                    blMotor.setPower(-methodMotorPower-minPower);
                    brMotor.setPower(-0.8*(methodMotorPower+minPower));
                }
                else{
                    flMotor.setPower(-methodMotorPower-minPower);
                    frMotor.setPower(-methodMotorPower-minPower);
                    blMotor.setPower(-methodMotorPower-minPower);
                    brMotor.setPower(-methodMotorPower-minPower);
                }
            }
            while (opModeIsActive() && brMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.01)*Math.pow((moveCounts-(brMotor.getCurrentPosition()-rightCurrentEncoder))/(-reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.01;
                flMotor.setPower(-currentMotorPower-minPower);
                frMotor.setPower(-currentMotorPower-minPower);
                blMotor.setPower(-currentMotorPower-minPower);
                brMotor.setPower(-currentMotorPower-minPower);
            }
        }
        flMotor.setPower(0);
        frMotor.setPower(0);
        blMotor.setPower(0);
        brMotor.setPower(0);
    }




//Strafe leftward or rightward a certain number of inches
    public void mecanumStrafe(double inches){
        inches = -inches; //flip + or - if moving opposite of what's expected
        int encdir = -1; //flip + or - if encoder flipped is backwards
        double COUNTS_PER_INCH = 1739.28;
        double methodMotorPower=0.5; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int moveCounts;
        int rightCurrentEncoder = encdir*blMotor.getCurrentPosition();
        int reducingSpeedInches = 12;
        moveCounts = (int)(inches * COUNTS_PER_INCH);
        double minPower = 0.25;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking

        if(inches > 0){
            while (opModeIsActive() && encdir*blMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts-reducingSpeedInches*COUNTS_PER_INCH)) {
                flMotor.setPower(methodMotorPower+minPower);
                frMotor.setPower(-methodMotorPower-minPower);
                blMotor.setPower(-methodMotorPower-minPower);
                brMotor.setPower(methodMotorPower+minPower);
            }
            while (opModeIsActive() && encdir*blMotor.getCurrentPosition() <= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.01)*Math.pow((moveCounts-((encdir*blMotor.getCurrentPosition())-rightCurrentEncoder))/(reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.01;
                flMotor.setPower(currentMotorPower+minPower);
                frMotor.setPower(-currentMotorPower-minPower);
                blMotor.setPower(-currentMotorPower-minPower);
                brMotor.setPower(currentMotorPower+minPower);
            }
        }
        else{
            while (opModeIsActive() && encdir*blMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts+reducingSpeedInches*COUNTS_PER_INCH)) {
                flMotor.setPower(-methodMotorPower-minPower);
                frMotor.setPower(methodMotorPower+minPower);
                blMotor.setPower(methodMotorPower+minPower);
                brMotor.setPower(-methodMotorPower-minPower);
            }
            while (opModeIsActive() && encdir*blMotor.getCurrentPosition() >= (rightCurrentEncoder+moveCounts)) {
                currentMotorPower=(methodMotorPower-0.01)*Math.pow((moveCounts-((encdir*blMotor.getCurrentPosition())-rightCurrentEncoder))/(-reducingSpeedInches*COUNTS_PER_INCH),0.9)+0.01;
                flMotor.setPower(-currentMotorPower-minPower);
                frMotor.setPower(currentMotorPower+minPower);
                blMotor.setPower(currentMotorPower+minPower);
                brMotor.setPower(-currentMotorPower-minPower);
            }
        }
        flMotor.setPower(0);
        frMotor.setPower(0);
        blMotor.setPower(0);
        brMotor.setPower(0);
    }






    //rotate to a certain heading
    public void mecanumTurn(int degrees){
        double methodMotorPower=0.5; //set to a power between 0.05 and 1.0
        double currentMotorPower=methodMotorPower;
        int reducingSpeedDegrees = 30;
        double minPower = 0.10;
        methodMotorPower=methodMotorPower-minPower;//helps create a minimum power to prevent sticking
        
        if(degrees > imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1){
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 <= (degrees-reducingSpeedDegrees)) {
                flMotor.setPower(-methodMotorPower-minPower);
                frMotor.setPower(methodMotorPower+minPower);
                blMotor.setPower(-methodMotorPower-minPower);
                brMotor.setPower(methodMotorPower+minPower);
                telemetry.update();
            }
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 <= (degrees)) {
                currentMotorPower=(methodMotorPower-0.01)*Math.pow((degrees-imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1)/(reducingSpeedDegrees),1.1)+0.01;
                flMotor.setPower(-currentMotorPower-minPower);
                frMotor.setPower(currentMotorPower+minPower);
                blMotor.setPower(-currentMotorPower-minPower);
                brMotor.setPower(currentMotorPower+minPower);
                telemetry.update();
            }
        }
        else{
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 >= (degrees+reducingSpeedDegrees)) {
            flMotor.setPower(methodMotorPower+minPower);
            frMotor.setPower(-methodMotorPower-minPower);
            blMotor.setPower(methodMotorPower+minPower);
            brMotor.setPower(-methodMotorPower-minPower);
            telemetry.update();
            }
            while (opModeIsActive() && imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1 >= (degrees)) {
                currentMotorPower=(methodMotorPower-0.01)*Math.pow((degrees-imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle*-1)/(-reducingSpeedDegrees),1.1)+0.01;
                flMotor.setPower(currentMotorPower+minPower);
                frMotor.setPower(-currentMotorPower-minPower);
                blMotor.setPower(currentMotorPower+minPower);
                brMotor.setPower(-currentMotorPower-minPower);
                telemetry.update();
            }
        }
        flMotor.setPower(0);
        frMotor.setPower(0);
        blMotor.setPower(0);
        brMotor.setPower(0);
    }



}//public class extends
