package RMI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialGridIndex {
    private int[] shape;        // shape of the grid
    private int[] gridSize;    // size of the grid cells
    private Map<String, List<int[]>> grid;   // grid to store the data

    public SpatialGridIndex(int[] shape, int[] gridSize) {
        this.shape = shape;
        this.gridSize = gridSize;
        this.grid = new HashMap<>();
    }

    public void insert(int[] point) {
        String gridKey = getGridKey(point);
        if (!grid.containsKey(gridKey)) {
            grid.put(gridKey, new ArrayList<int[]>());
        }
        grid.get(gridKey).add(point);
    }

    public List<int[]> kNNQuery(int[] queryPoint, int k) {
        List<int[]> kNNItems = new ArrayList<>();
        List<int[]> queryCellNeighbors = getCellNeighbors(queryPoint);
        while (kNNItems.size() < k) {
            int minDist = Integer.MAX_VALUE;
            int[] closestPoint = null;
            for (int[] neighbor : queryCellNeighbors) {
                String gridKey = getGridKey(neighbor);
                if (grid.containsKey(gridKey)) {
                    List<int[]> points = grid.get(gridKey);
                    for (int[] point : points) {
                        int dist = distance(queryPoint, point);
                        if (dist < minDist && !kNNItems.contains(point)) {
                            minDist = dist;
                            closestPoint = point;
                        }
                    }
                }
            }
            if (closestPoint != null) {
                kNNItems.add(closestPoint);
                queryCellNeighbors.addAll(getCellNeighbors(closestPoint));
            } else {
                break;
            }
        }
        return kNNItems;
    }

    public List<int[]> rangeQuery(int[] queryPoint, int[] range) {
        List<int[]> rangeItems = new ArrayList<>();
        List<int[]> queryCellNeighbors = getCellNeighbors(queryPoint);
        for (int[] neighbor : queryCellNeighbors) {
            String gridKey = getGridKey(neighbor);
            if (grid.containsKey(gridKey)) {
                List<int[]> points = grid.get(gridKey);
                for (int[] point : points) {
                    if (isInsideRange(point, queryPoint, range) && !rangeItems.contains(point)) {
                        rangeItems.add(point);
                    }
                }
            }
        }
        return rangeItems;
    }

    private String getGridKey(int[] point) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < point.length; i++) {
            sb.append(point[i] / gridSize[i]);
            if (i < point.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private List<int[]> getCellNeighbors(int[] point) {
        List<int[]> neighbors = new ArrayList<>();
        int[] lowerBounds = new int[point.length];
        int[] upperBounds = new int[point.length];
        for (int i = 0; i < point.length; i++) {
            int gridSizeInDimension = shape[i] / gridSize[i];
            lowerBounds[i] = Math.max(0, point[i] / gridSize[i] - 1);
            upperBounds[i] = Math.min(gridSizeInDimension - 1, point[i] / gridSize[i] + 1);
        }
        for (int[] indices : getGridIndices(lowerBounds, upperBounds)) {
            int[] neighbor = new int[point.length];
            for (int i = 0; i < point.length; i++) {
                for (int j = lowerBounds[i]; j <= upperBounds[i]; j++) {
                    neighbor[i] = j;
                    int[] copy = neighbor.clone();
                    neighbors.add(copy);
                }
            }
        }
        return neighbors;
    }

    private List<int[]> getGridIndices(int[] lowerBounds, int[] upperBounds) {
        List<int[]> gridIndices = new ArrayList<>();
        int[] current = lowerBounds.clone();
        while (true) {
            int[] copy = current.clone();
            gridIndices.add(copy);
            boolean carry = true;
            for (int i = current.length - 1; i >= 0 && carry; i--) {
                current[i]++;
                if (current[i] > upperBounds[i]) {
                    current[i] = lowerBounds[i];
                } else {
                    carry = false;
                }
            }
            if (carry) {
                break;
            }
        }
        return gridIndices;
    }

    private int distance(int[] point1, int[] point2) {
        int sum = 0;
        for (int i = 0; i < point1.length; i++) {
            int diff = point1[i] - point2[i];
            sum += diff * diff;
        }
        return sum;
    }

    private boolean isInsideRange(int[] point, int[] queryPoint, int[] range) {
        for (int i = 0; i < point.length; i++) {
            if (Math.abs(point[i] - queryPoint[i]) > range[i]) {
                return false;
            }
        }
        return true;
    }

    // Test code
    public static void main(String[] args) {
        int[] shape = {100, 100};        // shape of the grid
        int[] gridSize = {10, 10};      // size of the grid cells
        SpatialGridIndex gridIndex = new SpatialGridIndex(shape, gridSize);

        // Insert data points
        int[] point1 = {25, 35};
        int[] point2 = {75, 65};
        int[] point3 = {45, 75};
        int[] point4 = {85, 15};
        gridIndex.insert(point1);
        gridIndex.insert(point2);
        gridIndex.insert(point3);
        gridIndex.insert(point4);

        // Perform kNN query
        int[] queryPoint = {50, 60};
        int k = 1;
        List<int[]> kNNItems = gridIndex.kNNQuery(queryPoint, k);
        System.out.println("kNN Query Results:"+kNNItems.size());
        for (int[] item : kNNItems) {
            System.out.println("Point: " + item[0] + ", " + item[1]);
        }

        // Perform range query
        int[] queryRange = {20, 20};
        List<int[]> rangeItems = gridIndex.rangeQuery(queryPoint, queryRange);
        System.out.println("Range Query Results:");
        for (int[] item : rangeItems) {
            System.out.println("Point: " + item[0] + ", " + item[1]);
        }
    }
}