package Index;

import RMI.Params;
import RMI.*;

import java.math.BigDecimal;
import java.util.*;

import static MedianofMedian.MedianofMedian.check;
import static RMI.RMI.mofm_rmi;


public class AutoIS {
    IndexNode root;
    int K = 2;
    double RANGE = 1.0;
    // HyperPoint min, max are determined the range of KDTree Space
    HyperPoint min, max;
    int capacity = 50;

    Params pa = new Params();

    public IndexNode getRoot() {
        return root;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setPa(Params pa) {
        this.pa = pa;
    }

    public AutoIS(int K) {
        this.K = K;
        root = null;
        double[] vals = new double[K];
        min = new HyperPoint(vals);
        for (int i = 0; i < K; i++)
            vals[i] = RANGE;
        max = new HyperPoint(vals);
    }

    public AutoIS(int K, HyperPoint min, HyperPoint max) {
        this.K = K;
        this.min = min;
        this.max = max;
        root = null;
    }

    private static int partition(HyperPoint[] points, int k, int beg, int end) {
        HyperPoint pivot = points[beg];
        int i = beg, j = end + 1;
        while (true) {
            while (++i <= end && points[i].coords[k] < pivot.coords[k])
                ;
            while (--j > beg && points[j].coords[k] >= pivot.coords[k])
                ;
            if (i < j) {
                HyperPoint temp = points[i];
                points[i] = points[j];
                points[j] = temp;
            } else
                break;
        }
        points[beg] = points[j];
        points[j] = pivot;
        return j;
    }

    public void ConstructionAutoIS(HyperPoint[] points,int model) {
        int num = points.length;
        HyperPoint hmin = new HyperPoint(min);
        HyperPoint hmax = new HyperPoint(max);
        switch(model){
            case 1:
                root = insertByMedianFinding(root, points, hmin, hmax, 0, 0, num - 1);break;
            case 2:
                root = insertByMedianFindingLearnedIndex(root, points, hmin, hmax, 0, 0, num - 1);break;
            case 3:
                root = insertByAvgFinding(root, points, hmin, hmax,  0, 0,num-1);break;
            case 4:
                root = insertBySampling(root, points, hmin, hmax,  0, 0,num-1);break;
            case 5:
                root = insertByMedianofMedian(root, points, hmin, hmax,  0, 0,num-1);break;
            case 6:
                root = insertUnbalance(root, points, hmin, hmax,  0, num - 1,0);break;
            case 7:
                root = insertByMedianFindingLearnedIndex_VAR(root, points, hmin, hmax, 0, 0, num - 1);break;
       }
    }

    public long ConstructionAutoIS_memory(HyperPoint[] points,int model) {
        long[] memory = new long[1];
        memory[0] = 0;
        int num = points.length;
        HyperPoint hmin = new HyperPoint(min);
        HyperPoint hmax = new HyperPoint(max);
        switch(model){
            case 1:
                root = insertByMedianFinding(root, points, hmin, hmax, 0, 0, num - 1);break;
            case 2:
                root = insertByMedianFindingLearnedIndex_memory(root, points, hmin, hmax, 0, 0, num - 1,memory);break;
            case 3:
                root = insertByAvgFinding(root, points, hmin, hmax,  0, 0,num-1);break;
            case 4:
                root = insertBySampling(root, points, hmin, hmax,  0, 0,num-1);break;
            case 5:
                root = insertByMedianofMedian(root, points, hmin, hmax,  0, 0,num-1);break;
            case 6:
                root = insertUnbalance(root, points, hmin, hmax,  0, num - 1,0);break;
            case 7:
                root = insertByMedianFindingLearnedIndex_VAR(root, points, hmin, hmax, 0, 0, num - 1);break;
        }
        return memory[0];
    }

    public static double Med_test(HyperPoint[] points,int k,int model) {
        double res = 0;
        switch(model){
            case 1:
                TrainedRMI train_rmi = mofm_rmi(points,0,points.length-1,k);
                res = train_rmi.getPivot();break;
            case 2:
                res = findMedian(points, k, 0, points.length-1) ;break;
            case 3:
                res = points[check(points,0,points.length-1,(points.length-1)/2,k)].getcoords()[k];break;
            case 4:
                res = insertionbysampling(points,k,0, points.length-1);break;
        }
        return res;
    }

    public static double MedAndPartition(HyperPoint[] points,int k,int model) {
        double res = 0;
        switch(model){
            case 1:
                TrainedRMI train_rmi = mofm_rmi(points,0,points.length-1,k);
                res = train_rmi.getPivot();
                divide(points,res,0,points.length-1,k);break;
            case 2:
                res = findMedian(points, k, 0, points.length-1) ;
            case 3:
                res = points[check(points,0,points.length-1,(points.length-1)/2,k)].getcoords()[k];
                divide(points,res,0,points.length-1,k);break;
            case 4:
                res = insertionbysampling(points,k,0, points.length-1);
                divide(points,res,0,points.length-1,k);break;
        }
        return res;
    }

    public static int findMedian(HyperPoint[] points, int k, int beg, int end) {
        if (beg > end)
            return -1;
        else if (beg == end)
            return beg;
        int mid = (beg + end) / 2;
        int i = beg, j = end;
        while (true) {
            int t = partition(points, k, i, j);
            if (t == mid)
                return t;
            else if (t > mid)
                j = t - 1;
            else
                i = t + 1;
        }
    }

    public static double quickSort(HyperPoint[] arr, int begin, int end,int k) {
        if (begin < end) {
            int partitionIndex = partition_sort(arr, begin, end,k);

            quickSort(arr, begin, partitionIndex-1,k);
            quickSort(arr, partitionIndex+1, end,k);
        }
        return arr[arr.length/2].getcoords()[k];
    }

    private static int partition_sort(HyperPoint[] arr, int begin, int end,int k) {
        HyperPoint pivot = arr[end];
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (arr[j].getcoords()[k] <= pivot.getcoords()[k]) {
                i++;

                HyperPoint swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        HyperPoint swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;

        return i+1;
    }

    public static int divide(HyperPoint[] arr, double target, int l, int r,int k) {
        int left = l;
        int cur = l;
        int right = r;
        int anc = l;
        while (cur <= right) {
            if (arr[cur].getcoords()[k] < target) { // cur比目标数小，把这个小点儿的数换到维护区间的最左边，也就是left的位置。
                if (cur != left) { //如果当前没有维护和目标数7相等的区间，就不用交换，此时left和cur位置一样。
                    swap(arr, left, cur);
                }
                cur++;
                left++;
                anc++;
            } else if (arr[cur].getcoords()[k] == target) { // 和目标数相等，直接比较下一个
                cur++;
                anc++;
            }else { //cur比目标数大，就把大的数置换到数组后面，判断置换过来的新的数是大、小还是相等
                swap(arr, cur, right);
                right--;
            }
        }//1 2 5 6 7    5.5
        //if(anc == l+1) return anc;
        //if(anc == r+1) return anc-2;
        return anc - 1;
    }

    public static void swap(HyperPoint[] points, int left, int right){
        HyperPoint temp = points[left];
        points[left]= points[right];
        points[right]= temp;
    }

    private IndexNode insertByMedianFinding(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if(j-i+1 <= capacity){
            IndexNode node = null;
            if(i > j) node = new IndexNode(new HyperSpace(hmin, hmax), points[j]);
            else node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }
        int k = depth % K;

        int t = findMedian(points, k, i, j);
        HyperPoint p = points[t];


        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax),p);
        double pivot = p.coords[k];
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = p.coords[k];
        r.left = insertByMedianFinding(r.left, points, hmin, hmid1, depth + 1, i, t);

        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        r.right = insertByMedianFinding(r.right, points, hmid2, hmax, depth + 1, t+1 , j);
        r.Node_count = j-i+1;
        r.pivot = pivot;
        return r;
    }

