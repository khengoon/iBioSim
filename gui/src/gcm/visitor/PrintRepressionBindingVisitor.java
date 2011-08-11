package gcm.visitor;



import java.util.ArrayList;
import java.util.HashMap;

import gcm.network.BaseSpecies;
import gcm.network.ComplexSpecies;
import gcm.network.ConstantSpecies;
import gcm.network.GeneticNetwork;
import gcm.network.Promoter;
import gcm.network.Influence;
import gcm.network.SpasticSpecies;
import gcm.network.SpeciesInterface;
import gcm.util.GlobalConstants;
import gcm.util.Utility;

import org.sbml.libsbml.SBMLDocument;

public class PrintRepressionBindingVisitor extends AbstractPrintVisitor {

	public PrintRepressionBindingVisitor(SBMLDocument document, Promoter p, HashMap<String, SpeciesInterface> species, 
			String compartment, 
			HashMap<String, ArrayList<Influence>> complexMap, HashMap<String, ArrayList<Influence>> partsMap) {
		super(document);
		this.promoter = p;
		this.species = species;
		this.complexMap = complexMap;
		this.partsMap = partsMap;
		this.compartment = compartment;
	}

	/**
	 * Prints out all the species to the file
	 * 
	 */
	public void run() {
		for (SpeciesInterface specie : promoter.getRepressors()) {
			String repressor = specie.getId();
			String[] splitted = repressor.split("__");
			if (splitted.length > 1)
				repressor = splitted[1];
			boundId = promoter.getId() + "_" + repressor + "_bound";
			reactionId = "R_repression_binding_" + promoter.getId() + "_" + repressor;
			specie.accept(this);
		}
	}

	@Override
	public void visitSpecies(SpeciesInterface specie) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitComplex(ComplexSpecies specie) {
		loadValues(specie);
		r = Utility.Reaction(reactionId);
		r.setCompartment(compartment);
		r.addReactant(Utility.SpeciesReference(promoter.getId(), 1));
		r.addProduct(Utility.SpeciesReference(boundId, 1));
		r.setReversible(true);
		r.setFast(false);
		kl = r.createKineticLaw();
		kl.addParameter(Utility.Parameter("kf_r", kf,
				GeneticNetwork.getMoleTimeParameter(2)));
		if (coop > 1)
			kl.addParameter(Utility.Parameter(krepString, krep, GeneticNetwork.getMoleParameter(2)));
		kl.addParameter(Utility.Parameter(coopString, coop, "dimensionless"));
		String repExpression = "";
		if (complexAbstraction && specie.isAbstractable()) {
			repExpression = abstractComplex(specie.getId(), coop);
		} else if (complexAbstraction && specie.isSequesterable()) {
			repExpression = sequesterSpecies(specie.getId(), coop);
		} else {
			repExpression = specie.getId();
			r.addReactant(Utility.SpeciesReference(specie.getId(), coop));
		}
		kl.addParameter(Utility.Parameter("kr_r", kr, GeneticNetwork
				.getMoleTimeParameter(1)));
		kl.setFormula(generateLaw(repExpression));
		Utility.addReaction(document, r);
	}
	
	@Override
	public void visitBaseSpecies(BaseSpecies specie) {
		loadValues(specie);
		r = Utility.Reaction(reactionId);
		r.setCompartment(compartment);
		r.addReactant(Utility.SpeciesReference(promoter.getId(), 1));
		r.addProduct(Utility.SpeciesReference(boundId, 1));
		r.setReversible(true);
		r.setFast(false);
		kl = r.createKineticLaw();
		kl.addParameter(Utility.Parameter("kf_r", kf,
				GeneticNetwork.getMoleTimeParameter(2)));
		if (coop > 1)
			kl.addParameter(Utility.Parameter(krepString, krep,	GeneticNetwork.getMoleParameter(2)));
		kl.addParameter(Utility.Parameter(coopString, coop, "dimensionless"));
		String repExpression = "";
		//Checks for valid complex sequestering of repressing species if complex abstraction is selected
		if (complexAbstraction && specie.isSequesterable()) {
			repExpression = repExpression + sequesterSpecies(specie.getId(), coop);
		} else {
			repExpression = specie.getId();
			r.addReactant(Utility.SpeciesReference(specie.getId(), coop));
		}
		kl.addParameter(Utility.Parameter("kr_r", kr, GeneticNetwork
				.getMoleTimeParameter(1)));
		kl.setFormula(generateLaw(repExpression));
		Utility.addReaction(document, r);
	}

