package RMI;

public class linear_model {
    double slope;
    double intercept;

    int Case;

    double max;

    double min;

    public linear_model(double slope, double intercept) {
        this.slope = slope;
        this.intercept = intercept;
    }

    public int getCase() {
        return Case;
    }

    public void setCase(int aCase) {
        Case = aCase;
    }

    public linear_model() {
        this.slope =0;
        this.intercept = 0;
    }

    public double getSlope() {
        return slope;
    }

    public double getIntercept() {
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
