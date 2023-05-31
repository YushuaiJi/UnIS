package Competitor;

import Index.AutoIS;
import Index.HyperPoint;
import RMI_Z_Order.big_Params;
import RMI_Z_Order.big_RMI;
import RMI_Z_Order.big_linear_model;
import com.carrotsearch.sizeof.RamUsageEstimator;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static Competitor.BinarySearch.findKNearestNeighbors;
import static Competitor.BinarySearch.learned_findKNearestNeighbors;
import static Competitor.Zorder.encode;
import static Competitor.Zorder.zOrderDecode;
import static InputOutput.readfile.*;
import static InputOutput.readfile.Read_single_res;
import static RMI_Z_Order.big_RMI.predicition;

public class ZM {

    //所有数据转换成正数
   @Test
   public void Covert_to_Potive() {
       String[] address = new String[6];
       address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
       address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
       address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
       address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
       address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
       address[5] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";
       //address[5] = "src/test/java/Dataset/10_24d_KEGG_Metabolic.txt";

       //String[] address1 = new String[1];
       //address1[0] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";

       String[] outputs = new String[6];
       outputs[0] = "src/test/java/Dataset/positivedataset/1_2d_T_drive_No_duplication.txt";
       outputs[1] =  "src/test/java/Dataset/positivedataset/2_2d_Porto_No_duplication.txt";
       outputs[2] = "src/test/java/Dataset/positivedataset/3_3d_spatial_network.txt";//这个行
       outputs[3] = "src/test/java/Dataset/positivedataset/4_3d_Snapenet_new.txt";
       outputs[4] ="src/test/java/Dataset/positivedataset/5_3d_ConfLongDemo_JSI.txt"; //三快
       outputs[5] = "src/test/java/Dataset/positivedataset/6_4d_NYC_10M_transition_edit.txt";
       //outputs[5] = "src/test/java/Dataset/positivedataset/10_24d_KEGG_Metabolic.txt";



       //String[] outputs1 = new String[1];
       //outputs1[0] =  "src/test/java/Dataset/positivedataset/2_2d_Porto_No_duplication.txt";


       for(int i=0;i<address.length;i++){
           String inputFilePath =address[i]; // 输入文件路径
           String outputFilePath = outputs[i]; // 输出文件路径
           try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
               String line;
               while ((line = br.readLine()) != null) {
                   String[] numbers = line.split(","); // 使用正则表达式分隔行中的数字
                   StringBuilder sb = new StringBuilder();
                   for (String number : numbers) {
                       double value = Double.parseDouble(number);
                       if (value < 0) { // 如果数值为负数，则取相反数
                           value = -value;
                       }
                       sb.append(value).append(","); // 将转换后的数值添加到输出行中
                   }
                   String output = sb.toString().trim();
                   output= output.substring(0, output.length() - 1);
                   bw.write(output); // 去除末尾空格并写入输出文件
                   bw.newLine();
               }
               System.out.println("转换完成！");
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

   }
   //所有数据转成成整数
   @Test
   public void Convert_to_Integer() throws IOException {
       String[] address = new String[6];
       address[0] = "src/test/java/Dataset/positivedataset/1_2d_T_drive_No_duplication.txt";
       address[1] = "src/test/java/Dataset/positivedataset/2_2d_Porto_No_duplication.txt";
       address[2] = "src/test/java/Dataset/positivedataset/3_3d_spatial_network.txt";//这个行
       address[3] = "src/test/java/Dataset/positivedataset/4_3d_Snapenet_new.txt";
       address[4] ="src/test/java/Dataset/positivedataset/5_3d_ConfLongDemo_JSI.txt"; //三快
       address[5] = "src/test/java/Dataset/positivedataset/6_4d_NYC_10M_transition_edit.txt";


       String[] outputs = new String[6];
       outputs[0] = "src/test/java/Dataset/Integerdataset/1_2d_T_drive_No_duplication.txt";
       outputs[1] = "src/test/java/Dataset/Integerdataset/2_2d_Porto_No_duplication.txt";//这个行
       outputs[2] = "src/test/java/Dataset/Integerdataset/3_3d_spatial_network.txt";//这个行
       outputs[3] = "src/test/java/Dataset/Integerdataset/4_3d_Snapenet_new.txt";
       outputs[4] = "src/test/java/Dataset/Integerdataset/5_3d_ConfLongDemo_JSI.txt"; //三快
       outputs[5] = "src/test/java/Dataset/Integerdataset/6_4d_NYC_10M_transition_edit.txt";


       int[] scales = new int[6];
       scales[0] = 5;
       scales[1] = 6;
       scales[2] = 7;
       scales[3] = 5;
       scales[4]= 9;
       scales[5] = 14;

       for (int h = 0; h < address.length; h++) {
           HyperPoint[] hp = ReadData(address[h], Integer.MAX_VALUE);
           //LinkedList<double[]> list = quantile_value(hp,0.05);
           double[][] inputArray = new double[hp.length][hp[0].getK()];
           for (int j = 0; j < hp.length;j++){
               inputArray[j] = hp[j].getcoords();
           }
           //int scale = 0;
           //for(int i=0;i<inputArray.length;i++){
           //    for(int j=0;j<inputArray[0].length;j++){
           //        String[] parts = Double.toString(inputArray[i][j]).split("\\.");
           //        scale = Math.max(scale,parts[1].length());
           //    }
           //}
           long[][] arr = new long[inputArray.length][inputArray[0].length];
           for(int i=0;i<inputArray.length;i++){
               for(int j=0;j<inputArray[0].length;j++){
                   arr[i][j] = (long) (inputArray[i][j]*Math.pow(10,scales[h]));
               }
           }
           writeArrayToFile(arr,outputs[h]);
       }
   }
   public static void writeArrayToFile(long[][] array, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            for (int i = 0; i < array.length; i++) {
                String sb = "";
                for (int j = 0; j < array[i].length; j++) {
                    sb = sb+array[i][j] + ",";
                }
                String output = sb.toString().trim();
                output= output.substring(0, output.length() - 1);
                writer.write(output+"\n");
            }
            writer.close();
            System.out.println("导出成功！");
        } catch (IOException e) {
            System.out.println("导出失败：" + e.getMessage());
        }
    }

