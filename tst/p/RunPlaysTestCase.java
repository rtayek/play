package p;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.*;
class RunPlaysTestCase {
    // just so i know when i break one.
    @Test void testPlayWithNOArguments() throws IOException {
        plays.minSize=10;
        plays.maxsize=10;
        plays.maxFiles=10; // how many files.
        plays.main(new String[] {});
        // fails when we run all the tests together!
    }
    Plays plays=new Plays();
}
