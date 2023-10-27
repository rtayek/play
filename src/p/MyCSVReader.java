package p;
import static p.DataPaths.rPath;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public class MyCSVReader {
    MyCSVReader(String path) { this.path=path; }
    void parseLine() {
        words=new ArrayList<>();
        boolean inQuotes=false;
        boolean startOfWord=true;
        for(int i=0;i<line.length();++i) {
            if(startOfWord) { word=""; startOfWord=false; }
            if(((Character)(line.charAt(i))).equals(separator)) if(inQuotes) {
                word=word+line.charAt(i); // treat as a normal character. 
            } else words.add(word);
            else { // add in the character to the word.
                word=word+line.charAt(i);
            }
        }
    }
    void run() {
        if(!new File(path).exists()) throw new RuntimeException("file"+path+" does not esist!");
        try {
            BufferedReader br=new BufferedReader(new FileReader(path));
            ArrayList<ArrayList<String>> lines=new ArrayList<>();
            int row=0;
            try {
                while((line=br.readLine())!=null) {
                    System.out.println("line: "+line);
                    parseLine();
                    lines.add(words);
                    ++row;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("file"+path+" threw!");
        }
    }
    public static void main(String[] args) {
        String filename="apple.csv";
        Path csvFile=Path.of(rPath.toString(),filename);
        String path=csvFile.toString();
        MyCSVReader myCSVReader=new MyCSVReader(path);
        myCSVReader.run();
    }
    final String path;
    final Character separator=',';
    int row=0;
    ArrayList<String> words;
    String word; // really a column name or value
    String line;
    final ArrayList<ArrayList<String>> lines=new ArrayList<>();
    boolean inQuotes;
}
