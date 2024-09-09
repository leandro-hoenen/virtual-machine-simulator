package org.example.cloudsimulator;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

public class CloudSimRunner {

    public double runSimulation(long vmPes, long ramAssigned) {
        // Datacenter parameters
        int hostPes = 64;
        long hostPeSimpleMips = 20000;
        long hostStorage = 1000000; //in Megabytes
        long hostBw = 1000000; //in Megabits/s
        long hostRam = Math.round(ramAssigned * 1.2); //in Megabytes

        // Virtual machine parameters
        long vmMips = 15000; //in Million Instructions per Second
        long vmBw = 2000; //in Megabits/s
        long vmSize = 10000; //in Megabytes

        // Virtual machine workload
        long cloudletLength = 2000000; //in Million Instructions
        int cloudletPes = 4; //number of threads used by Cloudlet
        int cloudletRAM = 4000; // RAMutil of VM in MB

        // Set RAM performance weighting and factors
        double ramPerformanceWeight = 0.3;
        double ramSteepnessFactor = 0.01;
        double swapPartitionDecayConstant = 0.8;

        // Create datacenter and hosts
        var simulation = new CloudSimPlus();
        var broker0 = new DatacenterBrokerSimple(simulation);
        var host0 = createHost(hostPes, hostPeSimpleMips, hostRam, hostStorage, hostBw);
        var dc0 = new DatacenterSimple(simulation, List.of(host0));

        // Create VM
        var vm0 = createVm(vmMips, vmPes, ramAssigned, vmBw, vmSize);
        broker0.submitVmList(List.of(vm0));

        // Set workload
        var utilizationModel = new DistributedUtilizationModel(cloudletRAM, ramPerformanceWeight, ramSteepnessFactor, swapPartitionDecayConstant);
        var cloudlet = new CloudletSimple(cloudletLength, cloudletPes, utilizationModel);
        utilizationModel.setCloudlet(cloudlet);
        broker0.submitCloudletList(List.of(cloudlet));

        // Start simulation
        simulation.start();

        new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();

        return cloudlet.getTotalExecutionTime();
    }

    private static HostSimple createHost(int hostPes, long peSimpleMips, long ram, long storage, long bw) {
        final List<Pe> peList =
                IntStream.range(0, hostPes)
                        .mapToObj(i -> new PeSimple(peSimpleMips))
                        .collect(toCollection(() -> new ArrayList<>(hostPes)));

        return new HostSimple(ram, bw, storage, peList);
    }

    private static VmSimple createVm(long mips, long pes, long ram, long bw, long size) {
        var vm = new VmSimple(mips, pes);
        vm.setRam(ram).setBw(bw).setSize(size);
        return vm;
    }

}