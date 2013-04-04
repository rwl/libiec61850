package com.libiec61850.scl.model;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import com.libiec61850.scl.ParserUtils;
import com.libiec61850.scl.SclParserException;

public class DataSet {

	private String name = null;
	private String desc = null;
	
	List<FunctionalConstraintData> fcda = null;

	public DataSet(Node dataSet, IED ied) throws SclParserException {
		this.name = ParserUtils.parseAttribute(dataSet, "name");
		this.desc = ParserUtils.parseAttribute(dataSet, "desc");
		
		if (this.name == null)
			throw new SclParserException("Dataset misses required attribute \"name\"");
		
		fcda = new LinkedList<FunctionalConstraintData>();
		
		List<Node> fcdaNodes = ParserUtils.getChildNodesWithTag(dataSet, "FCDA");
		for (Node fcdaNode : fcdaNodes) {
			fcda.add(new FunctionalConstraintData(fcdaNode, ied));
		}
		
		
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public List<FunctionalConstraintData> getFcda() {
		return fcda;
	}
	
}
