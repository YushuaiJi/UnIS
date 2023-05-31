package Index;

public class BallSpace {
    int k;
    double radius;


    public BallSpace(int k, double radius) {
        this.k = k;
        this.radius = radius;
    }

    public int getK() {
        return k;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean contains(HyperSpace hs){
        return false;
    }

    @Override
    public String toString() {
        return "BallSpace{" +
                "k=" + k +
                ", radius=" + radius +
                '}';
    }
}
