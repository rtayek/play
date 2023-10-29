package p;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.*;
class RunPlaysTestCase {
    // just so i know when i break one.
    @Test void testPlayWithNOArguments() throws IOException {
        Plays.staticMaxFiles=10;
        Plays.main(new String[] {});
    }
}
