package Competitor;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.math.BigInteger;
import java.util.Arrays;

public final class HibertCurve {
    private final int bits;
    private final int dimensions;
    private final int length;

    private HibertCurve(int bits, int dimensions) {
        this.bits = bits;
        this.dimensions = dimensions;
        this.length = bits * dimensions;
    }

    public static Builder bits(int bits) {
        return new Builder(bits);
    }

    public BigInteger index(long... point) {
        return this.toIndex(transposedIndex(this.bits, point));
    }

    public long[] point(BigInteger index) {
        return transposedIndexToPoint(this.bits, this.transpose(index));
    }

    public void point(BigInteger index, long[] x) {
        this.transpose(index, x);
        transposedIndexToPoint(this.bits, x);
    }

    public void point(long i, long[] x) {
        this.point(BigInteger.valueOf(i), x);
    }

    public long[] point(long index) {
        return this.point(BigInteger.valueOf(index));
    }

    long[] transpose(BigInteger index) {
        long[] x = new long[this.dimensions];
        this.transpose(index, x);
        return x;
    }
    private void transpose(BigInteger index, long[] x) {
        byte[] b = index.toByteArray();

        for(int idx = 0; idx < 8 * b.length; ++idx) {
            if (((long)b[b.length - 1 - idx / 8] & 1L << idx % 8) != 0L) {
                int dim = (this.length - idx - 1) % this.dimensions;
                int shift = idx / this.dimensions % this.bits;
                x[dim] |= 1L << shift;
            }
        }

    }
    static long[] transposedIndex(int bits, long... point) {
        long M = 1L << bits - 1;
        int n = point.length;
        long[] x = Arrays.copyOf(point, n);

        long q;
        long t;
        int i;
        for(q = M; q > 1L; q >>= 1) {
            long p = q - 1L;

            for(i = 0; i < n; ++i) {
                if ((x[i] & q) != 0L) {
                    x[0] ^= p;
                } else {
                    t = (x[0] ^ x[i]) & p;
                    x[0] ^= t;
                    x[i] ^= t;
                }
            }
        }

        for(i = 1; i < n; ++i) {
            x[i] ^= x[i - 1];
        }

        t = 0L;

        for(q = M; q > 1L; q >>= 1) {
            if ((x[n - 1] & q) != 0L) {
                t ^= q - 1L;
            }
        }

        for(i = 0; i < n; ++i) {
            x[i] ^= t;
        }

        return x;
    }

    static long[] transposedIndexToPoint(int bits, long... x) {
        long N = 2L << bits - 1;
        int n = x.length;
        long t = x[n - 1] >> 1;

        int i;
        for(i = n - 1; i > 0; --i) {
            x[i] ^= x[i - 1];
        }

        x[0] ^= t;

        for(long q = 2L; q != N; q <<= 1) {
            long p = q - 1L;

            for(i = n - 1; i >= 0; --i) {
                if ((x[i] & q) != 0L) {
                    x[0] ^= p;
                } else {
                    t = (x[0] ^ x[i]) & p;
                    x[0] ^= t;
                    x[i] ^= t;
                }
            }
        }

        return x;
    }

    BigInteger toIndex(long... transposedIndex) {
        byte[] b = new byte[this.length];
        int bIndex = this.length - 1;
        long mask = 1L << this.bits - 1;

        for(int i = 0; i < this.bits; ++i) {
            for(int j = 0; j < transposedIndex.length; ++j) {
                if ((transposedIndex[j] & mask) != 0L) {
                    int var10001 = this.length - 1 - bIndex / 8;
                    b[var10001] = (byte)(b[var10001] | 1 << bIndex % 8);
                }

                --bIndex;
            }

            mask >>= 1;
        }

        return new BigInteger(1, b);
    }

    public static final class Builder {
        final int bits;

        private Builder(int bits) {
            this.bits = bits;
        }

        public HibertCurve dimensions(int dimensions) {
            return new HibertCurve(this.bits, dimensions);
        }
    }
}
