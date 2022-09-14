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

/* Gamepad Teleop controls - Joystick controls
    gamepad1.[whatever] (defaults are gamepad 1 and then gamepad 2)
    
    D-Pad = dpad_* ; no idea what goes in place of the star
    Left Stick = left_stick_x , left_stick_y
    Right Stick right_stick_x , right_stick_y
    X-Button = x
    Y-Button = y
    A-Button = a
    B-Button = b
    Left trigger = left_trigger
    Right trigger = right_trigger
    
    Cont Servo stuff:
    Max power forward = .setPower(1)
    Max power backward = .setPower(-1)
    stop = .setPower(0)
    Get servo power = getPower()
    
*/
package org.firstinspires.ftc.teamcode;

import java.util.Locale;
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
@TeleOp

public class OldMechanumDrive extends LinearOpMode {
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
    boolean elevatorLowering = false;
    boolean elevatorLifting = false;
    int ringCount = 0;
    int conveyorEncoderSave = 0;
    int flipRotation=1;
    

    @Override
    public void runOpMode() {

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
        rightfrontMotor.setDirection(DcMotor.Direction.REVERSE);
        rightbackMotor.setDirection(DcMotor.Direction.REVERSE);
        elevator.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

    // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            conveyor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            telemetry.addData("Status", "Running");
            telemetry.addData("ringCount ", ringCount);
            telemetry.addData("stackLaser ", stackLaser.getDistance(DistanceUnit.INCH));
            telemetry.addData("wallDistance ", wallDistance.getDistance(DistanceUnit.INCH));
            telemetry.update();
            
            if(initialSetup == false){
                
                
                claw.setPosition(1);
                stopper.setPosition(0.9);
                shooterAngle.setPosition(0.5);
                flicker.setPower(1);
                    while(magnetFlicker.isPressed() == true){}
                    while(magnetFlicker.isPressed() == false){}
                    flicker.setPower(0);
                
                
                //while(magnetFlicker.isPressed() == true){
                //    flicker.setPower(1);
                //}
                //flicker.setPower(1);
                //while(magnetFlicker.isPressed() == false){}
                //sleep(450);
                //flicker.setPower(0);
                conveyor.setPower(1);
                while(magnetSensor.isPressed() == false){
                        }
                        conveyor.setPower(0);
                    //magSwitchA isnt on
                    intake.setPower(-0.2);
                initialSetup=true;
            }
        
        // move 
        mecanumControl();
        
         //turn on shooter
         if(gamepad1.left_trigger > 0.2 && shooterOn == false){
            shooter.setPower(1.0);
            sleep(1000);
            shooterOn = true;
            while(gamepad1.left_trigger > 0.2){}
         }
         else if(gamepad1.left_trigger > 0.2 && shooterOn == true){
            shooter.setPower(0);
            shooterOn = false;
            while(gamepad1.left_trigger > 0.2){}
         }
         
         
         
        //color sensor code, from BlueQuarry
        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};
        
        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attentuate the measured values.
        final double SCALE_FACTOR = 255;
        
        Color.RGBToHSV((int) (sensorColorIntake.red() * SCALE_FACTOR),
                        (int) (sensorColorIntake.green() * SCALE_FACTOR),
                        (int) (sensorColorIntake.blue() * SCALE_FACTOR),
                        hsvValues);
        
