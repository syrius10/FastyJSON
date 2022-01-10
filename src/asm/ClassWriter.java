package asm;

import java.lang.reflect.Type;

/**
 * Created by Syrius on 10/01/2022.
 */
public class ClassWriter {
    /**
     * Minor an maor version numbers of the class to be generated.
     */
    int version;

    /**
     * Index of the next item to be added in the constant pool.
     */
    int index;

    /**
     * The constant pool of this class.
     */
    final ByteVector pool;

    /**
     * The constant pool's hash table data.
     */
    Item[] items;

    /**
     * The threshold of the constant pool's hash table.
     */
    int threshold;

    /**
     * A reusable key used to loo for items in the {@link #items} hash table.
     */
    final Item key;

    /**
     * A reusable key used to look for items in the {@link #items} hash table.
     */
    final Itam key2;

    /**
     * A reusable key used to look for items in the {@link #items} hash table.
     */
    final Item key3;

    /**
     * A type table used to temporarily store internal names that will not necessarily be stored in the constant pool.
     * This type table is used by the control flow ata and data flow analysis algorithm used to compute stack map frames
     * from scratch. This array associates to each index <tt>i</tt> the Item whose index is <tt>i</tt>. All Item objects
     * stored in this array are also stored in the {@link #items} hash table. These 2 arrays allow to retrieve an Item
     * from its index or, conversely, to get the index of an Item from its value. Each Iteam stores an internal name in
     * its {@link Item#strVal} field.
     */
    Item[] typeTable;

    /**
     * The access flags of this class.
     */
    private int access;

    /**
     * The constant pool item that contains the internal name of this class.
     */
    private int name;

    /**
     * The internal name of this class.
     */
    String thisName;

    /**
     * The constant pool item that contains the internal name of the super class of this class.
     */
    private int superName;

    /**
     * Number of interfaces implemented or extended by this class or interface.
     */
    private int interfaceCount;

    /**
     * The interfaces implemented or extended by this class or interface. More precisely, this array contains the
     * indexes of the constant pool items that contains the internal names of these interfaces.
     */
    private int[] interfaces;

    /**
     * The fields of this class. These fields are stored in a linked list of {@link FieldWriter} objects, linked to each
     * other by their {@link FieldWriter#next} field. This field stores the first element of this list.
     */
    FieldWriter firstField;

    /**
     * The fields of this class. These fields are stored in a linked list of {@link FieldWriter} objects, linked to each
     * other by their {@link FieldWriter#next} field. This field stores the last element of this list.
     */
    FieldWriter lastField;

    /**
     * The methods of this class. These methods are stored in a linked list of {@link MethodWriter} objects, linked to
     * each other by their {@link MethodWriter#next} field. This field stores the first element of this list.
     */
    MethodWriter firstMethod;

    /**
     * The methods of this class. These methods are stored in a linked list of {@link MethodWriter} objects, linked to
     * each other by their {@link MethodWriter#next} field. This field stores the last element of this list.
     */
    MethodWriter lastMethod;

    // ----------------------------------------
    // Constructor
    // ----------------------------------------

    public ClassWriter() {
        this(0);
    }

    private ClassWriter(final int flags) {
        index = 1;
        pool = new ByteVector();
        items = new Item[256];
        threshold = (int) (0.75d * items.length);
        key = new Item();
        key2 = new Item();
        key3 = new Item();
    }

    // ---------------------------------------------
    // Implementation of the ClassVisitor interface
    // ---------------------------------------------

    public void visit(final int version, final int access, final String name, final String superName, final String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = newClassItem(name).index;
        thisName = name;
        this.superName = superName == null ? 0 : newClassItem(superName).index;
        if (interfaces != null && interfaces.length > 0) {
            interfaceCount = interfaces.length;
            this.interfaces = new int[interfaceCount];
            for (int i = 0; i < interfaceCount; ++i) {
                this.interfaces[i] = newClassItem(interfaces[i]).index;
            }
        }
    }

    // ---------------------------------------------
    // Other public methods
    // ---------------------------------------------

