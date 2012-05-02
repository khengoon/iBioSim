package analysis.dynamicsim;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.ASTNode;

import flanagan.integration.RungeKutta;
import flanagan.integration.DerivnFunction;

import main.Gui;
import main.util.MutableBoolean;

public class SimulatorODERK extends Simulator {
	
	private static Long initializationTime = new Long(0);
	
	MutableBoolean eventsFlag = new MutableBoolean(false);
	MutableBoolean rulesFlag = new MutableBoolean(false);
	MutableBoolean constraintsFlag = new MutableBoolean(false);
	
	String[] variables;
	double[] values;
	ASTNode[] dvariablesdtime;
	HashMap<String, Integer> variableToIndexMap;
	HashMap<Integer, String> indexToVariableMap;

	public SimulatorODERK(String SBMLFileName, String outputDirectory, double timeLimit, double maxTimeStep, long randomSeed,
			JProgressBar progress, double printInterval, double stoichAmpValue, JFrame running,
			String[] interestingSpecies) throws IOException, XMLStreamException {
		
		super(SBMLFileName, outputDirectory, timeLimit, maxTimeStep, randomSeed,
				progress, printInterval, initializationTime, stoichAmpValue, running,
				interestingSpecies);
		
		try {
			initialize(randomSeed, 1);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	private void initialize(long randomSeed, int runNumber) throws IOException {
	
		setupArrays();
		setupSpecies();
		setupInitialAssignments();
		setupParameters();
		setupRules();
		setupConstraints();
		
		eventsFlag = new MutableBoolean(false);
		rulesFlag = new MutableBoolean(false);
		constraintsFlag = new MutableBoolean(false);
		
		if (numEvents == 0)
			eventsFlag.setValue(true);
		else
			eventsFlag.setValue(false);
		
		if (numAssignmentRules == 0)
			rulesFlag.setValue(true);
		else
			rulesFlag.setValue(false);
		
		if (numConstraints == 0)
			constraintsFlag.setValue(true);
		else
			constraintsFlag.setValue(false);
		
		
		//STEP 0: calculate initial propensities (including the total)		
		setupReactions();		
//		setupEvents();
//		
//		if (dynamicBoolean == true)
//			setupGrid();
		
		setupForOutput(randomSeed, runNumber);
		
		variables = new String[variableToValueMap.size()];
		values = new double[variableToValueMap.size()];
		dvariablesdtime = new ASTNode[variableToValueMap.size()];
		variableToIndexMap = new HashMap<String, Integer>(variableToValueMap.size());
		indexToVariableMap = new HashMap<Integer, String>(variableToValueMap.size());
		
		int index = 0;
		
		//convert variableToValueMap into two arrays
		//and create a hashmap to find indices
		for (String variable : variableToValueMap.keySet()) {
			
			variables[index] = variable;
			values[index] = variableToValueMap.get(variable);
			variableToIndexMap.put(variable, index);
			dvariablesdtime[index] = new ASTNode();
			dvariablesdtime[index].setValue(0);
			indexToVariableMap.put(index, variable);
			++index;
		}
		
		//create system of ODEs for the change in variables
		for (String reaction : reactionToFormulaMap.keySet()) {
			
			ASTNode formula = reactionToFormulaMap.get(reaction);
			
			HashSet<StringDoublePair> reactantAndStoichiometrySet = reactionToReactantStoichiometrySetMap.get(reaction);
			HashSet<StringDoublePair> speciesAndStoichiometrySet = reactionToSpeciesAndStoichiometrySetMap.get(reaction);
			
			//loop through reactants
			for (StringDoublePair reactantAndStoichiometry : reactantAndStoichiometrySet) {
				
				String reactant = reactantAndStoichiometry.string;
				double stoichiometry = reactantAndStoichiometry.doub;				
				int varIndex = variableToIndexMap.get(reactant);
				ASTNode stoichNode = new ASTNode();
				stoichNode.setValue(-1 * stoichiometry);
				
				dvariablesdtime[varIndex] = ASTNode.sum(dvariablesdtime[varIndex], formula.clone().multiplyWith(stoichNode));
			}
			
			//loop through products
			for (StringDoublePair speciesAndStoichiometry : speciesAndStoichiometrySet) {
				
				String species = speciesAndStoichiometry.string;
				double stoichiometry = speciesAndStoichiometry.doub;
				
				//if it's a product its stoichiometry will be positive
				//(and if it's a reactant it'll be negative)
				if (stoichiometry > 0) {
					
					int varIndex = variableToIndexMap.get(species);
					ASTNode stoichNode = new ASTNode();
					stoichNode.setValue(stoichiometry);
					
					dvariablesdtime[varIndex] = ASTNode.sum(dvariablesdtime[varIndex], formula.clone().multiplyWith(stoichNode));
				}
			}			
		}	
		
		
		HashSet<String> comps = new HashSet<String>();
		comps.addAll(componentToLocationMap.keySet());
		
		bufferedTSDWriter.write("(" + "\"" + "time" + "\"");
		
		//if there's an interesting species, only those get printed
		if (interestingSpecies.size() > 0) {
			
			for (String speciesID : interestingSpecies)
				bufferedTSDWriter.write(", \"" + speciesID + "\"");
		}
		else {
		
			for (String speciesID : speciesIDSet) {				
				bufferedTSDWriter.write(", \"" + speciesID + "\"");
			}
			
			if (dynamicBoolean == true) {
			
				//print compartment location IDs
				for (String componentLocationID : componentToLocationMap.keySet()) {
					
					String locationX = componentLocationID + "__locationX";
					String locationY = componentLocationID + "__locationY";
					
					bufferedTSDWriter.write(", \"" + locationX + "\", \"" + locationY + "\"");
				}
			}
			
			//print compartment IDs (for sizes)
			for (String componentID : compartmentIDSet) {
				
				bufferedTSDWriter.write(", \"" + componentID + "\"");
			}		
			
			//print nonconstant parameter IDs
			for (String parameterID : nonconstantParameterIDSet) {
				
				try {
					bufferedTSDWriter.write(", \"" + parameterID + "\"");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		}
		
		bufferedTSDWriter.write("),\n");
	}

	@Override
	protected void cancel() {
		
		cancelFlag = true;
	}

	@Override
	protected void clear() {
		
	}

	@Override
	protected void eraseComponentFurther(HashSet<String> reactionIDs) {
		
	}

	@Override
	protected void setupForNewRun(int newRun) {
		
	}

	protected void simulate() {
		
		if (sbmlHasErrorsFlag == true)
			return;
		
		final boolean noEventsFlag = (Boolean) eventsFlag.getValue();
		final boolean noAssignmentRulesFlag = (Boolean) rulesFlag.getValue();
		final boolean noConstraintsFlag = (Boolean) constraintsFlag.getValue();
		
		double printTime = -0.0001;
		double stepSize = 0.1;
		currentTime = 0.0;
		
		//create runge-kutta instance
		DerivnFunc derivnFunction = new DerivnFunc();
		RungeKutta rungeKutta = new RungeKutta();
		
		rungeKutta.setToleranceAdditionFactor(1e-9);
		rungeKutta.setToleranceScalingFactor(1e-9);
		
		//add events to queue if they trigger
		if (noEventsFlag == false)
			handleEvents(noAssignmentRulesFlag, noConstraintsFlag);
		
		while (printTime <= timeLimit && cancelFlag == false) {
			
			//if a constraint fails
			if (constraintFailureFlag == true) {
				
				JOptionPane.showMessageDialog(Gui.frame, "Simulation Canceled Due To Constraint Failure",
						"Constraint Failure", JOptionPane.ERROR_MESSAGE);
				return;
			}
	
			//EVENT HANDLING
			//trigger and/or fire events, etc.
			if (noEventsFlag == false) {
				
				fireEvents(noAssignmentRulesFlag, noConstraintsFlag);
			}
			
			//prints the initial (time == 0) data				
			if (printTime < 0) {
				
				printTime = 0.0;
				
				try {
					printToTSD(printTime);
					bufferedTSDWriter.write(",\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				printTime += printInterval;
			}
			
			//set rk values
			rungeKutta.setInitialValueOfX(currentTime);
			rungeKutta.setFinalValueOfX(currentTime + stepSize);
			rungeKutta.setInitialValuesOfY(values);
			rungeKutta.setStepSize(stepSize);
			
			currentTime += stepSize;
			
			//call the rk algorithm
			values = rungeKutta.fehlberg(derivnFunction);
			
			
			//TSD PRINTING
			//this prints the previous timestep's data				
			while ((currentTime >= printTime) && (printTime <= timeLimit)) {
				
				try {
					printToTSD(printTime);
					bufferedTSDWriter.write(",\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				printTime += printInterval;
				running.setTitle("Progress (" + (int)((currentTime / timeLimit) * 100.0) + "%)");
			}
			
			//update progress bar			
			progress.setValue((int)((printTime / timeLimit) * 100.0));
			
			//add events to queue if they trigger
			if (noEventsFlag == false) {
				
				handleEvents(noAssignmentRulesFlag, noConstraintsFlag);
			
				//step to the next event fire time if it comes before the next time step
				if (!triggeredEventQueue.isEmpty() && triggeredEventQueue.peek().fireTime <= currentTime)
					currentTime = triggeredEventQueue.peek().fireTime;
			}			
		} //end simulation loop
	}

	@Override
	protected void updateAfterDynamicChanges() {
		
	}
	
	private class DerivnFunc implements DerivnFunction {

		/**
		 * in this context, x is the time and y is the values array
		 * this method is called by the rk algorithm and returns the
		 * evaluated derivatives of the ODE system
		 * it needs to return the changes in values for y
		 * (ie, its length is the same)
		 */
		public double[] derivn(double x, double[] y) {
			
			double[] currValueChanges = new double[y.length];
			
			for (int i = 0; i < y.length; ++i)
				variableToValueMap.put(indexToVariableMap.get(i), y[i]);
			
			//calculate the current variable values
			//based on the ODE system			
			for (int i = 0; i < currValueChanges.length; ++i) {
				
				currValueChanges[i] = evaluateExpressionRecursive(dvariablesdtime[i]);
			}
			
			return currValueChanges;
		}
	}	
}
