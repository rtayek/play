package p;
import static org.junit.jupiter.api.Assertions.*;
import static p.CSV.*;
import static p.DataPaths.newPrices;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.tayek.util.Pair;
class CSVTestCase {
    // some of these will fail if we download newer versions of the data.
    @Test void testGetApple() {
        String filename="AAPL.csv";
        List<String[]> rows=getCSV(newPrices,filename);
        assertEquals(8524,rows.size());
    }
    @Test void testGetNewPrices() { List<String[]> rows=getNewPrices("AAPL"); assertEquals(8524,rows.size()); }
    @Test void testCSVEquals() {
        String[] x=new String[] {"a","b","c"};
        String[] y=new String[] {"a","b","c"};
        String[] z=new String[] {"a","b","z"};
        ArrayList<String[]> lx=new ArrayList<>();
        lx.add(x);
        ArrayList<String[]> ly=new ArrayList<>();
        ly.add(y);
        assertTrue(CSV.equals(lx,ly));
        ArrayList<String[]> lz=new ArrayList<>();
        lz.add(z);
        assertFalse(CSV.equals(lx,lz));
        ly.add(z);
        assertFalse(CSV.equals(ly,lz));
    }
    @Test void testIndicesToDates() {
        List<String[]> rows=getNewPrices("AAPL");
        //System.out.println(1+" "+(rows.size()-1));
        String f=rows.get(1)[0];
        String l=rows.get(rows.size()-1)[0];
        MyDate first=new MyDate(f);
        MyDate last=new MyDate(l);
        //System.out.println("first:  "+first+", last: "+last);
        Pair indices=datesToindices(rows,first,last);
        Integer start=(Integer)indices.first;
        Integer stop=(Integer)indices.second;
        //System.out.println("start: "+start+", stop: "+stop);
        Pair dates=indicesToDates(rows,start,stop);
        MyDate first2=(MyDate)dates.first;
        MyDate last2=(MyDate)dates.second;
        //System.out.println("first2: "+first2+", last2: "+last2);
        Pair indices2=datesToindices(rows,first,last);
        //System.out.println(indices2.first+" "+indices.second);
        assertEquals(1,indices2.first);
        assertEquals(rows.size()-1,indices2.second);
        assertEquals(first,first2);
        assertEquals(last,last2);
    }
    @Test void testGetFilter() {
        List<String[]> rows=getNewPrices("AAPL");
        String[] first=rows.get(1);
        String[] last=rows.get(rows.size()-1);
        System.out.println(first[0]+" "+last[0]);
        for(int i=1;i<6;++i) {
            String[] words=rows.get(i);
            MyDate myDate=new MyDate(words[0]);
            //System.out.println(myDate);
            //System.out.println(Arrays.asList(words));
        }
        MyDate from=new MyDate("1990-01-03");
        MyDate to=new MyDate("1990-01-05");
        //System.out.println("filtering from: "+from+" to: "+to);
        List<String[]> filtered=filter(rows,from.date(),to.date());
        //System.out.println("filtered");
        for(String[] row:filtered) {
            ; //System.out.println(Arrays.asList(row));
        }
        MyDate f=new MyDate(rows.get(2)[0]);
        MyDate t=new MyDate(rows.get(4)[0]);
        //System.out.println("filtering from: "+from+" to: "+to);
        List<String[]> filtered2=filter(rows,f.date(),t.date());
        //System.out.println("filtered2");
        for(String[] row:filtered2) {
            ; //System.out.println(Arrays.asList(row));
        }
        assertTrue(CSV.equals(filtered,filtered2));
        // seems to be working. why doesn't the code in Time work?
        // these filtered have a csv header.
        // do the ones in time have a header?
    }
}
