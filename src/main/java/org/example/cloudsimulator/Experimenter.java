package org.example.cloudsimulator;

public class Experimenter {

    public static void main(String[] args) {
        CloudSimRunner cloudSimRunner = new CloudSimRunner();
        double et = cloudSimRunner.runSimulation(6, 4134);
    }
}
