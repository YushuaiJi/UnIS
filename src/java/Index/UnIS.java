package Index;

import RMI.*;
import RMI.Params;

import java.math.BigDecimal;
import java.util.*;

import static MedianofMedian.MedianofMedian.check;
import static RMI.RMI.*;


public class UnIS {
    IndexNode root;
    int K = 3;
    double RANGE = 1.0;
    HyperPoint min, max;
    int capacity = 50;

    int[] pt;

    double td = 0.5;

    HyperPoint[] DATA;

    public IndexNode getRoot() {
        return root;
    }


    public UnIS(int K, int[] pt) {
        this.K = K;
        root = null;
        double[] vals = new double[K];
        min = new HyperPoint(vals);
        for (int i = 0; i < K; i++)
            vals[i] = RANGE;
        max = new HyperPoint(vals);
        this.pt = pt;
    }

    public UnIS(int K, int[] pt,HyperPoint min, HyperPoint max) {
        this.K = K;
        this.min = min;
        this.max = max;
        root = null;
        this.pt = pt;
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



    public int[] findQuantile(HyperPoint[] points, int k, int beg, int end, int Partition) {
        SortComparator sc = new SortComparator();
        sc.setK(k);
        Arrays.sort(points,beg,end,sc);
        int[] PivotArray = new int[Partition-1];
        int step = (end-beg)/(Partition);
        for(int i=0;i<PivotArray.length;i++){
            PivotArray[i] =beg+step*(i+1);
        }
        return PivotArray;
    }

    public int[] findQuantile_Density(HyperPoint[] points, int k, int beg, int end) {
        List<Integer> pivot_list = new ArrayList<>();
        SortComparator sc = new SortComparator();
        sc.setK(k);
        Arrays.sort(points,beg,end,sc);
        double val1 = points[beg].getcoords()[k];
        double val2 = points[end].getcoords()[k];
        double diff_val = val2-val1;

        pivot_traversal(points,k,beg,end,pivot_list,diff_val,0);
        int[] PIVOTARRAY = new int[pivot_list.size()];
        for(int i=0;i<PIVOTARRAY.length;i++){
            PIVOTARRAY[i] = pivot_list.get(i);
        }
        //System.out.print("len "+PIVOTARRAY.length+"\n");
        return PIVOTARRAY;
    }

    public void pivot_traversal(HyperPoint[] points, int k, int beg, int end, List<Integer> pivot_list,double diff_val,int times) {
        int mid = (end-beg)/2+beg;
        //System.out.print("beg: "+beg+" mid "+mid+"end "+end);
        pivot_list.add(mid);
        double val1 = points[beg].getcoords()[k];
        double val2 = points[end].getcoords()[k];
        double mathcalP =  points[mid].getcoords()[k];
        //System.out.print(" val1 "+val1+" val2 "+val2+" mathcalP: "+points[mid].getcoords()[k]+" diff_val "+diff_val+"\n");
        double P1 = (mathcalP-val1)/(diff_val);
        double P2 = (val2-mathcalP)/(diff_val);
        times++;
        if(times>1) return;
        if(end-beg<1000000) return;
        //System.out.print("P1: "+P1+" P2: "+P2+"\n");
        if(P1>td){
            pivot_traversal(points,k,beg,mid,pivot_list,diff_val,times);
        }
        if(P2>td){
            pivot_traversal(points,k,mid,end,pivot_list,diff_val,times);
        }
    }

    public int[] findQuantile_Density1(HyperPoint[] points, int k, int beg, int end) {
        List<Integer> pivot_list = new ArrayList<>();
        SortComparator sc = new SortComparator();
        sc.setK(k);
        Arrays.sort(points, beg, end, sc);
        double val1 = points[beg].getcoords()[k];
        double val2 = points[end].getcoords()[k];
        double diff_val = val2 - val1;

        int times = 0;
        int maxIterations = 2; // Set your desired max iterations here

        if(end - beg < 1000000){
            int mid = (end - beg) / 2 + beg;
            pivot_list.add(mid);
            int[] PIVOTARRAY = new int[pivot_list.size()];
            for (int i = 0; i < PIVOTARRAY.length; i++) {
                PIVOTARRAY[i] = pivot_list.get(i);
            }
            return PIVOTARRAY;
        }

        while (times < maxIterations && end - beg >= 1000000) {
            int mid = (end - beg) / 2 + beg;
            pivot_list.add(mid);
            double mathcalP = points[mid].getcoords()[k];
            double P1 = (mathcalP - val1) / diff_val;
            double P2 = (val2 - mathcalP) / diff_val;

            if (P1 > td) {
                end = mid;
            }else{
                beg = mid;
            }

            times++;
        }

        int[] PIVOTARRAY = new int[pivot_list.size()];
        for (int i = 0; i < PIVOTARRAY.length; i++) {
            PIVOTARRAY[i] = pivot_list.get(i);
        }

        return PIVOTARRAY;
    }

    //mathcal{P_1} = (mid-smallest_val)/(largest_val-smallest_val)
    //mathcal{P_2} = (largest_val_mid)/(largest_val-smallest_val)

    //对比P_1和P_2和预设的split-threshold(比如0.5)

    public static void swap(HyperPoint[] points, int left, int right){
        HyperPoint temp = points[left];
        points[left]= points[right];
        points[right]= temp;
    }

    public void ConstructionUnIS(HyperPoint[] points,int model) {
        int num = points.length;
        HyperPoint hmin = new HyperPoint(min);
        HyperPoint hmax = new HyperPoint(max);
        switch(model){
            case 1:
                root = insertBySorting(root, points, hmin, hmax, 0, 0, num - 1);break;
            case 2:
                root = insertByQuantileFindingLearnedIndex(root, points, hmin, hmax, 0, 0, num - 1);break;
            case 3:
                root = insertBySorting_Self_Partition(root, points, hmin, hmax, 0, 0, num - 1);break;
            case 4:
                root = insertBySorting_Self_Partition_learned(root, points, hmin, hmax, 0, 0, num - 1);break;
        }
    }

    private IndexNode insertBySorting(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        if(j-i+1 <= capacity){
            if(i > j) r = new IndexNode(new HyperSpace(hmin, hmax),points[j]);
            else r = new IndexNode(new HyperSpace(hmin, hmax),points[i]);
            r.Node_count = j-i+1;
            r.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[capacity];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            if(j-i+1 == capacity) r.hasSpace = false;
            r.empty_id = j-i+1;
            r.hp = tmp_hp;
            return r;
        }
        int k = depth % K;
        //pt[depth] means the number of subspace
        int[] t = findQuantile(points,k,i,j,pt[depth]);
        r.setP(points[t[0]]);
        HyperPoint[] listp = new HyperPoint[t.length];
        for(int h=0;h<listp.length;h++){
            listp[h] = points[t[h]];
        }
        HyperPoint[] hmid = new HyperPoint[listp.length+1];
        for(int h=0;h<=listp.length;h++){
            if(h < listp.length){
                HyperPoint hmid1 = new HyperPoint(hmax);
                hmid1.coords[k] = listp[h].coords[k];
                hmid[h] = hmid1;
            }else if(h == listp.length){
                HyperPoint hmid1 = new HyperPoint(hmin);
                hmid1.coords[k] = listp[h-1].coords[k];
                hmid[h] = hmid1;
            }
        }

        r.pivot = new double[listp.length+2];
        r.children = new IndexNode[listp.length+1];

        for(int h=0;h<listp.length+2;h++){
            if(h == 0) r.pivot[h] = hmin.coords[k];
            else if(h == listp.length+1) r.pivot[h] = hmax.coords[k];
            else r.pivot[h] = listp[h-1].coords[k];
        }

        //一共有listp.length+1个
        for(int h=0;h<=listp.length;h++){
            if(h == 0){
                r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, i, t[h]);
            }else if(h == listp.length){
                r.children[h] = insertBySorting(r.children[h], points, hmid[h], hmax, depth + 1, t[h-1]+1, j);
            }else{
                r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, t[h-1], t[h]);
            }
        }
        return r;
    }
    private IndexNode insertBySorting_Self_Partition(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        if(j-i+1 <= capacity){
            if(i > j) r = new IndexNode(new HyperSpace(hmin, hmax));
            else r = new IndexNode(new HyperSpace(hmin, hmax));
            r.Node_count = j-i+1;
            r.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[capacity];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            if(j-i+1 == capacity) r.hasSpace = false;
            r.empty_id = j-i+1;
            r.hp = tmp_hp;
            return r;
        }
        int k = depth % K;

        int[] t = findQuantile_Density1(points,k,i,j);

        HyperPoint[] listp = new HyperPoint[t.length];
        for(int h=0;h<listp.length;h++){
            listp[h] = points[t[h]];
        }
        //System.out.print("listp.length: "+listp.length+"\n");
        HyperPoint[] hmid = new HyperPoint[listp.length+1];
        for(int h=0;h<=listp.length;h++){
            if(h < listp.length){
                HyperPoint hmid1 = new HyperPoint(hmax);
                hmid1.coords[k] = listp[h].coords[k];
                hmid[h] = hmid1;
            }else if(h == listp.length){
                HyperPoint hmid1 = new HyperPoint(hmin);
                //System.out.print("h: "+h+"\n");
                hmid1.coords[k] = listp[h-1].coords[k];
                hmid[h] = hmid1;
            }
        }

        r.pivot = new double[listp.length+2];
        r.children = new IndexNode[listp.length+1];

        for(int h=0;h<listp.length+2;h++){
            if(h == 0) r.pivot[h] = hmin.coords[k];
            else if(h == listp.length+1) r.pivot[h] = hmax.coords[k];
            else r.pivot[h] = listp[h-1].coords[k];
        }

        for(int h=0;h<=listp.length;h++){
            if(h == 0){
                if((t[h]-i)>1000000) r.children[h] =insertBySorting_Self_Partition(r.children[h], points, hmin, hmid[h], depth + 1, i, t[h]);
                else r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, i, t[h]);
            }else if(h == listp.length){
                if((j-t[h-1]+1)>1000000) r.children[h] = insertBySorting_Self_Partition(r.children[h], points, hmid[h], hmax, depth + 1, t[h-1]+1, j);
                else r.children[h] = insertBySorting(r.children[h], points, hmid[h], hmax, depth + 1, t[h-1]+1, j);
            }else{
                if((t[h]-t[h-1])>1000000)r.children[h] = insertBySorting_Self_Partition(r.children[h], points, hmin, hmid[h], depth + 1, t[h-1], t[h]);
                else r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, t[h-1], t[h]);
            }
        }
        return r;
    }

    private IndexNode insertBySorting_Self_Partition_learned(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        if(j-i+1 <= capacity){
            if(i > j) r = new IndexNode(new HyperSpace(hmin, hmax));
            else r = new IndexNode(new HyperSpace(hmin, hmax));
            r.Node_count = j-i+1;
            r.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[capacity];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            if(j-i+1 == capacity) r.hasSpace = false;
            r.empty_id = j-i+1;
            r.hp = tmp_hp;
            return r;
        }
        int k = depth % K;

        TrainedRMI train_rmi = LRMI_Train_Sefl_Partition(points,i,j,k);

        double[] pivot = train_rmi.getPivot();
        int[] t = Partition_Space(points,i,j,k,pivot);

        HyperPoint[] listp = new HyperPoint[t.length];
        for(int h=0;h<listp.length;h++){
            listp[h] = points[t[h]];
        }

        HyperPoint[] hmid = new HyperPoint[listp.length+1];
        for(int h=0;h<=listp.length;h++){
            if(h < listp.length){
                HyperPoint hmid1 = new HyperPoint(hmax);
                hmid1.coords[k] = listp[h].coords[k];
                hmid[h] = hmid1;
            }else if(h == listp.length){
                HyperPoint hmid1 = new HyperPoint(hmin);
                //System.out.print("h: "+h+"\n");
                hmid1.coords[k] = listp[h-1].coords[k];
                hmid[h] = hmid1;
            }
        }

        r.pivot = new double[listp.length+2];
        r.children = new IndexNode[listp.length+1];

        for(int h=0;h<listp.length+2;h++){
            if(h == 0) r.pivot[h] = hmin.coords[k];
            else if(h == listp.length+1) r.pivot[h] = hmax.coords[k];
            else r.pivot[h] = listp[h-1].coords[k];
        }

        for(int h=0;h<=listp.length;h++){
            if(h == 0){
                if((t[h]-i)>1000000) r.children[h] =insertBySorting_Self_Partition_learned(r.children[h], points, hmin, hmid[h], depth + 1, i, t[h]);
                else r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, i, t[h]);
            }else if(h == listp.length){
                if((j-t[h-1]+1)>1000000) r.children[h] = insertBySorting_Self_Partition_learned(r.children[h], points, hmid[h], hmax, depth + 1, t[h-1]+1, j);
                else r.children[h] = insertBySorting(r.children[h], points, hmid[h], hmax, depth + 1, t[h-1]+1, j);
            }else{
                if((t[h]-t[h-1])>1000000)r.children[h] = insertBySorting_Self_Partition_learned(r.children[h], points, hmin, hmid[h], depth + 1, t[h-1], t[h]);
                else r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, t[h-1], t[h]);
            }
        }

        return r;
    }

    private IndexNode insertByQuantileFindingLearnedIndex(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        if(j-i+1 <= capacity){
            if(i > j) r = new IndexNode(new HyperSpace(hmin, hmax));
            else r = new IndexNode(new HyperSpace(hmin, hmax));
            r.Node_count = j-i+1;
            r.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[capacity];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            if(j-i+1 == capacity) r.hasSpace = false;
            r.empty_id = j-i+1;
            r.hp = tmp_hp;
            return r;
        }
        int k = depth % K;
        double[] Quantile = new double[pt[depth]-1];
        double quantile = 1.0/pt[depth];
        for(int h=0;h<Quantile.length;h++){
            Quantile[h] = quantile*(h+1);
        }
        TrainedRMI train_rmi = LRMI_Train(points,i,j,k,Quantile);
        double[] pivot = train_rmi.getPivot();
        int[] t = Partition_Space(points,i,j,k,pivot);
        HyperPoint[] listp = new HyperPoint[t.length];

        for(int h=0;h<listp.length;h++){
            listp[h] = points[t[h]];
        }

        HyperPoint[] hmid = new HyperPoint[listp.length+1];
        for(int h=0;h<=listp.length;h++){
            if(h < listp.length){
                HyperPoint hmid1 = new HyperPoint(hmax);
                hmid1.coords[k] = listp[h].coords[k];
                hmid[h] = hmid1;
            }else if(h == listp.length){
                HyperPoint hmid1 = new HyperPoint(hmin);
                hmid1.coords[k] = listp[h-1].coords[k];
                hmid[h] = hmid1;
            }
        }

        r.pivot = new double[listp.length+2];
        r.children = new IndexNode[listp.length+1];

        for(int h=0;h<listp.length+2;h++){
            if(h == 0) r.pivot[h] = hmin.coords[k];
            else if(h == listp.length+1) r.pivot[h] = hmax.coords[k];
            else r.pivot[h] = listp[h-1].coords[k];
        }
        //一共有listp.length+1个
        for(int h=0;h<=listp.length;h++){
            if(h == 0){
                if((t[h]-i)>500000) r.children[h] = insertByQuantileFindingLearnedIndex(r.children[h], points, hmin, hmid[h], depth + 1, i, t[h]);
                else r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, i, t[h]);
            }else if(h == listp.length){
                if((j-t[h-1]+1)>500000) r.children[h] = insertByQuantileFindingLearnedIndex(r.children[h], points, hmid[h], hmax, depth + 1, t[h-1]+1, j);
                else r.children[h] = insertBySorting(r.children[h], points, hmid[h], hmax, depth + 1, t[h-1]+1, j);
            }else{
                if((t[h]-t[h-1])>500000)r.children[h] = insertByQuantileFindingLearnedIndex(r.children[h], points, hmin, hmid[h], depth + 1, t[h-1], t[h]);
                else r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, t[h-1], t[h]);
            }
        }
        return r;
    }

    public static int[] Partition_Space(HyperPoint[] arr, int l, int r,int k,double[] pivot){
        int length = (r-l+1)/(pivot.length+1);
        HyperPoint[][] Tmp_Array = new HyperPoint[pivot.length+1][r-l+2];
        int[] Sum = new int[pivot.length+1];
        for(int i = 0;i<Sum.length;i++){
            if(i==0) Sum[i] = 0;
            else Sum[i] = 1;
        }
        for(int i=l;i<=r;i++){
            for(int j=0;j<=pivot.length;j++){
                if(j == 0){
                    //if(j==2047021)System.out.print(Tmp_Array[j].length+" ");
                    if(arr[i].getcoords()[k]<pivot[j]) Tmp_Array[j][Sum[j]++] = arr[i];
                }else if(j== pivot.length){
                    if(arr[i].getcoords()[k]==pivot[j-1] && Tmp_Array[j][0] == null){
                        Tmp_Array[j][0] = arr[i];

                    } else if(arr[i].getcoords()[k]>=pivot[j-1]) {
                        Tmp_Array[j][Sum[j]++] = arr[i];
                    }
                }else{
                    if(arr[i].getcoords()[k]<pivot[j] && arr[i].getcoords()[k]==pivot[j-1] && Tmp_Array[j][0] == null){
                        Tmp_Array[j][0] = arr[i];
                    }else if(arr[i].getcoords()[k]<pivot[j] && arr[i].getcoords()[k]>=pivot[j-1]) {
                        Tmp_Array[j][Sum[j]++] = arr[i];
                    }
                }
            }
        }
        int[] t = new int[pivot.length];
        //for(int i=0;i<Sum.length;i++){
         //   System.out.print(" "+Sum[i]+" ");
        //}
        //System.out.print("\n");
        for(int i=0;i<pivot.length;i++){
            for(int j=0;j<Sum[i]-1;j++){
                arr[l++] = Tmp_Array[i][j];
            }
            if(Sum[i] <= 1) t[i] = l;
            else t[i] = l-1;
        }
        return t;
    }

    public BoundedPQueue kNN(HyperPoint pointKD, int K, int model,long[] count, long best_method){
        BoundedPQueue k_nearest_points = new BoundedPQueue(K);

        switch(model){
            case 1:
                //dfs mbr
                get_all_nearest_points(pointKD,k_nearest_points,root,0);break;
            case 2:
                //dfs mbr
                get_all_nearest_points_ball(pointKD,k_nearest_points,root,0);break;
            case 3:
                get_all_nearest_points_BFS_MBR(pointKD,root,k_nearest_points,K,count,best_method);break;
            case 4:
                get_all_nearest_points_BFS_MBB(pointKD,root,k_nearest_points,K,count,best_method);break;
        }
        return k_nearest_points;
    }

    public List<Double> kNN_Bound(HyperPoint pointKD, int K, int model,long[] delta_T){
        BoundedPQueue k_nearest_points = new BoundedPQueue(K);
        List<Double> Max_Bound = new ArrayList<>();
        long t = System.currentTimeMillis();
        switch(model){
            case 1:
                //dfs mbr
                get_all_nearest_points_Delta_T(pointKD,k_nearest_points,root,0,delta_T,Max_Bound,t);break;
        }
        while(Max_Bound.size() < delta_T.length){
            Max_Bound.add(k_nearest_points.worst());
        }
        Max_Bound.add(k_nearest_points.worst());
        return Max_Bound;
    }

    private void get_all_nearest_points_ball(HyperPoint pointKD, BoundedPQueue knp, IndexNode curr,int depth){
        if(curr == null){
            return;
        }
        if(curr.isleaf){
            for(int i=0;i<curr.empty_id;i++){
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

        boolean[] Visited =new boolean[curr.children.length];

        for(int i=0;i<curr.children.length;i++){
            if(i==0){
                if(pointKD.coords[keyIndex]<curr.pivot[i]){
                    get_all_nearest_points_ball(pointKD, knp, curr.children[i], depth+1);
                    Visited[i] = true;
                }
            }else if(i== curr.children.length-1){
                if(pointKD.coords[keyIndex]>=curr.pivot[i-1]){
                    get_all_nearest_points_ball(pointKD, knp, curr.children[i], depth+1);
                    Visited[i] = true;
                }
            }else{
                if(curr.pivot[i-1]<pointKD.coords[keyIndex] &&pointKD.coords[keyIndex]<=curr.pivot[i]){
                    get_all_nearest_points_ball(pointKD, knp, curr.children[i], depth+1);
                    Visited[i] = true;
                }
            }
        }

        int anchor = 0;
        for(int i=0;i<curr.children.length;i++){
            if(curr.pivot[i]<pointKD.coords[keyIndex] && pointKD.coords[keyIndex] <=curr.pivot[i+1])  anchor = i;
        }
        for(int i=0;i<curr.children.length;i++){
            if(curr.children[i].bs == null) System.out.print("wrong");
            if(Visited[i] == false){
                if(i == anchor) get_all_nearest_points_ball(pointKD, knp, curr.children[i], depth+1);
                else if(curr.children[i].bs.radius+value<= (curr.p.getcoords()[keyIndex] -
                        pointKD.coords[keyIndex])*(curr.p.getcoords()[keyIndex]- pointKD.coords[keyIndex])){
                    get_all_nearest_points_ball(pointKD, knp, curr.children[i], depth+1);
                }
            }
        }
    }

    private void get_all_nearest_points(HyperPoint pointKD, BoundedPQueue knp, IndexNode curr,int depth){
        if(curr == null){
            return;
        }
        if(curr.isleaf){
            for(int i=0;i<curr.empty_id;i++){
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

        boolean[] Visited =new boolean[curr.children.length];

        for(int i=0;i<curr.children.length;i++){
            if(i==0){
                if(pointKD.coords[keyIndex]<curr.pivot[i]){
                    get_all_nearest_points(pointKD, knp, curr.children[i], depth+1);
                    Visited[i] = true;
                }
            }else if(i== curr.children.length-1){
                if(pointKD.coords[keyIndex]>=curr.pivot[i-1]){
                    get_all_nearest_points(pointKD, knp, curr.children[i], depth+1);
                    Visited[i] = true;
                }
            }else{
                if(curr.pivot[i-1]<pointKD.coords[keyIndex] &&pointKD.coords[keyIndex]<=curr.pivot[i]){
                    get_all_nearest_points(pointKD, knp, curr.children[i], depth+1);
                    Visited[i] = true;
                }
            }
        }

        int anchor = 0;
        for(int i=0;i<curr.children.length;i++){
            if(curr.pivot[i]<pointKD.coords[keyIndex] && pointKD.coords[keyIndex] <=curr.pivot[i+1])  anchor = i;
        }
        for(int i=0;i<curr.children.length;i++){
            if(Visited[i] == false){
                if(i == anchor) get_all_nearest_points(pointKD, knp, curr.children[i], depth+1);
                else if((curr.pivot[i] - pointKD.coords[keyIndex])*(curr.pivot[i] - pointKD.coords[keyIndex])<= value ||
                        (curr.pivot[i+1] - pointKD.coords[keyIndex])*(curr.pivot[i+1] - pointKD.coords[keyIndex]) <= value){
                    get_all_nearest_points(pointKD, knp, curr.children[i], depth+1);
                }
            }
        }
    }

    private void get_all_nearest_points_Delta_T(HyperPoint pointKD, BoundedPQueue knp, IndexNode curr,int depth,long[] delta_T,List<Double> Max_Bound,long t){
        long t1 = System.currentTimeMillis();
        long dt = t1-t;
        if(Max_Bound.size() < delta_T.length && dt>delta_T[Max_Bound.size()]){
            if(!knp.isEmpty()) Max_Bound.add(knp.worst());
            else Max_Bound.add(0.0);
        }
        if(curr == null){
            return;
        }
        if(curr.isleaf){
            for(int i=0;i<curr.empty_id;i++){
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

        boolean[] Visited =new boolean[curr.children.length];

        for(int i=0;i<curr.children.length;i++){
            if(i==0){
                if(pointKD.coords[keyIndex]<curr.pivot[i]){
                    get_all_nearest_points_Delta_T(pointKD, knp, curr.children[i], depth+1,delta_T,Max_Bound,t);
                    Visited[i] = true;
                }
            }else if(i== curr.children.length-1){
                if(pointKD.coords[keyIndex]>=curr.pivot[i-1]){
                    get_all_nearest_points_Delta_T(pointKD, knp, curr.children[i], depth+1,delta_T,Max_Bound,t);
                    Visited[i] = true;
                }
            }else{
                if(curr.pivot[i-1]<pointKD.coords[keyIndex] &&pointKD.coords[keyIndex]<=curr.pivot[i]){
                    get_all_nearest_points_Delta_T(pointKD, knp, curr.children[i], depth+1,delta_T,Max_Bound,t);
                    Visited[i] = true;
                }
            }
        }

        int anchor = 0;
        for(int i=0;i<curr.children.length;i++){
            if(curr.pivot[i]<pointKD.coords[keyIndex] && pointKD.coords[keyIndex] <=curr.pivot[i+1])  anchor = i;
        }
        for(int i=0;i<curr.children.length;i++){
            if(Visited[i] == false){
                if(i == anchor) get_all_nearest_points_Delta_T(pointKD, knp, curr.children[i], depth+1,delta_T,Max_Bound,t);
                else if((curr.pivot[i] - pointKD.coords[keyIndex])*(curr.pivot[i] - pointKD.coords[keyIndex])<= value ||
                        (curr.pivot[i+1] - pointKD.coords[keyIndex])*(curr.pivot[i+1] - pointKD.coords[keyIndex]) <= value){
                    get_all_nearest_points_Delta_T(pointKD, knp, curr.children[i], depth+1,delta_T,Max_Bound,t);
                }
            }
        }
    }

    public void setBallSpace(IndexNode root){
        BallSpace bs = new BallSpace(root.hs.K,0);
        root.bs = bs;
        radCal(root,root);
        if(root.isleaf) {
            return;
        }
        for(int i=0;i<root.children.length;i++){
            setBallSpace(root.children[i]);
        }
    }
    public void radCal(IndexNode pivot,IndexNode node){
        if(node.isleaf){
            for(int i=0;i<node.empty_id;i++){
                if(pivot.p == null) System.out.print("null1");
                if(node.hp[i] == null) System.out.print("null");
                double dis = pivot.p.distanceTo(node.hp[i]);
                if(pivot.bs.radius < dis) {
                    pivot.bs.setRadius(dis);
                }
            }
            return;
        }
        for(int i=0;i<root.children.length;i++){
            radCal(pivot,node.children[i]);
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
                        for(int i=0;i<temp.children.length;i++){
                            count[0] += 2;
                            if(temp.children[i] != null) queue.add(temp.children[i]);
                        }
                    }
                    heap.enqueue(Mindist,heap);
                }else{
                    for(int i=0;i<temp.children.length;i++){
                        count[0] += 2;
                        if(temp.children[i] != null) queue.add(temp.children[i]);
                    }
                }
            }else{
                if(count[0] > best_method){
                    count[0] = Long.MAX_VALUE-Integer.MAX_VALUE;
                    return;
                }
                //if(count[0] < 0) System.out.print("wrong"+"\n");
                count[0] += temp.hp.length*10;
                for(int i=0;i<temp.empty_id;i++){
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
                        for(int i=0;i<temp.children.length;i++){
                            count[0] += 2;
                            if(temp.children[i] != null) queue.add(temp.children[i]);
                        }
                    }
                    //操作次数计算
                    count[0] += 1;
                    heap.enqueue(distance,heap);
                }else{
                    for(int i=0;i<temp.children.length;i++){
                        count[0] += 2;
                        if(temp.children[i] != null) queue.add(temp.children[i]);
                    }
                }
            }else{
                if(count[0] > best_method){
                    count[0] = Long.MAX_VALUE-Integer.MAX_VALUE;
                    return;
                }
                count[0] += temp.hp.length*10;
                for(int i=0;i<temp.empty_id;i++){
                    double distance = pointKD.squareDistanceTo(temp.hp[i]);
                    heap.enqueue(distance,temp.hp[i]);
                    knp.enqueue(distance,temp.hp[i]);
                }
            }
        }
    }


    public static void Radius_Search_DFS_MBB(IndexNode r, double radius, Set<HyperPoint> res,int[] count,HyperPoint p,long[] delta_T,long t,List<Long> Max_Bound) {
        long t1 = System.currentTimeMillis();
        long dt = t1-t;
        if(Max_Bound.size() < delta_T.length && dt>delta_T[Max_Bound.size()]){
            if(!res.isEmpty()) Max_Bound.add((long)res.size());
            else Max_Bound.add((long)0);
        }
        double mindis = MINDIST(r.hs,p);
        if (r == null ||mindis > radius) return;
        if(r.isleaf){
            count[0] += r.empty_id;
            for(int i=0;i<r.empty_id;i++){
                if (p.distanceTo(r.hp[i])<=radius) {
                    res.add(r.hp[i]);
                }
            }
            return;
        }
        for(int i=0;i<r.children.length;i++){
            Radius_Search_DFS_MBB(r.children[i], radius,res,count,p,delta_T,t,Max_Bound);
        }
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

    public List<Long> Radius_Search(int model,int[] count,HyperPoint p, double radius){
        Set<HyperPoint> res = new HashSet<HyperPoint>();
        List<Long> Max_Bound = new ArrayList<>();
        long t = System.currentTimeMillis();
        long[] delta_T= new long[5];
        delta_T[0] = 10;
        delta_T[1] = 50;
        delta_T[2] = 100;
        delta_T[3] = 200;
        delta_T[4] = 500;
        switch(model){
            case 1:
                //dfs mbr
                Radius_Search_DFS_MBB(root, radius,res,count,p,delta_T,t,Max_Bound);break;
        }
        while(Max_Bound.size() < delta_T.length){
            Max_Bound.add((long)res.size());
        }
        Max_Bound.add((long)res.size());

        return Max_Bound;
    }

    public void insert(List<HyperPoint> p) {
        insert(root, p, 0);
    }

    private void insert(IndexNode r, List<HyperPoint> P, int depth) {
        int k = depth % K;
        if(r.Stage_Gap){
            for(int i=0;i<P.size();i++){
                HyperPoint p = P.get(i);
                if(r.Gap_idx < r.Max_Gap_Size){
                    p.inserted_location = r.Gap_idx;
                    r.Gap[r.Gap_idx++] = p;
                }else{
                    for(int j=0;j<r.Inserted_Location.size();j++){
                        int ilocation = r.Inserted_Location.get(j);
                        if(r.garbage_location >= r.garbage_collection.length-1) {
                            HyperPoint[] dataGap = new HyperPoint[r.Gap.length+r.garbage_collection.length];
                            for(int h = 0;h<r.Gap.length;h++){
                                dataGap[h] = r.Gap[h];
                            }
                            for(int h=0;h<r.garbage_collection.length;h++){
                                dataGap[r.Gap.length+h] = r.garbage_collection[h];
                            }
                            HyperPoint[] NEWDATA = new HyperPoint[r.getL()-r.getR()+1+dataGap.length];
                            for(int h=0;h<r.getL()-r.getR()+1;h++){
                                NEWDATA[h] = DATA[r.getL()+h];
                            }
                            for(int h=0;h<dataGap.length;h++){
                                NEWDATA[r.getR()-r.getL()+1+h] = dataGap[h];
                            }
                            UpdateTree(r,NEWDATA,dataGap,r.hs.min,r.hs.max,r.depth,0,NEWDATA.length-1);
                            List<HyperPoint> newp = new ArrayList<>();
                            for(int h = i;h<P.size();h++){
                                newp.add(P.get(i));
                            }
                            r.Fresh();
                            insert(r,newp,depth);
                            return;
                        }
                        r.garbage_collection[r.garbage_location++] = r.Gap[ilocation];
                        r.Gap[ilocation] = p;
                    }
                }
                for(int j=1;j<r.pivot.length-1;j++){
                    if(j == 1){
                        if(p.coords[k]<r.pivot[j]) {
                            point_insert(r.children[j],p,depth+1,r.Inserted_Location);
                        }
                    }else if(j == r.pivot.length-2){
                        if(p.coords[k]>=r.pivot[j]) {
                            point_insert(r.children[j],p,depth+1,r.Inserted_Location);
                        }
                    }else{
                        if(p.coords[k]>=r.pivot[j-1] && p.coords[k]<r.pivot[j-1]) {
                            point_insert(r.children[j],p,depth+1,r.Inserted_Location);
                        }
                    }
                }
            }
        }

        List<List<HyperPoint>> list = new LinkedList<>();
        for(int i=0;i<r.children.length;i++){
            List<HyperPoint> sublist = new LinkedList<>();
            list.add(sublist);
        }

        for(int i=0;i<P.size();i++){
            HyperPoint p = P.get(i);
            for(int j=1;j<r.pivot.length-1;j++){
                if(j == 1){
                    if(p.coords[k]<r.pivot[j]) {
                        List<HyperPoint> sublist = list.get(j);
                        sublist.add(p);
                    }
                }else if(j == r.pivot.length-2){
                    if(p.coords[k]>=r.pivot[j]) {
                        List<HyperPoint> sublist = list.get(j);
                        sublist.add(p);
                    }
                }else{
                    if(p.coords[k]>=r.pivot[j-1] && p.coords[k]<r.pivot[j]) {
                        List<HyperPoint> sublist = list.get(j);
                        sublist.add(p);
                    }
                }
            }
        }
        for(int i=1;i<r.children.length;i++){
            if(list.get(i).size()>0) insert(r.children[i],list.get(i),depth+1);
        }
    }

    private void point_insert(IndexNode r, HyperPoint p, int depth,Vector<Integer> Inserted_Location) {
        if (r.isleaf){
            if (r.hasSpace) {
                r.hp[r.empty_id++] = p;
                Inserted_Location.add(p.inserted_location);
                if (r.empty_id == r.hp.length) r.hasSpace = false;
            }
        }

        int k = depth % K;
        for(int i=1;i<r.pivot.length-1;i++){
            if(i == 1){
                if(p.coords[k]<r.pivot[i]) {
                    point_insert(r.children[i],p,depth+1,Inserted_Location);
                }
            }else if(i == r.pivot.length-2){
                if(p.coords[k]>=r.pivot[i]) {
                    point_insert(r.children[i],p,depth+1,Inserted_Location);
                }
            }else{
                if(p.coords[k]>=r.pivot[i-1] && p.coords[k]<r.pivot[i-1]) {
                    point_insert(r.children[i],p,depth+1,Inserted_Location);
                }
            }
        }
    }

    private void Rebuild(IndexNode r,HyperPoint[] hp,int k){
        double[] k_value =new double[hp.length];
        for(int i=0;i<hp.length;i++){
            k_value[i] = hp[i].getcoords()[k];
        }
        UpdateRegression UR = r.train_rmi.getRmi().Updata_RMI(r,k_value);
        linear_model rootmodel = new linear_model(UR.getA(),UR.getB());
        linear_model[] model = r.getTrain_rmi().getRmi().getModel();
        model[0] = rootmodel;
    }


    private IndexNode UpdateTree(IndexNode r, HyperPoint[] points,HyperPoint[] NewPoint, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j) {
        if (r == null) r = new IndexNode(new HyperSpace(hmin, hmax));
        if(j-i+1 <= capacity){
            if(i > j) r = new IndexNode(new HyperSpace(hmin, hmax));
            else r = new IndexNode(new HyperSpace(hmin, hmax));
            r.Node_count = j-i+1;
            r.isleaf = true;
            HyperPoint[] tmp_hp = new HyperPoint[capacity];
            for(int anchor=0;anchor<j-i+1;anchor++){
                tmp_hp[anchor] = points[i+anchor];
            }
            if(j-i+1 == capacity) r.hasSpace = false;
            r.empty_id = j-i+1;
            r.hp = tmp_hp;
            return r;
        }
        int k = depth % K;

        TrainedRMI train_rmi = null;
        if(i==0 && j== points.length-1) train_rmi = UpdatePartition(points,NewPoint,r,i,j,k);
        else train_rmi = LRMI_Train_Sefl_Partition(points,i,j,k);

        double[] pivot = train_rmi.getPivot();
        int[] t = Partition_Space(points,i,j,k,pivot);

        HyperPoint[] listp = new HyperPoint[t.length];
        for(int h=0;h<listp.length;h++){
            listp[h] = points[t[h]];
        }

        HyperPoint[] hmid = new HyperPoint[listp.length+1];
        for(int h=0;h<=listp.length;h++){
            if(h < listp.length){
                HyperPoint hmid1 = new HyperPoint(hmax);
                hmid1.coords[k] = listp[h].coords[k];
                hmid[h] = hmid1;
            }else if(h == listp.length){
                HyperPoint hmid1 = new HyperPoint(hmin);
                //System.out.print("h: "+h+"\n");
                hmid1.coords[k] = listp[h-1].coords[k];
                hmid[h] = hmid1;
            }
        }

        r.pivot = new double[listp.length+2];
        r.children = new IndexNode[listp.length+1];

        for(int h=0;h<listp.length+2;h++){
            if(h == 0) r.pivot[h] = hmin.coords[k];
            else if(h == listp.length+1) r.pivot[h] = hmax.coords[k];
            else r.pivot[h] = listp[h-1].coords[k];
        }

        for(int h=0;h<=listp.length;h++){
            if(h == 0){
                if((t[h]-i)>1000000) r.children[h] =UpdateTree(r.children[h], points,NewPoint, hmin, hmid[h], depth + 1, i, t[h]);
                else r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, i, t[h]);
            }else if(h == listp.length){
                if((j-t[h-1]+1)>1000000) r.children[h] = UpdateTree(r.children[h], points, NewPoint,hmid[h], hmax, depth + 1, t[h-1]+1, j);
                else r.children[h] = insertBySorting(r.children[h], points, hmid[h], hmax, depth + 1, t[h-1]+1, j);
            }else{
                if((t[h]-t[h-1])>1000000)r.children[h] = UpdateTree(r.children[h], points,NewPoint, hmin, hmid[h], depth + 1, t[h-1], t[h]);
                else r.children[h] = insertBySorting(r.children[h], points, hmin, hmid[h], depth + 1, t[h-1], t[h]);
            }
        }
        return r;
    }


}
