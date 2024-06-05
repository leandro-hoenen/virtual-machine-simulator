package org.example;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.utilizationmodels.UtilizationModelAbstract;
import org.cloudsimplus.vms.Vm;

public class DistributedUtilizationModel extends UtilizationModelAbstract {
    private final double ramUtil;
    private final double ramThresholdRate;
    private final double ramPlateauRate;
    private final double alpha; // Factor representing the influence of RAM utilization on performance improvement.
    private final double beta; // Factor representing the rate of performance degradation when RAM is over-utilized.
    private Cloudlet cloudlet;

    public DistributedUtilizationModel(double ramUtil, double alpha, double beta) {
        this.ramUtil = ramUtil;
        this.ramThresholdRate = 1.3; // Initial threshold set to 120% of ramAssigned
        this.ramPlateauRate = 1.5; // represents the maximum effective RAM beyond which additional RAM does not improve performance.
        this.alpha = alpha;
        this.beta = beta;
    }

    public void setCloudlet(Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }

    @Override
    protected double getUtilizationInternal(double v) {
        // RAM parameters
        Vm vm = this.cloudlet.getVm();
        double ramTotal = 4;
        double ramAssigned = vm.getRam().getCapacity();
        double execTimeBase = 1;
        double execTimeAdjusted;
        double ramThreshold = this.ramThresholdRate * ramAssigned;
        double ramPlateau = this.ramPlateauRate * ramUtil;

        // Adjust execution time based on RAM utilization
        if (ramUtil < ramAssigned && ramAssigned < ramPlateau) {
            execTimeAdjusted = Math.max(0.1, execTimeBase * (1 - alpha * (ramUtil / Math.min(ramAssigned, ramPlateau))));
        } else if (ramUtil <= ramThreshold && ramAssigned != ramUtil) {
            execTimeAdjusted = Math.max(0.1, execTimeBase);
        } else {
            execTimeAdjusted = Math.min(1, execTimeBase * (1 + beta * ((ramUtil - ramThreshold) / (ramTotal - ramThreshold))));
        }

        return execTimeAdjusted;
    }
}
