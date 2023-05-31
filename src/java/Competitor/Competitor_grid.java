package Competitor;

import Index.HyperPoint;
import RMI.linear_model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Competitor_grid {

    int  fine_grained = 10;
    HashMap<Integer,double[]> map;

    LinkedList<HyperPoint[]> grid;

    public Competitor_grid(int fine_grained,HashMap<Integer,double[]> map) {
        this.fine_grained = fine_grained;
        this.map = map;
    }

    public static double[] CDF(HashMap<Integer,double[]> map, double[] point) {
        double[] index = new double[point.length];
        for(int i=0;i<point.length;i++){
            double[] arr = map.get(i+1);
            index[i] = Arrays.binarySearch(arr, point[i])/arr.length;
        }
        return index;
    }

    public int calculate_address(HyperPoint p){
        double d = 1.0/fine_grained;
        double[] cdf = CDF(map,p.getcoords());
        int address = 0;
        for(int j=cdf.length-1;j>=0;j--){
            int k = 0;
            for(int u=1;u<=fine_grained;u++){
                if(cdf[j]<d*u){
                    k = u;
                    break;
                }
            }
            address += (int)Math.pow(fine_grained,j)*(k-1);
        }
        return address;
    }

    public static HashMap<Integer,double[]> generate_sorted_map(HyperPoint[] hp){
        HashMap<Integer,double[]> map = new HashMap<>();
        for(int i=0;i<hp[0].getK();i++){
            double[] arr = new double[hp.length];
            for(int j=0;j<arr.length;j++){
                arr[j] = hp[j].getcoords()[i];
            }
            Arrays.sort(arr);
            map.put(i+1,arr);
        }
        return map;
    }
    public  LinkedList<HyperPoint[]> Grid_Generation(HyperPoint[] hp,HashMap<Integer,double[]> map){
        int[] ID_Count = new int[(int)Math.pow(fine_grained,hp[0].getK())];
        int[] gridID = new int[hp.length];
       // double d = 1.0/fine_grained;
        double[] d = new double[hp[0].getK()];
        double[] max = new double[hp[0].getK()];
        double[] min  = new double[hp[0].getK()];

        for(int i=0;i<hp.length;i++){
            for(int j=0;j<hp[0].getK();j++){
                max[j] = Math.max(max[j],hp[i].getcoords()[j]);
                min[j] = Math.min(min[j],hp[i].getcoords()[j]);
            }
        }
        for(int i=0;i<d.length;i++){
            d[i] = (max[i]-min[i])/fine_grained;
        }
        System.out.print("element+ prepare"+"\n");
        for(int i=0;i<hp.length;i++){
           // double[] cdf = CDF(map,hp[i].getcoords());
            int address = 0;
            for(int j=hp[i].getcoords().length-1;j>=0;j--){
                int k = fine_grained;
                for(int u=1;u<=fine_grained;u++){
                    if(hp[i].getcoords()[j]<=d[j]*u+min[j]){
                        k = u;
                        break;
                    }
                }
                address += (int)Math.pow(fine_grained,j)*(k-1);//18+
                ID_Count[address] = ID_Count[address]+1;
            }
            gridID[i] = address;
        }

        LinkedList<HyperPoint[]> list = new LinkedList<>();
        HashMap<Integer,Integer> dictionary = new HashMap<>();

        for(int i=0;i<ID_Count.length;i++){
            HyperPoint[] arr = new HyperPoint[ID_Count[i]];
            if(ID_Count[i]>0) System.out.print(ID_Count[i]+"\n");
            list.add(arr);
            dictionary.put(i,0);
        }
        System.out.print("finish"+"\n");

        for(int i=0;i<hp.length;i++){
           HyperPoint[] arr = list.get(gridID[i]);
           int a  =  dictionary.get(gridID[i]);
           arr[a] = hp[i];
           dictionary.put(gridID[i],a+1);
          // System.out.print("ele added"+"\n");
        }
        System.out.print("added element"+"\n");
       return list;
    }



    public void kNN(HyperPoint p,LinkedList<HyperPoint[]> list,int k){
          int address = calculate_address(p);
          PriorityQueue<Double> pq = new PriorityQueue<>();
          for(int i=address,j=address;i>0 && j<Math.pow(fine_grained,p.getK());i++,j--){
              if(i==j){
                  kNNhelper(p,list,i,k,pq);
              }else if(pq.size()<k){
                  kNNhelper(p,list,j,k,pq);
                  kNNhelper(p,list,i,k,pq);
              }
              if(pq.size()>k) break;
          }
    }

    public void  kNNhelper(HyperPoint p,LinkedList<HyperPoint[]> list,int i,int k,PriorityQueue<Double> pq){
        HyperPoint[] arr = list.get(i);
        for(int h=0;h<arr.length;h++){
            double distance = distance(p.getcoords(),arr[h].getcoords());
            if(pq.size()<k){
                pq.add(distance);
            }else if(!pq.isEmpty()&& pq.peek()>distance){
                pq.add(distance);
            }
        }
    }

    public static double distance(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("The dimensions of the two points do not match.");
        }
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

}
