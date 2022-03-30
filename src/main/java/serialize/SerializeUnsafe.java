package serialize;

import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;


/**
 *
 * This abstract class for prepare @unsafe object
 * and declare need methods
 *
 */
abstract class SerializeUnsafe {
    protected static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    abstract byte[] serialize(Object obj) throws IOException;

    abstract Object deserialize(byte[] stream) throws InstantiationException, IOException;
}
