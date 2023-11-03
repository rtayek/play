package p;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static p.CSV.*;
import static p.Strategy.*;
import java.util.List;
import p.Plays.Play;
public class Time {
    public static void main(String[] args) {
        String ticker="AAPL";
        List<String[]> rows=getNewPrices(ticker);
        String f=rows.get(1)[0];
        String t=rows.get(rows.size()-1)[0];
        for(int i=1990;i<=2022;++i) {
            MyDate from=new MyDate(i+"-01-01");
            MyDate to=new MyDate((i+1)+"-01-01"); // maybe use 02-01?
            //System.out.println("from: "+from+" to: "+to);
            List<String[]> timePeriod=filter(rows,from.date(),to.date());
            Double[] prices=getClosingPrices(timePeriod);
            if(prices.length>0) {
            Plays plays=new Plays();
            // make this use complete path
            // filename is not really the filename
            Play play=plays.new Play(ticker);
            play.prices=prices;
            play.rake=.0;
            //play.verbosity=1;
            // need to add date!
            play.oneStock(strategy3);
            if(play.verbosity>0) System.out.println(play);
            if(play.verbosity>1) System.out.println("profit: "+play.hProfit());
            if(play.verbosity>0)
                System.out.println("wins: "+play.wins+", buys: "+play.buys+", total rake: "+play.totalRake);
            if(play.verbosity>0) System.out.println(play.toCSVLine());
            //System.out.println("summary:"); 
            System.out.println(play.toCSVLine());
            } else System.out.println("no prices for: from: "+from+" to: "+to);
        }
    }
}
