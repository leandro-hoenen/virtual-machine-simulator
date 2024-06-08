package org.example.cloudsimulator;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.utilizationmodels.UtilizationModelAbstract;
import org.cloudsimplus.vms.Vm;

public class DistributedUtilizationModel extends UtilizationModelAbstract {
    private final double alpha; // Factor representing the influence of RAM utilization on performance improvement.
    private final double steepness; // Steepness of the logistic function
    private final double ramUtil;
    private Cloudlet cloudlet;

    public DistributedUtilizationModel(double ramUtil, double alpha, double steepness) {
        this.alpha = alpha;
        this.steepness = steepness;
        this.ramUtil = ramUtil;
    }

    public void setCloudlet(Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }

    @Override
    protected double getUtilizationInternal(double v) {
// RAM parameters
        Vm vm = this.cloudlet.getVm();
        double ramAssigned = vm.getRam().getCapacity();
        double execTimeBase = 1; // Implement this method to get the base execution time

        // Logistic function parameters
        double midpointImprovement = ramAssigned;

        // Adjust execution time based on RAM utilization
        double execTimeAdjusted;

        if (ramUtil <= ramAssigned) {
            execTimeAdjusted = execTimeBase * (1 - alpha * logisticFunction(ramUtil, midpointImprovement, steepness));
        } else {
            // simple punsihment for over-utilization
            execTimeAdjusted = 0.1;
        }

        // Ensure execTimeAdjusted is within [0.1, 1]
        execTimeAdjusted = Math.max(0.1, Math.min(1, execTimeAdjusted));

        System.out.println(execTimeAdjusted);
        return execTimeAdjusted;
    }

    private double logisticFunction(double x, double midpoint, double steepness) {
        return 1 / (1 + Math.exp(-steepness * (x - midpoint)));
    }

}
