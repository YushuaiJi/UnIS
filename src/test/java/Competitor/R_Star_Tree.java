package Competitor;
import Index.AutoIS;
import Index.HyperPoint;
import com.carrotsearch.sizeof.RamUsageEstimator;
import org.junit.Test;
import org.tinspin.index.PointEntryDist;
import org.tinspin.index.QueryIteratorKNN;
import org.tinspin.index.phtree.PHTreeP;

import org.tinspin.index.qthypercube.QuadTreeKD;
import org.tinspin.index.qthypercube2.QuadTreeKD2;
import org.tinspin.index.qtplain.QuadTreeKD0;
import org.tinspin.index.rtree.*;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

import static Competitor.BinarySearch.findKNearestNeighbors;
import static Index.AutoIS.Memory;
import static InputOutput.readfile.*;
import static RMI.RMI.divide;
import static org.junit.Assert.assertEquals;

public class R_Star_Tree {
    @Test
    public void R_Time() throws IOException {
        String[] address = new String[9];
        //gas sensor没法用
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] ="src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[6] = "src/test/java/Dataset/7_5d_Phones_accelerometer_new.txt";
        address[7] = "src/test/java/Dataset/8_11d_HT_Sensor_dataset.txt";
        address[8] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";
        //address[9] = "src/test/java/Dataset/11_48d_YearPredictionMSD.txt";

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

        long[][][] data_log = new long[address.length][6][sample_ratio.length];//数据集+分类类型+数据占比

        for(int i = 0;i<address.length;i++){
            long[][] log = Construction_tree(i,address,sample_ratio,sample_pop[i]);
            data_log[i] = log;
        }
        SavetoCSV(data_log,"src/test/java/expfile/Index/Competitor_IndexTime1.txt");
    }
    public static long[][] Construction_tree(int ID,String[] Address, double[] sample_ratio,int sample_size) throws IOException{
        String address = Address[ID];
        long[][] log = new long[1][sample_ratio.length];
        //R^STAR tree
        for(int i=0;i<sample_ratio.length;i++){
            double[][] sample = Competitor_ReadData(address,(int)(sample_size*sample_ratio[i]));
            int DIM = sample[0].length;
            long start = System.currentTimeMillis();
            RTree<Integer> tree = RTree.createRStar(DIM);
            for(int j= 0;j<sample.length;j++){
                tree.insert(sample[j],0);
            }
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            log[0][i] = elapsedTime;
        }
        System.out.print("success"+"\n");
        return log;
    }

    //第三方软件库估算代码,memory
    @Test
    public void TestforMemory() throws IOException {
        String[] address = new String[9];
        //gas sensor没法用
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] ="src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
        address[6] = "src/test/java/Dataset/7_5d_Phones_accelerometer_new.txt";
        address[7] = "src/test/java/Dataset/8_11d_HT_Sensor_dataset.txt";
        address[8] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";

        double[][] log = new double[address.length][1];
        for(int i = 0;i<address.length;i++){
            log[i][0] = R_tree_Memory_cost_new(address[i]);

        }
        SavetoCSV(log,"src/test/java/expfile/Index/Memory_Cost1");
    }
    public static double R_tree_Memory_cost_new(String address) throws IOException {
        double[][] sample = Competitor_ReadData(address,Integer.MAX_VALUE);
        int DIM = sample[0].length;
        RTree<Integer> tree = RTree.createRStar(DIM);
        for(int j= 0;j<sample.length;j++){
            tree.insert(sample[j],0);
        }
        long mem = RamUsageEstimator.sizeOf(tree);
        return mem/(1024*1024);
    }

    @org.junit.Test
    public void Query_Time_kNN() throws IOException {
        int l = 6;
        String[] Query_address = new String[l];
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

        long[][] data_log = new long[address.length][5];//数据集+分类类型+数据占比

        for(int i = 0;i<address.length;i++){
            long[] log = query_time(address[i],Query_address[i]);
            data_log[i] = log;
        }
        //SavetoCSV(data_log,"src/test/java/expfile/competitor/competitorkNN.txt");
    }
    public static long[] query_time(String address,String Query_address) throws IOException{
        double[][] sample = Competitor_ReadData(address,Integer.MAX_VALUE);
        long[] log = new long[6];
        int DIM = sample[0].length;
        //读取kNN
        HyperPoint[] query = ReadQuery(Query_address,sample[0].length);
        int[] k = Readk(Query_address,sample[0].length);

        double[][] qKNN = new double[query.length][sample[0].length];
        for(int i=0;i<query.length;i++){
            qKNN[i] = query[i].getcoords();
        }
        //R^STAR tree
        RTree<Integer> tree = RTree.createRStar(DIM);
        for(int j= 0;j<sample.length;j++){
            tree.insert(sample[j],0);
        }
        long start = System.currentTimeMillis();
        for(int i=0;i<qKNN.length;i++){
            RTreeQueryKnn<Integer> a = tree.queryKNN(qKNN[i],k[i]);
        }
        long end = System.currentTimeMillis();
        log[0] = end-start;
        System.out.print(log[0]+"\n");
        return log;
    }
    //495
    //410
    //413
    //1423
    //244
    //12261

    @org.junit.Test
    public void Query_node_kNN() throws IOException {
        int l = 6;
        String[] Query_address = new String[l];
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

        long[] data_log = new long[address.length];//数据集+分类类型+数据占比

        for(int i = 0;i<address.length;i++){
            long log = query_node(address[i],Query_address[i]);
            data_log[i] = log;
        }
        //SavetoCSV(data_log,"src/test/java/expfile/competitor/competitorkNN.txt");
    }
    public static long query_node(String address,String Query_address) throws IOException{
        double[][] sample = Competitor_ReadData(address,Integer.MAX_VALUE);
        long[] log = new long[6];
        int DIM = sample[0].length;
        //读取kNN
        HyperPoint[] query = ReadQuery(Query_address,sample[0].length);
        int[] k = Readk(Query_address,sample[0].length);

        double[][] qKNN = new double[query.length][sample[0].length];
        for(int i=0;i<query.length;i++){
            qKNN[i] = query[i].getcoords();
        }
        //R^STAR tree
        RTree<Integer> tree = RTree.createRStar(DIM);
        for(int j= 0;j<sample.length;j++){
            tree.insert(sample[j],0);
        }
        long a = 0;
        for(int i=0;i<qKNN.length;i++){
             a += tree.queryKNN_node(qKNN[i],k[i]);
        }

        System.out.print(a+"\n");
        return a;
    }
    //495
    //410
    //413
    //1423
    //244
    //12261
}




