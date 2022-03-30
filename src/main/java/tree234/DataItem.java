package tree234;

class DataItem
{
    public long sumBytes; // Сумма байтов по системе кодировки
    public byte[] bytes; // Массив байтов сериализованного объекта

    //--------------------------------------------------------------
    public DataItem(long sumBytes, byte[] bytes) // Конструктор
    {
        this.sumBytes = sumBytes;
        this.bytes=bytes;
    }
    //--------------------------------------------------------------
    public void displayItem() // Вывод элемента в формате "/27"
    { System.out.print("/"+sumBytes); }
//--------------------------------------------------------------
}