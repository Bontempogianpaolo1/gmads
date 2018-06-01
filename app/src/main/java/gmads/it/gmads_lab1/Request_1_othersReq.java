
package gmads.it.gmads_lab1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gmads.it.gmads_lab1.model.Book;
import gmads.it.gmads_lab1.model.Request;

public class Request_1_othersReq extends Fragment {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<Book> listDataHeader;
    HashMap<String, List<Request>> listDataChild;
    Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    Index algoIndex = algoClient.getIndex("requests");


    public Request_1_othersReq() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {
        // preparing list data
        View root = inflater.inflate(R.layout.fragment_list_others_request, container, false);

        prepareListData();
        expListView = (ExpandableListView)root.findViewById(R.id.explv);

        return root;
    }

    private void prepareListData() {



        Query query = new Query("").setFilters("ownerId:" +FirebaseManagement.getUser().getUid())
                .setHitsPerPage(100);

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e==null){
                SearchRequestsJsonParser parser=new  SearchRequestsJsonParser();
                List<Request> listrequest=parser.parseResults(jsonObject);
                listDataChild = new HashMap<>();
                listDataHeader= new ArrayList<>();
                Book tempBook;

                for(Request rr : listrequest){
                    if(!listDataChild.containsKey(rr.getbId())){
                        tempBook = new Book();
                        tempBook.setBId(rr.getbId());
                        tempBook.setTitle(rr.getbName());
                        listDataHeader.add(tempBook);
                        listDataChild.put(rr.getbId(), new ArrayList<Request>());
                    }

                    listDataChild.get(rr.getbId()).add(rr);
                }

                listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);
                // Listview Group click listener
                expListView.setOnGroupClickListener(( parent, v, groupPosition, id ) -> {
                    //Toast.makeText(getApplicationContext(),"Group Clicked " + listDataHeader.get(groupPosition),Toast.LENGTH_SHORT).show();
                    //parent.expandGroup(groupPosition);
                    return false;
                });

                // Listview Group expanded listener
                expListView.setOnGroupExpandListener(groupPosition -> {
                    //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
                });

                // Listview Group collapsed listener
                expListView.setOnGroupCollapseListener(groupPosition -> {
                    //Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " Collapsed",Toast.LENGTH_SHORT).show();
                });

            }else{

            }

        });

    }

}
