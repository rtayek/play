package p;
import static p.DataPaths.rPath;
import java.util.List;
import java.util.function.BiPredicate;
import static p.Strategy.*;
import static p.CSV.*;
import p.Plays.Play;
public class Apple {
    public static Play apple() {
        //Double[] prices=getPricesFromR(rPath,"apple.csv",from,to);
        List<String[]> rows=getCSV(rPath,"apple.csv");
        Double[] prices=getClosingPrices(rows);
        //System.out.println(prices.length+" prices.");
        // using r file, but the quotes have been removed. // maybe not
        Plays plays=new Plays();
        Play play=plays.one("apple from R",prices,buy2);
        return play;
    }
    public static void main(String[] args) { Play play=apple(); System.out.println(play); }
}
