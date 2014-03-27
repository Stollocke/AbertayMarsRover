import lejos.nxt.*;
import lejos.util.*;

public class InstrumentsKit {
    
    private UltrasonicSensor mastSonic;
    private NXTRegulatedMotor mastMotor;
    
    private TouchSensor leftBumperSensor;
    private TouchSensor rightBumperSensor;
    
    private int lastBump;
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
    public InstrumentsKit(NXTRegulatedMotor headMotor, UltrasonicSensor headSonic, TouchSensor leftBumper, TouchSensor rightBumper) {
        
        this.mastMotor = headMotor;
        this.mastSonic = headSonic;
        
        this.leftBumperSensor = leftBumper;
        this.rightBumperSensor = rightBumper;
        
        this.lastBump = 0;
        
        // mast motor initialisation
        // wait for the user to put the mast in the right position
        this.rover.displayUserMessage("Position Sensor Head");
        Button.ENTER.waitForPressAndRelease();
        // issue a stop command to the motor
        // this sets the motor to position locking mode
        // and makes sure it isn't nudged by the cables
        this.mastMotor.stop();
        this.mastMotor.resetTachoCount();
        this.mastMotor.setSpeed(90);
        
        Sound.beepSequenceUp();
        Delay.msDelay(1000);
        
        this.safetyDistance = 10;
    }
    
    /**
    * Sets the main rover controller reference for the class
    * 
    * @param Object roverObject the instance of the main Rover Controller
    * @return void
    */
    public void setRoverObject(Rover roverObject) {
        this.rover = roverObject;
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
        int distance = this.mastSonic.getDistance();
        return distance < this.safetyDistance ? true : false;
    }
    
    /**
    * Tells if the bumpers detected a collision
    *
    * @return boolean true if one of the bumpers was triggered
    */
    public boolean bumpersTriggered() {
        boolean leftBump = this.leftBumperSensor.isPressed();
        boolean rightBump = this.rightBumperSensor.isPressed();
        
        if(leftBump || rightBump) {
            this.lastBump = leftBump ? -1 : 1;
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
    public int bestForwardAngle() {
        
        int bestAngle = 0;
        double bestDistance = 0;
        
        for (int i=-90; i <= 90; i+=20) {
            this.setMastAngle(i);
            double dist = (double) this.mastSonic.getDistance();
            if(dist >= bestDistance) {
                bestAngle = i;
                bestDistance = dist;
            }
            Delay.msDelay(500);
        }
        this.setMastAngle(0);
        return bestAngle;
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