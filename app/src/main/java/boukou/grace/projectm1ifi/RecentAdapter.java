package boukou.grace.projectm1ifi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import boukou.grace.projectm1ifi.db.room_db.RContact;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 26/04/2018.
 */
class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private List<RContact> recents;

    RecentAdapter(List<RContact> recentList) {
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
        holder.username.setText(recents.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }

    public void update(List<RContact> rContacts) {
        this.recents = rContacts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_or_phone_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("USERNAME", recents.get(getLayoutPosition()).getUsername());
                    intent.putExtra("PHONE", recents.get(getLayoutPosition()).getPhone());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
