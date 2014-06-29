public class Main {
    public static void main(String[] args) {
        
        Rover rover = new Rover(90);
        
        rover.startMission();
        rover.mainBehaviour();
        rover.finishMission();
        
    }
}