import lejos.nxt.*;
import lejos.util.*;
import lejos.robotics.*;

public class IndicatorsKit {

    private Rover rover;
    private ColorSensor light; 
    
    /**
    * Constructor
    * Initiates a User Indicators instance, passing messages through sound and Light
    *
    */
    public IndicatorsKit(ColorSensor colourIndicator, Rover roverInstance) {
        this.rover = roverInstance;
        this.light = colourIndicator;
    }
    
    
    public void waitForUser() {
        // light and sound message
        this.light.setFloodlight(Color.BLUE);
        Sound.playTone(440, 100);
        this.light.setFloodlight(Color.GREEN);
        Sound.playTone(540, 100);
        this.light.setFloodlight(Color.BLUE);
        Sound.playTone(440, 100);
        
        // wait for press
        Button.ENTER.waitForPressAndRelease();
        
        // confirm with light and sound
        this.light.setFloodlight(Color.GREEN);
        Sound.playTone(540, 100);
        Delay.msDelay(200);
        Sound.playTone(540, 100);
        this.light.setFloodlight(Color.NONE);
    }
    
    public void positiveFeedback() {
        this.light.setFloodlight(Color.GREEN);
        Sound.playTone(240, 100);
        Delay.msDelay(200);
        Sound.playTone(540, 100);
        this.light.setFloodlight(Color.NONE);
    }
    
    public void neutralFeedback() {
        this.light.setFloodlight(Color.BLUE);
        Sound.playTone(440, 100);
        Delay.msDelay(200);
        Sound.playTone(440, 100);
        this.light.setFloodlight(Color.NONE);
    }
    
    public void negativeFeedback() {
        this.light.setFloodlight(Color.RED);
        Sound.playTone(540, 100);
        Delay.msDelay(200);
        Sound.playTone(240, 100);
        this.light.setFloodlight(Color.NONE);
    }
    
    public void goingBack() {
        this.light.setFloodlight(Color.BLUE);
        Sound.playTone(440, 100);
        Delay.msDelay(200);
        this.light.setFloodlight(Color.RED);
        Sound.playTone(300, 100);
        Delay.msDelay(200);
        this.light.setFloodlight(Color.BLUE);
        Sound.playTone(440, 100);
        Delay.msDelay(200);
        this.light.setFloodlight(Color.NONE);
    }
    
}