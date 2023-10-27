package p;
import static p.CSVReader.read;
import static p.DataPaths.yahooPath;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
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
                if(exchange.isEmpty())
                    System.out.println("empty exchange");
                if(exchange.length()>3)
                    System.out.println("exchange: "+exchange);
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
        } else ; //System.out.println("strange length: "+ticker);
    }
    @Override public String toString() {
        return "Stock [ticker="+ticker+", name="+name+", exchange="+exchange+", categoryName="+categoryName+", country="
                +country+", x="+x+", y="+y+", z="+z+"]";
    }
    static void printExchanges() { for(String x:exchannges.keySet()) System.out.println(exchannges.get(x)+"\t\t"+x); }
    // we have frequencies in the map
    // we need to sort the map by frequency
    static void sort() { for(String x:exchannges.keySet()) sorted.put(exchannges.get(x),x); }
    static void printSorted() { for(Integer n:sorted.keySet()) System.out.println(n+"\t"+sorted.get(n)); }
    String ticker="",name="",exchange="",categoryName="",country="",x="",y="",z="";
    Integer frequency;
    // Ticker,Name,Exchange,Category Name,Country
    static final int tickerINdex=0,nameIndex=1,exchangeIndex=2,categoryIndex=3,countryIndex=4;
    static final SortedMap<String,Stock> stocks=new TreeMap<>();
    static final TreeMap<String,Integer> exchannges=new TreeMap<>();
    static final TreeMap<Integer,String> sorted=new TreeMap<>(); // will not be unique!
    static {
        List<String[]> data=read(",",yahooPath.toString());
        int n=data.get(0).length;
        for(String[] words:data) {
            Stock stock=new Stock(words);
            stocks.put(words[0],stock);
        }
        System.out.println(Stock.stocks.size()+" stocks.");
        //System.out.println(Stock.exchannges);
        System.out.println("end of static initialization.");
    }
}
