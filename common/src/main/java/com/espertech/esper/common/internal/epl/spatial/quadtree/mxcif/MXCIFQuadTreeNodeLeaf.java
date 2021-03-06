/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.common.internal.epl.spatial.quadtree.mxcif;

import com.espertech.esper.common.internal.epl.spatial.quadtree.core.BoundingBox;

public class MXCIFQuadTreeNodeLeaf<L> extends MXCIFQuadTreeNode<L> {
    public MXCIFQuadTreeNodeLeaf(BoundingBox bb, int level, L data, int dataCount) {
        super(bb, level, data, dataCount);
    }
}
