package com.libiec61850.scl.model;

/*
 *  Copyright 2013 Michael Zillgith
 *
 *	This file is part of libIEC61850.
 *
 *	libIEC61850 is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	libIEC61850 is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with libIEC61850.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	See COPYING file for the complete license text.
 */

import java.util.LinkedList;
import java.util.List;

import com.libiec61850.scl.DataAttributeDefinition;
import com.libiec61850.scl.DataObjectDefinition;
import com.libiec61850.scl.SclParserException;
import com.libiec61850.scl.types.DataObjectType;
import com.libiec61850.scl.types.SclType;
import com.libiec61850.scl.types.TypeDeclarations;

public class DataObject {

	private String name;
	
	private int count;
	
	private List<DataAttribute> dataAttributes = null;
	private List<DataObject> subDataObjects = null;
	
	public DataObject(DataObjectDefinition doDefinition, TypeDeclarations typeDeclarations) 
			throws SclParserException 
	{
		this.name = doDefinition.getName();
		this.count = doDefinition.getCount();
		
		SclType sclType = typeDeclarations.lookupType(doDefinition.getType());
		
		if (sclType == null)
			throw new SclParserException("type declaration missing for data object.");
		
		if (!(sclType instanceof DataObjectType))
			throw new SclParserException("type declaration of wrong type.");
		
		DataObjectType doType = (DataObjectType) sclType;
		
		createDataAttributes(typeDeclarations, doType);
			
		createSubDataObjects(typeDeclarations, doType);	
	}

	private void createSubDataObjects(TypeDeclarations typeDeclarations,
			DataObjectType doType) throws SclParserException 
	{
		this.subDataObjects = new LinkedList<DataObject>();
		
		List<DataObjectDefinition> sdoDefinitions = doType.getSubDataObjects();
		
		for (DataObjectDefinition sdoDefinition : sdoDefinitions) {
			this.subDataObjects.add(new DataObject(sdoDefinition, typeDeclarations));
		}
	}

	private void createDataAttributes(TypeDeclarations typeDeclarations,
			DataObjectType doType) throws SclParserException 
	{
		List<DataAttributeDefinition> daDefinitions = doType.getDataAttributes();
		
		this.dataAttributes = new LinkedList<DataAttribute>();
		
		for (DataAttributeDefinition daDefinition : daDefinitions) {
			this.dataAttributes.add(new DataAttribute(daDefinition, typeDeclarations, null));
		}
	}

	public String getName() {
		return name;
	}

	public List<DataAttribute> getDataAttributes() {
		return dataAttributes;
	}

	public List<DataObject> getSubDataObjects() {
		return subDataObjects;
	}

	public int getCount() {
		return count;
	}
	
}
