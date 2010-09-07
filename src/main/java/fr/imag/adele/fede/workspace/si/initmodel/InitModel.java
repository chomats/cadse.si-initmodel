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
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fr.imag.adele.fede.workspace.si.initmodel;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import fr.imag.adele.cadse.as.platformide.IPlatformIDE;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import java.util.UUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.workspace.as.classreferencer.IClassReferencer;
import fr.imag.adele.cadse.workspace.as.loadservice.ILoadFactory;
import fr.imag.adele.fede.workspace.as.initmodel.ErrorWhenLoadedModel;
import fr.imag.adele.fede.workspace.as.initmodel.IInitModel;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CAttType;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CCadse;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CItemType;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CValuesType;
import fr.imag.adele.melusine.as.findmodel.IFindModel;

public class InitModel implements IInitModel {
	InitModelImpl impl = new InitModelImpl(this);
	/**
	 * @generated
	 */
	public CadseDomain					workspaceCU;

	/**
	 * @generated
	 */
	public IFindModel					findModel;

	/**
	 * @generated
	 */
	public ILoadFactory[]				loadFactories;

	/**
	 * @generated
	 */
	public IClassReferencer			classReferencer;

	/**
	 * @generated
	 */
	public IPlatformIDE			platformService;

	
	
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

	public IPlatformIDE getPlatformService() {
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
	public UUID getUUID(String id) {
		return impl.getUUID(id, true, true);
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
