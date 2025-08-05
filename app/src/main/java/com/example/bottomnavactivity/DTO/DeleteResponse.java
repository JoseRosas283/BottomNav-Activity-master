package com.example.bottomnavactivity.DTO;

import com.google.gson.annotations.SerializedName;

public class DeleteResponse {
    @SerializedName("mensaje")
    private String mensaje;

    public String getMensaje() {
        return mensaje;
    }
}
