package bravin.shi.com.multinested;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContentAdapter extends SimpleRecyclerViewAdapter<DataBean> {
    private static final String TAG = "Multi";
    private Context context;
    private int[] colors = new int[]{Color.GRAY, Color.BLUE};
    private static int count = 0;

    public ContentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemType(DataBean item) {
        return 0;
    }

    @Override
    public void onBindVH(RecyclerView.ViewHolder holder, int position, DataBean item) {
        int colorPos = position % colors.length;
        holder.itemView.setBackgroundColor(colors[colorPos]);
        ((ContentViewHolder) holder).mText.setText(item.getName());
        ((ContentViewHolder) holder).mText.setTextColor(Color.WHITE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        count++;
        Log.d(TAG, "ContentAdapter " + count);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.content_item, parent, false);
        return new ContentViewHolder(view);
    }
}
