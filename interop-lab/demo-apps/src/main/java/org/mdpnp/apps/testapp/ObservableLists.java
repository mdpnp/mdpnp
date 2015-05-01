package org.mdpnp.apps.testapp;

import static com.google.common.collect.Sets.newHashSet;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Utility class for dealing with JavaFX ObservableLists.
 * 
 * @author dain.nilsson
 * @author henrik.olsson
 */
public class ObservableLists
{
    public static <E, T extends Iterable<E>> ObservableList<E> fromExpression( Callable<T> expression,
            ObservableList<? extends Observable> observables )
    {
        return new ExpressionList<E>( expression, observables ).readOnlyList;
    }

    /**
     * Returns a new ObservableList that contains all the elements of the given
     * lists, and keeps the new list in sync with any changes to the original
     * lists. The order of the elements are guaranteed to correspond to the order
     * of the elements in the sublists -- but as a consequence, this method is
     * inefficient and recreates the whole list on any change.
     * 
     * @param listsToConcat
     * @return
     */
    public static final <T> ObservableList<T> concat(
            final ObservableList<? extends ObservableList<? extends T>> listsToConcat )
    {
        return fromExpression( new Callable<Iterable<T>>()
        {
            @Override
            public Iterable<T> call() throws Exception
            {
                return Iterables.concat( listsToConcat );
            }
        }, listsToConcat );
    }

    @SafeVarargs
    public static final <T> ObservableList<T> concat( ObservableList<? extends T> first,
            ObservableList<? extends T> second, ObservableList<? extends T>... rest )
    {
        return concat( FXCollections.observableList( Lists.asList( first, second, rest ) ) );
    }

    @SuppressWarnings( "serial" )
    private static class ExpressionList<E> extends ArrayList<E>
    {
        private final ListChangeListener<Observable> observablesListListener = new ListChangeListener<Observable>()
        {
            @Override
            public void onChanged( ListChangeListener.Change<? extends Observable> change )
            {
                while( change.next() )
                {
                    for( Observable elem : change.getAddedSubList() )
                    {
                        elem.addListener( weakObservableListener );
                    }
                    for( Observable elem : change.getRemoved() )
                    {
                        elem.removeListener( weakObservableListener );
                    }
                }
            }
        };
        private final ListChangeListener<Observable> weakObservablesListListener = new WeakListChangeListener<>(
                observablesListListener );

        private final InvalidationListener observableListener = new InvalidationListener()
        {
            @Override
            public void invalidated( Observable arg0 )
            {
                if( Platform.isFxApplicationThread() )
                {
                    try
                    {
                        list.setAll( Lists.newArrayList( expression.call() ) );
                    }
                    catch( Exception e )
                    {
                        throw new RuntimeException( e );
                    }
                }
                else
                    Platform.runLater( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                list.setAll( Lists.newArrayList( expression.call() ) );
                            }
                            catch( Exception e )
                            {
                                throw new RuntimeException( e );
                            }
                        }
                    } );
            }
        };

        private final WeakInvalidationListener weakObservableListener = new WeakInvalidationListener( observableListener );

        private final ObservableList<E> list;
        private final ObservableList<E> readOnlyList;
        private final Callable<? extends Iterable<E>> expression;
        @SuppressWarnings( "unused" )
        private ObservableList<? extends Observable> observables; // Needs to be a field to avoid GC.

        private ExpressionList( Callable<? extends Iterable<E>> expression,
                ObservableList<? extends Observable> observables )
        {
            this.expression = expression;
            this.observables = observables;

            list = FXCollections.observableList( this );
            readOnlyList = FXCollections.unmodifiableObservableList( list );

            observables.addListener( weakObservablesListListener );
            observables.addListener( weakObservableListener );
            for( Observable observable : observables )
            {
                observable.addListener( weakObservableListener );
            }

            observableListener.invalidated( observables );
        }
    }


    public static <E> Set<E> getActuallyRemoved( ListChangeListener.Change<E> c )
    {
        if( c.wasRemoved() )
        {
            return ImmutableSet
                    .copyOf( Sets.difference( newHashSet( c.getRemoved() ), newHashSet( c.getAddedSubList() ) ) );
        }
        return ImmutableSet.of();
    }
}