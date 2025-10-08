 ![24749ca9797ed91113b69a4e12e7acd](https://github.com/YushuaiJi/UnIS/assets/52951960/93822ffc-6f7e-4423-950c-2c8eb3853356)


### Updatable Balanced Index for Fast On-device Search with Auto-selection Model.
## Introduction

This repo holds the source code and scripts for reproducing the key experiments of our paper: Updatable Balanced Index for Fast On-device Search with Auto-selection Model.

## Datasets

All datasets can be acquired via the web we list.

ArgoPOI    [ https://www.argoverse.org/index.html](https://www.argoverse.org/index.html)

ArgoAVL   [https://apolloscape.auto/](https://www.argoverse.org/index.html)

Porto     [https://www.argoverse.org/index.html](https://figshare.com/articles/dataset/Porto_taxi_trajectories/12302165)

Shapenet   [https://apolloscape.auto/](https://shapenet.org/)

T-drive    [ https://www.argoverse.org/index.html](https://www.microsoft.com/en-us/research/wp-content/uploads/2016/02/T-Drive-20Driving20Directions20Based20on20Taxi20Traces.pdf)

ArgoPC  [ https://apolloscape.auto/](https://www.argoverse.org/index.html)

Apollo     [https://www.argoverse.org/index.html](https://apolloscape.auto/)

ArgoTraj   [https://apolloscape.auto/](https://www.argoverse.org/index.html)

We separately obtained the POI data, map data, point cloud data, and trajectory embeddings from these two datasets. Notably, the trajectory embeddings were obtained by embedding the varying-length trajectories into 256-dimensional vectors using an embedding model.
    

## Usage

- If you run in IntelliJ, just go to "src/test/java" and click on the test you want. I have listed all the experiments.

- If you want use cmd to run the test, you should navigate to the root directory of the Maven project, which is the directory containing the pom.xml file. Next, use the command "mvn -Dtest=YourTestClass#yourTestMethod test" to test the desired function.

- If you run in other platforms (We run the comparative experiment on robot car), just pack the java project and load all the ".jar" files you get into the platform you want. Next, open the terminal and input "java -cp .:yourPackageName-tests.jar:yourPackageName.jar:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore yourTestMethod" to test the desired function

## Fast Index Construction

1 You can run the code in `test_construction_UnIS` to obtain the runtime of index construction.

The index construction has four methods (as shown in `test_construction_UnIS`), which are listed below:
```
`model=1` constructs the index using `insertBySorting`, which presorts the data in each dimension.

`model=2` constructs the index using `insertByQuantileFindingLearnedIndex`, which applies the method proposed in the paper.

`model=3` constructs the index using `insertBySorting_Self_Partition`, which applies presorting without allocating additional arrays for data storage.

`model=4` constructs the index using `insertBySorting_Self_Partition_learned`, which applies the proposed method without allocating additional arrays for data storage.
```

2 You can run the code in `UnIS_MemoryUsage` to obtain the memory usage of index construction. 

3:You can run the code in `UnIS_Tree_Depth` to obtain the tree depth of index construction.

## Queries

We support different types of queries, including $k$NN queries and radius search.

#### kNN queries

`kNN` in `src/java/Index/UnIS` allows you to query by simply inputting the point and the value of $k$. We provide four different query methods, each composed of distinct traversal methods and bounding technologies, and each employing different pruning techniques (see Section 2 in our paper).
| __Traversal Method__ | __Bounding Technology__ | __Pruning Technology__ |
|-------------|------------|------------|
|        MBR       |        DFS          |       Lemma 1      | 
|        MBR       |        BFS          |       Lemma 2      | 
|        MBB       |        DFS          |       Lemma 1      | 
|        MBB       |        BFS          |       Lemma 3      | 

```
`model=1` performs DFS based on the MBR-based traversal traversal using `get_all_nearest_points`.

`model=2` performs DFS with MBB-based pruning, implemented in `get_all_nearest_points_ball`.

`model=3` performs BFS using MBR-based traversal, implemented in `get_all_nearest_points_BFS_MBR`.

`model=4` performs BFS using MBB-based traversal, implemented in `get_all_nearest_points_BFS_MBB`.
```

#### Radius Search

`Radius_Search` in `src/java/Index/UnIS` allows you to query by simply inputting the point and the search radius $r$. We provide four different query methods, and each pruning method is shown as the following table (see Section 2 in our paper).
| __Traversal Method__ | __Bounding Technology__ | __Pruning Technology__ |
|-------------|------------|------------|
|        MBR       |        DFS          |       Lemma 2      | 
|        MBR       |        BFS          |       Lemma 1      | 
|        MBB       |        DFS          |       Lemma 2      | 
|        MBB       |        BFS          |       Lemma 1      | 

It is worth noting that, as discussed in the paper, our MBB-based approach outperforms the MBR-based one. For simplicity and clarity of implementation, we provide the search strategies based on MBB in the released code.
```
`model=1` performs radius search using the DFS strategy, implemented in `Radius_Search_DFS_MBB`. 
This method explores the index tree recursively and visits nodes in a depth-first manner based on their bounding volumn.

`model=2` performs radius search using the BFS strategy, implemented in `Radius_Search_BFS_MBB`. 
This method explores the index tree level by level, maintaining a queue to iteratively expand nodes whose the bounding volumn intersect with the search radius.
```

## Insertion

You can use `insert` to insert vectors, where the input should be in the form of a linked list of vectors, represented as `List<HyperPoint> p`.

## Auto-selection Model

You should go to the `src/test/java/Auto_Selection/LightweightAutokNN.java` first.

1 You can run the following `kNN_Ground_Truth_Generation`  or `Range_query_ground_truth` to generate the query ground truth.

2 You should use the `Predition.ipynb` to train the prediction model.

Then you should go to `src/test/java/Auto_Selection/Verification.java`

3 You can choose what you want to verification and choose one you like.

## Terminal

After you run the command, you will generate the CSV files. It will show our experiment results.







