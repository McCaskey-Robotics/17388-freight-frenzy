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
import java.util.concurrent.TimeUnit;
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

public class AutonomousQuick extends LinearOpMode { //public class extends

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
        
        rightfrontMotor.setDirection(DcMotor.Direction.REVERSE);
        rightbackMotor.setDirection(DcMotor.Direction.REVERSE);
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
        if (opModeIsActive()) { //while opmode is active
            telemetry.addData("Status", "Running");
            telemetry.update();
            
            /**
             * ALL MEASUREMENTS RELATIVE TO THE FRONT RIGHT CORNER OF ROBOT ON THE PLEXIGLASS
             */
            
            shooterAngle.setPosition(0.83);
            sleep(1000);
            
            stopper.setPosition(0.8);
            sleep(200);
            
            
            
            
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(1);
            rightfrontMotor.setPower(1);
            rightbackMotor.setPower(1);
            sleep(1400);
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(100);
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(1);
            rightfrontMotor.setPower(-1);
            rightbackMotor.setPower(-1);
            sleep(70);
            
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(50);
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(1);
            rightfrontMotor.setPower(1);
            rightbackMotor.setPower(1);
            sleep(1400);
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(100);
            
            shooter.setPower(1.0);
            
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(1);
            rightfrontMotor.setPower(-1);
            rightbackMotor.setPower(-1);
            sleep(150);
            
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(100);
            
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(-1);
            rightfrontMotor.setPower(-1);
            rightbackMotor.setPower(1);
            sleep(2600);
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(100);
            
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(1);
            rightfrontMotor.setPower(-1);
            rightbackMotor.setPower(-1);
            sleep(80);
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(100);
            
            
            
            /*shoot ring */
            stopper.setPosition(0.5);
                sleep(200);
                //while(magnetFlicker.isPressed() == true){
                //    flicker.setPower(1);
                //}
                flicker.setPower(1);

                if(magnetFlicker.isPressed() == true){
                    sleep(100);
                }
                if(magnetFlicker.isPressed() == false){
                sleep(100);
                }
                sleep(250);
                flicker.setPower(0);
                
                stopper.setPosition(0.8);
                
                while(conveyor.getCurrentPosition() < conveyorEncoderSave + 255){
                        conveyor.setPower(1);
                    }
                    flicker.setPower(1);
                    
                
                    
                    while(magnetSensor.isPressed() == false){
                        conveyor.setPower(1);    
                    }//magSwitchA isnt on
                    conveyor.setPower(0);
                    sleep(450);
                    flicker.setPower(0);
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
                
                
            
            
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(-1);
            rightfrontMotor.setPower(-1);
            rightbackMotor.setPower(1);
            sleep(700);
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(100);
            
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(1);
            rightfrontMotor.setPower(-1);
            rightbackMotor.setPower(-1);
            sleep(40);
            
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(100);
            
            
            
            /*shoot ring */
            stopper.setPosition(0.5);
                sleep(200);
                //while(magnetFlicker.isPressed() == true){
                //    flicker.setPower(1);
                //}
                
                flicker.setPower(1);

                if(magnetFlicker.isPressed() == true){
                    sleep(100);
                }
                if(magnetFlicker.isPressed() == false){
                sleep(100);
                }
                sleep(250);
                flicker.setPower(0);
                
                stopper.setPosition(0.8);
            while(conveyor.getCurrentPosition() < conveyorEncoderSave + 255){
                        conveyor.setPower(1);
                    }
                    flicker.setPower(1);
                    
                
                    
                    while(magnetSensor.isPressed() == false){
                        conveyor.setPower(1);    
                    }//magSwitchA isnt on
                    conveyor.setPower(0);
                    sleep(450);
                    flicker.setPower(0);
            
            
            
            
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(-1);
            rightfrontMotor.setPower(-1);
            rightbackMotor.setPower(1);
            sleep(700);
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            sleep(100);
            
            
            
            /*shoot last ring*/
            stopper.setPosition(0.5);
                sleep(200);
                //while(magnetFlicker.isPressed() == true){
                //    flicker.setPower(1);
                //}
                flicker.setPower(1);
                //while(magnetFlicker.isPressed() == false){}
                //sleep(600);
                sleep(280);
                flicker.setPower(0);
                
                stopper.setPosition(0.8);
                sleep(0);
            
            
            
            leftfrontMotor.setPower(1);
            leftbackMotor.setPower(1);
            rightfrontMotor.setPower(1);
            rightbackMotor.setPower(1);
            sleep(1200);
            leftfrontMotor.setPower(0);
            leftbackMotor.setPower(0);
            rightfrontMotor.setPower(0);
            rightbackMotor.setPower(0);
            
            
        } //while opmode is active
        
        
     //run opmode
}//public class extends

}