        //color sensor test
            //telemetry.addData("Alpha", sensorColorIntake.alpha());
            //telemetry.addData("Red  ", sensorColorIntake.red());
            //telemetry.addData("Green", sensorColorIntake.green());
            //telemetry.addData("Blue ", sensorColorIntake.blue());
            telemetry.addData("Hue", hsvValues[0]);
            telemetry.addData("Distance (cm)", sensorDistanceIntake.getDistance(DistanceUnit.CM));

        
           
        
        //intake and shooting system - right trigger shoots
        
        
            
            
            if(sensorDistanceIntake.getDistance(DistanceUnit.CM) < 2 && hsvValues[0] < 69 && ringCount < 3){
                intake.setPower(0);
                leftfrontMotor.setPower(0);
                rightfrontMotor.setPower(0);
                leftbackMotor.setPower(0);
                rightbackMotor.setPower(0);
                elevator.setPower(0);
                flicker.setPower(0);
                ringCount += 1;
                if(ringCount == 1){
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
                    
                    while(conveyor.getCurrentPosition() < conveyorEncoderSave + 215){
                        conveyor.setPower(1);
                    }
                    flicker.setPower(1);
                    
                    
                    
                
                    
                    while(magnetSensor.isPressed() == false){
                        conveyor.setPower(1);    
                    }//magSwitchA isnt on
                    conveyor.setPower(0);
                    while(magnetFlicker.isPressed() == true){}
                    flicker.setPower(0);
                    //sleep(450);
                    //flicker.setPower(0);
                    
                    intake.setPower(-0.2);
                }//ringCount == 1
                
                else if(ringCount == 2){
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
                
                intake.setPower(-0.2);
                
            }//ringCount == 2
            
                else if(ringCount == 3){
                    intake.setPower(0); // the thing where the motor stops and holds position like a servo
                }//ringCount == 3
    
            }//color-distance sensor

            else if((gamepad1.right_trigger > 0.2) && shooter.getPower() > 0.5 && (ringCount > 0)){
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
                while(gamepad1.right_trigger > 0.1){}
                
            } //shooting the rings */
        
        //change angle of the shooter servo (shooterAngle) - a, b, x, y buttons
        //Y (Hi-goal)= 1.00
        //B (Mid-goal) = 0.58
        //X  = .83
        
        if(gamepad1.y){//hi-goal
            //set the shooter power here to 1.0
            shooter.setPower(1.0);
            shooterAngle.setPosition(0.5);
        }
        else if(gamepad1.x){
            //set the shooter power here to 0.67 and change position to 0.60
            shooter.setPower(0.67);
            shooterAngle.setPosition(0.5);
        }
        /*Obsolete
        else if(gamepad1.b){//mid-goal
            if(ringCount==0){
                ringCount=3;
            }
            else{
                ringCount--;
            }
            while(gamepad1.b){}
            
        }*/
        else if(gamepad1.b){
            //shooterAngle.setPosition(0);
            while(gamepad1.b){}
            boolean troubleshoot = true;
            while(troubleshoot == true){
            shooter.setPower(0.2);
            stopper.setPosition(0.5);
            flicker.setPower(gamepad1.left_stick_y);
            conveyor.setPower(Math.abs(0.33*gamepad1.right_stick_y));
            if(gamepad1.b){
            while(gamepad1.b){}
            troubleshoot = false;
            }
            }
            shooter.setPower(0);
            stopper.setPosition(0.9);
            while(magnetFlicker.isPressed() == true){
                    flicker.setPower(1);
                }
                flicker.setPower(1);
                while(magnetFlicker.isPressed() == false){}
                sleep(450);
                flicker.setPower(0);
                while(magnetSensor.isPressed() == false){
                        conveyor.setPower(1);    
                    }//magSwitchA isnt on
                    conveyor.setPower(0);
        }
        
        
        //grab/let go of tower - left bumper
            if(gamepad1.left_bumper){
                if(claw.getPosition() != 1){
                    claw.setPosition(1);
                    rightfrontMotor.setDirection(DcMotor.Direction.REVERSE);
                    rightbackMotor.setDirection(DcMotor.Direction.REVERSE);
                    leftfrontMotor.setDirection(DcMotor.Direction.FORWARD);
                    leftbackMotor.setDirection(DcMotor.Direction.FORWARD);
                    flipRotation=1;
                }
                else{
                   claw.setPosition(0.4);
                   rightfrontMotor.setDirection(DcMotor.Direction.FORWARD);
                    rightbackMotor.setDirection(DcMotor.Direction.FORWARD);
                    leftfrontMotor.setDirection(DcMotor.Direction.REVERSE);
                    leftbackMotor.setDirection(DcMotor.Direction.REVERSE);
                    flipRotation=-1;
                }
                while(gamepad1.left_bumper){
                    
                }
            }//grab/let go of tower
            
