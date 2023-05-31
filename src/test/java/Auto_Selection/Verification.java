package Auto_Selection;

import Index.AutoIS;
import Index.HyperPoint;
import Index.HyperSpace;
import InputOutput.New_Data;
import RMI.linear_model;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static Index.AutoIS.*;
import static InputOutput.readfile.*;

public class Verification {

    @Test
    public void Node_time() throws IOException {
        int l = 6;
        String[] Query_address = new String[l];
        String[] best_method_address = new String[l];
        String[] address = new String[6];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        address[2] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[3] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[4] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[5] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        
        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/test_data"+(i+1)+".txt";
        }
        for(int i=0;i<l;i++){
            best_method_address[i] = "src/test/java/expfile/AutokNN/Auto/predict_val("+(i+1)+").csv";
        }
        long[][] log = new long[6][9];
        for(int i=0;i<l;i++){
            log[i] = time_Node(address[i],Query_address[i],best_method_address[i]);
        }

        SavetoCSV(log,"src/test/java/expfile/AutokNN/Prediction/kNN_time.csv");
    }
    public static long[] time_Node(String address, String Query_address,String best_method_address) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        HyperPoint[] query = ReadQuery(Query_address,sample[0].getK());
        int[] k = Readk(Query_address,sample[0].getK());
        int[] method = Read_single_res(best_method_address,query.length);
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

        long[] log = new long[9];
        for(int i=1;i<=2;i++){
            for(int j=1;j<=4;j++){
                if(i==1) {
                    log[j-1] = kNN_time_sum(query, kd1, k, j);
                    //System.out.print(log[j - 1] + " ");
                }else{
                    log[j+3] = kNN_time_sum(query, kd2, k, j);
                    //System.out.print(log[j + 3]  + " ");
                }
            }
        }
        long sum = 0;
        for(int i=0;i<query.length;i++){
            int query_k = k[i];
            HyperPoint p = query[i];
            long[] count = new long[1];
            long start = 0;
            long end = 0;
            if(method[i] < 5) {
                start = System.currentTimeMillis();
                kd1.kNN(p, query_k, method[i], count);
                end = System.currentTimeMillis();
            } else {
                start = System.currentTimeMillis();
                kd2.kNN(p, query_k, method[i] - 4, count);
                end = System.currentTimeMillis();
            }
            sum += end - start;
        }
        log[8] = sum;
        System.out.print("best "+log[8]+"\n");
        return log;
    }

    @Test
    public void Node_Access() throws IOException {
        int l = 6;
        String[] Query_address = new String[l];
        String[] best_method_address = new String[l];
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        address[2] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[3] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[4] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[5] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";

        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/test_data"+(i+1)+".txt";
        }

        for(int i=0;i<l;i++){
            best_method_address[i] = "src/test/java/expfile/AutokNN/Auto/predict_val("+(i+1)+").csv";
        }

        long[][] log = new long[6][9];
        for(int i=0;i<l;i++){
            log[i] = test_Node_Access(address[i],Query_address[i],best_method_address[i]);
        }
        SavetoCSV(log,"src/test/java/expfile/AutokNN/Auto/kNN_Node.csv");
    }
    public static long[] test_Node_Access(String address, String Query_address,String best_method_address) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        HyperPoint[] query = ReadQuery(Query_address,sample[0].getK());
        int[] k = Readk(Query_address,sample[0].getK());
        int[] method = Read_single_res(best_method_address,query.length);
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

        long[] log = new long[9];
        for(int i=1;i<=2;i++){
            for(int j=1;j<=4;j++){
                if(i==1) {
                    log[j - 1] = kNN_node_sum(query, kd1, k, j);
                    System.out.print(log[j - 1] + " ");
                }
                else {
                    log[j + 3] = kNN_node_sum(query, kd2, k, j);
                    System.out.print(log[j + 3]  + " ");
                }
            }
        }
        int sum = 0;
        for(int i=0;i<query.length;i++){
            int query_k = k[i];
            HyperPoint p = query[i];
            long[] count = new long[1];
            if(method[i] < 5) kd1.kNN(p,query_k,method[i],count);
            else kd2.kNN(p,query_k,method[i]-4,count);
            //System.out.print(count[0]+"\n");
            sum += count[0];
        }
        log[8] = sum;
        System.out.print("best "+sum+"\n");
        return log;
    }
    public static long kNN_node_sum(HyperPoint[] hp, AutoIS kd, int[] k,int model){
        long sum = 0;
        for(int i=0;i<hp.length;i++){
            //if(sum < 0) System.out.print("wrong"+"\n");
            int query_k = k[i];
            HyperPoint p = hp[i];
            long[] count = new long[1];
            kd.kNN(p,query_k,model,count);
            //if(sum > Integer.MAX_VALUE) System.out.print("wrong"+"\n");
            sum += count[0];
        }
        return sum;
    }
    public static long kNN_time_sum(HyperPoint[] hp, AutoIS kd, int[] k,int model){
        long sum = 0;
        long start = System.currentTimeMillis();
        for(int i=0;i<hp.length;i++){
            int query_k = k[i];
            HyperPoint p = hp[i];
            long[] count = new long[1];
            //防止java优化带来问题
            long start1 = System.currentTimeMillis();
            if(model == 1 || model == 5) kd.kNN(p,query_k,2,count);
            else kd.kNN(p,query_k,1,count);
            //if(model == 1 || model == 5) kd.kNN(p,query_k,3,count);
            //else kd.kNN(p,query_k,3,count);
            long end1 = System.currentTimeMillis();
            //count = new int[1];
            kd.kNN(p,query_k,model,count);
            sum += end1 - start1;
        }
        long end = System.currentTimeMillis();
        return end-start-sum;
    }

    @Test
    public void range_test_node_access() throws IOException{
        int l = 1;
        String[] Query_address = new String[l];
        String[] best_method_address = new String[l];
        String[] address = new String[l];
        //address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        //address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        //address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";//一三快
        //address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt"; //一二快
        //address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        //address[5] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        //address[6] = "src/test/java/Dataset/7_5d_Phones_accelerometer_new.txt"; //三四差不多
        //address[7] = "src/test/java/Dataset/8_11d_HT_Sensor_dataset.txt";
        address[0] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/range_test_data"+(i+1)+".txt";
        }
        for(int i=0;i<l;i++){
            best_method_address[i] = "src/test/java/expfile/AutokNN/Auto/range_predict_val("+(i+1)+").csv";
        }
        long[][] log = new long[8][9];
        for(int i=0;i<l;i++){
            log[i] = range_Node_Access(address[i],Query_address[i],best_method_address[i]);
        }
        SavetoCSV(log,"src/test/java/expfile/AutokNN/Auto/range_test.csv");
    }
    public static long[] range_Node_Access(String address, String Query_address,String best_method_address) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        HyperSpace[] query = ReadHS(Query_address,sample[0].getK());
        int[] method = Read_single_res(best_method_address,query.length);
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

        long[] log = new long[5];
        for(int i=1;i<=2;i++){
            for(int j=1;j<=2;j++){
                if(i==1) {
                    log[j - 1] = range_node_access(query,kd1,j);
                    System.out.print(log[j - 1] + " ");
                }
                else {
                    log[j + 1] = range_node_access(query,kd2,j);
                    System.out.print(log[j + 1]  + " ");
                }
            }
        }

        int sum = 0;
        for(int i=0;i<query.length;i++){
            int[] count = new int[1];
            if(method[i] < 3) kd1.RangeQuery(query[i],method[i],count);
            else kd2.RangeQuery(query[i],method[i]-2,count);
            sum += count[0];
        }
        log[4] = sum;
        System.out.print("best "+sum+"\n");
        return log;
    }
    public static int range_node_access(HyperSpace hs[],AutoIS kd,int model){
        int sum = 0;
        for(int i=0;i<hs.length;i++){
            int[] count = new int[1];
            kd.RangeQuery(hs[i],model,count);
            sum += count[0];
        }
        return sum;
    }

    //range query
    @Test
    public void test_node_time() throws IOException{
        int l = 1;
        String[] Query_address = new String[l];
        String[] best_method_address = new String[l];
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/range_test_data"+(i+1)+".txt";
        }
        for(int i=0;i<l;i++){
            best_method_address[i] = "src/test/java/expfile/AutokNN/Auto/range_predict_value.csv";
        }
        long[][] log = new long[8][9];
        for(int i=0;i<l;i++){
            log[i] = range_Node_time(address[i],Query_address[i],best_method_address[i]);
        }
        SavetoCSV(log,"src/test/java/expfile/AutokNN/Auto/range_test.csv");
    }
    public static long[] range_Node_time(String address, String Query_address,String best_method_address) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        HyperSpace[] query = ReadHS(Query_address,sample[0].getK());
        System.out.print(" "+query.length+"\n");
        int[] method = Read_single_res(best_method_address,query.length);
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

        long[] log = new long[5];
        for(int i=1;i<=2;i++){
            for(int j=1;j<=2;j++){
                if(i==1) {
                    log[j - 1] = range_node_time(query,kd1,j);
                    System.out.print(log[j - 1] + " ");
                }
                else {
                    log[j + 1] = range_node_time(query,kd2,j);
                    System.out.print(log[j + 1]  + " ");
                }
            }
        }

        long start = System.currentTimeMillis();
        for(int i=0;i<query.length;i++){
            int[] count = new int[1];
            if(method[i] < 3) kd1.RangeQuery(query[i],method[i],count);
            else kd2.RangeQuery(query[i],method[i]-2,count);
        }
        long end = System.currentTimeMillis();
        log[4] = end -start;
        System.out.print("best "+log[4] +"\n");
        return log;
    }
    public static long range_node_time(HyperSpace hs[],AutoIS kd,int model){
        long start = System.currentTimeMillis();
        for(int i=0;i<hs.length;i++){
            kd.RangeQuery(hs[i],model);
        }
        long end = System.currentTimeMillis();
        return end-start;
    }
    //time 575 788 218 490 best 211
    //node 8645183 8645183 7835745 7835745 best 7620190

    //node access
    @Test
    public void test_node() throws IOException{
        int l = 1;
        String[] Query_address = new String[l];
        String[] best_method_address = new String[l];
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/range_test_data"+(i+1)+".txt";
        }
        for(int i=0;i<l;i++){
            best_method_address[i] = "src/test/java/expfile/AutokNN/Auto/range_predict_value.csv";
        }
        long[][] log = new long[8][9];
        for(int i=0;i<l;i++){
            log[i] = range_Node(address[i],Query_address[i],best_method_address[i]);
        }
        SavetoCSV(log,"src/test/java/expfile/AutokNN/Auto/range_test_node.csv");
    }
    public static long[] range_Node(String address, String Query_address,String best_method_address) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        HyperSpace[] query = ReadHS(Query_address,sample[0].getK());
        System.out.print(" "+query.length+"\n");
        int[] method = Read_single_res(best_method_address,query.length);
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

        long[] log = new long[5];
        for(int i=1;i<=2;i++){
            for(int j=1;j<=2;j++){
                if(i==1) {
                    log[j - 1] = range_node(query,kd1,j);
                    System.out.print(log[j - 1] + " ");
                }
                else {
                    log[j + 1] = range_node(query,kd2,j);
                    System.out.print(log[j + 1]  + " ");
                }
            }
        }
        int[] count = new int[1];
        for(int i=0;i<query.length;i++){
            if(method[i] < 3) kd1.RangeQuery(query[i],method[i],count);
            else kd2.RangeQuery(query[i],method[i]-2,count);
        }
        System.out.print("best "+count[0] +"\n");
        return log;
    }
    public static long range_node(HyperSpace hs[],AutoIS kd,int model){
        int[] count = new int[1];
        for(int i=0;i<hs.length;i++){
            kd.RangeQuery(hs[i],1,count);
        }
        return count[0];
    }
}
