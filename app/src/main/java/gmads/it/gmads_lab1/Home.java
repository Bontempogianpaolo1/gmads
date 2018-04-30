package gmads.it.gmads_lab1;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout ctl = findViewById(R.id.collapsing_toolbar);

        //SearchView mSearchView = (SearchView) findViewById(R.id.searchView); // initiate a search view
        //mSearchView.attachNavigationDrawerToMenuButton(findViewById(R.id.drawer_layout));
        initCollapsingToolbar();

        //mSearchView.setIconifiedByDefault(false);  // set the default or resting state of the search field

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        bookList = new ArrayList<>();
        adapter = new BookAdapter(this, bookList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareBooks();

        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public boolean onQueryTextSubmit(String query) {
        // do something on text submit
        //parte la query di gogo
        return false;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

     /*mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
        @Override
        public void onSearchTextChanged(String oldQuery, final String newQuery) {

            //get suggestions based on newQuery

            //pass them on to the search view
            mSearchView.swapSuggestions(newSuggestions);
        }
    });*/

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few albums for testing
     */
    private void prepareBooks() {

        Book b = new Book ("1", "","Harry Potter", "", "http://books.google.com/books/content?id=oPBGnwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pinco");
        bookList.add(b);
        b = new Book ("2", "","Signore degli Anelli", "", "http://books.google.com/books/content?id=j1TNygAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pallino");
        bookList.add(b);
        b = new Book ("3", "","Novecento", "", "http://books.google.com/books/content?id=bVx9NAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Tizio");
        bookList.add(b);
        b = new Book ("4", "","La ragazza del treno", "", "http://books.google.com/books/content?id=IUo3rgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Caio");
        bookList.add(b);
        b = new Book ("1", "","Harry Potter", "", "http://books.google.com/books/content?id=oPBGnwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pinco");
        bookList.add(b);
        b = new Book ("2", "","Signore degli Anelli", "", "http://books.google.com/books/content?id=j1TNygAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pallino");
        bookList.add(b);
        b = new Book ("3", "","Novecento", "", "http://books.google.com/books/content?id=bVx9NAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Tizio");
        bookList.add(b);
        b = new Book ("4", "","La ragazza del treno", "", "http://books.google.com/books/content?id=IUo3rgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Caio");
        bookList.add(b);
        b = new Book ("1", "","Harry Potter", "", "http://books.google.com/books/content?id=oPBGnwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pinco");
        bookList.add(b);
        b = new Book ("2", "","Signore degli Anelli", "", "http://books.google.com/books/content?id=j1TNygAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pallino");
        bookList.add(b);
        b = new Book ("3", "","Novecento", "", "http://books.google.com/books/content?id=bVx9NAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Tizio");
        bookList.add(b);
        b = new Book ("4", "","La ragazza del treno", "", "http://books.google.com/books/content?id=IUo3rgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Caio");
        bookList.add(b);
        b = new Book ("1", "","Harry Potter", "", "http://books.google.com/books/content?id=oPBGnwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pinco");
        bookList.add(b);
        b = new Book ("2", "","Signore degli Anelli", "", "http://books.google.com/books/content?id=j1TNygAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pallino");
        bookList.add(b);
        b = new Book ("3", "","Novecento", "", "http://books.google.com/books/content?id=bVx9NAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Tizio");
        bookList.add(b);
        b = new Book ("4", "","La ragazza del treno", "", "http://books.google.com/books/content?id=IUo3rgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Caio");
        bookList.add(b);

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}