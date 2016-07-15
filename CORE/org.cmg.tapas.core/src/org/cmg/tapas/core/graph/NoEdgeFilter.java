package org.cmg.tapas.core.graph;

public class NoEdgeFilter<
	S extends StateInterface,
	A extends ActionInterface	
> extends EdgeFilter<S,A>
{

	@Override
	public boolean check(StateInterface src, ActionInterface action,
			StateInterface dest) {
		// TODO Auto-generated method stub
		return true;
	}

}
