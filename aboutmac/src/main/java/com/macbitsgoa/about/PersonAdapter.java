package com.macbitsgoa.about;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.about.models.Person;

import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Rushikesh Jogdand.
 */
public class PersonAdapter extends RecyclerView.Adapter<PersonVh> {
    private final Browser browser;
    private final List<Person> personList;

    public PersonAdapter(final List<Person> personList, final Browser browser) {
        this.personList = personList;
        this.browser = browser;
    }

    public void setPersonList(final Collection<Person> personList) {
        this.personList.clear();
        this.personList.addAll(personList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonVh onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new PersonVh(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.vh_person, parent, false),
                browser);
    }

    @Override
    public void onBindViewHolder(@NonNull final PersonVh holder, final int position) {
        holder.populate(personList.get(position));
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }
}
