package com.scorrer.ravi.scorrer;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class SoccerScoreCardFragment extends Fragment {

    private static final String ARG = "PAGE_NO";
    private int page_no;
    private static ArrayList<String> players = SoccerTeamFragment.players;
    private static ArrayList<String> Oplayers = SoccerTeamFragment.Oplayers;
    public static ArrayList<String> onField = new ArrayList<String>();
    public static ArrayList<String> OonField = new ArrayList<String>();
    private static PlayerListAdapter fadapter;
    private static PlayerListAdapter sadapter;
    private View movedView = null;
    private ArrayList<String> highlightType = new ArrayList<String>();
    private ArrayList<String> highlightText = new ArrayList<String>();;
    private ArrayList<String> highlightTime = new ArrayList<String>();;

    public static SoccerScoreCardFragment newInstance(int page_no){
        Bundle bundle = new Bundle();
        bundle.putInt(ARG, page_no);
        SoccerScoreCardFragment fragment = new SoccerScoreCardFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page_no = getArguments().getInt(ARG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        switch (page_no){
            case 1:
                view = inflater.inflate(R.layout.soccer_score_card, null);
                break;
            case 2:
                view = inflater.inflate(R.layout.first_team_formation, null);

                TextView tn1 = (TextView) view.findViewById(R.id.team_name);
                tn1.setText(SoccerTeamFragment.team+"'s Formation");

                ListView ftList = (ListView) view.findViewById(R.id.players_list);
                fadapter = new PlayerListAdapter(players);
                ftList.setAdapter(fadapter);
                ftList.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                ClipData.Item item = event.getClipData().getItemAt(0);
                                if(!players.contains(item.getText())){
                                    ViewGroup parent = (ViewGroup) movedView.getParent();
                                    parent.removeView(movedView);
                                    movedView = null;
                                    players.add(0, (String) item.getText());
                                    onField.remove(item.getText());
                                    fadapter.notifyAdapter(players);
                                }
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(movedView!=null){
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                        }
                        return false;
                    }
                });

                final RelativeLayout fbp = (RelativeLayout) view.findViewById(R.id.football_pitch);
                fbp.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                final ClipData.Item item = event.getClipData().getItemAt(0);
                                if(!onField.contains(item.getText())){
                                    onField.add((String) item.getText());
                                    players.remove(item.getText());
                                    fadapter.notifyAdapter(players);

                                    LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View playerOnGround = inflater1.inflate(R.layout.player_on_ground, null);
                                    TextView tv = (TextView) playerOnGround.findViewById(R.id.player_username);
                                    tv.setText(item.getText());
                                    playerOnGround.setX(event.getX());
                                    playerOnGround.setY(event.getY());
                                    fbp.addView(playerOnGround);

                                    playerOnGround.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            movedView = v;
                                            movedView.setVisibility(View.GONE);
                                            ClipData.Item data = new ClipData.Item(item.getText());
                                            ClipData dragData = new ClipData(item.getText(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                                            ShadowBuilder sb = new ShadowBuilder(playerOnGround, getContext());
                                            v.startDrag(dragData, sb, null, 0);
                                            return true;
                                        }
                                    });
                                    playerOnGround.setOnDragListener(new View.OnDragListener() {
                                        @Override
                                        public boolean onDrag(View v, DragEvent event) {
                                            final int action = event.getAction();
                                            switch (action){
                                                case DragEvent.ACTION_DRAG_STARTED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_ENTERED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_EXITED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_ENDED:
                                                    if(movedView!=null){
                                                        movedView.setVisibility(View.VISIBLE);
                                                        movedView = null;
                                                    }
                                                    return true;
                                            }
                                            return false;
                                        }
                                    });

                                }else{
                                    movedView.setX(event.getX());
                                    movedView.setY(event.getY());
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(movedView!=null){
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                        }
                        return false;
                    }
                });

                break;
            case 3:
                view = inflater.inflate(R.layout.second_team_formation, null);

                TextView tn2 = (TextView) view.findViewById(R.id.team_name);
                tn2.setText(SoccerTeamFragment.Oteam+"'s Formation");

                ListView stList = (ListView) view.findViewById(R.id.players_list);
                sadapter = new PlayerListAdapter(Oplayers);
                stList.setAdapter(sadapter);

                stList.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                ClipData.Item item = event.getClipData().getItemAt(0);
                                if(!Oplayers.contains(item.getText())){
                                    ViewGroup parent = (ViewGroup) movedView.getParent();
                                    parent.removeView(movedView);
                                    movedView = null;
                                    Oplayers.add(0, (String) item.getText());
                                    OonField.remove(item.getText());
                                    sadapter.notifyAdapter(Oplayers);
                                }
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(movedView!=null){
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                        }
                        return false;
                    }
                });

                final RelativeLayout sbp = (RelativeLayout) view.findViewById(R.id.football_pitch);
                sbp.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                final ClipData.Item item = event.getClipData().getItemAt(0);
                                if(!OonField.contains(item.getText())){
                                    OonField.add((String) item.getText());
                                    Oplayers.remove(item.getText());
                                    sadapter.notifyAdapter(Oplayers);

                                    LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View playerOnGround = inflater1.inflate(R.layout.player_on_ground, null);
                                    TextView tv = (TextView) playerOnGround.findViewById(R.id.player_username);
                                    tv.setText(item.getText());
                                    playerOnGround.setX(event.getX());
                                    playerOnGround.setY(event.getY());
                                    sbp.addView(playerOnGround);

                                    playerOnGround.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            movedView = v;
                                            movedView.setVisibility(View.GONE);
                                            ClipData.Item data = new ClipData.Item(item.getText());
                                            ClipData dragData = new ClipData(item.getText(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                                            ShadowBuilder sb = new ShadowBuilder(playerOnGround, getContext());
                                            v.startDrag(dragData, sb, null, 0);
                                            return true;
                                        }
                                    });
                                    playerOnGround.setOnDragListener(new View.OnDragListener() {
                                        @Override
                                        public boolean onDrag(View v, DragEvent event) {
                                            final int action = event.getAction();
                                            switch (action){
                                                case DragEvent.ACTION_DRAG_STARTED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_ENTERED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_EXITED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_ENDED:
                                                    if(movedView!=null){
                                                        movedView.setVisibility(View.VISIBLE);
                                                        movedView = null;
                                                    }
                                                    return true;
                                            }
                                            return false;
                                        }
                                    });

                                }else{
                                    movedView.setX(event.getX());
                                    movedView.setY(event.getY());
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(movedView!=null){
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                        }
                        return false;
                    }
                });
                break;
        }
        return view;
    }

    class HighLightAdapter extends BaseAdapter{

        ArrayList<String> highlightType;
        ArrayList<String> highlightText;
        ArrayList<String> highlightTime;

        public HighLightAdapter(ArrayList<String> highlightType, ArrayList<String> highlightText, ArrayList<String> highlightTime){
            this.highlightType = highlightType;
            this.highlightText = highlightText;
            this.highlightTime = highlightTime;
        }

        @Override
        public int getCount() {
            return highlightType.size();
        }

        @Override
        public Object getItem(int position) {
            return highlightText.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    class PlayerListAdapter extends BaseAdapter {

        ArrayList<String> lineup;

        public PlayerListAdapter(ArrayList<String> lineup) {
            this.lineup = lineup;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return lineup.size();
        }

        @Override
        public Object getItem(int position) {
            return lineup.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.player_team_formation_entry, null);
            }else{
                view = convertView;
            }
            TextView username = (TextView) view.findViewById(R.id.player_username);
            username.setText(lineup.get(position));
            final View finalView = view;
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    movedView = v;
                    movedView.setVisibility(View.GONE);
                    ClipData.Item data = new ClipData.Item(lineup.get(position));
                    ClipData dragData = new ClipData(lineup.get(position), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                    ShadowBuilder sb = new ShadowBuilder(finalView, getContext());
                    v.startDrag(dragData, sb, null, 0);
                    return true;
                }
            });
            return view;
        }

        public void notifyAdapter(ArrayList<String> lineup){
            this.lineup = lineup;
            notifyDataSetChanged();
        }
    }

    static class ShadowBuilder extends View.DragShadowBuilder{
        private static Drawable shadow;
        private int width, height;
        private String drawText;
        public ShadowBuilder(View view, Context context) {
            super(view);
            TextView tv = (TextView) view.findViewById(R.id.player_username);
            drawText = (String) tv.getText();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shadow = context.getResources().getDrawable(R.drawable.player_model, context.getTheme());
            }else{
                shadow = context.getResources().getDrawable(R.drawable.player_model);
            }
        }

        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            width = 60;
            height = 80;
            shadow.setBounds(0, 0, width, height);
            size.set(width, height);
            touch.set(width/2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextSize((float) (20));
            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 3) - ((paint.descent() + paint.ascent())));
            canvas.drawText(drawText, xPos, yPos, paint);
        }

    }

}

