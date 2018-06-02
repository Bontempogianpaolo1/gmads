package gmads.it.gmads_lab1;

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
import gmads.it.gmads_lab1.model.ReferenceRequest;
import gmads.it.gmads_lab1.model.Request;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    private Context mContext;
    private List<Request> reqList;
    private List<String> booksRequested = new LinkedList<String>();
    Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    Index algoIndex = algoClient.getIndex("requests");
    Gson gson = new Gson();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, owner, bookname,stato;
        public ImageView button;

        public MyViewHolder(View view) {
            super(view);
            bookname = (TextView) view.findViewById(R.id.bookname);
            owner = (TextView) view.findViewById(R.id.ownername);
            stato = view.findViewById(R.id.stato);
            button= view.findViewById(R.id.addComment);
        }
    }

    public void setbooks(List<Request> requests){
        reqList.clear();
        reqList.addAll(requests);
        this.notifyDataSetChanged();
    }

    public RequestAdapter( Context mContext, List<Request> requests) {
        this.mContext = mContext;
        this.reqList = requests;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_request, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Request request = reqList.get(position);
        //titolo libro
        holder.bookname.setText(request.getbName());
        //owner

        switch (reqList.get(position).getRequestStatus()){
            case AppConstants.ACCEPTED:
              holder.stato.setText(R.string.accepted);
              holder.button.setVisibility(View.VISIBLE);
              break;
            case AppConstants.REFUSED:
                holder.stato.setText(R.string.refused);
                holder.button.setVisibility(View.GONE);
                break;
            case AppConstants.PENDING:
                holder.stato.setText(R.string.pending);
                holder.button.setVisibility(View.GONE);
                break;
            case AppConstants.COMPLETED:
              holder.stato.setText(R.string.completed);
              holder.button.setVisibility(View.VISIBLE);
              break;
            default:
                  break;

        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent i= new Intent(mContext,AddReview.class);
                i.putExtra("userid",reqList.get(position).getOwnerId());
                i.putExtra("bookname",reqList.get(position).getbName());
                mContext.startActivity(i);
            }
        });
        holder.owner.setText(request.getOwnerName());
        //holder.stato.setText(request.getRequestStatus());
    }

    @Override
    public int getItemCount() {
        return reqList.size();
    }

}