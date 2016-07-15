package org.cmg.tapas.ccsp.runtime.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cmg.tapas.ccsp.runtime.CCSPAction;
import org.cmg.tapas.ccsp.runtime.CCSPChannel;
import org.cmg.tapas.ccsp.runtime.CCSPParallel;
import org.cmg.tapas.ccsp.runtime.CCSPProcess;
import org.cmg.tapas.ccsp.runtime.CCSPRestriction;
import org.cmg.tapas.ccsp.runtime.CCSPChannelSet;
import org.cmg.tapas.ccsp.runtime.CCSPState;
import org.cmg.tapas.ccsp.runtime.CCSPUtil;
import org.cmg.tapas.core.graph.filter.TrueFilter;
import org.cmg.tapas.formulae.hml.HmlAndFormula;
import org.cmg.tapas.formulae.hml.HmlDiamondFormula;
import org.cmg.tapas.formulae.hml.HmlFixPoint;
import org.cmg.tapas.formulae.hml.HmlTrue;
import org.cmg.tapas.pa.LTSGraph;
import org.junit.Test;

public class TestProcess {

	public static CCSPChannel p = new CCSPChannel("ping", 0);
		
	public static CCSPState ping = new CCSPState("PingPong","ping",0) {
		
		@Override
		protected void initializeTransistionTable() {
			addNext(new CCSPAction.OutputAction(p), pong);
		}
	};
	
	public static CCSPState pong = new CCSPState("PingPong","pong",1) {

		@Override
		protected void initializeTransistionTable() {
			addNext(new CCSPAction.InputAction(p), ping);
		}

	};

	@Test
	public void testEqualsState( ) {
		assertTrue(ping.equals(ping));
		assertTrue(pong.equals(pong));
		assertFalse(pong.equals(ping));
		assertFalse(ping.equals(pong));
	}
	
	@Test
	public void testLTSGenerationFromState() {
		LTSGraph<CCSPProcess, CCSPAction> lts = CCSPUtil.getGraph(ping);
		assertEquals(2, lts.getNumberOfStates());
		assertEquals(2, lts.getNumberOfEdges());
	}
	
	@Test
	public void testEqualsParallel() {
		CCSPProcess p1 = new CCSPParallel(ping,pong);
		CCSPProcess p2 = new CCSPParallel(ping,pong);
		assertEquals(p1,p2);
		assertEquals(p1.hashCode(),p2.hashCode());
		assertEquals(p1.toString(),p2.toString());
		System.out.println(p1.toString());
	}

	@Test
	public void testLTSGenerationFromParallel() {
		CCSPProcess p1 = new CCSPParallel(ping,pong);
		LTSGraph<CCSPProcess, CCSPAction> lts = CCSPUtil.getGraph(p1);
		assertEquals(4, lts.getNumberOfStates());
		assertEquals(10, lts.getNumberOfEdges());
	}

	@Test
	public void testLTSGenerationFromRestriction() {
		CCSPProcess p1 = new CCSPRestriction( new CCSPParallel(ping,pong) , new CCSPChannelSet(p));
		LTSGraph<CCSPProcess, CCSPAction> lts = CCSPUtil.getGraph(p1);
		assertEquals(2, lts.getNumberOfStates());
		assertEquals(2, lts.getNumberOfEdges());
	}
	
	public void testHmlFormula() {
		HmlFixPoint<CCSPProcess, CCSPAction> f = new HmlFixPoint<CCSPProcess, CCSPAction>(true, "X");
		f.setSubformula( 
			new HmlAndFormula<CCSPProcess, CCSPAction>(
				new HmlDiamondFormula<CCSPProcess,CCSPAction>( 
						new TrueFilter<CCSPAction>() , 
						new HmlTrue<CCSPProcess,CCSPAction>()
				) ,	
				new HmlDiamondFormula<CCSPProcess,CCSPAction>( 
						new TrueFilter<CCSPAction>() , 
						f.getReference()
				) 
			)				
		);
	}

}
