package InputOutput;


import Index.HyperPoint;
import Index.HyperSpace;

import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class readfile {

    public static int ReadDataAmount(String address) throws IOException {
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int count = 0;
        while (buffReader.ready()) {
            String tmp= buffReader.readLine()+s;
            count++;
        }
        return count;
    }

    public static HyperPoint[] ReadData(String address, int number) throws IOException {
        Vector<HyperPoint> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int count = 0;
        while (buffReader.ready()) {
            if(count == number) break;
            count++;
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            double[] tmp_p = new double[arr.length];
            for(int i=0;i<arr.length;i++){
                if(i == arr.length-1){
                    double loc = Double.parseDouble(arr[i].substring(0, arr[i].length() - 4));
                    tmp_p[i] = loc;
                }else{
                    double loc = Double.parseDouble(arr[i]);
                    tmp_p[i] = loc;
                }
            }
            HyperPoint p = new HyperPoint(tmp_p);
            points.add(p);
        }
        //System.out.print(points.size()+"\n");
        HyperPoint[] sample = new HyperPoint[points.size()];
        for(int i=0;i<points.size();i++) {
            sample[i] = points.get(i);
        }
        return sample;
    }

    public static double[][] Competitor_ReadData(String address, int number) throws IOException {
        Vector<double[]> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int count = 0;
        while (buffReader.ready()) {
            if(count == number) break;
            count++;
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            double[] tmp_p = new double[arr.length];
            for(int i=0;i<arr.length;i++){
                if(i == arr.length-1){
                    double loc = Double.parseDouble(arr[i].substring(0, arr[i].length() - 4));
                    tmp_p[i] = loc;
                }else{
                    double loc = Double.parseDouble(arr[i]);
                    tmp_p[i] = loc;
                }
            }
            points.add(tmp_p);
        }
        //System.out.print(points.size()+"\n");
        double[][] sample = new double[points.size()][points.get(0).length];
        for(int i=0;i<points.size();i++){
            sample[i] = points.get(i);
        }
        return sample;
    }

    public static HyperPoint[] ReadQuery(String address,int k) throws IOException {
        Vector<HyperPoint> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        String tmp = null;
        if(buffReader.ready()) tmp = buffReader.readLine()+s;
        while (buffReader.ready()) {
            tmp = buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            double[] tmp_p = new double[k];
            for(int i=0;i<k;i++){
                double loc = Double.parseDouble(arr[i]);
                tmp_p[i] = loc;
            }
            HyperPoint p = new HyperPoint(tmp_p);
            points.add(p);
        }
        //System.out.print(points.size()+"\n");
        HyperPoint[] sample = new HyperPoint[points.size()];
        for(int i=0;i<points.size();i++) {
            sample[i] = points.get(i);
        }
        return sample;
    }

    public static HyperSpace[] ReadHS(String address, int k) throws IOException {
        Vector<HyperSpace> HS = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        String tmp = null;
        if(buffReader.ready()) tmp = buffReader.readLine()+s;
        while (buffReader.ready()) {
            tmp = buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            double[] tmp_p_1 = new double[k];
            double[] tmp_p_2 = new double[k];
            for(int i=0;i<k;i++){
                double loc = Double.parseDouble(arr[i]);
                tmp_p_1[i] = loc;
            }
            for(int i=k;i<k*2;i++){
                double loc = Double.parseDouble(arr[i]);
                tmp_p_2[i-k] = loc;
            }
            HyperPoint p_1 = new HyperPoint(tmp_p_1);
            HyperPoint p_2 = new HyperPoint(tmp_p_2);
            HyperSpace hs = new HyperSpace(p_1,p_2);
            HS.add(hs);
        }
        //System.out.print(points.size()+"\n");
        HyperSpace[] sample = new HyperSpace[HS.size()];
        for(int i=0;i<HS.size();i++) {
            sample[i] = HS.get(i);
        }
        return sample;
    }
    public static int[] Readk(String address,int k) throws IOException {
        Vector<Integer> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        while (buffReader.ready()) {
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            int loc = 0;
            for(int i=0;i<arr.length;i++){
                if(i == (k*2+3)){
                    loc = (int)Double.parseDouble(arr[i]);
                }
            }
            points.add(loc);
        }
        //System.out.print(points.size()+"\n");
        int[] sample = new int[points.size()];
        for(int i=0;i<points.size();i++) {
            sample[i] = points.get(i);
        }
        return sample;
    }

    public static double Readlength(String address, int number) throws IOException {
        Vector<HyperPoint> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int count = 0;
        double len = 0;
        while (buffReader.ready()) {
            if(count == number) break;
            count++;
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");

            for(int i=0;i<arr.length;i++){
                if(i == arr.length-1){
                    len = len+ arr[i].length()-5;
                }else{
                    len = len+ arr[i].length()-1;
                }
            }
        }
        //System.out.print(points.size()+"\n");
        return len/(ReadDim(address)*number);
    }

    public static int ReadDim(String address) throws IOException {
        Vector<HyperPoint> points = new Vector<>();
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int k = 0;
        if(buffReader.ready()){
            String tmp= buffReader.readLine()+s;
            String[] arr = tmp.split(",");
            double[] tmp_p = new double[arr.length];
            for(int i=0;i<arr.length;i++){
                if(i == arr.length-1){
                    double loc = Double.parseDouble(arr[i].substring(0, arr[i].length() - 4));
                    tmp_p[i] = loc;
                }else{
                    double loc = Double.parseDouble(arr[i]);
                    tmp_p[i] = loc;
                }
            }
            HyperPoint p = new HyperPoint(tmp_p);
            k = p.getK();
        }
        return k;
    }

    public static int ReadLine(String address) throws IOException {
        int res = -1;
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        while (buffReader.ready()) {
            String tmp= buffReader.readLine()+"";
            res++;
        }
        return res;
    }

    public static int[] Read_single_res(String address,int len) throws IOException {
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int anchor = 0;
        int[] arr = new int[len];
        while (buffReader.ready()) {
            String tmp= buffReader.readLine()+s;
            int loc = Integer.parseInt(tmp.substring(0,tmp.length()-4));
            arr[anchor] = loc;
            anchor++;
        }
        return arr;
    }
    public static BigInteger[] Read_Z_order(String address, int len) throws IOException {
        FileReader fileReader = new FileReader(address);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String s = null;
        int anchor = 0;
        BigInteger[] arr = new BigInteger[len];
        while (buffReader.ready()) {
            String tmp= buffReader.readLine()+s;
            BigInteger loc = new BigInteger(tmp.substring(0,tmp.length()-4));
            //int loc = Integer.parseInt(tmp.substring(0,tmp.length()-4));
            arr[anchor] = loc;
            anchor++;
        }
        return arr;
    }


    public static LinkedList<double[]> Max_Min_value(HyperPoint[] hp){
        LinkedList<double[]> list = new LinkedList<>();
        int k = hp[0].getK();
        double[] max = new double[k];
        double[] min = new double[k];
        for(int i=0;i<k;i++){
            max[i] = Double.MIN_VALUE;
            min[i] = Double.MAX_VALUE;
        }

        for(int i=0;i<hp.length;i++){
            for(int j=0;j<k;j++){
                if(hp[i].getcoords()[j] >max[j]) max[j] =hp[i].getcoords()[j];
                if(hp[i].getcoords()[j] <min[j]) min[j] =hp[i].getcoords()[j];
            }
        }
        list.add(min);
        list.add(max);
        return list;
    }

    public static LinkedList<double[]> quantile_value(HyperPoint[] hp,double ratio){
        LinkedList<double[]> list = new LinkedList<>();
        int k = hp[0].getK();
        double[] max = new double[k];
        double[] min = new double[k];
        for(int i=0;i<k;i++){
            double[] arr = new double[hp.length];
            for(int j=0;j<hp.length;j++){
               arr[j] = hp[j].getcoords()[i];
            }
            Arrays.sort(arr);
            int lo = (int)(ratio * hp.length);
            int hi = (int)((1-ratio)* hp.length);
            min[i] = arr[lo];
            max[i] = arr[hi];
        }
        list.add(min);
        list.add(max);
        return list;
    }

    public static void SavetoCSV(long[][][] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            for(int j=0;j<datalog[0].length;j++){
                for(int k=0;k<datalog[0][0].length;k++){
                    String s = datalog[i][j][k]+"";
                    if(k == datalog[0][0].length-1) bw.write(s);
                    else bw.write(s+",");
                    System.out.print(s+"\n");
                }
                bw.newLine();
            }
        }
        bw.close();
    }

    public static void SavetoCSV(double[][][] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            for(int j=0;j<datalog[0].length;j++){
                for(int k=0;k<datalog[0][0].length;k++){
                    String s = datalog[i][j][k]+"";
                    if(k == datalog[0][0].length-1) bw.write(s);
                    else bw.write(s+",");
                    System.out.print(s+"\n");
                }
                bw.newLine();
            }
        }
        bw.close();
    }

    public static void SavetoCSV(int[][][] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            for(int j=0;j<datalog[0].length;j++){
                for(int k=0;k<datalog[0][0].length;k++){
                    String s = datalog[i][j][k]+"";
                    if(k == datalog[0][0].length-1) bw.write(s);
                    else bw.write(s+",");
                    System.out.print(s+"\n");
                }
                bw.newLine();
            }
        }
        bw.close();
    }

    public static void SavetoCSV(int[] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            if(i == datalog.length-1) bw.write(datalog[i]+"");
            else bw.write(datalog[i]+",");
            bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV(long[] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            if(i == datalog.length-1) bw.write(datalog[i]+"");
            else bw.write(datalog[i]+",");
            bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV(long[][] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            for(int j=0;j<datalog[0].length;j++){
                String s = datalog[i][j]+"";
                if(j == datalog[0].length-1) bw.write(s);
                else bw.write(s+",");
            }
                bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV(LinkedList<LinkedList<Double>> datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.size(); i++) {
            for(int j=0;j<datalog.get(i).size();j++){
                String s = datalog.get(i).get(j)+"";
                if(j == datalog.get(i).size()-1) bw.write(s);
                else bw.write(s+",");
            }
            bw.newLine();
        }
        bw.close();
    }
    public static void SavetoCSV(int title,Vector<LinkedList<Double>>  datalog,String address1, String address2,int len) throws IOException {
        File fout = new File(address1);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < len; i++) {
            for(int j=0;j<datalog.get(i).size();j++){
                String s = datalog.get(i).get(j)+"";
                if(j == datalog.get(i).size()-1) bw.write(s);
                else bw.write(s+",");
            }
            bw.newLine();
        }
        bw.close();

        fout = new File(address2);
        fos = new FileOutputStream(fout);
        bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = len; i < datalog.size(); i++) {
            for(int j=0;j<datalog.get(i).size();j++){
                String s = datalog.get(i).get(j)+"";
                if(j == datalog.get(i).size()-1) bw.write(s);
                else bw.write(s+",");
            }
            bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV(Vector<LinkedList<Double>> datalog,String address1, String address2,int len) throws IOException {
        File fout = new File(address1);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        bw.write("feature,result");
        bw.newLine();
        for(int i = 0; i < len; i++) {
            for(int j=0;j<datalog.get(i).size();j++){
                String s = datalog.get(i).get(j)+"";
                if(j == datalog.get(i).size()-1) bw.write(s);
                else bw.write(s+",");
            }
            bw.newLine();
        }
        bw.close();

        fout = new File(address2);
        fos = new FileOutputStream(fout);
        bw = new BufferedWriter(new OutputStreamWriter(fos));

        bw.write("feature,result");
        bw.newLine();

        for(int i = len; i < datalog.size(); i++) {
            for(int j=0;j<datalog.get(i).size();j++){
                String s = datalog.get(i).get(j)+"";
                if(j == datalog.get(i).size()-1) bw.write(s);
                else bw.write(s+",");
            }
            bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV(int[][] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            for(int j=0;j<datalog[0].length;j++){
                String s = datalog[i][j]+"";
                if(j == datalog[0].length-1) bw.write(s);
                else bw.write(s+",");
            }
            bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV_blank(int[][] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            for(int j=0;j<datalog[0].length;j++){
                String s = datalog[i][j]+"";
                if(j == datalog[0].length-1) bw.write(s);
                else bw.write(s+" ");
            }
            bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV_blank(Vector<int[]> datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.size(); i++) {
            for(int j=0;j<datalog.get(i).length;j++){
                String s = datalog.get(i)[j]+"";
                if(j == datalog.get(i).length-1) bw.write(s);
                else bw.write(s+",");
            }
            bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV(double[][] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            for(int j=0;j<datalog[0].length;j++){
                String s = datalog[i][j]+"";
                if(j == datalog[0].length-1) bw.write(s);
                else bw.write(s+",");
            }
            bw.newLine();
        }
        bw.close();
    }

    public static void SavetoCSV(HyperPoint[] datalog,String address) throws IOException {
        File fout = new File(address);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(int i = 0; i < datalog.length; i++) {
            double[] coord = datalog[i].getcoords();
            String s = "";
            for(int j=0;j<coord.length;j++){
                if(j == coord.length-1) s = s+coord[j];
                else s = s+coord[j]+",";
            }
            bw.write(s);
            bw.newLine();
        }
        bw.close();
    }
}
