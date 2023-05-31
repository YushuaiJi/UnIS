[2afd4675389fcbc7868444c9acec263](https://github.com/YushuaiJi/AutoIS/assets/52951960/fd8b6d8e-5a42-470a-982b-72c824f2da74)

Java code for paper "A Learned Framework for Fast Indexing and Queries over Multi-Dimensional Data".
## Introduction

This repo holds the source code and scripts for reproducing the key experiments of our paper: A Learned Framework for Fast Indexing and Queries over Multi-Dimensional Data.

## Datasets

All dataset are contained in our repo and the path is:AutoIS/src/test/java/Dataset.

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

1 You can run the following `Construction_AutoIS` function to obtain the runtime of index construction.

2 You can run the following `AutoIS_MemoryUsage` function to obtain the memoryusge of index construction. AutoIS_Tree_Depth.

3:You can run the following `AutoIS_Tree_Depth` function to obtain the treedepth of index construction.

If you want use 

## Auto-seleciton algorithm

You should go to the `src/test/java/Auto_Selection/LightweightAutokNN.java` first.

1 You can run the following `kNN_Ground_Truth_Generation`  or `Range_query_ground_truth` generate the query ground truth.

2 You should use the `Predition.py` to train the prediction model.

Then you should go to `src/test/java/Auto_Selection/Verification.java`

3 You can choose what you want to verificate and choose one you like.

## Terminal

After you run the command, you will generate the csv files. It will show our experiment results.







