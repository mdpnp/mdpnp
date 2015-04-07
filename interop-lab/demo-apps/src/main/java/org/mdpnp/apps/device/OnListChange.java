package org.mdpnp.apps.device;

import java.util.function.Consumer;

import javafx.collections.ListChangeListener;

public class OnListChange<D> implements ListChangeListener<D>{

    private final Consumer<D> add, update, delete;
    
    public OnListChange(Consumer<D> add, Consumer<D> update, Consumer<D> delete) {
        this.add = add;
        this.update = update;
        this.delete = delete;
    }
    
    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends D> c) {
        while(c.next()) {
            if(c.wasUpdated() && null != update) {
                c.getList().subList(c.getFrom(), c.getTo()).forEach((t) -> update.accept(t));
            }
            if(c.wasAdded() && null != add) {
                c.getAddedSubList().forEach((t)->add.accept(t));
            }
            if(c.wasRemoved() && null != delete) {
                c.getRemoved().forEach((t)->delete.accept(t));
            }
        }
    }

}
