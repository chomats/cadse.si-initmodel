/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fr.imag.adele.fede.workspace.si.initmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import fede.workspace.role.initmodel.ErrorWhenLoadedModel;
import fede.workspace.tool.loadmodel.internal.model.ModelRepository;
import fede.workspace.tool.loadmodel.model.jaxb.CAbsItemType;
import fede.workspace.tool.loadmodel.model.jaxb.CAction;
import fede.workspace.tool.loadmodel.model.jaxb.CActionContributor;
import fede.workspace.tool.loadmodel.model.jaxb.CAttType;
import fede.workspace.tool.loadmodel.model.jaxb.CCadse;
import fede.workspace.tool.loadmodel.model.jaxb.CCadseRef;
import fede.workspace.tool.loadmodel.model.jaxb.CExtensionItemType;
import fede.workspace.tool.loadmodel.model.jaxb.CItem;
import fede.workspace.tool.loadmodel.model.jaxb.CItemType;
import fede.workspace.tool.loadmodel.model.jaxb.CLinkType;
import fede.workspace.tool.loadmodel.model.jaxb.CMenuAction;
import fede.workspace.tool.loadmodel.model.jaxb.CMetaAttribute;
import fede.workspace.tool.loadmodel.model.jaxb.CPage;
import fede.workspace.tool.loadmodel.model.jaxb.CPages;
import fede.workspace.tool.loadmodel.model.jaxb.CValuesType;
import fede.workspace.tool.loadmodel.model.jaxb.CommitKindType;
import fede.workspace.tool.loadmodel.model.jaxb.EvolutionDestinationKindType;
import fede.workspace.tool.loadmodel.model.jaxb.EvolutionKindType;
import fede.workspace.tool.loadmodel.model.jaxb.UpdateKindType;
import fede.workspace.tool.loadmodel.model.jaxb.ValueTypeType;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.DefaultItemManager;
import fr.imag.adele.cadse.core.IItemFactory;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.ILinkTypeManager;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.enumdef.TWCommitKind;
import fr.imag.adele.cadse.core.enumdef.TWDestEvol;
import fr.imag.adele.cadse.core.enumdef.TWEvol;
import fr.imag.adele.cadse.core.enumdef.TWUpdateKind;
import fr.imag.adele.cadse.core.impl.AbstractLinkTypeManager;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.attribute.BooleanAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.DateAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.DoubleAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.EnumAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.IntegerAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.ListAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.MapAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.StringAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.StructAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.SymbolicBitMapAttributeType;
import fr.imag.adele.cadse.core.impl.attribute.UUIDAttributeType;
import fr.imag.adele.cadse.core.impl.internal.AbstractGeneratedItem;
import fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceImpl;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.ConfigurablePageFactory;
import fr.imag.adele.cadse.core.impl.ui.PageImpl;
import fr.imag.adele.cadse.core.internal.attribute.IInternalTWAttribute;
import fr.imag.adele.cadse.core.internal.attribute.IInternalTWLink;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.ui.EmptyPageFactory;
import fr.imag.adele.cadse.core.ui.GenericActionContributor;
import fr.imag.adele.cadse.core.ui.IActionContributor;
import fr.imag.adele.cadse.core.ui.IPageFactory;
import fr.imag.adele.cadse.core.ui.MenuAction;
import fr.imag.adele.cadse.core.ui.PageFactory;
import fr.imag.adele.cadse.core.util.Assert;
import fr.imag.adele.cadse.core.util.Convert;
import fr.imag.adele.cadse.workspace.as.classreferencer.IClassReferencer;
import fr.imag.adele.cadse.workspace.as.loadfactory.ILoadFactory;
import fr.imag.adele.fede.workspace.as.initmodel.IInitModel;
import fr.imag.adele.fede.workspace.as.initmodel.InitModelLoadAndWrite;
import fr.imag.adele.fede.workspace.as.platformeclipse.IPlatformEclipse;
import fr.imag.adele.melusine.as.findmodel.IFindModel;
import fr.imag.adele.melusine.as.findmodel.ModelEntry;

/**
 * @generated
 */
public class InitModel implements IInitModel {

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

	/*
	 * This object is assigned to the value of a field map to indicate that a
	 * translated message has already been assigned to that field.
	 */

	/** The Constant MOD_EXPECTED. */
	private static final int	MOD_EXPECTED	= Modifier.PUBLIC | Modifier.STATIC;

	/** The Constant MOD_MASK. */
	private static final int	MOD_MASK		= MOD_EXPECTED | Modifier.FINAL;

	/** The m logger. */
	static Logger				_logger			= Logger.getLogger("SI.Workspace.InitModel");

	/**
	 * The Class InitContext.
	 * 
	 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
	 */
	class InitContext {

		/** The item types. */
		Map<CompactUUID, CItemType>	itemTypes;

		/** The cache items. */
		Map<CompactUUID, ItemType>	cacheItems;

		/** The values_to_field. */
		Map<String, Object>			values_to_field;

		/** The init link. */
		ArrayList<ItemType>			initLink;

		/** The loadclass. */
		public boolean				loadclass;

		/** The current cadse name. */
		public CadseRuntime			currentCadseName;

		/** The bundle. */
		public Bundle				bundle;

		public int					executedNumber	= 0;

		/**
		 * Gets the current cadse name.
		 * 
		 * @return the current cadse name
		 */
		public CadseRuntime getCurrentCadseName() {
			return currentCadseName;
		}

		public void reset() {
			cacheItems = new HashMap<CompactUUID, ItemType>();
			values_to_field = new HashMap<String, Object>();
			initLink = new ArrayList<ItemType>();
		}
	}

	/**
	 * The constructor.
	 * 
	 * @exception IllegalArgumentException(Workspace
	 *                Model not found)
	 * @exception IllegalArgumentException(Workspace
	 *                has more one model)
	 */
	public InitModel() {
	}

