package p;
import static org.junit.jupiter.api.Assertions.*;
import static p.CSV.getNewPrices;
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
    @Test void test() {
        String ticker="AAPL";
        final List<String[]> rows=getNewPrices(ticker);
        ArrayList<Pair> pairs=timePeriods(rows);
        for(Pair pair:pairs) System.out.println(pair.first+"  "+pair.second);
        // this may break if the data changes
    }
}
