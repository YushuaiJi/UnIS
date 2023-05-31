package RMI;

public class TrainedRMI {
    double pivot;
    RMI rmi;

    public TrainedRMI(double pivot, RMI rmi) {
        this.pivot = pivot;
        this.rmi = rmi;
    }

    public double getPivot() {
        return pivot;
    }

    public RMI getRmi() {
        return rmi;
    }
}
