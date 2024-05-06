import Index.UnIS;
import Index.HyperPoint;
import com.carrotsearch.sizeof.RamUsageEstimator;
import org.junit.Test;
import org.tinspin.index.qthypercube.QuadTreeKD;
import org.tinspin.index.qthypercube2.QuadTreeKD2;
import org.tinspin.index.rtree.RTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static Index.OSFuntion.*;

public class Construction {
    //R
    @Test
    public void test_construction_R_tree() throws IOException {
        int t = 4;
        HyperPoint[] data = null;
        int totalCount = 20000000;
        int[] proportions = {2, 4, 6, 8, 10}; // Proportions: 0.2, 0.4, 0.6, 0.8, 1.0
        //int[] proportions = {2, 4, 6, 8, 10}; // Proportions: 0.2, 0.4, 0.6, 0.8, 1.0
        if(t == 2){
            String address = "src/test/MapData/map.csv";
            totalCount = ReadFileDataNumber(address);
            double[][] sample = Competitor_ReadData(address,totalCount);
            data = new HyperPoint[sample.length];
            for(int i=0;i<data.length;i++){
                data[i] = new HyperPoint(sample[i]);
            }
        }else if(t == 3){
            data = ReadFileFixedData("src/test/Trajectory_data_point/output", 10, totalCount);
        }else if(t==1){
            data = ReadFileFixedData("src/test/CloudPoint/", 20000, totalCount);
        }else{
            String address = "src/test/combined_data.csv";
            totalCount = ReadFileDataNumber(address);
            double[][] sample = Competitor_ReadData(address,totalCount);
            data = new HyperPoint[sample.length];
            for(int i=0;i<data.length;i++){
                data[i] = new HyperPoint(sample[i]);
            }
        }
        List<Long> list = new ArrayList<>();
        List<Long> list1 = new ArrayList<>();
        for (int p : proportions) {
            int count = (totalCount * p) / 10; // Calculate the count for the given proportion
            HyperPoint[] points = ReadFixedData(count, data);

            double[][] sample = new double[points.length][points[0].getK()];
            for (int i = 0; i < sample.length; i++) {
                sample[i] = points[i].getcoords();
            }
            //double[][] sample = Competitor_ReadData(address,count);
            int DIM = sample[0].length;
            long start = System.currentTimeMillis();
            RTree<Integer> tree = RTree.createRStar(DIM);
            for (int j = 0; j < sample.length; j++) {
                tree.insert(sample[j], 0);
            }
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;

            long mem = RamUsageEstimator.sizeOf(tree)/(1024*1024);
            list.add(elapsedTime);
            list1.add(mem);
        }

        long[][] res = new long[list.size()][2];
        for (int i = 0; i < list.size(); i++) {
            res[i][0] = list.get(i);
        }

        for (int i = 0; i < list1.size(); i++) {
            res[i][1] = list1.get(i);
        }
        SavetoCSV(res,"src/test/Result/TimeMemory_4_1.csv");

    }


