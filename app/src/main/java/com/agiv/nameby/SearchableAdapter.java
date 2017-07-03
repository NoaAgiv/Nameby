package com.agiv.nameby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.agiv.nameby.entities.Member;
import com.agiv.nameby.entities.Name;

import java.util.ArrayList;
import java.util.List;

public class SearchableAdapter extends BaseAdapter implements ListAdapter, Filterable {
//    private ArrayList<Name> list = new ArrayList<Name>();
    private Context context;

    private List<Name> list = null;
    private List<Name> filteredList = null;
    private LayoutInflater mInflater;
    private NameFilter mFilter = new NameFilter();
    private TagFilter tagFilter = new TagFilter();

//    public SearchableAdapter(ArrayList<Name> list, Context context) {
//        this.list = list;
//        this.context = context;
//    }

//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public Object getItem(int pos) {
//        return list.get(pos);
//    }
//
//    @Override
//    public long getItemId(int pos) {
////        return list.get(pos).getId();
//        return 0;
//        //just return 0 if your list items do not have an Id variable.
//    }

//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (view == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.simple_row, null);
//        }
//
//        //Handle TextView and display string from your list
//        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
//        listItemText.setText(list.get(position).name);
//
//        return view;
//    }


    public SearchableAdapter(Context context, List<Name> list) {
        this.context = context;
        this.filteredList = list ;
        this.list = list ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredList.size();
    }

    public Object getItem(int position) {
        return filteredList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.simple_row, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.list_item_string);

        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.text.setText(filteredList.get(position).name);
        holder.tagImg = (ImageButton) convertView.findViewById(R.id.tag);

        Integer img = Settings.getMember().getTag(filteredList.get(position)).imageResId;
        if (img != null)
            holder.tagImg.setImageResource(img);

        // Bind the data efficiently with the holder.

        convertView.setTag(holder);
        return convertView;
    }

    static class ViewHolder {
        TextView text;
        ImageButton tagImg;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class NameFilter extends Filter {

        protected FilterResults performTagFiltering(String constraint) {

            final NameList filtered = new NameList();

            if (constraint.equals(context.getString(R.string.all))){
                filtered.conditionalAddAll(list, NameList.identityFilter);
            }
            else if (constraint.equals(context.getString(R.string.matches))){
                filtered.conditionalAddAll(list, NameList.unanimouslyPositiveFilter);
            }
            else{
                Member.NameTag tag = Member.NameTag.getTag(context, constraint);
                filtered.conditionalAddAll(list, new NameList.tagFilter(tag));
            }

            FilterResults results = new FilterResults();
            results.values = filtered;
            results.count = filtered.size();

            return results;
        }

        protected FilterResults performNameFiltering(String constraint) {

            FilterResults results = new FilterResults();

            final List<Name> list = SearchableAdapter.this.list;

            int count = list.size();
            final ArrayList<Name> nlist = new ArrayList<>(count);

            Name filterableName;

            for (int i = 0; i < count; i++) {
                filterableName = list.get(i);
                if (filterableName.name.toLowerCase().contains(constraint)) {
                    nlist.add(filterableName);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String stringConstraint = constraint.toString().toLowerCase();
            if (stringConstraint.startsWith("name.")){
                return performNameFiltering(stringConstraint.substring(5));
            }
            else if (stringConstraint.startsWith("tag.")){
                return performTagFiltering(stringConstraint.substring(4));
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Name>) results.values;
            notifyDataSetChanged();
        }

    }

    private class TagFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence tagConstraint) {
            int count = list.size();
            final ArrayList<Name> nlist = new ArrayList<>(count);

            String constraint = tagConstraint.toString().toLowerCase();
            if (constraint.equals(context.getString(R.string.all))){
                for (Name name : list){
                    nlist.add(name);
                }
            }
            else if (constraint.equals(context.getString(R.string.matches))){
                for (Name name : list){
                    if (Settings.getFamily().isUnanimouslyPositive(name)){
                        nlist.add(name);
                    }
                }
            }
            else{
                Member.NameTag tag = Member.NameTag.getTag(context, constraint);
                for (Name name : list){
                    if (Settings.member.getTag(name).equals(tag)){
                        nlist.add(name);
                    }
                }
            }


            FilterResults results = new FilterResults();

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Name>) results.values;
            notifyDataSetChanged();
        }

    }
}

