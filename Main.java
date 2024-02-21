import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.nio.file.*;

public class Main {

    static int numQuestions;
    public static void main(String[] args) {

        Scanner inp = new Scanner(System.in);

        while (true) {
            System.out.println("Enter the number of questions, 0 to exit, or nothing for 23 questions: ");
            try {
                String input = inp.nextLine();
                if (input.isEmpty()) {
                    numQuestions = 23;
                    break;
                }

                numQuestions = Integer.parseInt(input);

                if (numQuestions == 0) {
                    System.out.println(inp);
                    System.out.println("Exiting...");
                    System.exit(0);
                }

                if (numQuestions < 0) {
                    throw new IllegalArgumentException();
                }
                break;
            }

            catch (Exception e) {
                System.out.println("Invalid input. Please enter a positive integer.");
            }
        }

        String latexContent = generateLatexContent();
        System.out.println("content generated");


        try {

            /* Handle directories for exported files.
               If the directory does not exist, create it */

            // Create tex_output directory if it does not exist
            Path tex_dir_path = Paths.get("tex_output");
            Files.createDirectories(tex_dir_path);

            // Create pdf_output directory if it does not exist
            Path pdf_dir_path = Paths.get("pdf_output");
            Files.createDirectories(pdf_dir_path);

            // Set file names

            String baseFileName = "worksheet";
            String tExt = ".tex";
            String pExt = ".pdf";

            String fileName = baseFileName;


            int i = 0;
            while (Files.exists(tex_dir_path.resolve(fileName + tExt)) || Files.exists(pdf_dir_path.resolve(fileName + pExt))) {
                i++;
                fileName = baseFileName + "_" + i;
            }

            String latexFileName = tex_dir_path + "/" + fileName + tExt;
            String pdfFileName = pdf_dir_path + "/" + fileName + pExt;

            // Save LaTeX content to a .tex file
            createTex(latexFileName, latexContent);

            // Compile tex file to PDF
            pdfCompiler(latexFileName, pdf_dir_path.toString());

            System.out.println("Success: " + pdfFileName);
        }

        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
        String questionFormat = "\\item{\\large\\hspace{20pt} $x \\times y =$}";
        Random random = new Random();
        StringBuilder questionsBuilder = new StringBuilder();




        // Generate correct number of questions
        for (int i = 0; i < numQuestions; i++) {
            int operand1 = random.nextInt(12) + 1;  // Random number between 1 and 12
            int operand2 = random.nextInt(12) + 1;  // as above

            questionsBuilder.append(questionFormat.replace("x", String.valueOf(operand1)).replace("y", String.valueOf(operand2)));
            questionsBuilder.append("\n");
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

    private static void pdfCompiler(String latexFileName, String pdfOutputDir) throws InterruptedException, IOException {
        Path absLatexFilePath = Paths.get(latexFileName).toAbsolutePath();
        ProcessBuilder processBuilder = new ProcessBuilder("pdflatex", absLatexFilePath.toString());
        processBuilder.directory(new File(pdfOutputDir));
        processBuilder.redirectErrorStream(true); // Merge standard output and error streams
        Process process = processBuilder.start();
    }

}
