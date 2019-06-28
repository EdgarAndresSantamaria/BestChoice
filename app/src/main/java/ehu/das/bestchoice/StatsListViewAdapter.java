package ehu.das.bestchoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StatsListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private String[] categories;
    private String[] option1;
    private String[] votePercent1;
    private String[] option2;
    private String[] votePercent2;

    StatsListViewAdapter(Context pCtx, String[] pCategories, String[] pOption1, String[] pVotePercent1,
                                String[] pOption2, String[] pVotePercent2) {
        categories = pCategories;
        option1 = pOption1;
        votePercent1 = pVotePercent1;
        option2 = pOption2;
        votePercent2 = pVotePercent2;

        inflater = (LayoutInflater) pCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Carga los datos en una fila del listView
     *
     * @param i
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_stat_row, null);

        TextView category = (TextView) view.findViewById(R.id.textViewCategoryRow);
        TextView op1 = (TextView) view.findViewById(R.id.textViewOption1Row);
        TextView perc1 = (TextView) view.findViewById(R.id.textViewVotePercent1Row);
        TextView op2 = (TextView) view.findViewById(R.id.textViewOption2Row);
        TextView perc2 = (TextView) view.findViewById(R.id.textViewVotePercent2Row);

        category.setText(categories[i]);
        op1.setText(option1[i]);
        perc1.setText(votePercent1[i]);
        op2.setText(option2[i]);
        perc2.setText(votePercent2[i]);

        return view;
    }


    public Object getItem(int i) {
        return categories[i];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return categories.length;
    }
}
