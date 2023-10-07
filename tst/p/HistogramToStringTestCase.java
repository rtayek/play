package p;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import com.tayek.util.Histogram;
class HistogramToStringTestCase {
    @BeforeAll static void setUpBeforeClass() throws Exception {}
    @AfterAll static void tearDownAfterClass() throws Exception {}
    @BeforeEach void setUp() throws Exception {}
    @AfterEach void tearDown() throws Exception {}
    @Test void test() {
        histogram.add(0);
        histogram.add(1);
        histogram.add(2);
        System.out.println(histogram);
        System.out.println(Play.toString(histogram));
    }
    Histogram histogram=new Histogram();
}
