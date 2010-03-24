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

package fr.imag.adele.fede.workspace.si.initmodel.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import java.util.UUID;
import fr.imag.adele.cadse.util.Nullable;
import fr.imag.adele.fede.workspace.as.initmodel.ErrorWhenLoadedModel;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CCadse;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CItemType;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CTypeDefinition;
import fr.imag.adele.fede.workspace.si.initmodel.InitModel;
import fr.imag.adele.fede.workspace.si.initmodel.InitModelImpl;
import fr.imag.adele.melusine.as.findmodel.CheckModel;
import fr.imag.adele.melusine.as.findmodel.ModelEntry;

/**
 * This class represents a workspace model, comprising itemtypes and
 * workspacetype.
 * 
 * This particular implementation stores itemtype definition in an independent
 * XML document named types.xml and each workspacetypes in a separted file, in
 * the specified model directory.
 * 
 * @author Stephane Chomat
 * @version 1
 * @date 23/09/05
 */
public class ModelRepository  {

	/** The Constant FILE_NAME. */
	static public final String			FILE_NAME			= "cadse.xml";

	/** The Constant QUALIFIED_FILE_NAME. */
	static public final String			QUALIFIED_FILE_NAME	= "model/cadse.xml";

	/** The Constant METADATA. */
	private final static String			METADATA			= "definition.xml";

	/** The Constant SUFFIX. */
	private final static String			SUFFIX				= ".xml";

	/** The Constant TYPES_NAME_FILE. */
	private final static String			TYPES_NAME_FILE		= "types.xml";

	// private CItemType loadedWt = null;
	/** The type map. */
	private Map<UUID, CItemType>	typeMap;

	/** The model. */
	private ModelEntry					model;

	/** The cadse. */
	private CCadse						cadse;

	/**
	 * The Constructor.
	 * 
	 * @param model
	 *            the model
	 * 
	 * @exception IllegalArgumentException(Workspace
	 *                Model not found)
	 * @exception IllegalArgumentException(Workspace
	 *                has more one model)
	 */
	public ModelRepository(ModelEntry model) {
		this.model = model;
	}

//	/**
//	 * Instantiates a new model repository.
//	 */
//	public ModelRepository() {
//		model = null;
//	}

	/**
	 * Find model file.
	 * 
	 * @param initModel
	 * 
	 * @param qualifiedModelName
	 *            the qualified model name
	 * 
	 * @return the model repository
	 */
	public static ModelRepository findModelFile(InitModel initModel, String qualifiedModelName) {
		ModelEntry models = initModel.getFindModel().findQualifiedModel("Workspace", qualifiedModelName);
		if (models == null) {
			return null;
		}
		return new ModelRepository(models);
	}

	/**
	 * Find model file.
	 * 
	 * @param initModel
	 * 
	 * @param qualifiedModelName
	 *            the qualified model name
	 * 
	 * @return the model repository
	 */
	public static ModelEntry[] findModelFile(InitModel initModel) {
		return initModel.getFindModel().findModelEntries("Workspace", new CheckModel() {
			@Override
			public boolean check(ModelEntry e) {
				try {
					URL aa = e.getEntry(ModelRepository.QUALIFIED_FILE_NAME);
					return aa != null;
				} catch (IOException e1) {
				}
				return false;
			}
		});
	}

//	/**
//	 * Gets the types.
//	 * 
//	 * @return the unmodifiable map of the type
//	 */
//	public Map<UUID, CItemType> getTypes() {
//		return Collections.unmodifiableMap(typeMap);
//	}

//	/**
//	 * Gets the type.
//	 * 
//	 * @param typeName
//	 *            the name of the type to return
//	 * 
//	 * @return return the type which name is typeName.
//	 */
//	public CItemType getType(String typeName) {
//		return typeMap.get(typeName.toUpperCase());
//	}

