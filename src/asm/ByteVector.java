package asm;

/**
 * Created by Syrius on 06/01/2022.
 */

/**
 * A dynamically extensible vector of bytes. This class is roughly equivalent to
 * a DataOutputStream on top of a ByteArrayOutputStream, but is more efficient.
 */
public class ByteVector {
    /**
     * The content of this vector.
     */
    public byte[] data;

    /**
     * Actual number of bytes in this vector.
     */
    public int length;

    /**
     * Constructs a new {@link ByteVector ByteVector} with a default initial size.
     */
    public ByteVector() {
        data = new byte[64];
    }

    /**
     * Constructs a new {@link ByteVector ByteVector} with the given initial size.
     *
     * @param initialSize the initial size of the byte vector to be constructed.
     */
    public ByteVector(final int initialSize) {
        data = new byte[initialSize];
    }

    /**
     * Puts a byte into this byte vector. The byte vector is automatically enlarged if necessary.
     *
     * @param b a byte.
     * @return this byte vector.
     */
    public ByteVector putByte(final int b) {
        int length = this.length;
        if (length + 1 > data.length) {
            enlarge(1);
        }
        data[length++] = (byte) b;
        this.length = length;
        return this;
    }
}
