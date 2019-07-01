package com.app.cardholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for RecycleView
 */
public class CardsRecycleViewAdapter extends RecyclerView.Adapter<CardsRecycleViewAdapter.CardViewHolder> {

    private Context context;
    private List<Card> cards;

    /**
     * Accessor method for Context object
     *
     * @return card list
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Getter method for Context object
     *
     * @param cards - list of cards for initializing
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }


    /**
     * Getter method for Context object
     *
     * @return current context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Accessor method for Context object
     *
     * @param context - Context of activity
     */
    public void setContext(Context context) {
        this.context = context;
    }

    CardsRecycleViewAdapter(List<Card> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(
                viewGroup.getContext()).inflate(
                R.layout.item_row, viewGroup,
                false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder cardViewHolder, final int i) {
        cardViewHolder.name.setText(cards.get(i).getName());
        cardViewHolder.place.setText(cards.get(i).getPlace());
        cardViewHolder.code.setText(cards.get(i).getCode());
        cardViewHolder.setCardColor(cards.get(i).getColor());
        cardViewHolder.fullscreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenCardActivity.class);
                intent.putExtra("name", cardViewHolder.name.getText());
                intent.putExtra("barcode", cardViewHolder.code.getText());
                intent.putExtra("color", cardViewHolder.color);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    /**
     * Class for storing data about card for RecycleView
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {

        private CardView cv;
        private TextView name;
        private TextView place;
        private String color;
        private TextView code;
        private ImageView fullscreen;

        /**
         * Constructor of initializing
         *
         * @param itemView - view for initializing
         */
        public CardViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            name = itemView.findViewById(R.id.nameTextView);
            place = itemView.findViewById(R.id.placeTextView);
            code = itemView.findViewById(R.id.codeTextView);
            fullscreen = itemView.findViewById(R.id.fullScreenImageView);
        }

        /**
         * Setter method for card color
         *
         * @param color - color value as string
         */
        public void setCardColor(String color) {
            this.color = color;
            cv.setCardBackgroundColor(Color.parseColor(color));
        }

        /**
         * Getter method for card color
         *
         * @return color value
         */
        public String getColor() {
            return color;
        }
    }

    public void onItemSwiped(int position) {
        cards.remove(position);
        notifyItemRemoved(position);
    }

}
