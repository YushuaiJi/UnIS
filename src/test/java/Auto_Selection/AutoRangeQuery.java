package Auto_Selection;

import Index.AutoIS;
import Index.HyperPoint;
import Index.HyperSpace;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static InputOutput.readfile.*;
import static lsh.LSHMinHash.minLSH_AutoIS_kNN;
import static lsh.LSHMinHash.minLSH_AutoIS_RQ;

public class AutoRangeQuery {

    @Test
    public void Range_query_ground_truth() throws IOException {
        String[] address = new String[1];
        //address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        //address[0] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        //address[0] = "src/test/java/Dataset/3_3d_spatial_network.txt";//一三快
        //address[0] = "src/test/java/Dataset/4_3d_Snapenet_new.txt"; //一二快
        //address[0] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        //address[0] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        //address[0] = "src/test/java/Dataset/7_5d_Phones_accelerometer_new.txt"; //三四差不多
        //address[0] = "src/test/java/Dataset/8_11d_HT_Sensor_dataset.txt";
        address[0] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        for(int i=0;i<address.length;i++){
            lightweight_GenerateData(address[i],i+1);
        }
    }
    public void lightweight_GenerateData(String address,int id) throws IOException {
        int sample_size = ReadDataAmount(address);
        int dim = ReadDim(address);
        int train_size = 50000;
        //double[][] data_log = new double[train_size*3][dim+2];//数据集+分类类型+数据占比
        Vector<LinkedList<Double>> log = Range_query_data(address,sample_size,train_size);
        int training_perenct = (int)(log.size()*0.8);
        //double[][] log = kNNdata(address,sample_size,train_size);
        String train_address = "src/test/java/expfile/AutokNN/Auto/range_train_data"+id+".txt";
        String test_address = "src/test/java/expfile/AutokNN/Auto/range_test_data"+id+".txt";
        SavetoCSV(log.get(0).size(),log,train_address,test_address,training_perenct);
    }
    public Vector<LinkedList<Double>> Range_query_data(String address, int sample_size, int train_sample) throws IOException {
        HyperPoint[] sample = ReadData(address,sample_size);
        System.out.print(sample[0].getK());
        LinkedList<double[]> list = quantile_value(sample,0.05);
        int K = list.get(0).length;
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));
        AutoIS kd1 = new AutoIS(K, min, max);
        kd1.ConstructionAutoIS(sample,2);
        kd1.setBallSpace(kd1.getRoot());

        AutoIS kd2 = new AutoIS(K, min, max);
        kd2.ConstructionAutoIS(sample,7);
        kd2.setBallSpace(kd2.getRoot());

        HyperSpace[] Rand_Range = generate_sample(list,train_sample,sample);
        int[][][] lsh = minLSH_AutoIS_RQ(sample,0.75,Rand_Range);

        Vector<LinkedList<Double>> data_log = new Vector<>();
        long start = System.currentTimeMillis();
        long seconds = 10;
        long end = start + (seconds)*1000; //  seconds * 1000 ms/sec
        for(int i=0;i<train_sample;i++){
            HyperSpace hs = Rand_Range[i];

            int[][] e = new int[4][2];
            SortComparator sc = new SortComparator();


            int[] count = new int[1];
            kd1.RangeQuery(hs,1,count);
            e[0][0] = 1;
            e[0][1] = count[0];
            System.out.print("method1 "+e[0][1]+"|| ");

            count = new int[1];
            kd1.RangeQuery(hs,2,count);
            e[1][0] = 2;
            e[1][1] = count[0];
            System.out.print("method2 "+count[0]+"|| ");

            count = new int[1];
            kd2.RangeQuery(hs,1,count);
            e[2][0] = 3;
            e[2][1] = count[0];
            System.out.print("method3 "+count[0]+"|| ");

            count = new int[1];
            kd2.RangeQuery(hs,2,count);
            e[3][0] = 4;
            e[3][1] = count[0];
            System.out.print("method4 "+count[0]+"|| ");

            Arrays.sort(e, sc);
            System.out.print("best method "+e[0][0]+"\n");

            LinkedList<Double> log = new LinkedList<>();

            for(int j=0;j<K;j++){
                log.add(hs.getMin().getcoords()[j]);
            }
            for(int j=0;j<K;j++){
                log.add(hs.getMax().getcoords()[j]);
            }

            for(int j=0;j<lsh[i][0].length;j++){
                log.add((double)lsh[i][0][j]);
            }
            for(int j=0;j<lsh[i][1].length;j++){
                log.add((double) lsh[i][1][j]);
            }

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

    public static HyperSpace[] generate_sample(LinkedList<double[]> list,int train_sample,HyperPoint[] sample){
        int K = sample[0].getK();
        HyperSpace[] Rand_Range = new HyperSpace[train_sample];
        for(int i=0;i<train_sample;i++) {
            int rand_ID = (int)(Math.random()*sample.length);
            HyperPoint p = sample[rand_ID];
            double[] min_val = new double[K];
            double[] max_val = new double[K];
            for(int j=0;j<K;j++){
                double var = list.get(1)[j]-list.get(0)[j];
                double add_one = Math.random()*var;
                double add_two = Math.random()*var;
                min_val[j] = p.getcoords()[j]-add_one;
                max_val[j] = p.getcoords()[j]+add_two;
            }
            HyperPoint min_p = new HyperPoint(min_val);
            HyperPoint max_p = new HyperPoint(max_val);

            HyperSpace hs = new HyperSpace(min_p,max_p);
            Rand_Range[i] = hs;
        }
        return Rand_Range;
    }

    class SortComparator implements Comparator<int[]> {
        @Override
        public int compare(int[] o1, int[] o2) {
            if (o1[1] > o2[1])
                return 1;
            else if (o1[1] == o2[1])
                return 0;
            return -1;
        }
    }

}
