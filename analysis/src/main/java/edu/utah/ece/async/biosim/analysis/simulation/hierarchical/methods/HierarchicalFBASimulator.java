/*******************************************************************************
 *  
 * This file is part of iBioSim. Please visit <http://www.async.ece.utah.edu/ibiosim>
 * for the latest version of iBioSim.
 *
 * Copyright (C) 2017 University of Utah
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online at <http://www.async.ece.utah.edu/ibiosim/License>.
 *  
 *******************************************************************************/
package edu.utah.ece.async.biosim.analysis.simulation.hierarchical.methods;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;

import edu.utah.ece.async.biosim.analysis.fba.FluxBalanceAnalysis;
import edu.utah.ece.async.biosim.analysis.simulation.hierarchical.HierarchicalSimulation;
import edu.utah.ece.async.biosim.analysis.simulation.hierarchical.math.VariableNode;
import edu.utah.ece.async.biosim.analysis.simulation.hierarchical.model.HierarchicalModel;

/**
 * 
 *
 * @author Leandro Watanabe
 * @author Chris Myers
 * @author <a href="http://www.async.ece.utah.edu/ibiosim#Credits"> iBioSim Contributors </a>
 * @version %I%
 */
public class HierarchicalFBASimulator extends HierarchicalSimulation
{

	private FluxBalanceAnalysis		fba;
	private HashMap<String, Double>	values;

	public HierarchicalFBASimulator(HierarchicalSimulation simulation, HierarchicalModel topmodel)
	{
		super(simulation);
		setTopmodel(topmodel);
		values = new HashMap<String, Double>();
	}

	public void setFBA(Model model)
	{
		for (Parameter parameter : model.getListOfParameters())
		{
			values.put(parameter.getId(), parameter.getValue());
		}
		fba = new FluxBalanceAnalysis(model, 1e-9);
	}

	@Override
	public void simulate()
	{
	  //TODO: check return value of fba
		getState();
		fba.setBoundParameters(values);
		fba.PerformFluxBalanceAnalysis();
		retrieveFbaState();
	}

	private void retrieveFbaState()
	{
		Map<String, Double> flux = fba.getFluxes();
		HierarchicalModel topmodel = getTopmodel();
		for (String reaction : flux.keySet())
		{
			topmodel.getNode(reaction).setValue(topmodel.getIndex(), flux.get(reaction));
		}

	}

	@Override
	public void cancel()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setupForNewRun(int newRun)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void printStatisticsTSD()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(long randomSeed, int runNumber) throws IOException, XMLStreamException
	{

	}

	public void getState()
	{

		HierarchicalModel topmodel = getTopmodel();
		for (String name : values.keySet())
		{
		  VariableNode node = topmodel.getNode(name);
      double value = node.getValue(topmodel.getIndex());
      if(Math.abs(value) < 1e-9)
      {
        value = 0;
      }
			values.put(name, value);
		}
	}

}