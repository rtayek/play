package p;
import static org.junit.jupiter.api.Assertions.*;
import static p.Strategy.strategy2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.tayek.util.Pair;
import p.Plays.Play;
import static p.CSV.*;
class TimePeriodTestCase {
    @BeforeEach void setUp() throws Exception {}
    @AfterEach void tearDown() throws Exception {}
    @Test void test() { // this may break if the data changes
        String ticker="AAPL";
        final List<String[]> rows=getNewPrices(ticker);
        ArrayList<Pair> pairs=timePeriodDates(rows,false);
        assertEquals("1990-01-01",pairs.get(0).first.toString());
        assertEquals("1991-01-01",pairs.get(0).second.toString());
    }
    @Test void testQuarter() { // this may break if the data changes
        String ticker="AAPL";
        final List<String[]> rows=getNewPrices(ticker);
        ArrayList<Pair> pairs=timePeriodDates(rows,true);
        assertEquals("1990-01-01",pairs.get(0).first.toString());
        assertEquals("1990-04-01",pairs.get(0).second.toString());
    }
    @Test void testFilterWithQuarterTimePeriods() { // this may break if the data changes
        String ticker="AAPL";
        final List<String[]> rows=getNewPrices(ticker);
        String first=rows.get(1)[0];
        String last=rows.get(rows.size()-1)[0];
        System.out.println(first+" - "+last);
        ArrayList<Pair> pairs=timePeriodDates(rows,true);
        for(Pair pair:pairs) {
            MyDate from=new MyDate(pair.first.toString());
            MyDate to=new MyDate(pair.second.toString());
            System.out.println("pair: from: "+from+" to: "+to);
            List<String[]> inRange=filter(rows,from.date(),to.date());
            System.out.println(inRange.size()+" rows  in range of time period.");
            break;
        }
    }
}
