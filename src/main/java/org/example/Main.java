package org.example;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        //Enables just some level of logging.
        //Make sure to import org.cloudsimplus.util.Log;
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        //Creates a CloudSimPlus object to initialize the simulation.
        var simulation = new CloudSimPlus();

        //Creates a Broker that will act on behalf of a cloud user (customer).
        var broker0 = new DatacenterBrokerSimple(simulation);

        //Host configuration
        int hostPes = 64;
        long peSimpleMips = 20000;
        long ram = 100000; //in Megabytes
        long storage = 1000000; //in Megabytes
        long bw = 1000000; //in Megabits/s

        //Creates one host with a specific list of CPU cores (PEs).
        //Uses a PeProvisionerSimple by default to provision PEs for VMs
        //Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        //Uses VmSchedulerSpaceShared by default for VM scheduling

        final List<Pe> peList =
                IntStream.range(0, hostPes)
                        .mapToObj(i -> new PeSimple(peSimpleMips))
                        .collect(toCollection(() -> new ArrayList<>(hostPes)));

        var host0 = new HostSimple(ram, bw, storage, peList);

        //Creates a Datacenter with a list of Hosts.
        //Uses a VmAllocationPolicySimple by default to allocate VMs
        var dc0 = new DatacenterSimple(simulation, List.of(host0));

        //Creates one VM with one CPU core to run applications.
        //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
        var vm0 = new VmSimple(2000, 1);
        vm0.setRam(1000).setBw(1000).setSize(1000);

        //Creates Cloudlets that represent applications to be run inside a VM.
        //It has a length of 1000 Million Instructions (MI) and requires 1 CPU core
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        var utilizationModel = new UtilizationModelDynamic(0.5);
        var cloudlet0 = new CloudletSimple(1000000, 1, utilizationModel);
        var cloudlet1 = new CloudletSimple(1000000, 1, utilizationModel);
        var cloudletList = List.of(cloudlet0, cloudlet1);

        broker0.submitVmList(List.of(vm0));
        broker0.submitCloudletList(cloudletList);

        /*Starts the simulation and waits all cloudlets to be executed, automatically
        stopping when there is no more events to process.*/
        simulation.start();

/*Prints the results when the simulation is over
(you can use your own code here to print what you want from this cloudlet list).*/
        new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
    }

}