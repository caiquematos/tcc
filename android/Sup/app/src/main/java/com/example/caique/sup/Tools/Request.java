package com.example.caique.sup.Tools;

import org.json.JSONObject;

/**
 * Created by caique on 05/12/14.
 */
public class Request {
    private String type;
    private JSONObject response;

    public void Request(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }
}
