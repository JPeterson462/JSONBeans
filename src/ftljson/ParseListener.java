package ftljson;

public interface ParseListener {
    void beginObject();

    void endObject();

    void booleanLiteral(boolean value);

    void doubleLiteral(double value);

    void longLiteral(long value);

    void stringLiteral(String value);

    void nullLiteral();

    void beginObjectEntry(String value);

    void beginList();

    void endList();
}
