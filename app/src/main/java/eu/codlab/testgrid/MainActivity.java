package eu.codlab.testgrid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.recyclercolumnadaptable.IRecyclerColumnsListener;
import eu.codlab.recyclercolumnadaptable.RecyclerColumnsWithContentView;
import eu.codlab.recyclercolumnadaptable.inflater.AbstractItemInflater;
import eu.codlab.recyclercolumnadaptable.item.ContentItem;

public class MainActivity extends AppCompatActivity
        implements AbstractItemInflater<ColumnsNewItemHolder>,
        IRecyclerColumnsListener {

    private List<ContentItem> _items = new ArrayList<>();
    private RecyclerColumnsWithContentView _grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 255; i++) _items.add(new ContentItem(i));

        setContentView(R.layout.activity_main);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_grid.isShowingContent()) {
                    _grid.hideContent();
                } else {
                    _grid.showContent(12);
                }
            }
        });
        _grid = (RecyclerColumnsWithContentView) findViewById(R.id.grid);

        _grid.setRecyclerColumnsListener(this);
        _grid.setRecyclerAdapter(this);
    }

    @Override
    public ColumnsNewItemHolder onCreateViewHolder(ViewGroup parent) {
        return new ColumnsNewItemHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_content, parent, false));
    }

    @Override
    public void onBindViewHolder(final ColumnsNewItemHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Click on " + holder.getItem().getPosition(),
                        Toast.LENGTH_SHORT).show();

                _grid.showContent(holder.getItem().getPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    @Override
    public ContentItem getContentItemAt(int position) {
        return _items.get(position);
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public View getHeader(ViewGroup parent) {
        return LayoutInflater.from(this).inflate(R.layout.sample_blue_view, parent, false);
    }

    @Override
    public boolean hasFooter() {
        return true;
    }

    @NonNull
    @Override
    public View getFooter(@NonNull ViewGroup parent) {
        return LayoutInflater.from(this).inflate(R.layout.sample_blue_view, parent, false);
    }

    @Override
    public void onHideContent(ViewGroup content) {
        Toast.makeText(this, "Hide content", Toast.LENGTH_SHORT).show();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CONTENT");

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onShowContent(ViewGroup content) {
        Toast.makeText(this, "Show content", Toast.LENGTH_SHORT).show();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(content.getId(), new BlankFragment(), "CONTENT")
                .commit();
    }
}
