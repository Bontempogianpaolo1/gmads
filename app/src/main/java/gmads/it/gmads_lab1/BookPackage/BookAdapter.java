package gmads.it.gmads_lab1.BookPackage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import gmads.it.gmads_lab1.constants.AppConstants;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.RequestPackage.SearchRequestsJsonParser;
import gmads.it.gmads_lab1.UserPackage.ShowUserProfile;
import gmads.it.gmads_lab1.RequestPackage.ReferenceRequest;
import gmads.it.gmads_lab1.RequestPackage.Request;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {

    private Context mContext;
    private List<Book> bookList;
    private List<Request> requestList=new ArrayList<>();
    private List<String> booksRequested = new LinkedList<String>();
    private Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    private Index algoIndex = algoClient.getIndex("requests");
    private Gson gson = new Gson();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, owner, rating, distance;
        public ImageView thumbnail, overflow;

        MyViewHolder( View view ) {
            super(view);
            title =  view.findViewById(R.id.title);
            owner = view.findViewById(R.id.owner);
            thumbnail =  view.findViewById(R.id.thumbnail);
            //mettere anche rating book

            //metere distanza
            distance =  view.findViewById(R.id.distance);
            overflow =  view.findViewById(R.id.overflow);
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
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

                        for (DataSnapshot aDataList : dataList) {
                            booksRequested.add(Objects.requireNonNull(aDataList.getValue(ReferenceRequest.class)).getBookid());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( @NonNull final MyViewHolder holder, int position) {
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

        holder.thumbnail.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ShowBook.class);
            intent.putExtra("book_id", book.getBId());
            mContext.startActivity(intent);
        });

        holder.overflow.setOnClickListener(view -> showPopupMenu(holder.overflow, position));
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
        private boolean alreadyRequested = false;
        private boolean completed = true;

        MyMenuItemClickListener( int position ) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_prenota:

                    // query per controlla se io ho gia mandato una richiesta per quel libro

                    Query query = new Query().setFilters("ownerId:" + bookList.get(position).getOwner() + " AND "
                                    + "renterId:" + FirebaseManagement.getUser().getUid() + " AND "
                                    + "bId:" + bookList.get(position).getBId()).setFilters("requestStatus:" + AppConstants.PENDING);

                    algoIndex.searchAsync(query, ( jsonObject, e ) -> {
                        if(e == null){

                            SearchRequestsJsonParser search= new SearchRequestsJsonParser();
                            Log.d("lista",jsonObject.toString());
                            requestList.addAll(search.parseResults(jsonObject));

                            if(requestList.size() != 0){
                                alreadyRequested = true;
                            }
                        }

                    });

                    if(bookList.get(position).getStato() == AppConstants.AVAILABLE && !alreadyRequested) {
                        try {
                            // aggiungo i dati su firebase
                            Request request = new Request("0", AppConstants.NOT_REVIEWED, AppConstants.NOT_REVIEWED,
                                    AppConstants.PENDING, bookList.get(position).getOwner(),
                                    bookList.get(position).getBId(), bookList.get(position).getTitle(), FirebaseManagement.getUser().getUid(), bookList.get(position).getNomeproprietario(),
                                    FirebaseManagement.getUser().getDisplayName(), bookList.get(position).getUrlimage(), null);

                            String rId = FirebaseManagement.getDatabase().getReference().child("requests").push().getKey();
                            request.setrId(rId);

                            algoIndex.addObjectAsync(new JSONObject(gson.toJson(request)), ( jsonObject, exception ) -> {
                                if(exception == null){
                                    try{
                                        Long id= jsonObject.getLong("objectID");
                                        request.setObjectID(id);

                                    }catch (Exception e){
                                        completed = false;
                                    }
                                    if(completed) {
                                        FirebaseManagement.getDatabase().getReference().child("requests").child(rId).setValue(request);
                                        FirebaseManagement.getDatabase().getReference()
                                                .child("users")
                                                .child(request.getOwnerId())
                                                .child("reqNotified")
                                                .setValue(true);
                                    }
                                }
                                else{
                                    Toast.makeText(mContext, "Error in algolia occurred", Toast.LENGTH_SHORT).show();
                                    exception.getMessage();
                                    Log.d("error",exception.toString());
                                    completed = false;

                                }
                            });

                            if(completed) {
                                Toast.makeText(mContext, "Book added", Toast.LENGTH_SHORT).show();
                            }
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