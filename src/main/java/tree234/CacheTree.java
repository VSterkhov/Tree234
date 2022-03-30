package tree234;



import serialize.SerializeEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

////////////////////////////////////////////////////////////////
    public class CacheTree
    {

        private int inMemoryCacheSize[] = {512,0};
        private int inFileSystemCacheSize[] = {2048,0};

        public Node getRoot() {
            return root;
        }

        public void showCache() {
            System.out.println("InMemoryCache");
            printMemoryCache(getRoot());
            System.out.println("FileSystemCache");
            printFileSystemCache();
        }

        private void printFileSystemCache() {
            try (BufferedReader br = new BufferedReader(new FileReader("./cache.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void printMemoryCache(Node root) {
            for (int i = 0; i < 3; i++)
                if (root.getItem(i) != null) {

                    String bytesEntity = root.getItem(i).sumBytes + "x";

                    byte[] bytes = root.getItem(i).bytes;
                    for (int y = 0; y < bytes.length; y++)
                        bytesEntity += y == 0 ? bytes[y] : " " + bytes[y];
                    System.out.println(bytesEntity);
                }

            for (int i = 0; i < 4; i++)
                if (root.getChild(i) != null)
                    printMemoryCache(root.getChild(i));
        }

        private Node root = new Node(); // Создание корневого узла
        // -------------------------------------------------------------
        public byte[] find(long sumBytes, byte[] bytes) throws IOException {
            Node curNode = root;
            while(true)
            {
                if((curNode.findItem(sumBytes, bytes) ) != null){
                    return bytes; // Cache found in memory
                }

                //In memory cache not found, next finding in file system
                else if( curNode.isLeaf() ){

                    //It is first operation before insert, so creating file here.
                    if (!new File("./cache.txt").exists()){
                        new File("./cache.txt").createNewFile();
                    }

                    /*Read cache from file*/
                    try (BufferedReader br = new BufferedReader(new FileReader("./cache.txt"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] splitLine = line.split("x");
                            if (Long.parseLong(splitLine[0])==sumBytes) {
                                String[] splitBytes = splitLine[1].split(" ");
                                boolean equals = true;
                                for (int i = 0; i < splitBytes.length; i++)
                                    try {
                                        if (Byte.parseByte(splitBytes[i]) != bytes[i]) {
                                            equals = false;
                                            continue;
                                        }
                                    } catch (NullPointerException e) {
                                    return null;
                                    }

                                    // Cache found in file system.
                                    if (equals)
                                        return bytes;
                            }
                        }

                    }
                    return null; // Cache not found
                }

                else
                    curNode = getNextChild(curNode, sumBytes, bytes);
            }
        }


        private static SerializeEntity serializeUnsafe = new SerializeEntity();
        // -------------------------------------------------------------
        // Вставка элемента данных
        public void insert(long sumBytes, byte[] bytes) throws IOException {
            Node curNode = root;
            DataItem tempItem = new DataItem(sumBytes, bytes);
            byte[] find = find(sumBytes, bytes);
            if (find == null) {
                if (inMemoryCacheSize[0] > inMemoryCacheSize[1] + bytes.length) {


                    while (true) {
                        if (curNode.isFull()) // Если узел полон,
                        {
                            split(curNode); // он разбивается.
                            curNode = curNode.getParent(); // Возврат уровнем выше
                            // Поиск
                            curNode = getNextChild(curNode, sumBytes, bytes);
                        } else if (curNode.isLeaf()) // Если узел листовой,
                            break; // переход к вставке
                            // Узел не полный и не листовой; спуститься уровнем ниже
                        else
                            curNode = getNextChild(curNode, sumBytes, bytes);
                    }
                    curNode.insertItem(tempItem); // Вставка нового объекта DataItem
                    inMemoryCacheSize[1] += bytes.length;
                    System.out.println(inMemoryCacheSize[1] + "/" + inMemoryCacheSize[0] + " - saved in memory.");
                } else if (inFileSystemCacheSize[0] > inFileSystemCacheSize[1] + bytes.length) {


                    String bytesEntity = sumBytes + "x";
                    for (int y = 0; y < bytes.length; y++)
                        bytesEntity += y == 0 ? bytes[y] : " " + bytes[y];
                    bytesEntity += "\n";
                    try {
                        Files.write(Paths.get("./cache.txt"), bytesEntity.getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        System.out.println(e);
                    }


                    inFileSystemCacheSize[1] += bytes.length;
                    System.out.println(inFileSystemCacheSize[1] + "/" + inFileSystemCacheSize[0] + " - saved in file system.");
                }


            } else {
                System.out.println("Object exists");
                try {
                    System.out.println(serializeUnsafe.deserialize(bytes));
                    System.out.println(serializeUnsafe.deserialize(find));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        // -------------------------------------------------------------
        public void split(Node thisNode) // Разбиение узла
        {
            // Предполагается, что узел полон
            DataItem itemB, itemC;
            Node parent, child2, child3;
            int itemIndex;
            itemC = thisNode.removeItem(); // Удаление элементов из
            itemB = thisNode.removeItem(); // текущего узла
            child2 = thisNode.disconnectChild(2); // Отсоединение потомков
            child3 = thisNode.disconnectChild(3); // от текущего узла
            Node newRight = new Node(); // Создание нового узла
            if(thisNode==root) // Если узел является корнем,
            {
                root = new Node(); // Создание нового корня
                parent = root; // Корень становится родителем
                root.connectChild(0, thisNode); // Связывание с родителем
            }
            else // Текущий узел не является корнем
                parent = thisNode.getParent(); // Получение родителя
            // Разбираемся с родителем
            itemIndex = parent.insertItem(itemB); // B вставляется в родителя
            int n = parent.getNumItems(); // Всего элементов?
            for(int j=n-1; j>itemIndex; j--) // Перемещение связей
            { // родителя
                Node temp = parent.disconnectChild(j); // На одного потомка
                parent.connectChild(j+1, temp); // вправо
            }
            // Связывание newRight с родителем
            parent.connectChild(itemIndex+1, newRight);
            // Разбираемся с узлом newRight
            newRight.insertItem(itemC); // Элемент C в newRight
            newRight.connectChild(0, child2); // Связывание 0 и 1
            newRight.connectChild(1, child3); // с newRight
        }
        // -------------------------------------------------------------
        // Получение соответствующего потомка при поиске значения
        public Node getNextChild(Node theNode, long theValue, byte[] bytes)
        {
            int j;
            // Предполагается, что узел не пуст, не полон и не является листом
            int numItems = theNode.getNumItems();

            for(j=0; j<numItems; j++) // Для каждого элемента в узле
            { // Наше значение меньше?
                if( theValue < theNode.getItem(j).sumBytes )
                    return theNode.getChild(j); // Вернуть левого потомка
            } // Наше значение больше,
            return theNode.getChild(j); // Вернуть правого потомка
        }
        // -------------------------------------------------------------
        public void displayTree()
        {
            recDisplayTree(root, 0, 0);
        }
        // -------------------------------------------------------------
        private void recDisplayTree(Node thisNode, int level,
                                    int childNumber)
        {
            System.out.print("level="+level+" child="+childNumber+" ");
            thisNode.displayNode(); // Вывод содержимого узла
            // Рекурсивный вызов для каждого потомка текущего узла
            int numItems = thisNode.getNumItems();
            for(int j=0; j<numItems+1; j++)
            {
                Node nextNode = thisNode.getChild(j);
                if(nextNode != null)
                    recDisplayTree(nextNode, level+1, j);
                else
                    return;
            }
        }

        public void setInMemoryCacheSize(int inMemoryCacheSize) {
            this.inMemoryCacheSize[0] = inMemoryCacheSize;
        }

        public void setInFileSystemCacheSize(int inFileSystemCacheSize) {
            this.inFileSystemCacheSize[0] = inFileSystemCacheSize;
        }

    }