    @Test
    public void test_construction_KD() throws IOException{
        int t = 4;
        HyperPoint[] data = null;
        int totalCount = 20000000;
        int[] proportions = {2, 4, 6, 8, 10}; // Proportions: 0.2, 0.4, 0.6, 0.8, 1.0
        //int[] proportions = {2, 4, 6, 8, 10}; // Proportions: 0.2, 0.4, 0.6, 0.8, 1.0
        if(t == 2){
            String address = "src/test/MapData/map.csv";
            totalCount = ReadFileDataNumber(address);
            double[][] sample = Competitor_ReadData(address,totalCount);
            data = new HyperPoint[sample.length];
            for(int i=0;i<data.length;i++){
                data[i] = new HyperPoint(sample[i]);
            }
        }else if(t == 3){
            data = ReadFileFixedData("src/test/Trajectory_data_point/output", 10, totalCount);
        }else if(t==1){
            data = ReadFileFixedData("src/test/CloudPoint/", 20000, totalCount);
        }else{
            String address = "src/test/combined_data.csv";
            totalCount = ReadFileDataNumber(address);
            double[][] sample = Competitor_ReadData(address,totalCount);
            data = new HyperPoint[sample.length];
            for(int i=0;i<data.length;i++){
                data[i] = new HyperPoint(sample[i]);
            }
        }
        List<Long> list1 = new ArrayList<>();
        List<Long> list2 = new ArrayList<>();

        for (int p : proportions) {
            int count = (totalCount * p) / 10; // Calculate the count for the given proportion
            HyperPoint[] points = ReadFixedData(count, data);

            int[] pt = new int[10000];
            for(int j=0;j<pt.length;j++){
                pt[j] = 2;
            }
            LinkedList<double[]> list = Max_Min_value(data);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            UnIS autois = new UnIS(K,pt,min,max);
            long t1 = System.currentTimeMillis();
            //model1, model2, model3
            autois.ConstructionUnIS(points,1);
            long t2 = System.currentTimeMillis();
            long elapsedTime = t2 - t1;
            long mem = RamUsageEstimator.sizeOf(autois)/(1024*1024);
            list1.add(elapsedTime);
            list2.add(mem);
        }

        long[][] res = new long[list1.size()][2];
        for (int i = 0; i < list1.size(); i++) {
            res[i][0] = list1.get(i);
        }

        for (int i = 0; i < list1.size(); i++) {
            res[i][1] = list2.get(i);
        }
        SavetoCSV(res,"src/test/Result/TimeMemory_KD_3_1.csv");

    }

    @Test
    public void test_construction_autois() throws IOException{
        int t = 4;
        HyperPoint[] data = null;
        int totalCount = 20000000;
        int[] proportions = {2, 4, 6, 8, 10}; // Proportions: 0.2, 0.4, 0.6, 0.8, 1.0
        //int[] proportions = {2, 4, 6, 8, 10}; // Proportions: 0.2, 0.4, 0.6, 0.8, 1.0
        if(t == 2){
            String address = "src/test/MapData/map.csv";
            totalCount = ReadFileDataNumber(address);
            double[][] sample = Competitor_ReadData(address,totalCount);
            data = new HyperPoint[sample.length];
            for(int i=0;i<data.length;i++){
                data[i] = new HyperPoint(sample[i]);
            }
        }else if(t == 3){
            data = ReadFileFixedData("src/test/Trajectory_data_point/output", 10, totalCount);
        }else if(t==1){
            data = ReadFileFixedData("src/test/CloudPoint/", 20000, totalCount);
        }else{
            String address = "src/test/combined_data.csv";
            totalCount = ReadFileDataNumber(address);
            double[][] sample = Competitor_ReadData(address,totalCount);
            data = new HyperPoint[sample.length];
            for(int i=0;i<data.length;i++){
                data[i] = new HyperPoint(sample[i]);
            }
        }
        List<Long> list1 = new ArrayList<>();
        List<Long> list2 = new ArrayList<>();


        for (int p : proportions) {
            int count = (totalCount * p) / 10; // Calculate the count for the given proportion
            HyperPoint[] points = ReadFixedData(count, data);

            int[] pt = new int[10000];
            for(int j=0;j<pt.length;j++){
                pt[j] = 2;
            }
            LinkedList<double[]> list = Max_Min_value(data);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            UnIS autois = new UnIS(K,pt,min,max);
            long t1 = System.currentTimeMillis();
            //model1, model2, model3
            autois.ConstructionUnIS(points,3);
            long t2 = System.currentTimeMillis();
            long elapsedTime = t2 - t1;
            long mem = RamUsageEstimator.sizeOf(autois)/(1024*1024);
            list1.add(elapsedTime);
            list2.add(mem);
        }

        long[][] res = new long[list1.size()][2];
        for (int i = 0; i < list1.size(); i++) {
            res[i][0] = list1.get(i);
        }

        for (int i = 0; i < list1.size(); i++) {
            res[i][1] = list2.get(i);
        }
        SavetoCSV(res,"src/test/Result/TimeMemory_autois_4_4.csv");
    }
    //Liang两种数据集上不行，建树的时候有什么特点，建树的时候不考虑，只考虑了点云数据。为什么多分（ikd-tree),unified,个方面性能做测试。轨迹里做kNN.一定要体现unfied的的优势
    //服务于kNN,RADIUE SEARCH

    //data1
    //39358

    //data2
    //52153

    //data3
    //51387

}
