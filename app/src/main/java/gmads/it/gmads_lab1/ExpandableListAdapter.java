package gmads.it.gmads_lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import gmads.it.gmads_lab1.Chat.constants.AppConstants;
import gmads.it.gmads_lab1.Chat.glide.GlideApp;
import gmads.it.gmads_lab1.model.Book;
import gmads.it.gmads_lab1.model.Request;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Book> listHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Request>> listChild;

    private Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    private Index algoReqIndex = algoClient.getIndex("requests");
    private Index algoBookIndex = algoClient.getIndex("BookIndex");

    public ExpandableListAdapter(Context context, List<Book> listDataHeader,
                                 HashMap<String, List<Request>> listChildData) {
        this.context = context;
        this.listHeader = listDataHeader;
        this.listChild = listChildData;
    }

    @Override
    public Request getChild(int groupPosition, int childPosititon) {
        return this.listChild.get(this.listHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Request child = getChild(groupPosition, childPosition);
        final String childText =  child.getRenterName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_others_request, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.name);
        //CircleImageView civ = (CircleImageView) convertView.findViewById(R.id.ownerphoto);
        //settare foto libro se c'è DOPO AVER SETTATO QUELLA DI DEFAULT
        /*GlideApp.with(context)
                .load(R.drawable.default_picture)
                .centerCrop()
                .into(civ);*/
        txtListChild.setText(childText);

        ImageView bYes = convertView.findViewById(R.id.yes);
        bYes.setOnClickListener( v -> onClickYes(child));

        ImageView bNo = convertView.findViewById(R.id.no);
        bYes.setOnClickListener( v -> onClickNo(child));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listChild.get(this.listHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater linflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = linflater.inflate(R.layout.card_request_root, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.bookname);
        TextView number = (TextView) convertView.findViewById(R.id.number);
        CircleImageView civ = (CircleImageView) convertView.findViewById(R.id.bookphoto);
        //settare foto libro se c'è DOPO AVER SETTATO QUELLA DI DEFAULT
        GlideApp.with(context)
                .load(R.drawable.default_book)
                .centerCrop()
                .into(civ);
        //lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        //setto numerino richieste
        String count = String.valueOf(getChildrenCount(groupPosition));
        number.setText(String.valueOf(count));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void onClickYes(Request request){

        /*List<Request> algoliaRequests = new ArrayList<>();
        Request algoliaReq;
        Query query = new Query().setFilters("rId:" + request.getrId());

        algoReqIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e == null){

                SearchRequestsJsonParser search= new SearchRequestsJsonParser();
                algoliaRequests.addAll(search.parseResults(jsonObject));

            }
            //TODO maybe use loading bar
        });
        algoliaReq = algoliaRequests.get(0);

        algoReqIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e == null){

                SearchRequestsJsonParser search= new SearchRequestsJsonParser();
                algoliaRequests.addAll(search.parseResults(jsonObject));

            }
            //TODO maybe use loading bar
        });
        algoliaReq = algoliaRequests.get(0);*/

        FirebaseManagement.getDatabase().getReference()
                .child("requests")
                .child(request.getrId())
                .child("requestStatus")
                .setValue(AppConstants.ACCEPTED)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Gson gson = new Gson();
                        try {
                            request.setRequestStatus(AppConstants.ACCEPTED);
                            algoReqIndex.saveObjectAsync(new JSONObject(gson.toJson(request)),
                                    request.getObjectID().toString(),
                                    new CompletionHandler() {
                                        @Override
                                        public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {

                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        FirebaseManagement.getDatabase().getReference()
                .child("books")
                .child(request.getbId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Book book = dataSnapshot.getValue(Book.class);

                        if(book != null) {

                            FirebaseManagement.getDatabase().getReference()
                                    .child("books")
                                    .child(request.getbId())
                                    .child("stato")
                                    .setValue(AppConstants.NOT_AVAILABLE)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Gson gson = new Gson();
                                            try {
                                                book.setStato(AppConstants.NOT_AVAILABLE);
                                                book.setOwner(request.getOwnerId());
                                                algoBookIndex.saveObjectAsync(new JSONObject(gson.toJson(book)),
                                                        book.getObjectID().toString(),
                                                        null);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            FirebaseManagement.getDatabase().getReference()
                                    .child("books")
                                    .child(request.getbId())
                                    .child("owner")
                                    .setValue(request.getRenterId());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void onClickNo(Request request){

        FirebaseManagement.getDatabase().getReference()
                .child("requests")
                .child(request.getrId())
                .setValue(AppConstants.REFUSED)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Gson gson = new Gson();
                        try {
                            request.setRequestStatus(AppConstants.REFUSED);
                            algoReqIndex.saveObjectAsync(new JSONObject(gson.toJson(request)),
                                    request.getObjectID().toString(),
                                    new CompletionHandler() {
                                        @Override
                                        public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {

                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}