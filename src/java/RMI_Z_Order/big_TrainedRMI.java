package RMI_Z_Order;

import RMI.RMI;

import java.math.BigDecimal;
import java.math.BigInteger;

public class big_TrainedRMI {
    BigDecimal pivot;
    RMI rmi;

    public big_TrainedRMI(BigDecimal pivot, RMI rmi) {
        this.pivot = pivot;
        this.rmi = rmi;
    }

    public BigDecimal getPivot() {
        return pivot;
    }

    public RMI getRmi() {
        return rmi;
    }
}