	/**
	 * Read.
	 * 
	 * @param initModel
	 *            the init model
	 * @param s
	 *            the s
	 * 
	 * @throws JAXBException
	 *             the JAXB exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void read(InitModelImpl initModel, InputStream s) throws JAXBException, IOException {
		typeMap = new HashMap<UUID, CItemType>();
		cadse = initModel.read(s);
		List<CItemType> types = cadse.getItemType();
		for (CItemType it : types) {
			typeMap.put(initModel.getUUID(it.getId(), false, false), it);
		}

	}

//	/**
//	 * remove un type dans la map courrante des type.
//	 * 
//	 * @param type
//	 *            la definition d'un item type, peut �tre null.
//	 */
//	public void removeType(@Nullable
//	CItemType type) {
//		if (type != null) {
//			typeMap.remove(type.getName().toUpperCase());
//		}
//	}

//	/**
//	 * Accept.
//	 * 
//	 * @param dir
//	 *            the dir
//	 * @param name
//	 *            the name
//	 * 
//	 * @return true, if accept
//	 * 
//	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
//	 */
//	public boolean accept(@SuppressWarnings("unused")
//	File dir, String name) {
//		return name.endsWith(SUFFIX) && !name.equalsIgnoreCase(METADATA) && !name.equalsIgnoreCase(TYPES_NAME_FILE);
//	}

	/**
	 * Load workspace type.
	 * 
	 * @param initModel
	 *            the init model
	 * 
	 * @throws ErrorWhenLoadedModel
	 *             the error when loaded model
	 */
	public CCadse loadWorkspaceType(InitModelImpl initModel) throws ErrorWhenLoadedModel {
		try {
			URL models = model.getEntry(QUALIFIED_FILE_NAME);
			if (models == null) {
				throw new ErrorWhenLoadedModel("Cant find the entry " + QUALIFIED_FILE_NAME + " in " + model.getName());
			}

			read(initModel, models.openStream());
		} catch (FileNotFoundException e) {
			throw new ErrorWhenLoadedModel("Can't read the workspace type in the model " + model.getName(), e);
		} catch (IOException e) {
			throw new ErrorWhenLoadedModel("Can't read the workspace type in the model " + model.getName(), e);
		} catch (JAXBException e) {
			throw new ErrorWhenLoadedModel("Can't read the workspace type in the model " + model.getName(), e);
		}
		return cadse;

	}

	// return a copy modifiable
	/**
	 * Gets the item type.
	 * 
	 * @return the item type
	 */
	public Map<UUID, CTypeDefinition> getItemType() {
		return new HashMap<UUID, CTypeDefinition>(this.typeMap);
	}

//	/**
//	 * Gets the cadse.
//	 * 
//	 * @return the cadse
//	 * @throws IOException
//	 * @throws ErrorWhenLoadedModel
//	 */
//	public CCadse getCadse() {
//		return cadse;
//	}

	/**
	 * Gets the cadse.
	 * 
	 * @return the cadse
	 * @throws IOException
	 * @throws ErrorWhenLoadedModel
	 * @throws JAXBException
	 */
	public CCadse load(InitModelImpl initModel) throws ErrorWhenLoadedModel {
		if (cadse == null) {
			try {
				URL models = model.getEntry(QUALIFIED_FILE_NAME);
				if (models == null) {
					throw new ErrorWhenLoadedModel("Cant find the entry " + QUALIFIED_FILE_NAME + " in "
							+ model.getName());
				}
				cadse = initModel.read(models.openStream());
			} catch (FileNotFoundException e) {
				throw new ErrorWhenLoadedModel("Can't read the workspace type in the model " + model.getName(), e);
			} catch (IOException e) {
				throw new ErrorWhenLoadedModel("Can't read the workspace type in the model " + model.getName(), e);
			} catch (JAXBException e) {
				throw new ErrorWhenLoadedModel("Can't read the workspace type in the model " + model.getName(), e);
			}
		}
		return cadse;
	}

}
