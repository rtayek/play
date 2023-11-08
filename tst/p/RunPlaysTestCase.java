package p;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.*;
import com.opencsv.exceptions.CsvException;
class RunPlaysTestCase {
    // just so i know when i break one.
    @Test void testPlayWithNOArguments() throws IOException, CsvException {
        Plays.staticMaxFiles=10;
        Plays.main(new String[] {});
    }
}
