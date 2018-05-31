package gmads.it.gmads_lab1;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;

import de.hdodenhof.circleimageview.CircleImageView;
import gmads.it.gmads_lab1.Chat.glide.GlideApp;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> listChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.listHeader = listDataHeader;
        this.listChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listChild.get(this.listHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

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

        Button bYes = convertView.findViewById(R.id.yes);
        bYes.setOnClickListener( v -> onClickYes(childPosition));

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

    public void onClickYes(View v){

    }

}