package org.mdpnp.clinicalscenarios.client.tag;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * 
 * @author dalonso@mdpnp.org <p>
 * Tags management panel <p>
 * XXX As Jeff pointed, we might have a problem w/ this list of items when the list is really long. 
 * We should look for a way of using ListEditor instead
 *
 */
public class TagsManagementPanel extends Composite implements Editor<TagProxy>{

	private static TagManagementPanelUiBinder uiBinder = GWT.create(TagManagementPanelUiBinder.class);

	interface TagManagementPanelUiBinder extends UiBinder<Widget, TagsManagementPanel> {
	}
	
	interface Driver extends RequestFactoryEditorDriver<TagProxy, TagsManagementPanel> {
		
	}

	Driver driver = GWT.create(Driver.class);
	TagRequestFactory tagRequestFactory;
	TagProxy currentTag;
	

	@UiField
	FlexTable list;
	
	@UiField 
	Button addNewTag;
	
	private static final String[] headers = new String[] {"Name", "Description"};
	
	public TagsManagementPanel(final TagRequestFactory tagRequestFactory) {
		this.tagRequestFactory = tagRequestFactory;
		initWidget(uiBinder.createAndBindUi(this));
		TagRequest taReq = tagRequestFactory.tagRequest();
		driver.initialize(tagRequestFactory, this);
		
		list.insertRow(0);
		list.getRowFormatter().addStyleName(0, "tableHeader"); //TODO Style this table
		
		for(int j = 0; j < headers.length; j++) {
			
			list.setText(0, j, headers[j]);
		}
		

		/**
		 * Populate the list w/ all the values
		 */
		taReq.findAll().to(new Receiver<List<TagProxy>>() {
			@Override
			public void onSuccess(List<TagProxy> response) {
				
				for(int i = 0; i < response.size(); i++) {
					final int row = i;//current row
					list.insertRow(i + 1);
					final TagProxy tagProxy = response.get(i);
					final TextBox tName = new TextBox();
					tName.setText(tagProxy.getName());
					final TextBox tDescription = new TextBox();
					tDescription.setText(tagProxy.getDescription());
					tName.setMaxLength(25);// max length of text
					tDescription.setWidth("600px");
					tDescription.setMaxLength(100);// Max length of thex 100 car
					list.setWidget(1 + i, 0, tName);
					list.setWidget(1 + i, 1, tDescription);
					
					Button bUpdate = new Button(); bUpdate.setText("Update");//update tag Button
					Button bDelete = new Button(); bDelete.setText("Delete");//delete tag Button
					list.setWidget(1 + i, 3, bUpdate);
					list.setWidget(1 + i, 4, bDelete);
					
					/**
					 * Add a click handler for the update button
					 */
					bUpdate.addClickHandler(new ClickHandler() {
										
						@Override
						public void onClick(ClickEvent event) {						
							TagRequest request = tagRequestFactory.tagRequest();
							TagProxy mutableTagProxy = request.edit(tagProxy);
							mutableTagProxy.setName(tName.getText());
							mutableTagProxy.setDescription(tDescription.getText());
							
							request.persist().using(mutableTagProxy).fire(new Receiver<TagProxy>() {

								@Override
								public void onSuccess(TagProxy response) {
//									Window.alert("Successfully saved the modified Tag");
								}
								@Override
								public void onFailure(ServerFailure error) {
//									Window.alert("Failed to save the modified tag "+error.getMessage());
									super.onFailure(error);
								}
								
							});
						}
					});
					
					/**
					 * Add click handler form he delete button
					 */
					bDelete.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							TagRequest request = tagRequestFactory.tagRequest();
							TagProxy mutableTagProxy = request.edit(tagProxy);

							//XXX clean hee a little bit and get rid of this alerts
							request.remove().using(mutableTagProxy).fire(/*new Receiver() {
								@Override
								public void onSuccess(Object response) {
									Window.alert("Successfully deleted the modified Tag");									
								}	
							}*/);

							list.removeRow(row);
						}
					});
	
				}
				
			}//on success
			
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
//				Window.alert(error.getMessage());
			}
		}).fire();
	}
	
	
	//getters and setters
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
	//setter
	SaveHandler saveHandler;
	public void setSaveHandler(SaveHandler saveHandler) {
		this.saveHandler = saveHandler;
	}
	
	@UiHandler("addNewTag")
	void onANTClick(ClickEvent click) {
		tagRequestFactory.tagRequest().create().fire(new Receiver<TagProxy>() {

			@Override
			public void onFailure(ServerFailure error) {
//				Window.alert("Failed to create Tag ... " + error.getMessage());
				super.onFailure(error);
			}
			
			@Override
			public void onSuccess(final TagProxy response) {
//				Window.alert("Successfully created a Tag... building UI components etc...");
				final int rows = list.getRowCount();
				list.insertRow(rows);
				final TextBox tName = new TextBox();
				tName.setMaxLength(25);//max length of text
				final TextBox tDescription = new TextBox();	
				tDescription.setWidth("600px");
				tDescription.setMaxLength(100);//Max length of thex 100 car
				Button bUpdate = new Button(); bUpdate.setText("Update");//update tag Button
				Button bDelete = new Button(); bDelete.setText("Delete");//delete tag Button
				
				list.setWidget(rows, 0, tName);
				list.setWidget(rows, 1, tDescription);
				list.setWidget(rows, 3, bUpdate);
				list.setWidget(rows, 4, bDelete);

				/**
				 * Add a click handler for the update button
				 */
				bUpdate.addClickHandler(new ClickHandler() {
									
					@Override
					public void onClick(ClickEvent event) {						
						TagRequest request = tagRequestFactory.tagRequest();
						TagProxy mutableTagProxy = request.edit(response);
						mutableTagProxy.setName(tName.getText());
						mutableTagProxy.setDescription(tDescription.getText());
						
						request.persist().using(mutableTagProxy).fire(new Receiver<TagProxy>() {

							@Override
							public void onSuccess(TagProxy response) {
//								Window.alert("Successfully saved the modified Tag");
							}
							@Override
							public void onFailure(ServerFailure error) {
//								Window.alert("Failed to save the modified tag "+error.getMessage());
								super.onFailure(error);
							}
							
						});
					}
				});
				
				/**
				 * Add click handler form he delete button
				 */
				bDelete.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						TagRequest request = tagRequestFactory.tagRequest();
						TagProxy mutableTagProxy = request.edit(response);
					
						request.remove().using(mutableTagProxy).fire();
						list.removeRow(rows);
					}
				});
			}
			
		});
		

	}
	
	//----------------------------------------------

}
