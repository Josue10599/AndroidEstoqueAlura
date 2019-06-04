package br.com.alura.estoque.retrofit.callback;

import android.content.Context;

import br.com.alura.estoque.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallbackSemRetorno implements Callback<Void> {

    private final RespostaCallback callback;
    private final Context context;

    public CallbackSemRetorno(Context context, RespostaCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
            callback.quandoSucesso();
        } else {
            callback.quandoFalhar(context.getString(R.string.falha_resposta));
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<Void> call, Throwable t) {
        callback.quandoFalhar(context.getString(R.string.falha_comunicacao) + t.getLocalizedMessage());
    }

    public interface RespostaCallback {
        void quandoSucesso();

        void quandoFalhar(String erro);
    }
}