	public InitModel(IFindModel findModel, CadseDomain workspaceCU) {
		this.findModel = findModel;
		this.workspaceCU = workspaceCU;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.role.initmodel.IInitModel#getLoadedCadses()
	 */
	public String[] getLoadedCadses() {
		CadseDomain wsDomain = this.workspaceCU;
		LogicalWorkspace theWorkspaceLogique = wsDomain.getLogicalWorkspace();
		return theWorkspaceLogique.getCadseName();
	}

	public CadseRuntime[] loadCadses() {
		CadseDomain wsDomain = this.workspaceCU;
		long start = System.currentTimeMillis();
		LogicalWorkspace theWorkspaceLogique = wsDomain.getLogicalWorkspace();

		HashMap<String, CadseRuntime> theCadsesLoadedList = new HashMap<String, CadseRuntime>();
		HashMap<CadseRuntime, List<CCadseRef>> theCadsesLoadedListRef = new HashMap<CadseRuntime, List<CCadseRef>>();

		// Find the loaded cadse.
		CadseRuntime[] cadseName = theWorkspaceLogique.getCadseRuntime();
		if (cadseName != null) {
			for (int i = 0; i < cadseName.length; i++) {
				theCadsesLoadedList.put(cadseName[i].getName(), cadseName[i]);
			}
		}
		ModelEntry[] models = ModelRepository.findModelFile(this);

		if (theCadsesLoadedList.size() == 0) {
			ModelRepository repository = ModelRepository.findModelFile(this, CadseDomain.CADSE_ROOT_MODEL);
			// load the m�ta model (meta item type and meta link type)
			try {
				this.executeCadses(load(true, theWorkspaceLogique, repository, CadseDomain.CADSE_ROOT_MODEL,
						theCadsesLoadedList, theCadsesLoadedListRef));
			} catch (ErrorWhenLoadedModel e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		for (ModelEntry c : models) {
			try {
				load(false, theWorkspaceLogique, new ModelRepository(c), c.getName(), theCadsesLoadedList,
						theCadsesLoadedListRef);
			} catch (ErrorWhenLoadedModel e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (CadseRuntime cr : theCadsesLoadedListRef.keySet()) {
			List<CCadseRef> v = theCadsesLoadedListRef.get(cr);
			if (v.size() == 0) {
				continue;
			}
			CadseRuntime[] extendsCadse = new CadseRuntime[v.size()];
			int i = 0;
			for (CCadseRef ref : v) {
				CadseRuntime findC = theCadsesLoadedList.get(ref.getName());
				if (findC == null) {
					cr.addError("Cannot find the cadse " + ref.getName());
				}
				extendsCadse[i++] = findC;
			}
			cr.setRequiredCadses(extendsCadse);
		}
		_logger.finest("load all cadse in " + (System.currentTimeMillis() - start) + " ms");

		return theCadsesLoadedList.values().toArray(new CadseRuntime[theCadsesLoadedList.values().size()]);
	}

	public void start() {
		_logger.info("start");
	}

	/**
	 * Load.
	 * 
	 * @param theWorkspaceLogique
	 *            the the workspace logique
	 * @param cadseName
	 *            the cadse name
	 * @param theCadsesLoadedList
	 *            the the cadses loaded list
	 * 
	 * @return the int
	 * 
	 * @throws ErrorWhenLoadedModel
	 *             the error when loaded model
	 * 
	 * @see fede.workspace.role.initmodel.IInitModel#init(java.lang.String)
	 */
	private CadseRuntime load(boolean root, LogicalWorkspace theWorkspaceLogique, ModelRepository repository,
			String cadseName, Map<String, CadseRuntime> theCadsesLoadedList, HashMap<CadseRuntime, List<CCadseRef>> ref)
			throws ErrorWhenLoadedModel {

		CadseRuntime findCadse = theCadsesLoadedList.get(cadseName);
		if (findCadse != null) {
			return findCadse;
		}

		if (repository == null) {
			_logger.severe("Can't find the model " + cadseName);
			throw new ErrorWhenLoadedModel("Can't find the model " + cadseName);
		}

		CCadse ccadse = repository.load(this);

		CadseRuntime newCadseRuntime = theWorkspaceLogique.createCadseRuntime(ccadse.getName(),
				getUUID(ccadse.getId()), getUUID(ccadse.getIdCadseDefinition()));
		newCadseRuntime.setIsStatic(true);
		newCadseRuntime.setVersion(ccadse.getVersion());
		newCadseRuntime.setRequiredCadses(null);
		newCadseRuntime.setDescription(ccadse.getDescription());
		newCadseRuntime.setCadseroot(root);
		newCadseRuntime.setExecuted(false);
		newCadseRuntime.setDisplayName(ccadse.getDisplayName());
		theCadsesLoadedList.put(cadseName, newCadseRuntime);
		List<CCadseRef> cadseRef = ccadse.getCadseRef();
		if (!root) {
			addCadseRootInCadseRef(cadseRef);
		}
		ref.put(newCadseRuntime, cadseRef);
		return newCadseRuntime;

	}

	private void addCadseRootInCadseRef(List<CCadseRef> cadseRef) {
		for (CCadseRef cadseRef2 : cadseRef) {
			if (cadseRef2.getName().equals(CadseDomain.CADSE_ROOT_MODEL)) {
				return;
			}
		}
		CCadseRef refCadseRoot = new CCadseRef();
		refCadseRoot.setName(CadseDomain.CADSE_ROOT_MODEL);
		cadseRef.add(refCadseRoot);

	}

	public int executeCadses(CadseRuntime... cadseName) throws ErrorWhenLoadedModel {
		CadseDomain wsDomain = this.workspaceCU;
		long start = System.currentTimeMillis();
		LogicalWorkspace theWorkspaceLogique = wsDomain.getLogicalWorkspace();
		InitContext cxt = new InitContext();
		for (CadseRuntime cadseRuntime : cadseName) {
			try {
				load(cxt, cadseRuntime, theWorkspaceLogique);
			} catch (Throwable e) {
				_logger.log(Level.SEVERE, "Cannot execute cadse " + cadseRuntime.getName(), e);
				cadseRuntime.addError(e.getMessage());
			}
		}
		_logger.finest("load all cadse in " + (System.currentTimeMillis() - start) + " ms");

		return cxt.executedNumber;
	}

	/**
	 * Load.
	 * 
	 * @param theWorkspaceLogique
	 *            the the workspace logique
	 * @param repository
	 *            the repository
	 * @throws ErrorWhenLoadedModel
	 * @throws CadseException
	 */
	private CadseRuntime load(InitContext cxt, CadseRuntime cadse, LogicalWorkspace theWorkspaceLogique)
			throws ErrorWhenLoadedModel, CadseException {

		if (cadse.isExecuted()) {
			return cadse;
		}
		CadseRuntime[] neededCadses = cadse.getRequiredCadses();
		if (neededCadses != null) {
			for (CadseRuntime cadseRuntime : neededCadses) {
				load(cxt, cadseRuntime, theWorkspaceLogique);
			}
		}
		boolean root = cadse.isCadseRoot();

		ModelRepository repository = ModelRepository.findModelFile(this, cadse.getName());

		long start = System.currentTimeMillis();

		CCadse ccadse = repository.loadWorkspaceType(this);
		cxt.reset();
		cxt.currentCadseName = cadse;

		cxt.itemTypes = repository.getItemType();
		String cstClass = ccadse.getCstClass();
		cxt.loadclass = cstClass != null;
		cxt.bundle = platformService.findBundle(cxt.currentCadseName.getName());
		if (cxt.bundle != null) {
			try {
				cxt.bundle.start();
			} catch (BundleException e) {
				_logger.log(Level.SEVERE, "Cannot start bundle " + cxt.currentCadseName, e);
			}
		}
		CLinkType rootMLT = null;
		if (root) {
			_logger.info("Load Root model");
			getItemType(true, theWorkspaceLogique, CadseDomain.META_ITEMTYPE_ID, cxt);
			if (CadseCore.mIT == null) {
				_logger.log(Level.SEVERE, "Cannot load meta item type");
				throw new RuntimeException("Cannot load meta item type");
			} else {
				_logger.info("Load meta item type");
			}
			//
			getItemType(true, theWorkspaceLogique, CadseDomain.ITEM_TYPE_ID, cxt);
			_logger.info("Load item type");
			Assert.isTrue(CadseCore.mIT.getSuperType() == CadseCore.theItemType, "super type of mIT bad set");

			CItemType cit = cxt.itemTypes.get(CadseCore.mIT.getId());
			if (cit != null) {
				List<CLinkType> links = cit.getOutgoingLink();
				for (CLinkType link : links) {
					// if (!(link instanceof CLinkType)) continue;

					CLinkType linkType = link;
					if (linkType.getName().equals("meta-link-type")) {
						_logger.info("Load meta linktype type");
						rootMLT = linkType;
						CadseCore.mLT = createLinkType(theWorkspaceLogique, CadseCore.mIT, linkType, cxt);
						_logger.info("Load meta linktype type");
						break;
					}
				}
			}
			if (CadseCore.mLT == null) {
				_logger.warning("Load default meta link type");
				CadseCore.mLT = ((LogicalWorkspaceImpl) theWorkspaceLogique).createMLTIfNeed();
			}
		}
		for (CItemType cit : cxt.itemTypes.values()) {
			getItemType(false, theWorkspaceLogique, getUUID(cit.getId()), cxt);
		}

		//
		for (ItemType source : cxt.initLink) {
			CItemType cit = cxt.itemTypes.get(source.getId());
			List<CLinkType> links = cit.getOutgoingLink();
			for (CLinkType link : links) {
				if (!(link instanceof CLinkType)) {
					continue;
				}

				CLinkType linkType = link;
				if (linkType == rootMLT) {
					continue;
				}
				try {
					createLinkType(theWorkspaceLogique, source, linkType, cxt);
				} catch (CadseException e) {
					_logger.log(Level.SEVERE, "Cannot create link type " + link.getCstName(), e);
					cadse.addError("Cannot create link type " + link.getCstName() + " : " + e.getMessage());
				}
			}
		}

		List<CExtensionItemType> extItemTypes = ccadse.getExtItemType();
		for (CExtensionItemType extit : extItemTypes) {
			CompactUUID uuid = getUUID(extit.getItemTypeSource());
			ItemType it = cxt.cacheItems.get(uuid);
			if (it == null) {
				it = theWorkspaceLogique.getItemType(uuid);
			}
			if (it == null) {
				final String errorMsg = "Cannot load the extension for the type " + extit.getItemTypeSource();
				_logger.log(Level.SEVERE, errorMsg);
				cadse.addError(errorMsg);
				continue;
			}
			loadAttributesDefinition(theWorkspaceLogique, extit, cxt, it);
			loadPageAndAction(cxt, it, extit);
			List<CLinkType> links = extit.getOutgoingLink();
			for (CLinkType link : links) {
				if (!(link instanceof CLinkType)) {
					continue;
				}

				CLinkType linkType = link;
				try {
					createLinkType(theWorkspaceLogique, it, linkType, cxt);
				} catch (CadseException e) {
					_logger.log(Level.SEVERE, "Cannot create link type " + link.getCstName(), e);
					cadse.addError("Cannot create link type " + link.getCstName() + " : " + e.getMessage());
				}
			}
		}
		if (cxt.loadclass) {
			load(cxt, cstClass);
		}

		if (root) {
			for (ItemType it : cxt.cacheItems.values()) {
				List<? extends Link> links = it.getOutgoingLinks();
				for (Link l : links) {
					if (l.getDestination() instanceof IAttributeType) {
						l.getDestination().addIncomingLink(l, false);
					}
				}
			}
		}
		for (ItemType it : cxt.cacheItems.values()) {
			try {
				it.getItemManager().init();
			} catch (Throwable e) {
				final String errorMsg = "Cannot init item type " + it.getDisplayName();
				_logger.log(Level.SEVERE, errorMsg, e);
				cadse.addError(errorMsg);
			}
		}

		cxt.currentCadseName.setExecuted(true);
		cxt.executedNumber++;

		_logger.finest("load cadse " + ccadse.getName() + " in " + (System.currentTimeMillis() - start) + " ms");

		return cxt.currentCadseName;
	}

	/**
	 * New instance.
	 * 
	 * @param cadseName
	 *            the cadse name
	 * @param cstClass
	 *            the cst class
	 * 
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T> T newInstance(String cadseName, String cstClass) {
		Class c = loadClass(cadseName, cstClass);
		if (c == null) {
			return null;
		}
		try {
			return (T) c.newInstance();
		} catch (InstantiationException e) {
			_logger.log(Level.SEVERE, "Cannot instanciate " + cstClass, e);
		} catch (IllegalAccessException e) {
			_logger.log(Level.SEVERE, "Cannot instanciate " + cstClass, e);
		} catch (LinkageError e) {
			_logger.log(Level.SEVERE, "Cannot instanciate " + cstClass, e);
		} catch (java.lang.Error e) {
			_logger.log(Level.SEVERE, "Cannot instanciate " + cstClass, e);
		}
		return null;
	}

	/**
	 * Load class.
	 * 
	 * @param cadseName
	 *            the cadse name
	 * @param qualifiedClassName
	 *            the qualified class name
	 * 
	 * @return the class< t>
	 */
	@SuppressWarnings("unchecked")
	public <T> Class<T> loadClass(String cadseName, String qualifiedClassName) {
		if (qualifiedClassName == null || qualifiedClassName.length() == 0) {
			return null;
		}
		int i = qualifiedClassName.indexOf('/');
		String contributorName = null;
		String className;
		if (i != -1) {
			contributorName = qualifiedClassName.substring(0, i).trim();
			className = qualifiedClassName.substring(i + 1).trim();
		} else {
			className = qualifiedClassName;
			contributorName = cadseName;
		}

		Bundle contributingBundle;
		contributingBundle = platformService.findBundle(contributorName);

		if (contributingBundle == null) {
			_logger.log(Level.SEVERE, "cannot find Bundle " + contributorName);
			return null;
		}

		// load the requested class from this bundle
		Class<T> classInstance = null;
		try {
			classInstance = contributingBundle.loadClass(className);
			return classInstance;
		} catch (Exception e) {
			_logger.log(Level.SEVERE, "Cannot load " + className + " from " + contributorName, e);
		} catch (LinkageError e) {
			_logger.log(Level.SEVERE, "Cannot load " + className + " from " + contributorName, e);
		}
		return null;
	}

	/**
	 * Initialize the given class with the values from the specified message
	 * bundle.
	 * 
	 * @param cxt
	 *            the cxt
	 * @param cstClass
	 *            the cst class
	 */
	@SuppressWarnings("unchecked")
	private void load(final InitContext cxt, final String cstClass) {
		if (System.getSecurityManager() == null) {
			load2(cxt, cstClass);
			return;
		}
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				load2(cxt, cstClass);
				return null;
			}
		});
	}

	/*
	 * Load the given resource bundle using the specified class loader.
	 */
	/**
	 * Load2.
	 * 
	 * @param cxt
	 *            the cxt
	 * @param clazzName
	 *            the clazz name
	 */
	void load2(InitContext cxt, String clazzName) {

		Class clazz = loadClass(cxt.getCurrentCadseName().getName(), clazzName);
		if (clazz == null) {
			return;
		}
		long start = System.currentTimeMillis();
		final Field[] fieldArray = clazz.getDeclaredFields();

		boolean isAccessible = (clazz.getModifiers() & Modifier.PUBLIC) != 0;

		// build a map of field names to Field objects
		final int len = fieldArray.length;
		for (int i = 0; i < len; i++) {
			Field field = fieldArray[i];
			Object value = cxt.values_to_field.get(field.getName());
			if (value == null) {
				continue;
			}
			// can only set value of public static non-final fields
			if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
				continue;
			}
			try {
				// Check to see if we are allowed to modify the field. If we
				// aren't (for instance
				// if the class is not public) then change the accessible
				// attribute of the field
				// before trying to set the value.
				if (!isAccessible) {
					field.setAccessible(true);
				}

				// Set the value into the field. We should never get an
				// exception here because
				// we know we have a public static non-final field. If we do get
				// an exception, silently
				// log it and continue. This means that the field will (most
				// likely) be un-initialized and
				// will fail later in the code and if so then we will see both
				// the NPE and this error.
				field.set(null, value);
			} catch (Exception e) {
				_logger.log(Level.SEVERE, "Exception setting field value.", e); //$NON-NLS-1$
				// e.printStackTrace();
			}
		}
		_logger.finest("set cst " + (System.currentTimeMillis() - start) + " ms");

	}

	/** The string_to_uuid. */
	Map<String, CompactUUID>	string_to_uuid;

	/**
	 * Gets the uUID.
	 * 
	 * @param id
	 *            the id
	 * 
	 * @return the uUID
	 */
	public CompactUUID getUUID(String id) {
		if (id == null) {
			return null;
		}
		if (string_to_uuid == null) {
			string_to_uuid = new HashMap<String, CompactUUID>();
		}
		CompactUUID ret = string_to_uuid.get(id);
		if (ret == null) {
			ret = new CompactUUID(id);
			string_to_uuid.put(id, ret);
		}
		return ret;
	}

	/**
	 * Gets the super type.
	 * 
	 * @param currentModelType
	 *            the current model type
	 * @param cit
	 *            the cit
	 * @param cxt
	 *            the cxt
	 * 
	 * @return the super type
	 */
	private ItemType getSuperType(LogicalWorkspace currentModelType, CItemType cit, InitContext cxt) {
		String name = cit.getSuperTypeName();
		if (name == null || name.length() == 0) {
			return null;
		}
		CompactUUID uuid = getUUID(name);
		return getItemType(false, currentModelType, uuid, cxt);
	}

	/**
	 * Gets the item type.
	 * 
	 * @param theWorkspaceLogique
	 *            the the workspace logique
	 * @param itemTypeId
	 *            the name
	 * @param cxt
	 *            the cxt
	 * 
	 * @return the item type
	 */
	private ItemType getItemType(boolean nosuper, LogicalWorkspace theWorkspaceLogique, CompactUUID itemTypeId,
			InitContext cxt) {
		ItemType it = cxt.cacheItems.get(itemTypeId);
		if (it != null) {
			return it;
		}
		try {
			it = theWorkspaceLogique.getItemType(itemTypeId);
			if (it != null) {
				return it;
			}
		} catch (Throwable e) {
			_logger.log(Level.SEVERE, "Cannot find item type.", e); //$NON-NLS-1$
		}
		CItemType cit = cxt.itemTypes.get(itemTypeId);
		ItemType super_it = null;
		if (!nosuper) {
			super_it = getSuperType(theWorkspaceLogique, cit, cxt);
		}
		it = createItemType(theWorkspaceLogique, cit, super_it, cxt);
		cxt.initLink.add(it);
		cxt.cacheItems.put(it.getId(), it);
		String cstName = cit.getCstName();
		if (cxt.loadclass && cstName != null) {
			cxt.values_to_field.put(cstName, it);
		}
		loadPageAndAction(cxt, it, cit);
		return it;
	}

	/**
	 * Load page and action.
	 * 
	 * @param cxt
	 *            the cxt
	 * @param it
	 *            the it
	 * @param cit
	 *            the cit
	 */
	private void loadPageAndAction(InitContext cxt, ItemType it, CAbsItemType cit) {
		CPages creationPagesInfo = cit.getCreationPages();
		List<IPageFactory> creationPages = loadPages(it, cxt, creationPagesInfo);
		List<IPageFactory> modificationPages = loadPages(it, cxt, cit.getModificationPages());

		if (creationPagesInfo != null && cit instanceof CItemType) {
			String mainActionClass = creationPagesInfo.getMainActionClass();
			Class<? extends AbstractActionPage> clazz = null;
			if (mainActionClass != null) {
				clazz = loadClass(cxt.getCurrentCadseName().getName(), mainActionClass);
			}
			it.setCreationAction(clazz, creationPagesInfo.getDefaultShortName());
		}
		if (creationPages != null) {
			it.addCreationPages(creationPages);
		}
		if (modificationPages != null) {
			it.addModificationPages(modificationPages);
		}

		List<IMenuAction> genActions = new ArrayList<IMenuAction>();
		List<CMenuAction> menuactions = cit.getMenu();
		for (CMenuAction action : menuactions) {
			genActions.add(GenericActionContributor.createMenu(action.getName(), action.getLabel(), action.getPath(),
					url(action.getIcon(), cxt)));
		}
		List<CAction> actions = cit.getAction();
		for (CAction action : actions) {
			MenuAction ma = newInstance(cxt.currentCadseName.getName(), action.getClassAction());
			if (ma != null) {
				ma.init(action.getName(), action.getLabel(), action.getPath(), action.getForNb(), url(action.getIcon(),
						cxt));
				genActions.add(ma);
			}
		}
		if (genActions.size() != 0) {
			it.addActionContributeur(new GenericActionContributor(genActions
					.toArray(new IMenuAction[genActions.size()])));
		}
		for (CActionContributor ca : cit.getActionContributor()) {
			IActionContributor ac = newInstance(cxt.currentCadseName.getName(), ca.getClazz());
			it.addActionContributeur(ac);
		}
	}

	/**
	 * Url.
	 * 
	 * @param iconFile
	 *            the icon file
	 * @param cxt
	 *            the cxt
	 * 
	 * @return the uRL
	 */
	private URL url(String iconFile, InitContext cxt) {
		if (iconFile == null || iconFile.length() == 0) {
			return null;
		}

		String bundleId = cxt.currentCadseName.getName();
		int indexOfpointpoint = iconFile.indexOf(':');
		if (indexOfpointpoint != -1) {
			bundleId = iconFile.substring(0, indexOfpointpoint);
			iconFile = iconFile.substring(indexOfpointpoint + 1);
		}
		Bundle bundle = platformService.findBundle(bundleId);
		if (bundle == null) {
			_logger.log(Level.SEVERE, "Cannot load icon : " + iconFile + " bundle not found " + bundleId);
			return null;
		}
		URL url = bundle.getEntry(iconFile);
		if (url == null) {
			_logger.log(Level.SEVERE, "Cannot create the url : " + iconFile);
			return null;
		}
		return url;

	}

	/**
	 * Load pages.
	 * 
	 * @param cxt
	 *            the cxt
	 * @param pages
	 *            the pages
	 * 
	 * @return the list< page factory>
	 */
	private List<IPageFactory> loadPages(ItemType it, InitContext cxt, CPages pages) {
		if (pages == null) {
			return null;
		}
		List<CPage> allPages = pages.getPage();
		if (allPages.size() == 0) {
			return null;
		}
		List<IPageFactory> pagesFactories = new ArrayList<IPageFactory>();
		for (int i = 0; i < allPages.size(); i++) {

			CPage cpage = allPages.get(i);
			int cas = cpage.getCas();
			if (cas == IPageFactory.PAGE_EMPTY) {
				pagesFactories.add(new EmptyPageFactory(getUUID(cpage.getUuid()), cpage.getId()));
			} else if (cas == 0) {
				PageFactory a = newInstance(cxt.currentCadseName.getName(), cpage.getClassName());
				if (a != null) {
					if (cpage.getTitre() != null) {
						a.setTitle(cpage.getTitre());
					}
					pagesFactories.add(a);
				}
			} else {
				Class<? extends PageImpl> clazz = loadClass(cxt.currentCadseName.getName(), cpage.getClassName());
				if (clazz != null) {
					try {
						ConfigurablePageFactory a = new ConfigurablePageFactory(it, getUUID(cpage.getUuid()), cas,
								cpage.getId(), clazz);
						if (cpage.getTitre() != null) {
							a.setTitle(cpage.getTitre());
						}
						pagesFactories.add(a);
					} catch (SecurityException e) {
						_logger.log(Level.SEVERE, "cannot create page " + cpage.getId() + ", " + cpage.getClassName(),
								e);
					} catch (NoSuchMethodException e) {
						_logger.log(Level.SEVERE, "cannot create page " + cpage.getId() + ", " + cpage.getClassName(),
								e);
					}
				}
			}
		}
		if (pagesFactories.size() != 0) {
			return pagesFactories;
		}
		return null;

	}

	/**
	 * Inits the manager.
	 * 
	 * @param cxt
	 *            the cxt
	 * @param it
	 *            the it
	 * @param itemType
	 *            the item type
	 * @param manager
	 *            the manager
	 */
	private void initManager(InitContext cxt, CItemType it, ItemType itemType, IItemManager manager) {
		itemType.setIcon(url(it.getIcon(), cxt));

		String pattern_valid_id = it.getPatternValidId();
		if (pattern_valid_id != null && pattern_valid_id.length() == 0) {
			pattern_valid_id = null;
		}
		if (pattern_valid_id != null) {
			try {
				Pattern.compile(pattern_valid_id);
			} catch (PatternSyntaxException e) {
				pattern_valid_id = null;
			}
		}

		String error_valid_id = it.getErrorValidId();
		if (error_valid_id != null && error_valid_id.length() == 0) {
			error_valid_id = null;
		}

		if (pattern_valid_id != null) {
			manager.setErrorValidId(error_valid_id);
			manager.setPatternValidId(pattern_valid_id);
		}

		String pattern_id = it.getPatternId(); // pattern_id
		if (pattern_id != null) {
			manager.setPatternId(pattern_id);
		}

	}

	/**
	 * Creates the item type.
	 * 
	 * @param theWorkspaceLogique
	 *            the the workspace logique
	 * @param cit
	 *            the cit
	 * @param super_it
	 *            the super_it
	 * @param cxt
	 *            the cxt
	 * 
	 * @return the item type
	 */
	private ItemType createItemType(LogicalWorkspace theWorkspaceLogique, CItemType cit, ItemType super_it,
			InitContext cxt) {
		String mc = cit.getManagerClass();
		ItemType it;
		IItemManager it_manager = null;
		if (mc != null) {
			it_manager = newInstance(cxt.currentCadseName.getName(), mc);
		}
		if (it_manager == null) {
			_logger.warning(MessageFormat.format(
					"Cannot find an item manager for type {0} : cannot find type in this model.", cit.getName()));
			it_manager = new DefaultItemManager();
		}
		ItemType metaType = CadseCore.mIT;
		CompactUUID metaTypeUUID = getUUID(cit.getMetaType());
		if (metaTypeUUID != null) {
			metaType = theWorkspaceLogique.getItemType(metaTypeUUID);
		}

		it = theWorkspaceLogique.createItemType(metaType, cxt.currentCadseName, super_it, cit.getIntID(), getUUID(cit
				.getId()), cit.getName(), cit.getDisplayName(), cit.isHasContent(), cit.isIsAbstract(), it_manager);
		String className = cit.getFactoryClass();
		if (className != null) {
			IItemFactory factory = newInstance(cxt.currentCadseName.getName(), className);
			it.setItemFactory(factory);
		}
		it.setRootElement(cit.isIsRootElement());
		it.setIsStatic(true);
		it.setPackageName(cit.getPackageName());

		it_manager.setItemType(it);

		if (cit.isHidden() != null && cit.isHidden().booleanValue()) {
			if (it instanceof AbstractGeneratedItem) {
				((AbstractGeneratedItem) it).setFlag(Item.IS_HIDDEN, true);
			}
		}
		initManager(cxt, cit, it, it_manager);

		finishLoadItemType(theWorkspaceLogique, cit, cxt, it);
		return it;
	}

	private void finishLoadItemType(LogicalWorkspace theWorkspaceLogique, CItemType cit, InitContext cxt, ItemType it) {
		// ((ItemTypeImpl) it).setItemManager(it_manager);
		loadAttributesDefinition(theWorkspaceLogique, cit, cxt, it);
		for (CItem item : cit.getAttributeDefinition()) {
			Item loadedItem = loadItem(theWorkspaceLogique, item);
			if (loadedItem != null && loadedItem instanceof IAttributeType) {
				IAttributeType<? extends Object> att = (IAttributeType<? extends Object>) loadedItem;
				att.setIsStatic(true);
				it.addAttributeType(att);
			}
		}
	}

	private void loadAttributesDefinition(LogicalWorkspace theWorkspaceLogique, CAbsItemType cit, InitContext cxt,
			ItemType it) {
		List<CMetaAttribute> metaAttributes = cit.getMetaAttribute();
		for (CMetaAttribute ma : metaAttributes) {
			try {
				ma.getValue().setKey(ma.getKey());
				it.setAttribute(ma.getKey(), convertToCValue(ma.getValue(), null));
			} catch (Throwable e) {
				_logger.log(Level.SEVERE, MessageFormat.format("Cannot create for type {0} an attribute {1}.", it
						.getName(), ma.getKey()), e);

			}
		}
		List<CAttType> attributeTypes2 = cit.getAttributeDefinition();
		for (CAttType attType : attributeTypes2) {
			try {
				IAttributeType<? extends Object> att = convertToAttributeType(attType, it, cxt.currentCadseName
						.getName());
				att.setIsStatic(true);
				initEvol(att, attType);
				it.addAttributeType(att);
				String cstName = attType.getCstName();
				if (cxt.loadclass && cstName != null && cstName.endsWith("_")) {
					cxt.values_to_field.put(cstName, att);
				}
			} catch (Throwable e) {
				_logger.log(Level.SEVERE, MessageFormat.format("Cannot create for type {0} an attribute {1}.", it
						.getName(), attType.getKey()), e);
			}
		}
		List<CValuesType> attributeTypes = cit.getAttributeType();
		for (CValuesType vt : attributeTypes) {
			try {
				IAttributeType<? extends Object> att = createAttrituteType(theWorkspaceLogique, it, vt,
						cxt.currentCadseName.getName());
				att.setIsStatic(true);
				initEvol(att, vt);
				it.addAttributeType(att);
				String cstName = vt.getCstName();
				if (cxt.loadclass && cstName != null && cstName.endsWith("_")) {
					cxt.values_to_field.put(cstName, att);
				}
			} catch (Throwable e) {
				_logger.log(Level.SEVERE, MessageFormat.format("Cannot create for type {0} an attribute {1}.", it
						.getName(), vt.getKey()), e);
				e.printStackTrace();
			}
		}
	}

	private void initEvol(IAttributeType<? extends Object> att, CValuesType attType) {
		TWCommitKind commitKind = convert(attType.getTwCommit());
		IInternalTWAttribute evolAtt = (IInternalTWAttribute) att;
		evolAtt.setTWCommitKind(commitKind);
		evolAtt.setEvol(convert(attType.getTwEvolution()));
		evolAtt.setTWRevSpecific(attType.isTwRevSpecific());
		evolAtt.setTWUpdateKind(convert(attType.getTwUpdate()));
	}

	private void initEvol(IAttributeType<? extends Object> att, CAttType attType) {
		TWCommitKind commitKind = convert(attType.getTwCommit());
		IInternalTWAttribute evolAtt = (IInternalTWAttribute) att;
		evolAtt.setTWCommitKind(commitKind);
		evolAtt.setEvol(convert(attType.getTwEvolution()));
		evolAtt.setTWRevSpecific(attType.isTwRevSpecific());
		evolAtt.setTWUpdateKind(convert(attType.getTwUpdate()));
	}

	private void initLinkEvol(IAttributeType<? extends Object> att, CLinkType attType) {
		TWCommitKind commitKind = convert(attType.getTwCommit());
		IInternalTWLink evolAtt = (IInternalTWLink) att;
		evolAtt.setTWCommitKind(commitKind);
		evolAtt.setEvol(convert(attType.getTwEvolution()));
		evolAtt.setTWRevSpecific(attType.isTwRevSpecific());
		evolAtt.setTWUpdateKind(convert(attType.getTwUpdate()));

		evolAtt.setTWCoupled(attType.isTwCoupled());
		evolAtt.setTWDestEvol(convert(attType.getTwEvolDestination()));
	}

	private TWDestEvol convert(EvolutionDestinationKindType twEvolDestination) {
		if (twEvolDestination == null) {
			return TWDestEvol.mutable;
		}
		switch (twEvolDestination) {
			case BRANCH:
				return TWDestEvol.branch;
			case EFFECTIVE:
				return TWDestEvol.effective;
			case FINAL:
				return TWDestEvol.finalDest;
			case IMMUTABLE:
				return TWDestEvol.immutable;
			case MUTABLE:
				return TWDestEvol.mutable;

			default:
				break;
		}
		return TWDestEvol.mutable;
	}

	private TWUpdateKind convert(UpdateKindType twUpdate) {
		if (twUpdate == null) {
			return TWUpdateKind.merge;
		}
		switch (twUpdate) {
			case COMPUTE:
				return TWUpdateKind.compute;
			case NONE:
				return TWUpdateKind.none;
			case MERGE:
				return TWUpdateKind.merge;

			default:
				break;
		}
		return TWUpdateKind.merge;
	}

	private TWEvol convert(EvolutionKindType twEvolution) {
		if (twEvolution == null) {
			return TWEvol.twImmutable;
		}
		switch (twEvolution) {
			case FINAL:
				return TWEvol.twFinal;
			case IMMUTABLE:
				return TWEvol.twImmutable;
			case MUTABLE:
				return TWEvol.twMutable;
			case TRANSIENT:
				return TWEvol.twTransient;

			default:
				break;
		}
		return TWEvol.twImmutable;
	}

	private TWCommitKind convert(CommitKindType twCommit) {
		if (twCommit == null) {
			return TWCommitKind.conflict;
		}
		switch (twCommit) {
			case CONFLICT:
				return TWCommitKind.conflict;
			case NONE:
				return TWCommitKind.none;
			case RECONCILE:
				return TWCommitKind.reconcile;

			default:
				break;
		}
		return TWCommitKind.conflict;
	}

	private Item loadItem(LogicalWorkspace theWorkspaceLogique, CItem item) {
		ItemType it = theWorkspaceLogique.getItemType(getUUID(item.getType()));

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Creates the attritute type.
	 * 
	 * @param type
	 *            the type
	 * @param cadseName
	 *            the cadse name
	 * 
	 * @return the i attribute type<? extends object>
	 * @throws CadseException
	 */
	public IAttributeType<?> createAttrituteType(LogicalWorkspace theWorkspaceLogique, ItemType parent,
			CValuesType type, String cadseName) throws CadseException {

		ValueTypeType kind = type.getType(); // kind peut etre null...
		ItemType attributeType = null;
		if (kind == null) {
			try {
				CompactUUID uuid = Convert.toUUID(type.getTypeName());
				if (uuid != null) {
					attributeType = (ItemType) theWorkspaceLogique.getItem(uuid);

				}
			} catch (Throwable e) {

			}
		} else {
			switch (kind) {
				case UUID: {
					attributeType = CadseRootCST.UUIDATTRIBUTE_TYPE;
					break;
				}

				case ITEM: {
					String uuid_str = type.getValue();
					if (uuid_str == null) {
						throw new CadseException("cannot create type from {0}", type.getKey());
					}

					CompactUUID uuid;
					try {
						uuid = new CompactUUID(uuid_str);
					} catch (IllegalArgumentException e1) {
						throw new CadseException("cannot create type from {0}", e1, type.getKey());
					}
					ItemType it = theWorkspaceLogique.getItemType(uuid);
					if (it == null) {
						throw new CadseException("cannot create type from {0}", type.getKey());
					}
					LogicalWorkspaceTransaction copy = theWorkspaceLogique.createTransaction();
					Item attType = copy.createItem(it, parent, CadseRootCST.META_ITEM_TYPE_lt_ATTRIBUTES_DEFINITION,
							getUUID(type.getId()), null, null);
					CompactUUID attTypeId = attType.getId();
					List<CValuesType> elements = type.getElement();
					if (elements != null) {
						for (CValuesType e : elements) {
							String c = e.getKey();
							IAttributeType<?> att = it.getAttributeType(e.getKey());
							if (att == null) {
								continue;
							}
							Object value = att.convertTo(e.getValue());
							attType.setAttribute(att, value);
							// TODO set flag
						}
					}
					copy.commit();
					return (IAttributeType<? extends Object>) theWorkspaceLogique.getItem(attTypeId);
				}
				case LIST: {
					attributeType = CadseRootCST.LIST_ATTRIBUTE_TYPE;
					break;
				}
				case STRUCT: {
					attributeType = CadseRootCST.STRUCT_ATTRIBUTE_TYPE;
					break;
				}
				case MAP: {
					attributeType = CadseRootCST.MAP_ATTRIBUTE_TYPE;
					break;
				}
				case BOOLEAN: {
					attributeType = CadseRootCST.BOOLEAN_ATTRIBUTE_TYPE;
					break;
				}
				case STRING: {
					attributeType = CadseRootCST.STRING_ATTRIBUTE_TYPE;
					break;
				}
				case INTEGER: {
					attributeType = CadseRootCST.INTEGER_ATTRIBUTE_TYPE;
					break;
				}
				case DATE: {
					attributeType = CadseRootCST.DATE_ATTRIBUTE_TYPE;
					break;
				}

				case DOUBLE: {
					attributeType = CadseRootCST.DOUBLE_ATTRIBUTE_TYPE;
					break;
				}

				case ENUMERATION: {
					attributeType = CadseRootCST.ENUM_ATTRIBUTE_TYPE;
					break;
				}
				case SYMBOLIC_BIT_MAP: {
					attributeType = CadseRootCST.SYMBOLIC_BIT_MAP_ATTRIBUTE_TYPE;
					break;
				}
			}
		}

		if (attributeType != null) {
			InitModelLoadAndWrite manager = (InitModelLoadAndWrite) attributeType.getItemManager();
			IAttributeType<?> ret = manager.loadAttributeDefinition(this, theWorkspaceLogique, parent, type, cadseName);
			if (ret != null) {
				return ret;
			}
		}
		// keep this for the bootstraping. At start the attribute doens't exist;
		if (kind == null) {
			//
			if ("3daef02c-3ef2-4c14-8ffa-ca98498039c3".equals(type.getTypeName())) {
				UUIDAttributeType ret = new UUIDAttributeType(this.getUUID(type.getId()), type.getKey(), this
						.getFlag(type));
				return ret;
			}
			throw new CadseException("cannot create type from {0}", type.getKey());
		}
		switch (kind) {
			case UUID: {
				UUIDAttributeType ret = new UUIDAttributeType(this.getUUID(type.getId()), type.getKey(), getFlag(type));
				return ret;
			}
			case ITEM: {
				String uuid_str = type.getValue();
				if (uuid_str == null) {
					throw new CadseException("cannot create type from {0}", type.getKey());
				}

				CompactUUID uuid;
				try {
					uuid = new CompactUUID(uuid_str);
				} catch (IllegalArgumentException e1) {
					throw new CadseException("cannot create type from {0}", e1, type.getKey());
				}
				ItemType it = theWorkspaceLogique.getItemType(uuid);
				if (it == null) {
					throw new CadseException("cannot create type from {0}", type.getKey());
				}
				LogicalWorkspaceTransaction copy = theWorkspaceLogique.createTransaction();
				Item attType = copy.createItem(it, parent, CadseRootCST.META_ITEM_TYPE_lt_ATTRIBUTES_DEFINITION,
						getUUID(type.getId()), null, null);
				CompactUUID attTypeId = attType.getId();
				List<CValuesType> elements = type.getElement();
				if (elements != null) {
					for (CValuesType e : elements) {
						String c = e.getKey();
						IAttributeType<?> att = it.getAttributeType(e.getKey());
						if (att == null) {
							continue;
						}
						Object value = att.convertTo(e.getValue());
						attType.setAttribute(att.getName(), value);
						// TODO set flag
					}
				}
				copy.commit();
				return (IAttributeType<? extends Object>) theWorkspaceLogique.getItem(attTypeId);
			}
			case LIST: {
				List<CValuesType> elements = type.getElement();
				if (elements == null || elements.size() != 1) {
					throw new CadseException("cannot create value from {0} : bad definition of list", type.getKey());
				}
				return new ListAttributeType(getUUID(type.getId()), getFlag(type), type.getKey(), getMin(type),
						getMax(type), createAttrituteType(theWorkspaceLogique, null, elements.get(0), cadseName));
			}
			case STRUCT: {
				List<CValuesType> elements = type.getElement();
				if (elements == null) {
					throw new CadseException("cannot create value from {0} : bad definition of map", type.getKey());
				}
				IAttributeType<? extends Object>[] defs = new IAttributeType<?>[elements.size()];
				for (int i = 0; i < defs.length; i++) {
					defs[i] = createAttrituteType(theWorkspaceLogique, null, elements.get(i), cadseName);
				}
				return new StructAttributeType(getUUID(type.getId()), type.getKey(), type.getMin(), defs);
			}
			case MAP: {
				List<CValuesType> elements = type.getElement();
				if (elements == null || elements.size() != 2) {
					throw new CadseException("cannot create value from {0} : bad definition of map", type.getKey());
				}
				return new MapAttributeType(getUUID(type.getId()), type.getKey(), type.getMin(), createAttrituteType(
						theWorkspaceLogique, null, elements.get(0), cadseName), createAttrituteType(
						theWorkspaceLogique, null, elements.get(1), cadseName));
			}
			case BOOLEAN: {
				BooleanAttributeType ret = new BooleanAttributeType(getUUID(type.getId()), getFlag(type),
						type.getKey(), type.getValue());
				return ret;
			}
			case STRING: {
				StringAttributeType ret = new StringAttributeType(getUUID(type.getId()), getFlag(type), type.getKey(),
						type.getValue());
				return ret;
			}
			case INTEGER: {
				IntegerAttributeType ret = new IntegerAttributeType(getUUID(type.getId()), getFlag(type),
						type.getKey(), null, null, type.getValue());
				return ret;
			}
			case DATE: {
				DateAttributeType ret = new DateAttributeType(getUUID(type.getId()), getFlag(type), type.getKey(), type
						.getValue());
				return ret;
			}

			case DOUBLE: {
				DoubleAttributeType ret = new DoubleAttributeType(getUUID(type.getId()), getFlag(type), type.getKey(),
						null, null, type.getValue());
				return ret;
			}

			case ENUMERATION: {
				String enumTypeName = type.getTypeName();
				if (type.getElement().size() == 1) {
					CValuesType clazzElement = type.getElement().get(0);
					enumTypeName = clazzElement.getValue();
				}
				Class<? extends Enum> clazz = loadClass(cadseName, enumTypeName);
				if (clazz == null) {
					throw new CadseException("cannot create type from {0}", type.getKey());
				}
				return new EnumAttributeType(getUUID(type.getId()), getFlag(type), type.getKey(), clazz, type
						.getValue());
			}
			case SYMBOLIC_BIT_MAP: {
				SymbolicBitMapAttributeType ret = new SymbolicBitMapAttributeType(getUUID(type.getId()), type.getKey(),
						getFlag(type), type.getValue());

				return ret;
			}
		}
		throw new CadseException("cannot create type from {0}", type.getKey());
		//
		//
		// }
		// switch (kind) {
		// case ITEM: {
		// String uuid_str = type.getValue();
		// if (uuid_str == null) throw new CadseException("cannot create type
		// from
		// {0}",type.getKey());
		//
		// CompactUUID uuid;
		// try {
		// uuid = new CompactUUID(uuid_str);
		// } catch (IllegalArgumentException e1) {
		// throw new CadseException("cannot create type from {0}",e1,
		// type.getKey());
		// }
		// ItemType it = theWorkspaceLogique.getItemType(uuid);
		// if (it == null) throw new CadseException("cannot create type from
		// {0}",type.getKey());
		// IWorkspaceLogiqueCopy copy = theWorkspaceLogique.createWorkingCopy();
		// Item attType = copy.createItem(it, parent,
		// CadseRootCST.META_ITEM_TYPE_lt_ATTRIBUTES_DEFINITION,
		// getUUID(type.getId()),
		// null, null);
		// CompactUUID attTypeId = attType.getId();
		// List<CValuesType> elements = type.getElement();
		// if (elements != null) {
		// for (CValuesType e : elements) {
		// String c = e.getKey();
		// IAttributeType<?> att = it.getAttributeType(e.getKey());
		// if (att == null) continue;
		// Object value = att.convertTo(e.getValue());
		// attType.setAttribute(att.getShortName(), value);
		// //TODO set flag
		// }
		// }
		// copy.commit();
		// return (IAttributeType<? extends Object>)
		// theWorkspaceLogique.getItem(attTypeId);
		// }
		// case LIST: {
		// List<CValuesType> elements = type.getElement();
		// if (elements == null || elements.size() !=1)
		// throw new CadseException("cannot create value from {0} : bad
		// definition of
		// list",type.getKey());
		// return new ListAttributeType(getUUID(type.getId()),
		// getFlag(type),
		// type.getKey(),
		// getMin(type),
		// getMax(type),createAttrituteType(theWorkspaceLogique,
		// null,elements.get(0),
		// cadseName));
		// }
		// case STRUCT: {
		// List<CValuesType> elements = type.getElement();
		// if (elements == null)
		// throw new CadseException("cannot create value from {0} : bad
		// definition of
		// map",type.getKey());
		// IAttributeType<? extends Object>[] defs = new
		// IAttributeType<?>[elements.size()];
		// for (int i = 0; i < defs.length; i++) {
		// defs[i] = createAttrituteType(theWorkspaceLogique,
		// null,elements.get(i),
		// cadseName);
		// }
		// return new StructAttributeType(getUUID(type.getId()), type.getKey(),
		// type.getMin(),
		// defs);
		// }
		// case MAP: {
		// List<CValuesType> elements = type.getElement();
		// if (elements == null || elements.size() !=2)
		// throw new CadseException("cannot create value from {0} : bad
		// definition of
		// map",type.getKey());
		// return new MapAttributeType(getUUID(type.getId()), type.getKey(),
		// type.getMin(),
		// createAttrituteType(theWorkspaceLogique, null,elements.get(0),
		// cadseName),
		// createAttrituteType(theWorkspaceLogique, null,elements.get(1),
		// cadseName));
		// }
		// case BOOLEAN: {
		// BooleanAttributeType ret = new
		// BooleanAttributeType(getUUID(type.getId()),
		// getFlag(type),
		// type.getKey(),
		// type.getValue());
		// return ret;
		// }
		// case STRING: {
		// StringAttributeType ret = new
		// StringAttributeType(getUUID(type.getId()),
		// getFlag(type),
		// type.getKey(),
		// type.getValue());
		// return ret;
		// }
		// case INTEGER: {
		// IntegerAttributeType ret = new
		// IntegerAttributeType(getUUID(type.getId()),
		// getFlag(type),
		// type.getKey(),
		// null,
		// null,
		// type.getValue());
		// return ret;
		// }
		// case DATE: {
		// DateAttributeType ret = new DateAttributeType(getUUID(type.getId()),
		// getFlag(type),
		// type.getKey(),
		// type.getValue());
		// return ret;
		// }
		//
		// case DOUBLE: {
		// DoubleAttributeType ret = new
		// DoubleAttributeType(getUUID(type.getId()),
		// getFlag(type),
		// type.getKey(),
		// null, null,
		// type.getValue());
		// return ret;
		// }
		//
		// case ENUMERATION: {
		// String enumTypeName = type.getTypeName();
		// if (type.getElement().size() == 1) {
		// CValuesType clazzElement = type.getElement().get(0);
		// enumTypeName = clazzElement.getValue();
		// }
		//
		// Class<? extends Enum> clazz = loadClass(cadseName, enumTypeName);
		// if (clazz == null)
		// throw new CadseException("cannot create type from
		// {0}",type.getKey());
		// return new EnumAttributeType(getUUID(type.getId()),
		// getFlag(type),
		// type.getKey(),
		// clazz ,
		// type.getValue());
		// }
		// case SYMBOLIC_BIT_MAP: {
		// SymbolicBitMapAttributeType ret = new
		// SymbolicBitMapAttributeType(getUUID(type.getId()),
		// type.getKey(),
		// getFlag(type),
		// type.getValue());
		//
		//
		// return ret;
		// }
		// }
		// throw new CadseException("cannot create type from
		// {0}",type.getKey());

	}

	public int getMin(CValuesType type) {
		if (type.getMin() != null) {
			return type.getMin();
		}
		return 0;
	}

	public int getMax(CValuesType type) {
		if (type.getMax() != null) {
			return type.getMax();
		}
		return Integer.MAX_VALUE;
	}

	public int getFlag(CValuesType type) {
		if (type.getFlag() != null) {
			return type.getFlag().intValue() | (type.getMin() == 1 ? Item.MUST_BE_INITIALIZED_AT_CREATION_TIME : 0);
		}

		return type.getMin() == 1 ? Item.MUST_BE_INITIALIZED_AT_CREATION_TIME : 0 + Item.DEFAULT_FLAG;
	}

	private int findIntegerValue(CValuesType item, String key, int defaultvalue) {
		List<CValuesType> elements = item.getElement();
		if (elements != null) {
			for (CValuesType vt : elements) {
				if (vt.getKey().equals(key)) {
					return Integer.parseInt(vt.getValue());
				}
			}
		}
		return defaultvalue;
	}

	/**
	 * Creates the link type.
	 * 
	 * @param currentModelType
	 *            the current model type
	 * @param source
	 *            the source
	 * @param linkType
	 *            the link type
	 * @param cxt
	 *            the cxt
	 * @return
	 * @throws CadseException
	 */
	private LinkType createLinkType(LogicalWorkspace currentModelType, ItemType source, CLinkType linkType,
			InitContext cxt) throws CadseException {
		int kind = 0;
		if (linkType.isIsAggregation()) {
			kind += LinkType.AGGREGATION;
		}
		if (linkType.isIsPart()) {
			kind += LinkType.PART;
		}
		if (linkType.isIsComposition()) {
			kind += LinkType.COMPOSITION;
		}
		if (linkType.isIsRequire()) {
			kind += LinkType.REQUIRE;
		}
		if (linkType.isIsAnnotation()) {
			kind += LinkType.ANNOTATION;
		}
		if (linkType.isHidden() != null && linkType.isHidden()) {
			kind += LinkType.HIDDEN;
		}

		int min = linkType.getMin();
		int max = linkType.getMax();
		// bug transitoire
		if (max == 0) {
			max = 1;
		}

		String inverse = linkType.getInverseLink();
		CompactUUID uuid = getUUID(linkType.getDestination());
		ItemType destType = currentModelType.getItemType(uuid);
		if (destType == null) {
			_logger.log(Level.SEVERE, "Cannot find item type " + linkType.getDestination());
			throw new CadseException("Cannot find the item type {0}.", linkType.getDestination());
		}
		// invers link
		LinkType inverselt = null;
		if (inverse != null && inverse.length() > 0) {
			inverselt = destType.getOutgoingLinkType(inverse);
			if (inverselt != null && inverselt.getSource() != destType) {
				// inverse not in super type...
				inverselt = null;
			}
		}

		// selection contrainte
		String selection = linkType.getSelectionExpression();
		if (selection != null && selection.length() == 0) {
			selection = null;
		}
		LinkType lt;
		if (inverselt != null) {
			lt = source.createLinkType(getUUID(linkType.getId()), linkType.getIntID(), linkType.getName(), kind, min,
					max, selection, inverselt);
		} else {
			lt = source.createLinkType(getUUID(linkType.getId()), linkType.getIntID(), linkType.getName(), kind, min,
					max, selection, destType);
			if (source == destType && inverse != null && inverse.length() > 0 && linkType.getName().equals(inverse)) {
				lt.setInverseLinkType(lt);
			}
		}
		String managerClassName = linkType.getManagerClass();
		ILinkTypeManager linkTypeManager = newInstance(cxt.currentCadseName.getName(), managerClassName);
		if (linkTypeManager == null) {
			linkTypeManager = new AbstractLinkTypeManager();
		}
		lt.setManager(linkTypeManager);
		initLinkEvol(lt, linkType);
		linkTypeManager.setLinkType(lt);

		String cstName = linkType.getCstName();
		if (cxt.loadclass && cstName != null) {
			cxt.values_to_field.put(cstName, lt);
		}
		if (Boolean.TRUE.equals(linkType.isIsNatif())) {
			lt.setIsNatif(true);
		}
		lt.setIsStatic(true);
		return lt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.role.initmodel.IInitModel#listCadseName()
	 */
	public String[] listCadseName() {
		ModelEntry[] models = findModel.findModelEntries("Workspace");
		List<String> ret = new ArrayList<String>();
		for (ModelEntry m : models) {
			try {
				URL aa = m.getEntry(ModelRepository.QUALIFIED_FILE_NAME);
				if (aa != null) {
					ret.add(m.getName());
				}
			} catch (IOException e) {
			}
		}
		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * Load from file.
	 * 
	 * @param cadsetype
	 *            the cadsetype
	 * @param modelfile
	 *            the modelfile
	 * @param modelname
	 *            the modelname
	 */
	public void loadFromFile(LogicalWorkspace cadsetype, File modelfile, String modelname) {
		// TODO Auto-generated method stub

	}

	public IFindModel getFindModelService() {
		return findModel;
	}

	public CCadse read(InputStream s) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("fede.workspace.tool.loadmodel.model.jaxb", this.getClass()
				.getClassLoader());
		Unmarshaller m = jc.createUnmarshaller();
		return (CCadse) m.unmarshal(s);
	}

	public CCadse load(File file) throws FileNotFoundException, JAXBException {
		return read(new FileInputStream(file));
	}

	public void save(CCadse cadse, File file) throws JAXBException, FileNotFoundException {
		JAXBContext jc = JAXBContext.newInstance("fede.workspace.tool.loadmodel.model.jaxb", this.getClass()
				.getClassLoader());
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(cadse, new FileOutputStream(file));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.as.initmodel.IInitModel#convertCadsegToCAttType(fr.imag.adele.cadse.core.Item)
	 */
	public CAttType convertCadsegToCAttType(Item attributeType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.as.initmodel.IInitModel#convertCadsegToCItemType(fr.imag.adele.cadse.core.Item)
	 */
	public CItemType convertCadsegToCItemType(Item itemType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.as.initmodel.IInitModel#convertToAttributeType(fede.workspace.tool.loadmodel.model.jaxb.CAttType)
	 */
	public IAttributeType<?> convertToAttributeType(CAttType attType, Item parent, String cadseName) {
		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		ItemType attTypeType = wl.getItemType(getUUID(attType.getType()));
		if (attTypeType == null) {
			return null;
		}
		synchronized (loadFactories) {
			for (int i = 0; i < this.loadFactories.length; i++) {
				IAttributeType<?> ret;
				try {
					ret = this.loadFactories[i].convertToAttributeType(this, parent, cadseName, attType, attTypeType);
					if (ret != null) {
						return ret;
					}
				} catch (CadseException e) {
					_logger.log(Level.SEVERE, "Cannot load attribute definition", e);
				}

			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.as.initmodel.IInitModel#convertToAttributeType(fede.workspace.tool.loadmodel.model.jaxb.CValuesType)
	 */
	public IAttributeType<?> convertToAttributeType(CValuesType attType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.as.initmodel.IInitModel#convertToCAttType(fr.imag.adele.cadse.core.attribute.IAttributeType)
	 */
	public CAttType convertToCAttType(IAttributeType<?> attributeType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.as.initmodel.IInitModel#convertToCItemType(fr.imag.adele.cadse.core.ItemType)
	 */
	public CItemType convertToCItemType(ItemType itemType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.as.initmodel.IInitModel#convertToItemType(fede.workspace.tool.loadmodel.model.jaxb.CItemType)
	 */
	public ItemType convertToItemType(CItemType itemType) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.as.initmodel.IInitModel#convertToCValue(fede.workspace.tool.loadmodel.model.jaxb.CValuesType,
	 *      fr.imag.adele.cadse.core.attribute.IAttributeType)
	 */
	public Object convertToCValue(CValuesType value, IAttributeType<?> attDefinition) {
		synchronized (loadFactories) {
			for (int i = 0; i < this.loadFactories.length; i++) {
				Object ret = this.loadFactories[i].convertToCValue(this, value, attDefinition);
				if (ret != null) {
					return ret;
				}
			}
		}
		throw new IllegalArgumentException(MessageFormat.format("cannot create value from key:{0}, value:{1}", value
				.getKey(), value.getValue()));

	}
}
