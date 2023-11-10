package p;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static p.CSV.*;
import static p.Plays.*;
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
        String ticker=null;
        //TPVG
        ticker="CTR";
        ticker="OII";
        ticker="LXU";
        ticker="SKT";
        ticker="LC";
        ticker="AAPL";
        ticker="GHL";
        final List<String[]> rows=getNewPrices(ticker);
        sample(rows);
        String f=rows.get(1)[0];
        String t=rows.get(rows.size()-1)[0];
        System.out.println("f: "+f+",  t: "+t);
        Strategy strategy=strategy2;
        TreeMap<Comparable<?>,Play> map=new TreeMap<>();
        // maybe generate a list of (from,to) pairs
        ArrayList<Pair> pairs=timePeriodDates(rows,true);
        // this starts at the earliest date
        // make it go backwards?
        System.out.println(pairs);
        System.out.println(pairs.size()+" timer pairs.");
        Plays plays=new Plays();
        int i=0;
        for(Pair pair:pairs) {
            //if(++i>5) break;
            MyDate from=new MyDate(pair.first.toString());
            MyDate to=new MyDate(pair.second.toString());
            // System.out.println("pair: from: "+from+" to: "+to);
            List<String[]> inRange=filter(rows,from.date(),to.date());
            //System.out.println(inRange.size()+" rows  in range of timer period.");
            //List<String> firstRowOfPart=Arrays.asList(timePeriod.get(0));
            //System.out.println("first row of this part: "+firstRowOfPart);
            //List<String> secondRowOfPart=Arrays.asList(timePeriod.get(1));
            //System.out.println("second row of this part: "+secondRowOfPart);
            if(inRange.size()<=1) { System.out.println("no rows in data range from: "+from+" to: "+to); continue; }
            MyDate myDate=new MyDate(inRange.get(1)[0]);
            Double[] prices=getClosingPrices(inRange);
            if(prices.length>0) {
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
                } else System.out.println("no plays!");
                // add to time map so these come out in order!
                // or just build a new map from combined map.
                combinedMap.putAll(plays.map); // combine results
                System.out.println(play.toCSVLine());
            } else System.out.println("no prices for: from: "+from+" to: "+to);
        }
        if(combinedMap.size()>0) {
            toCsvFile(combinedMap.values(),"time.csv");
            TreeMap<Comparable<?>,Play> timeOrdered=new TreeMap<>();
            for(Comparable<?> key:combinedMap.keySet()) {
                Play play=combinedMap.get(key);
                timeOrdered.put(play.date.date(),play);
            }
            toCsvFile(timeOrdered.values(),"orderedtime.csv");
        } else System.out.println("no plays!");
        plays.summary(plays.map,"doesnt.matter"); // needs different plays
        System.out.println(plays.map.size());
        System.out.println(combinedMap.size());
    }
}
