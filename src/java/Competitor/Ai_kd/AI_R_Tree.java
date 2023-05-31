package Competitor.AI_kd;


import org.tinspin.index.Index;

import java.util.LinkedList;

public class AI_R_Tree {
    //先搜索，后正常搜索
    //
    IndexNode root;

    IndexNode[] dictionary = new IndexNode[(int)Math.pow(2,9)];
    int K = 2;
    double RANGE = 1.0;
    // HyperPoint min, max are determined the range of KDTree Space
    HyperPoint min, max;
    int capacity = 20;

    int ID = 1;

    public IndexNode getRoot() {
        return root;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public AI_R_Tree(int K) {
        this.K = K;
        root = null;
        double[] vals = new double[K];
        min = new HyperPoint(vals);
        for (int i = 0; i < K; i++)
            vals[i] = RANGE;
        max = new HyperPoint(vals);
    }

    public AI_R_Tree(int K, HyperPoint min, HyperPoint max) {
        this.K = K;
        this.min = min;
        this.max = max;
        root = null;
    }

    private IndexNode insertByMedianFinding(IndexNode r, HyperPoint[] points, HyperPoint hmin, HyperPoint hmax, int depth, int i, int j){
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
    public void Construction_AI_KD(HyperPoint[] points) {
        int num = points.length;
        HyperPoint hmin = new HyperPoint(min);
        HyperPoint hmax = new HyperPoint(max);
        root = insertByMedianFinding(root, points, hmin, hmax, 0, 0, num - 1);
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

    private static int partition(HyperPoint[] points, int k, int beg, int end) {
        HyperPoint pivot = points[beg];
        int i = beg, j = end + 1;
        while (true) {
            while (++i <= end && points[i].getcoords()[k] < pivot.getcoords()[k])
                ;
            while (--j > beg && points[j].getcoords()[k] >= pivot.getcoords()[k])
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

    public BoundedPQueue kNN(HyperPoint pointKD, int K){
        BoundedPQueue k_nearest_points = new BoundedPQueue(K);
        get_all_nearest_points(pointKD,k_nearest_points,root,0,false,null,new long[1]);
        return k_nearest_points;
    }
    public BoundedPQueue learned_kNN(HyperPoint pointKD, int K,LinkedList<Integer> dir,long[] count){
        BoundedPQueue k_nearest_points = new BoundedPQueue(K);
        for(int i=0;i<dir.size();i++){
            if(dir.get(i) != 0) get_all_nearest_points(pointKD,k_nearest_points,dictionary[dir.get(i)],0,true,null,count);
        }
        get_all_nearest_points(pointKD,k_nearest_points,root,0,false,null,count);
        //get_all_nearest_points(pointKD,k_nearest_points,root,0);
        return k_nearest_points;
    }

    int org_depth = 8;
    private void get_all_nearest_points(HyperPoint pointKD, BoundedPQueue knp, IndexNode curr, int depth,boolean flag,LinkedList<Integer> dir,long[] count){
        if(dir != null){
            if(!flag && depth==org_depth && dir.contains(root)){
                return;
            }
        }
        if(curr == null){
            return;
        }
        if(curr.isleaf){
            count[0] = count[0]+curr.hp.length;
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
        if(pointKD.getcoords()[keyIndex] < curr.pivot){
            get_all_nearest_points(pointKD, knp,curr.left,depth+1,flag,dir,count);
            near_is_left = true;
        }else{
            //to_left = true;
            get_all_nearest_points(pointKD, knp,curr.right,depth+1,flag,dir,count);
            near_is_right = true;
        }

        if(knp.Size()!= knp.maxSize()||(curr.p.coords[keyIndex] - pointKD.coords[keyIndex])
                *(curr.p.coords[keyIndex] - pointKD.coords[keyIndex]) < value){
            if(!near_is_left) get_all_nearest_points(pointKD, knp,curr.left,depth+1,flag,dir,count);
            else get_all_nearest_points(pointKD, knp,curr.right,depth+1,flag,dir,count);
        }
    }

    public void Add_ID(IndexNode root, int depth){
        if(depth == org_depth) {
            root.ID = ID;
            dictionary[ID-1] = root;
            ID++;
            //System.out.print(" " + root.ID + "\n");
        }else if(depth > org_depth){
            root.ID = root.parent.ID;
            //if(root.parent.ID != 0) System.out.print(" " + root.ID + "\n");
        }

        if(root.isleaf){
            HyperPoint[] hp = root.hp;
            for(int i=0;i<hp.length;i++){
                hp[i].ID = root.ID;
            }
            return;
        }

        root.left.parent = root;
        root.right.parent = root;
        Add_ID(root.left,depth+1);
        Add_ID(root.right,depth+1);
    }

    public void traversal(IndexNode root, int depth){
        if(root.isleaf){
            HyperPoint[] hp = root.hp;
            for(int i=0;i<hp.length;i++){
                if(hp[i].getID() != 0) System.out.print(hp[i].getID()+"\n");
            }
            return;
        }
        Add_ID(root.left,depth+1);
        Add_ID(root.right,depth+1);
    }
}
