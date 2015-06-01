/**
 *
 */
package org.openforis.idm.model.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathInvalidSyntaxException;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.VariablePointer;
import org.openforis.commons.collection.CollectionUtils;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.expression.internal.ModelJXPathCompiledExpression;
import org.openforis.idm.model.expression.internal.ModelJXPathContext;
import org.openforis.idm.model.expression.internal.ModelNodePointer;
import org.openforis.idm.path.Path;

/**
 * @author M. Togna
 * @author G. Miceli
 */
public abstract class AbstractExpression {

	public static final String THIS_VARIABLE_NAME = "this";
	private static final QName THIS = new QName(THIS_VARIABLE_NAME);
	private ModelJXPathContext jxPathContext;
	private ModelJXPathCompiledExpression compiledExpression;

	AbstractExpression(ModelJXPathCompiledExpression compiledExpression, ModelJXPathContext jxPathContext) {
		this.compiledExpression = compiledExpression;
		this.jxPathContext = jxPathContext;
	}

	/**
	 * Returns the list of reference paths of this expression
	 */
	public Set<String> getReferencedPaths() {
		Set<String> paths = compiledExpression.getReferencedPaths();
		return CollectionUtils.unmodifiableSet(paths);
	}

	public Set<NodeDefinition> getReferencedNodeDefinitions(NodeDefinition context, NodeDefinition thisNodeDef) throws InvalidExpressionException {
		Set<NodeDefinition> result = new HashSet<NodeDefinition>();
		Set<String> paths = compiledExpression.getReferencedPaths();
		for (String path : paths) {
			NodeDefinition nodeDef = getReferencedNodeDefinition(context, thisNodeDef, path);
			result.add(nodeDef);
		}
		return result;
	}

	private NodeDefinition getReferencedNodeDefinition(NodeDefinition context, NodeDefinition thisNodeDef, String path) throws InvalidExpressionException {
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		NodeDefinition currentContext = context;
		while (tokenizer.hasMoreTokens()) {
			String pathSection = tokenizer.nextToken();
			currentContext = getChildDefinition(currentContext, thisNodeDef, pathSection);
		}
		return currentContext;
	}

	public Set<String> getFunctionNames() {
		return compiledExpression.getFunctionNames();
	}
	
	protected Object evaluateSingle(Node<?> contextNode, Node<?> thisNode) throws InvalidExpressionException {
		try {
			JXPathContext jxPathContext = createJXPathContext(contextNode, thisNode);
			Object object = compiledExpression.getValue(jxPathContext);
			return object;
		} catch (IllegalArgumentException e) {
			throw new InvalidExpressionException(e.getMessage(), this.compiledExpression.toString());
		} catch (JXPathInvalidSyntaxException e) {
			throw new InvalidExpressionException(e.getMessage(), this.compiledExpression.toString());
		} catch (JXPathNotFoundException e) {
			return null;
		}
	}

	/**
	 * Returns a list of Node that matches the expression
	 *
	 * @throws InvalidExpressionException
	 */
	protected List<Node<?>> evaluateMultiple(Node<?> contextNode, Node<?> thisNode) throws InvalidExpressionException {
		try {
			List<Node<?>> list = new ArrayList<Node<?>>();
			JXPathContext jxPathContext = createJXPathContext(contextNode, thisNode);

			Iterator<?> pointers = compiledExpression.iteratePointers(jxPathContext);
			while (pointers.hasNext()) {
				Object pointer = pointers.next();
				if (pointer instanceof ModelNodePointer) {
					ModelNodePointer modelNodePointer = (ModelNodePointer) pointer;
					Object ptrNode = modelNodePointer.getNode();
					if (ptrNode != null && ptrNode instanceof Node) {
						Node<?> n = (Node<?>) ptrNode;
						list.add(n);
					}
				} else if (pointer instanceof VariablePointer && ((VariablePointer) pointer).getName().equals(THIS)) {
					list.add(thisNode);
				}
				// ignore node pointer if it's a NullPointer
			}
			return list;
		} catch (IllegalArgumentException e) {
			throw new InvalidExpressionException(e.getMessage(), this.compiledExpression.toString());
		} catch (JXPathInvalidSyntaxException e) {
			throw new InvalidExpressionException(e.getMessage());
		}
	}

	private NodeDefinition getChildDefinition(NodeDefinition contextNode, NodeDefinition thisNodeDef, String pathSection) throws InvalidExpressionException {
		if (Path.NORMALIZED_PARENT_FUNCTION.equals(pathSection)) {
			return contextNode.getParentDefinition();
		} else if (Path.THIS_ALIASES.contains(pathSection)) {
			return thisNodeDef;
		} else {
			String childName = pathSection.replaceAll("\\[.+]", "");
			if (contextNode instanceof EntityDefinition) {
				try {
					NodeDefinition childDefinition = ((EntityDefinition) contextNode).getChildDefinition(childName);
					return childDefinition;
				} catch (Exception e) {
					String message = String.format("Node '%s' not found", childName);
					Set<String> childNames = ((EntityDefinition) contextNode).getChildDefinitionNames();
					String childNamesFormatted = "\t" + joinSplittingInGroups(childNames, 5, ',', "\n\t");
					String detailedMessage = String.format("Node '%s' not found\n - current parent entity: '%s'\n - possible valid values in %s:\n %s", 
							childName, contextNode.getPath(), contextNode.getPath(), childNamesFormatted);
					throw new InvalidExpressionException(message, compiledExpression.toString(), detailedMessage);
				}
			} else {
				String message = String.format("Context node %s is not a Entity and does not allow child definitions", contextNode.getPath());
				throw new InvalidExpressionException(message, compiledExpression.toString());
			}
		}
	}

	private String joinSplittingInGroups(Set<String> items, int groupSize, char itemSeparator, String groupSeparator) {
		StringBuilder childNamesFormattedSB = new StringBuilder();
		int count = 0;
		for (String name : items) {
			childNamesFormattedSB.append(name);
			count ++;
			if (count < items.size()) {
				childNamesFormattedSB.append(itemSeparator);
				if (count % groupSize == 0) {
					childNamesFormattedSB.append(groupSeparator);
				}
			}
		}
		return childNamesFormattedSB.toString();
	}

	/**
	 * Creates a new JXPath context in order to evaluate the expression
	 */
	private JXPathContext createJXPathContext(Node<?> contextNode, Node<?> thisNode) {
		ModelJXPathContext jxPathContext = ModelJXPathContext.newContext(this.jxPathContext, contextNode);
		if (thisNode != null) {
			Variables variables = jxPathContext.getVariables();
			variables.declareVariable(THIS_VARIABLE_NAME, thisNode);
		}
		return jxPathContext;
	}

}
