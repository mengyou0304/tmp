package com.robin.view;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.robin.source.DataSource;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tutorial.addressbook.FileUtility;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/* 
 * UI class is the starting point for your app. You may deploy it with VaadinServlet
 * or VaadinPortlet by giving your UI class name a parameter. When you browse to your
 * app a web page showing your UI is automatically generated. Or you may choose to 
 * embed your UI to an existing web page. 
 */
@Title("Addressbook")
@Theme("valo")
public class AddressbookUI extends UI {

	/* User interface components are stored in session. */
	private Table contactList = new Table();
	private TextField searchField = new TextField();
	private Button newPropertyButton = new Button("New property");
	private Button saveContactButton = new Button("Save this info");
	private FormLayout editorLayout = new FormLayout();
	private FieldGroup editorFields = new FieldGroup();
	
	private TextField[] keyfields=new TextField[20];
	private TextField[] valuefields=new TextField[20];
	
	private TextField nameField=null;
	private static int fieldindexer=-1;
	private ArrayList<TextField> originFieldList=new ArrayList<TextField>();
	
	

	private String[] fieldNames = null;
	private DataSource ds;

	/*
	 * Any component can be bound to an external data source. This example uses
	 * just a dummy in-memory list, but there are many more practical
	 * implementations.
	 */
	IndexedContainer contactContainer = createDummyDatasource();

	/*
	 * After UI class is created, init() is executed. You should build and wire
	 * up your user interface here.
	 */
	protected void init(VaadinRequest request) {
		initLayout();
		initContactList();
		initEditor();
		initSearch();
	}

	/*
	 * In this example layouts are programmed in Java. You may choose use a
	 * visual editor, CSS or HTML templates for layout instead.
	 */
	private void initLayout() {

		/* Root of the user interface component tree is set */
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		setContent(splitPanel);

		/* Build the component tree */
		VerticalLayout leftLayout = new VerticalLayout();
		leftLayout.addComponent(contactList);
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		leftLayout.addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(searchField);
		
		VerticalLayout rightLayout=new VerticalLayout();
		rightLayout.addComponent(editorLayout);
		
		splitPanel.addComponent(leftLayout);
		splitPanel.addComponent(rightLayout);
//		bottomLeftLayout.addComponent(addNewContactButton);

		/* Set the contents in the left of the split panel to use all the space */
		leftLayout.setSizeFull();

		/*
		 * On the left side, expand the size of the contactList so that it uses
		 * all the space left after from bottomLeftLayout
		 */
		leftLayout.setExpandRatio(contactList, 1);
		contactList.setSizeFull();

		/*
		 * In the bottomLeftLayout, searchField takes all the width there is
		 * after adding addNewContactButton. The height of the layout is defined
		 * by the tallest component.
		 */
		bottomLeftLayout.setWidth("100%");
		searchField.setWidth("100%");
		bottomLeftLayout.setExpandRatio(searchField, 1);

		/* Put a little margin around the fields in the right side editor */
		editorLayout.setMargin(true);
		editorLayout.setVisible(false);
	}

