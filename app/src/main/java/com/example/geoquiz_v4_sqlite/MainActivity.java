package com.example.geoquiz_v4_sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.example.geoquiz_v4_sqlite.database.RespostaDbHelper;

/*
  Modelo de projeto para a Atividade 1.
  Será preciso adicionar o cadastro das respostas do usuário ao Quiz, conforme
  definido no Canvas.

  GitHub: https://github.com/udofritzke/GeoQuiz
 */

public class MainActivity extends AppCompatActivity {
    private Button mBotaoVerdadeiro;
    private Button mBotaoFalso;
    private Button mBotaoProximo;
    private Button mBotaoMostra;
    private Button mBotaoDeleta;
    private TextView mTextViewQuestoesArmazenadas;


    private Button mBotaoCola;

    private TextView mTextViewQuestao;
    private static final String TAG = "QuizActivity";
    private static final String CHAVE_INDICE = "INDICE";
    private static final int CODIGO_REQUISICAO_COLA = 0;

    private Questao[] mBancoDeQuestoes = new Questao[]{
            new Questao(R.string.questao_suez, true),
            new Questao(R.string.questao_alemanha, false)
    };

    QuestaoDB mQuestoesDb;

    private int mIndiceAtual = 0;

    private boolean mEhColador;

    @Override
    protected void onCreate(Bundle instanciaSalva) {
        super.onCreate(instanciaSalva);
        setContentView(R.layout.activity_main);
        //Log.d(TAG, "onCreate()");
        if (instanciaSalva != null) {
            mIndiceAtual = instanciaSalva.getInt(CHAVE_INDICE, 0);
        }

        mTextViewQuestao = (TextView) findViewById(R.id.view_texto_da_questao);
        atualizaQuestao();

        mBotaoVerdadeiro = (Button) findViewById(R.id.botao_verdadeiro);
        // utilização de classe anônima interna
        mBotaoVerdadeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(true);
            }
        });

        mBotaoFalso = (Button) findViewById(R.id.botao_falso);
        mBotaoFalso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(false);
            }
        });
        mBotaoProximo = (Button) findViewById(R.id.botao_proximo);
        mBotaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIndiceAtual = (mIndiceAtual + 1) % mBancoDeQuestoes.length;
                mEhColador = false;
                atualizaQuestao();
            }
        });

        mBotaoCola = (Button) findViewById(R.id.botao_cola);
        mBotaoCola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inicia ColaActivity
                // Intent intent = new Intent(MainActivity.this, ColaActivity.class);
                boolean respostaEVerdadeira = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
                Intent intent = ColaActivity.novoIntent(MainActivity.this, respostaEVerdadeira);
                //startActivity(intent);
                startActivityForResult(intent, CODIGO_REQUISICAO_COLA);
            }
        });


        //Cursor cur = mQuestoesDb.queryQuestao ("_id = ?", val);////(null, null);
        //String [] val = {"1"};
        mBotaoMostra = findViewById(R.id.botao_mostra_questoes);
        mBotaoMostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarRespostas();
            }
        });

        mBotaoDeleta = findViewById(R.id.botao_deleta);
        mBotaoDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletarRespostas();
            }
        });
    }

    private void atualizaQuestao() {
        int questao = mBancoDeQuestoes[mIndiceAtual].getTextoRespostaId();
        mTextViewQuestao.setText(questao);
    }

    private void verificaResposta(boolean respostaPressionada) {
        boolean respostaCorreta = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
        int idMensagemResposta;

        // Julgamento especial para colador
        if (mEhColador) {
            idMensagemResposta = R.string.toast_julgamento;
        } else {
            idMensagemResposta = (respostaPressionada == respostaCorreta)
                    ? R.string.toast_correto
                    : R.string.toast_incorreto;
        }

        // Gera um "UUID" simples baseado na posição da questão
        String uuid = "questao_" + mIndiceAtual;

        // Chama o método que salva no banco
        salvarResposta(uuid, respostaPressionada == respostaCorreta, respostaPressionada, mEhColador);

        // Mostra feedback
        Toast.makeText(this, idMensagemResposta, Toast.LENGTH_SHORT).show();

        // Resetar "colou" para a próxima questão
        mEhColador = false;
    }

    private void salvarResposta(String uuid, boolean respostaCorreta, boolean respostaOferecida, boolean colou) {
        RespostaDbHelper dbHelper = new RespostaDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("uuid", uuid);
        values.put("resposta_correta", respostaCorreta ? 1 : 0);
        values.put("resposta_oferecida", respostaOferecida ? "verdadeiro" : "falso");
        values.put("colou", colou ? 1 : 0);

        long id = db.insert("respostas", null, values);
        Log.d("DB", "Resposta salva com ID: " + id);

        db.close();
    }

    private void mostrarRespostas() {
        RespostaDbHelper dbHelper = new RespostaDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "respostas",
                null, // todas as colunas
                null, null, null, null,
                "id DESC" // ordena do mais recente para o mais antigo
        );

        if (mTextViewQuestoesArmazenadas == null) {
            mTextViewQuestoesArmazenadas = findViewById(R.id.texto_questoes_a_apresentar);
        }

        StringBuilder sb = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
                int correta = cursor.getInt(cursor.getColumnIndex("resposta_correta"));
                String oferecida = cursor.getString(cursor.getColumnIndex("resposta_oferecida"));
                int colou = cursor.getInt(cursor.getColumnIndex("colou"));

                sb.append("Questão: ").append(uuid).append("\n")
                        .append("Resposta oferecida: ").append(oferecida).append("\n")
                        .append("Correta: ").append(correta == 1 ? "Sim" : "Não").append("\n")
                        .append("Colou: ").append(colou == 1 ? "Sim" : "Não").append("\n")
                        .append("-----------------------------\n");

            } while (cursor.moveToNext());
        } else {
            sb.append("Nenhuma resposta armazenada.");
        }

        mTextViewQuestoesArmazenadas.setText(sb.toString());
        cursor.close();
        db.close();
    }



    @Override
    public void onSaveInstanceState(Bundle instanciaSalva) {
        super.onSaveInstanceState(instanciaSalva);
        Log.i(TAG, "onSaveInstanceState()");
        instanciaSalva.putInt(CHAVE_INDICE, mIndiceAtual);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent dados) {
        super.onActivityResult(codigoRequisicao, codigoResultado, dados);
        if (codigoResultado != Activity.RESULT_OK) {
            return;
        }
        if (codigoRequisicao == CODIGO_REQUISICAO_COLA) {
            if (dados == null) {
                return;
            }
            mEhColador = ColaActivity.foiMostradaResposta(dados);
        }
    }

    private void deletarRespostas() {
        RespostaDbHelper dbHelper = new RespostaDbHelper(getApplicationContext()); // CORRIGIDO
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int linhasAfetadas = db.delete("respostas", null, null);
        Log.d("DB", "Respostas deletadas: " + linhasAfetadas);

        db.close();

        if (mTextViewQuestoesArmazenadas == null) {
            mTextViewQuestoesArmazenadas = findViewById(R.id.texto_questoes_a_apresentar);
        }
        mTextViewQuestoesArmazenadas.setText("Respostas apagadas.");
    }
}



