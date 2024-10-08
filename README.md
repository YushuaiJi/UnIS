 ![24749ca9797ed91113b69a4e12e7acd](https://github.com/YushuaiJi/UnIS/assets/52951960/93822ffc-6f7e-4423-950c-2c8eb3853356)


### Towards Fast Exact Search over Multi-source Vectors in Autonomous Vehicles.
## Introduction

This repo holds the source code and scripts for reproducing the key experiments of our paper: Towards Fast Exact Search over Multi-source Vectors in Autonomous Vehicles.

## Datasets

All datasets can be acquired via the web we list.

Argoverse     https://www.argoverse.org/index.html

Apolloscope   https://apolloscape.auto/

We separately obtained the POI data, map data, point cloud data, and trajectory embeddings from these two datasets. Notably, the trajectory embeddings were obtained by embedding the varying-length trajectories into 256-dimensional vectors using an embedding model.

## Competitors

#### [Balanced KD-tree](https://arxiv.org/pdf/1410.5420)

- Balanced KD-tree iteratively divides the space using splitting hyperplanes determined by the median along each dimension.
- We build the tree by pre-sorting the dataset in each dimension.

#### [Batch-Dynamic KD-tree](https://arxiv.org/pdf/2112.06188)

- Batch-Dynamic KD-tree is constructed by partitioning the dataset into subsets and then building a balanced multi-way KD-tree for each subset.
- The partition number of a balanced multi-way KD-tree is determined based on the AEPL-optimal criterion. Both \( k \)NN and radius searches traverse all trees, and the results are subsequently consolidated.

#### [iKD-tree](https://arxiv.org/abs/2102.10808)

- iKD-tree improves query efficiency via parallel queries and result merging and ensures insertion efficiency by rebalancing sub-trees using the scapegoat strategy.

#### [``AI+R''-tree](https://ieeexplore.ieee.org/abstract/document/9861112)

- ``AI+R''-tree uses the ML model to improve the R-tree's query efficiency, which identifies the space that needs to be traversal.
- To meet the exact search requirement, we use the random forest \cite{SuZ06} to select spaces that may contain target vectors. We first search the spaces likely to contain the target vectors, followed by searching the remaining spaces.
    

## Usage

If you run in IntelliJ, just go to "src/test/java" and click on the test you want. I have listed all the experiments.

If you want use cmd to run the test, you should navigate to the root directory of the Maven project, which is the directory containing the pom.xml file. 

Next, use the command "mvn -Dtest=YourTestClass#yourTestMethod test" to test the desired function.

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

#### Radius Search

`Radius_Search` in `src/java/Index/UnIS` allows you to query by simply inputting the point and the search radius $r$. We provide four different query methods, and each pruning method is shown as the following table (see Section 2 in our paper).
| __Traversal Method__ | __Bounding Technology__ | __Pruning Technology__ |
|-------------|------------|------------|
|        MBR       |        DFS          |       Lemma 2      | 
|        MBR       |        BFS          |       Lemma 1      | 
|        MBB       |        DFS          |       Lemma 2      | 
|        MBB       |        BFS          |       Lemma 1      | 

## Insertion

You can use `insert` to insert vectors, where the input should be in the form of a linked list of vectors, represented as `List<HyperPoint> p`.

## Auto-selection Model

You should go to the `src/test/java/Auto_Selection/LightweightAutokNN.java` first.

1 You can run the following `kNN_Ground_Truth_Generation`  or `Range_query_ground_truth` to generate the query ground truth.

2 You should use the `Predition.py` to train the prediction model.

Then you should go to `src/test/java/Auto_Selection/Verification.java`

3 You can choose what you want to verification and choose one you like.

## Terminal

After you run the command, you will generate the CSV files. It will show our experiment results.







