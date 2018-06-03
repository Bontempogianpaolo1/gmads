package gmads.it.gmads_lab1.RequestPackage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import gmads.it.gmads_lab1.ReviewPackage.AddReview;
import gmads.it.gmads_lab1.constants.AppConstants;
import gmads.it.gmads_lab1.R;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    private Context mContext;
    private List<Request> reqList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, owner, bookname,stato;
        public ImageView button;

        MyViewHolder( View view ) {
            super(view);
            bookname =  view.findViewById(R.id.bookname);
            owner =  view.findViewById(R.id.ownername);
            stato = view.findViewById(R.id.stato);
            button= view.findViewById(R.id.addComment);
        }
    }

    public void setbooks(List<Request> requests){
        reqList.clear();
        reqList.addAll(requests);
        this.notifyDataSetChanged();
    }

    RequestAdapter( Context mContext, List<Request> requests ) {
        this.mContext = mContext;
        this.reqList = requests;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_request, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( @NonNull final MyViewHolder holder, int position) {
        Request request = reqList.get(holder.getAdapterPosition());
        //titolo libro
        holder.bookname.setText(request.getbName());
        //owner

        switch (reqList.get(holder.getAdapterPosition()).getRequestStatus()){
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
        holder.button.setOnClickListener(v -> {
            Intent i= new Intent(mContext,AddReview.class);
            i.putExtra("userid",reqList.get(holder.getAdapterPosition()).getOwnerId());
            i.putExtra("bookname",reqList.get(holder.getAdapterPosition()).getbName());
            mContext.startActivity(i);
        });
        holder.owner.setText(request.getOwnerName());
    }

    @Override
    public int getItemCount() {
        return reqList.size();
    }

}