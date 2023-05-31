package Competitor;

import Auto_Selection.LightweightAutokNN;
import Competitor.AI_kd.AI_R_Tree;
import Competitor.AI_kd.BoundedPQueue;
import Competitor.AI_kd.HyperPoint;
import Competitor.AI_kd.IndexNode;
import Index.AutoIS;
import RMI.linear_model;
import com.carrotsearch.sizeof.RamUsageEstimator;
import org.junit.Test;
import org.tinspin.index.Index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static Index.AutoIS.Cal_CDF;
import static Index.AutoIS.linear_model_Set;
import static InputOutput.readfile.*;
import static lsh.LSHMinHash.minLSH_AutoIS_kNN;

public class AI_Tree {
    //index the construction
    @Test
    public void Construction_LearnIndex_time_test() throws IOException {
        String[] address = new String[8];
        //gas sensor没法用
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] ="src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[6] = "src/test/java/Dataset/8_11d_HT_Sensor_dataset.txt";
        address[7] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";


        double[] sample_ratio = new double[5];
        sample_ratio[0] = 0.2;
        sample_ratio[1] = 0.4;
        sample_ratio[2] = 0.6;
        sample_ratio[3] = 0.8;
        sample_ratio[4] = 1;

        int[] sample_pop = new int[address.length];
        for(int i=0;i<address.length;i++){
            sample_pop[i] = ReadDataAmount(address[i]);
        }
        long[][] data_log = new long[address.length][sample_ratio.length];//数据集+分类类型+数据占比

