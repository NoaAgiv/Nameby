package com.agiv.nameby;

import android.app.Activity;
import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.agiv.nameby.entities.Member;
import com.agiv.nameby.entities.Name;
import com.agiv.nameby.utils.ImageArrayAdapater;

import java.util.ArrayList;
import java.util.List;

public class SearchableAdapter extends BaseAdapter implements ListAdapter, Filterable {
    private Context context;

    private List<Name> list = null;
    private List<Name> filteredList = null;
    private LayoutInflater mInflater;
    private NameFilter mFilter = new NameFilter();
    private TagFilter tagFilter = new TagFilter();
    private ImageArrayAdapater itemTagAdapter;

    public SearchableAdapter(Context context, List<Name> list, ImageArrayAdapater itemTagAdapter) {
        this.context = context;
        this.filteredList = list ;
        this.list = list ;
        mInflater = LayoutInflater.from(context);
        this.itemTagAdapter = itemTagAdapter;
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
            convertView = mInflater.inflate(R.layout.list_item, null);

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
        Name name = filteredList.get(position);
        holder.text.setText(name.name);
//        holder.tagImg = (ImageButton) convertView.findViewById(R.id.tag);
        holder.tagSpinner = (Spinner) convertView.findViewById(R.id.item_tag_spinner);

        holder.tagSpinner.setAdapter(itemTagAdapter);

        Integer img = Settings.getMember().getTag(filteredList.get(position)).imageResId;
        if (img != null)
//            holder.tagImg.setImageResource(img);
            holder.tagSpinner.setSelection(itemTagAdapter.getImagePosition(img));

        setTagSpinner(name, holder.tagSpinner);

        // Bind the data efficiently with the holder.

        convertView.setTag(holder);
        return convertView;
    }

    static class ViewHolder {
        TextView text;
//        ImageButton tagImg;
        Spinner tagSpinner;
    }


    private void setTagSpinner(final Name name, final Spinner tagSpinner){
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                Member.NameTag selectedTag = ((Member.NameTag) itemTagAdapter.get(pos));
                Settings.getMember().tagName(name, selectedTag);
                NameTagger.saveNameTag(name, Settings.getMember());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    public Filter  getFilter() {
        return mFilter;
    }

    private class NameFilter extends Filter {

        protected FilterResults performTagFiltering(String constraint) {

            final NameList filtered = new NameList();

            if (constraint.equals(context.getString(R.string.all))){
                filtered.conditionalAddAll(list, NameList.identityFilter);
            }
            else if (constraint.equals(context.getString(R.string.matches))){
                BottomNavigationItemView matchesMenuItem = (BottomNavigationItemView) ((Activity) context).findViewById(R.id.menu_matches);
                matchesMenuItem.setSelected(false); // this will set the new match icon off
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

