package ppsoft1991.decompier;

public class Printer implements org.jd.core.v1.api.printer.Printer {
    protected static final String TAB = "   ";
    protected static final String NEWLINE = "\n";

    protected int indentationCount = 0;
    protected StringBuilder sb = new StringBuilder();

    @Override public String toString() { return sb.toString(); }

    @Override public void start(int maxLineNumber, int majorVersion, int minorVersion) {}
    @Override public void end() {}

    @Override public void printText(String text) { sb.append(text); }
    @Override public void printNumericConstant(String constant) { sb.append(constant); }
    @Override public void printStringConstant(String constant, String ownerInternalName) { sb.append(constant); }
    @Override public void printKeyword(String keyword) { sb.append(keyword); }
    @Override public void printDeclaration(int type, String internalTypeName, String name, String descriptor) { sb.append(name); }
    @Override public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) { sb.append(name); }

    @Override public void indent() { this.indentationCount++; }
    @Override public void unindent() { this.indentationCount--; }

    @Override public void startLine(int lineNumber) { for (int i=0; i<indentationCount; i++) sb.append(TAB); }
    @Override public void endLine() { sb.append(NEWLINE); }
    @Override public void extraLine(int count) { while (count-- > 0) sb.append(NEWLINE); }

    @Override public void startMarker(int type) {}
    @Override public void endMarker(int type) {}
};