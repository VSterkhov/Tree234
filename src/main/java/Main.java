

import pojo.Entity;
import serialize.SerializeEntity;
import tree234.CacheTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    private static SerializeEntity serializeUnsafe = new SerializeEntity();

    public static void main(String[] args) throws InstantiationException {
        try {
            CacheTree theTree = new CacheTree();


            System.out.println("Please configure cache properties. An Object size range from 18 bytes");
            System.out.println("Set InMemory Cache size (a Value must be Integer):");
            theTree.setInMemoryCacheSize(Integer.parseInt(consoleReader.readLine()));

            System.out.println("Set InFileSystem Cache size (a Value must be Integer):");
            theTree.setInFileSystemCacheSize(Integer.parseInt(consoleReader.readLine()));


            System.out.println("Object is two saved in bytes strings.");
            System.out.println("Please enter count objects for save in cache:");
            int count = Integer.parseInt(consoleReader.readLine());
            for (int i = 1; i<=count; i++) {
                Object obj = new Entity(("test"+i).getBytes(), ("test"+i).getBytes());
                byte[] stream = serializeUnsafe.serialize(obj);

                int sumBytes = 0;
                for (byte b : stream) {
                    sumBytes += b;
                }
                theTree.insert(sumBytes, stream);
            }


            System.out.println("");
            System.out.println("-- All caches --");
            theTree.showCache();


            System.out.println("");
            System.out.println("-- Show memory tree234 --");
            theTree.displayTree();

            System.out.println("");
            System.out.println("What object must be find in cache (Enter number 1-"+count+")");
            int number = Integer.parseInt(consoleReader.readLine());

            Object obj = new Entity(("test"+number).getBytes(), ("test"+number).getBytes());
            byte[] stream = serializeUnsafe.serialize(obj);

            int sumBytes = 0;
            for (byte b : stream) {
                sumBytes += b;
            }

            System.out.println(serializeUnsafe.deserialize(theTree.find(sumBytes,stream)));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
