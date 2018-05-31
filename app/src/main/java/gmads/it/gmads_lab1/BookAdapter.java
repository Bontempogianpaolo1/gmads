package gmads.it.gmads_lab1;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import gmads.it.gmads_lab1.Chat.constants.AppConstants;
import gmads.it.gmads_lab1.model.Book;
import gmads.it.gmads_lab1.model.Request;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {

    private Context mContext;
    private List<Book> bookList;
    private List<String> booksRequested = new LinkedList<String>();
    Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    Index algoIndex = algoClient.getIndex("requests");
    Gson gson = new Gson();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, owner, rating, distance;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            owner = (TextView) view.findViewById(R.id.owner);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //mettere anche rating book

            //metere distanza
            distance = (TextView) view.findViewById(R.id.distance);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public void setbooks(List<Book> books){
        bookList.clear();
        bookList.addAll(books);
        this.notifyDataSetChanged();
    }
    public BookAdapter(Context mContext, List<Book> bookList) {
        this.mContext = mContext;
        this.bookList = bookList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_card, parent, false);
        FirebaseManagement.getDatabase().getReference()
                .child("users")
                .child(FirebaseManagement.getUser().getUid())
                .child("myRequests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> dataList = dataSnapshot.getChildren();

                        for(Iterator<DataSnapshot> iterator = dataList.iterator(); iterator.hasNext(); ){
                            booksRequested.add(iterator.next().getValue(ReferenceRequest.class).getBookid());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Book book = bookList.get(position);
        //titolo libro
        holder.title.setText(book.getTitle());
        //owner
        holder.owner.setText(R.string.of);
        holder.owner.append(": " +book.getNomeproprietario());
        //distanza
        holder.distance.setText(String.valueOf(book.getDistance()/1000));
        holder.distance.append(" Km");
        //rating
        // loading album cover using Glide library
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_book);
        requestOptions.error(R.drawable.default_book);
        //if(book.getUrlimage()!=null && book.getUrlimage().length()!=0){
            Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(book.getUrlimage()).into(holder.thumbnail);
       // }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowBook.class);
                intent.putExtra("book_id", book.getBId());
                mContext.startActivity(intent);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_book, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position = 0;

        public MyMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_prenota:
                    if(bookList.get(position).getStato() == AppConstants.AVAILABLE &&
                            !booksRequested.contains(bookList.get(position).getBId()) ) {
                        try {
                            Request request = new Request(AppConstants.NOT_REVIEWED, AppConstants.NOT_REVIEWED,
                                    AppConstants.PENDING, bookList.get(position).getOwner(),
                                    FirebaseManagement.getUser().getUid());

                            String rId = FirebaseManagement.getDatabase().getReference().child("requests").push().getKey();
                            FirebaseManagement.getDatabase().getReference().child("requests").child(rId).setValue(request);

                            ReferenceRequest referenceRequest = new ReferenceRequest(bookList.get(position).getTitle(),
                                    bookList.get(position).getUrlimage(),
                                    FirebaseManagement.getUser().getDisplayName(),
                                    rId, bookList.get(position).getBId());

                            FirebaseManagement.getDatabase().getReference().
                                    child("users").
                                    child(FirebaseManagement.getUser().getUid()).
                                    child("myRequests").
                                    child(rId).setValue(referenceRequest);

                            FirebaseManagement.getDatabase().getReference().
                                    child("users").
                                    child(bookList.get(position).getOwner()).
                                    child("othersRequests").
                                    child(bookList.get(position).getBId()).setValue(referenceRequest);

                            algoIndex.addObjectAsync(new JSONObject(gson.toJson(referenceRequest)),null);
                            //bookList.get(position).setStato(AppConstants.NOT_AVAILABLE);
                            Toast.makeText(mContext, "Book added", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(mContext, "Exception Occurred", Toast.LENGTH_SHORT).show();
                            e.getMessage();
                            Log.d("error",e.toString());
                        }
                        return true;
                    }
                    else{
                        Toast.makeText(mContext, "Book not available or already requested.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                case R.id.action_viewP:
                    Intent intent = new Intent(mContext, ShowUserProfile.class);
                    intent.putExtra("userId", bookList.get(position).getOwner());
                    mContext.startActivity(intent);
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

}