package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {

    private Context mContext;
    private List<Book> bookList;

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

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Book book = bookList.get(position);
        //titolo libro
        holder.title.setText(book.getTitle());
        //owner
        holder.owner.setText("di ");
        holder.owner.append( book.getNomeproprietario());
        //distanza
        holder.distance.setText(String.valueOf(book.getDistance()/1000));
        holder.distance.append(" Km");
        //rating
        // loading album cover using Glide library
        Glide.with(mContext).load(book.getUrlimage()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookPage.class);
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
                    Toast.makeText(mContext, "Book added", Toast.LENGTH_SHORT).show();
                    return true;
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