    //装换成单维度数据
    @Test
    public void Index_time() throws IOException {
        String[] address = new String[6];
        address[0] = "src/test/java/Dataset/Integerdataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/Integerdataset/3_3d_spatial_network.txt";//这个行
        address[2] = "src/test/java/Dataset/Integerdataset/4_3d_Snapenet_new.txt";
        address[3] = "src/test/java/Dataset/Integerdataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[4] = "src/test/java/Dataset/Integerdataset/6_4d_NYC_10M_transition_edit.txt";

        String[] outputs = new String[6];
        outputs[0] = "src/test/java/Dataset/z_order_dataset/1_2d_T_drive_z_order.txt";
        outputs[1] = "src/test/java/Dataset/z_order_dataset/3_3d_spatial_network_z_order.txt";//这个行
        outputs[2] = "src/test/java/Dataset/z_order_dataset/4_3d_Snapenet_z_order.txt";
        outputs[3] = "src/test/java/Dataset/z_order_dataset/5_3d_ConfLongDemo_z_order.txt"; //三快
        outputs[4] = "src/test/java/Dataset/z_order_dataset/6_4d_NYC_z_order.txt";

        String[] address1 = new String[1];
        address1[0] = "src/test/java/Dataset/Integerdataset/2_2d_Porto_No_duplication.txt";

        String[] outputs1= new String[1];
        outputs1[0] = "src/test/java/Dataset/z_order_dataset/2_2d_Porto_No_duplication.txt";

        for(int i=0;i<address1.length;i++){
            Z_order(address1[i],outputs1[i]);
        }
    }

