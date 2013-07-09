package org.mdpnp.clinicalscenarios.client.tag;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

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
	
	public TagsManagementPanel(TagRequestFactory tagRequestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		TagRequest taReq = tagRequestFactory.tagRequest();
		driver.initialize(tagRequestFactory, this);
		
		list.insertRow(0);
		list.getRowFormatter().addStyleName(0, "tableHeader"); //TODO Style this table
		
		for(int j = 0; j < headers.length; j++) {
			
			list.setText(0, j, headers[j]);
		}
		

		taReq.findAll().to(new Receiver<List<TagProxy>>() {
			@Override
			public void onSuccess(List<TagProxy> response) {
				
				for(int i = 0; i < response.size(); i++) {
					list.insertRow(i + 1);
					TagProxy u = response.get(i);
//					list.setText(1 + i, 0, u.getName());
//					list.setText(1 + i, 1, u.getDescription());
					TextBox tName = new TextBox();
					tName.setText(u.getName());
					TextBox tDescription = new TextBox();
					tDescription.setText(u.getDescription());
					tDescription.setWidth("200");
					list.setWidget(1 + i, 0, tName);
					list.setWidget(1 + i, 1, tDescription);
					
					Button bUpdate = new Button(); bUpdate.setText("Update");//update tag Button
					Button bDelete = new Button(); bDelete.setText("Delete");//delete tag Button
					list.setWidget(1 + i, 3, bUpdate);
					list.setWidget(1 + i, 4, bDelete);
	

				}
				
			}
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
				Window.alert(error.getMessage());
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
		final int rows = list.getRowCount();
		list.insertRow(rows);
		TextBox tName = new TextBox();
		tName.setMaxLength(25);//max length of text
		TextBox tDescription = new TextBox();	
		tDescription.setWidth("600px");
		tDescription.setMaxLength(100);//Max length of thex 100 car
		Button bUpdate = new Button(); bUpdate.setText("Update");//update tag Button
		Button bDelete = new Button(); bDelete.setText("Delete");//delete tag Button
		
		list.setWidget(rows, 0, tName);
		list.setWidget(rows, 1, tDescription);
		list.setWidget(rows, 3, bUpdate);
		list.setWidget(rows, 4, bDelete);
		
		bUpdate.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//on click, save the appropiate TAG
//				Key tagKey = KeyFactory.createKey("TagList", "D");
//				Entity myTag = new Entity("Tag", tagKey);
//				myTag.setProperty("name", currentTag.getName());
//				myTag.setProperty("description", currentTag.getDescription());
//				
//				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//				datastore.put(myTag);
//				
				TagRequest tagRequest = (TagRequest)driver.flush();				
				tagRequest.persist().using(currentTag).with(driver.getPaths()).to(new Receiver<TagProxy>() {

					@Override
					public void onSuccess(TagProxy response) {
						// TODO Auto-generated method stub
						//XXX see how its done in user info panel
//						scenarioRequestFactory.getEventBus().fireEvent(new EntityProxyChange<ScenarioProxy>(response, WriteOperation.UPDATE));
//						setCurrentScenario(response);
//						
						saveHandler.onSave(response);
					}
					
					@Override
					public void onFailure(ServerFailure error) {
						super.onFailure(error);
						Window.alert(error.getMessage());
					}
				}).fire();
	
			}
		});
	
	}

}
