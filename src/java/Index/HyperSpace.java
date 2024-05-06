package Index;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class HyperSpace {
    HyperPoint min, max;
    int K = 0;

    public HyperSpace(HyperPoint min, HyperPoint max) {
        if (min == null ){
            System.out.print("min is null");
            throw new NullPointerException("");
        }
        if(max == null){
            System.out.print("max is null");
            throw new NullPointerException("");
        }
        K = min.K;
        if (K == 0 || K != max.K)
            throw new IllegalArgumentException("");
        this.min = new HyperPoint(min);
        this.max = new HyperPoint(max);
    }

    // Detect whether intersects with other HyperSpace or not
    public boolean intersects(HyperSpace p) {
        for (int i = 0; i < K; i++)
            if (min.coords[i] > p.max.coords[i] || max.coords[i] < p.min.coords[i])
                return false;
        return true;
    }

    public boolean contains(HyperPoint p) {
        if (K != p.K)
            throw new IllegalArgumentException("");
        for (int i = 0; i < K; i++)
            if (min.coords[i] > p.coords[i] || p.coords[i] > max.coords[i])
                return false;
        return true;
    }

    // The square of Euclidean Distance
    public double squareDistanceTo(HyperPoint p) {
        if (K != p.K)
            throw new IllegalArgumentException("");
        double res = 0;
        for (int i = 0; i < K; i++)
            if (min.coords[i] > p.coords[i])
                res += (min.coords[i] - p.coords[i]) * (min.coords[i] - p.coords[i]);
            else if (p.coords[i] > max.coords[i])
                res += (p.coords[i] - max.coords[i]) * (p.coords[i] - max.coords[i]);
        return res;
    }

    // Euclidean Distance
    public double distanceTo(HyperPoint p) {
        return Math.sqrt(squareDistanceTo(p));
    }

    public String toString() {
        return min.toString() + "->" + max.toString();
    }

    public HyperPoint getMin() {
        return min;
    }

    public HyperPoint getMax() {
        return max;
    }

    public static void main(String[] args) {
        String sourceFolder = "src/test/Trajectory_data_point"; // 替换为你的CSV文件所在文件夹的路径
        String mergedFilePath = "src/test/Trajectory_Data_Point.csv";  // 合并后的CSV文件路径
        int maxFilesToMerge = 20;             // 合并的最大文件数

        try {
            File folder = new File(sourceFolder);
            File[] files = folder.listFiles();
            List<String> mergedLines = new ArrayList<>();
            int filesMerged = 0;

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".csv")) {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;

                        while ((line = br.readLine()) != null) {
                            mergedLines.add(line);
                        }

                        br.close();

                        filesMerged++;

                        if (filesMerged >= maxFilesToMerge) {
                            break; // 达到最大文件数，停止合并
                        }
                    }
                }

                // 创建合并后的CSV文件并写入数据
                BufferedWriter bw = new BufferedWriter(new FileWriter(mergedFilePath));
                for (String mergedLine : mergedLines) {
                    bw.write(mergedLine);
                    bw.newLine();
                }
                bw.close();

                System.out.println("前" + maxFilesToMerge + "个CSV文件合并完成，合并后的文件路径为: " + mergedFilePath);
            } else {
                System.out.println("指定文件夹中没有CSV文件。");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
