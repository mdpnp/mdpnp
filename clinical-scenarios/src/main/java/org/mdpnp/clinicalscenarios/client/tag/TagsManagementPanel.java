package org.mdpnp.clinicalscenarios.client.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * 
 * @author diego@mdpnp.org
 *         <p>
 *         Tags management panel
 *         <p>
 *         XXX As Jeff pointed, we might have a problem w/ this list of items
 *         when the list is really long. We should look for a way of using
 *         ListEditor instead
 * 
 */
public class TagsManagementPanel extends Composite implements Editor<TagProxy> {

	private static TagManagementPanelUiBinder uiBinder = GWT
			.create(TagManagementPanelUiBinder.class);

	interface TagManagementPanelUiBinder extends
			UiBinder<Widget, TagsManagementPanel> {
	}

	interface Driver extends
			RequestFactoryEditorDriver<TagProxy, TagsManagementPanel> {

	}

	Driver driver = GWT.create(Driver.class);
	TagRequestFactory tagRequestFactory;
	private TagProxy currentTag;
	private Set<String> allTagNames = new HashSet<String>();//Aux structure to check the different tags (keep them in lowercase)
	private List<TagProxy> listTags = new ArrayList<TagProxy>();//list of tags
	private TagComparator  tagComparator = new TagComparator();

	private TextBox newTagTextbox = new TextBox();
	private Button buttonAddTag = new Button("Add tag");
	
	private final static String STYLE_HOVER_CROSS = "hoverCross";
	private final int COLUMNS_NUMBER = 5;
	private final String COLUMN_WIDTH = "100px";

	@UiField
	FlexTable list; //TODO sooner or later this should be a Grid component

	// @UiField
	// Button addNewTag;

	public TagsManagementPanel(final TagRequestFactory tagRequestFactory) {
		this.tagRequestFactory = tagRequestFactory;
		initWidget(uiBinder.createAndBindUi(this));

		driver.initialize(tagRequestFactory, this);
		newTagTextbox.addClickHandler(newTagTextBoxClickHandler);
		newTagTextbox.addKeyUpHandler(buttonAddKeyUpEventHandler);
		buttonAddTag.addClickHandler(buttonAddClickHandler);
		getTagsList();
	}

