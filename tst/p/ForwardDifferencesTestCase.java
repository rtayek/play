package p;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static p.ForwardDifferences.*;
class ForwardDifferencesTestCase {
    @BeforeEach void setUp() throws Exception {}
    @AfterEach void tearDown() throws Exception {}
    @Test void test() {
        double value=52;
        int n=4;
        Double x[]= {45.,50.,55.,60.};
        Double[] y=new Double[] {0.7071,0.7660,0.8192,0.8660,};
        Double sum=extrapolate(value,n,x,y);
        // Value at 52.0 is 0.788003
        if(Math.abs(sum-0.788003)>1e6) System.out.println("oops");
        //System.out.println("\n Value at "+value+" is "+String.format("%.6g%n",sum));
        value=51;
        sum=extrapolate(value,n,x,y);
        //System.out.println("\n Value at "+value+" is "+String.format("%.6g%n",sum));
        value=53;
        sum=extrapolate(value,n,x,y);
        //System.out.println("\n Value at "+value+" is "+String.format("%.6g%n",sum));
        }
}
