package com.example.geoquiz_v4_sqlite;

import java.util.UUID;

public class Questao {
    private UUID mId;
    private int mTextoRespostaId;
    private boolean mRespostaCorreta;

    public Questao(int TextoRespostaId, boolean RespostaCorreta) {
        this.mTextoRespostaId = TextoRespostaId;
        this.mRespostaCorreta = RespostaCorreta;
        mId = UUID.randomUUID();
    }

    UUID getId(){return mId;};

    public int getTextoRespostaId() {
        return mTextoRespostaId;
    }

    public void setTextoRespostaId(int textoRespostaId) {
        mTextoRespostaId = textoRespostaId;
    }

    public boolean isRespostaCorreta() {
        return mRespostaCorreta;
    }

    public void setRespostaCorreta(boolean respostaCorreta) {
        mRespostaCorreta = respostaCorreta;
    }


}
