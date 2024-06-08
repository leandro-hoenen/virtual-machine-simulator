package org.example;
import org.example.pso.PSOimplementation;

public class Main {

    public static void main(String[] args) {
        int numDimensions = 30; //Number of dimensions for problem
        int numParticles = 30; //Number of particles in swarm
        int maxIterations = 10; //Max number of iterations
        final double c1 = 1.496180; //Cognitive coefficient
        final double c2 = 1.496180; //Social coefficient
        final double w = 0.729844;
        final double[] range = new double[]{1, 10}; //Range of values for the problem
        PSOimplementation pso = new PSOimplementation(numDimensions, numParticles, maxIterations, c1, c2, w, range);
        pso.optimize();
        // CloudSimRunner cloudSimRunner = new CloudSimRunner();
        // cloudSimRunner.runSimulation();
    }

}