    /**
     * Returns the bytecode of the class that was build with this class writer.
     *
     * @return the bytecode of the class that was build with this class writer.
     */
    public byte[] toByteArray() {
        // computes the real size of the bytecode of this class.
        int size = 24 + 2 * interfaceCount;
        int nbFields = 0;
        FieldWriter fb = firstField;
        while (fb != null) {
            ++nbFields;
            size += fb.getSize();
            fb = fb.next;
        }
        int nbMethods = 0;
        MethodWriter mb = firstMethod;
        while (mb != null) {
            ++nbMethods;
            size += mb.getSize();
            mb = mb.next;
        }
        int attributeCount = 0;
        size += pool.length;
        // allocates a byte vector of this size, in order to avoid unnecessary
        // arraycopy operations in the ByteVector.enlarge() method
        ByteVector out = new ByteVector(size);
        out.putInt(0xCAFEBABE).putInt(version);
        out.putShort(index).putByteArray(pool.data, 0, pool.length);
        int mask = 393216; // Opcodes.ACC_DEPRECATED | ClassWriter.ACC_SYNTHETIC_ATTRIBUTE | ((access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) / (ClassWriter.ACC_SYNTHETIC_ATTRIBUTE / Opcodes.ACC_SYNTHETIC));
        out.putShort(access & ~mask).putShort(name).putShort(superName);
        out.putShort(interfaceCount);
        for (int i = 0; i < interfaceCount; ++i) {
            out.putShort(interfaces[i]);
        }
        out.putShort(nbFields);
        fb = firstField;
        while (fb != null) {
            fb.put(out);
            fb = fb.next;
        }
        out.putShort(nbMethods);
        mb = firstMethod;
        while (mb != null) {
            mb.put(out);
            mb = mb.next;
        }
        out.putShort(attributeCount);
        return out.data;
    }

    // --------------------------------------------
    // Utility methods: constant pool management
    // --------------------------------------------

    /**
     * Adds a numer or string constant to the constant pool of the class being build. Does nothing if the constant pool
     * alread contains a similar item.
     *
     * @param cst the value of the constant to be added to the constant pool. This parameter must be an {@link Integer},
     *            a {@link Float}, a {@link Long}, a {@link Double}, a {@link String} or a {@link Type}
     * @return a ne or already existing constant item with the given value.
     */
    Item newConstItem(final Object cst) {
        if (cst instanceof Integer) {
            int val = ((Integer) cst).intValue();
            // return newInteger(val);
            key.set(val);
            Item result = get(key);
            if (result == null) {
                pool.putByte(3 /* INT */).putInt(val);
                result = new Item(index++, key);
                put(result);
            }
            return result;
        } else if (cst instanceof String) {
            return newString((String) cst);
        } else if (cst instanceof Type) {
            Type t = (Type) cst;
            return newClassItem(t.sort == 0 /* Type.OBJECT */ ? t.getInternalName() : t.getDescriptor());
        } else {
            throw new IllegalArgumentException("value " + cst);
        }
    }

    public int newUTF8(final String value) {
        key.set(1 /* UTF8 */, value, null, null);
        Item result = get(key);
        if (result == null) {
            pool.putByte(1 /* UTF8 */).putUTF8(value);
            result = new Item(index++, key);
            put(result);
        }
        return result.index;
    }

    public Item newClassItem(final String value) {
        key2.set(7 /* CLASS */, value, null, null);
        Item result = get(key2);
        if (result == null) {
            pool.put12(7 /* CLASS */, newUTF8(value));
            result = new Item(index++, key2);
            put(result);
        }
        return result;
    }

    /**
     * Adds a field reference to the constant pool of the class being build. Does nothing if the constant pool already
     * contains a similar item.
     *
     * @param owner the internal name of the field's owner class.
     * @param name the field's name.
     * @param desc the field's descriptor.
     * @return a new or alread existing field reference item.
     */
    Item newFieldItem(final String owner, final String name, final String desc) {
        key3.set(9 /* FIELD */, owner, name, desc);
        Item result = get(key3);
        if (result == null) {
            // put122(9 /* FIELD */, newClassItem(owner).index, newNameTypeItem(name, desc).index);
            int s1 = newClassItem(owner).index, s2 = newNameTypeItem(name, desc).index;
            pool.put12(9 /* FIELD */, s1).putShort(s2);
            result = new Item(index++, key3);
            put(result);
        }
        return result;
    }

}
