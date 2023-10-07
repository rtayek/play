package p;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.*;
class RunPlaysTestCase {
    // just so i know when i break one.
    @Test void testPlayWithNOArguments() throws IOException {
        System.out.println("<<<<<<<<<<<<<<<");
        Play.minSize=10;
        Play.maxsize=10;
        Play.maxFiles=10; // how many files.
        Play.main(new String[] {});
        // fails when we run all the tests together!
        System.out.println(">>>>>>>>>>>>>>");
    }
}