    private IndexNode insertByMedianFinding_memory(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j,long[] memory) {
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if(j-i+1 <= capacity){
            IndexNode node = null;
            if(i > j) node = new IndexNode(new HyperSpace(hmin, hmax), points[j]);
            else node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }
        int k = depth % K;

        int t = findMedian(points, k, i, j);
        HyperPoint p = points[t];


        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax),p);
        double pivot = p.coords[k];
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = p.coords[k];

        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if(endMemory > initialMemory){
            memory[0] += endMemory-initialMemory;
        }

        r.left = insertByMedianFinding(r.left, points, hmin, hmid1, depth + 1, i, t);

        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        r.right = insertByMedianFinding(r.right, points, hmid2, hmax, depth + 1, t+1 , j);
        r.Node_count = j-i+1;
        r.pivot = pivot;
        return r;
    }
    private IndexNode insertByMedianFinding_VAR(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {

        if(j-i+1 <= capacity){
            IndexNode node = null;
            if(i > j) node = new IndexNode(new HyperSpace(hmin, hmax), points[j]);
            else node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }

        int k= 0;
        int max_d = 0;
        for(int d=0;d<K;d++){
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for(int anchor = i;anchor<j;anchor++){
                min= Math.min(min,points[anchor].getcoords()[d]);
                max= Math.max(max,points[anchor].getcoords()[d]);
            }
            if(max-min > max_d) k=d;
        }

        int t = findMedian(points, k, i, j);
        HyperPoint p = points[t];


        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax),p);
        double pivot = p.coords[k];
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = p.coords[k];
        r.left = insertByMedianFinding(r.left, points, hmin, hmid1, depth + 1, i, t);

        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        r.right = insertByMedianFinding(r.right, points, hmid2, hmax, depth + 1, t+1 , j);
        r.Node_count = j-i+1;
        r.pivot = pivot;
        return r;
    }

    private IndexNode insertByMedianFindingLearnedIndex(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if(j-i+1 <= capacity){
            IndexNode node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }
        int k = depth % K;
        int sample_size = j-i+1;
        HyperPoint p = null;
        int t = 0;
        //Params par = new Params();

        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        r.hasModel = true;
        TrainedRMI train_rmi = mofm_rmi(points,i,j,k);
        double pivot = train_rmi.getPivot();
        t = divide(points,pivot,i,j,k);
        p = points[t];
        r.setModel(train_rmi.getRmi().getModel());
        r.setP(p);
        pivot = p.coords[k];
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = p.coords[k];
        if((t-1-i+1) < 10000) r.left = insertByMedianFinding(r.left, points, hmin, hmid1, depth + 1, i, t );
        else r.left = insertByMedianFindingLearnedIndex(r.left, points, hmin, hmid1, depth + 1, i, t );
        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        if(((j-t-1+1) <10000)) r.right = insertByMedianFinding(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        else r.right = insertByMedianFindingLearnedIndex(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        r.Node_count = j-i+1;
        return r;
    }
    private IndexNode insertByMedianFindingLearnedIndex_memory(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j,long[] memory) {
        long initialMemory = Runtime.getRuntime().freeMemory();
        if(j-i+1 <= capacity){
            IndexNode node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }
        int k = depth % K;
        int sample_size = j-i+1;
        HyperPoint p = null;
        int t = 0;
        //Params par = new Params();

        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        r.hasModel = true;
        TrainedRMI train_rmi = mofm_rmi(points,i,j,k);
        double pivot = train_rmi.getPivot();
        t = divide(points,pivot,i,j,k);
        p = points[t];
        r.setModel(train_rmi.getRmi().getModel());
        r.setP(p);
        pivot = p.coords[k];
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = p.coords[k];

        long endMemory = Runtime.getRuntime().freeMemory();
        if(endMemory < initialMemory){
            memory[0] += initialMemory-endMemory;
        }

        if((t-1-i+1) < 10000) r.left = insertByMedianFinding_memory(r.left, points, hmin, hmid1, depth + 1, i, t,memory );
        else r.left = insertByMedianFindingLearnedIndex_memory(r.left, points, hmin, hmid1, depth + 1, i, t,memory );
        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        if(((j-t-1+1) <10000)) r.right = insertByMedianFinding_memory(r.right, points, hmid2, hmax, depth + 1, t + 1, j,memory);
        else r.right = insertByMedianFindingLearnedIndex_memory(r.right, points, hmid2, hmax, depth + 1, t + 1, j,memory);
        r.Node_count = j-i+1;
        return r;
    }

    private IndexNode insertByMedianFindingLearnedIndex_VAR(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if(j-i+1 <= capacity){
            IndexNode node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }

        int k= 0;
        int max_d = 0;
        for(int d=0;d<K;d++){
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for(int anchor = i;anchor<j;anchor++){
                min= Math.min(min,points[anchor].getcoords()[d]);
                max= Math.max(max,points[anchor].getcoords()[d]);
            }
            if(max-min > max_d) k=d;
        }

        int sample_size = j-i+1;
        HyperPoint p = null;
        int t = 0;
        //Params par = new Params();

        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        r.hasModel = true;
        TrainedRMI train_rmi = mofm_rmi(points,i,j,k);
        double pivot = train_rmi.getPivot();
        t = divide(points,pivot,i,j,k);
        p = points[t];
        r.setModel(train_rmi.getRmi().getModel());
        r.setP(p);
        pivot = p.coords[k];
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = p.coords[k];
        if((t-1-i+1) < 10000) r.left = insertByMedianFinding_VAR(r.left, points, hmin, hmid1, depth + 1, i, t );
        else r.left = insertByMedianFindingLearnedIndex_VAR(r.left, points, hmin, hmid1, depth + 1, i, t );
        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        if(((j-t-1+1) <10000)) r.right = insertByMedianFinding_VAR(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        else r.right = insertByMedianFindingLearnedIndex_VAR(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        r.Node_count = j-i+1;
        return r;
    }
    private IndexNode insertBySampling(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        //if (i > j) return null;
        //else if (i == j) return new IndexNode(new HyperSpace(hmin, hmax), points[i]);
        if(j-i+1 <= capacity){
            IndexNode node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }
        int k = depth % K;

        HyperPoint p = null;
        double p_val = 0;

        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        p_val = insertionbysampling(points,k,i,j);
        int t = divide(points,p_val,i,j,k);
        p = points[t];
        r.setP(p);
        double pivot = p.coords[k];
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = p.coords[k];
        if((t-1-i+1) < 2000) r.left = insertByMedianFinding(r.left, points, hmin, hmid1, depth + 1, i, t );
        else r.left = insertBySampling(r.left, points, hmin, hmid1, depth + 1, i, t );
        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        if(((j-t-1+1) <2000)) r.right = insertByMedianFinding(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        else r.right = insertBySampling(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        r.Node_count = j-i+1;
        return r;
    }

    public static double insertionbysampling(HyperPoint[] arr,int k,int beg, int end) {
        int len = (end-beg+1)/5+1;
        double[] tmp_arr = new double[len];
        int l = end-beg+1;
        for(int i=0;i<len;i+=1){
            int loc = (int)(Math.random()*l)+beg;
            tmp_arr[i] = arr[loc].getcoords()[k];
        }
        Arrays.sort(tmp_arr);
        return tmp_arr[tmp_arr.length/2];
    }
    private IndexNode insertByMedianofMedian(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        //if (i > j) return null;
        //else if (i == j) return new IndexNode(new HyperSpace(hmin, hmax), points[i]);
        if(j-i+1 <= capacity){
            IndexNode node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }
        int k = depth % K;

        HyperPoint p = null;
        double p_val = 0;

        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        p_val = points[check(points,i,j,(j-i)/2,k)].getcoords()[k];
        int t = divide(points,p_val,i,j,k);
        p = points[t];
        r.setP(p);
        double pivot = p.coords[k];
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = p.coords[k];
        if((t-1-i+1) < 1000) r.left = insertByMedianFinding(r.left, points, hmin, hmid1, depth + 1, i, t );
        else r.left = insertByMedianofMedian(r.left, points, hmin, hmid1, depth + 1, i, t );
        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        if(((j-t-1+1) <1000)) r.right = insertByMedianFinding(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        else r.right = insertByMedianofMedian(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        r.Node_count = j-i+1;
        return r;
    }
    private IndexNode insertByAvgFinding(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if(j-i<0) return null;
        if(j-i+1 <= capacity){
            IndexNode node = new IndexNode(new HyperSpace(hmin, hmax), points[i]);
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        };
        int k = depth % K;
        double sum = 0;
        for(int anchor = i;anchor<=j;anchor++){
            sum = sum+points[anchor].getcoords()[k];
        }
        double avg = sum/(j-i+1);

        int t = divide(points,avg,i,j,k);
        HyperPoint p = points[t];
        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax),p);
        double pivot = avg;
        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[k] = avg;
        r.left = insertByAvgFinding(r.left, points, hmin, hmid1, depth + 1, i, t);

        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[k] = pivot;
        r.right = insertByAvgFinding(r.right, points, hmid2, hmax, depth + 1, t+1 , j);
        r.Node_count = j-i+1;
        r.pivot = pivot;
        return r;
    }

    private IndexNode insertUnbalance(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int i, int j,int depth) {
        if(j-i+1 <= capacity){
            IndexNode node = new IndexNode(new HyperSpace(hmin, hmax),points[i]);
            if(j-i+1<=0) return node;
            node.Node_count = j-i+1;
            node.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[j-i+1];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            node.hp = tmp_hp;
            return node;
        }
        int d = depth % K;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for(int anchor = i;anchor<j;anchor++){
            min= Math.min(min,points[anchor].getcoords()[d]);
            max = Math.max(max,points[anchor].getcoords()[d]);
        }
        double sum = addDouble(min,max);
        double pivot_val = divide(sum,2);
        int t = divide(points,pivot_val,i,j,d);
        HyperPoint p = points[t];
        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax),p);

        HyperPoint hmid1 = new HyperPoint(hmax);
        hmid1.coords[d] = pivot_val;
        //System.out.print("r val: "+pivot_val+"\n");
        //System.out.print("i: "+i+" t: "+t+"r: "+j+"\n");
        if((t-1-i+1) < 10000) r.left = insertByMedianFinding(r.left, points, hmin, hmid1, depth + 1, i, t );
        else r.left = insertUnbalance(r.left, points, hmin, hmid1, i, t,depth+1);
        HyperPoint hmid2 = new HyperPoint(hmin);
        hmid2.coords[d] = pivot_val;
        if(((j-t-1+1) <10000)) r.right = insertByMedianFinding(r.right, points, hmid2, hmax, depth + 1, t + 1, j);
        else r.right = insertUnbalance(r.right, points, hmid2, hmax, t+1 , j,depth+1);
        r.Node_count = j-i+1;
        r.pivot = pivot_val;
        return r;
    }


    class SortComparator implements Comparator<HyperPoint> {
        int k;

        public void setK(int k) {
            this.k = k;
        }

        @Override
        public int compare(HyperPoint o1, HyperPoint o2) {
            if (o1.coords[k] > o2.coords[k])
                return 1;
            else if (o1.coords[k] == o2.coords[k])
                return 0;
            return -1;
        }
    }

    public IndexNode insertByPreSort(HyperPoint[] points,HyperPoint hmin, HyperPoint hmax) {
        int num = points.length;
        // k presort points set
        HyperPoint[][] kpoints = new HyperPoint[K][];
        SortComparator sc = new SortComparator();
        // Presort
        for (int k = 0; k < K; k++) {
            sc.setK(k);
            Arrays.sort(points, sc);
            kpoints[k] = points.clone();
        }
        Vector<HyperPoint> avails = new Vector<HyperPoint>();
        for (int i = 0; i < num; i++)
            avails.add(kpoints[0][i]);
        root = insertByPreSort(root, kpoints, hmin, hmax, 0, avails);
        return root;
    }

    private IndexNode insertByPreSort(IndexNode r, HyperPoint[][] kpoints, HyperPoint hmin, HyperPoint hmax, int depth, Vector<HyperPoint> avails) {
        int num = avails.size();
        if (num == 0)
            return null;
        else {
            int k = depth % K;
            if (num == 1)
                return new IndexNode(new HyperSpace(hmin, hmax), avails.get(0));
            int mid = (num - 1) / 2;
            if (r == null)
                r = new IndexNode(new HyperSpace(hmin, hmax), avails.get(mid));
            HyperPoint hmid1 = new HyperPoint(hmax);
            hmid1.coords[k] = kpoints[k][mid].coords[k];
            // Splitting current points set
            HashMap<HyperPoint, Integer> split = new HashMap<HyperPoint, Integer>();
            for (int p = 0; p < num; p++)
                if (p < mid)
                    split.put(avails.get(p), 0);
                else if (p > mid)
                    split.put(avails.get(p), 1);
            int k1 = (depth + 1) % K;
            // Generating left and right branch available points set
            Vector<HyperPoint> left = new Vector<HyperPoint>(), right = new Vector<HyperPoint>();
            for (HyperPoint p : kpoints[k1])
                if (split.containsKey(p))
                    if (split.get(p) == 0)
                        left.addElement(p);
                    else
                        right.addElement(p);
            // Recursive Split
            r.left = insertByPreSort(r.left, kpoints, hmin, hmid1, depth + 1, left);
            HyperPoint hmid2 = new HyperPoint(hmin);
            hmid1.coords[k] = kpoints[k][mid].coords[k];
            r.right = insertByPreSort(r.right, kpoints, hmid2, hmax, depth + 1, right);
            return r;
        }
    }
    public static double divide(double num, double total) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(num));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(total));
        return bigDecimal1.divide(bigDecimal2, 15, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double addDouble(double number1 , double number2) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(number1));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(number2));
        return bigDecimal1.add(bigDecimal2).doubleValue();
    }

    public void rangeQuery_DFS_MBR(IndexNode r, HyperSpace hs, Set<HyperPoint> res) {
        // If current node r is null or doesn't intersect with hs, then return
        if (r == null ||!r.hs.intersects(hs)) return;
        if(r.isleaf){
            for(int i=0;i<r.hp.length;i++){
                if (hs.contains(r.hp[i])) {
                    res.add(r.hp[i]);
                }
            }
            return;
        }
        // recursively check the left, right branch of current node
        rangeQuery_DFS_MBR(r.left, hs, res);
        rangeQuery_DFS_MBR(r.right, hs, res);
    }

    public static void rangeQuery_DFS_MBB(IndexNode r, HyperSpace hs, Set<HyperPoint> res) {
        double mindis = MINDIST(hs,r.p);
        if (r == null || mindis > r.bs.radius) return;
        if(r.isleaf){
            for(int i=0;i<r.hp.length;i++){
                if (hs.contains(r.hp[i])) {
                    res.add(r.hp[i]);
                }
            }
            return;
        }
        // recursively check the left, right branch of current node
        rangeQuery_DFS_MBB(r.left, hs, res);
        rangeQuery_DFS_MBB(r.right, hs, res);
    }

    public static double MINDIST(HyperSpace hs, HyperPoint p) {
        if (hs.K != p.K)
            throw new IllegalArgumentException("");
        double res = 0;
        HyperPoint min = hs.min;
        HyperPoint max = hs.max;
        for (int i = 0; i < p.K; i++) {
            if (p.coords[i] < min.coords[i]) res += (p.coords[i] - min.coords[i]) * (p.coords[i] - min.coords[i]);
            else if(p.coords[i] > max.coords[i]) res += (p.coords[i] - max.coords[i]) * (p.coords[i] - max.coords[i]);
        }
        double dis = Math.sqrt(res);
        return dis;
    }


    public void rangeQuery_BFS_MBR(Set<HyperPoint> res, HyperSpace target) {
        Queue<IndexNode> heap = new LinkedList<IndexNode>();
        heap.add(root);
        while (!heap.isEmpty()) {//&& anchor
            IndexNode temp = heap.poll();
            HyperSpace hs = temp.hs;
            if(temp != null && hs.intersects(target)){
                if(!temp.isleaf){
                    if (temp.left != null) {
                        heap.add(temp.left);
                    }
                    if (temp.right != null) {
                        heap.add(temp.right);
                    }
                }else{
                    for(int i=0;i<temp.hp.length;i++){
                        if (target.contains(temp.hp[i])) {
                            res.add(temp.hp[i]);
                        }
                    }
                }
            }
        }
    }


    public void rangeQuery_BFS_MBB(Set<HyperPoint> res, HyperSpace target) {
        Queue<IndexNode> heap = new LinkedList<IndexNode>();
        heap.add(root);
        while (!heap.isEmpty()) {//&& anchor
            IndexNode temp = heap.poll();
            double mindis = MINDIST(target,temp.p);
            if(temp.equals(null)) continue;
            if(mindis <= temp.bs.radius){
                if(!temp.isleaf){
                    if (temp.left != null) {
                        heap.add(temp.left);
                    }
                    if (temp.right != null) {
                        heap.add(temp.right);
                    }
                }else{
                    for(int i=0;i<temp.hp.length;i++){
                        if (target.contains(temp.hp[i])) {
                            res.add(temp.hp[i]);
                        }
                    }
                }
            }
        }
    }


    public BoundedPQueue kNN(HyperPoint pointKD, int K, int model){
        BoundedPQueue k_nearest_points = new BoundedPQueue(K);

        switch(model){
            case 1:
                //dfs mbr
                get_all_nearest_points(pointKD,k_nearest_points,root,0);break;
            case 2:
                //dfs mbb
                get_all_nearest_points_ball(pointKD,k_nearest_points,root,0);break;
            case 3:
                //bfs mbr
                get_all_nearest_points_BFS_MBR(pointKD,root,k_nearest_points,K);break;
            case 4:
                //bfs mbb
                get_all_nearest_points_BFS_MBB(pointKD,root,k_nearest_points,K);break;
        }
        return k_nearest_points;
    }


    private void get_all_nearest_points(HyperPoint pointKD, BoundedPQueue knp, IndexNode curr,int depth){
        if(curr == null){
            return;
        }
        if(curr.isleaf){
            for(int i=0;i<curr.hp.length;i++){
                double distance = pointKD.squareDistanceTo(curr.hp[i]);
                knp.enqueue(distance, curr.hp[i]);
            }
            return;
        }
        double value = Double.MAX_VALUE;
        if(!knp.isEmpty()){
            value = knp.worst();
        }
        int keyIndex = depth % K;
        //boolean to_left = false;// do I need to go further side, consider sheng wang kNN
        boolean near_is_left = false;
        boolean near_is_right = false;
        if(pointKD.coords[keyIndex] < curr.pivot){
            get_all_nearest_points(pointKD, knp, curr.left,depth+1);
            near_is_left = true;
        }else{
            //to_left = true;
            get_all_nearest_points(pointKD, knp, curr.right,depth+1);
            near_is_right = true;
        }

        if(knp.Size()!= knp.maxSize()||(curr.p.coords[keyIndex] - pointKD.coords[keyIndex])
                *(curr.p.coords[keyIndex] - pointKD.coords[keyIndex]) < value){
            if(!near_is_left) get_all_nearest_points(pointKD, knp, curr.left,depth+1);
            else get_all_nearest_points(pointKD, knp, curr.right,depth+1);
        }
    }


    private void get_all_nearest_points_ball(HyperPoint pointKD, BoundedPQueue knp, IndexNode curr,int depth){
        if(curr == null){
            return;
        }
        if(curr.isleaf){
            for(int i=0;i<curr.hp.length;i++){
                double distance = pointKD.squareDistanceTo(curr.hp[i]);
                knp.enqueue(distance, curr.hp[i]);
            }
            return;
        }
        double value = Double.MAX_VALUE;
        if(!knp.isEmpty()){
            value = knp.worst();
        }

        int keyIndex = depth % K;
        //boolean to_left = false;// do I need to go further side, consider sheng wang kNN
        boolean near_is_left = false;
        boolean near_is_right = false;
        if(pointKD.coords[keyIndex] < curr.pivot){
            get_all_nearest_points_ball(pointKD, knp, curr.left,depth+1);
            near_is_left = true;
        }else{
            //to_left = true;
            get_all_nearest_points_ball(pointKD, knp, curr.right,depth+1);
            near_is_right = true;
        }
        if(knp.Size()!= knp.maxSize()|| curr.bs.radius <= value){
            if(!near_is_left) get_all_nearest_points_ball(pointKD, knp, curr.left,depth+1);
            else get_all_nearest_points_ball(pointKD, knp, curr.right,depth+1);
        }
    }

    public void setBallSpace(IndexNode root){
        BallSpace bs = new BallSpace(root.hs.K,0);
        root.bs = bs;
        radCal(root,root);
        if(root.isleaf) {
            return;
        }
        setBallSpace(root.left);
        setBallSpace(root.right);
    }
    public void radCal(IndexNode pivot,IndexNode node){
        if(node.isleaf){
            for(int i=0;i<node.hp.length;i++){
                double dis = pivot.p.distanceTo(node.hp[i]);
                if(pivot.bs.radius < dis) {
                    pivot.bs.setRadius(dis);
                }
            }
            return;
        }
        radCal(pivot,node.left);
        radCal(pivot,node.right);
    }

    private void get_all_nearest_points_BFS_MBR(HyperPoint pointKD,IndexNode root, BoundedPQueue knp,int n) {
        Queue<IndexNode> queue = new LinkedList<IndexNode>();
        BoundedPQueue heap = new BoundedPQueue(n);
        queue.add(root);
        while (!queue.isEmpty()) {//&& anchor
            IndexNode temp = queue.poll();
            if(!temp.isleaf){
                // Recursively search the half of the tree that contains the test point.
                if(heap.Size()== heap.maxSize()){
                    double Mindist = MINDIST(temp.hs,pointKD);
                    double value = heap.worst();
                    if(Mindist <= value){
                        if (temp.left != null) {
                            queue.add(temp.left);
                        }
                        /*add right right child to the queue */
                        if (temp.right != null) {
                            queue.add(temp.right);
                        }
                    }
                    heap.enqueue(Mindist,heap);
                }else{
                    if (temp.left != null) {
                        queue.add(temp.left);
                    }
                    /*add right right child to the queue */
                    if (temp.right != null) {
                        queue.add(temp.right);
                    }
                }
            }else{
                for(int i=0;i<temp.hp.length;i++){
                    double distance = pointKD.distanceTo(temp.hp[i]);
                    heap.enqueue(distance,temp.hp[i]);
                    knp.enqueue(distance, temp.hp[i]);
                }
            }
        }
    }

    private void get_all_nearest_points_BFS_MBB(HyperPoint pointKD,IndexNode root, BoundedPQueue knp,int n) {
        Queue<IndexNode> queue = new LinkedList<IndexNode>();
        BoundedPQueue heap = new BoundedPQueue(n);
        queue.add(root);
        while (!queue.isEmpty()) {//&& anchor
            IndexNode temp = queue.poll();
            if(!temp.isleaf){
                // Recursively search the half of the tree that contains the test point.
                if(heap.Size()== heap.maxSize()){
                    double distance = Math.abs(pointKD.distanceTo(temp.p)-temp.bs.radius);
                    double value = heap.worst();
                    if(distance<= value){//distance <= value+temp.bs.radius
                        if (temp.left != null) {
                            queue.add(temp.left);
                        }
                        /*add right right child to the queue */
                        if (temp.right != null) {
                            queue.add(temp.right);
                        }
                    }
                    heap.enqueue(distance,heap);
                }else{
                    if (temp.left != null) {
                        queue.add(temp.left);
                    }
                    /*add right right child to the queue */
                    if (temp.right != null) {
                        queue.add(temp.right);
                    }
                }
            }else{
                for(int i=0;i<temp.hp.length;i++){
                    double distance = pointKD.squareDistanceTo(temp.hp[i]);
                    heap.enqueue(distance,temp.hp[i]);
                    knp.enqueue(distance, temp.hp[i]);
                }
            }
        }
    }

    public static int maxDepth(IndexNode root) {
        return root == null ? 0 : Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }

    public static long Hp_memory(IndexNode node){
        if(node.p == null) System.out.print("1111111"+"\n");
        return node.p.K*8+4;
    }

    public static long Hs_memory(IndexNode node){
        return Hp_memory(node)*2+4;
    }

    public static long Linear_model_memory(IndexNode node){
        if(node.model.length == 0) return 0;
        else return 16*node.model.length;
    }

    public static long IndexNode_memory(IndexNode node){
        if(node.hasModel) return Hs_memory(node)*2+Hp_memory(node)+Linear_model_memory(node)+12+1;
        else return (Hs_memory(node)*2+Hp_memory(node)+12+1);
    }

    public static double divide(long num, long total) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(num));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(total));
        //System.out.print(bigDecimal1+" ");
        //System.out.print(bigDecimal2+" ");
        //System.out.print(bigDecimal1.divide(bigDecimal2, 20, BigDecimal.ROUND_HALF_UP).longValue()+"\n");
        return bigDecimal1.divide(bigDecimal2, 20, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static long Memory(IndexNode node){
        long[] res = new long[1];
        memorycost(node,res);
        return res[0];
    }

    public static void memorycost(IndexNode node,long[] res){
        if(node == null) return;
        res[0] =  res[0]+IndexNode_memory(node);

        if(node.isleaf) {
            return;
        }
        memorycost(node.left,res);
        memorycost(node.right,res);
        return;
    }

    public int[] Mark_location(HyperPoint p) {
        int[] res= new int[2];
        left_right_count(root, p,0,res);
        return res;
    }

    public  void left_right_count(IndexNode r, HyperPoint p,int k,int[] res) {
        if (r.isleaf) return;
        k = k % K;
        if(p.getcoords()[k] < r.pivot){
            res[0] = res[0]+1;
            left_right_count(r.left, p,k+1,res);
        }else{
            res[1] = res[1]+1;
            left_right_count(r.right, p,k+1,res);
        }
    }

    public int leaf_count() {
        int[] res= new int[1];
        l_count(root, res);
        return res[0];
    }

    public  void l_count(IndexNode r, int[] res) {
        if (r.isleaf) {
            res[0] =res[0]+1;
            return;
        }
        l_count(r.left,res);
        l_count(r.right,res);
    }

    public int Imbalanced_tree() {
        return Math.abs(maxDepth(root.left)-maxDepth(root.right));
    }

    public double CDF_SUM(HyperPoint p) {
        double[] res= new double[2];
        cdf_sum(root, p,0,res,p.K);
        //return divide(res[0],res[1]);
        //System.out.print(res[0]+" "+res[1]+"\n");
        return res[0]/res[1];
    }

    public void cdf_sum(IndexNode r, HyperPoint p,int k,double[] res,int dim) {
        if (r.isleaf) return;
        double key = p.getcoords()[k];
        k = k % dim;
        if(p.getcoords()[k] < r.pivot){
            if(r.hasModel){
                res[1] =res[1]+1;
                int num_model = r.model.length-1;//是对的
                double root_slope = r.model[0].getSlope();
                double root_intercept = r.model[0].getIntercept();
                int pre_model_idx = (int)Math.max(0, Math.min(num_model-1,root_slope*key+root_intercept));
                double pred_cdf = r.model[pre_model_idx].getSlope()*key+r.model[pre_model_idx].getIntercept();
                res[0] =res[0]+ pred_cdf;
                return;
            }
            cdf_sum(r.left, p,k+1,res,dim);
        }else{
            if(r.hasModel){
                res[1] =res[1]+1;
                double root_slope = r.model[0].getSlope();
                int num_model = r.model.length-1;//是对的
                double root_intercept = r.model[0].getIntercept();
                int pre_model_idx = (int)Math.max(0, Math.min(num_model-1,root_slope*key+root_intercept));
                double pred_cdf = r.model[pre_model_idx].getSlope()*key+r.model[pre_model_idx].getIntercept();
                res[0] =res[0]+ pred_cdf;
                return;
            }
            cdf_sum(r.right, p,k+1,res,dim);
        }
    }

    public static linear_model[][] linear_model_Set(HyperPoint[] hp){
        Params p = new Params();
        linear_model[][] model = new linear_model[hp[0].getK()][p.getArch().get(1)+p.getArch().get(0)];
        for(int i=0;i<hp[0].getK();i++){
            TrainedRMI rmi  = mofm_rmi(hp,0,hp.length-1,i);
            model[i] = rmi.getRmi().getModel();
        }
        return model;
    }

    public static double[] Cal_CDF(linear_model[][] model,HyperPoint p){
        double[] cdf = new double[p.getK()];
        for(int i=0;i<p.getK();i++){
            double key = p.getcoords()[i];
            int num_model = model[i].length-1;//是对的
            double root_slope = model[i][0].getSlope();
            double root_intercept = model[i][0].getIntercept();
            int pre_model_idx = (int)Math.max(0, Math.min(num_model-1,root_slope*key+root_intercept));
            double pred_cdf = 0;
            if(model[i][pre_model_idx] == null) pred_cdf = 1;
            else pred_cdf = model[i][pre_model_idx].getSlope()*key+model[i][pre_model_idx].getIntercept();
            cdf[i] = pred_cdf;
        }
        return cdf;
    }

    public static double Cal_V(linear_model[][] model,HyperPoint p){
        double[] cdf = new double[p.getK()];
        double sum = 0;
        for(int i=0;i<p.getK();i++){
            double key = p.getcoords()[i];
            int num_model = model[i].length-1;//是对的
            double root_slope = model[i][0].getSlope();
            double root_intercept = model[i][0].getIntercept();
            int pre_model_idx = (int)Math.max(0, Math.min(num_model-1,root_slope*key+root_intercept));
            double pred_cdf = model[i][pre_model_idx].getSlope()*key+model[i][pre_model_idx].getIntercept();
            cdf[i] = pred_cdf;
            sum += sum+(pred_cdf-0.5)*(pred_cdf-0.5);
        }
        return sum/p.getK();
    }
    public static double[] Node_Embeding(LinkedList<double[]> list, HyperPoint p,linear_model[][] model){
        double[] res = new double[2];

        double x = 0;
        double y = 0;
        double sum = 0;
        HyperPoint p1 = new HyperPoint(list.get(0));
        HyperPoint p2 = new HyperPoint(list.get(1));
        double[] cdf = Cal_CDF(model,p);
        double[] cdf1 = Cal_CDF(model,p1);
        double[] cdf2 = Cal_CDF(model,p2);
        for(int i=0;i<list.size();i++){
            x += x+cdf[i]*cdf[i];
            y += y+cdf1[i]*cdf1[i];
            sum += cdf[i]*cdf1[i];
        }
        res[0] = sum/(Math.pow(x,0.5)*Math.pow(y,0.5));

        x = 0;
        y = 0;
        sum = 0;
        for(int i=0;i<list.size();i++){
            x += x+cdf[i]*cdf[i];
            y += y+cdf2[i]*cdf2[i];
            sum += cdf[i]*cdf2[i];
        }
        res[1] = sum/(Math.pow(x,0.5)*Math.pow(y,0.5));

        return res;
    }



    int ID = 0;
    public void mark_leaf_id(IndexNode root){
        if(root == null) return;
        if(root.isleaf){
            root.setID(ID++);
        }
        mark_leaf_id(root.left);
        mark_leaf_id(root.right);
    }

    public static int findleaf(HyperPoint p,int k,IndexNode root){
        int dim = k % p.getK();
        if(root == null){
            System.out.print("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+"\n");
            return Integer.MAX_VALUE;
        }
        if(root.isleaf){
            //System.out.print("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+"\n");
            return root.ID;
        }
        if(root.pivot>p.getcoords()[dim]) return findleaf(p,k+1,root.left);
        else{
            return findleaf(p,k+1,root.right);
        }
    }


    public Vector<int[]> store_coordinate_grid(int[] grid, HyperPoint[] hp){
        int[] leafID = new int[hp.length];
        for(int i=0;i<hp.length;i++){
            leafID[i] = findleaf(hp[i],0,root);
            //if(leafID[i]<100) System.out.print(leafID[i]+"\n");
        }
        HashMap<Integer,HashSet<Integer>> map = new HashMap<>();
        for(int i=0;i<hp.length;i++){
            if(!map.containsKey(grid[i])){
                HashSet<Integer> hashset = new HashSet<>();
                map.put(grid[i],hashset);
                hashset.add(leafID[i]);
            }else{
                HashSet<Integer> hashset = map.get(grid[i]);
                hashset.add(leafID[i]);
            }
        }
        gridMatch[] arr = new gridMatch[map.size()];
        int u = 0;
        for (Map.Entry<Integer, HashSet<Integer>> entry: map.entrySet()){
            gridMatch gM = new gridMatch(entry.getKey(),entry.getValue());
            arr[u++] = gM;
        }
        Vector<int[]> vec = new Vector<>();
        for(int i=0;i<arr.length;i++){
            for(int j=i+1;j<arr.length;j++){
                HashSet<Integer> set1 = arr[i].getSet();
                HashSet<Integer> set2 = arr[i].getSet();
                if(twoSet(set1,set2)){
                    int[] ele = new int[2];
                    ele[0] = i;
                    ele[1] = j;
                    vec.add(ele);
                }
            }
        }
        return vec;

    }

    public static boolean twoSet(HashSet<Integer> set1,HashSet<Integer> set2){
        int l = 0;
        for(int ele1:set1){
            for(int ele2:set2){
                if(l>50) return true;
                if(ele1 == ele2){
                    l++;
                }
            }
        }
        return false;
    }


    private void get_all_nearest_points(HyperPoint pointKD, BoundedPQueue knp, IndexNode curr,int depth,long[] count, Long best_method,boolean[] flag){
        if(flag[0]) return;
        if(curr == null){
            return;
        }
        //计算操作次数
        count[0]+= 3;
        if(curr.isleaf){
            if(count[0] > best_method){
                count[0] = Long.MAX_VALUE-Integer.MAX_VALUE;
                flag[0] = true;
                return;
            }
            count[0] += curr.hp.length*5;
            for(int i=0;i<curr.hp.length;i++){
                double distance = pointKD.squareDistanceTo(curr.hp[i]);//4
                knp.enqueue(distance, curr.hp[i]);
            }
            return;
        }
        //计算操作次数
        count[0]+= 2;
        double value = Double.MAX_VALUE;
        if(!knp.isEmpty()){
            //计算操作次数
            count[0] += 3;
            value = knp.worst();
        }
        //计算操作次数
        count[0] += 4;

        int keyIndex = depth % K;
        //boolean to_left = false;// do I need to go further side, consider sheng wang kNN
        boolean near_is_left = false;
        boolean near_is_right = false;
        if(pointKD.coords[keyIndex] < curr.pivot){
            get_all_nearest_points(pointKD, knp, curr.left,depth+1,count,best_method,flag);
            near_is_left = true;
        }else{
            //to_left = true;
            get_all_nearest_points(pointKD, knp, curr.right,depth+1,count,best_method,flag);
            near_is_right = true;
        }
        //计算操作次数
        count[0] += 2;

        if(knp.Size()!= knp.maxSize()||(curr.p.coords[keyIndex] - pointKD.coords[keyIndex])
                *(curr.p.coords[keyIndex] - pointKD.coords[keyIndex]) < value){
            //计算操作次数
            count[0] += 1;
            if(!near_is_left) get_all_nearest_points(pointKD, knp, curr.left,depth+1,count,best_method,flag);
            else get_all_nearest_points(pointKD, knp, curr.right,depth+1,count,best_method,flag);
        }
    }

    private void get_all_nearest_points_ball(HyperPoint pointKD, BoundedPQueue knp, IndexNode curr,int depth,long[] count,long best_method,boolean[] flag){
        if(flag[0]) return;
        if(curr == null){
            return;
        }
        //计算操作次数
        if(count[0] < Long.MAX_VALUE-1000)  count[0]+= 3;
        if(curr.isleaf){
            if(count[0] >= best_method){
                count[0] = Long.MAX_VALUE-Integer.MAX_VALUE;
                flag[0] = true;
                return;
            }
            count[0] += curr.hp.length*5;
            for(int i=0;i<curr.hp.length;i++){
                double distance = pointKD.squareDistanceTo(curr.hp[i]);
                knp.enqueue(distance, curr.hp[i]);
            }
            return;
        }

        //计算操作次数
        count[0]+= 2;
        double value = Double.MAX_VALUE;
        if(!knp.isEmpty()){
            //计算操作次数
            count[0] += 3;
            value = knp.worst();
        }
        //计算操作次数
        count[0] += 4;
        int keyIndex = depth % K;
        //boolean to_left = false;// do I need to go further side, consider sheng wang kNN
        boolean near_is_left = false;
        boolean near_is_right = false;
        if(pointKD.coords[keyIndex] < curr.pivot){
            get_all_nearest_points_ball(pointKD, knp, curr.left,depth+1,count,best_method,flag);
            near_is_left = true;
        }else{
            //to_left = true;
            get_all_nearest_points_ball(pointKD, knp, curr.right,depth+1,count,best_method,flag);
            near_is_right = true;
        }
        //计算操作次数
        count[0] += 2;
        if(knp.Size()!= knp.maxSize()|| curr.bs.radius <= value){
            //计算操作次数
            count[0] += 1;
            if(!near_is_left) get_all_nearest_points_ball(pointKD, knp, curr.left,depth+1,count,best_method,flag);
            else get_all_nearest_points_ball(pointKD, knp, curr.right,depth+1,count,best_method,flag);
        }
    }

    private void get_all_nearest_points_BFS_MBR(HyperPoint pointKD,IndexNode root, BoundedPQueue knp,int n,long[] count,long best_method) {
        Queue<IndexNode> queue = new LinkedList<IndexNode>();
        BoundedPQueue heap = new BoundedPQueue(n);
        queue.add(root);
        //操作次数计算
        count[0] += 3;
        while (!queue.isEmpty()) {//&& anchor
            IndexNode temp = queue.poll();
            //操作次数计算
            count[0] += 3;
            if(!temp.isleaf){
                // Recursively search the half of the tree that contains the test point.
                if(heap.Size()== heap.maxSize()){
                    //操作次数计算
                    count[0] += 8+pointKD.K;
                    double Mindist = MINDIST(temp.hs,pointKD);
                    double value = heap.worst();
                    if(Mindist <= value){
                        if (temp.left != null) {
                            //操作次数计算
                            count[0] += 2;
                            queue.add(temp.left);
                        }
                        /*add right right child to the queue */
                        if (temp.right != null) {
                            //操作次数计算
                            count[0] += 2;
                            queue.add(temp.right);
                        }
                    }
                    heap.enqueue(Mindist,heap);
                }else{
                    if (temp.left != null) {
                        //操作次数计算
                        count[0] += 2;
                        queue.add(temp.left);
                    }
                    /*add right right child to the queue */
                    if (temp.right != null) {
                        //操作次数计算
                        count[0] += 2;
                        queue.add(temp.right);
                    }
                }
            }else{
                if(count[0] > best_method){
                    count[0] = Long.MAX_VALUE-Integer.MAX_VALUE;
                    return;
                }
                //if(count[0] < 0) System.out.print("wrong"+"\n");
                count[0] += temp.hp.length*10;
                for(int i=0;i<temp.hp.length;i++){
                    double distance = pointKD.distanceTo(temp.hp[i]);
                    heap.enqueue(distance,temp.hp[i]);
                    knp.enqueue(distance, temp.hp[i]);
                }
            }
        }
    }

    private void get_all_nearest_points_BFS_MBB(HyperPoint pointKD,IndexNode root, BoundedPQueue knp,int n,long[] count,long best_method) {
        Queue<IndexNode> queue = new LinkedList<IndexNode>();
        BoundedPQueue heap = new BoundedPQueue(n);
        queue.add(root);
        count[0] += 3;
        while (!queue.isEmpty()) {//&& anchor
            IndexNode temp = queue.poll();
            //操作次数计算
            count[0] += 3;
            if(!temp.isleaf){
                // Recursively search the half of the tree that contains the test point.
                if(heap.Size()== heap.maxSize()){
                    //操作次数计算
                    count[0] += 8+pointKD.K;
                    double distance = Math.abs(pointKD.distanceTo(temp.p)-temp.bs.radius);
                    double value = heap.worst();
                    if(distance<= value){//distance <= value+temp.bs.radius
                        if (temp.left != null) {
                            //操作次数计算
                            count[0] += 2;
                            queue.add(temp.left);
                        }
                        /*add right right child to the queue */
                        if (temp.right != null) {
                            //操作次数计算
                            count[0] += 2;
                            queue.add(temp.right);
                        }
                    }
                    //操作次数计算
                    count[0] += 1;
                    heap.enqueue(distance,heap);
                }else{
                    if (temp.left != null) {
                        //操作次数计算
                        count[0] += 2;
                        queue.add(temp.left);
                    }
                    /*add right right child to the queue */
                    if (temp.right != null) {
                        //操作次数计算
                        count[0] += 2;
                        queue.add(temp.right);
                    }
                }
            }else{
                if(count[0] > best_method){
                    count[0] = Long.MAX_VALUE-Integer.MAX_VALUE;
                    return;
                }
                count[0] += temp.hp.length*10;
                for(int i=0;i<temp.hp.length;i++){
                    double distance = pointKD.squareDistanceTo(temp.hp[i]);
                    heap.enqueue(distance,temp.hp[i]);
                    knp.enqueue(distance,temp.hp[i]);
                }
            }
        }
    }

    public long kNN(HyperPoint pointKD, int K, int model,long[] count){
        BoundedPQueue k_nearest_points = new BoundedPQueue(K);
        boolean[] flag = new boolean[1];
        switch(model){
            case 1:
                //dfs mbr
                get_all_nearest_points(pointKD,k_nearest_points,root,0,count,Long.MAX_VALUE,flag);break;
            case 2:
                //dfs mbb
                get_all_nearest_points_ball(pointKD,k_nearest_points,root,0,count,Long.MAX_VALUE,flag);break;
            case 3:
                //bfs mbr
                get_all_nearest_points_BFS_MBR(pointKD,root,k_nearest_points,K,count,Long.MAX_VALUE);break;
            case 4:
                //bfs mbb
                get_all_nearest_points_BFS_MBB(pointKD,root,k_nearest_points,K,count,Long.MAX_VALUE);break;
        }
        return count[0];
    }

    public long kNN(HyperPoint pointKD, int K, int model,long[] count, long best_method){
        BoundedPQueue k_nearest_points = new BoundedPQueue(K);
        boolean[] flag = new boolean[1];
        switch(model){
            case 1:
                //dfs mbr
                get_all_nearest_points(pointKD,k_nearest_points,root,0,count,best_method,flag);break;
            case 2:
                //dfs mbb
                get_all_nearest_points_ball(pointKD,k_nearest_points,root,0,count,best_method,flag);break;
            case 3:
                //bfs mbr
                get_all_nearest_points_BFS_MBR(pointKD,root,k_nearest_points,K,count,best_method);break;
            case 4:
                //bfs mbb
                get_all_nearest_points_BFS_MBB(pointKD,root,k_nearest_points,K,count,best_method);break;
        }
        return count[0];
    }

    public int  RangeQuery(HyperSpace hs, int model,int[] count){
        Set<HyperPoint> res = new HashSet<HyperPoint>();
        switch(model){
            case 1:
                //dfs mbr
                rangeQuery_DFS_MBR(root, hs, res,count);break;
            case 2:
                //dfs mbb
                rangeQuery_DFS_MBB(root, hs, res,count);break;
            case 3:
                //bfs mbr
                rangeQuery_BFS_MBR(res,hs,count);break;
            case 4:
                //bfs mbb
                rangeQuery_BFS_MBB(res,hs,count);break;
        }
        return count[0];
    }

    public void rangeQuery_DFS_MBR(IndexNode r, HyperSpace hs, Set<HyperPoint> res, int[] count) {
        // If current node r is null or doesn't intersect with hs, then return
        if (r == null ||!r.hs.intersects(hs)) return;
        if(r.isleaf){
            count[0] += r.hp.length;
            for(int i=0;i<r.hp.length;i++){
                if (hs.contains(r.hp[i])) {
                    res.add(r.hp[i]);
                }
            }
            return;
        }
        rangeQuery_DFS_MBR(r.left, hs, res,count);
        rangeQuery_DFS_MBR(r.right, hs, res,count);
    }

    public static void rangeQuery_DFS_MBB(IndexNode r, HyperSpace hs, Set<HyperPoint> res,int[] count) {
        double mindis = MINDIST(hs,r.p);
        if (r == null ||mindis > r.bs.radius) return;
        if(r.isleaf){
            count[0] += r.hp.length;
            for(int i=0;i<r.hp.length;i++){
                if (hs.contains(r.hp[i])) {
                    res.add(r.hp[i]);
                }
            }
            return;
        }
        rangeQuery_DFS_MBB(r.left, hs, res,count);
        rangeQuery_DFS_MBB(r.right, hs, res,count);
    }

    public void rangeQuery_BFS_MBB(Set<HyperPoint> res, HyperSpace target,int[] count) {
        Queue<IndexNode> heap = new LinkedList<IndexNode>();
        heap.add(root);
        while (!heap.isEmpty()) {//&& anchor
            IndexNode temp = heap.poll();
            double mindis = MINDIST(target,temp.p);
            if(temp.equals(null)) continue;
            if(mindis <= temp.bs.radius){
                if(!temp.isleaf){
                    if (temp.left != null) {
                        heap.add(temp.left);
                    }
                    if (temp.right != null) {
                        heap.add(temp.right);
                    }
                }else{
                    count[0] += temp.hp.length;
                    for(int i=0;i<temp.hp.length;i++){
                        if (target.contains(temp.hp[i])) {
                            res.add(temp.hp[i]);
                        }
                    }
                }
            }
        }
    }

    public void rangeQuery_BFS_MBR(Set<HyperPoint> res, HyperSpace target,int[] count) {
        Queue<IndexNode> heap = new LinkedList<IndexNode>();
        heap.add(root);
        while (!heap.isEmpty()) {//&& anchor
            IndexNode temp = heap.poll();
            HyperSpace hs = temp.hs;
            if(temp != null && hs.intersects(target)){
                if(!temp.isleaf){
                    if (temp.left != null) {
                        heap.add(temp.left);
                    }
                    if (temp.right != null) {
                        heap.add(temp.right);
                    }
                }else{
                    count[0] += temp.hp.length;
                    for(int i=0;i<temp.hp.length;i++){
                        if (target.contains(temp.hp[i])) {
                            res.add(temp.hp[i]);
                        }
                    }
                }
            }
        }
    }

    public Set<HyperPoint> RangeQuery(HyperSpace hs, int model){
        Set<HyperPoint> res = new HashSet<HyperPoint>();
        switch(model){
            case 1:
                //dfs mbr
                rangeQuery_DFS_MBR(root, hs, res);break;
            case 2:
                //dfs mbb
                rangeQuery_DFS_MBB(root, hs, res);break;
            case 3:
                //bfs mbr
                rangeQuery_BFS_MBR(res,hs);break;
            case 4:
                //bfs mbb
                rangeQuery_BFS_MBB(res,hs);break;
        }
        return res;
    }


}
