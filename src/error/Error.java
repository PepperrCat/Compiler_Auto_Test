package error;

public class Error {
    int line;
    ErrorType errorType;

    public Error(int line, ErrorType errorType) {
        this.line = line;
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return line +
                " " + errorType
                ;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}
