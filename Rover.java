import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.robotics.localization.*;
import lejos.util.*;

/**
* Rover class
* Main controller, keeps track of all the sensors and navigation arrays
* 
*/

public class Rover implements ButtonListener {
    
    private DifferentialPilot driveUnit;
    private AdvancedRoverNavigator navigationUnit;
    private InstrumentsKit sensorUnit;
    
    
    /**
    * Constructor
    * Creates the object, and sets up all units necessary for driving
    */
    public Rover() {
        
        // instantiate sensor objects
        UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S3);
        TouchSensor left = new TouchSensor(SensorPort.S4);
        TouchSensor right = new TouchSensor(SensorPort.S2);
        
        // instantiate elementary rover units
        this.sensorUnit = new InstrumentsKit(Motor.B, sonic, left, right, this);
        this.driveUnit = new DifferentialPilot(4.2f, 16.8f, Motor.A, Motor.C, true);
        this.navigationUnit = new AdvancedRoverNavigator(this.driveUnit, this.sensorUnit, this);
        
        // drive unit settings
        this.driveUnit.setTravelSpeed(20);
        this.driveUnit.setRotateSpeed(30);
    }
    
    
    /**
    * Method to call before starting exploration
    * 
    * @return void
    */
    public void startMission() {
        
    }
    
    /**
    * Main Exploration and mapping method
    *
    * @return void
    */
    public void mainBehaviour() {
        
    }
    
    /**
    * Mission wrap-up method
    *
    * @return void
    */
    public void finishMission() {
    
    }
    
    /*
    #########################################################################
    Rover Interface methods
    #########################################################################
    */
    // those methods **must** be implemented for other classes to work properly.
    
    public void displayUserMessage(String message) {
        LCD.clear();
        LCD.drawString("",0,0);
        // TODO: check the length of the string and display on multiple lines
    }
    
    
    /*
    #########################################################################
    ButtonListener Interface methods
    #########################################################################
    */
    
    /**
    * Button Press interface method
    * Interupts any ongoing program
    *
    * @param Button b the pressed button
    * @return void
    */
    public void buttonPressed(Button b) {
        if(b == Button.ESCAPE) {
            // notice the user that everything stopped
            this.driveUnit.stop();
            this.displayUserMessage("Program Interrupted");
            Delay.msDelay(1000);
            System.exit(0);
        }
    }
    
    /**
    * Button Release interface method
    *
    * @param Button b the pressed button
    * @return void
    */
    public void buttonReleased(Button b) {
        
    }
    
}