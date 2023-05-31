package Construction;

import Index.AutoIS;
import Index.HyperPoint;

import org.junit.Test;
import org.tinspin.index.rtree.RTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;


import static Index.AutoIS.*;
import static InputOutput.readfile.*;
import static RMI.RMI.divide;


public class MemoryUsage {
    @Test
    public void AutoIS_MemoryUsage() throws IOException {
        String address = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        int size = ReadDataAmount(address);
        int samplesize = (int)(size);
        HyperPoint[] sample = ReadData(address,samplesize);
        long data = (long)(samplesize*8*sample[0].getK());
        //System.out.println("memory " +((data)/1024/1024)+"\n");

        LinkedList<double[]> list = Max_Min_value(sample);
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));
        int K = list.get(0).length;
        AutoIS kd1 = new AutoIS(K, min, max);
        kd1.ConstructionAutoIS(sample,1);
        double M_1 = Memory(kd1.getRoot());
        double M_2 = divide(M_1,1024*1024);
        System.out.println("memory " +M_2+"\n");
        //System.out.println("memory " +((kd1.memory+data)/1024/1024)+"\n");
        //输出
    }

    @Test
    public void Construction_M() throws IOException {
        String[] address = new String[10];
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
        ////address[8] = "src/test/java/Dataset/9_18d_gas-sensor-array-temperature-modulation.txt";
        //address[9] = "src/test/java/Dataset/11_48d_YearPredictionMSD.txt";


        int[] sample_pop = new int[address.length];
        for(int i=0;i<address.length;i++){
            sample_pop[i] = ReadDataAmount(address[i]);
        }

        double[][] data_log = new double[address.length][6];//数据集+分类类型+数据占比

        for(int i = 0;i<address.length;i++){
            double[] log = M_Competitor(i,address[i],sample_pop[i]);
            data_log[i] = log;
        }
        SavetoCSV(data_log,"src/test/java/expfile/Index/M_Cost.txt");
    }

    public static double[][] M_Cost(int ID,String[] Address, double[] sample_ratio,int sample_size) throws IOException {
        String address = Address[ID];
        double[][] log = new double[6][sample_ratio.length];
        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            kd1.ConstructionAutoIS(sample,1);
            //memorycost(kd1.getRoot());
            long data = (sample.length*8*sample[0].getK());
            double M_1 = Memory(kd1.getRoot());
            double M_2 = divide(M_1,1024*1024);
            double data_m = divide(data,1024*1024);
            double M = (M_2+data_m);
            log[0][i] = M;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByMedianFinding+false"+"\n");
        }

        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            kd1.ConstructionAutoIS(sample,2);
            long data = (sample.length*8*sample[0].getK());
            //double m = divide(kd1.getMemory(),1024);
            double M_1 = Memory(kd1.getRoot());
            double M_2 = divide(M_1,1024*1024);
            double data_m = divide(data,1024*1024);
            double M = (M_2+data_m);
            log[1][i] = M;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByMedianFinding+true"+"\n");
        }

        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            kd1.ConstructionAutoIS(sample,3);
            long data = (sample.length*8*sample[0].getK());
            //double m = divide(kd1.getMemory(),1024);
            double M_1 = Memory(kd1.getRoot());
            double M_2 = divide(M_1,1024*1024);
            double data_m = divide(data,1024*1024);
            double M = (M_2+data_m);
            log[2][i] = M;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByAvgFinding"+"\n");
        }

        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            kd1.ConstructionAutoIS(sample,4);
            long data = (sample.length*8*sample[0].getK());
            //double m = divide(kd1.getMemory(),1024);
            double M_1 = Memory(kd1.getRoot());
            double M_2 = divide(M_1,1024*1024);
            double data_m = divide(data,1024*1024);
            double M = (M_2+data_m);
            log[3][i] = M;
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
                kd1.ConstructionAutoIS(sample,5);
                long data = (sample.length*8*sample[0].getK());
                //double m = divide(kd1.getMemory(),1024);
                double M_1 = Memory(kd1.getRoot());
                double M_2 = divide(M_1,1024*1024);
                double data_m = divide(data,1024*1024);
                double M = (M_2+data_m);
                log[4][i] = M;
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
                kd1.ConstructionAutoIS(sample,6);
                long data = (sample.length*8*sample[0].getK());
                //double m = divide(kd1.getMemory(),1024);
                double M_1 = Memory(kd1.getRoot());
                double M_2 = divide(M_1,1024*1024);
                double data_m = divide(data,1024*1024);
                double M = (M_2+data_m);
                log[5][i] = M;
                System.out.print(ID+"sample_rate "+sample_ratio[i]+" MinMax"+"\n");
            }
        }else{
            for(int i=0;i<sample_ratio.length;i++){
                log[5][i] = 1000000;
            }
        }


        return log;
    }

    public static double[] M_Competitor(int ID,String address, int sample_size) throws IOException {
        HyperPoint[] sample = ReadData(address,Integer.MAX_VALUE);
        double[] log = new double[6];
        LinkedList<double[]> list = Max_Min_value(sample);
        int K = list.get(0).length;
        HyperPoint min = new HyperPoint(list.get(0));
        HyperPoint max = new HyperPoint(list.get(1));

        AutoIS kd1 = new AutoIS(K, min, max);
        kd1.ConstructionAutoIS(sample,1);
        //memorycost(kd1.getRoot());
        long data = (sample.length*8*sample[0].getK());
        double M_1 = Memory(kd1.getRoot());
        double M_2 = divide(M_1,1024*1024);
        double data_m = divide(data,1024*1024);
        double M = (M_2+data_m);
        log[0] = M;
        System.out.print(ID+"sample_rate "+" insertByMedianFinding+false"+"\n");

        return log;
    }



    @Test
    public void generateToCSV() throws IOException {
        String address = "src/test/java/expfile/Index/M_Cost.txt";
        double[][] ratio = new double[10][6];
        Vector<HyperPoint> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int count = 0;
        while (buffReader.ready()) {
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            int a = count/6;
            int b = count % 6;
            for(int i=0;i<arr.length;i++){
                if(i == arr.length-1){
                    double loc = Double.parseDouble(arr[i].substring(0, arr[i].length() - 4));
                    ratio[a][b] = loc;
                }
            }
            count++;
        }
        SavetoCSV(ratio,"src/test/java/expfile/Index/M.txt");
    }

    @Test
    public void StandardToCSV() throws IOException {
        String address = "src/test/java/expfile/Index/M.txt";
        double[][] ratio = new double[10][6];
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int count = 0;
        while (buffReader.ready()) {
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            double sum = 0;
            for(int i=0;i<arr.length;i++){
                double loc = 1;
                System.out.print(arr[i]+"\n");
                if(i == arr.length-1){
                    loc = Double.parseDouble(arr[i].substring(0, arr[i].length() - 4));
                }else{
                    loc = Double.parseDouble(arr[i]);
                }
                sum += loc;
            }
            double avg = (sum/arr.length);
            for(int i=0;i<arr.length;i++){
                double loc = 1;
                if(i == arr.length-1){
                    loc = Double.parseDouble(arr[i].substring(0, arr[i].length() - 4));
                    ratio[count][i] = loc/sum;
                }else{
                    loc = Double.parseDouble(arr[i]);
                }
                ratio[count][i] = loc/sum;
            }
            count++;
        }
        SavetoCSV(ratio,"src/test/java/expfile/Index/M1.txt");
    }

    @Test
    public void ConvertToCSV() throws IOException {
        String address = "src/test/java/expfile/Index/M1.txt";
        double[][] ratio = new double[6][10];
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int count = 0;
        while (buffReader.ready()) {
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            for(int i=0;i<arr.length;i++){
                double loc = Double.parseDouble(arr[i].substring(0, 7));
                System.out.print(count+" "+i+"\n");
                ratio[i][count] = loc;
            }
            count++;
        }
        SavetoCSV(ratio,"src/test/java/expfile/Index/M2.txt");
    }

}
