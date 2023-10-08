package p;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static p.MyDate.*;
class MyDateTestCase {
    @BeforeAll static void setUpBeforeClass() throws Exception {}
    @AfterAll static void tearDownAfterClass() throws Exception {}
    @BeforeEach void setUp() throws Exception {}
    @AfterEach void tearDown() throws Exception {}
    @Test void test1() {
        String expected="1984-09-07";
        MyDate myDate=new MyDate(expected);
        String actual=myDate.toString2();
        assertEquals(expected,actual);
    }
    @Test void test2() {
        String expected="2000-01-01";
        MyDate myDate=new MyDate(expected);
        String actual=myDate.toString2();
        assertEquals(expected,actual);
    }
    @Test void test3() {
        String expected="1970-01-01";
        MyDate myDate=new MyDate(expected);
        String actual=myDate.toString2();
        assertEquals(expected,actual);
    }
    @Test void testComparable() {
        String d1s="1970-01-01";
        String d2s="1984-09-07";
        String d3s="2000-01-01";
        Date d1=new MyDate(d1s).date();
        Date d2=new MyDate(d2s).date();
        Date d3=new MyDate(d3s).date();
        //int c12=d1.compareTo(d2);
        //int c23=d2.compareTo(d3);
        boolean b=inRange(d1,d2,d3);
        assertTrue(b);
    }

}
