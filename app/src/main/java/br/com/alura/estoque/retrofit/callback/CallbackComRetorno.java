package br.com.alura.estoque.retrofit.callback;

import android.content.Context;

import br.com.alura.estoque.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallbackComRetorno<T> implements Callback<T> {

    private final RespostaCallback<T> callback;
    private final Context context;

    public CallbackComRetorno(Context context, RespostaCallback<T> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            T resultado = response.body();
            if (resultado != null) {
                callback.quandoSucesso(resultado);
            }
        } else {
            callback.quandoFalhar(context.getString(R.string.falha_resposta));
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
        callback.quandoFalhar(context.getString(R.string.falha_comunicacao) + t.getLocalizedMessage());
    }

    public interface RespostaCallback<T> {
        void quandoSucesso(T resultado);

        void quandoFalhar(String erro);
    }
}
