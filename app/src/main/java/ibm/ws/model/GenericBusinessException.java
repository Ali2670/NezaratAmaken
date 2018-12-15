package ibm.ws.model;

import java.sql.SQLException;

public class GenericBusinessException extends Exception {

    private static final String SQLERRORCODE = "SQLERROR.";
    private int errCode = -1;
    private Exception exception;


    public GenericBusinessException(String message) {
        super(message);
    }

    public GenericBusinessException(Exception cause) {
        super(cause);
        this.toApplicationException(cause);
    }

    public GenericBusinessException(Throwable cause) {
        super(cause);
        this.toApplicationException(cause);
    }

    public GenericBusinessException(String message, Exception cause) {
        super(message, cause);
        this.toApplicationException(cause);
    }

    public GenericBusinessException(int errcode) {
        this.errCode = errcode;
    }

    public GenericBusinessException(int errcode, Exception cause) {
        this.exception = cause;
        this.errCode = errcode;
        this.toApplicationException(cause);
    }

    public int getErrCode() {
        return this.errCode;
    }

    public Exception getException() {
        return this.exception;
    }

    private void toApplicationException(Throwable trble) {
        if (trble instanceof SQLException) {
            SQLException se = (SQLException) trble;
            switch (se.getErrorCode()) {
                case 1:
                    this.errCode = 1;
                    break;
                case 1400:
                    this.errCode = 1400;
                    break;
                case 2291:
                    this.errCode = 2291;
                    break;
                case 2292:
                    this.errCode = 2292;
                    break;
                case 12899:
                    this.errCode = 12899;
            }
        }

    }

    public String toApplicationMessage() {
        switch (this.errCode) {
            case 1:
                return "������� ���� ��� ���� ��� ������ ���";
            case 2291:
                return "������� ���� ���� ���� ��� �� ���� �������� ���� �����";
            case 2292:
                return "����� ��� ������� ���� �����";
            case 12899:
                return "��� ���� �� ������ ���� ��� ����� ������";
            default:
                return null;
        }
    }

    public String toApplicationMessageKey() {
        switch (this.errCode) {
            case 1:
                return "SQLERROR.1";
            case 1400:
                return "SQLERROR.1400";
            case 2291:
                return "SQLERROR.2291";
            case 2292:
                return "SQLERROR.2292";
            case 12899:
                return "SQLERROR.12899";
            default:
                return this.getExceptionKey();
        }
    }

    public Throwable getRootCause() {
        Throwable throwable;
        for (throwable = this.getCause(); throwable.getCause() != null; throwable = throwable.getCause()) {
            ;
        }

        return throwable;
    }

    public String getExceptionKey() {
        String exClass = this.getClass().getName();
        int p = exClass.lastIndexOf(46);
        if (p != -1) {
            exClass = exClass.substring(p + 1);
        }

        exClass = exClass + "." + this.getErrCode();
        return exClass;
    }
}
