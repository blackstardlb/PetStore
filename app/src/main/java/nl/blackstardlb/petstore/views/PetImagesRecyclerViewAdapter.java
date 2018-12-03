package nl.blackstardlb.petstore.views;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.blackstardlb.petstore.R;

public class PetImagesRecyclerViewAdapter extends RecyclerView.Adapter<PetImagesRecyclerViewAdapter.Holder> {
    private List<Bitmap> bitmaps = new ArrayList<>();

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_image_item, parent, false);
        return new Holder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(bitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public void addBitmap(Bitmap bitmap) {
        bitmaps.add(bitmap);
        notifyItemInserted(bitmaps.size());
    }

    public void addBitmaps(Collection<Bitmap> bitmaps) {
        int size = this.bitmaps.size();
        this.bitmaps.addAll(bitmaps);
        notifyItemRangeInserted(size, bitmaps.size());
    }

    public void clear() {
        this.bitmaps = new ArrayList<>();
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        protected ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