        //move the elevator up and down-dpad
            if(gamepad1.dpad_up || elevatorLifting == true){
                if(elevatorTopButton.isPressed() == true){
                    elevator.setPower(0);
                    elevatorLifting = false;
                    rightfrontMotor.setDirection(DcMotor.Direction.FORWARD);
                    rightbackMotor.setDirection(DcMotor.Direction.FORWARD);
                    leftfrontMotor.setDirection(DcMotor.Direction.REVERSE);
                    leftbackMotor.setDirection(DcMotor.Direction.REVERSE);
                    flipRotation=-1;
                }
                else{
                    claw.setPosition(1);
                    elevator.setPower(-1);
                    elevatorLifting = true;
                    rightfrontMotor.setDirection(DcMotor.Direction.FORWARD);
                    rightbackMotor.setDirection(DcMotor.Direction.FORWARD);
                    leftfrontMotor.setDirection(DcMotor.Direction.REVERSE);
                    leftbackMotor.setDirection(DcMotor.Direction.REVERSE);
                    flipRotation=-1;
                }
            }//dpad up 
            
            else if(gamepad1.dpad_down || elevatorLowering == true){
                if(elevatorBottomButton.isPressed() == true){
                    elevator.setPower(0);
                    elevatorLowering = false;
                    rightfrontMotor.setDirection(DcMotor.Direction.REVERSE);
                    rightbackMotor.setDirection(DcMotor.Direction.REVERSE);
                    leftfrontMotor.setDirection(DcMotor.Direction.FORWARD);
                    leftbackMotor.setDirection(DcMotor.Direction.FORWARD);
                    flipRotation=1;
                }
                else{
                    elevator.setPower(1);
                    elevatorLowering = true;
                    rightfrontMotor.setDirection(DcMotor.Direction.REVERSE);
                    rightbackMotor.setDirection(DcMotor.Direction.REVERSE);
                    leftfrontMotor.setDirection(DcMotor.Direction.FORWARD);
                    leftbackMotor.setDirection(DcMotor.Direction.FORWARD);
                    flipRotation=1;
                }
            }//dpad down
            
