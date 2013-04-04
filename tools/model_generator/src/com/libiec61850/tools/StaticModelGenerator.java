package com.libiec61850.tools;

/*
 *  StaticModelGenerator.java
 *
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.libiec61850.scl.SclParser;
import com.libiec61850.scl.SclParserException;
import com.libiec61850.scl.model.AccessPoint;
import com.libiec61850.scl.model.DataAttribute;
import com.libiec61850.scl.model.DataObject;
import com.libiec61850.scl.model.DataSet;
import com.libiec61850.scl.model.FunctionalConstraintData;
import com.libiec61850.scl.model.IED;
import com.libiec61850.scl.model.LogicalDevice;
import com.libiec61850.scl.model.LogicalNode;
import com.libiec61850.scl.model.Server;

public class StaticModelGenerator {

	public static void main(String[] args) throws FileNotFoundException, SclParserException {
		if (args.length < 1) {
			System.out.println("Usage: genmodel <ICD file>");
			System.exit(1);
		}
		
		String icdFile = args[0];
		
		InputStream stream = new FileInputStream(icdFile);
		
		SclParser sclParser = new SclParser(stream);
			
		IED ied = sclParser.getIed();
			
		AccessPoint accessPoint = ied.getAccessPoint();
						
		printHeader(icdFile);
				
		String iedDevices = "static LogicalDevice* logicalDevices[] = {";
							
		Server server = accessPoint.getServer();
				
		boolean first = true;
				
		int ldCount = 0;
		
		for (LogicalDevice logicalDevice : server.getLogicalDevices()) {
		
			String ldPrefix = "ld" + ldCount;
					
			printLogicalNodes(ldPrefix, logicalDevice);
			
			String logicalDeviceName = logicalDevice.getLdName();
			
			if (logicalDeviceName == null)
				logicalDeviceName = ied.getName() + logicalDevice.getInst();
					
			System.out.println("static LogicalDevice " + ldPrefix
					+ " = {\"" 	+ logicalDeviceName + "\", "
					+ ldPrefix + "_lnodes"
					+ "};");
							
			ldCount ++;
					
			if (!first) {
				iedDevices = iedDevices + ", ";
			}
			else
				first = false;
					
			iedDevices = iedDevices + "&" + ldPrefix;
		}
				
		iedDevices += ", NULL};";
		
		System.out.println(iedDevices);
		
		System.out.println("\nIedModel staticIedModel = {\"" + accessPoint.getName() +
				"\", logicalDevices};");
						
	}

	private static void printHeader(String filename) {
		
		System.out.println("/*");
		System.out.println(" * static_model.c");
		System.out.println(" *");
		System.out.println(" * automatically generated from " + filename);
		System.out.println(" */");
		System.out.println("#include <stdlib.h>");
		System.out.println("#include \"model.h\"");
		System.out.println();
	}
	
	private static void printLogicalNodes(String ldPrefix, LogicalDevice logicalDevice) {		
		List<LogicalNode> logicalNodes = logicalDevice.getLogicalNodes();
		
		int lnCount = 0;		
		boolean first = true;
		
		String lnodes = "static LogicalNode* " + ldPrefix + "_lnodes[] = {";
		
		for (LogicalNode logicalNode : logicalNodes) {
			String lnPrefix = ldPrefix + "ln" + lnCount; 
			
			printDataObjects(lnPrefix, logicalNode);
			
			boolean hasDataSets = printDataSets(lnPrefix, logicalNode);
			
			String lnName = logicalNode.getLnClass() + logicalNode.getInst();
			System.out.print("static LogicalNode " + lnPrefix +
					" = {\"" + lnName + "\", " + lnPrefix + "_dataObjects");
			
			if (hasDataSets)
				System.out.println(", " + lnPrefix + "_dataSets};");
			else
				System.out.println(", NULL};");
			
			if (!first) {
				lnodes = lnodes + ", ";
			}
			else
				first = false;
			
			lnodes = lnodes + "&" + lnPrefix;
			
			lnCount++;
		}
		
		lnodes += ", NULL};";
		
		System.out.println(lnodes);
	}

	private static boolean printDataSets(String lnPrefix, LogicalNode logicalNode) {
		List<DataSet> dataSets = logicalNode.getDataSets();
		
		if (dataSets.size() == 0) return false;
		
		String dataSetsString = "static DataSet* " + lnPrefix + "_dataSets[] = {";
		
		boolean firstDataSet = true;
		
		int dataSetNumber = 0;
		
		for (DataSet dataSet : dataSets) {
			
			String dataSetName = lnPrefix + "_dataset" + dataSetNumber;
			
			String fcdaString = "static FunctionalConstrainedData* " + dataSetName + "_fcdas[] = {";
			
			boolean firstFCDA = true;
			int fcdaCount = 0;
			
			for (FunctionalConstraintData fcda : dataSet.getFcda()) {
				String fcdaName = dataSetName + "_fcda" + fcdaCount;
				
				System.out.println("//   FCDA: " + fcda.toString());
				
				System.out.print("FunctionalConstrainedData " + fcdaName + " = {");
				System.out.println("\"" + fcda.toString() + "\", " + fcda.getFc() + "};");
				
				if (firstFCDA) {
					fcdaString += "&" + fcdaName;
					firstFCDA = false;
				}
				else {
					fcdaString += ", &" + fcdaName;
				}
				
				fcdaCount ++;
			}
			
			fcdaString += ", NULL};";
			
			System.out.println(fcdaString);
			
			System.out.print("static DataSet " + dataSetName);
			System.out.println(" = {\"" + dataSet.getName() + "\", " + dataSetName +"_fcdas};");
			
			if (firstDataSet){
				dataSetsString += "&" + dataSetName;
				firstDataSet = false;
			}
			else
				dataSetsString += ", &" + dataSetName;
			
			dataSetNumber++;
		}
		
		dataSetsString += ", NULL};";
		System.out.println(dataSetsString);
		
		return true;
	}

	private static void printDataObjects(String lnName, LogicalNode logicalNode) {
		
		String dataObjectsStr = "static DataObject* " + lnName + "_dataObjects[] = {";
		
		List<DataObject> dataObjects = logicalNode.getDataObjects();
		
		String dataObjectName = "";
		int dataObjectCount = 0;
		
		boolean firstObject = true;
		
		for (DataObject dataObject : dataObjects) {
			
			if ((dataObject.getDataAttributes().size() == 0) && (dataObject.getSubDataObjects().size() == 0)) {
				System.out.println("/* WARNING - data object " + dataObject.getName() + " has no children! */");
				continue;
			}

			dataObjectName = lnName + "do" + dataObjectCount;
			dataObjectCount++;
			
			int res = printDataObjectChildren(dataObjectName, dataObject);

			System.out.print("static DataObject " + dataObjectName);
			System.out.print(" = {\"" + dataObject.getName() +  "\", "); 
			
			if ((res & 0x02) == 2)
				System.out.print(dataObjectName + "_subDataObjects, ");
			else
				System.out.print("NULL, ");
			
			if ((res & 0x01) == 1)
				System.out.print(dataObjectName + "_dataAttributes};");
			else
				System.out.print("NULL};");
			
			System.out.println(" /* " + dataObject.toString() + " */");

			if (!firstObject) {
				dataObjectsStr += ", ";
			}
			else
				firstObject = false;

			dataObjectsStr = dataObjectsStr + "&" + dataObjectName;	
		}
			
		dataObjectsStr += ", NULL};";		
		System.out.println(dataObjectsStr);
	}

	private static int /* bit 1 - has attributes, bit 2 - has sub objects */ 
	printDataObjectChildren(String dataObjectName, DataObject dataObject) {
		int retVal = 0;
		int dataAttributeCount = 0; /* DataAttribute counter */
		int dataObjectCount = 0;
		boolean firstAttribute = true;
		boolean firstSubDataObject = true;
		
		String dataAttributes = "static DataAttribute* " + dataObjectName + "_dataAttributes[] = {";
		String subDataObjects = "static DataObject* " + dataObjectName + "_subDataObjects[] = {"; 
		
		for (DataObject subDataObject : dataObject.getSubDataObjects()) {
			String subDataObjectName = printSubDataObject(dataObjectName, dataObjectCount, subDataObject);
			
			if (!firstSubDataObject)
				subDataObjects += ", ";
			else
				firstSubDataObject = false;
			
			subDataObjects = subDataObjects + "&" + subDataObjectName;
			
			dataObjectCount++;
		}
		
		for (DataAttribute dataAttribute : dataObject.getDataAttributes()) {
			String dataAttributeName;
			
			dataAttributeName = printDataAttribute(dataObjectName,
					dataAttributeCount, dataAttribute);
			
			if (!firstAttribute)
				dataAttributes += ", ";
			else
				firstAttribute = false;

			dataAttributes = dataAttributes + "&" + dataAttributeName;

			dataAttributeCount++;			
		}
		
		if (dataAttributeCount > 0) {
			System.out.println(dataAttributes + ", NULL};");
			retVal += 1;
		}
		
		if (dataObjectCount > 0) {
			System.out.println(subDataObjects + ", NULL};");
			retVal += 2;
		}
		
		return retVal;
	}
	
	
	private static String printSubDataObject(String dataObjectName,
			int subDataObjectCount, DataObject dataObjectNode)
	{	
		DataObject dataObject = (DataObject) dataObjectNode;
		
		String subDataObjectName = dataObjectName + "sdo" + subDataObjectCount;
		
		int res = printDataObjectChildren(subDataObjectName, dataObject);
		
		System.out.print("static DataObject " + subDataObjectName + " = {");
		System.out.print("\"" + dataObject.getName() + "\", ");
		
		if ((res & 0x02) == 2)
			System.out.print(subDataObjectName + "_subDataObjects, ");
		else
			System.out.print("NULL, ");
		
		if ((res & 0x01) == 1)
			System.out.println(subDataObjectName + "_dataAttributes};");
		else
			System.out.println("NULL);");
			
		return subDataObjectName;
	}

	private static String printDataAttribute(String dataObjectName,
			int dataAttributeCount, DataAttribute dataAttribute) 
	{
		String dataAttributeName = null;
		
		if (dataAttribute.isBasicAttribute()) {
			int arrayCount = dataAttribute.getCount();
			
			dataAttributeName = dataObjectName + "bda" + dataAttributeCount;
			System.out.print("static DataAttribute " + dataAttributeName + " = {");
			System.out.print("\"" + dataAttribute.getName() + "\", " + dataAttribute.getFc());
			System.out.println(", " + dataAttribute.getType() + ", " + arrayCount + ", NULL, NULL};");
		}
		else  {
			dataAttributeName = dataObjectName + "cda" + dataAttributeCount;
			
			String subDataAttributes = "static DataAttribute* " + dataAttributeName + "_subDataAttributes[] = {";
			int subDataAttributeCount = 0;
			boolean firstSubAttribute = true;
			
			for (DataAttribute basicAttribute : dataAttribute.getSubDataAttributes()) {
				String subDataAttributeName;
				
				subDataAttributeName = printDataAttribute(dataAttributeName, subDataAttributeCount, basicAttribute);
				
				if (!firstSubAttribute)
					subDataAttributes += ", ";
				else
					firstSubAttribute = false;

				subDataAttributes = subDataAttributes + "&" + subDataAttributeName;
				
				subDataAttributeCount++;
			}
			System.out.println(subDataAttributes + ", NULL};");
			
			int arrayCount = dataAttribute.getCount();
			
			System.out.print("static DataAttribute " + dataAttributeName + " = {");
			System.out.print("\"" + dataAttribute.getName() + "\", " + dataAttribute.getFc());
			System.out.println(", CONSTRUCTED, " + arrayCount + ", " + dataAttributeName + "_subDataAttributes" + ", NULL};");
			
		}
		return dataAttributeName;
	}

}
