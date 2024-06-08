package org.example;

import org.example.cloudsimulator.CloudSimRunner;
import py4j.GatewayServer;

public class CloudSimulatorEntry {

        private CloudSimRunner cloudSimRunner;

        public CloudSimulatorEntry() {
            cloudSimRunner = new CloudSimRunner();
        }

        public CloudSimRunner getCloudSimRunner() {
            return cloudSimRunner;
        }

        public static void main(String[] args) {
            GatewayServer gatewayServer = new GatewayServer(new CloudSimulatorEntry());
            gatewayServer.start();
            System.out.println("Gateway Server Started");
        }
}
