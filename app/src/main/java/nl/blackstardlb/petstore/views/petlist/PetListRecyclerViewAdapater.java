package nl.blackstardlb.petstore.views.petlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.lucasurbas.listitemview.ListItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.views.PetDetailsFragment;

public class PetListRecyclerViewAdapater extends RecyclerView.Adapter<PetListRecyclerViewAdapater.ViewHolder> {
    private List<Pet> pets = new ArrayList<>();

    public void setPets(List<Pet> pets) {
        this.pets = pets;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_list_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(pets.get(position));
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public void remove(Pet pet) {
        int indexOf = pets.indexOf(pet);
        pets.remove(pet);
        notifyItemRemoved(indexOf);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_view)
        ListItemView listItemView;
        private Pet pet;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Pet pet) {
            this.pet = pet;
            listItemView.setTitle(pet.getName());
            listItemView.setSubtitle(pet.getAnimalType());

            Context context = listItemView.getContext();
            String name = pet.getName().charAt(0) + "";
            TextDrawable drawable2 = TextDrawable.builder()
                    .buildRound(name, ContextCompat.getColor(context, R.color.colorAccent));
            ImageView avatarView = listItemView.getAvatarView();
            avatarView.setImageDrawable(drawable2);

            List<String> images = pet.getImages();
            if (!images.isEmpty()) {
                String path = images.get(0);
                Glide.with(listItemView).load(FirebaseStorage.getInstance().getReference(path)).apply(RequestOptions.circleCropTransform()).into(avatarView);
            }
        }

        @OnClick(R.id.list_item_view)
        protected void onListItemClick() {
            if (pet != null) {
                Context context = listItemView.getContext();
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                    FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                    PetDetailsFragment.startFor(fragmentManager, pet.getId());
                }
            }
        }
    }
}
