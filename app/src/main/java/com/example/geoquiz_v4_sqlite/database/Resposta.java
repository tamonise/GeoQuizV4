package com.example.geoquiz_v4_sqlite.database;

public class Resposta {
    public String uuid;
    public int respostaCorreta;
    public String respostaOferecida;
    public int colou;

    public Resposta(String uuid, int respostaCorreta, String respostaOferecida, int colou) {
        this.uuid = uuid;
        this.respostaCorreta = respostaCorreta;
        this.respostaOferecida = respostaOferecida;
        this.colou = colou;
    }
}