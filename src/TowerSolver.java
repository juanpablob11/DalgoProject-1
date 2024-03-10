import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.nio.file.*;

public class TowerSolver {

    public static void main(String[] args) {
        // The path to the file containing the test cases (RELATIVE PATH)
        String filePath = "src\\testCases.txt";
    
        try {
            // Read all lines from the file into a list
            List<String> lines = Files.readAllLines(Paths.get(filePath));
    
            // The first line contains the number of test cases
            int numberOfTestCases = Integer.parseInt(lines.get(0));
    
            // Process each test case
            for (int i = 1; i <= numberOfTestCases; i++) {
                String[] parts = lines.get(i).split("\\s+");
                int n = Integer.parseInt(parts[0]);
                int[] T = new int[parts.length - 1];
                final int testCaseNumber = i; 
    
                for (int j = 1; j < parts.length; j++) {
                    T[j - 1] = Integer.parseInt(parts[j]);
                }
    
                // Create a separate thread for each computation
                Thread taskThread = new Thread(() -> {
                    int result = solveDp(T, n);
                    System.out.println("The minimum number of moves for test case " + testCaseNumber + " is: " + result);
                });
    
                taskThread.start();
    
                try {
                    taskThread.join(120000); // Wait for up to 2 minutes
                    if (taskThread.isAlive()) {
                        taskThread.interrupt();
                        System.out.println("Calculation for test case " + testCaseNumber + " was aborted because it took more than two minutes.");
                    }
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted due to an unexpected error.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }
    

    private static int solveDp(int[] T, int n) {

        // Total number of pieces across all towers
        int sumOfPieces = Arrays.stream(T).sum();
        // Maximum height that can be achieved by any tower
        int maxHeight = Arrays.stream(T).max().getAsInt();
    
        // Initialize the DP array with the dimension based on input sizes
        int[][][] dp = new int[n + 1][sumOfPieces + 1][maxHeight + 1];
    
        // Fill base cases for DP
        for (int j = 0; j <= sumOfPieces; j++) {
            Arrays.fill(dp[1][j], 0); // No moves needed with only one tower
        }
    
        // Optimize by precalculating sums to avoid redundant calculations within loops
        int[] prefixSums = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            prefixSums[i] = prefixSums[i - 1] + T[i - 1];
        }
    
        // Bottom-up DP approach
        for (int i = 2; i <= n; i++) {
            for (int j = 0; j <= sumOfPieces; j++) {
                int currentSum = prefixSums[i - 1]; // Use precalculated sum
    
                for (int k = 0; k <= j / i; k++) {
                    int absDiff = Math.abs((j - k) - currentSum);
                    if (k <= maxHeight) {
                        int currentMinMoves = dp[i - 1][j - k][k];
                        int newB = k;
                        for (int b = k; b <= (j - k) / (i - 1); b++) {
                            if (b < maxHeight && dp[i - 1][j - k][b] < currentMinMoves) {
                                currentMinMoves = dp[i - 1][j - k][b];
                                newB = b;
                            }
                        }
                        dp[i][j][k] = absDiff + dp[i - 1][j - k][newB];
                    }
                }
            }
        }
    
        // Find minimum number of moves
        int minMoves = dp[n][sumOfPieces][0];
        for (int k = 1; k <= sumOfPieces / n; k++) {
            if (dp[n][sumOfPieces][k] < minMoves) {
                minMoves = dp[n][sumOfPieces][k];
            }
        }
        return minMoves;
    }
    
}
