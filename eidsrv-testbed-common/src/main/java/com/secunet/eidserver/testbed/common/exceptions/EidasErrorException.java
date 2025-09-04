package com.secunet.eidserver.testbed.common.exceptions;

public class EidasErrorException extends Exception {
    private final String errorCode;

    public EidasErrorException(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String toString() {
        return this.errorCode;
    }

    @Override
    public int hashCode() {
        return this.errorCode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EidasErrorException)) return false;
        return ((EidasErrorException)obj).getErrorCode().equals(this.errorCode);
    }
}
