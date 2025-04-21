package com.example.geoquiz_v4_sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ColaActivity extends AppCompatActivity {

    private static final String EXTRA_REPOSTA_E_VERDADEIRA = "com.example.geoquiz_v3.reposta_e_verdadeira";
    private static final String EXTRA_RESPOSTA_FOI_MOSTRADA= "com.example.geoquiz_v3.reposta_foi_mostrada";

    private static final String CHAVE_COLOU = "CHAVE_COLOU";

    private boolean mRespostaEVerdadeira;
    private TextView mTextViewResposta;
    private Button mBotaoMostraResposta;
    private boolean mColou = false;  // registra se o usuário viu ou não a reposta (para recuperar o estado em caso de rotação
                                     // do dispositivo)
    @Override
    protected void onCreate(Bundle instanciaSalva) {
        super.onCreate(instanciaSalva);
        setContentView(R.layout.activity_cola);

        /* recupera estado da activivty em caso de rotação do dispositivo */
        if (instanciaSalva != null) {
            mColou = instanciaSalva.getBoolean(CHAVE_COLOU);
            setRespostaFoiMostrada(mColou);
        }

        mRespostaEVerdadeira = getIntent().getBooleanExtra(EXTRA_REPOSTA_E_VERDADEIRA, false);

        mTextViewResposta = (TextView) findViewById(R.id.view_texto_resposta);
        mBotaoMostraResposta = (Button) findViewById(R.id.botao_mostra_resposta);
        mBotaoMostraResposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mColou = true;
                if (mRespostaEVerdadeira) {
                    mTextViewResposta.setText(R.string.botao_verdadeiro);
                } else
                    mTextViewResposta.setText(R.string.botao_falso);
                setRespostaFoiMostrada(true);
            }
        });
    }

    private void setRespostaFoiMostrada(boolean repostaFoiMostrada) {
        Intent dados = new Intent();
        dados.putExtra(EXTRA_RESPOSTA_FOI_MOSTRADA, repostaFoiMostrada);
        setResult(RESULT_OK, dados);
    }

    public static Intent novoIntent(Context packageContext, boolean respostaEVderdadeira) {
        Intent intent = new Intent(packageContext, ColaActivity.class);
        intent.putExtra(EXTRA_REPOSTA_E_VERDADEIRA, respostaEVderdadeira);
        return intent;
    }
    public static boolean foiMostradaResposta(Intent result){
        return result.getBooleanExtra(EXTRA_RESPOSTA_FOI_MOSTRADA, false);
    }

    /* salva estado da activivty em caso de rotação do dispositivo */
    public void onSaveInstanceState(Bundle instanciaSalva) {
        super.onSaveInstanceState(instanciaSalva);
        //Log.i(TAG, "onSaveInstanceState()");
        instanciaSalva.putBoolean(CHAVE_COLOU, mColou);
    }
}