	@Override
	public void visitConstantSpecies(ConstantSpecies specie) {
		loadValues(specie);
		r = Utility.Reaction(reactionId);
		r.setCompartment(compartment);
		r.addReactant(Utility.SpeciesReference(promoter.getId(), 1));
		r.addProduct(Utility.SpeciesReference(boundId, 1));
		r.setReversible(true);
		r.setFast(false);
		kl = r.createKineticLaw();
		kl.addParameter(Utility.Parameter("kf_r", kf, GeneticNetwork.getMoleTimeParameter(2)));
		if (coop > 1)
			kl.addParameter(Utility.Parameter(krepString, krep, GeneticNetwork.getMoleParameter(2)));
		kl.addParameter(Utility.Parameter(coopString, coop, "dimensionless"));
		String repExpression = "";
		//Checks for valid complex sequestering of repressing species if complex abstraction is selected
		if (complexAbstraction && specie.isSequesterable()) {
			repExpression = repExpression + sequesterSpecies(specie.getId(), coop);
		} else {
			repExpression = specie.getId();
			r.addReactant(Utility.SpeciesReference(specie.getId(), coop));
		}
		kl.addParameter(Utility.Parameter("kr_r", kr, GeneticNetwork
				.getMoleTimeParameter(1)));
		kl.setFormula(generateLaw(repExpression));
		Utility.addReaction(document, r);
	}

	@Override
	public void visitSpasticSpecies(SpasticSpecies specie) {
		loadValues(specie);
		r = Utility.Reaction(reactionId);
		r.setCompartment(compartment);
		r.addReactant(Utility.SpeciesReference(promoter.getId(), 1));
		r.addProduct(Utility.SpeciesReference(boundId, 1));
		r.setReversible(true);
		r.setFast(false);
		kl = r.createKineticLaw();
		kl.addParameter(Utility.Parameter("kf_r", kf, GeneticNetwork.getMoleTimeParameter(2)));
		if (coop > 1)
			kl.addParameter(Utility.Parameter(krepString, krep, GeneticNetwork.getMoleParameter(2)));
		kl.addParameter(Utility.Parameter(coopString, coop, "dimensionless"));
		String repExpression = "";
		//Checks for valid complex sequestering of repressing species if complex abstraction is selected
		if (complexAbstraction && specie.isSequesterable()) {
			repExpression = repExpression + sequesterSpecies(specie.getId(), coop);
		} else {
			repExpression = specie.getId();
			r.addReactant(Utility.SpeciesReference(specie.getId(), coop));
		}
		kl.addParameter(Utility.Parameter("kr_r", kr, GeneticNetwork
				.getMoleTimeParameter(1)));
		kl.setFormula(generateLaw(repExpression));
		Utility.addReaction(document, r);
	}

	/**
	 * Generates a kinetic law
	 * 
	 * @param specieName
	 *            specie name
	 * @param repExpression
	 *            repressor molecule
	 * @return
	 */
	private String generateLaw(String repExpression) {
		String law = "";
		if (coop == 1)
			law = "kf_r*" + "(" + repExpression + ")" + "^" + coopString + "*" + promoter.getId() + "-kr_r*" + boundId;
		else if (coop > 1)
			law = "kf_r*" + "(" + krepString + ")" + "^" + "(" + coopString + "-1" + ")" + "*" + "(" + repExpression + ")" 
			+ "^" + coopString + "*" + promoter.getId() + "-kr_r*" + boundId;
		return law;
	}

	// Checks if binding parameters are specified as forward and reverse rate constants or
	// as equilibrium binding constants before loading values
	private void loadValues(SpeciesInterface s) {
		Influence r = promoter.getRepressionMap().get(s.getId());
		coop = r.getCoop();
		double[] krepArray = r.getRep();
		kf = krepArray[0];
		if (krepArray.length == 2) {
			krep = krepArray[0]/krepArray[1];
			kr = krepArray[1];
		} else {
			krep = krepArray[0];
			kr = 1;
		}
	}
		

	private Promoter promoter;
	
	private double coop;
	private double kf;
	private double krep;
	private double kr;

	private String krepString = GlobalConstants.KREP_STRING;
	private String coopString = GlobalConstants.COOPERATIVITY_STRING;

	private String boundId;
	private String reactionId;
	private String compartment;
}

