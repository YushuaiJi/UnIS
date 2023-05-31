package RMI_Z_Order;


import java.math.BigDecimal;
import java.math.BigInteger;

public class big_linear_model {
    BigDecimal slope; // Changed to BigInteger
    BigDecimal intercept; // Changed to BigInteger

    int Case;

    BigDecimal max;
    BigDecimal min;

    public big_linear_model(BigDecimal slope, BigDecimal intercept) { // Modified constructor
        this.slope = slope;
        this.intercept = intercept;
    }

    public int getCase() {
        return Case;
    }

    public void setCase(int aCase) {
        Case = aCase;
    }

    public big_linear_model() {
        this.slope = BigDecimal.ZERO; // Changed default values to BigInteger.ZERO
        this.intercept = BigDecimal.ZERO; // Changed default values to BigInteger.ZERO
    }

    public BigDecimal getSlope() { // Modified getter
        return slope;
    }

    public BigDecimal getIntercept() { // Modified getter
        return intercept;
    }

    @Override
    public String toString() {
        return "linear_model{" +
                "slope=" + slope +
                ", intercept=" + intercept +
                '}';
    }
}

