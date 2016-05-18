package com.locusenergy.homework.elevatorsimulator;


public interface Elevator {
    /**
     * Requests the Elevator to move to a certain floor. This method immitates pressing a button inside the
     * elevator. Therefore, it should not move the Elevator immidiately but just register the request.
     *
     * Bonus: this method should throw InvalidStateException if the Elevator is NOT busy.
     */
    void requestFloor(int floor);

    /**
     * Returns the internal state of the Elevator.
     */
    boolean isBusy();

    /**
     * Returns the floor where the Elevator is now.
     */
    int getCurrentFloor();

}

public class BuildingElevator implements Elevator {

    private int currentFloor;
    private boolean moving;

    public BuildingElevator() {
        this.currentFloor = 1;
        this.moving = false;
    }


    public void requestFloor(int floor){
        this.currentFloor = floor;
    }

    /**
     * Returns the internal state of the Elevator.
     */
    public boolean isBusy(){
        return this.moving;
    }

    /**
     * Returns the floor where the Elevator is now.
     */
    public int getCurrentFloor(){
        return this.currentFloor;
    }

}
