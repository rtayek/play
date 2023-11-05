package p;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static p.CSV.*;
import static p.Strategy.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import com.tayek.util.Pair;
import p.Plays.Play;
public class Time {
    public static void main(String[] args) throws IOException {
        System.out.println("enter main()");
        Stock.sortExchangesByFrequency();
        String ticker="AAPL";
        final List<String[]> rows=getNewPrices(ticker);
        //String f=rows.get(1)[0];
        //String t=rows.get(rows.size()-1)[0];
        //System.out.println("f: "+f+",  t: "+t);
        Strategy strategy=strategy2;
        TreeMap<Comparable<?>,Play> map=new TreeMap<>();
        // maybe generate a list of (from,to) pairs
        ArrayList<Pair> pairs=timePeriodDates(rows);
        for(Pair pair:pairs) {
            //for(int i=1990;i<=2022;++i) {
            //System.out.println("enter loop");
            //MyDate from=new MyDate(i+"-01-01");
            //MyDate to=new MyDate((i+1)+"-01-01"); // maybe use 02-01?
            MyDate from=new MyDate(pair.first.toString());
            MyDate to=new MyDate(pair.second.toString());
            //System.out.println("from: "+from+" to: "+to);
            List<String[]> timePeriod=filter(rows,from.date(),to.date());
            //List<String> firstRowOfPart=Arrays.asList(timePeriod.get(0));
            //System.out.println("first row of this part: "+firstRowOfPart);
            //List<String> secondRowOfPart=Arrays.asList(timePeriod.get(1));
            //System.out.println("second row of this part: "+secondRowOfPart);
            MyDate myDate=new MyDate(timePeriod.get(1)[0]);
            Double[] prices=getClosingPrices(timePeriod);
            if(prices.length>0) {
                Plays plays=new Plays();
                Play play=plays.new Play(ticker);
                play.prices=prices;
                play.rake=.0;
                //play.verbosity=1;
                play.date=myDate;
                play.strategyName=strategy.name; // this is just the name for csv.
                play.oneStock(strategy);
                if(play.hProfit.n()>0) {
                    if(play.verbosity>1) System.out.println("profit: "+play.hProfit());
                    play.update();
                }
                Plays.combinedMap.putAll(plays.map); // combine results
                //System.out.println(play);
                System.out.println(play.toCSVLine());
            } else System.out.println("no prices for: from: "+from+" to: "+to);
        }
        Plays.toCsvFile(Plays.combinedMap,"time.csv");
    }
}
