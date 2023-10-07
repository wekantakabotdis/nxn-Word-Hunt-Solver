import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

class Main {
    private static final String[] Dictionary = readArray("Words.txt");
    private static final Set< String > finalDictionary = removeShortWords(convertToSet(Dictionary));
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(true);
        while (true) {
            System.out.println("Board size?");
            int size = reader.nextInt();
            System.out.println("type letters in word hunt board to solve");
            String input = reader.next();
            input = input.toUpperCase();
            input = flattenGrid(input);
            char[][] map = create2DArray(input, size);
            printBoard(map);
            Map< String, String > foundWords = searchBoggle(map, finalDictionary, size);
            int totalWords = 0;
            int totalPoints = 0;
            foundWords = sortWords(foundWords);
            for (Map.Entry < String, String > entry: foundWords.entrySet()) {
                String word = entry.getKey();
                String coordinates = entry.getValue().split(":")[0];
                String directions = entry.getValue().split(":")[1];

                System.out.format("%-20s %20s %20s %40s", word, nf.format(getPoints(word)), "(" + coordinates + ")", directions + "\n");
                totalWords++;
                totalPoints += getPoints(word);

            }
            System.out.format("%-20s %20s", "Total words", nf.format(totalWords));
            System.out.println();
            System.out.format("%-20s %20s", "Total points", nf.format(totalPoints));
            System.out.println();

            printBoard(map);
        }
    }
    public static char[][] create2DArray(String input, int size) {

        // Create an empty sizexsize 2D array
        char[][] arr = new char[size][size];

        // Iterate through the input string and place each letter in the 2D array
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int index = i * size + j;
                if (index >= 0 && index < input.length()) {
                    arr[i][j] = input.charAt(index);
                }
            }
        }

        return arr;
    }
    public static String flattenGrid(String input) {
        // Use a regular expression to match any sequence of whitespace characters
        String regex = "\\s+";

        // Split the input string into an array of strings using the regular expression
        String[] rows = input.split(regex);

        // Create a new StringBuilder to store the flattened grid
        StringBuilder sb = new StringBuilder();

        // Iterate through each row in the input string
        for (String row : rows) {
            // Append the current row to the StringBuilder
            sb.append(row);
        }

        // Return the flattened grid
        return sb.toString();
    }

    public static int getPoints(String x) {

        if (x.length() < 3) {
            return 0;
        }
        if (x.length() == 3) {
            return 100;
        }
        if (x.length() == 4) {
            return 400;
        }
        if (x.length() == 5) {
            return 800;
        }
        if (x.length() == 6) {
            return 1400;
        }
        return (1400 + (400 * (x.length() - 6)));
    }


    public static Set < String > convertToSet(String[] array) {
        return new HashSet< >(Arrays.asList(array));
    }

    public static void printBoard(char[][] x) {
        for (char[] chars : x) {
            for (int c = 0; c < x[0].length; c++) {
                System.out.print(chars[c] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static String[] readArray(String file) {
        int ctr = 0;
        try {
            Scanner s1 = new Scanner(new File(file));
            while (s1.hasNextLine()) {
                ctr++;
                s1.next();
            }

            String[] Dictionary = new String[ctr];
            Scanner s2 = new Scanner(new File(file));
            for (int i = 0; i < ctr; i++) {
                Dictionary[i] = s2.next();
            }
            return Dictionary;
        } catch (FileNotFoundException ignored) {

        }
        return null;
    }



    private static void insert(Trie root, String str) {
        // start from the root node
        Trie curr = root;

        for (char ch: str.toCharArray()) {
            // create a new node if the path doesn't exist
            curr.character.putIfAbsent(ch, new Trie());

            // go to the next node
            curr = curr.character.get(ch);
        }

        curr.isLeaf = true;
    }
    public static void search(char[][] map, boolean[][] visited, int i, int j, Trie trie, StringBuilder word, Map < String, String > foundWords, int startRow, int startCol, String directions) {
        // Check if the current cell is within the bounds of the grid
        if (i >= 0 && i < map.length && j >= 0 && j < map[0].length) {
            // Check if the current cell has not been visited
            if (!visited[i][j]) {
                // Check if the current cell is a valid character for the current word
                if (trie.character.containsKey(map[i][j])) {
                    // Mark the current cell as visited
                    visited[i][j] = true;

                    // Append the current character to the current word
                    word.append(map[i][j]);

                    // Move to the next node in the Trie
                    trie = trie.character.get(map[i][j]);

                    // Check if the current word is a valid word
                    if (trie.isLeaf) {
                        // Add the current word to the Map object
                        foundWords.put(word.toString(), startRow + "," + startCol + ":" + directions);
                    }

                    // Recursively search for words in the 8 adjacent cells
                    search(map, visited, i - 1, j - 1, trie, word, foundWords, startRow, startCol, directions + "LU, ");
                    search(map, visited, i - 1, j, trie, word, foundWords, startRow, startCol, directions + "U, ");
                    search(map, visited, i - 1, j + 1, trie, word, foundWords, startRow, startCol, directions + "RU, ");
                    search(map, visited, i, j - 1, trie, word, foundWords, startRow, startCol, directions + "L, ");
                    search(map, visited, i, j + 1, trie, word, foundWords, startRow, startCol, directions + "R, ");
                    search(map, visited, i + 1, j - 1, trie, word, foundWords, startRow, startCol, directions + "LD, ");
                    search(map, visited, i + 1, j, trie, word, foundWords, startRow, startCol, directions + "D, ");
                    search(map, visited, i + 1, j + 1, trie, word, foundWords, startRow, startCol, directions + "RD, ");

                    // Backtrack and mark the current cell as unvisited
                    visited[i][j] = false;
                    word.setLength(word.length() - 1);
                }
            }
        }
    }
    public static Map < String, String > searchBoggle(char[][] map, Set < String > dictionary, int size) {
        // Create a new Map object to store the words that are found
        Map < String, String > foundWords = new HashMap < > ();

        // Create a Trie to store the dictionary of valid words
        Trie trie = new Trie();
        for (String word: dictionary) {
            insert(trie, word);
        }

        // Create a boolean array to keep track of which cells have been visited
        boolean[][] visited = new boolean[size][size];

        // Iterate through each cell in the grid
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Create a StringBuilder to store the current word
                StringBuilder word = new StringBuilder();

                // Search for words starting at the current cell
                search(map, visited, i, j, trie, word, foundWords, i, j, "");
            }
        }

        // Return the Map object containing the words that were found
        return foundWords;
    }
    public static Set < String > removeShortWords(Set < String > words) {
        return words.stream()
                .filter(word -> word.length() >= 3)
                .collect(Collectors.toSet());
    }
    public static Map < String, String > sortWords(Map < String, String > foundWords) {
        // Create a new Map object to store the sorted words
        Map < String, String > sortedWords = new TreeMap < > ((w1, w2) -> {
            // Compare the lengths of the two words
            int lengthDiff = w1.length() - w2.length();
            if (lengthDiff != 0) {
                // If the lengths are different, sort by length
                return lengthDiff;
            } else {
                // If the lengths are the same, sort alphabetically
                return w1.compareTo(w2);
            }
        });

        // Add the words from the foundWords map to the sortedWords map
        sortedWords.putAll(foundWords);

        return sortedWords;
    }
}