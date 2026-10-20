package com.mathlab;
import java.util.Scanner; 

public class RREFMatrix { 

  // This method converts the matrix into Reduced Row Echelon Form. 

    static void rref(double[][] a) { 

      int rows = a.length;        // Number of rows 

        int cols = a[0].length;    // Number of columns 

        int pivotRow = 0;           // Row where the next pivot is placed 

       // Check every column from left to right. 

        for (int col = 0; col < cols && pivotRow < rows; col++) { 

             // Find the row having the largest absolute value 

            // in the current column. 

            int bestRow = pivotRow; 

            for (int i = pivotRow + 1; i < rows; i++) { 

                if (Math.abs(a[i][col]) > Math.abs(a[bestRow][col])) { 

                    bestRow = i; 

                } 

            } 

            // If the column contains only zeros, move to next column. 

            if (Math.abs(a[bestRow][col]) < 1e-10) { 

                continue; 

            } 

  

            // Swap the pivot row with the best row. 

            double[] temp = a[pivotRow]; 

            a[pivotRow] = a[bestRow]; 

            a[bestRow] = temp; 

            // Divide the entire pivot row by the pivot element. 

            // This makes the pivot equal to 1. 

            double pivot = a[pivotRow][col]; 

            for (int j = 0; j < cols; j++) { 

                a[pivotRow][j] /= pivot; 

            } 

            // Make every other element in the pivot column zero. 

            for (int i = 0; i < rows; i++) { 

                 if (i != pivotRow) { 

                     double factor = a[i][col]; 

                       for (int j = 0; j < cols; j++) { 

                       a[i][j] -= factor * a[pivotRow][j]; 

                    } 

                } 

            } 

         // Move to the next pivot row. 

            pivotRow++; 

        } 

    } 

    // Method to display the matrix. 

    static void printMatrix(double[][] a) { 

        for (double[] row : a) { 

            for (double value : row) { 

  

                // Very small values are treated as zero. 

                if (Math.abs(value) < 1e-10) { 

                    value = 0; 

                } 

  

                System.out.printf("%8.3f ", value); 

            } 

            System.out.println(); 

        } 

    } 

     public static void main(String[] args) { 

       Scanner sc = new Scanner(System.in);  

       // Read matrix dimensions. 

        System.out.print("Enter number of rows: "); 

        int rows = sc.nextInt(); 

        System.out.print("Enter number of columns: "); 

        int cols = sc.nextInt(); 

         double[][] matrix = new double[rows][cols]; 

       // Read matrix elements. 

        System.out.println("Enter the matrix elements:"); 

       for (int i = 0; i < rows; i++) { 

            for (int j = 0; j < cols; j++) { 

                matrix[i][j] = sc.nextDouble(); 

            }   } 

         System.out.println("\nOriginal Matrix:"); 

        printMatrix(matrix); 

        // Convert matrix to RREF. 

        rref(matrix); 

        System.out.println("\nReduced Row Echelon Form (RREF):"); 

        printMatrix(matrix); 

        sc.close(); 

   } 

} 