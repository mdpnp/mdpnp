package org.mdpnp.transport;


public interface Wrapper {
    void tearDown();
    
    String DEVICE_PARTITION = "DEV";
    String CONTROLLER_PARTITION = "INC";
    
    public enum Role {
        Promiscuous(new String[] {DEVICE_PARTITION, CONTROLLER_PARTITION}, new String[] {DEVICE_PARTITION, CONTROLLER_PARTITION}),
        Controller(CONTROLLER_PARTITION, DEVICE_PARTITION),
        Device(DEVICE_PARTITION, CONTROLLER_PARTITION);
        
        private final String[] sendPartitions, receivePartitions;
        
        Role(String sendPartition, String receivePartition) {
            this.sendPartitions = new String[] {sendPartition};
            this.receivePartitions = new String[] {receivePartition};
        }
        Role(String[] sendPartitions, String[] receivePartitions) {
            this.sendPartitions = sendPartitions;
            this.receivePartitions = receivePartitions;
        }
        public String[] getSendPartitions() {
            return this.sendPartitions;
        }
        public String[] getReceivePartitions() {
            return receivePartitions;
        }
    }
}