            else{
                elevator.setPower(0);
            }//stop elevator
        
        }//while opmode is active
    }//run opmode
    public void mecanumControl(){
    //false if wheel bars create a diamond shape
        boolean mecanumLayout_isX = true;
    
    //variables for later use 
        double max_motorPower = 0;
        int mecanumAdjust = 1;
        
    //list of variables to keep track of each direction's contribution to power in each wheel
        double powerDueTo_forwardBackward_frontLeft = 0;
        double powerDueTo_forwardBackward_frontRight = 0;
        double powerDueTo_forwardBackward_backLeft = 0;
        double powerDueTo_forwardBackward_backRight = 0;
        
        double powerDueTo_strafe_frontLeft = 0;
        double powerDueTo_strafe_frontRight = 0;
        double powerDueTo_strafe_backLeft = 0;
        double powerDueTo_strafe_backRight = 0;
        
        double powerDueTo_rotate_frontLeft = 0;
        double powerDueTo_rotate_frontRight = 0;
        double powerDueTo_rotate_backLeft = 0;
        double powerDueTo_rotate_backRight = 0;
        
    //variables to keep track of actual power sent to wheels based on averages for aboec 
        double average_motorPower_frontLeft = 0;
        double average_motorPower_frontRight = 0;
        double average_motorPower_backLeft = 0;
        double average_motorPower_backRight = 0;
    
    //let driver switch between slow, precise and not that 
        if(gamepad1.right_bumper && speedMultiplier == 1){
            speedMultiplier = 0.33;
        //get computer in loop to prevent flickering
        while(gamepad1.right_bumper){}
        }
        else if(gamepad1.right_bumper && speedMultiplier == 0.33){
            speedMultiplier = 1;
            //get computer in loop to prevent flickering
        while(gamepad1.right_bumper){}
        }
    //negate strafe variables to account for diamond wheel layout
        if(mecanumLayout_isX == false){
            mecanumAdjust = -1;
        }
    //add deadspace so joystick must be pushed 10% to account for accidental taps 
        if(Math.abs(gamepad1.left_stick_y) > .2){
        //x.x, y.y mean decimal input from controller joystick like 1.0 or 0.7
        //set variables for forward/backward situation to left stick y 
            powerDueTo_forwardBackward_frontLeft = gamepad1.left_stick_y;
            powerDueTo_forwardBackward_frontRight = gamepad1.left_stick_y;
            powerDueTo_forwardBackward_backLeft = gamepad1.left_stick_y;
            powerDueTo_forwardBackward_backRight = gamepad1.left_stick_y;
        }
        if(Math.abs(gamepad1.left_stick_x) > .2){
        //set variables for left/right situmacation to left stick x 
            powerDueTo_strafe_frontLeft = -gamepad1.left_stick_x*mecanumAdjust;
            powerDueTo_strafe_frontRight = gamepad1.left_stick_x*mecanumAdjust;
            powerDueTo_strafe_backLeft = gamepad1.left_stick_x*mecanumAdjust;
            powerDueTo_strafe_backRight = -gamepad1.left_stick_x*mecanumAdjust;
        }
        if(Math.abs(gamepad1.right_stick_x) > .2){
        //set variables for turn situation to right stick x 
            powerDueTo_rotate_frontLeft = -gamepad1.right_stick_x*flipRotation*0.7;
            powerDueTo_rotate_frontRight = gamepad1.right_stick_x*flipRotation*0.7;
            powerDueTo_rotate_backLeft = -gamepad1.right_stick_x*flipRotation*0.7;
            powerDueTo_rotate_backRight = gamepad1.right_stick_x*flipRotation*0.7;
        }//setting variables if joystick moved out of deadspace
        
        //set actual motor power variables to average of above varibles for corresponding wheel
            average_motorPower_frontLeft = ((powerDueTo_forwardBackward_frontLeft+powerDueTo_strafe_frontLeft+powerDueTo_rotate_frontLeft)/3);
            average_motorPower_frontRight = ((powerDueTo_forwardBackward_frontRight+powerDueTo_strafe_frontRight+powerDueTo_rotate_frontRight)/3);
            average_motorPower_backLeft = ((powerDueTo_forwardBackward_backLeft+powerDueTo_strafe_backLeft+powerDueTo_rotate_backLeft)/3);
            average_motorPower_backRight = ((powerDueTo_forwardBackward_backRight+powerDueTo_strafe_backRight+powerDueTo_rotate_backRight)/3);
        
        //take abs value of motor powers, find max value, divide all by 
        //normalizes all motors to max at 1 or -1, make sure to not / by 0
            max_motorPower = Math.max(Math.max(Math.abs(average_motorPower_frontLeft), Math.abs(average_motorPower_frontRight)), Math.max(Math.abs(average_motorPower_backLeft), Math.abs(average_motorPower_backRight)));
            if(max_motorPower != 0){
                average_motorPower_frontLeft /= max_motorPower;
                average_motorPower_frontRight /= max_motorPower;
                average_motorPower_backLeft /= max_motorPower;
                average_motorPower_backRight /= max_motorPower;
            }
        //drive motors at power calculated for them
            leftfrontMotor.setPower(average_motorPower_frontLeft*speedMultiplier);
            rightfrontMotor.setPower(average_motorPower_frontRight*speedMultiplier);
            leftbackMotor.setPower(average_motorPower_backLeft*speedMultiplier);
            rightbackMotor.setPower(average_motorPower_backRight*speedMultiplier);
    }//mecanum control
}//public class
