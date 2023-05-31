package Construction;

import Index.AutoIS;
import Index.HyperPoint;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;

import static InputOutput.readfile.*;

public class Index_Time {

    @Test
    public void Construction_AutoIS() throws IOException {
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
        //address[8] = "src/test/java/Dataset/9_18d_gas-sensor-array-temperature-modulation.txt";
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
        SavetoCSV(data_log,"src/test/java/expfile/Index/IndexTime.txt");
    }

    public static long[][] Construction_tree(int ID,String[] Address, double[] sample_ratio,int sample_size) throws IOException {
        String address = Address[ID];
        long[][] log = new long[6][sample_ratio.length];
        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            long start = System.currentTimeMillis();
            kd1.ConstructionAutoIS(sample,1);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            log[0][i] = elapsedTime;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByMedianFinding+false"+"\n");
        }

        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            long start = System.currentTimeMillis();
            kd1.ConstructionAutoIS(sample,2);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            log[1][i] = elapsedTime;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByMedianFinding+true"+"\n");
        }

        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            long start = System.currentTimeMillis();
            kd1.ConstructionAutoIS(sample,3);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            log[2][i] = elapsedTime;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByAvgFinding"+"\n");
        }

        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            long start = System.currentTimeMillis();
            kd1.ConstructionAutoIS(sample,4);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            log[3][i] = elapsedTime;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertBySampling"+"\n");
        }
        if(ID < 6){
            for(int i=0;i<sample_ratio.length;i++){
                HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
                LinkedList<double[]> list = Max_Min_value(sample);
                int K = list.get(0).length;
                HyperPoint min = new HyperPoint(list.get(0));
                HyperPoint max = new HyperPoint(list.get(1));
                AutoIS kd1 = new AutoIS(K, min, max);
                long start = System.currentTimeMillis();
                kd1.ConstructionAutoIS(sample,5);
                long end = System.currentTimeMillis();
                long elapsedTime = end - start;
                log[4][i] = elapsedTime;
                System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByMedianofMedian"+"\n");
            }
        }else{
            for(int i=0;i<sample_ratio.length;i++){
                log[4][i] = 1000000;
            }
        }

        if(ID != 3){
            for(int i=0;i<sample_ratio.length;i++){
                HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
                LinkedList<double[]> list = Max_Min_value(sample);
                int K = list.get(0).length;
                HyperPoint min = new HyperPoint(list.get(0));
                HyperPoint max = new HyperPoint(list.get(1));
                AutoIS kd1 = new AutoIS(K, min, max);
                long start = System.currentTimeMillis();
                kd1.ConstructionAutoIS(sample,6);
                long end = System.currentTimeMillis();
                long elapsedTime = end - start;
                log[5][i] = elapsedTime;
                System.out.print(ID+"sample_rate "+sample_ratio[i]+" MinMax"+"\n");
            }
        }else{
            for(int i=0;i<sample_ratio.length;i++){
                log[5][i] = 1000000;
            }
        }
        return log;
    }

    @Test
    public void Construction_LearnIndex_time_test1() throws IOException {
        String[] address = new String[1];
        //gas sensor没法用
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";

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
            long[][] log = Construction_tree1(i,address,sample_ratio,sample_pop[i]);
            data_log[i] = log;
        }
        SavetoCSV(data_log,"src/test/java/expfile/Index/IndexTime.txt");
    }

    public static long[][] Construction_tree1(int ID,String[] Address, double[] sample_ratio,int sample_size) throws IOException {
        String address = Address[ID];
        long[][] log = new long[6][sample_ratio.length];
        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            long start = System.currentTimeMillis();
            kd1.ConstructionAutoIS(sample,7);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            log[0][i] = elapsedTime;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByMedianFinding+false"+"\n");
        }
        return log;
    }
}
