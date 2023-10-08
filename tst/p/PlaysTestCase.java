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
        Play play=plays.appl21016(buy2);
        assertEquals(1.1618703003602524,play.bankroll);
        }
    Plays plays=new Plays();
    Play play;
}
