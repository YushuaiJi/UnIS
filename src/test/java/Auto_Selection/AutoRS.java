package Auto_Selection;

import Index.AutoIS;
import Index.HyperPoint;
import Index.HyperSpace;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static InputOutput.readfile.*;
import static lsh.LSHMinHash.minLSH_AutoIS_kNN;
import static lsh.LSHMinHash.minLSH_AutoIS_RQ;

public class AutoRS {

    @Test
    public void Range_query_ground_truth() throws IOException {
        String[] address = new String[8];
        address[0] = "src/test/java/Dataset/ArgoPOI_clean.csv";
        address[1] = "src/test/java/Dataset/ArgoAVL_2d_clean.csv";
        address[2] = "src/test/java/Dataset/2_2d_Porto_No_duplication_clean.txt";
        address[3] = "src/test/java/Dataset/1_2d_T_drive_No_duplication_clean.txt";
        address[4] = "src/test/java/Dataset/4_3d_Snapenet_new_clean.txt";
        address[5] = "src/test/java/Dataset/ArgoPC_clean.csv";
        address[6] = "src/test/java/Dataset/Apollo_Scape_Cloud_Point_clean.csv";
        address[7] = "src/test/java/Dataset/ArgoTraj_4d_clean.csv";
        for(int i=0;i<address.length;i++){
            lightweight_GenerateData(address[i],i+1);
        }
    }


    public void lightweight_GenerateData(String address,int id) throws IOException {
        int sample_size = ReadDataAmount(address);
        int dim = ReadDim(address);
        int train_size = 50000;
        //double[][] data_log = new double[train_size*3][dim+2];//
        Vector<LinkedList<Double>> log = Range_query_data(address,sample_size,train_size);
        if (log == null || log.isEmpty()) {
            System.out.println("⚠️ Dataset " + address + " null。");
            return;
        }
        int training_perenct = (int)(log.size()*0.8);
        //double[][] log = kNNdata(address,sample_size,train_size);
        String train_address = "src/test/java/expfile/AutokNN/Verification/range_train_data"+id+".txt";
        String test_address = "src/test/java/expfile/AutokNN/Verification/range_test_data"+id+".txt";
        SavetoCSV(log.get(0).size(),log,train_address,test_address,training_perenct);
    }

