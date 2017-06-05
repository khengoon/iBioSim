package edu.utah.ece.async.ibiosim.gui.util.preferences;

import java.awt.Component;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import edu.utah.ece.async.ibiosim.dataModels.util.IBioSimPreferences;
import edu.utah.ece.async.ibiosim.gui.ResourceManager;
import edu.utah.ece.async.ibiosim.gui.util.preferences.PreferencesDialog.PreferencesTab;
import edu.utah.ece.async.sboldesigner.sbol.editor.Images;
import edu.utah.ece.async.sboldesigner.swing.FormBuilder;

public enum GeneralPreferencesTab implements PreferencesTab {
	INSTANCE;

	private JCheckBox dialog = new JCheckBox("Use File Dialog",IBioSimPreferences.INSTANCE.isFileDialogEnabled());
	private JCheckBox icons = new JCheckBox("Use Plus/Minus For Expanding/Collapsing File Tree",
			IBioSimPreferences.INSTANCE.isPlusMinusIconsEnabled());
	private JCheckBox delete = new JCheckBox("Do Not Confirm File Deletions",
			IBioSimPreferences.INSTANCE.isNoConfirmEnabled());
	private JCheckBox libsbmlFlatten = new JCheckBox("Use libsbml to Flatten Models",
			IBioSimPreferences.INSTANCE.isLibSBMLFlattenEnabled());
	private JCheckBox libsbmlValidate = new JCheckBox("Use libsbml to Validate Models",
			IBioSimPreferences.INSTANCE.isLibSBMLFlattenEnabled());
	private JCheckBox showWarnings = new JCheckBox("Report Validation Warnings",
			IBioSimPreferences.INSTANCE.isWarningsEnabled());
	
	private JLabel xhtmlCmdLabel = new JLabel("Browser Viewer Command");
	private JTextField xhtmlCmd = new JTextField(IBioSimPreferences.INSTANCE.getXhtmlCmd());
	private JLabel prismCmdLabel = new JLabel("PRISM Model Checking Command");
	private JTextField prismCmd = new JTextField(IBioSimPreferences.INSTANCE.getPrismCmd());
	private JLabel dotCmdLabel = new JLabel("Graphviz Viewer Command");
	private JTextField dotCmd = new JTextField(IBioSimPreferences.INSTANCE.getGraphvizCmd());

	@Override
	public String getTitle() {
		return "General";
	}

	@Override
	public String getDescription() {
		return "General Preferences";
	}

	@Override
	public Icon getIcon() {
		return ResourceManager.getImageIcon("general.png");
	}

	@Override
	public Component getComponent() {

		FormBuilder builder = new FormBuilder();
		builder.add("", dialog);
		builder.add("", icons);
		builder.add("", delete);
		builder.add("", libsbmlFlatten);
		builder.add("", libsbmlValidate);
		builder.add("", showWarnings);
		builder.add("", xhtmlCmdLabel);
		builder.add("", xhtmlCmd);
		builder.add("", dotCmdLabel);
		builder.add("", dotCmd);
		builder.add("", prismCmdLabel);
		builder.add("", prismCmd);

		return builder.build();
	}

	@Override
	public void save() {
		IBioSimPreferences.INSTANCE.setFileDialogEnabled(dialog.isSelected());
		IBioSimPreferences.INSTANCE.setPlusMinusIconsEnabled(icons.isSelected()); 
		IBioSimPreferences.INSTANCE.setNoConfirmEnabled(delete.isSelected());
		IBioSimPreferences.INSTANCE.setLibSBMLFlattenEnabled(libsbmlFlatten.isSelected());
		IBioSimPreferences.INSTANCE.setLibSBMLValidateEnabled(libsbmlValidate.isSelected());
		IBioSimPreferences.INSTANCE.setWarningsEnabled(showWarnings.isSelected());
		IBioSimPreferences.INSTANCE.setXhtmlCmd(xhtmlCmd.getText());
		IBioSimPreferences.INSTANCE.setGraphvizCmd(dotCmd.getText());
		IBioSimPreferences.INSTANCE.setPrismCmd(prismCmd.getText());
	}

	@Override
	public boolean requiresRestart() {
		return false;
	}
}