package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.Msg;
import boukou.grace.projectm1ifi.java_files.cesar.Cesar;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 30/04/2018.
 */
public class MsgAdapter extends RecyclerView.Adapter {

    private static final int VIEW_MESSAGE_SENT = 1;
    private static final int VIEW_MESSAGE_RECEIVED = 2;

    private List<Msg> msgList;

    MsgAdapter(List<Msg> msgList) {
        this.msgList = msgList;
    }

    @Override
    public int getItemViewType(int position) {
        Msg msg = msgList.get(position);
        if (Objects.equals(msg.phoneSender, "sender")) {
            return VIEW_MESSAGE_SENT;
        } else if (Objects.equals(msg.phoneSender, "receiver")) {
            return VIEW_MESSAGE_RECEIVED;
        } else {
            return 3;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item, parent, false);
            return new MsgAdapter.MySentSmsViewHolder(view);
        } else if (viewType == VIEW_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_item, parent, false);
            return new MsgAdapter.MyReceivedSmsViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_MESSAGE_SENT:
                ((MsgAdapter.MySentSmsViewHolder) holder).bind(msg);
                break;
            case VIEW_MESSAGE_RECEIVED:
                ((MsgAdapter.MyReceivedSmsViewHolder) holder).bind(msg);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void update(List<Msg> msgs) {
        this.msgList = msgs;
        notifyDataSetChanged();
    }

    class MySentSmsViewHolder extends RecyclerView.ViewHolder {

        TextView sms;
        TextView sms_decrypt;
        TextView status_sms;
        TextView status_key;
        TextView time;
        Button lire_sms_ok;
        Button re_send_sms;

        MySentSmsViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.edit_txt_sms_send);
            sms_decrypt = itemView.findViewById(R.id.edit_txt_sms_send_decrypt);
            status_sms = itemView.findViewById(R.id.tv_sms_status);
            status_key = itemView.findViewById(R.id.tv_key_status);
            lire_sms_ok = itemView.findViewById(R.id.btn_see);
            re_send_sms = itemView.findViewById(R.id.btn_renvoyer);
        }

        @SuppressLint("SetTextI18n")
        void bind(Msg msg2) {
            this.sms.setText(msg2.sms1);
            this.sms_decrypt.setText(Cesar.decrypter(Integer.parseInt(msg2.key), msg2.sms1));
            this.status_sms.setText(msg2.status_sms);

            if (this.status_sms.getText() == "") {
                this.status_sms.setText("En attente de l'accuse");
            }

            if (this.status_sms.getText() == "En attente de l'accuse") {
                re_send_sms.setVisibility(View.GONE);
            } else {
                re_send_sms.setVisibility(View.VISIBLE);
            }
            //this.status_key.setText(msg2.status_key);

            lire_sms_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sms.getVisibility() == View.VISIBLE) {
                        sms.setVisibility(View.GONE);
                        sms_decrypt.setVisibility(View.VISIBLE);
                        lire_sms_ok.setText("Chiffre");
                    } else {
                        sms.setVisibility(View.VISIBLE);
                        sms_decrypt.setVisibility(View.GONE);
                        lire_sms_ok.setText("Dechiffre");
                    }
                }
            });

            re_send_sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSMS(msg2.phoneReceiver, msg2.sms1, msg2.nameReceiver);
                }
            });
        }

        void sendSMS(String phone, String msg, String id_sms_send) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, "iSMS " + id_sms_send + " " + msg, null, null/*sentPendingIntent, deliveredPendingIntent*/);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MyReceivedSmsViewHolder extends RecyclerView.ViewHolder {

        TextView sms;

        MyReceivedSmsViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.edit_txt_sms_received);
        }

        void bind(Msg msg2) {
            this.sms.setText(msg2.sms1);
        }
    }
}
