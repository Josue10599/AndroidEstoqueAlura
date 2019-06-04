package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";

    private ListaProdutosAdapter adapter;
    private ProdutoRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);
        repository = new ProdutoRepository(this);
        configuraListaProdutos();
        configuraFabSalvaProduto();
        buscaProdutos();
    }

    private void buscaProdutos() {
        repository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallback<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> produto) {
                atualizaLista(produto);
            }

            @Override
            public void quandoFalhar(String erro) {
                apresentaErro(pegaString(R.string.falha_atualiza));
            }
        });
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(this::removeProduto);
    }

    private void removeProduto(int posicao, Produto produtoSelecionado) {
        repository.remove(produtoSelecionado, new ProdutoRepository.DadosCarregadosCallback<Void>() {
            @Override
            public void quandoSucesso(Void produto) {
                adapter.remove(posicao);
            }

            @Override
            public void quandoFalhar(String erro) {
                apresentaErro(pegaString(R.string.falha_deletar));
            }
        });
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto;
        fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, this::salvaProduto).mostra();
    }

    private void salvaProduto(Produto produtoCriado) {
        repository.salva(produtoCriado,
                new ProdutoRepository.DadosCarregadosCallback<Produto>() {
                    @Override
                    public void quandoSucesso(Produto produto) {
                        adapter.adiciona(produto);
                    }

                    @Override
                    public void quandoFalhar(String erro) {
                        apresentaErro(pegaString(R.string.falha_inserir));
                    }
                });
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoEditado -> editaProduto(posicao, produtoEditado))
                .mostra();
    }

    private void editaProduto(int posicao, Produto produtoEditado) {
        repository.edita(produtoEditado,
                new ProdutoRepository.DadosCarregadosCallback<Produto>() {
                    @Override
                    public void quandoSucesso(Produto produto) {
                        adapter.edita(posicao, produto);
                    }

                    @Override
                    public void quandoFalhar(String erro) {
                        apresentaErro(pegaString(R.string.falha_remove));
                    }
                });
    }

    private String pegaString(int p) {
        return getString(p);
    }

    private void atualizaLista(List<Produto> produtos) {
        adapter.atualiza(produtos);
    }

    private void apresentaErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}
