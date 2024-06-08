package org.example;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.VmSimple;
import org.example.cloudsimulator.CloudSimRunner;
import org.example.pso.PSOimplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

public class Main {

    public static void main(String[] args) {
        int numDimensions = 30; //Number of dimensions for problem
        int numParticles = 30; //Number of particles in swarm
        int maxIterations = 10000; //Max number of iterations
        final double c1 = 1.496180; //Cognitive coefficient
        final double c2 = 1.496180; //Social coefficient
        final double w = 0.729844;
        PSOimplementation pso = new PSOimplementation(numDimensions, numParticles, maxIterations, c1, c2, w, new double[]{1, 10});
        pso.optimize();
        // CloudSimRunner cloudSimRunner = new CloudSimRunner();
        // cloudSimRunner.runSimulation();
    }

}