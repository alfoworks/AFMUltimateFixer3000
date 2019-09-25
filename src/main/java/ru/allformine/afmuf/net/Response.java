package ru.allformine.afmuf.net;

public class Response {
    public String response;
    public int responseCode;

    public Response(String response) {
        this.response = response;
        this.responseCode = 200;
    }

    Response(String response, int responseCode) {
        this.response = response;
        this.responseCode = responseCode;
    }

    public Response(int responseCode) {
        this.response = null;
        this.responseCode = responseCode;
    }

    public String toString() {
        return "Response{\"response\"=" + this.response + ",\"responseCode\"=" + this.responseCode + "}";
    }
}
