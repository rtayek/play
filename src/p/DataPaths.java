package p;
import java.nio.file.Path;
import java.util.*;
public class DataPaths {
    public static void main(String[] args) {
        System.out.println("in main()");
    }
    public static final Path rPath=Path.of("D:\\ray\\rapps\\getSymbols\\");
    public static final Path kagglePath=Path.of("D:\\data\\kagglestock\\Stocks\\");
    public static final Path indicesPath=Path.of("D:\\data\\spy\\indice\\");
    public static final String[] indiesFilenames=new String[] {"ALLOrdinary.csv","CAC40.csv","DAXI.csv","DJI.csv","HSI.csv",
            "Nikkei225.csv","SP500.csv","SPY.csv","nasdaq_composite.csv"};
}
