package p;
import static p.DataPaths.rPath;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import com.opencsv.exceptions.CsvException;
import static p.Strategy.*;
import static p.CSV.*;
import p.Plays.Play;
public class Apple {
    public static Play apple() throws IOException, CsvException {
        List<String[]> rows=getCSV(rPath,"apple.csv");
        Double[] prices=getClosingPrices(rows);
        //System.out.println(prices.length+" prices.");
        // using r file, but the quotes have been removed. // maybe not
        Plays plays=new Plays();
        // make this use complete path
        // filename is not really the filename
        Play play=plays.new Play("apple from R");
        play.prices=prices;
        play.rake=.0;
        //play.verbosity=1;
        // need to add date!
        play.oneStock(strategy2);
        if(play.verbosity>0) System.out.println(play);
        if(play.verbosity>1) System.out.println("profit: "+play.hProfit());
        if(play.verbosity>0)
            System.out.println("wins: "+play.wins+", buys: "+play.buys+", total rake: "+play.totalRake);
        if(play.verbosity>0) System.out.println(play.toCSVLine());
        return play;
    }
    public static void main(String[] args) throws IOException, CsvException { 
        Play play=apple(); 
        System.out.println(play);
        }
}
