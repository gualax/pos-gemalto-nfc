package com.example.mygemaltonfc.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygemaltonfc.R;
import com.example.mygemaltonfc.model.Card;
import java.util.ArrayList;


public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder>{

    private ArrayList<Card> cardList;
    private Context mContext;

    public CardsAdapter(Context context,ArrayList<Card> cardList) {
        this.cardList = cardList;
        this.mContext = context;
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardNumber, tvHolder, tvCvv, tvExpDate;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardNumber = itemView.findViewById(R.id.t_frag_card_number);
            tvHolder = itemView.findViewById(R.id.t_frag_holder);
            tvCvv = itemView.findViewById(R.id.t_frag_cvv);
            tvExpDate = itemView.findViewById(R.id.t_frag_exp_date);
        }

        public void assignData(final Card card, int position) {
            tvHolder.setText(card.getHolder());
            tvExpDate.setText(card.getExpDate());
            tvCardNumber.setText(card.getNumber());
            tvCvv.setText(String.valueOf(card.getCvv()));

            //   this.cardPoke = cardPoke;
            // this.position = position;
        }

    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.assignData(cardList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

}
