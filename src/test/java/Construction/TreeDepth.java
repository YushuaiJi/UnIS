package Construction;

import Index.AutoIS;
import Index.HyperPoint;

import org.junit.Test;
import org.tinspin.index.rtree.RTree;

import java.io.IOException;
import java.util.LinkedList;
import static Index.AutoIS.maxDepth;
import static InputOutput.readfile.*;


public class TreeDepth {
    @Test
    public void AutoIS_Tree_Depth() throws IOException {
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
            long[][] log = Tree_depth(i,address,sample_ratio,sample_pop[i]);
            data_log[i] = log;
        }
        //SavetoCSV(data_log,"src/test/java/expfile/Index/TreeDepth.txt");
    }

    public static long[][] Tree_depth(int ID,String[] Address, double[] sample_ratio,int sample_size) throws IOException {
        String address = Address[ID];
        long[][] log = new long[7][sample_ratio.length];
        //for(int i=0;i<sample_ratio.length;i++){
        //    HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
        //    LinkedList<double[]> list = Max_Min_value(sample);
        //    int K = list.get(0).length;
        //    HyperPoint min = new HyperPoint(list.get(0));
        //    HyperPoint max = new HyperPoint(list.get(1));
        //    AutoIS kd1 = new AutoIS(K, min, max);
        //    kd1.ConstructionAutoIS(sample,1);
        //    int depth = maxDepth(kd1.getRoot());
        //    log[0][i] = depth;
       //     System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByMedianFinding+ "+depth+"\n");
        //}

        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            kd1.ConstructionAutoIS(sample,2);
            int depth = maxDepth(kd1.getRoot());
            log[1][i] = depth;
            System.out.print(ID+"sample_rate "+sample_ratio[i]+" insertByMedianFinding+true "+depth+"\n");
        }

        for(int i=0;i<sample_ratio.length;i++){
            HyperPoint[] sample = ReadData(address,(int)(sample_size*sample_ratio[i]));
            LinkedList<double[]> list = Max_Min_value(sample);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            kd1.ConstructionAutoIS(sample,3);
            int depth = maxDepth(kd1.getRoot());
            log[2][i] = depth;
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
            int depth = maxDepth(kd1.getRoot());
            log[3][i] = depth;
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
                int depth = maxDepth(kd1.getRoot());
                log[4][i] = depth;
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
                int depth = maxDepth(kd1.getRoot());
                log[5][i] = depth;
                System.out.print(ID+"sample_rate "+sample_ratio[i]+" MinMax"+"\n");
            }
        }else{
            for(int i=0;i<sample_ratio.length;i++){
                log[5][i] = 1000000;
            }
        }

        //R^STAR tree
        //for(int i=0;i<sample_ratio.length;i++){
        //    double[][] sample = Competitor_ReadData(address,(int)(sample_size*sample_ratio[i]));
        //    int DIM = sample[0].length;
        //    RTree<Integer> tree = RTree.createRStar(DIM);
        //    for(int j= 0;j<sample.length;j++){
        //        tree.insert(sample[j],0);
        //    }
        //    log[6][i] = tree.getDepth();
       // }
        return log;
    }
}
