package elevatorSystem;
 
public class Guest {
    private int destination;
    private int currentLayer;
    private int startLayer;
    private int waitTime;
    private int weight;
    private int state;
    private String name;
    
    public Guest(int state, int weight, int destination, int currentLayer, String name) {
        this.state = state;
        this.currentLayer = currentLayer;
        this.startLayer = currentLayer;
        this.destination = destination;
        this.weight = weight;
        this.name = name;
    }
    
    public void setWaitTime(int time) {
        this.waitTime = time;
    }
    public int getWaitTime() {
        return waitTime;
    }
    
    public int getState() {
        return state;
    }
    
    public int getDestination() {
        return destination;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public int getCurrentLayer() {
        return currentLayer;
    }
    
    public int getStartLayer() {
        return startLayer;
    }
    
    public String getName() {
        return name;
    }
}

