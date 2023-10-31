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
            Play play=plays.one(ticker,prices,buy3);
            play.summary();
            } else System.out.println("no prices for: from: "+from+" to: "+to);
        }
    }
}
