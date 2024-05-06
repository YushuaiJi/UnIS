package Index;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HyperPoint {
    double[] coords;
    int K = 0;

    boolean inserted;

    int inserted_location;

    public int getK() {
        return K;
    }

    public HyperPoint(double[] crds) {
        if (crds == null)
            throw new NullPointerException("");
        K = crds.length;
        coords = new double[K];
        for (int i = 0; i < K; i++)
            coords[i] = crds[i];
    }

    public double[] getcoords(){
        return this.coords;
    }

    public HyperPoint(HyperPoint p) {
        this(p.coords);
    }

    public boolean equals(HyperPoint p) {
        if (K != p.K)
            throw new IllegalArgumentException("");
        for (int i = 0; i < K; i++)
            if (p.coords[i] != coords[i])
                return false;
        return true;
    }

    // Euclidean Distance
    public double distanceTo(HyperPoint p) {
        return Math.sqrt(squareDistanceTo(p));
    }


    public double distanceTo(double[] p) {
        return Math.sqrt(squareDistanceTo(p));
    }
    public double squareDistanceTo(HyperPoint p) {
        if (K != p.K)
            throw new IllegalArgumentException("");
        double res = 0;
        for (int i = 0; i < K; i++)
            res += (coords[i] - p.coords[i]) * (coords[i] - p.coords[i]);
        return res;
    }

    public double squareDistanceTo(double[] p) {
        if (K != p.length)
            throw new IllegalArgumentException("");
        double res = 0;
        for (int i = 0; i < K; i++)
            res += (coords[i] - p[i]) * (coords[i] - p[i]);
        return res;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < K; i++)
            sb.append(coords[i] + ",");
        return sb.toString();
    }

    public void setCoords(double[] coords) {
        this.coords = coords;
    }

    public static void main(String[] args) {
        // 指定输入文件夹和输出文件
        String sourceFolder = "src/test/CloudPoint";
        String outputFile = "src/test/CloudPoint.csv";

        try (PrintWriter writer = new PrintWriter(outputFile)) {
            // 获取输入文件夹中的所有 CSV 文件
            List<File> csvFiles = Arrays.stream(Objects.requireNonNull(new File(sourceFolder).listFiles()))
                    .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".csv"))
                    .collect(Collectors.toList());

            // 遍历每个 CSV 文件并将其内容写入输出文件
            for (File csvFile : csvFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("CSV 文件已成功合并到 " +"\n");
            }

            System.out.println("CSV 文件已成功合并到 " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
