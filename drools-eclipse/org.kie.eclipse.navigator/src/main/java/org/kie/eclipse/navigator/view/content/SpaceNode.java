/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 ******************************************************************************/

package org.kie.eclipse.navigator.view.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kie.eclipse.server.IKieSpaceHandler;
import org.kie.eclipse.server.IKieRepositoryHandler;
import org.kie.eclipse.server.IKieResourceHandler;

/**
 *
 */
public class SpaceNode extends ContainerNode<ServerNode> {
	
	/**
	 * @param parent
	 * @param name
	 */
	protected SpaceNode(ServerNode parent, IKieSpaceHandler space) {
		super(parent, space);
	}
	
	@Override
	protected List<? extends IContentNode<?>> createChildren() {
		clearHandlerChildren();
		load();
		List<RepositoryNode> children = new ArrayList<RepositoryNode>();
		if (handlerChildren!=null) {
			Iterator<? extends IKieResourceHandler> iter = handlerChildren.iterator();
			while (iter.hasNext()) {
				IKieResourceHandler h = iter.next();
				if (h instanceof IKieRepositoryHandler)
					children.add(new RepositoryNode(this,(IKieRepositoryHandler)h));
			}
		}
		return children;
	}
	
	@Override
	public boolean isResolved() {
		return true;
	}
}
