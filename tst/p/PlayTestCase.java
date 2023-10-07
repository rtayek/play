package p;
import static p.DataPaths.kagglePath;
import static p.Strategy.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiPredicate;
import org.junit.jupiter.api.*;
class PlayTestCase {
    @AfterEach void setUpn() throws Exception { Play.buffer=3; }
    @AfterEach void tearDown() throws Exception {}
    @Test void testAdjustBankrollForR() {
        Double[] prices=new Double[] {1.,2.,3.,4.,5.,6.,7.,8.,9.,10.};
        play=new Play("test",prices);
        play.rake=.01;
        play.verbosity=2;
        Play.buffer=5;
        play.rake=.01;
        if(play.verbosity>0) System.out.println(play);
        play.oneStock(buy0);
        double expectedBankroll=1.37214;
        assertEquals(expectedBankroll,play.bankroll,epsilon);
        if(play.verbosity>0) System.out.println(play);
        System.out.println(play.totalRake);
        double expectedTotalRake=.02188;
        assertEquals(expectedTotalRake,play.totalRake,epsilon);
    }
    static double epsilon=1e-6;
    @Test void testP1() {
        play=new Play("test",p1);
        play.rake=0;
        play.verbosity=2;
        play.oneStock(buy);
        assertEquals(0.,play.bankroll);
    }
    @Test void testP2() { play=new Play("test",p2); play.rake=0; play.oneStock(buy); assertEquals(1,play.bankroll); }
    @Test void testP3() { play=new Play("test",p3); play.rake=0; play.oneStock(buy); assertEquals(2,play.bankroll); }
    @Test void testP4() {
        play=new Play("test",p4);
        play.rake=0;
        play.bankroll=2;
        play.oneStock(buy);
        assertEquals(4,play.bankroll);
    }
    @Test void testP5() {
        play=new Play("test",p5);
        play.rake=0;
        play.verbosity=2;
        play.oneStock(buy);
        assertEquals(1,play.bankroll);
    }
    @Test void testEmpty() { String s=Play.toLine(); assertEquals("",s); }
    @Test void testPartOfOne() { String s=Play.toLine("foo"); assertEquals("foo",s); }
    @Test void testOne() { String foo="foo"; String s=Play.toLine("foo",foo); assertEquals("foo: foo",s); }
    @Test void testTwo() {
        String foo="foo",bar="bar";
        String s=Play.toLine("foo",foo,"bar",bar);
        assertEquals("foo: foo, bar: bar",s);
    }
    Play play;
    BiPredicate<Integer,Double[]> buy=buy2;
    static final Double[] p1=new Double[] {1.,2.,3.,0.,0.,0.,0.};
    static final Double[] p2=new Double[] {1.,2.,3.,3.,0.,0.,0.};
    static final Double[] p3=new Double[] {1.,2.,3.,6.,0.,0.,0.};
    static final Double[] p4=new Double[] {1.,2.,3.,6.,0.,0.,0.};
    static final Double[] p5=new Double[] {1.,2.,3.,6.,3.,0.,0.,0.};
}
