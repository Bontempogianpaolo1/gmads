package gmads.it.gmads_lab1;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gmads.it.gmads_lab1.fragments.Home_1;
import gmads.it.gmads_lab1.model.Book;

public class Request_2_myReq extends Fragment {
    RecyclerView recycle;
    private BookAdapter adapter;
    private List<Book> bookList;

    public Request_2_myReq() {

        //Required empty public constructor
        //RecyclerView recyclerView = (RecyclerView)container.getChildAt((int)R.id.recycler_view);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new gmads.it.gmads_lab1.Home.GridSpacingItemDecoration(2, dpToPx(10), true));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(adapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View root = inflater.inflate(R.layout.fragment_home_1, container, false);
        recycle = (RecyclerView) root.findViewById(R.id.recycler_view);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(getContext(), bookList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);

        recycle.setLayoutManager(mLayoutManager);
        //recycle.addItemDecoration(new Home_1.GridSpacingItemDecoration(2, dpToPx(10), true));
        recycle.setItemAnimator(new DefaultItemAnimator());
        prepareBooks();
        recycle.setAdapter(adapter);

        // Inflate the layout for this fragment
        return root;
    }

    public void updateData(List<Book> books){
        bookList.clear();
        bookList = books;
        adapter.notifyDataSetChanged();
    }

    public BookAdapter getAdapter() {
        return adapter;
    }

    /**
     * Adding few albums for testing
     */
    private void prepareBooks() {

        adapter.notifyDataSetChanged();
    }
}