    public Vector<LinkedList<Double>> Range_query_data(String address, int sample_size, int train_sample) throws IOException {
        System.out.print("===================" + address + " started" + "===================" + "\n");

        Vector<LinkedList<Double>> result = new Vector<>();

        Callable<Void> task = () -> {
            HyperPoint[] sample = ReadData(address, sample_size);
            System.out.print(sample[0].getK());
            LinkedList<double[]> list = quantile_value(sample, 0.05);
            int K = list.get(0).length;
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd1 = new AutoIS(K, min, max);
            kd1.ConstructionAutoIS(sample, 4);
            kd1.setBallSpace(kd1.getRoot());

            HyperSpace[] Rand_Range = generate_sample(list, train_sample, sample);
            int[][][] lsh = minLSH_AutoIS_RQ(sample, 0.75, Rand_Range);

            Random rand = new Random();

            for (int i = 0; i < train_sample; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                int idx = rand.nextInt(sample.length);
                HyperPoint hp = sample[idx];
                int[][] e = new int[2][2];
                SortComparator sc = new SortComparator();
                double r = new Random().nextDouble() * 5;

                // method 1
                int[] count = new int[1];
                long start1 = System.currentTimeMillis();
                kd1.Radius_Search(hp, r, 1, count);
                long end1 = System.currentTimeMillis();
                e[0][0] = 1;
                e[0][1] = (int) (end1 - start1);
//                System.out.print("method1 " + e[0][1] + "|| ");

                // method 2
                count = new int[1];
                long start2 = System.currentTimeMillis();
                kd1.Radius_Search(hp, r, 2, count);
                long end2 = System.currentTimeMillis();
                e[1][0] = 2;
                e[1][1] = (int) (end2 - start2);
//                System.out.print("method2 " + count[0] + "|| ");

                Arrays.sort(e, sc);
//                System.out.print("best method " + e[0][0] + "\n");

                LinkedList<Double> log = new LinkedList<>();
                for (int j = 0; j < hp.getK(); j++) {
                    log.add(hp.getcoords()[j]);
                }
                for (int j = 0; j < lsh[i][0].length; j++) {
                    log.add((double) lsh[i][0][j]);
                }
                for (int j = 0; j < lsh[i][1].length; j++) {
                    log.add((double) lsh[i][1][j]);
                }
                log.add((double) e[0][0]);
                for (int h = 0; h < e.length; h++) {
                    log.add((double) e[h][0]);
                }

                result.add(log);
            }
            return null;
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            // 
            executor.submit(task).get(150, TimeUnit.SECONDS);
            System.out.println(address + " finished normally");
        } catch (TimeoutException e) {
            System.out.println(address + " ⚠️ total time out (150s), saving partial result...");
            // 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow(); // 
        }
        return result; //
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


    @Test
    public void test_radius_search_verification() throws IOException {
        int l = 8;
        String[] address = new String[l];
        String[] Query_address = new String[l];
        String[] best_method_address = new String[l];

        address[0] = "src/test/java/Dataset/ArgoPOI_clean.csv";
        address[1] = "src/test/java/Dataset/ArgoAVL_2d_clean.csv";
        address[2] = "src/test/java/Dataset/2_2d_Porto_No_duplication_clean.txt";
        address[3] = "src/test/java/Dataset/1_2d_T_drive_No_duplication_clean.txt";
        address[4] = "src/test/java/Dataset/4_3d_Snapenet_new_clean.txt";
        address[5] = "src/test/java/Dataset/ArgoPC_clean.csv";
        address[6] = "src/test/java/Dataset/Apollo_Scape_Cloud_Point_clean.csv";
        address[7] = "src/test/java/Dataset/ArgoTraj_4d_clean.csv";

        for (int i = 0; i < l; i++) {
            Query_address[i] = "src/test/java/expfile/AutokNN/Verification/range_test_100_data" + (i + 1) + ".txt";
            best_method_address[i] = "src/test/java/expfile/AutokNN/Verification/range_predict_val(" + (i + 1) + ").csv";
        }

        String[] datasetNames = {
                "ArgoPOI", "ArgoAVL_2d", "Porto_2d", "T_drive_2d",
                "Snapenet_3d", "ArgoPC", "Apollo_Scape", "ArgoTraj_4d"
        };

        double[] speedupResults = new double[l];
        double[] predictionResults = new double[l];

        Map<String, Double> predictTimeMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/test/java/expfile/AutokNN/Verification/predict_time_results.csv"))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String dataset = parts[0].trim();     // df1, df2 ...
                    String raw = parts[2].trim();
                    if (!raw.isEmpty()) {
                        double t3 = Double.parseDouble(raw);
                        predictTimeMap.put(dataset, t3);
                    }
                }
            }
        }
        System.out.println("predictTimeMap = " + predictTimeMap);

        Map<String, String> datasetKeyMap = new HashMap<>();
        datasetKeyMap.put("ArgoPOI", "df1");
        datasetKeyMap.put("ArgoAVL_2d", "df2");
        datasetKeyMap.put("Porto_2d", "df3");
        datasetKeyMap.put("T_drive_2d", "df4");
        datasetKeyMap.put("Snapenet_3d", "df5");
        datasetKeyMap.put("ArgoPC", "df6");
        datasetKeyMap.put("Apollo_Scape", "df7");
        datasetKeyMap.put("ArgoTraj_4d", "df8");

        Random globalRand = new Random(42);

        for (int i = 0; i < l; i++) {
            System.out.println("========== Dataset: " + datasetNames[i] + " ==========");

            HyperPoint[] sample = ReadData(address[i], Integer.MAX_VALUE);
            if (sample == null || sample.length == 0) {
                System.out.println("fail: " + address[i]);
                speedupResults[i] = -1;
                predictionResults[i] = -1;
                continue;
            }
            int dim = sample[0].getK();

            HyperPoint[] queries = ReadQuery(Query_address[i], dim);
            if (queries == null || queries.length == 0) {
                System.out.println("Query 文件为空或读取失败: " + Query_address[i]);
                speedupResults[i] = -1;
                predictionResults[i] = -1;
                continue;
            }
            
            LinkedList<double[]> list = quantile_value(sample, 0.05);
            HyperPoint min = new HyperPoint(list.get(0));
            HyperPoint max = new HyperPoint(list.get(1));
            AutoIS kd = new AutoIS(dim, min, max);
            kd.ConstructionAutoIS(sample, 4);
            kd.setBallSpace(kd.getRoot());


            int maxQueriesToUse = Math.min(queries.length, 100); // 你可以改成 queries.length 来用全部
            double sum_t1_m1 = 0.0;
            double sum_t1_m2 = 0.0;
            double sum_t2_best = 0.0;


            int[] best_method_arr = Read_single_res(best_method_address[i], 1);
            int bestMethodFromFile = (best_method_arr != null && best_method_arr.length > 0) ? best_method_arr[0] : 1;

            for (int qidx = 0; qidx < maxQueriesToUse; qidx++) {
                HyperPoint q = queries[qidx];
    
                double initialR = globalRand.nextDouble() * 50.0;

                double t1_m1 = adaptiveRadiusSearch(kd, q, 1, initialR);
                double t1_m2 = adaptiveRadiusSearch(kd, q, 2, initialR);
                double t2_best = adaptiveRadiusSearch(kd, q, bestMethodFromFile, initialR);

                sum_t1_m1 += t1_m1;
                sum_t1_m2 += t1_m2;
                sum_t2_best += t2_best;
            }

            double t1_avg = (sum_t1_m1 + sum_t1_m2) / (2.0 * maxQueriesToUse);
            double t2_avg = sum_t2_best / (double) maxQueriesToUse;

            double speedup;
            if (t1_avg <= 0 || Double.isNaN(t1_avg)) {
                speedup = -1.0;
            } else {
                speedup = (t1_avg - t2_avg) / t1_avg;
            }
            speedupResults[i] = speedup;

            String dfKey = datasetKeyMap.get(datasetNames[i]);
            Double t3 = predictTimeMap.get(dfKey) / 100;
            if (t3 == null) {
                System.out.println("⚠️ predict_time_results 没有 " + dfKey + " 的记录，设 t3=0");
                t3 = 0.0;
            }

            double predictionVal;
            if ((t2_avg + t3) <= 0 || Double.isNaN(t2_avg + t3)) {
                predictionVal = -1.0;
            } else {
                predictionVal = t3 / (t2_avg + t3);
            }
            predictionResults[i] = predictionVal;

            System.out.println(datasetNames[i] + " => t1_avg=" + t1_avg + ", t2_avg=" + t2_avg + ", t3=" + t3 +
                    ", speedup=" + speedup + ", prediction=" + predictionVal);

            System.out.println("========== Dataset: " + datasetNames[i] + " ==========\n");
        }

        // 保存 speedup.csv
        try (FileWriter writer = new FileWriter("src/test/java/expfile/AutokNN/Verification/speedup.csv")) {
            writer.append("Dataset,Speedup\n");
            for (int i = 0; i < l; i++) {
                writer.append(datasetNames[i]).append(",").append(String.valueOf(speedupResults[i])).append("\n");
            }
        }

        // 保存 prediction.csv
        try (FileWriter writer = new FileWriter("src/test/java/expfile/AutokNN/Verification/prediction.csv")) {
            writer.append("Dataset,Prediction\n");
            for (int i = 0; i < l; i++) {
                writer.append(datasetNames[i]).append(",").append(String.valueOf(predictionResults[i])).append("\n");
            }
        }

        System.out.println("CSV ：speedup.csv 和 prediction.csv");
    }

    
    private double adaptiveRadiusSearch(AutoIS kd, HyperPoint query, int method, double initialR) {
        double r = Math.max(initialR, 1e-6); // 
        int maxTries = 10;
        int[] count = new int[1];

        for (int i = 0; i < maxTries; i++) {
            long startNano = System.nanoTime();
            kd.Radius_Search(query, r, method, count);
            long endNano = System.nanoTime();
            double tMs = (endNano - startNano) / 1_000_000.0;

            if (count[0] > 0 && tMs > 0 && !Double.isNaN(tMs)) {
                return tMs;
            }

            r *= 2.0;
        }

        System.out.println("⚠️ adaptiveRadiusSearch failed after " + maxTries + " tries for method=" + method + ", returning fallback tiny value");
        return 0.0001;
    }

}
