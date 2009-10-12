package fr.imag.adele.fede.workspace.si.initmodel;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import fede.workspace.role.initmodel.ErrorWhenLoadedModel;
import fede.workspace.tool.loadmodel.model.jaxb.CAttType;
import fede.workspace.tool.loadmodel.model.jaxb.CCadse;
import fede.workspace.tool.loadmodel.model.jaxb.CItemType;
import fede.workspace.tool.loadmodel.model.jaxb.CValuesType;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.workspace.as.classreferencer.IClassReferencer;
import fr.imag.adele.cadse.workspace.as.loadfactory.ILoadFactory;
import fr.imag.adele.fede.workspace.as.initmodel.IInitModel;
import fr.imag.adele.fede.workspace.as.platformeclipse.IPlatformEclipse;
import fr.imag.adele.melusine.as.findmodel.IFindModel;

public class InitModel implements IInitModel {
	InitModelImpl impl = new InitModelImpl(this);
	/**
	 * @generated
	 */
	CadseDomain					workspaceCU;

	/**
	 * @generated
	 */
	IFindModel					findModel;

	/**
	 * @generated
	 */
	ILoadFactory[]				loadFactories;

	/**
	 * @generated
	 */
	IClassReferencer			classReferencer;

	/**
	 * @generated
	 */
	IPlatformEclipse			platformService;

	
	
	public CadseDomain getWorkspaceCU() {
		return workspaceCU;
	}

	public IFindModel getFindModel() {
		return findModel;
	}

	public ILoadFactory[] getLoadFactories() {
		return loadFactories;
	}

	public IClassReferencer getClassReferencer() {
		return classReferencer;
	}

	public IPlatformEclipse getPlatformService() {
		return platformService;
	}

	@Override
	public CAttType convertCadsegToCAttType(Item attributeType) {
		// TODO Auto-generated method stub
		return impl.convertCadsegToCAttType(attributeType);
	}

	@Override
	public CItemType convertCadsegToCItemType(Item itemType) {
		return impl.convertCadsegToCItemType(itemType);
	}

	@Override
	public IAttributeType<?> convertToAttributeType(CValuesType attType) {
		return impl.convertToAttributeType(attType);
	}

	@Override
	public IAttributeType<?> convertToAttributeType(CAttType attType,
			Item parent, String cadseName) {
		return impl.convertToAttributeType(attType, parent, cadseName);
	}

	@Override
	public CAttType convertToCAttType(IAttributeType<?> attributeType) {
		return impl.convertToCAttType(attributeType);
	}

	@Override
	public CItemType convertToCItemType(ItemType itemType) {
		return impl.convertToCItemType(itemType);
	}

	@Override
	public Object convertToCValue(CValuesType value,
			IAttributeType<?> attDefinition) {
		return impl.convertToCValue(value, attDefinition);
	}

	@Override
	public ItemType convertToItemType(CItemType itemType) {
		return impl.convertToItemType(itemType);
	}

	@Override
	public IAttributeType<?> createAttrituteType(
			LogicalWorkspace theWorkspaceLogique, ItemType itemTypeParent,
			CValuesType valuesType, String cadseName) throws CadseException {
		return impl.createAttrituteType(theWorkspaceLogique, itemTypeParent, valuesType, cadseName);
	}

	@Override
	public int executeCadses(CadseRuntime... cadseName)
			throws ErrorWhenLoadedModel {
		return impl.executeCadses(cadseName);
	}

	@Override
	public int getFlag(CValuesType type) {
		return impl.getFlag(type);
	}

	
	@Override
	public int getMax(CValuesType type) {
		return impl.getMax(type);
	}

	@Override
	public int getMin(CValuesType type) {
		return impl.getMin(type);
	}

	@Override
	public CompactUUID getUUID(String id) {
		return impl.getUUID(id);
	}

	@Override
	public String[] listCadseName() {
		return impl.listCadseName();
	}

	@Override
	public CCadse load(File file) throws FileNotFoundException, JAXBException {
		return impl.load(file);
	}

	@Override
	public CadseRuntime[] loadCadses() {
		return impl.loadCadses();
	}

	@Override
	public <T> Class<T> loadClass(String cadseName, String qualifiedClassName) {
		return impl.loadClass(cadseName, qualifiedClassName);
	}

	@Override
	public void save(CCadse test, File file) throws JAXBException,
			FileNotFoundException {
		impl.save(test, file);
	}
	
	public void start() {
		impl.start();
	}
}
