import java.util.*;

import lejos.nxt.*;
import lejos.geom.Point;
import lejos.robotics.navigation.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.util.*;

/**
* Abertay University - SET
* Mars Rover Project
* AdvancedRoverNavigator class
* 2014-03-25 Simon Stoll | Cesar Parent
*
* RoverNavigator Subclass
* 
* Provide path following functions and waypoint management
*/

public class AdvancedRoverNavigator extends RoverNavigator {
    
    private LinkedList<Point> path;
    private Point nextPoint;
    
    
    public AdvancedRoverNavigator(DifferentialPilot pilot, InstrumentsKit mast, Rover roverObject) {
        super(pilot, mast, roverObject);
        path = new LinkedList<Point>();
    }
    
    
    /**
    * Adds a new waypoint at the end of the path
    *
    * @param float x new waypoint's x coordinate
    * @param float y new waypoint's y coordinate
    */
    public void addWaypoint(float x, float y) {
        
        Point newPoint = new Point(x, y);
        //insert the point at the very end of the list
        this.path.add(newPoint);
    }
    
    
    /**
    * Fushes the path of the rover
    *
    * @return void
    */
    public void clearPath() {
        this.path.clear();
    }
    
    /**
    * Commands the rover to follow the path in memory
    * The rover will go on until an obstacle is encountered
    * In that case, the rest of the path will stay in memory.
    *
    * @return boolean true if the last point was reached, false otherwise
    */
    public boolean followPath() {
        
        LCD.drawString("Waypoints: "+this.path.size(),0,4);
        
        Point[] pathArray = this.path.toArray(new Point[this.path.size()]);
        
        // iterate over the path queue
        for(int i = 0; i < pathArray.length; i++) {
            
            Point target = pathArray[i];
            
            if(!this.goTo((float)target.getX(), (float)target.getY())) {
                return false;
            }
            else {
                // the waypoint was reached, remove it from the list
                this.path.remove(target);
                LCD.drawString(this.toString(),0,0);
            }
        }
        // if the path was empty, return true
        return true;
    }
    
    /**
    * @return the next waypoint on the path
    */
    public Point getNextWayPoint() {
        return new Point(0.0f, 0.0f);
    }
    
}