    public void Z_order(String address,String output) throws IOException {
        HyperPoint[] hp = ReadData(address, Integer.MAX_VALUE);
        //BigInteger[][] big_hp = new BigInteger[hp.length][hp[0].getK()];
        BigInteger[] arr = new BigInteger[hp.length];
        long start = System.currentTimeMillis();
        for(int i=0;i<hp.length;i++){
            double[] ele1 = hp[i].getcoords();
            //if(i%100000==0) System.out.print("100000 ready"+"\n");
            BigInteger[] ele2 = new BigInteger[ele1.length];
            for(int j=0;j<ele1.length;j++){
                ele2[j] = BigInteger.valueOf((long)ele1[j]);
            }
            //big_hp[i] = ele2;
            arr[i] = encode(ele2);
        }
        long end = System.currentTimeMillis();
        System.out.print(address+" time "+(end-start)+"\n");
        String fileName = output;
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (int i = 0; i < arr.length; i++) {
                bufferedWriter.write(arr[i].toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //normal case
    //1_2d_T_drive time 1109163
    //2-2d porto 119865
    //3_3d_spatial_network 65885
    //4_3d_Snapenet_new 1428104
    //5_3d_ConfLongDemo_JSI 24910
    //6_4d_NYC_10M_transition_edit 2295561


    @Test
    public void kNN_time() throws IOException {
        int l = 6;
        String[] Query_address = new String[l];
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/Integerdataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/Integerdataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/Integerdataset/3_3d_spatial_network.txt";//这个行
        address[3] = "src/test/java/Dataset/Integerdataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/Integerdataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[5] = "src/test/java/Dataset/Integerdataset/6_4d_NYC_10M_transition_edit.txt";

        String[] Z_order_address = new String[l];
        Z_order_address[0] = "src/test/java/Dataset/z_order_dataset/1_2d_T_drive_z_order.txt";
        Z_order_address[1] = "src/test/java/Dataset/z_order_dataset/2_2d_Porto_No_duplication.txt";
        Z_order_address[2] = "src/test/java/Dataset/z_order_dataset/3_3d_spatial_network_z_order.txt";
        Z_order_address[3] = "src/test/java/Dataset/z_order_dataset/4_3d_Snapenet_z_order.txt";
        Z_order_address[4] = "src/test/java/Dataset/z_order_dataset/5_3d_ConfLongDemo_z_order.txt";
        Z_order_address[5] = "src/test/java/Dataset/z_order_dataset/6_4d_NYC_z_order.txt";

        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/test_data"+(i+1)+".txt";
        }
        long[] log = new long[5];
        for(int i=0;i<l;i++){
            log[i] = kNN(address[i],Z_order_address[i], Query_address[i]);
        }
        SavetoCSV(log,"src/test/java/expfile/competitor/z_order_kNN");
    }
    public static long kNN(String k_value, String address, String Query_address) throws IOException {
        HyperPoint[] sample = ReadData(k_value,Integer.MAX_VALUE);
        BigInteger[] index = Read_Z_order(address,sample.length);
        HyperPoint[] query = ReadQuery(Query_address,sample[0].getK());
        Arrays.sort(index);
        int[] k = Readk(Query_address,sample[0].getK());

        BigInteger[] arr = new BigInteger[query.length];
        for(int i=0;i<arr.length;i++){
            double[] ele1 = query[i].getcoords();
            BigInteger[] ele2 = new BigInteger[ele1.length];
            for(int j=0;j<ele1.length;j++){
                ele2[j] = BigInteger.valueOf((long)ele1[j]);
            }
            arr[i] = encode(ele2);
        }

        long start = System.currentTimeMillis();
        for(int i=0;i<arr.length;i++){
            List<BigInteger> list = findKNearestNeighbors(index,arr[i],k[i],new long[1]);

            if(i==1){
                System.out.print("res.size"+list.size()+" ");
                for(int j=0;j<list.size();j++){
                    System.out.print(list.get(j)+" ");
                }
                System.out.print("\n");
            }

            List<BigInteger[]> res = new ArrayList<>();
            for(int j=0;j<list.size();j++) {
                BigInteger[] ele = zOrderDecode(list.get(j), sample[0].getK());
            }
        }
        long end = System.currentTimeMillis();
        System.out.print((end-start)+"\n");
        return end-start;
    }
    //normal case
    //8789
    //1584
    //31533
    //11702
    //83698
    //3133

    //learned index time
    @Test
    public void learned_time() throws IOException {
        String[] address = new String[6];
        address[0] = "src/test/java/Dataset/Integerdataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/Integerdataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/Integerdataset/3_3d_spatial_network.txt";//这个行
        address[3] = "src/test/java/Dataset/Integerdataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/Integerdataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[5] = "src/test/java/Dataset/Integerdataset/6_4d_NYC_10M_transition_edit.txt";

        String[] Z_order_address = new String[6];
        Z_order_address[0] = "src/test/java/Dataset/z_order_dataset/1_2d_T_drive_z_order.txt";
        Z_order_address[1] = "src/test/java/Dataset/z_order_dataset/2_2d_Porto_No_duplication.txt";
        Z_order_address[2] = "src/test/java/Dataset/z_order_dataset/3_3d_spatial_network_z_order.txt";
        Z_order_address[3] = "src/test/java/Dataset/z_order_dataset/4_3d_Snapenet_z_order.txt";
        Z_order_address[4] = "src/test/java/Dataset/z_order_dataset/5_3d_ConfLongDemo_z_order.txt";
        Z_order_address[5] = "src/test/java/Dataset/z_order_dataset/6_4d_NYC_z_order.txt";

        for(int i=0;i<address.length;i++){
            Learned_index_time(address[i],Z_order_address[i]);
        }

    }
    public void Learned_index_time(String k_value,String address) throws IOException{
        HyperPoint[] sample = ReadData(k_value,Integer.MAX_VALUE);
        big_Params p = new big_Params();
        BigInteger[] index = Read_Z_order(address,sample.length);

        BigDecimal[] myBigDecimalArray = new BigDecimal[index.length];
        long start = System.currentTimeMillis();
        for (int i = 0; i < index.length; i++) {
            myBigDecimalArray[i] = new BigDecimal(index[i]);
        }
        big_RMI rmi = new big_RMI();
        big_linear_model[] model = rmi.train(p,myBigDecimalArray);
        long end = System.currentTimeMillis();
        //double cdf = predicition(model,target);
        System.out.print(k_value+" time: "+(end-start)+"\n");
    }
    //T_drive_No_duplication.txt time: 4331
    //2_2d_Porto_No_duplication.txt time: 39
    //3_3d_spatial_network.txt time: 344
    //4_3d_Snapenet_new.txt time: 109
    //5_3d_ConfLongDemo_JSI.txt time: 0
    //6_4d_NYC_10M_transition_edit.txt time: 110

    //memory cost
    @Test
    public void learned_memory() throws IOException {
        int l = 6;
        String[] Query_address = new String[l];
        String[] address = new String[6];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";//这个行
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[5] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";

        String[] Z_order_address = new String[6];
        Z_order_address[0] = "src/test/java/Dataset/z_order_dataset/1_2d_T_drive_z_order.txt";
        Z_order_address[1] = "src/test/java/Dataset/z_order_dataset/2_2d_Porto_No_duplication.txt";
        Z_order_address[2] = "src/test/java/Dataset/z_order_dataset/3_3d_spatial_network_z_order.txt";
        Z_order_address[3] = "src/test/java/Dataset/z_order_dataset/4_3d_Snapenet_z_order.txt";
        Z_order_address[4] = "src/test/java/Dataset/z_order_dataset/5_3d_ConfLongDemo_z_order.txt";
        Z_order_address[5] = "src/test/java/Dataset/z_order_dataset/6_4d_NYC_z_order.txt";


        long[] log = new long[l];
        for(int i=5;i<l;i++){
            log[i] = learned_memory(address[i],Z_order_address[i]);
        }

    }
    public static long learned_memory(String k_value, String address) throws IOException {
        HyperPoint[] sample = ReadData(k_value,Integer.MAX_VALUE);
        BigInteger[] index = Read_Z_order(address,sample.length);
        long memory = RamUsageEstimator.sizeOf(index);
        return memory/(1024*1024);
    }

   //learned kNN time test
    @Test
    public void learned_kNN_time() throws IOException {
        int l = 6;
        String[] Query_address = new String[l];
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";

        String[] Z_order_address = new String[l];
        Z_order_address[0] = "src/test/java/Dataset/z_order_dataset/1_2d_T_drive_z_order.txt";
        Z_order_address[1] = "src/test/java/Dataset/z_order_dataset/2_2d_Porto_No_duplication.txt";
        Z_order_address[2] = "src/test/java/Dataset/z_order_dataset/3_3d_spatial_network_z_order.txt";
        Z_order_address[3] = "src/test/java/Dataset/z_order_dataset/4_3d_Snapenet_z_order.txt";
        Z_order_address[4] = "src/test/java/Dataset/z_order_dataset/5_3d_ConfLongDemo_z_order.txt";
        Z_order_address[5] = "src/test/java/Dataset/z_order_dataset/6_4d_NYC_z_order.txt";

        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/lightweight_test_data"+(i+1)+".txt";
        }

        long[] log = new long[l];
        for(int i=0;i<l;i++){
            log[i] = learned_kNN(address[i],Z_order_address[i], Query_address[i]);
        }
        //SavetoCSV(log,"src/test/java/expfile/competitor/z_order_kNN1");
    }
    public static long learned_kNN(String k_value, String address, String Query_address) throws IOException {
        HyperPoint[] sample = ReadData(k_value,Integer.MAX_VALUE);
        BigInteger[] index = Read_Z_order(address,sample.length);
        HyperPoint[] query = ReadQuery(Query_address,sample[0].getK());
        Arrays.sort(index);
        int[] k = Readk(Query_address,sample[0].getK());

        BigInteger[] arr = new BigInteger[query.length];
        for(int i=0;i<arr.length;i++){
            double[] ele1 = query[i].getcoords();
            BigInteger[] ele2 = new BigInteger[ele1.length];
            for(int j=0;j<ele1.length;j++){
                ele2[j] = BigInteger.valueOf((long)ele1[j]);
            }
            arr[i] = encode(ele2);
        }

        BigDecimal[] myBigDecimalArray = new BigDecimal[index.length];
        for (int i = 0; i < index.length; i++) {
            myBigDecimalArray[i] = new BigDecimal(index[i]);
        }
        System.out.print("prepare "+"\n");
        big_RMI rmi = new big_RMI();
        big_Params p = new big_Params();
        big_linear_model[] model = rmi.train(p,myBigDecimalArray);
        System.out.print("training finished "+"\n");
        long start = System.currentTimeMillis();
        for(int i=0;i<arr.length;i++){
            BigDecimal target = new BigDecimal(arr[i]);
            double cdf = predicition(model,target);
            long[] count = new long[1];
            List<BigInteger> list = learned_findKNearestNeighbors(index,arr[i],k[i],cdf,count);
            for(int j=0;j<list.size();j++) {
                BigInteger[] ele = zOrderDecode(list.get(j), sample[0].getK());
            }
        }
        long end = System.currentTimeMillis();
        System.out.print(k_value+" "+(end-start)+"\n");
        return end-start;
    }

    //T_drive_No_duplication.txt 10371
    //1302
    //spatial_network.txt 31269
    //Snapenet_new.txt 6303
    //ConfLongDemo_JSI.txt 86912
    //NYC_10M_transition_edit.txt 2752
    //learned kNN node test
    @Test
    public void learned_kNN_node() throws IOException {
        int l = 6;
        String[] Query_address = new String[l];
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/2_2d_Porto_No_duplication.txt";
        address[2] = "src/test/java/Dataset/3_3d_spatial_network.txt";
        address[3] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[4] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt";
        address[5] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";

        String[] Z_order_address = new String[l];
        Z_order_address[0] = "src/test/java/Dataset/z_order_dataset/1_2d_T_drive_z_order.txt";
        Z_order_address[1] = "src/test/java/Dataset/z_order_dataset/2_2d_Porto_No_duplication.txt";
        Z_order_address[2] = "src/test/java/Dataset/z_order_dataset/3_3d_spatial_network_z_order.txt";
        Z_order_address[3] = "src/test/java/Dataset/z_order_dataset/4_3d_Snapenet_z_order.txt";
        Z_order_address[4] = "src/test/java/Dataset/z_order_dataset/5_3d_ConfLongDemo_z_order.txt";
        Z_order_address[5] = "src/test/java/Dataset/z_order_dataset/6_4d_NYC_z_order.txt";

        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/lightweight_test_data"+(i+1)+".txt";
        }

        long[] log = new long[l];
        for(int i=0;i<l;i++){
            log[i] = learned_kNN_node(address[i],Z_order_address[i], Query_address[i]);
        }
        //SavetoCSV(log,"src/test/java/expfile/competitor/z_order_kNN1");
    }
    public static long learned_kNN_node(String k_value, String address, String Query_address) throws IOException {
        HyperPoint[] sample = ReadData(k_value,Integer.MAX_VALUE);
        BigInteger[] index = Read_Z_order(address,sample.length);
        HyperPoint[] query = ReadQuery(Query_address,sample[0].getK());
        Arrays.sort(index);
        int[] k = Readk(Query_address,sample[0].getK());

        BigInteger[] arr = new BigInteger[query.length];
        for(int i=0;i<arr.length;i++){
            double[] ele1 = query[i].getcoords();
            BigInteger[] ele2 = new BigInteger[ele1.length];
            for(int j=0;j<ele1.length;j++){
                ele2[j] = BigInteger.valueOf((long)ele1[j]);
            }
            arr[i] = encode(ele2);
        }

        BigDecimal[] myBigDecimalArray = new BigDecimal[index.length];
        for (int i = 0; i < index.length; i++) {
            myBigDecimalArray[i] = new BigDecimal(index[i]);
        }
        System.out.print("prepare "+"\n");
        big_RMI rmi = new big_RMI();
        big_Params p = new big_Params();
        big_linear_model[] model = rmi.train(p,myBigDecimalArray);
        System.out.print("training finished "+"\n");
        long[] count = new long[1];
        for(int i=0;i<arr.length;i++){
            BigDecimal target = new BigDecimal(arr[i]);
            double cdf = predicition(model,target);
            List<BigInteger> list = learned_findKNearestNeighbors(index,arr[i],k[i],cdf,count);
            for(int j=0;j<list.size();j++) {
                BigInteger[] ele = zOrderDecode(list.get(j), sample[0].getK());
            }
        }
        System.out.print(k_value+" "+count[0]+"\n");
        return 1;
    }

