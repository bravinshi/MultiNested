package bravin.shi.com.multinested;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ContentViewHolder extends RecyclerView.ViewHolder {
    public TextView mText;
    private static int count = 0;

    public ContentViewHolder(View itemView) {
        super(itemView);
        count++;
        Log.d("VY", "count: " + count);
        mText = itemView.findViewById(R.id.tv_content);
    }
}