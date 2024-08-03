import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.nio.file.*;
import java.util.Set;

public class Main {

    private static final int DEFAULT_NUM_QUESTIONS = 23;
    private static final String BASE_FILE_NAME = "worksheet";
    private static final String TEX_EXTENSION = ".tex";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String TEX_OUTPUT_DIR = "tex_output";
    private static final String PDF_OUTPUT_DIR = "pdf_output";
    private static int numQuestions;

    public static void main(String[] args) {

        Scanner userInputScanner = new Scanner(System.in);

        numQuestions = getNumQuestions(userInputScanner);

        String generatedLatexContent = generateLatexContent();


        try {
            /* Handle directories for exported files.
               If the directory does not exist, create it */

            // Create tex_output directory if it does not exist
            prepareDirectories();

            String fileName = getUniqueFileName();

            String latexFilePath = TEX_OUTPUT_DIR + "/" + fileName + TEX_EXTENSION;
            String pdfFilePath = PDF_OUTPUT_DIR + "/" + fileName + PDF_EXTENSION;

            createTex(latexFilePath, generatedLatexContent);

            // Compile tex file to PDF
            compilePDF(latexFilePath, PDF_OUTPUT_DIR);

            System.out.println("Content generated! Find it at: " + pdfFilePath);
        }

        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    private static int getNumQuestions(Scanner scanner) {
        while (true) {
            System.out.println("Enter the number of questions, 0 to exit, or nothing for " + DEFAULT_NUM_QUESTIONS + " questions: ");
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    return DEFAULT_NUM_QUESTIONS;
                }

                int numQuestions = Integer.parseInt(input);
                if (numQuestions < 0) {
                    throw new IllegalArgumentException();
                }

                if (numQuestions == 0) {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                return numQuestions;
            }

            catch (Exception e) {
                System.out.println("Invalid input. Please enter a positive integer.");
            }
        }
    }

    private static void prepareDirectories() throws IOException {
        Files.createDirectories(Paths.get(TEX_OUTPUT_DIR));
        Files.createDirectories(Paths.get(PDF_OUTPUT_DIR));
    }

    private static String getUniqueFileName() {
        String fileName = BASE_FILE_NAME;
        int i = 0;
        while (Files.exists(Paths.get(TEX_OUTPUT_DIR, fileName + TEX_EXTENSION)) || Files.exists(Paths.get(PDF_OUTPUT_DIR, fileName + PDF_EXTENSION))) {
            i++;
            fileName = BASE_FILE_NAME + "_" + i;
        }
        return fileName;
    }


    public static String preambleReader() {
        String preambleFile = "preamble.tex";

        StringBuilder preamble = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(preambleFile))) {
            String br_line;
            while ((br_line = br.readLine()) != null) {
                br_line = br_line.replace("\\newcommand{\\numQuestions}{20}", "\\newcommand{\\numQuestions}{" + numQuestions + "}");
                preamble.append(br_line).append("\n");
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return preamble.toString();
    }


    public static String ttQuestionGenerator() {
        String questionFormat = "\\item{\\large\\hspace{20pt} $%d \\times %d =$}";
        Random rng = new Random();
        StringBuilder questionsBuilder = new StringBuilder();
        Set<Tuple<Integer>> uniqueQuestions = new HashSet<>();  // Store unique questions

        while (uniqueQuestions.size() < numQuestions) {
            // Generate random numbers between 2 and 12 inclusive
            int operand1 = rng.nextInt(2, 12) + 1;
            int operand2 = rng.nextInt(2, 12) + 1;

            // Create an ordered tuple to store the operands
            Tuple<Integer> questionTuple = new Tuple<>(operand1, operand2);

            // Ensure questions are unique
            if (uniqueQuestions.contains(questionTuple)) {
                continue;
            }

            uniqueQuestions.add(questionTuple);
            questionsBuilder.append(String.format(questionFormat, operand1, operand2)).append("\n");
        }
        return questionsBuilder.toString();
    }


    private static String generateLatexContent() {
        return preambleReader() +
                "\\begin{document}\n" +
                "\\maketitle\n" +
                "\\section*{Questions}" +
                "\\vspace{1em}" +
                "\\begin{enumerate}" +
                ttQuestionGenerator() +
                "\\end{enumerate}" +
                "\\end{document}";
    }

    private static void createTex(String fileName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compilePDF(String latexFileName, String pdfOutputDir) throws InterruptedException, IOException {
        Path absLatexFilePath = Paths.get(latexFileName).toAbsolutePath();
        ProcessBuilder processBuilder = new ProcessBuilder("pdflatex", absLatexFilePath.toString());
        processBuilder.directory(new File(pdfOutputDir));
        processBuilder.redirectErrorStream(true);  // Merge standard output and error streams
        Process process = processBuilder.start();
        process.waitFor();
    }

}
