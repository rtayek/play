package p;
import static org.junit.jupiter.api.Assertions.*;
import static p.Strategy.buy2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import p.Plays.Play;
class PlaysTestCase {
    @BeforeEach void setUpn() throws Exception { plays.buffer=3; }
    @AfterEach void tearDown() throws Exception {}
    @Test void testApple() {
        Play play=Apple.apple();
        //assertEquals(1.1618703003602524,play.bankroll); // old buy2 with <=
        assertEquals(1.1540557154982802,play.bankroll); // old buy2 with <=
        }
    Plays plays=new Plays();
    Play play;
}
