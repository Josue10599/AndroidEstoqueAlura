package br.com.alura.estoque.repository;

import android.content.Context;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.callback.CallbackComRetorno;
import br.com.alura.estoque.retrofit.callback.CallbackSemRetorno;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import retrofit2.Call;

public class ProdutoRepository {

    private final ProdutoDAO dao;
    private final ProdutoService produtoService;
    private Context context;

    public ProdutoRepository(Context context) {
        this.dao = EstoqueDatabase.getInstance(context).getProdutoDAO();
        this.context = context;
        produtoService = new EstoqueRetrofit().getProdutoService();
    }

    public void buscaProdutos(DadosCarregadosCallback<List<Produto>> callback) {
        buscaProdutosOnline(callback);
    }

    private void buscaProdutosInternamente(DadosCarregadosCallback<List<Produto>> callback) {
        new BaseAsyncTask<>(dao::buscaTodos, callback::quandoSucesso).execute();
    }

    private void buscaProdutosOnline(DadosCarregadosCallback<List<Produto>> callback) {
        Call<List<Produto>> listCall = produtoService.buscaTodos();
        listCall.enqueue(new CallbackComRetorno<>(context, new CallbackComRetorno.RespostaCallback<List<Produto>>() {
                    @Override
                    public void quandoSucesso(List<Produto> resultado) {
                        salvaLista(resultado, callback);
                    }

                    @Override
                    public void quandoFalhar(String erro) {
                        buscaProdutosInternamente(callback);
                        callback.quandoFalhar(erro);
                    }
                })
        );
    }

    private void salvaLista(List<Produto> produtos, DadosCarregadosCallback<List<Produto>> callback) {
        new BaseAsyncTask<>(() -> {
            dao.salvaProdutos(produtos);
            return dao.buscaTodos();
        }, callback::quandoSucesso).execute();
    }

    public void salva(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = produtoService.salva(produto);
        call.enqueue(new CallbackComRetorno<>(context, new CallbackComRetorno.RespostaCallback<Produto>() {
                    @Override
                    public void quandoSucesso(Produto resultado) {
                        salvaInternamente(resultado, callback);
                    }

                    @Override
                    public void quandoFalhar(String erro) {
                        callback.quandoFalhar(erro);
                    }
                })
        );
    }

    private void salvaInternamente(Produto produto, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produto);
            return dao.buscaProduto(id);
        }, callback::quandoSucesso).execute();
    }

    public void edita(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = produtoService.edita(produto.getId(), produto);
        call.enqueue(new CallbackComRetorno<>(context, new CallbackComRetorno.RespostaCallback<Produto>() {
            @Override
            public void quandoSucesso(Produto resultado) {
                editaInternamente(resultado, callback);
            }

            @Override
            public void quandoFalhar(String erro) {
                callback.quandoFalhar(erro);
            }
        }));
    }

    private void editaInternamente(Produto produtoEditado, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            dao.atualiza(produtoEditado);
            return produtoEditado;
        }, callback::quandoSucesso).execute();
    }

    public void remove(Produto produtoSelecionado, DadosCarregadosCallback<Void> callback) {
        removeOnline(produtoSelecionado, callback);
    }

    private void removeOnline(Produto produtoSelecionado, DadosCarregadosCallback<Void> callback) {
        Call<Void> call = produtoService.remove(produtoSelecionado.getId());
        call.enqueue(new CallbackSemRetorno(context, new CallbackSemRetorno.RespostaCallback() {
                    @Override
                    public void quandoSucesso() {
                        removeInternamente(produtoSelecionado, callback);
                    }

                    @Override
                    public void quandoFalhar(String erro) {
                        callback.quandoFalhar(erro);
                    }
                })
        );
    }

    private void removeInternamente(Produto produtoSelecionado, DadosCarregadosCallback<Void> callback) {
        new BaseAsyncTask<>(() -> {
            dao.remove(produtoSelecionado);
            return null;
        }, callback::quandoSucesso)
                .execute();
    }

    public interface DadosCarregadosCallback<T> {
        void quandoSucesso(T produto);

        void quandoFalhar(String erro);
    }
}