    @Test
    public void kNN_node() throws IOException {
        int l = 5;
        String[] Query_address = new String[l];
        String[] address = new String[l];
        address[0] = "src/test/java/Dataset/1_2d_T_drive_No_duplication.txt";
        address[1] = "src/test/java/Dataset/3_3d_spatial_network.txt";//这个行
        address[2] = "src/test/java/Dataset/4_3d_Snapenet_new.txt";
        address[3] = "src/test/java/Dataset/5_3d_ConfLongDemo_JSI.txt"; //三快
        address[4] = "src/test/java/Dataset/6_4d_NYC_10M_transition_edit.txt";

        String[] Z_order_address = new String[l];
        Z_order_address[0] = "src/test/java/Dataset/z_order_dataset/1_2d_T_drive_z_order.txt";
        Z_order_address[1] = "src/test/java/Dataset/z_order_dataset/3_3d_spatial_network_z_order.txt";
        Z_order_address[2] = "src/test/java/Dataset/z_order_dataset/4_3d_Snapenet_z_order.txt";
        Z_order_address[3] = "src/test/java/Dataset/z_order_dataset/5_3d_ConfLongDemo_z_order.txt";
        Z_order_address[4] = "src/test/java/Dataset/z_order_dataset/6_4d_NYC_z_order.txt";

        for(int i=0;i<l;i++){
            Query_address[i] = "src/test/java/expfile/AutokNN/Auto/test_data"+(i+1)+".txt";
        }
        long[] log = new long[5];
        for(int i=0;i<l;i++){
            log[i] = kNN_node(address[i],Z_order_address[i], Query_address[i]);
        }
        //SavetoCSV(log,"src/test/java/expfile/competitor/z_order_kNN");
    }
    public static long kNN_node(String k_value, String address, String Query_address) throws IOException {
        HyperPoint[] sample = ReadData(k_value,Integer.MAX_VALUE);
        BigInteger[] index = Read_Z_order(address,sample.length);
        HyperPoint[] query = ReadQuery(Query_address,sample[0].getK());
        Arrays.sort(index);
        int[] k = Readk(Query_address,sample[0].getK());

        BigInteger[] arr = new BigInteger[query.length];
        for(int i=0;i<arr.length;i++){
            double[] ele1 = query[i].getcoords();
            BigInteger[] ele2 = new BigInteger[ele1.length];
            for(int j=0;j<ele1.length;j++){
                ele2[j] = BigInteger.valueOf((long)ele1[j]);
            }
            arr[i] = encode(ele2);
        }
        long[] count = new long[1];
        for(int i=0;i<arr.length;i++){
            List<BigInteger> list = findKNearestNeighbors(index,arr[i],k[i],count);

            if(i==1){
                //System.out.print("res.size"+list.size()+" ");
                for(int j=0;j<list.size();j++){
                    System.out.print(list.get(j)+" ");
                }
            }
            List<BigInteger[]> res = new ArrayList<>();
            for(int j=0;j<list.size();j++) {
                BigInteger[] ele = zOrderDecode(list.get(j), sample[0].getK());
            }
        }
        System.out.print(count[0]+"\n");
        return count[0];
    }


}
