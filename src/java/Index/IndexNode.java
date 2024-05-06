package Index;

import RMI.LinearRegression;
import RMI.TrainedRMI;
import RMI.linear_model;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class IndexNode {
    // HyperSpace hs is used to accelerate range search
    HyperSpace hs;

    int Max_Gap_Size = 100000;

    boolean Stage_Gap;

    HyperPoint[] Gap;

    HyperPoint[] garbage_collection;

    int garbage_location;

    Vector<Integer> Inserted_Location;

    int Gap_idx;

    BallSpace bs;

    // Current spliting node
    HyperPoint p;

    linear_model[] model;

    boolean hasModel = false;

    IndexNode[] children;

    boolean isleaf = false;

    boolean hasSpace = true;

    int ID;

    int empty_id;
    HyperPoint[] hp;
    int Node_count;

    double[] pivot;

    int l;

    int r;

    TrainedRMI train_rmi;

    int depth;

    int DLength = 100000;


    public int getL() {
        return l;
    }

    public int getR() {
        return r;
    }

    public void Fresh(){
        this.garbage_collection = new HyperPoint[Max_Gap_Size];
        this.Gap = new HyperPoint[Max_Gap_Size];
        this.Inserted_Location = new Vector<>();
        this.garbage_location = 0;
        this.Gap_idx = 0;
    }

    public TrainedRMI getTrain_rmi() {
        return train_rmi;
    }

    public boolean isLeaf(){
        for(int i=0;i<children.length;i++){
            if(children[i] == null) return true;
        }
        return false;
    }

    public IndexNode(HyperSpace hs, HyperPoint p) {
        this.hs = hs;
        this.p = p;
    }

    public IndexNode(HyperSpace hs) {
        this.hs = hs;
    }

    public IndexNode() {
        this.hs = null;
        this.p = null;
    }

    public void setP(HyperPoint p) {
        this.p = p;
    }


    public void setModel(linear_model[] model) {
        this.model = model;
    }

    public linear_model[] getModel() {
        return model;
    }

    public HyperPoint getP() {
        return p;
    }

    public HyperSpace getHs() {
        return hs;
    }

    public boolean isHasModel() {
        return hasModel;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    //"C:\\Users\\jys19\\Downloads\\train\\train"

    public static void main(String[] args) throws IOException {
        String rootFolderPath = "C:\\Users\\jys19\\Downloads\\train\\train";

        try (Stream<Path> pathStream = Files.walk(Paths.get(rootFolderPath))) {
            String[] jsonFileAddresses = pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::toString)
                    .toArray(String[]::new);

            for(int i=0;i<jsonFileAddresses.length;i++){
                EatData(jsonFileAddresses[i],i);
                System.out.print("success "+i+"\n");
            }
        }
    }

    public static void EatData(String address,int ID) {


        String jsonFileName = address;
        String csvFileName = "src/test/CloudPoint/"+ID+".csv";

        try (BufferedReader br = new BufferedReader(new FileReader(jsonFileName));
             BufferedWriter bw = new BufferedWriter(new FileWriter(csvFileName))) {


            Pattern pattern = Pattern.compile("\\{\"x\": ([-\\d.]+), \"y\": ([-\\d.]+), \"z\": ([-\\d.]+)\\}");

            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
        
                    String x = matcher.group(1);
                    String y = matcher.group(2);
                    String z = matcher.group(3);

         
                    bw.write(x + "," + y + "," + z);
                    bw.newLine();
                }
            }

            System.out.println("read: " + csvFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
