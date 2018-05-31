
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

public class Request_1_othersReq extends Fragment {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    Index algoIndex = algoClient.getIndex("requests");


    public Request_1_othersReq() {

        //Required empty public constructor
        //RecyclerView recyclerView = (RecyclerView)container.getChildAt((int)R.id.recycler_view);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new gmads.it.gmads_lab1.Home.GridSpacingItemDecoration(2, dpToPx(10), true));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {
        // preparing list data
        View root = inflater.inflate(R.layout.fragment_list_others_request, container, false);

        prepareListData();
        expListView = (ExpandableListView)root.findViewById(R.id.explv);
        /*
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

        // Listview on child click listener
        /*expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });*/
        return root;
    }

    private void prepareListData() {



        Query query = new Query("")
                .setHitsPerPage(100);

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e==null){
                SearchRequestsJsonParser parser=new  SearchRequestsJsonParser();
                List<ReferenceRequest> listrequest=parser.parseResults(jsonObject);
                listDataChild = new HashMap<>();
                listDataHeader= new ArrayList<>();
                for(ReferenceRequest rr : listrequest){
                    if(!listDataChild.containsKey(rr.getBookname())){
                        List<String> request= new ArrayList<>();
                        listDataHeader.add(rr.getBookname());
                        request.add(rr.getNomerichiedente());
                        listDataChild.put(rr.getBookname(),request);
                    }
                    else{
                        listDataChild.put(rr.getBookname(),listDataChild.get(rr.getBookname()));
                    }
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

                // Listview on child click listener
        /*expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });*/

            }else{

            }

        });

/*
        listDataHeader = new ArrayList<String>();

        listDataChild = new HashMap<String, List<String>>();
        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly e bla bla bla bla ciao come va bla bla bla");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
*/
    }

}
