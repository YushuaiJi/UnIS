package RMI;


import Index.HyperPoint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Vector;


public class RMI {

    linear_model[] model;
    public RMI() {
    }

    public linear_model[] getModel() {
        return model;
    }

    public void setModel(linear_model[] model) {
        this.model = model;
    }

    public linear_model[] train(Params p, double[] training_sample) {
        Vector<Training_point>[][] training_data= (Vector<Training_point>[][]) new Vector[2][p.arch.get(1)];
        training_data[0][0] = new Vector<>();
        for(int i=0;i<training_data[0].length;i++){
            training_data[1][i] = new Vector<>();
        }

       //model constructor
        linear_model[] model = new linear_model[p.arch.get(0)+p.arch.get(1)];

        Arrays.sort(training_sample);
        //training_sample = insertionSort(training_sample);
       if(training_sample[0]== training_sample[training_sample.length-1]) {
           linear_model tmp_model = new linear_model();
           tmp_model.slope = Integer.MAX_VALUE;
           tmp_model.intercept = Integer.MAX_VALUE;
           model[0] = tmp_model;
           return model;
        }
        // Populate the training data for the root model
        for(int i=0;i<training_sample.length;i++){
            Training_point tp = new Training_point(training_sample[i],(i*1.0/training_sample.length));
            training_data[0][0].add(tp);
        }

        Vector<Training_point> current_train_data = training_data[0][0];
        linear_model current_model = new linear_model();

        // Calculate the slope and intercept terms
        Training_point max = current_train_data.lastElement();
        Training_point min = current_train_data.firstElement();


        current_model.slope = 1/(max.T-min.T);
        current_model.intercept = -current_model.slope*min.T;//slope is larger than 0
        model[0] = current_model;//root model
        model[0].Case = training_data[0][0].size();

        //current_model.slope = current_model.slope*(p.arch.get(1)-1);
        //current_model.intercept = current_model.intercept*(p.arch.get(1)-1);

        //assign each train_data to the palce they stay

        for(Training_point pt:current_train_data){
            int rank = (int)((p.arch.get(1)-1)*(current_model.intercept-current_model.slope*(pt.T-max.T/2-min.T/2)));

            //BigDecimal number_of_model = new BigDecimal(p.arch.get(1));
            //BigDecimal pt_T = new BigDecimal(pt.T);
            //BigDecimal tmp_rank = (number_of_model.subtract(BigDecimal.ONE)).multiply((in.subtract(s.multiply((pt_T.subtract(MAX.divide(BigDecimal.valueOf(2)).add(MIN.divide(BigDecimal.valueOf(2)))))))));
            //int rank = tmp_rank.intValue();
            //int rank = (int) (pt.T*current_model.slope+current_model.intercept);

            rank = Math.max(0,Math.min(p.arch.get(1)-1,rank));
            System.out.print(rank+" ");
            training_data[1][rank].add(pt);
        }
        //for(Training_point pt:current_train_data){
        //    int cdf = (int)(1-Math.pow((max.T- pt.T)/(max.T- min.T),p.arch.get(1)-1));
        //    int rank = cdf*p.arch.get(1)-1;
        //    rank = Math.max(0,Math.min(p.arch.get(1)-1,rank));
         //   training_data[1][rank].add(pt);
        //    System.out.print(rank+" ");
        //}

        System.out.print("\n");
        //System.out.print("data end"+"\n");

        //leaf model
        for(int model_idx = 0;model_idx<p.arch.get(1);model_idx++){
            current_train_data = training_data[1][model_idx];
            current_model = new linear_model();
            // Interpolate the min points in the training buckets
            // The current model is the first model in the current layer
            if(model_idx == 0){
                if(current_train_data.size()<2){
                    current_model.intercept = 0;
                    current_model.slope = 0;
                    Training_point tmp_tp = new Training_point();
                    tmp_tp.T = 0;
                    tmp_tp.y = 0;
                    current_train_data.addElement(tmp_tp);
                    current_model.max = 0;
                    current_model.min = 0;
                }else{
                    max = current_train_data.lastElement();
                    min = current_train_data.firstElement();

                    current_model.slope = max.y/(max.T-min.T);
                    current_model.intercept = min.y - current_model.slope*min.T;

                    current_model.max = max.y;
                    current_model.min = min.y;
                }
            }else if(model_idx == (p.arch.get(1)-1)){
                if(current_train_data.isEmpty()){
                    current_model.slope =0;
                    current_model.intercept = 1;

                    current_model.max = 1;
                    current_model.min = 0;
                }else{
                    min = training_data[1][model_idx-1].lastElement();
                    max = current_train_data.lastElement();
                    current_model.slope= (min.y-1)/(min.T-max.T);// Hallucinating as if max.y = 1
                    current_model.intercept = min.y - current_model.slope*min.T;

                    current_model.max = max.y;
                    current_model.min = min.y;
                }
            }else{// The current model is not the first or last model in the current layer
                if(current_train_data.isEmpty()){
                    current_model.slope =0;
                    current_model.intercept = training_data[1][model_idx-1].lastElement().y;
                    Training_point tmp_tp = new Training_point();
                    tmp_tp.T = training_data[1][model_idx-1].lastElement().T;
                    tmp_tp.y = training_data[1][model_idx-1].lastElement().y;
                    current_train_data.addElement(tmp_tp);

                    current_model.max = 0;
                    current_model.min = training_data[1][model_idx-1].lastElement().y;
                }else{
                    min = training_data[1][model_idx-1].lastElement();
                    max = current_train_data.lastElement();

                    current_model.slope= (min.y-max.y)/(min.T-max.T);
                    current_model.intercept = min.y - current_model.slope*min.T;

                    current_model.max = max.y;
                    current_model.min = min.y;
                }
            }
            current_model.Case = current_train_data.size();
            model[model_idx+1] = current_model;
        }
        return model;
    }