	private void initEditor() {
		HorizontalLayout thout=new HorizontalLayout ();
		
		thout.addComponent(saveContactButton);
		thout.addComponent(newPropertyButton);
		editorLayout.addComponent(thout);
		
		/* User interface can be created dynamically to reflect underlying data. */
		for (String fieldName : fieldNames) {
			TextField field = new TextField(fieldName);
			originFieldList.add(field);
			if(fieldName.equals("name"))
				nameField=field;
			editorLayout.addComponent(field);
			field.setWidth("100%");
			/*
			 * We use a FieldGroup to connect multiple components to a data
			 * source at once.
			 */
			editorFields.bind(field, fieldName);
		}
		
		newPropertyButton.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				fieldindexer++;
				keyfields[fieldindexer]=new TextField("key");
				valuefields[fieldindexer]=new TextField("value");
				HorizontalLayout hout=new HorizontalLayout ();
				hout.addComponent(keyfields[fieldindexer]);
				hout.addComponent(valuefields[fieldindexer]);
				editorLayout.addComponent(hout);
			}
		});
		
		saveContactButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				HashMap<String,String> map=new HashMap<String,String>();
				map.put("name", nameField.getValue());
				System.out.println("info from nameField: "+nameField.getValue());
				System.out.println("keyfields.length: "+keyfields.length);
				for(int i=0;i<=fieldindexer;i++){
					if(keyfields[i]==null)
						continue;
					String ks=keyfields[i].getValue();
					String vs=valuefields[i].getValue();
					map.put(ks, vs);
//					System.out.println("key: "+ks+" /t value: "+vs);
				}
				for(TextField field: originFieldList)
					map.put(field.getCaption(), field.getValue());
				ds.updateInfo(null, nameField.getValue(), map);
				
			}
		});

		/*
		 * Data can be buffered in the user interface. When doing so, commit()
		 * writes the changes to the data source. Here we choose to write the
		 * changes automatically without calling commit().
		 */
		editorFields.setBuffered(false);
	}

	private void initSearch() {

		/*
		 * We want to show a subtle prompt in the search field. We could also
		 * set a caption that would be shown above the field or description to
		 * be shown in a tooltip.
		 */
		searchField.setInputPrompt("Search contacts");

		/*
		 * Granularity for sending events over the wire can be controlled. By
		 * default simple changes like writing a text in TextField are sent to
		 * server with the next Ajax call. You can set your component to be
		 * immediate to send the changes to server immediately after focus
		 * leaves the field. Here we choose to send the text over the wire as
		 * soon as user stops writing for a moment.
		 */
		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);

		/*
		 * When the event happens, we handle it in the anonymous inner class.
		 * You may choose to use separate controllers (in MVC) or presenters (in
		 * MVP) instead. In the end, the preferred application architecture is
		 * up to you.
		 */
		searchField.addTextChangeListener(new TextChangeListener() {
			public void textChange(final TextChangeEvent event) {

				/* Reset the filter for the contactContainer. */
				contactContainer.removeAllContainerFilters();
				contactContainer.addContainerFilter(new ContactFilter(event
						.getText()));
			}
		});
	}

	/*
	 * A custom filter for searching names and companies in the
	 * contactContainer.
	 */
	private class ContactFilter implements Filter {
		private String needle;

		public ContactFilter(String needle) {
			this.needle = needle.toLowerCase();
		}

		public boolean passesFilter(Object itemId, Item item) {
			String haystack = ("" + item.getItemProperty("source").getValue()
					+ item.getItemProperty("name").getValue() + item
					.getItemProperty("category").getValue()).toLowerCase();
			return haystack.contains(needle);
		}

		public boolean appliesToProperty(Object id) {
			return true;
		}
	}


	private void initContactList() {
		contactList.setContainerDataSource(contactContainer);
		contactList.setVisibleColumns(new String[] { "source", "name",
				"category" });
		contactList.setSelectable(true);
		contactList.setImmediate(true);

		contactList.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object contactId = contactList.getValue();

				/*
				 * When a contact is selected from the list, we want to show
				 * that in our editor on the right. This is nicely done by the
				 * FieldGroup that binds all the fields to the corresponding
				 * Properties in our contact at once.
				 */
				if (contactId != null)
					editorFields.setItemDataSource(contactList
							.getItem(contactId));

				editorLayout.setVisible(contactId != null);
			}
		});
	}

	/*
	 * Generate some in-memory example data to play with. In a real application
	 * we could be using SQLContainer, JPAContainer or some other to persist the
	 * data.
	 */
	private IndexedContainer createDummyDatasource() {
		List<Map> infolist = getAllFromMongo();
		IndexedContainer ic = new IndexedContainer();
		for (String p : fieldNames) {
			ic.addContainerProperty(p, String.class, "");
		}
		/* Create dummy data by randomly combining first and last names */
		for (Map map : infolist) {
			Object id = ic.addItem();
			for (String p : fieldNames) {
				String key = p;
				String value = String.valueOf(map.get(key));
				ic.getContainerProperty(id, key).setValue(value);
			}
		}
		return ic;
	}

	public List<Map> getAllFromMongo() {
		ds = new DataSource();
		try {
			ds.getConnection();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		List<Map> infos = ds.queryInfo(null, "", "");
		Set<String> keyset = new HashSet<String>();
		for (Map map : infos)
			keyset.addAll(map.keySet());
		fieldNames = new String[keyset.size()];
		int j = 0;
		for (String key : keyset)
			fieldNames[j++] = key;
		return infos;
	}
}
