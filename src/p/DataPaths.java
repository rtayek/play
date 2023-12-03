package p;
import java.nio.file.Path;
import java.util.*;
public class DataPaths {
    public static void main(String[] args) {
        System.out.println("in main()");
    }
    public static final String myRoot="c:\\dfromrays8350\\";
    public static final Path newPrices=Path.of("c:\\data");
    public static final Path yahooPath=Path.of(myRoot+"\\data\\yahoodata\\yahoosymbols.csv\\");
    public static final Path rPath=Path.of(myRoot+"\\ray\\rapps\\getSymbols\\");
    public static final Path kagglePath=Path.of(myRoot+"\\data\\kagglestock\\Stocks\\");
    public static final Path indicesPath=Path.of(myRoot+"\\data\\spy\\indice\\");
    public static final String[] indiesFilenames=new String[] {"ALLOrdinary.csv","CAC40.csv","DAXI.csv","DJI.csv","HSI.csv",
            "Nikkei225.csv","SP500.csv","SPY.csv","nasdaq_composite.csv"};
}
