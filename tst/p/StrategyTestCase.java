package p;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static p.Strategy.*;
import java.util.Arrays;
import java.util.Random;;
class StrategyTestCase {
    @BeforeEach void setUp() throws Exception {}
    @AfterEach void tearDown() throws Exception {}
    @Test void testBuy3AndBuy3b() {
        //Double[] prices=new Double[] {0.,1.,3.,6.,10.,15.};
        Double[] prices=new Double[] {0.,1.,2.,3.,4.,5.};
        int index=4;
        boolean b3=buy3.test(index,prices);
        boolean b3b=buy3b.test(index,prices);
        assertEquals(b3,b3b);
        // make 2 tests and have both pass.
    }
    @Test void testRandom() {
        Random r=new Random();
        Double[] prices=new Double[6];
        int failures=0;
        int n=1000;
        for(int j=0;j<n;++j) {
            for(int i=0;i<prices.length;++i) prices[i]=r.nextDouble();
            //System.out.println(Arrays.asList(prices));
            int index=4;
            boolean b3=buy3.test(index,prices);
            boolean b3b=buy3b.test(index,prices);
            if(b3!=b3b) ++failures;
        }
        System.out.println("failures: "+failures+"/"+n);
        //assertEquals(0,failures);

    }
}