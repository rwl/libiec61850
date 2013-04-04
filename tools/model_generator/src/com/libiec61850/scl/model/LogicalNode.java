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

import org.w3c.dom.Node;

import com.libiec61850.scl.DataObjectDefinition;
import com.libiec61850.scl.ParserUtils;
import com.libiec61850.scl.SclParserException;
import com.libiec61850.scl.types.LogicalNodeType;
import com.libiec61850.scl.types.SclType;
import com.libiec61850.scl.types.TypeDeclarations;

public class LogicalNode {

	private String lnClass;
	private String lnType;
	private String inst;
	private String desc;
	
	private List<DataObject> dataObjects;
	private List<DataSet> dataSets;
	
	public LogicalNode(Node lnNode, TypeDeclarations typeDeclarations, IED ied) throws SclParserException {
		this.lnClass = ParserUtils.parseAttribute(lnNode, "lnClass");
		this.lnType = ParserUtils.parseAttribute(lnNode, "lnType");
		this.inst = ParserUtils.parseAttribute(lnNode, "inst");
		this.desc = ParserUtils.parseAttribute(lnNode, "desc");
		
		if ((this.lnClass == null) || (this.lnType == null) || (this.inst == null))
			throw new SclParserException("required attribute is missing in logical node.");
		
		//instantiate DataObjects
		SclType sclType = typeDeclarations.lookupType(this.lnType);
		
		if (sclType == null)
			throw new SclParserException("missing type declaration " + this.lnType);
		
		if (sclType instanceof LogicalNodeType) {
			dataObjects = new LinkedList<DataObject>();
			
			LogicalNodeType type = (LogicalNodeType) sclType;
			
			List<DataObjectDefinition> doDefinitions = type.getDataObjectDefinitions();
			
			for (DataObjectDefinition doDefinition : doDefinitions) {
				dataObjects.add(new DataObject(doDefinition, typeDeclarations));
			}
			
			
		}
		else throw new SclParserException("wrong type " + this.lnType + " for logical node");
		
		/* Parse data set definitions */
		dataSets = new LinkedList<DataSet>();
		
		List<Node> dataSetNodes = ParserUtils.getChildNodesWithTag(lnNode, "DataSet");
		for (Node dataSet : dataSetNodes) {
			dataSets.add(new DataSet(dataSet, ied));
		}
			
	}

	public String getLnClass() {
		return lnClass;
	}

	public String getLnType() {
		return lnType;
	}

	public String getInst() {
		return inst;
	}

	public String getDesc() {
		return desc;
	}
	
	public String getName() {
		return lnClass + inst;
	}

	public List<DataObject> getDataObjects() {
		return dataObjects;
	}

	public List<DataSet> getDataSets() {
		return dataSets;
	}
	
	
	
}
