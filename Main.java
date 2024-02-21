import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static int numQuestions;
    public static void main(String[] args) {

        Scanner inp = new Scanner(System.in);

        while (true) {
            System.out.println("Enter the number of questions, or 0 to exit: ");
            try {
                numQuestions = Integer.parseInt(inp.nextLine());

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

        try {
            String latexFileName = "worksheet.tex";
            String pdfFileName = "worksheet.pdf";

            // Save LaTeX content to a .tex file
            createTex(latexFileName, latexContent);

            // Compile tex file to PDF
            pdfCompiler(latexFileName);

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


    public static String questionGenerator() {
        String questionFormat = "\\item{\\large\\hspace{20pt} $x \\times y =$}";
        Random random = new Random();
        StringBuilder questionsBuilder = new StringBuilder();

        // Generate n questions
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
                questionGenerator() +
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

    private static void pdfCompiler(String latexFileName) throws InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("pdflatex", latexFileName);
        processBuilder.start();
    }

}
