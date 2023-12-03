package p;
import static p.DataPaths.yahooPath;
import static p.Stock.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import com.opencsv.exceptions.CsvException;
class Stock {
    Stock(String[] words) {
        switch(words.length) {
            case 8:
                z=words[7];
                //System.out.println("z: "+z);
            case 7:
                y=words[6];
                //System.out.println("y: "+y);
            case 6:
                x=words[5];
                //System.out.println("x: "+x);
            case 5:
                country=words[4];
            case 4:
                categoryName=words[3];
            case 3:
                exchange=words[2];
                if(exchange.isEmpty()) {
                    ++empty;
                    //System.out.println(words[0]+ " has empty exchange!");
                }
                if(exchange.length()>3) System.out.println("exchange lebgth!=3: "+exchange);
            case 2:
                name=words[1];
            case 1:
                ticker=words[0];
                break;
            case 0:
                System.out.println("empty line: "+Arrays.asList(words));
        }
        if(words.length>2) {
            Integer n=exchannges.containsKey(words[2])?exchannges.get(words[2]):0;
            exchannges.put(words[2],n+1);
        } else; //System.out.println("strange length: "+ticker);
    }
    @Override public String toString() {
        return "Stock [ticker="+ticker+", name="+name+", exchange="+exchange+", categoryName="+categoryName+", country="
                +country+", x="+x+", y="+y+", z="+z+"]";
    }
    static void printExchanges() { for(String x:exchannges.keySet()) System.out.println(exchannges.get(x)+"\t\t"+x); }
    // we have frequencies in the map
    // we do? where?
    // we need to sort the map by frequency
    static void sortExchangesByFrequency() {
        for(String key:exchannges.keySet()) {
            int value=exchannges.get(key);
            if(sortedExchanges.containsKey(value)) {
                System.out.println("duplicate frequency: "+value+" "+key);
            } else sortedExchanges.put(exchannges.get(key),key);
        }
    }
    static void printSortedExchanges() {
        System.out.println("exchages sorted by number of stocks in them");
        for(Integer n:sortedExchanges.keySet()) System.out.println(n+"\t"+sortedExchanges.get(n));
    }
    public static ArrayList<String[]> stocks() { return yahoo; }
    private static ArrayList<String[]> readYahooStocks() {
        Path csvFile=yahooPath;
        ArrayList<String[]> rows=readStocks(csvFile);
        return rows;
    }
    private static ArrayList<String[]> readStocks(Path csvFile) {
        List<String[]> rows=null;
        Reader reader;
        try {
            reader=new FileReader(csvFile.toString());
            com.opencsv.CSVReader r=new com.opencsv.CSVReader(reader);
            try {
                rows=r.readAll();
                //for(String[] row:rows) System.out.println(Arrays.asList(row));
                for(String[] row:rows) if(row.length!=5) System.out.println(Arrays.asList(row));
                //System.out.println(rows.size()+" rows");
            } catch(IOException e) {
                e.printStackTrace();
            } catch(CsvException e) {
                e.printStackTrace();
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<String[]> stocks=new ArrayList<>(); // much faster than List!
        for(String[] row:rows) stocks.add(row);
        return stocks;
    }
    public static boolean isTickerExcluded(String ticker) {
        if(excludedStocks.contains(ticker)) return true;
        return false;
    }
    public static boolean isExchangeExcluded(String exchange) {
        if(excludedExchanges.contains(exchange)) return true;
        return false;
    }
    public static boolean isExcluded(Stock stock) {
        if(isExchangeExcluded(stock.exchange)) return true;
        if(isTickerExcluded(stock.ticker)) return true;
        return false;
    }
    static void buildMap(ArrayList<String[]> stocks,TreeMap<String,Stock> map) {
        for(int i=1;i<stocks.size();++i) { // skip header
            String[] words=stocks.get(i);
            Stock stock=new Stock(words);
            map.put(words[0].trim(),stock); // using ticker here
        }
    }
    public static void main(String[] args) throws IOException,CsvException {
        System.out.println("----------------");
        System.out.println(isExchangeExcluded("YZC.F"));
        Stock stock=yahooStocks.get("YZC.F");
        System.out.println(stock);
        System.out.println(isTickerExcluded("BOTY"));
        stock=yahooStocks.get("BOTY");
        System.out.println(stock);
        // find the init stuff and put into init();
        // read unusual or  maybe unique 
        //  and show exchange frequency
        sortExchangesByFrequency();
        printSortedExchanges();
    }
    String ticker="",name="",exchange="",categoryName="",country="",x="",y="",z="";
    Integer frequency;
    // Ticker,Name,Exchange,Category Name,Country
    static final int tickerINdex=0,nameIndex=1,exchangeIndex=2,categoryIndex=3,countryIndex=4;
    static final TreeMap<String,Stock> yahooStocks=new TreeMap<>(); // map from ticker to stock.
    static final TreeSet<String> excludedExchanges=new TreeSet<>();
    static {
        excludedExchanges.add("PNK"); // excludes about 7k stocks.
    }
    static final TreeSet<String> excludedStocks=new TreeSet<>();
    static {
        excludedStocks.add("YZC.F"); // stopped trading
    }
    static final TreeMap<String,Integer> exchannges=new TreeMap<>();// map from exchange to frequency.
    static final TreeMap<Integer,String> sortedExchanges=new TreeMap<>();
    // the frequencies above may not be unique.
    // only the lasts exchange will  appear.
    // maybe use Map<Integer,List<String>>?
    static int empty;
    static final ArrayList<String[]> yahoo=readYahooStocks();
    static {
        buildMap(yahoo,yahooStocks);
        /*
        Iterator<String> keys=yahooStocks.keySet().iterator();
        int samples=0;
        for(int i=0;i<samples;++i) {
            //while(keys.hasNext()) {
            String key=keys.next();
            System.out.println(key+" "+yahooStocks.get(key));
        }
        */
        if(false) {
            System.out.println(yahooStocks.size()+" stocks.");
            System.out.println(empty+" stocks have empty exchanges!.");
            System.out.println(exchannges.size()+" exchanges");
            System.out.println("frequency of exchangeoccurrence: "+Stock.exchannges);
            System.out.println("header: "+Arrays.asList(yahoo.get(0)));
            System.out.println("first row: "+Arrays.asList(yahoo.get(1)));
            System.out.println("last row: "+Arrays.asList(yahoo.get(yahoo.size()-1)));
            System.out.println("end of static initialization.");
        }
    }
    static Path here=Paths.get("");
    static Path nyqPath=Path.of(here.toString(),"nyq.csv");
    static final ArrayList<String[]> nyq=readStocks(nyqPath);
    static final TreeMap<String,Stock> nyqStocks=new TreeMap<>(); // map from ticker to stock.
    static {
        buildMap(nyq,nyqStocks);
    }
    static final Path nmsFPath=Path.of(here.toString(),"nms.csv");
    static final ArrayList<String[]> nms=readStocks(nmsFPath);
    static final TreeMap<String,Stock> nmsStocks=new TreeMap<>(); // map from ticker to stock.
    static {
        buildMap(nms,nmsStocks);
    }
    static final Path newTopNYQPath=Path.of(here.toString(),"newtopnyq.csv");
    static final Path newTopNMSPath=Path.of(here.toString(),"newtopnms.csv");
}
