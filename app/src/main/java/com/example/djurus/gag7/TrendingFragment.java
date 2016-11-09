package com.example.djurus.gag7;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrendingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GagAdapter adapterFeed;
    private GagRecommendedAdapter adapterRecommended;

    private JSONObject obj;


    private ArrayList<Gag> trendingRecommendedList = new ArrayList<>();
    private ArrayList<Gag> trendingFeedList = new ArrayList<>();
    private ArrayList<Gag> displayRecommendedList = new ArrayList<>();
    private ArrayList<Gag> displayFeedList = new ArrayList<>();


    public TrendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendingFragment newInstance(String param1, String param2) {
        TrendingFragment fragment = new TrendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trending, container, false);

        loadRecommendedListJSON();
        loadFeedListJSON();
        displayRecommendedList.add(trendingRecommendedList.get(0));
        displayRecommendedList.add(trendingRecommendedList.get(1));
        displayFeedList.add(trendingFeedList.get(0));
        displayFeedList.add(trendingFeedList.get(1));
        
        
        
        adapterFeed = new GagAdapter(getContext(), displayFeedList);
        ListView listView = (ListView) v.findViewById(android.R.id.list);
        listView.setAdapter(adapterFeed);
        listView.setOnScrollListener(new InfiniteScrollListener(2) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                loadFeedData(2, page);
                adapterFeed.notifyDataSetChanged();
            }
        });
        adapterRecommended = new GagRecommendedAdapter(getContext(), trendingRecommendedList);
        TwoWayView recommendedLV = (TwoWayView) v.findViewById(R.id.lvItems);
        recommendedLV.setAdapter(adapterRecommended);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public class GagAdapter extends ArrayAdapter<Gag> {
        private final ArrayList<Gag> values;
        private final Context context;
        public GagAdapter(Context context, ArrayList<Gag> values) {
            super(context, R.layout.listview, values);
            this.values=values;
            this.context=context;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listView = inflater.inflate(R.layout.listview, parent, false);
            TextView gagTitle = (TextView) listView.findViewById(R.id.gagTitle);
            ImageView gagImage = (ImageView) listView.findViewById(R.id.gagImage);
            Gag gag = values.get(position);
            gagTitle.setText(gag.getTitle());
            int id = getResources().getIdentifier("com.example.djurus.gag7:drawable/" + gag.getImgSrc(), null, null);
            gagImage.setImageResource(id);
            return listView;
        }
    }
    public class GagRecommendedAdapter extends ArrayAdapter<Gag> {
        private final ArrayList<Gag> values;
        private final Context context;
        public GagRecommendedAdapter(Context context, ArrayList<Gag> values) {
            super(context, R.layout.listview, values);
            this.values=values;
            this.context=context;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (position==values.size()-1){
                loadRecommendedData(values.size());
            }
            View listView = inflater.inflate(R.layout.recommendedview, parent, false);
            TextView gagRTitle = (TextView) listView.findViewById(R.id.gagRTitle);
            ImageView gagRImage = (ImageView) listView.findViewById(R.id.gagRImage);
            Gag gag = values.get(position);
            gagRTitle.setText(gag.getTitle());
            int id = getResources().getIdentifier("com.example.djurus.gag7:drawable/" + gag.getImgSrc(), null, null);
            gagRImage.setImageResource(id);
            return listView;
        }
    }
    public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
        private int bufferItemCount = 10;
        private int currentPage = 0;
        private int itemCount = 0;
        private boolean isLoading = true;

        public InfiniteScrollListener(int bufferItemCount) {
            this.bufferItemCount = bufferItemCount;
        }

        public abstract void loadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Do Nothing1
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (totalItemCount < itemCount) {
                this.itemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.isLoading = true; }
            }

            if (isLoading && (totalItemCount > itemCount)) {
                isLoading = false;
                itemCount = totalItemCount;
                currentPage++;
            }

            if (!isLoading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)) {
                loadMore(currentPage + 1, totalItemCount);
                isLoading = true;
            }
        }
    }
    public void loadFeedData(int perPage,int page){
        int start = perPage*(page-1);
        for (int i=start;i<start+perPage;i++){
            if (i<trendingFeedList.size()){
                displayFeedList.add(trendingFeedList.get(i));
            }
        }
    }
    public void loadRecommendedData(int start){
        for (int i=start;i<start+2;i++){
            if (i<trendingRecommendedList.size()){
                displayRecommendedList.add(trendingRecommendedList.get(i));
            }

        }
        adapterRecommended.notifyDataSetChanged();
    }
    public String loadJSONFromAsset() {
        String json = "";
        try {

            InputStream is = getActivity().getAssets().open("gag7.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
    public void loadRecommendedListJSON(){
        try{
            obj = new JSONObject(loadJSONFromAsset());
            JSONArray arr= obj.getJSONObject("gags").getJSONObject("trending").getJSONArray("recommended");
            for(int i = 0; i<arr.length();i++){
                JSONObject gagObj= arr.getJSONObject(i);
                trendingRecommendedList.add(new Gag(gagObj.getString("title"),gagObj.getString("imgSrc")));
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void loadFeedListJSON(){
        try{
            obj = new JSONObject(loadJSONFromAsset());
            JSONArray arr= obj.getJSONObject("gags").getJSONObject("trending").getJSONArray("feed");
            for(int i = 0; i<arr.length();i++){
                JSONObject gagObj= arr.getJSONObject(i);
                trendingFeedList.add(new Gag(gagObj.getString("title"),gagObj.getString("imgSrc")));
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }
}
