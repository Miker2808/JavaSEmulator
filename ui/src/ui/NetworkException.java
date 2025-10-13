package ui;

import java.io.IOException;

public class NetworkException extends IOException {
    Integer code;
    public NetworkException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public int getHttpCode(){
        return code;
    }


}
