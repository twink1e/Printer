# Usage

- Compile `javac Printer.java`
- Run `java Printer <input file> <output file>`, where `<input file>` is where the raw text is stored and `<output file>` is where the formatted text is stored
- To see the result, use a text editor to open `<output file>`. Sublime Text and Terminal may treat the file as a binary file

# Assumptions

1. The height of the paper is 100 lines including the last line of === as page break
2. The width of the paper id 105 chars
3. All the space, tab at the start of the line is ignored, i.e. no indentation
4. Tab is 4 spaces
5. Input files are all ascii encoded
