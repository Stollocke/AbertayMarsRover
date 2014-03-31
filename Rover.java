import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.robotics.localization.*;
import lejos.util.*;
import lejos.geom.Point;
import java.io.*;

/**
* Rover class
* Main controller, keeps track of all the sensors and navigation arrays
* 
*/

public class Rover implements ButtonListener {
    
    public DifferentialPilot driveUnit;
    public RoverNavigator navigationUnit;
    public InstrumentsKit sensorUnit;
    public MapKit mappingUnit;
    
    /**
    * Constructor
    * Creates the object, and sets up all units necessary for driving
    */
    public Rover() {
        Sound.setVolume(60);
        // instantiate sensor objects
        UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S3);
        TouchSensor left = new TouchSensor(SensorPort.S1);
        TouchSensor right = new TouchSensor(SensorPort.S2);
        
        Motor.A.resetTachoCount();
        Motor.B.resetTachoCount();
        
        // instantiate elementary rover units
        this.sensorUnit = new InstrumentsKit(Motor.B, sonic, left, right, this);
        this.driveUnit = new DifferentialPilot(4.2f, 16.8f, Motor.A, Motor.C, true);
        this.navigationUnit = new RoverNavigator(this.driveUnit, this.sensorUnit, this);
        this.mappingUnit = new MapKit(this);
        
        // drive unit settings
        this.driveUnit.setTravelSpeed(20);
        this.driveUnit.setRotateSpeed(45);
        
        Button.ESCAPE.addButtonListener(this);
    }
    
    
    /**
    * Method to call before starting exploration
    * 
    * @return void
    */
    public void startMission() {
        this.navigationUnit.rotateTo(0);
    }
    
    /**
    * Main Exploration and mapping method
    *
    * @return void
    */
    public void mainBehaviour() {
        this.navigationUnit.addWaypoint(100.0f,0.0f);
        this.navigationUnit.addWaypoint(100.0f,50.0f);
        this.navigationUnit.addWaypoint(0.0f,-50.0f);
        
        while(!this.navigationUnit.followPath()) {
            this.avoidObstacle();
        }
    }
    
    /**
    * Mission wrap-up method
    *
    * @return void
    */
    public void finishMission() {
        this.navigationUnit.goTo(0.0f,0.0f);
        
        Point[] map = this.mappingUnit.getMap();
        // write the map file
        this.writePointsToFile("map.csv", map);
        Button.waitForAnyPress();
    }
    
    
    private void avoidObstacle() {
        
        // potentially add bumper obstacle
        this.mappingUnit.addBump(this.sensorUnit.lastBump);
        
        this.driveUnit.travel(-15);
        int[][] distances = this.sensorUnit.forwardSweep();
        this.processScan(distances);
        double angle = (double)this.bestAngle(distances);
        
        // turn towards the best direction
        if(!this.navigationUnit.rotateBy(angle)) {
            // bump during turn
            this.mappingUnit.addBump(this.sensorUnit.lastBump);
            this.driveUnit.travel(-20);
            return;
        }
        // drive 60 cm
        if(!this.navigationUnit.travelDistance(60.0)) {
            // obstacle during avoidance travel
            this.mappingUnit.addBump(this.sensorUnit.lastBump);
            
            int distanceAhead = this.sensorUnit.distanceAhead();
            if(this.sensorUnit.lastBump == 0 && distanceAhead < 200) {
                this.mappingUnit.addObstacle(0, (float)distanceAhead);
            }
            
            this.driveUnit.travel(-20);
            return;
        }
    }
    
    
    private void writePointsToFile(String filename, Point[] pointArray) {
        
        FileOutputStream out = null;
        File data = new File(filename);
        try {
          out = new FileOutputStream(data);
        } catch(IOException e) {
           System.err.println("Failed to create output stream");
           Button.ESCAPE.waitForPress();
           System.exit(1);
        }
        
        DataOutputStream dataOut = new DataOutputStream(out);
        
        try {
            
            for(int i = 0; i < pointArray.length; i++) {
                dataOut.writeBytes(pointArray[i].getY()+","+pointArray[i].getX()+"\n");
            }
            out.close();
            
        } catch(IOException e) {
           System.err.println("Failed to create output stream");
           Button.ESCAPE.waitForPress();
           System.exit(1);
        }
    }
    
    /*
    #########################################################################
    Accessor methods
    #########################################################################
    */
    
    /**
    * Rover's x coordinate accessor
    *
    * @return float the rover's current x coordinate
    */
    public float getX() {
        return (float)this.navigationUnit.getX();
    }
    
    /**
    * Rover's y coordinate accessor
    *
    * @return float the rover's current y coordinate
    */
    public float getY() {
        return (float)this.navigationUnit.getY();
    }
    
    /**
    * Rover's heading accessor
    *
    * @return float the rover's current heading
    */
    public float getHeading() {
        return (float)this.navigationUnit.getHeading();
    }
    
    
    /*
    #########################################################################
    Rover Interface methods
    #########################################################################
    */
    // those methods **must** be implemented for other classes to work properly.
    
    public void displayUserMessage(String message) {
        LCD.clear();
        LCD.drawString(message,0,0);
    }
    
    /**
    * Executed each time the rover reaches a waypoint
    *
    * @return void
    */
    public void arrivedAtWaypoint() {
        
        int[][] distances = this.sensorUnit.forwardSweep();
        
        this.processScan(distances);
    }
    
    /**
    * Determines the best direction to take from an array of bearing and free headway
    *
    * @param int[][] distance the array of [bearing],[distance]
    * @return int the best angle to turn towards
    */
    public int bestAngle(int[][] distances) {
        
        int bestAngle = 0;
        int bestDistance = 0;
        
        for(int i = 0; i < distances.length; i++) {
            if(distances[i][1] > bestDistance) {
                bestAngle = distances[i][0];
                bestDistance = distances[i][1];
            }
        }
        return bestAngle;
    }
    
    /**
    * Process an array of distances and bearing to add obstacles to the map
    *
    * @param int[][] distance the array of [bearing],[distance]
    * @return void
    */
    public void processScan(int[][] distances) {
        for(int i = 0; i < distances.length; i++){
            if(distances[i][1] < 200) {
                float distance = (float)distances[i][1];
                this.mappingUnit.addObstacle(distances[i][0], (float)distances[i][1]);
            }
        }
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