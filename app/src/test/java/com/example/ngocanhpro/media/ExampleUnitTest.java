import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    // List of all dictionary words
    private List<QuizObject> dictionaryWords;
    private List<QuizObject> filteredList;
    // RecycleView adapter object
    private SimpleItemRecyclerViewAdapter mAdapter;
    // Search edit box
    private EditText searchBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dictionaryWords = getListItemData();
        filteredList = new ArrayList<QuizObject>();
        filteredList.addAll(dictionaryWords);
        searchBox = (EditText)findViewById(R.id.search_box);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.item_list);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        assert recyclerView != null;
        mAdapter = new SimpleItemRecyclerViewAdapter(filteredList);
        recyclerView.setAdapter(mAdapter);
        // search suggestions using the edittext widget
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    // create a custom RecycleViewAdapter class
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable {
        private List<QuizObject> mValues;
        private CustomFilter mFilter;
        public SimpleItemRecyclerViewAdapter(List<QuizObject> items) {
            mValues = items;
            mFilter = new CustomFilter(SimpleItemRecyclerViewAdapter.this);
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(String.valueOf(mValues.get(position).getId()));
            holder.mContentView.setText(mValues.get(position).getWord());
        }
        @Override
        public int getItemCount() {
            return mValues.size();
        }
        @Override
        public Filter getFilter() {
            return mFilter;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public QuizObject mItem;
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
        public class CustomFilter extends Filter {
            private SimpleItemRecyclerViewAdapter mAdapter;
            private CustomFilter(SimpleItemRecyclerViewAdapter mAdapter) {
                super();
                this.mAdapter = mAdapter;
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredList.clear();
                final FilterResults results = new FilterResults();
                if (constraint.length() == 0) {
                    filteredList.addAll(dictionaryWords);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final QuizObject mWords : dictionaryWords) {
                        if (mWords.getWord().toLowerCase().startsWith(filterPattern)) {
                            filteredList.add(mWords);
                        }
                    }
                }
                System.out.println("Count Number " + filteredList.size());
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                System.out.println("Count Number 2 " + ((List<QuizObject>) results.values).size());
                this.mAdapter.notifyDataSetChanged();
            }
        }
    }
    private List<QuizObject> getListItemData(){
        List<QuizObject> listViewItems = new ArrayList<QuizObject>();
        listViewItems.add(new QuizObject(1, "Apple", "Apple"));
        listViewItems.add(new QuizObject(2, "Orange", "Orange"));
        listViewItems.add(new QuizObject(3, "Banana", "Banana"));
        listViewItems.add(new QuizObject(4, "Grape", "Grape"));
        listViewItems.add(new QuizObject(5, "Mango", "Mango"));
        listViewItems.add(new QuizObject(6, "Pear", "Pear"));
        listViewItems.add(new QuizObject(7, "Pineapple", "Pineapple"));
        listViewItems.add(new QuizObject(8, "Strawberry", "Strawberry"));
        listViewItems.add(new QuizObject(9, "Coconut", "Coconut"));
        listViewItems.add(new QuizObject(10, "Almond", "Almond"));
        return listViewItems;
    }
}