        for(int i = 0;i<address.length;i++){
            long[] log = Construction_tree(i,address,sample_ratio,sample_pop[i]);
            data_log[i] = log;
        }
        SavetoCSV(data_log,"src/test/java/expfile/AI_R/indextime.txt");
    }
    public static HyperPoint[] ReadData(String address, int number) throws IOException {
        Vector<HyperPoint> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int count = 0;
        while (buffReader.ready()) {
            if(count == number) break;
            count++;
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            double[] tmp_p = new double[arr.length];
            for(int i=0;i<arr.length;i++){
                if(i == arr.length-1){
                    double loc = Double.parseDouble(arr[i].substring(0, arr[i].length() - 4));
                    tmp_p[i] = loc;
                }else{
                    double loc = Double.parseDouble(arr[i]);
                    tmp_p[i] = loc;
                }
            }
            HyperPoint p = new HyperPoint(tmp_p);
            points.add(p);
        }
        //System.out.print(points.size()+"\n");
        HyperPoint[] sample = new HyperPoint[points.size()];
        for(int i=0;i<points.size();i++) {
            sample[i] = points.get(i);
        }
        return sample;
    }
    public static LinkedList<double[]> Max_Min_value(HyperPoint[] hp){
        LinkedList<double[]> list = new LinkedList<>();
        int k = hp[0].getK();
        double[] max = new double[k];
        double[] min = new double[k];
        for(int i=0;i<k;i++){
            max[i] = Double.MIN_VALUE;
            min[i] = Double.MAX_VALUE;
        }

        for(int i=0;i<hp.length;i++){
            for(int j=0;j<k;j++){
                if(hp[i].getcoords()[j] >max[j]) max[j] =hp[i].getcoords()[j];
                if(hp[i].getcoords()[j] <min[j]) min[j] =hp[i].getcoords()[j];
            }
        }
        list.add(min);
        list.add(max);
        return list;
    }
    public static long[] Construction_tree(int ID,String[] Address, double[] sample_ratio,int sample_size) throws IOException {
        String address = Address[ID];
        long[] log = new long[sample_ratio.length];
        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AI_R_Tree kd1 = new AI_R_Tree(K, min, max);
            long start = System.currentTimeMillis();
            kd1.Construction_AI_KD(sample);
            kd1.Add_ID(kd1.getRoot(),0);
            long end = System.currentTimeMillis();
            log[i] = end-start;
        }
        return log;
    }

    //memory test:
    @Test
    public void Construction_LearnIndex_memory_test() throws IOException {
        String[] address = new String[8];
        //gas sensor没法用
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] ="src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[6] = "src/test/java/Dataset/8_11d_HT_Sensor_dataset.txt";
        address[7] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";


        int[] sample_pop = new int[address.length];
        for(int i=0;i<address.length;i++){
            sample_pop[i] = ReadDataAmount(address[i]);
        }
        long[] data_log = new long[address.length];//数据集+分类类型+数据占比

        for(int i = 0;i<address.length;i++){
            long log = memory_tree(address[i]);
            data_log[i] = log;
        }
        SavetoCSV(data_log,"src/test/java/expfile/AI_R/memory.txt");
    }
    public static long memory_tree(String address) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        LinkedList<double[]> list = Max_Min_value(sample);
        int K = list.get(0).length;
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));
        AI_R_Tree kd1 = new AI_R_Tree(K, min, max);
        kd1.Construction_AI_KD(sample);
        kd1.Add_ID(kd1.getRoot(),0);
        long log = RamUsageEstimator.sizeOf(kd1);
        return log/(1024*1024);
    }
    //generate the kNN sample
    @Test
    public void lightweight_Data() throws IOException {
        String[] address = new String[1];
        //address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        //address[1] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        //address[2] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        //address[3] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        //address[4] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[0] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        for(int i=0;i<address.length;i++){
            kNN_Generation(address[i],i+1);
        }
    }
    public void kNN_Generation(String address,int id) throws IOException {
        int sample_size = ReadDataAmount(address);
        int train_size = 10000;
        //double[][] data_log = new double[train_size*3][dim+2];//数据集+分类类型+数据占比
        Vector<LinkedList<Double>>  log = kNNdata(address,sample_size,train_size);
        int training_perenct = (int)(log.size()*0.9);

        String train_address = "src/test/java/expfile/AI_R/train_data"+6+".txt";
        String test_address = "src/test/java/expfile/AI_R/test_data"+6+".txt";
        SavetoCSV(log,train_address,test_address,training_perenct);
    }
    public Vector<LinkedList<Double>> kNNdata(String address, int sample_size, int train_sample) throws IOException {
        HyperPoint[] sample = ReadData(address,sample_size);
        LinkedList<double[]> list = quantile_value(sample,0.05);
        int K = list.get(0).length;
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));
        AI_R_Tree kd1 = new AI_R_Tree(K, min, max);
        kd1.Construction_AI_KD(sample);
        kd1.Add_ID(kd1.getRoot(),0);
        kd1.traversal(kd1.getRoot(),0);
        HyperPoint[] qkNN = generate_sample(list,train_sample,K);
        Vector<LinkedList<Double>>  data_log = new Vector<>();
        for(int i=0;i<train_sample*3;i++){
            LinkedList<Double> log = new LinkedList<>();
            int k = (int)(Math.random()*100+1);
            HyperPoint p = qkNN[i];
            BoundedPQueue k_nearest_points = kd1.kNN(p,k);
            System.out.print(p.getK()+"\n");
            for(int j=0;j<p.getK();j++){
                log.add(p.getcoords()[j]);
            }

            log.add((double)k);
            //HashSet<Double> set = new HashSet<>();
            double[] map = new double[512];
            if(k_nearest_points.Size() == 0) System.out.print("wrong");
            for(int j=0;j<k_nearest_points.Size();j++){
                HyperPoint tmp = k_nearest_points.dequeue();
                map[tmp.getID()-1] = 1;
            }
            for(int j=0;j<map.length;j++){
                log.add(map[j]);
            }
            data_log.add(log);
            //kd1.get_all_nearest_points(p,k_nearest_points,lls,kd1.getRoot(),0);
        }
        System.out.print("success"+"\n");
        return data_log;
    }
    public static LinkedList<double[]> quantile_value(HyperPoint[] hp, double ratio){
        LinkedList<double[]> list = new LinkedList<>();
        int k = hp[0].getK();
        double[] max = new double[k];
        double[] min = new double[k];
        for(int i=0;i<k;i++){
            double[] arr = new double[hp.length];
            for(int j=0;j<hp.length;j++){
                arr[j] = hp[j].getcoords()[i];
            }
            Arrays.sort(arr);
            int lo = (int)(ratio * hp.length);
            int hi = (int)((1-ratio)* hp.length);
            min[i] = arr[lo];
            max[i] = arr[hi];
        }
        list.add(min);
        list.add(max);
        return list;
    }
    public static HyperPoint[] generate_sample(LinkedList<double[]> list, int train_sample, int K){
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

    // prediction time
    @Test
    public void prediction_time() throws IOException {
        int l = 6;
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        address[2] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[3] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[4] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[5] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";


        String[] Query_address = new String[l];
        Query_address[0] = "src/test/java/expfile/AutokNN/testforkNN/1_2d_T_drive_No_duplication.txt";
        Query_address[1] = "src/test/java/expfile/AutokNN/testforkNN/3_3d_spatial_network.txt";
        Query_address[2] = "src/test/java/expfile/AutokNN/testforkNN/4_3d_Snapenet_new.txt";
        Query_address[3] = "src/test/java/expfile/AutokNN/testforkNN/5_3d_ConfLongDemo_JSI.txt";
        Query_address[4] = "src/test/java/expfile/AutokNN/testforkNN/6_4d_NYC_10M_transition_edit.txt";
        Query_address[5] = "src/test/java/expfile/AutokNN/testforkNN/10_24d_KEGG_Metabolic.txt";

        String[] bestmethod = new String[l];
        bestmethod[0] = "src/test/java/expfile/AI_R/bestmethod/res1.txt";
        bestmethod[1] = "src/test/java/expfile/AI_R/bestmethod/res2.txt";//这个行
        bestmethod[2] = "src/test/java/expfile/AI_R/bestmethod/res3.txt";
        bestmethod[3] = "src/test/java/expfile/AI_R/bestmethod/res4.txt"; //三快
        bestmethod[4] = "src/test/java/expfile/AI_R/bestmethod/res5.txt";
        bestmethod[5] = "src/test/java/expfile/AI_R/bestmethod/res6.txt";


        for(int i=0;i<address.length;i++){
            time_kNN(address[i],Query_address[i],bestmethod[i]);
        }
    }
    public static void time_kNN(String address, String Query_address,String best_method_address) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        HyperPoint[] query = ReadQuery(Query_address,sample[0].getK());
        int[] k = readLastNumbers(Query_address);
        List<LinkedList<Integer>> method = readTxtFile(best_method_address);

        LinkedList<double[]> list = quantile_value(sample,0.05);
        int K = list.get(0).length;
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));
        AI_R_Tree kd1 = new AI_R_Tree(K, min, max);
        kd1.Construction_AI_KD(sample);
        kd1.Add_ID(kd1.getRoot(),0);
        kd1.traversal(kd1.getRoot(),0);

        long start = System.currentTimeMillis();
        for(int i=0;i<query.length;i++){
            kd1.learned_kNN(query[i],k[i],method.get(i),new long[1]);
        }
        long end = System.currentTimeMillis();
        System.out.print(" "+(end-start)+"\n");
    }

    //prediction node
    @Test
    public void prediction_node() throws IOException {
        int l = 6;
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        address[2] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[3] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[4] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[5] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";


        String[] Query_address = new String[l];
        Query_address[0] = "src/test/java/expfile/AutokNN/testforkNN/1_2d_T_drive_No_duplication.txt";
        Query_address[1] = "src/test/java/expfile/AutokNN/testforkNN/3_3d_spatial_network.txt";
        Query_address[2] = "src/test/java/expfile/AutokNN/testforkNN/4_3d_Snapenet_new.txt";
        Query_address[3] = "src/test/java/expfile/AutokNN/testforkNN/5_3d_ConfLongDemo_JSI.txt";
        Query_address[4] = "src/test/java/expfile/AutokNN/testforkNN/6_4d_NYC_10M_transition_edit.txt";
        Query_address[5] = "src/test/java/expfile/AutokNN/testforkNN/10_24d_KEGG_Metabolic.txt";

        String[] bestmethod = new String[l];
        bestmethod[0] = "src/test/java/expfile/AI_R/bestmethod/res1.txt";
        bestmethod[1] = "src/test/java/expfile/AI_R/bestmethod/res2.txt";//这个行
        bestmethod[2] = "src/test/java/expfile/AI_R/bestmethod/res3.txt";
        bestmethod[3] = "src/test/java/expfile/AI_R/bestmethod/res4.txt"; //三快
        bestmethod[4] = "src/test/java/expfile/AI_R/bestmethod/res5.txt";
        bestmethod[5] = "src/test/java/expfile/AI_R/bestmethod/res6.txt";


        for(int i=0;i<address.length;i++){
            node_kNN(address[i],Query_address[i],bestmethod[i]);
        }
    }
    public static void node_kNN(String address, String Query_address,String best_method_address) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        HyperPoint[] query = ReadQuery(Query_address,sample[0].getK());
        int[] k = readLastNumbers(Query_address);
        List<LinkedList<Integer>> method = readTxtFile(best_method_address);

        LinkedList<double[]> list = quantile_value(sample,0.05);
        int K = list.get(0).length;
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));
        AI_R_Tree kd1 = new AI_R_Tree(K, min, max);
        kd1.Construction_AI_KD(sample);
        kd1.Add_ID(kd1.getRoot(),0);
        kd1.traversal(kd1.getRoot(),0);
        long[] count = new long[1];
        for(int i=0;i<query.length;i++){
            kd1.learned_kNN(query[i],k[i],method.get(i),count);
        }

        System.out.print(" "+count[0]+"\n");
    }

    public static int[] readLastNumbers(String filename) {
        List<Integer> numbers = new ArrayList<Integer>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int lastNumber = (int)Double.parseDouble(fields[fields.length - 1].trim());
                numbers.add(lastNumber);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int[] result = new int[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            result[i] = numbers.get(i);
        }
        return result;
    }

    public static List<LinkedList<Integer>> readTxtFile(String filePath) {
        List<LinkedList<Integer>> result = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                LinkedList<Integer> list = new LinkedList<>();

                for (String value : values) {
                    list.add(Integer.parseInt(value));
                }

                result.add(list);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static HyperPoint[] ReadQuery(String address, int k) throws IOException {
        Vector<HyperPoint> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        String tmp = null;
        if(buffReader.ready()) tmp = buffReader.readLine()+s;
        while (buffReader.ready()) {
            tmp = buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            double[] tmp_p = new double[k];
            for(int i=0;i<k;i++){
                double loc = Double.parseDouble(arr[i]);
                tmp_p[i] = loc;
            }
            HyperPoint p = new HyperPoint(tmp_p);
            points.add(p);
        }
        //System.out.print(points.size()+"\n");
        HyperPoint[] sample = new HyperPoint[points.size()];
        for(int i=0;i<points.size();i++) {
            sample[i] = points.get(i);
        }
        return sample;
    }

    // 625
    // 3013
    // 5831
    // 3112
    // 2711
    // 9001

}
