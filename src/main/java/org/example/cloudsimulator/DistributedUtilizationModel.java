package org.example.cloudsimulator;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.utilizationmodels.UtilizationModelAbstract;
import org.cloudsimplus.vms.Vm;

public class DistributedUtilizationModel extends UtilizationModelAbstract {
    private final double ramPerformanceWeight; // RAM performance weight factor (x)
    private final double steepnessFactor; // Steepness of the logistic curve (k)
    private final double ramUtilization; // Current RAM utilization of the VM
    private final double decayConstant; // Decay constant for exponential degradation
    private Cloudlet cloudlet;

    // Constructor to initialize the parameters
    public DistributedUtilizationModel(double ramUtilization, double ramPerformanceWeight, double steepnessFactor, double decayConstant) {
        this.ramPerformanceWeight = ramPerformanceWeight;
        this.steepnessFactor = steepnessFactor;
        this.ramUtilization = ramUtilization;
        this.decayConstant = decayConstant;
    }

    public void setCloudlet(Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }

    @Override
    protected double getUtilizationInternal(double v) {
        // Get the VM parameters
        Vm vm = this.cloudlet.getVm();
        double assignedRam = vm.getRam().getCapacity(); // Assigned RAM capacity (RAM_assigned)
        double baseExecutionTimeFactor = 1; // Base execution time factor (ETF_base)

        // Adjust execution time based on RAM utilization or Swap utilization
        double adjustedExecutionTimeFactor;

        if (ramUtilization <= assignedRam) {
            // RAM utilization is within the assigned limits, use RPETFM model
            adjustedExecutionTimeFactor = calculateRPETFM(baseExecutionTimeFactor, ramUtilization, assignedRam, ramPerformanceWeight, steepnessFactor);
        } else {
            // RAM utilization exceeds assigned limits, use SPPETFM model
            adjustedExecutionTimeFactor = calculateSPPETFM(baseExecutionTimeFactor, ramPerformanceWeight, decayConstant, ramUtilization, assignedRam);
        }

        // Ensure that the adjusted execution time factor is within [0.1, 1]
        adjustedExecutionTimeFactor = Math.max(0.1, Math.min(1, adjustedExecutionTimeFactor));

        // Optional: Print for debugging
        // System.out.println(adjustedExecutionTimeFactor);
        return adjustedExecutionTimeFactor;
    }

    /**
     * RAM Performance Execution Time Factor Model (RPETFM)
     * Adjusts the execution time factor based on the logistic function for RAM utilization.
     * @param baseExecutionTimeFactor The baseline execution time factor (ETF_base)
     * @param ramUtilization The current RAM utilization (RAM_util)
     * @param assignedRam The assigned RAM capacity (RAM_assigned)
     * @param ramPerformanceWeight The weight factor for RAM performance impact (x)
     * @param steepnessFactor The steepness of the logistic curve (k)
     * @return The adjusted execution time factor for the RAM performance model in the range from 0.1 - 1.0
     */
    private double calculateRPETFM(double baseExecutionTimeFactor, double ramUtilization, double assignedRam, double ramPerformanceWeight, double steepnessFactor) {
        double logisticValue = logisticFunction(ramUtilization, assignedRam, steepnessFactor);
        return baseExecutionTimeFactor * (1 - ramPerformanceWeight * logisticValue);
    }

    /**
     * Swap Partition Performance Execution Time Factor Model (SPPETFM)
     * Applies an exponential decay penalty based on the swap partition usage.
     * @param baseExecutionTimeFactor The baseline execution time factor
     * @param ramPerformanceWeight The weight factor for RAM performance impact (x)
     * @param decayConstant The decay constant for exponential decay (k)
     * @param ramUtilization The current RAM utilization (RAM_util)
     * @param assignedRam The assigned RAM capacity (RAM_assigned)
     * @return The adjusted execution time factor for the swap partition model
     */
    private double calculateSPPETFM(double baseExecutionTimeFactor, double ramPerformanceWeight, double decayConstant, double ramUtilization, double assignedRam) {
        // Calculate ETFBaseline as starting position in SPPETFM
        // etfBasline is the Execution Time Factor of RPETFM(x) at RAMutil = RAMassigned
        double etfBaseline = calculateRPETFM(baseExecutionTimeFactor, assignedRam, assignedRam, ramPerformanceWeight, steepnessFactor);

        // Normalize RAM over-utilization from MB to GB
        double ramOverUtilization = (ramUtilization - assignedRam) / 1000;

        // Calculate the exponential decay
        double exponentialDecay = Math.exp(-decayConstant * ramOverUtilization);
        return etfBaseline * (1 - ramPerformanceWeight + ramPerformanceWeight * exponentialDecay);
    }

    /**
     * Logistic function used for the RPETFM model
     * @param ramUtilization The current RAM utilization (RAM_util)
     * @param assignedRam The assigned RAM capacity (RAM_assigned)
     * @param steepnessFactor The steepness of the logistic curve (k)
     * @return The result of the logistic function
     */
    private double logisticFunction(double ramUtilization, double assignedRam, double steepnessFactor) {
        return 1 / (1 + Math.exp(-steepnessFactor * (ramUtilization - assignedRam)));
    }
}
