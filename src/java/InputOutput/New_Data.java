package InputOutput;

import Index.HyperPoint;

import java.util.LinkedList;

public class New_Data {
    long[] data_log;
    HyperPoint p;

    public New_Data(long[] data_log, HyperPoint p) {
        this.data_log = data_log;
        this.p = p;
    }

    public long[] getData_log() {
        return data_log;
    }

    public HyperPoint getP() {
        return p;
    }
}
