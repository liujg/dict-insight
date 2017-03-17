/**
 * @(#)Listen.java, 2015年10月9日. 
 * 
 * Copyright 2015 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.zkcurator;

import java.util.List;

import org.apache.curator.framework.recipes.cache.ChildData;

/**
 *
 * @author liujg
 *
 */
public interface IListener {

    public void refreshNodes(List<String> datas);

}