	/**
	 * Click handler for the tag textbox. On click it requests a new entity
	 */
	private ClickHandler newTagTextBoxClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if(null==currentTag || null==newTagTextbox.getText() || newTagTextbox.getText().trim().endsWith("") ){
				tagRequestFactory.tagRequest().create()
				.fire(new Receiver<TagProxy>() {
					@Override
					public void onFailure(ServerFailure error) {
//						Window.alert("Failed to create Tag ... "+ error.getMessage());
						super.onFailure(error);
					}

					@Override
					public void onSuccess(final TagProxy response) {
						currentTag = response;
					}
				});
			}
		}
	};

	/**
	 * ClickHandler for the add button. On click it persists the tag
	 */
	private ClickHandler buttonAddClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			TagRequest request = tagRequestFactory.tagRequest();
			String tagname = newTagTextbox.getText();
			if (null == currentTag || null == tagname
					|| tagname.trim().equals("")) {
				Window.alert("Please, insert a tag ");
				return;
			}
			if (allTagNames.contains(tagname.toLowerCase())) {
				// Window.alert("Tag "+tagname+" already exists");
				newTagTextbox.setText("");
				return;
			}

			currentTag = request.edit(currentTag);
			currentTag.setName(tagname);

			request.persist().using(currentTag).fire(new Receiver<TagProxy>() {
				@Override
				public void onSuccess(TagProxy response) {
//					Window.alert("Successfully saved the modified Tag");
					listTags.add(response);
					Collections.sort(listTags, tagComparator);
					drawTagTable();
				}

				@Override
				public void onFailure(ServerFailure error) {
//					Window.alert("Failed to save the modified tag "	+ error.getMessage());
					super.onFailure(error);
				}

			});

		}
	};
	
	/**
	 * Key event; for when the user hits 'enter' in the tag textbox
	 */
	private KeyUpHandler buttonAddKeyUpEventHandler = new KeyUpHandler() {
		
		@Override
		public void onKeyUp(KeyUpEvent event) {
			if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER) {
				TagRequest request = tagRequestFactory.tagRequest();
				String tagname = newTagTextbox.getText();
				if (null == currentTag || null == tagname
						|| tagname.trim().equals("")) {
					Window.alert("Please, insert a tag ");
					return;
				}
				if (allTagNames.contains(tagname.toLowerCase())) {
					// Window.alert("Tag "+tagname+" already exists");
					newTagTextbox.setText("");
					return;
				}

				currentTag = request.edit(currentTag);
				currentTag.setName(tagname);
				request.persist().using(currentTag).fire(new Receiver<TagProxy>() {
					
					public void onSuccess(TagProxy response) {
//						Window.alert("Successfully saved the modified Tag");
						listTags.add(response);
						Collections.sort(listTags, tagComparator);
						drawTagTable();
					}

					public void onFailure(ServerFailure error) {
//						Window.alert("Failed to save the modified tag "	+ error.getMessage());
						super.onFailure(error);
					}
				});				
			}			
		}
	};
	
	/**
	 * Get the list with all the tags
	 */
	private void getTagsList(){
		
		TagRequest taReq = tagRequestFactory.tagRequest();
		taReq.findAll().to(new Receiver<List<TagProxy>>() {
			@Override
			public void onSuccess(List<TagProxy> response) {
				listTags = response;
				Collections.sort(listTags, tagComparator);
				drawTagTable();
			}
			
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
				// Window.alert(error.getMessage());
			}
		}).fire();
		
	}
	

	/**
	 * Draws the table to display the tags
	 */
	public void drawTagTable() {
		list.removeAllRows();
		newTagTextbox.setText("");
		newTagTextbox.setWidth(COLUMN_WIDTH);

		list.insertRow(0);
		list.setWidget(0, 0, newTagTextbox);
		list.setWidget(0, 1, buttonAddTag);
		int position = 0;
		if(null!=listTags && listTags.size()>0){
			for(TagProxy tag: listTags){
				final TagProxy tagProxy = tag;
				if(position%COLUMNS_NUMBER==0){
					list.insertRow(position/COLUMNS_NUMBER +1);
				}
				allTagNames.add(tagProxy.getName().toLowerCase());
				Label lName = new Label(tagProxy.getName());
				lName.setWidth(COLUMN_WIDTH);
				list.setWidget(position/COLUMNS_NUMBER+1, position%COLUMNS_NUMBER, lName);
				lName.setStyleName(STYLE_HOVER_CROSS);
				lName.setTitle("click to delete \""+ tagProxy.getName() +"\" tag");
				
				//on click, ask for confirmation, delete the tag
				lName.addClickHandler(new ClickHandler() {
					
					public void onClick(ClickEvent event) {
						boolean confirm = Window.confirm("Are you sure you want to delete tag \""+tagProxy.getName()+"\"?");
						if(confirm){
							TagRequest request = tagRequestFactory.tagRequest();
							TagProxy mutableTagProxy = request.edit(tagProxy);
							request.remove().using(mutableTagProxy).fire();
							listTags.remove(tagProxy);
							Collections.sort(listTags, tagComparator);
							allTagNames.remove(tagProxy.getName().toLowerCase());
							drawTagTable();
						}
					}
				});
				position++;
			}
		}
		
		
	}

	// getters and setters
	public TagProxy getCurrentTag() {
		return currentTag;
	}

	public void setCurrentTag(TagProxy currentTag) {
		TagRequest context = tagRequestFactory.tagRequest();
		currentTag = context.edit(currentTag);
		driver.edit(currentTag, context);
		this.currentTag = currentTag;

	}

	public interface SaveHandler {
		void onSave(TagProxy tagProxy);
	}

	// setter
	SaveHandler saveHandler;

	public void setSaveHandler(SaveHandler saveHandler) {
		this.saveHandler = saveHandler;
	}


	// ----------------------------------------------------------
	/**
	 * compares Tags lexicographically by tag name
	 * @author diego@mdpnp.org
	 *
	 */
	private class TagComparator implements Comparator<TagProxy> {

		public int compare(TagProxy o1, TagProxy o2) {
			o1.getName().compareToIgnoreCase(o2.getName());
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
		
	}

}
