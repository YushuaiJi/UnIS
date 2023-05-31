package Competitor;

import java.math.BigInteger;
import java.util.Arrays;
public class Zorder {

    // Method to encode n-dimensional point into Z-value
    public static long encode(long[] coordinates) {
        int numDimensions = coordinates.length;
        long zValue = 0;
        for (int i = 0; i < 512; i++) { // Assuming each coordinate is 32 bits (int)
            for (int j = 0; j < numDimensions; j++) {
                long bit = (coordinates[j] >> i) & 1; // Extract the ith bit from each coordinate
                zValue |= (bit << (numDimensions * i + j)); // Interleave the bit to the Z-value
            }
        }
        return zValue;
    }
    // Method to decode Z-value into n-dimensional point
    public static long[] zOrderDecode(long zValue, int numDimensions) {
        long[] coordinates = new long[numDimensions];

        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < numDimensions; j++) {
                long tmp = (long)(zValue / (Math.pow(2,numDimensions * i + j)));
                //double tmp = zValue / (1 << (numDimensions * i + j));
                //int bit = (zValue >> (numDimensions * i + j)) & 1;
                long bit = tmp & 1;
                coordinates[j] |= (bit << i);
                //System.out.print((zValue >>> (numDimensions * i + j))+" "+bit+" "+i+" "+(bit << i)+" "+(coordinates[j] | (bit << i))+"\n");
            }
        }
        return coordinates;
    }
    // Method to encode n-dimensional point into Z-value
    public static BigInteger encode(BigInteger[] coordinates) {
        int numDimensions = coordinates.length;
        BigInteger zValue = BigInteger.ZERO;
        for (int i = 0; i < 1024; i++) { // Assuming each coordinate is 32 bits (int)
            for (int j = 0; j < numDimensions; j++) {
                BigInteger bit = coordinates[j].shiftRight(i).and(BigInteger.ONE); // Extract the ith bit from each coordinate
                zValue = zValue.or(bit.shiftLeft(numDimensions * i + j)); // Interleave the bit to the Z-value
            }
        }
        return zValue;
    }
    // Method to decode Z-value into n-dimensional point
    public static BigInteger[] zOrderDecode(BigInteger zValue, int numDimensions) {
        BigInteger[] coordinates = new BigInteger[numDimensions];
        for (int i = 0; i < numDimensions; i++) {
            coordinates[i] = BigInteger.ZERO;
        }
        for (int i = 0; i < 1024; i++) {
            for (int j = 0; j < numDimensions; j++) {
                BigInteger divisor = BigInteger.valueOf(2).pow(numDimensions * i + j);
                BigInteger tmp = zValue.divide(divisor);
                BigInteger bit = tmp.and(BigInteger.ONE);
                BigInteger iBigInteger = BigInteger.valueOf(i);
                coordinates[j] = coordinates[j].or(bit.shiftLeft(iBigInteger.intValue()));
            }
        }
        return coordinates;
    }

    public static void main(String[] args) {
        int numDimensions = 4;
        long[] coordinates = {99943254,41234213,43243243,432432432};
        BigInteger[] bigIntArray = new BigInteger[coordinates.length];
        for (int i = 0; i < bigIntArray.length; i++) {
            bigIntArray[i] = BigInteger.valueOf(coordinates[i]);
        }
        BigInteger zValue = encode(bigIntArray);
        System.out.print(zValue+"\n");
        BigInteger[] decodedCoordinates = zOrderDecode(zValue, numDimensions);
        System.out.print(decodedCoordinates[0]);
        //long a = (1<<11);
        //System.out.print(a);
        //System.out.println("Decoded coordinates: " + decodedCoordinates[0].toString());
        //long a = 474284623672471571195149084461618750600556960210800563922894978508184030674637997
    }
}

