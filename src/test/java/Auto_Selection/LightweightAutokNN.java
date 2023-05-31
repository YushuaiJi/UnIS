package Auto_Selection;

import Index.AutoIS;
import Index.HyperPoint;
import Index.IndexNode;
import RMI.linear_model;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static Index.AutoIS.*;
import static InputOutput.readfile.*;
import static InputOutput.readfile.quantile_value;
import static lsh.LSHMinHash.minLSH_AutoIS_kNN;
import static lsh.LSHMinHash.minLSH_AutoIS_kNN;

public class LightweightAutokNN {
    //normal
    @Test
    public void kNN_Ground_Truth_Generation() throws IOException {
        String[] address = new String[1];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        address[2] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[3] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[4] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[5] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        for(int i=0;i<address.length;i++){
            lightweight_GenerateData(address[i],i+1);
        }
    }
    public void lightweight_GenerateData(String address,int id) throws IOException {
        int sample_size = ReadDataAmount(address);
        int dim = ReadDim(address);
        int train_size = 50000;
        //double[][] data_log = new double[train_size*3][dim+2];//数据集+分类类型+数据占比
        Vector<LinkedList<Double>>  log = kNNdata(address,sample_size,train_size);
        int training_perenct = (int)(log.size()*0.8);
        //double[][] log = kNNdata(address,sample_size,train_size);
        String train_address = "src/test/java/expfile/AutokNN/Auto/train_data"+id+".txt";
        String test_address = "src/test/java/expfile/AutokNN/Auto/test_data"+id+".txt";
        SavetoCSV(log.get(0).size(),log,train_address,test_address,training_perenct);
    }
    public Vector<LinkedList<Double>>  kNNdata(String address, int sample_size, int train_sample) throws IOException {
        HyperPoint[] sample = ReadData(address,sample_size);
        LinkedList<double[]> list = quantile_value(sample,0.05);
        int K = list.get(0).length;
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));
        AutoIS kd1 = new AutoIS(K, min, max);
        System.out.print("kd1 begin "+"\n");
        kd1.ConstructionAutoIS(sample,2);
        kd1.setBallSpace(kd1.getRoot());
        System.out.print("kd2 begin "+"\n");
        AutoIS kd2 = new AutoIS(K, min, max);
        kd2.ConstructionAutoIS(sample,7);
        kd2.setBallSpace(kd2.getRoot());
        System.out.print("data generation"+"\n");
        HyperPoint[] qkNN = generate_sample(list,train_sample,K);
        int[][] lsh = minLSH_AutoIS_kNN(sample,0.75,qkNN);
        System.out.print("groundtruth begin"+"\n");
        Vector<LinkedList<Double>>  data_log = new Vector<>();
        linear_model[][] model = linear_model_Set(sample);

        long start = System.currentTimeMillis();
        long seconds = 400;
        long end = start + (seconds)*1000; //  seconds * 1000 ms/sec
        for(int i=0;i<train_sample*3;i++){
            int k = (int)(Math.random()*100+1);
            HyperPoint p = qkNN[i];
            //int[] t = new int[8];
            //HashSet<Integer> set = new HashSet<>();
            long[][] e = new long[8][2];
            SortComparator sc = new SortComparator();

            long[] bestmethod = new long[2];
            bestmethod[0] = Long.MAX_VALUE;
            bestmethod[1] = Long.MAX_VALUE;

            for(int j=1;j<=2;j++){
                for(int h=1;h<=4;h++){
                    long[] access = new long[2];
                    if(j==1) access = normal_node_access(kd1,j,p,k,bestmethod,h);
                    else access =  normal_node_access(kd2,j,p,k,bestmethod,h);
                    int loc = (int)(access[0]-1);
                    e[loc] = access;
                    //t[access[0]-1] = access[1];
                    //set.add(access[1]);
                }
            }
            double[] cdf = Cal_CDF(model,p);
            Arrays.sort(e, sc);

            System.out.print(" "+bestmethod[1]);
            System.out.print("\n");


            LinkedList<Double> log = new LinkedList<>();
            for(int j=0;j<p.getK();j++){
                log.add(p.getcoords()[j]);
            }

            for(int j=0;j<lsh[i].length;j++){
                log.add((double)lsh[i][j]);
            }
            for(int j=0;j<cdf.length;j++){
                log.add(cdf[j]);
            }
            log.add((double)k);
            log.add((double)bestmethod[1]);

            for(int h=0;h<e.length;h++){
                log.add((double)e[h][0]);
            }

            data_log.add(log);
            if(System.currentTimeMillis() >= end) {
                System.out.print(address+" "+"time out"+"\n");
                return data_log;
            }
        }
        System.out.print(address+" "+"not out"+"\n");
        return data_log;
    }

    //lightweight
    @Test
    public void Data() throws IOException {
        String[] address = new String[6];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        address[2] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[3] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[4] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[5] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        for(int i=0;i<address.length;i++){
            GenerateData(address[i],i+1);
        }
    }
    public void GenerateData(String address,int id) throws IOException {
        int sample_size = ReadDataAmount(address);
        int train_size = 50000;
        Vector<LinkedList<Double>> log = lightweight_kNNdata(address,sample_size,train_size);
        //LinkedList<LinkedList<Double>> log = lightweight_kNNdata(address,sample_size,train_size);
        int training_perenct = (int)(log.size()*0.8);
        String train_address = "src/test/java/expfile/AutokNN/Auto/lightweight_train_data"+id+".txt";
        String test_address = "src/test/java/expfile/AutokNN/Auto/lightweight_test_data"+id+".txt";
        SavetoCSV(log.get(0).size(),log,train_address,test_address,training_perenct);
    }
    public Vector<LinkedList<Double>>  lightweight_kNNdata(String address, int sample_size, int train_sample) throws IOException {
        HyperPoint[] sample = ReadData(address,sample_size);
        LinkedList<double[]> list = quantile_value(sample,0.05);
        int K = list.get(0).length;
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));
        AutoIS kd1 = new AutoIS(K, min, max);
        System.out.print("kd1 begin "+"\n");
        kd1.ConstructionAutoIS(sample,2);
        kd1.setBallSpace(kd1.getRoot());
        System.out.print("kd2 begin "+"\n");
        AutoIS kd2 = new AutoIS(K, min, max);
        kd2.ConstructionAutoIS(sample,7);
        kd2.setBallSpace(kd2.getRoot());
        System.out.print("data generation"+"\n");
        HyperPoint[] qkNN = generate_sample(list,train_sample,K);
        int[][] lsh = minLSH_AutoIS_kNN(sample,0.75,qkNN);
        System.out.print("groundtruth begin"+"\n");
        Vector<LinkedList<Double>>  data_log = new Vector<>();
        linear_model[][] model = linear_model_Set(sample);
        long start = System.currentTimeMillis();
        long seconds = 300;
        long end = start + (seconds)*1000; //  seconds * 1000 ms/sec
        for(int i=0;i<train_sample*3;i++){
            int k = (int)(Math.random()*100+1);
            HyperPoint p = qkNN[i];

            long[][] e = new long[8][2];
            SortComparator sc = new SortComparator();

            long[] bestmethod = new long[1];
            long[] r = normal_node_access(kd1,1,p,k,bestmethod,1);
            bestmethod[0] = r[1];
            e[0] = r;

            for(int j=1;j<=2;j++){
                for(int h=1;h<=4;h++){
                    long[] access = null;
                    if(j==1 && h == 1) continue;
                    if(j==1) access = node_access(kd1,j,p,k,bestmethod,h);
                    else access =  node_access(kd2,j,p,k,bestmethod,h);
                    int loc = (int)(access[0]-1);
                    e[loc] = access;
;                }
            }
            Arrays.sort(e, sc);
            double[] cdf = Cal_CDF(model,p);
            System.out.print(" "+e[0][0]);
            System.out.print("\n");

            LinkedList<Double> log = new LinkedList<>();
            for(int j=0;j<p.getK();j++){
                log.add(p.getcoords()[j]);
            }

            for(int j=0;j<lsh[i].length;j++){
                log.add((double)lsh[i][j]);
            }
            for(int j=0;j<cdf.length;j++){
                log.add(cdf[j]);
            }
            log.add((double)k);
            log.add((double)e[0][0]);

            for(int h=0;h<e.length;h++){
                log.add((double)e[h][0]);
            }
            data_log.add(log);
            if(System.currentTimeMillis() >= end) {
                System.out.print(address+" "+"time out"+"\n");
                return data_log;
            }
        }
        System.out.print(address+" "+"not out"+"\n");
        return data_log;
    }

    public static HyperPoint[] generate_sample(LinkedList<double[]> list,int train_sample,int K){
        int different_type_data_log = 3;
        HyperPoint[] qkNN = new HyperPoint[train_sample*different_type_data_log];
        for(int i=0;i<train_sample*3;i+=3){
            double[] kNN1 = new double[K];
            for(int j=0;j<K;j++){
                double tmp = Math.random()*(list.get(1)[j]-list.get(0)[j])*0.6;
                double add = (list.get(1)[j]-list.get(0)[j])*0.4;
                kNN1[j] = list.get(0)[j]-add+tmp;//list.get(0)[j]
            }
            HyperPoint point1 = new HyperPoint(kNN1);
            qkNN[i] = point1;

            double[] kNN2 = new double[K];
            for(int j=0;j<K;j++){
                double tmp = Math.random()*(list.get(1)[j]-list.get(0)[j])*0.6;
                double add = (list.get(1)[j]-list.get(0)[j])*0.2;
                kNN2[j] = list.get(0)[j]+add+tmp;//list.get(0)[j]
            }
            HyperPoint point2 = new HyperPoint(kNN2);
            qkNN[i+1] = point2;

            double[] kNN3 = new double[K];
            for(int j=0;j<K;j++){
                double tmp = Math.random()*(list.get(1)[j]-list.get(0)[j])*0.6;
                double add = (list.get(1)[j]-list.get(0)[j])*0.8;
                kNN3[j] = list.get(0)[j]+add+tmp;
            }
            HyperPoint point3 = new HyperPoint(kNN3);
            qkNN[i+2] = point3;
        }
        return qkNN;
    }
    public static long[] node_access(AutoIS kd,int kd_type,HyperPoint p,int k,long[] bestmethod,int m){
        long[] count = new long[1];
        kd.kNN(p,k,m,count,(int)bestmethod[0]);
        if(count[0]<bestmethod[0]){
            bestmethod[0] = count[0];
        }
        long[] res = new long[2];
        res[0] = (kd_type-1)*4+m;
        res[1] = count[0];
        System.out.print(((kd_type-1)*4+m)+" "+count[0]+" ");
        return res;
    }
    public static long[] normal_node_access(AutoIS kd,int kd_type,HyperPoint p,int k,long[] bestmethod,int m){
        long[] count = new long[1];
        kd.kNN(p,k,m,count);
        if(count[0]<bestmethod[0]){
            bestmethod[0] = count[0];
            bestmethod[1] = (kd_type-1)*4+m;
        }
        long[] res = new long[2];
        res[0] = (kd_type-1)*4+m;
        res[1] = count[0];
        System.out.print(((kd_type-1)*4+m)+" "+count[0]+" ");
        return res;
    }


    class SortComparator implements Comparator<long[]> {
        @Override
        public int compare(long[] o1, long[] o2) {
            if (o1[1] > o2[1])
                return 1;
            else if (o1[1] == o2[1])
                return 0;
            return -1;
        }
    }
}
