package p;

import static org.junit.jupiter.api.Assertions.*;
import static p.CSV.*;
import static p.DataPaths.newPrices;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CSVTestCase {
    // these will fail if we download newer versions of the data.
    @Test void testGetApple() { 
        String filename="AAPL.csv";
        List<String[]> rows=getCSV(newPrices,filename);
        assertEquals(8524,rows.size());
        }
    @Test void testGetNewPrices() { 
        List<String[]> rows=getNewPrices("AAPL");
        assertEquals(8524,rows.size());
        }
}
