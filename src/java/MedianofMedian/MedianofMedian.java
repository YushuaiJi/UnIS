package MedianofMedian;

import Index.HyperPoint;

import java.util.Arrays;

public class MedianofMedian {

    public static void swap(HyperPoint[] a, int i, int j){
        HyperPoint temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
    public static void insertSort(HyperPoint[] a, int beg, int end,int k){
        for (int i=beg; i<end; ++i) {
            HyperPoint key=a[i];
            int j=i-1;
            while (j>=beg && a[j].getcoords()[k]>key.getcoords()[k]) {
                a[j+1] = a[j];
                j = j-1;
            }
            a[j+1] = key;
        }
    }
    public static int partition(HyperPoint[] a, int l, int r,int k){
        // partition, Lomuto version
        HyperPoint pivot = a[r];
        int i = (l-1);
        for (int j=l; j<r; j++) {
            if (a[j].getcoords()[k]<=pivot.getcoords()[k]) {
                i++;
                swap(a, i ,j);
            }
        }
        swap(a, i+1, r);
        return i+1;
    }
    public static int check(HyperPoint[] a, int l, int r, int elem,int k){
        // in-place median of medians algorithm
        // a[] - array
        // l - element "from"
        // r - element "to"
        // elem - kth element we're looking for
        if (r-l<=5){
            insertSort(a, l, r+1,k);
            return l+elem-1;
        } else{
            int count=l;
            for (int i=l;i<r;i=i+5){
                if (i+5<r) {
                    insertSort(a, i, i+5,k);
                    swap(a, count, i+2);
                    // swapping the found medians with elements
                    // at the beginning of the considered array (from "l")
                    // instead of creating a new array to store medians
                }
                else {
                    insertSort(a, i, r,k);
                    swap(a, count, (i+r)/2);
                }
                count++;
            }
            int medianaMedian = check(a, l, count-1, (count-l+1)/2,k);
            swap(a, medianaMedian, r);
            int pivot = partition(a, l, r,k);
            int elem2 = pivot-l+1;
            if (elem==elem2) return pivot;
            else if (elem<elem2) return check(a, l, pivot-1, elem,k);
            else return check(a, pivot+1, r, elem-elem2,k);
        }
    }
    public static void main(String[] args) {
        // assumption: query is >=0 and <arr.length
        // elements dont need to be distinct
        HyperPoint[] test = new HyperPoint[200];
        double[] test1 = new double[200];
        for(int i=0;i<200;i++){
            double[] arr = new double[1];
            double val = (double) (Math.random());
            arr[0] = val;
            test1[i] = val;
            HyperPoint hp = new HyperPoint(arr);
            test[i] = hp;
        }
        //int query = 20;
        //System.out.println(test[check(test,0,test.length-1,query)],0);
        int query = test.length/2;
        System.out.println(test[check(test,0,test.length-1,query,0)]+"\n");
        Arrays.sort(test1);
        System.out.print(test1[test1.length/2]);
    }
}
