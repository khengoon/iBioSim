package analysis.dynamicsim.hierarchical.states;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.sbml.jsbml.Model;

public abstract class SpeciesState extends VariableState
{

	private final HashMap<String, HashSet<String>>	speciesToAffectedReactionSetMap;
	private final HashMap<String, String>			speciesToCompartmentNameMap;
	private final HashMap<String, Boolean>			speciesToHasOnlySubstanceUnitsMap;
	private final HashMap<String, Boolean>			speciesToIsBoundaryConditionMap;

	public SpeciesState(Map<String, Model> models, String bioModel, String submodelID)
	{
		super(models, bioModel, submodelID);
		speciesToAffectedReactionSetMap = new HashMap<String, HashSet<String>>(
				(int) getNumSpecies());
		speciesToIsBoundaryConditionMap = new HashMap<String, Boolean>((int) getNumSpecies());
		speciesToHasOnlySubstanceUnitsMap = new HashMap<String, Boolean>((int) getNumSpecies());
		speciesToCompartmentNameMap = new HashMap<String, String>((int) getNumSpecies());
	}

	public SpeciesState(SpeciesState state)
	{
		super(state);
		speciesToAffectedReactionSetMap = state.speciesToAffectedReactionSetMap;
		speciesToIsBoundaryConditionMap = state.speciesToIsBoundaryConditionMap;
		speciesToHasOnlySubstanceUnitsMap = state.speciesToHasOnlySubstanceUnitsMap;
		speciesToCompartmentNameMap = state.speciesToCompartmentNameMap;
	}

	public HashMap<String, HashSet<String>> getSpeciesToAffectedReactionSetMap()
	{
		return speciesToAffectedReactionSetMap;
	}

	public HashMap<String, String> getSpeciesToCompartmentNameMap()
	{
		return speciesToCompartmentNameMap;
	}

	public HashMap<String, Boolean> getSpeciesToHasOnlySubstanceUnitsMap()
	{
		return speciesToHasOnlySubstanceUnitsMap;
	}

	public HashMap<String, Boolean> getSpeciesToIsBoundaryConditionMap()
	{
		return speciesToIsBoundaryConditionMap;
	}
}
