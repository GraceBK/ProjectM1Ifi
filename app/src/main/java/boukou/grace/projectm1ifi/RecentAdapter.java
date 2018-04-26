package boukou.grace.projectm1ifi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import boukou.grace.projectm1ifi.db.room_db.RContact;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 26/04/2018.
 */
class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private ArrayList<RContact> recents;

    RecentAdapter(ArrayList<RContact> recentList) {
        this.recents = recentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO : cas si pas de nom
        holder.username.setText(recents.get(position).getUsername());
        holder.status.setText(recents.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView status;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_or_phone_text_view);
            status = itemView.findViewById(R.id.status_sms_text_view);
        }
    }
}
