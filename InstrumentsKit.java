import lejos.nxt.*;
import lejos.util.*;

public class InstrumentsKit {
    
    private UltrasonicSensor mastSonic;
    private NXTRegulatedMotor mastMotor;
    
    private TouchSensor leftBumperSensor;
    private TouchSensor rightBumperSensor;
    
    public int lastBump;
    private int safetyDistance;
    
    private Rover rover;
    
    /**
    * Constructor
    * Assigns the different sensors to their instance variables
    *
    * @param NXTRegulatedMotor headMotor the motor controlling the sensors' rotation
    * @param UltrasonicSensor headSonic the US Sensor mounted on the rotating head
    * @param TouchSensor leftBumper the bumper located on the front left of the rover
    * @param TouchSensor rightBumper the bumper located on the front right of the rover
    */
    public InstrumentsKit(NXTRegulatedMotor headMotor, UltrasonicSensor headSonic, TouchSensor leftBumper, TouchSensor rightBumper, Rover roverObject) {
        
        this.mastMotor = headMotor;
        this.mastSonic = headSonic;
        
        this.mastSonic.setMode(UltrasonicSensor.MODE_PING);
        
        this.leftBumperSensor = leftBumper;
        this.rightBumperSensor = rightBumper;
        
        this.rover = roverObject;
        
        this.lastBump = 0;
        
        // mast motor initialisation
        // wait for the user to put the mast in the right position
        this.mastMotor.flt(true);
        this.rover.displayUserMessage("Position Sensor Head");
        this.rover.displayUnit.waitForUser();
        
        // issue a stop command to the motor
        // this sets the motor to position locking mode
        // and makes sure it isn't nudged by the cables
        this.mastMotor.stop();
        this.mastMotor.resetTachoCount();
        this.mastMotor.setSpeed(90);
        
        Delay.msDelay(300);
        
        this.safetyDistance = 20;
    }
    
    /**
    * Tells if there is a collision risk
    *
    * @return boolean true if there is a collision risk, false otherwise
    */
    public boolean obstacleAhead() {
        return (this.obstacleInRange() || this.bumpersTriggered()) ? true : false;
    }
    
    /**
    * Checks for a possible obstacle using the US sensor
    *
    * @return boolean true if there is an obstacle in safety range
    */
    public boolean obstacleInRange() {
        this.mastSonic.setMode(UltrasonicSensor.MODE_CONTINUOUS);
        int distance = this.mastSonic.getDistance();
        return (distance < this.safetyDistance) ? true : false;
    }
    
    /**
    * Tells if the bumpers detected a collision
    *
    * @return boolean true if one of the bumpers was triggered
    */
    public boolean bumpersTriggered() {
        boolean leftBump = this.leftBumperSensor.isPressed();
        boolean rightBump = this.rightBumperSensor.isPressed();
        
        if((leftBump && !rightBump) || (rightBump && !leftBump)) {
            this.lastBump = leftBump ? -1 : 1;
            return true;
        }
        else if (leftBump || rightBump) {
            this.lastBump = 2;
            return true;
        }
        else {
            this.lastBump = 0;
            return false;
        }
    }
    
    /**
    * Returns the direction with the most free way in a 180degree arc
    *
    * @return int the relative angle between the current and desired direction (clockwise)
    */
    public int[][] forwardSweep() {
        
        //Motor.A.flt();
        //Motor.C.flt();
        
        int[][] distances = new int[19][2];
        int j = 0;
        
        for (int i=-90; i <= 90; i+=10) {
            this.setMastAngle(i);
            distances[j][0] = -this.mastMotor.getPosition();
            this.mastSonic.ping();
            Delay.msDelay(100);
            distances[j][1] = this.mastSonic.getDistance();
            Delay.msDelay(100);
            j++;
        }
        
        //Motor.A.stop();
        //Motor.C.stop();
        this.setMastAngle(0);
        return distances;
    }
    
    /**
    * Sets the mastHead heading
    *
    * @param int angle the angle to which the mast needs to be sent
    * @return void
    */
    private void setMastAngle(int angle) {
        this.mastMotor.rotateTo(angle);
    }
}