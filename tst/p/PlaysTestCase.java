package p;
import static org.junit.jupiter.api.Assertions.*;
import static p.CSV.getCSV;
import static p.CSV.getClosingPrices;
import static p.DataPaths.newPrices;
import static p.Strategy.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.opencsv.exceptions.CsvException;
import p.Plays.Play;
class PlaysTestCase {
    @BeforeEach void setUpn() throws Exception { plays.buffer=3; }
    @AfterEach void tearDown() throws Exception {}
    @Test void testApple() throws IOException, CsvException {
        Play play=Apple.apple();
        //assertEquals(1.1618703003602524,play.bankroll); // old buy2 with <=
        assertEquals(1.1540557154982802,play.bankroll); // old buy2 with <=
        }
    @Test void testApple2() throws IOException, CsvException {
        Path path=newPrices;
        String filename="AAPL.csv";
        Double[] prices=null;
        List<String[]> rows=getCSV(path,filename);
        System.out.println("map size;  "+map.size()+", file: "+filename+" has data from: "
                +rows.get(1)[0]+" to: "+rows.get(rows.size()-1)[0]);
        prices=getClosingPrices(rows);
        play=plays.new Play(filename);
        play.prices=prices;
        play.rake=.0;
        //play.verbosity=1;
        play.oneStock(strategy2);
        //System.out.println(Plays.result.header());
        //System.out.println(play.toCSVLine());
        //play.toCSVLine();
        //System.out.println(play.bankroll());
        assertEquals(2.8092392880796333,play.bankroll());

    }
    Plays plays=new Plays();
    Play play;
}
