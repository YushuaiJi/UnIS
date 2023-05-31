package Construction;

import Index.HyperPoint;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import static Index.AutoIS.MedAndPartition;
import static Index.AutoIS.Med_test;
import static InputOutput.readfile.*;

public class MedianFinding {
    @Test
    public void MedFinding_time_test() throws IOException {
        String[] address = new String[6];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";

        LinkedList<LinkedList<Long>> list = new LinkedList<>();
        for(int i=0;i<4;i++){
            LinkedList<Long> l = new LinkedList<>();
            list.add(l);
        }
        for(int i = 0;i<address.length;i++){
            test_med_time(address[i],list);
        }
        long[][] data_log = new long[list.size()][list.get(0).size()];
        for(int i=0;i<data_log.length;i++){
            for(int j=0;j<data_log[0].length;j++){
                data_log[i][j] = list.get(i).get(j);
            }
        }
        SavetoCSV(data_log,"src/test/java/expfile/Index/Medfind.txt");
    }

    public static void test_med_time(String address,LinkedList<LinkedList<Long>> list) throws IOException {
        HyperPoint[] sample = ReadData(address, 1000000000);
        for(int i=0;i<sample[0].getK();i++){
            long start = System.currentTimeMillis();
            double res = Med_test(sample,i,1);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            list.get(0).add(elapsedTime);
        }

        for(int i=0;i<sample[0].getK();i++){
            long start = System.currentTimeMillis();
            double res = Med_test(sample,i,2);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            list.get(1).add(elapsedTime);
        }

        for(int i=0;i<sample[0].getK();i++){
            long start = System.currentTimeMillis();
            double res = Med_test(sample,i,3);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            list.get(2).add(elapsedTime);
        }

        for(int i=0;i<sample[0].getK();i++){
            long start = System.currentTimeMillis();
            double res = Med_test(sample,i,4);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            list.get(3).add(elapsedTime);
        }
        System.out.print("success"+address+"\n");
    }

    @Test
    public void MedAndPartition_test() throws IOException {
        String[] address = new String[6];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";

        LinkedList<LinkedList<Long>> list = new LinkedList<>();
        for(int i=0;i<4;i++){
            LinkedList<Long> l = new LinkedList<>();
            list.add(l);
        }
        for(int i = 0;i<address.length;i++){
            medandpartition(address[i],list);
        }
        long[][] data_log = new long[list.size()][list.get(0).size()];
        for(int i=0;i<data_log.length;i++){
            for(int j=0;j<data_log[0].length;j++){
                data_log[i][j] = list.get(i).get(j);
            }
        }
        SavetoCSV(data_log,"src/test/java/expfile/Index/MedAndPartition.txt");
    }

    public static void medandpartition(String address,LinkedList<LinkedList<Long>> list) throws IOException {
        HyperPoint[] sample = ReadData(address, 1000000000);
        for(int i=0;i<sample[0].getK();i++){
            long start = System.currentTimeMillis();
            MedAndPartition(sample,i,1);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            list.get(0).add(elapsedTime);
        }

        for(int i=0;i<sample[0].getK();i++){
            long start = System.currentTimeMillis();
            MedAndPartition(sample,i,2);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            list.get(1).add(elapsedTime);
        }

        for(int i=0;i<sample[0].getK();i++){
            long start = System.currentTimeMillis();
            MedAndPartition(sample,i,3);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            list.get(2).add(elapsedTime);
        }

        for(int i=0;i<sample[0].getK();i++){
            long start = System.currentTimeMillis();
            MedAndPartition(sample,i,4);
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            list.get(3).add(elapsedTime);
        }
        System.out.print("success"+address+"\n");
    }



    @Test
    public void MedFinding_Accuracy_test() throws IOException {
        String[] address = new String[6];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";

        LinkedList<LinkedList<Double>> list = new LinkedList<>();
        for(int i=0;i<3;i++){
            LinkedList<Double> l = new LinkedList<>();
            list.add(l);
        }
        for(int i = 0;i<address.length;i++){
            Accuracy_test(address[i],list);
        }

        double[][] data_log = new double[list.size()][list.get(0).size()];
        for(int i=0;i<data_log.length;i++){
            for(int j=0;j<data_log[0].length;j++){
                data_log[i][j] = list.get(i).get(j);
            }
        }
        SavetoCSV(data_log,"src/test/java/expfile/Index/Medfind_ACCURACY.txt");
    }

    public static void Accuracy_test(String address,LinkedList<LinkedList<Double>> list) throws IOException {
        System.out.print("0"+address+"\n");
        HyperPoint[] sample = ReadData(address, 1000000000);
        for(int i=0;i<sample[0].getK();i++){
            double res = Med_test(sample,i,1);
            double ratio = midcase(sample,res,i);
            list.get(0).add(ratio);
        }
        System.out.print("1"+address+"\n");
        for(int i=0;i<sample[0].getK();i++){
            double res = Med_test(sample,i,3);
            double ratio = midcase(sample,res,i);
            list.get(1).add(ratio);
        }
        System.out.print("2"+address+"\n");
        for(int i=0;i<sample[0].getK();i++){
            double res = Med_test(sample,i,4);
            double ratio = midcase(sample,res,i);
            list.get(2).add(ratio);
        }
        System.out.print("success"+address+"\n");
    }

    public static double midcase(HyperPoint[] arr,double target,int k){
        double res = Med_test(arr,k,2);
        return (target-res)/res;
    }

}
