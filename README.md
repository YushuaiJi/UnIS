## ![24749ca9797ed91113b69a4e12e7acd](https://github.com/YushuaiJi/UnIS/assets/52951960/93822ffc-6f7e-4423-950c-2c8eb3853356)


### A Learned Framework for Fast Indexing and Queries over Multi-Dimensional Data.
## Introduction

This repo holds the source code and scripts for reproducing the key experiments of our paper: A Learned Framework for Fast Indexing and Queries over Multi-Dimensional Data.

## Datasets

All datasets can be acquired via the web we list.

T-drive        https://dl.acm.org/doi/10.1145/1869790.1869807

Proto           https://figshare.com/articles/dataset/Porto_taxi_trajectories/12302165

3D-RD         https://networkrepository.com/3D-spatial-network.php

ShapeNet     https://shapenet.org/

Conflong       https://networkrepository.com/ConfLongDemo-JSI.php

NYC    http://www.nyc.gov/html/tlc/html/technology/data.shtml

HT Sensor (HS)       https://archive.ics.uci.edu/ml/datasets/Gas+sensors+for+home+activity+monitoring

KEGG        https://archive.ics.uci.edu/ml/datasets/Individual+household+electric+power+consumption

## Usage

If you run in IntelliJ, just go to "src/test/java" and click on the test you want. I have listed all the experiments.

If you want use cmd to run the test, you should navigate to the root directory of the Maven project, which is the directory containing the pom.xml file. 

Next, use the command "mvn -Dtest=YourTestClass#yourTestMethod test" to test the desired function.

## Fast Index Construction

1 You can run the code in `Construction_AutoIS` to obtain the runtime of index construction.

2 You can run the code in `AutoIS_MemoryUsage` to obtain the memory usage of index construction. AutoIS_Tree_Depth.

3:You can run the code in `AutoIS_Tree_Depth` to obtain the tree depth of index construction.

## Auto-selection Algorithm

You should go to the `src/test/java/Auto_Selection/LightweightAutokNN.java` first.

1 You can run the following `kNN_Ground_Truth_Generation`  or `Range_query_ground_truth` to generate the query ground truth.

2 You should use the `Predition.py` to train the prediction model.

Then you should go to `src/test/java/Auto_Selection/Verification.java`

3 You can choose what you want to verification and choose one you like.

## Terminal

After you run the command, you will generate the CSV files. It will show our experiment results.







