import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordManager {
    private List<Word> words;

    public WordManager(String fileName) {
        this.words = readWords(fileName);
        Collections.shuffle(words);
    }

    private List<Word> readWords(String fileName) {
        List<Word> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    words.add(new Word(parts[0].trim().toUpperCase(), parts[1].trim().toUpperCase()));
                } else {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public List<Word> getWords() {
        return words;
    }
}
