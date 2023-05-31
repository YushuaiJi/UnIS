package Competitor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BinarySearch {

    public static List<BigInteger> findKNearestNeighbors(BigInteger[] arr, BigInteger target, int k,long[] count) {
        List<BigInteger> result = new ArrayList<>();
        // Binary search to find the index of target element
        int targetIndex = findClosestElement(arr, target,count);

        // Initialize two pointers for finding k nearest neighbors
        int i = targetIndex - 1;
        int j = targetIndex + 1;

        // Add target element to the result lis

        result.add(arr[targetIndex]);

        // Find k nearest neighbors
        while (result.size() < k) {
            count[0] = count[0]+14;
            if (i >= 0 && j < arr.length) {
                BigInteger diff1 = arr[i].subtract(target).abs();
                BigInteger diff2 = arr[j].subtract(target).abs();
                if (diff1.compareTo(diff2) <= 0) {
                    result.add(arr[i]);
                    i--;
                } else {
                    result.add(arr[j]);
                    j++;
                }
            } else if (i >= 0) {
                result.add(arr[i]);
                i--;
            } else if (j < arr.length) {
                result.add(arr[j]);
                j++;
            }
        }

        return result;
    }

    public static List<BigInteger> learned_findKNearestNeighbors(BigInteger[] arr, BigInteger target, int k,double cdf,long[] count) {
        List<BigInteger> result = new ArrayList<>();
        // Binary search to find the index of target element
        int targetIndex = learned_findClosestElement(arr,target,cdf,count);

        // Initialize two pointers for finding k nearest neighbors
        int i = targetIndex - 1;
        int j = targetIndex + 1;

        // Add target element to the result lis

        result.add(arr[targetIndex]);

        // Find k nearest neighbors
        while (result.size() < k) {
            count[0] = count[0]+14;
            if (i >= 0 && j < arr.length) {
                BigInteger diff1 = arr[i].subtract(target).abs();
                BigInteger diff2 = arr[j].subtract(target).abs();
                if (diff1.compareTo(diff2) <= 0) {
                    result.add(arr[i]);
                    i--;
                } else {
                    result.add(arr[j]);
                    j++;
                }
            } else if (i >= 0) {
                result.add(arr[i]);
                i--;
            } else if (j < arr.length) {
                result.add(arr[j]);
                j++;
            }
        }

        return result;
    }
    public static int findClosestElement(BigInteger[] arr, BigInteger target,long[] count) {
        int left = 0;
        int right = arr.length - 1;
        int location = 0;
        BigInteger closest = null;

        while (left <= right) {
            count[0] = count[0]+10;
            int mid = left + (right - left) / 2;
            BigInteger midVal = arr[mid];

            // Update closest element if current element is closer to target
            if (closest == null || midVal.subtract(target).abs().compareTo(closest.subtract(target).abs()) < 0) {
                location = mid;
                closest = midVal;
            }

            // If target is found, return it
            if (midVal.compareTo(target) == 0) {
                return location;
            }

            // If target is less than midVal, search in left half
            if (target.compareTo(midVal) < 0) {
                right = mid - 1;
            }
            // If target is greater than midVal, search in right half
            else {
                left = mid + 1;
            }
        }
        return location;
    }

    public static int learned_findClosestElement(BigInteger[] arr, BigInteger target,double cdf,long[] count) {
        double left_cdf = Math.max(0,cdf-0.2);
        double right_cdf = Math.max(1,cdf+0.2);
        int left = Math.max((int)(left_cdf*arr.length),0);
        int right = Math.min((int)(right_cdf*arr.length),arr.length-1);
        int location = 0;
        BigInteger closest = null;

        while (left <= right) {
            count[0] = count[0]+10;
            int mid = left + (right - left) / 2;
            BigInteger midVal = arr[mid];

            // Update closest element if current element is closer to target
            if (closest == null || midVal.subtract(target).abs().compareTo(closest.subtract(target).abs()) < 0) {
                location = mid;
                closest = midVal;
            }

            // If target is found, return it
            if (midVal.compareTo(target) == 0) {
                return location;
            }

            // If target is less than midVal, search in left half
            if (target.compareTo(midVal) < 0) {
                right = mid - 1;
            }
            // If target is greater than midVal, search in right half
            else {
                left = mid + 1;
            }
        }
        return location;
    }
}
