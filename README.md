# TimesTablesGenerator


![Made with Java](https://forthebadge.com/images/badges/made-with-java.svg "Made with Java")


A program that generates LaTeX formatted worksheets for practicing times tables.

## Requirements
- Java (JDK 11 or later)
- LaTeX Distribution (TeX Live, MiKTeX, etc.)
- pdflatex (included in LaTeX distributions)
  - Less common tex packages: enumerate, titling

## Usage
1. Clone the repository:

```bash
git clone https://github.com/siddharth-shringarpure/TimesTablesGenerator.git
cd times-tables-generator
```

2. Compile and run the Java program:

```bash
javac Main.java; java Main
```

3. The program will generate a LaTeX file and compile it into a PDF (worksheet.pdf). A success message will be displayed in the console once complete.


## Customisation

You can customise the generated worksheets by modifying the `preamble.tex` file for LaTeX styling and adjusting parameters in the Main.java file, such as the number of questions generated.


## Planned Improvements
- Add support for custom number of questions
- Add support for output in a separate directory
  - Add support for saving multiple worksheets (currently overwrites previous worksheet)
- Add support for user arguments:
  - Number of questions, worksheets, etc.
- Remove temporary files after compilation