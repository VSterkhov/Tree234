package serialize;


import pojo.Entity;

import java.io.*;

/**
 *
 * It is class initialise
 *
 * @unsafe - Unsafe object realised in SerializeUnsafe class
 * @featureOffset - Id Declared Field of Entity class - @feature
 * @messageOffset - Id Declared Field of Entity class - @message
 * @aClass - Reflection name of class Entity
 */
public class SerializeEntity extends SerializeUnsafe {

    private long featureOffset;
    private long messageOffset;
    private Class aClass;



    public SerializeEntity() {
        try {
            aClass = Entity.class;

            featureOffset = unsafe.objectFieldOffset(Entity.class.getDeclaredField("feature"));
            messageOffset = unsafe.objectFieldOffset(Entity.class.getDeclaredField("message"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Invalid schema");
        }
    }

    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bos);

        byte[] featureByteArray = (byte[]) unsafe.getObject(obj, featureOffset);
        os.writeInt(featureByteArray.length);
        os.write(featureByteArray);

        byte[] messageByteArray = (byte[]) unsafe.getObject(obj, messageOffset);
        os.writeInt(messageByteArray.length);
        os.write(messageByteArray);

        return bos.toByteArray();
    }

    public Object deserialize(byte[] stream) throws InstantiationException, IOException {
        Object obj = unsafe.allocateInstance(aClass);

        DataInputStream is = new DataInputStream(new ByteArrayInputStream(stream));

        int sizeFeatureByteArray = is.readInt();
        byte[] featureByteArray = new byte[sizeFeatureByteArray];
        is.read(featureByteArray, 0, sizeFeatureByteArray);
        unsafe.putObject(obj, featureOffset, featureByteArray);

        int sizeMessageArray = is.readInt();
        byte[] messageArray = new byte[sizeMessageArray];
        is.read(messageArray, 0, sizeMessageArray);
        unsafe.putObject(obj, messageOffset, messageArray);

        return obj;
    }

}

