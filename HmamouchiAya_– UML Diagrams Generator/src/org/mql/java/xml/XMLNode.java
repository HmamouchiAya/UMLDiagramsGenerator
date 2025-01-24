package org.mql.java.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLNode {
	private Node node;

	public XMLNode(Node node) {
		this.node = node;
	}
	
	public XMLNode(String documentPath) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(documentPath);
			node = document.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public XMLNode firstChild() {
		return children().get(0);
	}
	
	public List<XMLNode> children() {
		NodeList children = node.getChildNodes();
		List<XMLNode> childrenList = new ArrayList<XMLNode>();
		for (int i = 0 ; i < children.getLength() ; i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				childrenList.add(new XMLNode(children.item(i)));
			}
		}
		return childrenList;
	}
	
	public XMLNode child(String name) {
		List<XMLNode> chilren = children();
		for (XMLNode node : chilren) {
			if (name.equals(node.getName())) {
				return node;
			}
		}
		return null;
	}
	
	public String getAttribute(String name) {
		NamedNodeMap attributes = node.getAttributes();
		return attributes.getNamedItem(name).getNodeValue();
	}
	
	public int getIntAttribute(String name) {
		return Integer.parseInt(getAttribute(name));
	}
	
	public String getName() {
		return node.getNodeName();
	}
 
	public String getValue() {
		return node.getChildNodes().item(0).getNodeValue();
	}
}