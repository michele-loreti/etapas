/**
 * 
 */
package org.cmg.tapas.rm.runtime.tests;

import static org.junit.Assert.fail;

import org.cmg.tapas.rm.runtime.ModuleAction;
import org.cmg.tapas.rm.runtime.ModuleFactory;
import org.junit.Test;

/**
 * @author loreti
 *
 */
public class TestCooperationModule {

	
	private ModuleAction ACTION_A = new ModuleAction("a");
	
	private ModuleAction ACTION_B = new ModuleAction("b");
	
	private ModuleFactory factory = new ModuleFactory(null,ACTION_A , ACTION_B);
	
	private ModuleAction TAU = factory.getTauAction();

	


	
	
	/**
	 * Test method for {@link org.cmg.tapas.rm.runtime.old.CooperationModule#getNext(org.cmg.tapas.rm.runtime.State)}.
	 */
	@Test
	public void testGetNext() {

	}

	/**
	 * Test method for {@link org.cmg.tapas.rm.runtime.old.CooperationModule#synchronize(org.cmg.tapas.rm.runtime.State, org.cmg.tapas.rm.runtime.ModuleAction, java.util.Set)}.
	 */
	@Test
	public void testSynchronize() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.cmg.tapas.rm.runtime.old.CooperationModule#interleaving(org.cmg.tapas.rm.runtime.State, org.cmg.tapas.rm.runtime.ModuleAction, java.util.Set)}.
	 */
	@Test
	public void testInterleaving() {
		fail("Not yet implemented");
	}

}
