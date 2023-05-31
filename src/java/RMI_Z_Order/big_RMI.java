package RMI_Z_Order;

import RMI.Params;
import RMI.Training_point;
import RMI.linear_model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Vector;

public class big_RMI {
    big_linear_model[] model;
    public big_RMI() {
    }

    public big_linear_model[] getModel() {
        return model;
    }

    public void setModel(big_linear_model[] model) {
        this.model = model;
    }

    public big_linear_model[] train(big_Params p, BigDecimal[] sample) {
        Vector<big_Training_point>[][] training_data= (Vector<big_Training_point>[][]) new Vector[2][p.arch.get(1)];
        training_data[0][0] = new Vector<>();
        for(int i=0;i<training_data[0].length;i++){
            training_data[1][i] = new Vector<>();
        }
        int beg = 0;
        int end = sample.length-1;
        int INPUT_SIZE = end+1-beg;
        int SAMPLE_SIZE = Math.min(INPUT_SIZE,Math.max(p.threshold,(int)(p.sampling_rate*INPUT_SIZE)));
        BigDecimal[] training_sample = new BigDecimal[SAMPLE_SIZE];

        int block = p.batch_sz;//10
        int step = INPUT_SIZE/block;//10000 / 10 = 1000
        int blocksample = SAMPLE_SIZE/block;// 1000 / 10 = 100
        if(INPUT_SIZE<step) return model;

        int count = 0;
        for(int i=0;i<INPUT_SIZE;i+=step){ // 10 times
            if(INPUT_SIZE-i<step){
                int restSample = SAMPLE_SIZE-count;
                for(int j=0;j<restSample;j++){
                    int rand = (int)(i+Math.random()*(INPUT_SIZE-i));
                    training_sample[count] = sample[rand + beg];
                    count++;
                }
                break;
            }
            for(int j=0;j<blocksample;j++){ // 100
                int rand = (int)(i+Math.random()*(step));
                //System.out.print("i:"+i+"-r"+(rand-i)+ "-rand:"+rand+"-beg:"+beg+"-sum:"+(rand+beg));
                //System.out.print("\n");
                training_sample[count] = sample[rand+beg];
                count++;
            }
        }
        //model constructor
        big_linear_model[] model = new big_linear_model[p.arch.get(0)+p.arch.get(1)];


        Arrays.sort(training_sample);
        //training_sample = insertionSort(training_sample);
        if(training_sample[0]== training_sample[training_sample.length-1]) {
            big_linear_model tmp_model = new big_linear_model();
            tmp_model.slope = null;
            tmp_model.intercept = null;
            model[0] = tmp_model;
            return model;
        }
        // Populate the training data for the root model
        for(int i=0;i<training_sample.length;i++){
            double a = i*1.0/training_sample.length;
            BigDecimal y = new BigDecimal(a);
            big_Training_point tp = new big_Training_point(training_sample[i],y);
            training_data[0][0].add(tp);
        }

        Vector<big_Training_point> current_train_data = training_data[0][0];
        big_linear_model current_model = new big_linear_model();

        // Calculate the slope and intercept terms
        big_Training_point max = current_train_data.lastElement();
        big_Training_point min = current_train_data.firstElement();

        // Calculate intercept
        current_model.slope = BigDecimal.ONE.divide(max.T.subtract(min.T), 10, BigDecimal.ROUND_HALF_UP);
        current_model.intercept = current_model.intercept.multiply(min.T);
        //current_model.slope = 1/(max.T-min.T);
        //current_model.intercept = -current_model.slope*min.T;//slope is larger than 0

        model[0] = current_model;//root model
        model[0].Case = training_data[0][0].size();

        //add rank to the slope and intercept
        //current_model.slope = current_model.slope*(p.arch.get(1)-1);
        //current_model.intercept = current_model.intercept*(p.arch.get(1)-1);
        BigDecimal tmp = new BigDecimal(p.arch.get(1)-1);
        current_model.slope = current_model.slope.multiply(tmp);
        current_model.intercept = current_model.intercept.multiply(tmp);


        //assign each train_data to the palce they stay
        ///for(Training_point pt:current_train_data){
        //    int rank = (int) (pt.T*current_model.slope+current_model.intercept);
        //    rank = Math.max(0,Math.min(p.arch.get(1)-1,rank));
        //    training_data[1][rank].add(pt);
        //}
        for(big_Training_point pt:current_train_data){
            BigDecimal tmp1  = pt.T.multiply(current_model.slope).add(current_model.intercept);
            //int rank = (int) (pt.T*current_model.slope+current_model.intercept);
            //tmp1.intValue()
            int rank = Math.max(0,Math.min(p.arch.get(1)-1,tmp1.intValue()));
            training_data[1][rank].add(pt);
        }

        //leaf model
        for(int model_idx = 0;model_idx<p.arch.get(1);model_idx++){
            current_train_data = training_data[1][model_idx];
            current_model = new big_linear_model();
            // Interpolate the min points in the training buckets
            // The current model is the first model in the current layer
            if(model_idx == 0){
                if(current_train_data.size()<2){
                    current_model.intercept = BigDecimal.ZERO;
                    current_model.slope = BigDecimal.ZERO;
                    big_Training_point tmp_tp = new big_Training_point();
                    tmp_tp.T = BigDecimal.ZERO;
                    tmp_tp.y = BigDecimal.ZERO;
                    current_train_data.addElement(tmp_tp);
                    current_model.max = BigDecimal.ZERO;
                    current_model.min = BigDecimal.ZERO;
                }else{
                    max = current_train_data.lastElement();
                    min = current_train_data.firstElement();

                    //current_model.slope = max.y/(max.T-min.T);
                    //current_model.intercept = min.y - current_model.slope*min.T;

                    current_model.slope = max.y.divide(max.T.subtract(min.T), 10, BigDecimal.ROUND_HALF_UP);
                    current_model.intercept = min.y.subtract(current_model.slope.multiply(min.T));
                    current_model.max = max.y;
                    current_model.min = min.y;
                }
            }else if(model_idx == (p.arch.get(1)-1)){
                if(current_train_data.isEmpty()){
                    current_model.slope =BigDecimal.ZERO;;
                    current_model.intercept = BigDecimal.ONE;
                    current_model.max = BigDecimal.ONE;
                    current_model.min = BigDecimal.ZERO;;
                }else{
                    min = training_data[1][model_idx-1].lastElement();
                    max = current_train_data.lastElement();
                    //current_model.slope= (min.y-1)/(min.T-max.T);// Hallucinating as if max.y = 1
                    current_model.slope = (min.y.subtract(BigDecimal.ONE)).divide(min.T.subtract(max.T));
                    //current_model.intercept = min.y - current_model.slope*min.T;
                    current_model.intercept = min.y.subtract(current_model.slope.multiply(min.T));

                    current_model.max = max.y;
                    current_model.min = min.y;
                }
            }else{// The current model is not the first or last model in the current layer
                if(current_train_data.isEmpty()){
                    current_model.slope =BigDecimal.ZERO;
                    current_model.intercept = training_data[1][model_idx-1].lastElement().y;
                    big_Training_point tmp_tp = new big_Training_point();
                    tmp_tp.T = training_data[1][model_idx-1].lastElement().T;
                    tmp_tp.y = training_data[1][model_idx-1].lastElement().y;
                    current_train_data.addElement(tmp_tp);
                    current_model.max = BigDecimal.ZERO;
                    current_model.min = training_data[1][model_idx-1].lastElement().y;
                }else{
                    min = training_data[1][model_idx-1].lastElement();
                    max = current_train_data.lastElement();

                   // current_model.slope= (min.y-max.y)/(min.T-max.T);
                    //current_model.intercept = min.y - current_model.slope*min.T;

                    current_model.slope = min.y.subtract(max.y).divide(min.T.subtract(max.T));
                    current_model.intercept = min.y.subtract(current_model.slope.multiply(min.T));
                    current_model.max = max.y;
                    current_model.min = min.y;
                }
            }
            current_model.Case = current_train_data.size();
            model[model_idx+1] = current_model;
        }
        return model;
    }

    public static double predicition(big_linear_model[] model, BigDecimal target){
        int num_model = model.length-1;//是对的
        BigDecimal root_slope = model[0].slope;
        BigDecimal root_intercept = model[0].intercept;
        double pred_cdf = 0;

        int tmp = root_slope.multiply(target).add(root_intercept).intValue();
        int pre_model_idx = Math.max(0, Math.min(num_model-1,tmp));
        if(model[pre_model_idx] == null) pred_cdf = 1;
        //else pred_cdf = model[pre_model_idx].slope*key+model[pre_model_idx].intercept;
        else pred_cdf = model[pre_model_idx].slope.multiply(target).add(model[pre_model_idx].intercept).doubleValue();
        return pred_cdf;
    }



}
