
package gmads.it.gmads_lab1.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gmads.it.gmads_lab1.Book;
import gmads.it.gmads_lab1.BookAdapter;
import gmads.it.gmads_lab1.Home;
import gmads.it.gmads_lab1.R;

import static com.arlib.floatingsearchview.util.Util.dpToPx;

public class Home_1 extends Fragment {
    RecyclerView recycle;
    private BookAdapter adapter;
    private List<Book> bookList;

    public Home_1() {

        // Required empty public constructor
        //RecyclerView recyclerView = (RecyclerView)container.getChildAt((int)R.id.recycler_view);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new gmads.it.gmads_lab1.Home.GridSpacingItemDecoration(2, dpToPx(10), true));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(adapter);
    }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View root = inflater.inflate(R.layout.fragment_home_1, container, false);
        recycle = (RecyclerView) root.findViewById(R.id.recycler_view);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(getContext(), bookList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);

        recycle.setLayoutManager(mLayoutManager);
        recycle.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
        recycle.setItemAnimator(new DefaultItemAnimator());
        prepareBooks();
        recycle.setAdapter(adapter);

        // Inflate the layout for this fragment
        return root;
    }

    public BookAdapter getAdapter() {
        return adapter;
    }

    /**
     * Adding few albums for testing
     */
    private void prepareBooks() {

        Book b = new Book ("1", "","Harry Potter", "", "http://books.google.com/books/content?id=oPBGnwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pinco", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("2", "","Signore degli Anelli", "", "http://books.google.com/books/content?id=j1TNygAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pallino", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("3", "","Novecento", "", "http://books.google.com/books/content?id=bVx9NAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Tizio", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("4", "","La ragazza del treno", "", "http://books.google.com/books/content?id=IUo3rgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Caio", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("1", "","Harry Potter", "", "http://books.google.com/books/content?id=oPBGnwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pinco", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("2", "","Signore degli Anelli", "", "http://books.google.com/books/content?id=j1TNygAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pallino", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("3", "","Novecento", "", "http://books.google.com/books/content?id=bVx9NAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Tizio", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("4", "","La ragazza del treno", "", "http://books.google.com/books/content?id=IUo3rgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Caio", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("1", "","Harry Potter", "", "http://books.google.com/books/content?id=oPBGnwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pinco", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("2", "","Signore degli Anelli", "", "http://books.google.com/books/content?id=j1TNygAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pallino", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("3", "","Novecento", "", "http://books.google.com/books/content?id=bVx9NAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Tizio", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("4", "","La ragazza del treno", "", "http://books.google.com/books/content?id=IUo3rgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Caio", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("1", "","Harry Potter", "", "http://books.google.com/books/content?id=oPBGnwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pinco", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("2", "","Signore degli Anelli", "", "http://books.google.com/books/content?id=j1TNygAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Pallino", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("3", "","Novecento", "", "http://books.google.com/books/content?id=bVx9NAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Tizio", 10.0, 12.0);
        bookList.add(b);
        b = new Book ("4", "","La ragazza del treno", "", "http://books.google.com/books/content?id=IUo3rgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "", "", "", "", "Caio", 10.0, 12.0);
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

        public GridSpacingItemDecoration( int spanCount, int spacing, boolean includeEdge ) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets( Rect outRect, View view, RecyclerView parent, RecyclerView.State state ) {
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
    private int dpToPx( int dp ) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}