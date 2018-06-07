package com.macbitsgoa.about;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.about.models.SocialLink;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;

/**
 * @author Rushikesh Jogdand.
 */
public class SocialAdapter extends RecyclerView.Adapter<SocialLinkVh> {
    private final List<SocialLink> links = new ArrayList<>(0);
    private final Realm realm = Realm.getDefaultInstance();
    private final Browser browser;

    public SocialAdapter(@NonNull final Browser browser) {
        getData();
        this.browser = browser;
    }

    private void getData() {
        realm.where(SocialLink.class).findAllAsync().addChangeListener(socialLinks -> {
            links.clear();
            for (final SocialLink link : socialLinks) {
                links.add(realm.copyFromRealm(link));
            }
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public SocialLinkVh onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new SocialLinkVh(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.vh_social, parent, false),
                browser);
    }

    @Override
    public void onBindViewHolder(@NonNull final SocialLinkVh holder, final int position) {
        holder.populate(links.get(position));
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull final RecyclerView recyclerView) {
        realm.close();
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
