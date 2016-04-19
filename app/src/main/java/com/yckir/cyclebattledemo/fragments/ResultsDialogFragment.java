package com.yckir.cyclebattledemo.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yckir.cyclebattledemo.R;
import com.yckir.cyclebattledemo.utility.AlarmHandler;
import com.yckir.cyclebattledemo.utility.GameResultsData;


/**
 * Displays the results of a game onto a dialog. Takes a GameResultData object to fill
 * four ListViews, number of wins, place, player name, and duration without crashing. This dialog has a minimum delay of 1 second before it can be dismissed. A exit option will be enabled on the dialog ofter the delay has passed, the user may also tap outside the dialog to dismiss as well.
 */
public class ResultsDialogFragment extends DialogFragment implements View.OnClickListener{

    private static final String ARG_PLACE       = "ResultsDialogFragment_place";
    private static final String ARG_PLAYERS     = "ResultsDialogFragment_players";
    private static final String ARG_DURATION    = "ResultsDialogFragment_duration";
    private static final String ARG_WINS        = "ResultsDialogFragment_wins";
    private static final long   CLOSE_DELAY     = 1000;


    private String[] mPlace = {"Place", "1st","1st","1st","1st"};
    private String[] mPlayers = {"Player", "Red","Green","Pink","White"};
    private String[] mDurations = {"Time", "00.00","00.00","00.00","00.00"};
    private String[] mWins = {"Wins", "   0","   0","   0","   0"};

    private ListView mPlaceList;
    private ListView mPlayerList;
    private ListView mDurationList;
    private ListView mWinsList;
    private TextView mCloseTextView;

    private AlarmHandler mAlarm;

    public ResultsDialogFragment(){}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gameResultsData object containing results data.
     * @return A new instance of fragment ResultsDialogFragment.
     */
    public static ResultsDialogFragment newInstance(GameResultsData gameResultsData) {
        ResultsDialogFragment fragment = new ResultsDialogFragment();
        Bundle args = new Bundle();

        args.putStringArray(ARG_PLACE, gameResultsData.getPlace());
        args.putStringArray(ARG_PLAYERS, gameResultsData.getNames());
        args.putStringArray(ARG_DURATION, gameResultsData.getDurations());
        args.putStringArray(ARG_WINS, gameResultsData.getWins());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null){
            mPlace = args.getStringArray(ARG_PLACE);
            mPlayers = args.getStringArray(ARG_PLAYERS);
            mDurations = args.getStringArray(ARG_DURATION);
            mWins = args.getStringArray(ARG_WINS);
        }

        setCancelable(false);

        mAlarm = new AlarmHandler(new AlarmHandler.AlarmListener() {
            @Override
            public void alarm(int id) {
                mCloseTextView.setEnabled(true);
                setCancelable(true);
            }
        });
        mAlarm.setAlarm(CLOSE_DELAY,0);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.results,container,false);

        mPlaceList = (ListView) view.findViewById(R.id.place_list);
        mPlayerList = (ListView) view.findViewById(R.id.player_list);
        mDurationList = (ListView) view.findViewById(R.id.duration_list);
        mWinsList = (ListView) view.findViewById(R.id.wins_list);
        mCloseTextView = (TextView)view.findViewById(R.id.close);
        mCloseTextView.setOnClickListener(this);

        ArrayAdapter<String> placeAdapter = new ArrayAdapter<>(getActivity(),R.layout.result_list_item, mPlace);
        ArrayAdapter<String> playerAdapter = new ArrayAdapter<>(getActivity(),R.layout.result_list_item, mPlayers);
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(getActivity(),R.layout.result_list_item, mDurations);
        ArrayAdapter<String> winsAdapter = new ArrayAdapter<>(getActivity(),R.layout.result_list_item, mWins);

        mPlaceList.setAdapter(placeAdapter);
        mPlayerList.setAdapter(playerAdapter);
        mDurationList.setAdapter(durationAdapter);
        mWinsList.setAdapter(winsAdapter);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                setCancelable(true);
                dismiss();
                break;
        }
    }
}
