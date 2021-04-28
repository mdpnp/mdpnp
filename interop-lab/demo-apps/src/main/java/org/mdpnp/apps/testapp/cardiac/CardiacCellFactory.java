package org.mdpnp.apps.testapp.cardiac;

import java.text.Format;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.scene.paint.*;
import javafx.scene.layout.*;

public class CardiacCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private TextAlignment alignment;
    private Format format;

    public TextAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }
    
    BackgroundFill redFill=new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
    Background redBackground=new Background(redFill);
    
    BackgroundFill yellowFill=new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
    Background yellowBackground=new Background(yellowFill);
    
    BackgroundFill greenFill=new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY);
    Background greenBackground=new Background(greenFill);


    @Override
    @SuppressWarnings("unchecked")
    public TableCell<S, T> call(TableColumn<S, T> p) {
        TableCell<S, T> cell = new TableCell<S, T>() {

            @Override
            public void updateItem(Object item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem((T) item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else if (format != null) {
                    super.setText(format.format(item));
                } else if (item instanceof Node) {
                    super.setText(null);
                    super.setGraphic((Node) item);
                } else {
                	//This shouldn't be used for anything that isn't but just in case...
                	if(item instanceof Boolean) {
                		boolean b=((Boolean)item).booleanValue();
                		if(!b) {
                			/*
                			 * This is unpleasant, but we can't otherwise get the index.
                			 * Perhaps we should have a non primitive boolean wrapper in
                			 * the model that could then be switched by type to check what
                			 * we were dealing with... 
                			 */
                			int index=p.getTableView().getColumns().indexOf(p);
                			switch(index) {
                			case 2:
                			case 3:
                				setBackground(redBackground);
                				break;
                			case 1:
                			case 4:
                			case 5:
                				setBackground(yellowBackground);
                				break;
            				default:
            					break;
                			}
                		} else {
                			setBackground(greenBackground);
                		}
                	}
                    super.setText(item.toString());
                    super.setGraphic(null);
                }
            }
        };
        cell.setTextAlignment(alignment);
        switch (alignment) {
            case CENTER:
                cell.setAlignment(Pos.CENTER);
                break;
            case RIGHT:
                cell.setAlignment(Pos.CENTER_RIGHT);
                break;
            default:
                cell.setAlignment(Pos.CENTER_LEFT);
                break;
        }
        return cell;
    }
}