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

    public void runSimulation() {
        // Datacenter parameters
        int hostPes = 64;
        long peSimpleMips = 20000;
        long ram = 100000; //in Megabytes
        long storage = 1000000; //in Megabytes
        long bw = 1000000; //in Megabits/s

        // Virtual machine parameters
        int vmPes = 3; //number of vCPUs
        long vmMips = 15000; //in Million Instructions per Second
        long vmRam = 1800; //in Megabytes
        long vmBw = 2000; //in Megabits/s
        long vmSize = 10000; //in Megabytes

        // Virtual machine workload
        double iniVmLoad = 1; //Initial VM load
        long cloudletLength = 1000000; //in Million Instructions
        int cloudletPes = 3; //number of threads used by Cloudlet


        // Create datacenter and hosts
        var simulation = new CloudSimPlus();
        var broker0 = new DatacenterBrokerSimple(simulation);
        var host0 = createHost(hostPes, peSimpleMips, ram, storage, bw);
        var dc0 = new DatacenterSimple(simulation, List.of(host0));

        // Create VM
        var vm0 = createVm(vmMips, vmPes, vmRam, vmBw, vmSize);
        broker0.submitVmList(List.of(vm0));

        // Set workload
        var utilizationModel = new DistributedUtilizationModel(1700, 0.3, 0.01);
        var cloudlet0 = new CloudletSimple(cloudletLength, cloudletPes, utilizationModel);
        utilizationModel.setCloudlet(cloudlet0);
        broker0.submitCloudletList(List.of(cloudlet0));

        // Start simulation
        simulation.start();

        new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
        double executionTime = cloudlet0.getTotalExecutionTime();

        // System.out.println(executionTime);
        // new CloudletSimple(100000, 10).setUtilizationModelBw();
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