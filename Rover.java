import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.robotics.localization.*;
import lejos.util.*;
import lejos.geom.Point;
import java.io.*;
import java.util.*;

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
    
    private int startTime;
    private ArrayList<Point> followedPath;
    
    /**
    * Constructor
    * Creates the object, and sets up all units necessary for driving
    */
    public Rover() {
        Sound.setVolume(10);
        // instantiate sensor objects
        UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
        TouchSensor left = new TouchSensor(SensorPort.S1);
        TouchSensor right = new TouchSensor(SensorPort.S2);
        
        Motor.A.resetTachoCount();
        Motor.B.resetTachoCount();
        
        // instantiate elementary rover units
        this.sensorUnit = new InstrumentsKit(Motor.B, sonic, left, right, this);
        this.driveUnit = new DifferentialPilot(4.32f, 16.8f, Motor.A, Motor.C, true);
        this.navigationUnit = new RoverNavigator(this.driveUnit, this.sensorUnit, this);
        this.mappingUnit = new MapKit(this);
        this.followedPath = new ArrayList<Point>();
        
        // drive unit settings
        this.driveUnit.setTravelSpeed(15);
        this.driveUnit.setRotateSpeed(45);
        
        Button.ESCAPE.addButtonListener(this);
    }
    
    /**
    * Method to call before starting exploration
    * 
    * @return void
    */
    public void startMission() {
        LCD.clear();
        this.startTime = (int) System.currentTimeMillis() / 1000;
        this.navigationUnit.rotateTo(0);
        this.followedPath.add(new Point(this.getX(), this.getY()));
    }
    
    /**
    * Main Exploration and mapping method
    *
    * @return void
    */
    public void mainBehaviour() {
        int missionElapsedTime = 0;
        int maxMissionTime = 180;
        
        
        while(missionElapsedTime < maxMissionTime) {
            this.avoidObstacle(this.navigationUnit.travelDistance(100.0));
            missionElapsedTime = ((int) System.currentTimeMillis() / 1000) - this.startTime;
        } 
    }
    
    /**
    * Mission wrap-up method
    *
    * @return void
    */
    public void finishMission() {
        
        while (!this.navigationUnit.goTo(0.0f,0.0f)) {
            
            this.followedPath.add(new Point(this.getX(), this.getY()));
            this.driveUnit.travel(-15);
            
            this.followedPath.add(new Point(this.getX(), this.getY()));
            int[][] distances = this.sensorUnit.forwardSweep();
            
            double angle = (double)this.bestAngle(distances);
            if(!this.navigationUnit.rotateBy(angle)) {
                // bump during turn
                this.driveUnit.travel(-20);
                this.followedPath.add(new Point(this.getX(), this.getY()));
            }
            else {
                this.driveUnit.travel(60);
                this.followedPath.add(new Point(this.getX(), this.getY()));
            }
        }
        
        
        Point[] map = this.mappingUnit.getMap();
        Point[] traj = this.followedPath.toArray(new Point[this.followedPath.size()]);
        // write the map and trajectory to file
        this.writePointsToFile("map-"+this.startTime+".csv", map);
        this.writePointsToFile("traj-"+this.startTime+".csv", traj);
        Sound.beepSequence();
        Button.waitForAnyPress();
    }
    
    /**
    *
    *
    *
    *
    */
    private void avoidObstacle(boolean success) {
        
        this.followedPath.add(new Point(this.getX(), this.getY()));
        // potentially add bumper obstacle
        double angle = 0;
        
        if(!success) {
            this.mappingUnit.addBump(this.sensorUnit.lastBump);
            this.driveUnit.travel(-15);
            this.followedPath.add(new Point(this.getX(), this.getY()));
        }
        
        int[][] distances = this.sensorUnit.forwardSweep();
        this.processScan(distances);
        
        
        if(!success) {
            angle = (double)this.bestAngle(distances);
        }
        else {
            Random rnd = new Random();
            angle = (double)(rnd.nextInt(240) - 120);
        }
        
        // if the direction is obstructed, back, turn around
        if(angle == -180) {
            this.diveUnit.travel(-20);
        }
        // turn towards the best direction
        if(!this.navigationUnit.rotateBy(angle)) {
            // bump during turn
            this.mappingUnit.addBump(this.sensorUnit.lastBump);
            this.driveUnit.travel(-20);
            return;
        }
    }
    
    /**
    *
    *
    *
    */
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
        System.out.println(message);
    }
    
    /**
    * Executed each time the rover reaches a waypoint
    *
    * @return void
    */
    public void arrivedAtWaypoint() {
        
    }
    
    /**
    * Determines the best direction to take from an array of bearing and free headway
    *
    * @param int[][] distance the array of [bearing],[distance]
    * @return int the best angle to turn towards
    */
    public int bestAngle(int[][] distances) {
        
        int bestDistance = 0;

        List<Integer> best = new ArrayList<Integer>();
        
        Random rnd = new Random();
        
        for(int i = 0; i < distances.length; i++) {
            if(distances[i][1] > bestDistance) {
                // clear the list of angles
                best.clear();
                best.add(distances[i][0]);
                bestDistance = distances[i][1];
            }
            else if (distances[i][1] == bestDistance){
                best.add(distances[i][0]);
            }
        }
        
        // return one of the best values
        int bestDir = best.get(rnd.nextInt(best.size()));
        return bestDistance > 30 ? bestDir : -180;
    }
    
    /**
    * Process an array of distances and bearing to add obstacles to the map
    *
    * @param int[][] distance the array of [bearing],[distance]
    * @return void
    */
    public void processScan(int[][] distances) {
        for(int i = 0; i < distances.length; i++){
            if(distances[i][1] < 150) {
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