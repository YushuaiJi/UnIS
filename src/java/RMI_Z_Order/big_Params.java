package RMI_Z_Order;

import RMI.linear_model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class big_Params {
    int batch_sz = 10;
    int fanout;
    float overallocation_ratio;
    float sampling_rate;
    int threshold;
    Vector<Integer> arch;

    public Vector<Integer> getArch() {
        return arch;
    }

    public big_Params(Vector<Integer> arch) {
        this.fanout = 100;
        this.overallocation_ratio = (float)1.1;
        this.sampling_rate = (float) 0.01;
        this.threshold = 100;
        this.arch = arch;
    }

    public big_Params(float sampling_rate, Vector<Integer> arch) {
        this.sampling_rate = sampling_rate;
        this.arch = arch;
    }

    public big_Params(int batch_sz, int fanout, float overallocation_ratio, float sampling_rate, int threshold, Vector<Integer> arch, ArrayList<LinkedList<linear_model>> model) {
        this.batch_sz = batch_sz;
        this.fanout = fanout;
        this.overallocation_ratio = overallocation_ratio;
        this.sampling_rate = sampling_rate;
        this.threshold = threshold;
        this.arch = arch;
    }

    public big_Params(int sample) {
        this.threshold = 100;
        this.sampling_rate = (float) (10000.0/(sample*1.0));
        Vector<Integer> default_arch = new Vector<>();
        default_arch.add(1);
        default_arch.add(100);
        this.arch = default_arch;
    }

    public big_Params() {
        this.threshold = 100;
        this.sampling_rate = (float) (0.0001);
        Vector<Integer> default_arch = new Vector<>();
        default_arch.add(1);
        default_arch.add(100);
        this.arch = default_arch;
    }
}
