package p;
import static org.junit.jupiter.api.Assertions.*;
import static p.CSV.*;
import static p.DataPaths.newPrices;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
class CSVTestCase {
    // these will fail if we download newer versions of the data.
    @Test void testGetApple() {
        String filename="AAPL.csv";
        List<String[]> rows=getCSV(newPrices,filename);
        assertEquals(8524,rows.size());
    }
    @Test void testGetNewPrices() { List<String[]> rows=getNewPrices("AAPL"); assertEquals(8524,rows.size()); }
    @Test void testGetFilter() {
        List<String[]> rows=getNewPrices("AAPL");
        String[] first=rows.get(1);
        String[] last=rows.get(rows.size()-1);
        System.out.println(first[0]+" "+last[0]);
        for(int i=1;i<5;++i) {
            String[] words=rows.get(i);
            MyDate myDate=new MyDate(words[0]);
            System.out.println(myDate);
            System.out.println(Arrays.asList(words));
        }
        MyDate from=new MyDate("1990-01-03");
        MyDate to=new MyDate("1990-01-05");
        System.out.println("filtering from: "+from+" to: "+to);
        List<String[]> filtered=filter(rows,from.date(),to.date());
        System.out.println("filtered");
        for(String[] row:filtered) {
            System.out.println(Arrays.asList(row));
        }
    }
}
