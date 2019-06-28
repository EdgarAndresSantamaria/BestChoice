package ehu.das.bestchoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RankingListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private String[] usernames;
    private int[] points;

    RankingListViewAdapter(Context pCtx, String[] pUsernames, int[] pPoints) {
        usernames = pUsernames;
        points = pPoints;

        inflater = (LayoutInflater) pCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Carga una fila del ListView con los datos
     *
     * @param i
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_ranking_row, null);

        TextView pos = (TextView) view.findViewById(R.id.textViewPosInRankingRow);
        TextView user = (TextView) view.findViewById(R.id.textViewUsernameRow);
        TextView pts = (TextView) view.findViewById(R.id.textViewRankingPointsRow);

        String position = (i + 1) + ".";
        pos.setText(position);

        user.setText(usernames[i]);

        String point = Integer.toString(points[i]);
        pts.setText(point);

        return view;
    }

    public Object getItem(int i) {
        return usernames[i];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return usernames.length;
    }
}
