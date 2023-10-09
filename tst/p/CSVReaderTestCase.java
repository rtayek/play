package p;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static p.DataPaths.*;
import static p.CSVReader.*;
class CSVReaderTestCase {
    String y=yahooPath.toString();
    @Test void testReadYahooSymbols() {
        List<String[]> data=read(",",y);
        assertEquals(106329,data.size());
    }
    @Test void testReadRFile() {
        String filename="apple.csv";
        MyDate from=new MyDate("2016-01-01");
        MyDate to=new MyDate("2017-01-01");
        List<String[]> r=CSVReader.fromR(rPath,filename,from,to);
        }
    @Test void testGetPricesFromR() {
        MyDate from=new MyDate("2016-01-01");
        MyDate to=new MyDate("2017-01-01");
        Double[] prices=CSVReader.getPricesFromR(rPath,"apple.csv",from,to);
        assertNotNull(prices);
        }
    @Test void test() {
        String string=com.tayek.util.StringUtilities.toString(Arrays.asList(aapl).iterator(),"\n",true);
        BufferedReader br=new BufferedReader(new StringReader(string));
        List<String[]> rows=CSVReader.read(",",br);
        String[] headers=rows.remove(0);
        for(String[] words:rows) {
            String expected=words[0];
            String actual=new MyDate(expected).toString2();
            assertEquals(expected,actual);
        }
    }
    // @formatter:off
    static String[] aapl =new String[] { 
    "Date,Open,High,Low,Close,Volume,OpenInt",
    "1984-09-07,0.42388,0.42902,0.41874,0.42388,23220030,0",
    "1984-09-10,0.42388,0.42516,0.41366,0.42134,18022532,0",
    "1984-09-11,0.42516,0.43668,0.42516,0.42902,42498199,0",
    "1984-09-12,0.42902,0.43157,0.41618,0.41618,37125801,0",
    "1984-09-13,0.43927,0.44052,0.43927,0.43927,57822062,0",
    "1984-09-14,0.44052,0.45589,0.44052,0.44566,68847968,0",
    "1984-09-17,0.45718,0.46357,0.45718,0.45718,53755262,0",
    "1984-09-18,0.45718,0.46103,0.44052,0.44052,27136886,0",
    "1984-09-19,0.44052,0.44566,0.43157,0.43157,29641922,0",
    };
    // @formatter:on
}
