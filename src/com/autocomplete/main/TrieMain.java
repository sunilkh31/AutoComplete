package com.autocomplete.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import com.autocomplete.comparators.StringKeyComparator;
import com.autocomplete.datatype.PatriciaTrie;

public class TrieMain {

    private static final int MAX_RESULTS = 20;

    public static void main(String[] args) throws Exception {
        TrieMain main = new TrieMain();

        PatriciaTrie<String> trie = main.createTrie();

        String input = main.getUserInput();

        List<String> suggestions = trie.getSuggestions(input, MAX_RESULTS);
        for (String suggestion : suggestions) {
            System.out.println(suggestion);
        }
    }

    /**
     * Get's the user input from command line
     * 
     * @return String
     */
    private String getUserInput() {
        Scanner in = null;
        try {
            in = new Scanner(System.in);
            return in.nextLine();
        } finally {
            if (in != null)
                in.close();
        }
    }

    /**
     * Creates the Patricia Trie from the data.txt file
     * 
     * @return PatriciaTrie
     * @throws Exception
     */
    private PatriciaTrie<String> createTrie() throws Exception {
        PatriciaTrie<String> trie = new PatriciaTrie<>(StringKeyComparator.CHAR);
        BufferedReader br = null;
        try {

            URL url = TrieMain.class.getResource("data.txt");
            File file = new File(url.toURI());
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                line.trim();
                if (!line.isEmpty()) {
                    trie.put(line);
                }
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        return trie;
    }
}