    public static double Pivot_finding(linear_model[] model, double[] test){
        int pre_model_idx = 0;
        int num_model = model.length-1;//是对的
        double root_slope = model[0].slope;
        double root_intercept = model[0].intercept;
        double pred_cdf = 0;
        Double DisToMedOfres = 1.0;
        double val = Integer.MAX_VALUE;

        for(int cur_key = 0;cur_key<test.length;cur_key += 1){
            double key = test[cur_key];
            pre_model_idx = (int)Math.max(0, Math.min(num_model-1,root_slope*key+root_intercept));
            //if(pre_model_idx<=0 || pre_model_idx>num_model-1 ) System.out.print("wrong"+"\n");
            //System.out.print(pre_model_idx+" "+(num_model-2)+"\n");
            if(model[pre_model_idx] == null) pred_cdf = 1;
            else pred_cdf = model[pre_model_idx].slope*key+model[pre_model_idx].intercept;
            Double DisToMed = Math.abs(pred_cdf-0.5);
            if(DisToMed < DisToMedOfres){
                DisToMedOfres = DisToMed;
                val = key;
            }
        }
        return val;
    }

    public static double divide(double num, double total) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(num));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(total));
        return bigDecimal1.divide(bigDecimal2, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static void swap(HyperPoint[] a, int i, int j) {
        HyperPoint temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void Three_ele_Compare(HyperPoint[] a, int beg, int end, int k,int count) {
        if(a[beg].getcoords()[k] < a[end].getcoords()[k]&& a[beg].getcoords()[k] > a[beg+1].getcoords()[k]){
            return;
        }
        if(a[end].getcoords()[k] < a[beg].getcoords()[k] && a[end].getcoords()[k] > a[beg+1].getcoords()[k]){
            swap(a, end, count);
        }else{
            swap(a, beg+1, count);
        }
    }

    public static TrainedRMI mofm_rmi(HyperPoint[] a, int l, int r, int k) {
        int count=l;
        for (int i = l; i < r; i = i + 9) {
            if (i + 3 < r){
                Three_ele_Compare(a, i, i + 3, k,count);
            }
            count++;
        }
        Params p = new Params();
        int input_size = (count-l);
        int len = (int)(input_size*p.sampling_rate);
        double[] sample = new double[len];
        int step = (int)(1.0/ p.sampling_rate);
        for(int i=0,j=0;i<l+input_size;i+=step,j++){
            if(i > l+input_size) break;
            if(j >= len) break;
            sample[j] = a[l+i].getcoords()[k];
        }
        RMI rmi = new RMI();
        linear_model[] model  = rmi.train(p, sample);
        rmi.setModel(model);
        double pivot = Pivot_finding(model,sample);
        TrainedRMI train_rmi = new TrainedRMI(pivot,rmi);
        return train_rmi;
    }

    public static double Slope(double max, double min,int n) {
        double slope = (n * Math.pow((max - (min + max) / 2), n - 1) / (Math.pow(2, n) * Math.pow(max - min, n)));
        return slope;
    }
    //double cdf = 1 / Math.pow(2, n) - (n * Math.pow((max - (min + max) / 2), n - 1) / (Math.pow(2, n) * Math.pow(max - min, n)) * (target - (min + max) / 2));
    public static BigDecimal Slope(BigDecimal max, BigDecimal min, int n) {
        BigDecimal two = BigDecimal.valueOf(2);
        BigDecimal slope = BigDecimal.valueOf(n)
                .multiply((max.subtract(min.add(max).divide(two)))
                        .pow(n - 1))
                .divide(two.pow(n).multiply((max.subtract(min)).pow(n)), 10, RoundingMode.HALF_UP);
        return slope;
    }
    public static double Intercept(double max, double min,int n) {
        double intercept = 1 / Math.pow(2, n);
        return intercept;
    }

    public static BigDecimal Intercept(BigDecimal max, BigDecimal min, int n) {
        BigDecimal intercept = BigDecimal.ONE.divide(new BigDecimal("2").pow(n));
        return intercept;
    }

}
