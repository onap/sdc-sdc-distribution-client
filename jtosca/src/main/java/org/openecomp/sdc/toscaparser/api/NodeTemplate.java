package org.openecomp.sdc.toscaparser.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;
import org.openecomp.sdc.toscaparser.api.elements.*;
import org.openecomp.sdc.toscaparser.api.utils.CopyUtils;
import org.openecomp.sdc.toscaparser.api.utils.ThreadLocalsHolder;

public class NodeTemplate extends EntityTemplate {
	
	private LinkedHashMap<String,Object> templates;
	private LinkedHashMap<String,Object> customDef;
	private ArrayList<RelationshipTemplate> availableRelTpls;
	private LinkedHashMap<String,Object> availableRelTypes;
	private LinkedHashMap<NodeTemplate,RelationshipType> related;
	private ArrayList<RelationshipTemplate> relationshipTpl;
	private LinkedHashMap<RelationshipType,NodeTemplate> _relationships;
	private SubstitutionMappings subMappingToscaTemplate;
	private SubstitutionMappings subMappingToscaTemplate2;
	private Metadata metadata;

	private static final String METADATA = "metadata";

	@SuppressWarnings("unchecked")
	public NodeTemplate(String name,
						LinkedHashMap<String,Object> ntnodeTemplates,
						LinkedHashMap<String,Object> ntcustomDef,
						ArrayList<RelationshipTemplate> ntavailableRelTpls,
						LinkedHashMap<String,Object> ntavailableRelTypes) {
		
       super(name, (LinkedHashMap<String,Object>)ntnodeTemplates.get(name), "node_type", ntcustomDef);

       templates = ntnodeTemplates;
       _validateFields((LinkedHashMap<String,Object>)templates.get(name));
       customDef = ntcustomDef;
       related = new LinkedHashMap<NodeTemplate,RelationshipType>();
       relationshipTpl = new ArrayList<RelationshipTemplate>();
       availableRelTpls = ntavailableRelTpls;
       availableRelTypes = ntavailableRelTypes;
       _relationships = new LinkedHashMap<RelationshipType,NodeTemplate>();
       subMappingToscaTemplate = null;
       subMappingToscaTemplate2 = null;
       metadata = _metaData();
	}

	@SuppressWarnings("unchecked")
	public LinkedHashMap<RelationshipType,NodeTemplate> getRelationships() {
		if(_relationships.isEmpty()) {
			ArrayList<Object> requires = getRequirements();
			if(requires != null && requires instanceof ArrayList) {
				for(Object ro: requires) {
					LinkedHashMap<String,Object> r = (LinkedHashMap<String,Object>)ro;
					for(Map.Entry<String,Object> me: r.entrySet()) {
						LinkedHashMap<RelationshipType,NodeTemplate> explicit = _getExplicitRelationship(r,me.getValue());
						if(explicit != null) {
							// _relationships.putAll(explicit)...
							for(Map.Entry<RelationshipType,NodeTemplate> ee: explicit.entrySet()) {
								_relationships.put(ee.getKey(), ee.getValue());
							}
						}
					}
				}
			}
		}
		return _relationships;
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<RelationshipType,NodeTemplate> _getExplicitRelationship(LinkedHashMap<String,Object> req,Object value) {
        // Handle explicit relationship

        // For example,
        // - req:
        //     node: DBMS
        //     relationship: tosca.relationships.HostedOn
		
		LinkedHashMap<RelationshipType,NodeTemplate> explicitRelation = new LinkedHashMap<RelationshipType,NodeTemplate>();
		String node;
		if(value instanceof LinkedHashMap) {
			node = (String)((LinkedHashMap<String,Object>)value).get("node");
		}
		else {
			node = (String)value;
		}
		
		if(node != null && !node.isEmpty()) {
            //msg = _('Lookup by TOSCA types is not supported. '
            //        'Requirement for "%s" can not be full-filled.') % self.name
			boolean bFound = false;
			for(String k: EntityType.TOSCA_DEF.keySet()) {
				if(k.equals(node)) {
					bFound = true;
					break;
				}
			}
			if(bFound || customDef.get(node) != null) {
                ThreadLocalsHolder.getCollector().appendException(String.format(
                		"NotImplementedError: Lookup by TOSCA types is not supported. Requirement for \"%s\" can not be full-filled",
                		getName()));
                return null;
			}
			if(templates.get(node) == null) {
                ThreadLocalsHolder.getCollector().appendException(String.format(
                        "KeyError: Node template \"%s\" was not found",node));
                    return null;
			}
			NodeTemplate relatedTpl = new NodeTemplate(node,templates,customDef,null,null);
			Object relationship = null;
			String relationshipString = null;
			if(value instanceof LinkedHashMap) {
				relationship = ((LinkedHashMap<String,Object>)value).get("relationship");
				// here relationship can be a string or a LHM with 'type':<relationship>
			}
            // check if its type has relationship defined
			if(relationship == null) {
				ArrayList<Object> parentReqs = ((NodeType)typeDefinition).getAllRequirements();
				if(parentReqs == null) {
                    ThreadLocalsHolder.getCollector().appendException("ValidationError: parent_req is null");
				}
				else {
					for(String key: req.keySet()) {
						boolean bFoundRel = false;
						for(Object rdo: parentReqs) {
							LinkedHashMap<String,Object> reqDict = (LinkedHashMap<String,Object>)rdo;
							LinkedHashMap<String,Object> relDict = (LinkedHashMap<String,Object>)reqDict.get(key);
							if(relDict != null) {
								relationship = relDict.get("relationship");
								//BUG-python??? need to break twice?
								bFoundRel = true;
								break;
							}
						}
						if(bFoundRel) {
							break;
						}
					}
				}
			}
			
			if(relationship != null) {
				// here relationship can be a string or a LHM with 'type':<relationship>
				if(relationship instanceof String) {
					relationshipString = (String)relationship;
				}
				else if(relationship instanceof LinkedHashMap) {
					relationshipString = (String)((LinkedHashMap<String,Object>)relationship).get("type");
				}
				
				boolean foundRelationshipTpl = false;
				// apply available relationship templates if found
				if(availableRelTpls != null) {
					for(RelationshipTemplate tpl: availableRelTpls) {
						if(tpl.getName().equals(relationshipString)) {
							RelationshipType rtype = new RelationshipType(tpl.getType(),null,customDef);
							explicitRelation.put(rtype, relatedTpl);
							tpl.setTarget(relatedTpl);
							tpl.setSource(this);
							relationshipTpl.add(tpl);
							foundRelationshipTpl = true;
						}
					}
				}
				// create relationship template object.
				String relPrfx = EntityType.RELATIONSHIP_PREFIX;
				if(!foundRelationshipTpl) {
					if(relationship instanceof LinkedHashMap) {
            	   	   	relationshipString = (String)((LinkedHashMap<String,Object>)relationship).get("type");
            	   	   	if(relationshipString != null) {
            	   	   		if(availableRelTypes != null && !availableRelTypes.isEmpty() && 
            	   	   				availableRelTypes.get(relationshipString) != null) {
            	   	   			;
            	   	   		}
            	   	   		else if(!(relationshipString).startsWith(relPrfx)) {
            	   	   			relationshipString = relPrfx + relationshipString;
            	   	   		}
            	   	   	}
            	   	   	else {
            	   	   			ThreadLocalsHolder.getCollector().appendException(String.format(
            	   	   					"MissingRequiredFieldError: \"relationship\" used in template \"%s\" is missing required field \"type\"",
            	   	   					relatedTpl.getName()));
            		   	}
            	   }
            	   for(RelationshipType rtype: ((NodeType)typeDefinition).getRelationship().keySet()) {
            		   if(rtype.getType().equals(relationshipString)) {
            			   explicitRelation.put(rtype,relatedTpl);
            			   relatedTpl._addRelationshipTemplate(req,rtype.getType(),this);
            		   }
            		   else if(availableRelTypes != null && !availableRelTypes.isEmpty()) {
            			   LinkedHashMap<String,Object> relTypeDef = (LinkedHashMap<String,Object>)availableRelTypes.get(relationshipString);
            			   if(relTypeDef != null) {
            				   String superType = (String)relTypeDef.get("derived_from");
            				   if(superType != null) {
            					   if(!superType.startsWith(relPrfx)) {
            						   superType = relPrfx + superType;
            					   }
            					   if(rtype.getType().equals(superType)) {
                        			   explicitRelation.put(rtype,relatedTpl);
                        			   relatedTpl._addRelationshipTemplate(req,rtype.getType(),this);
            					   }
            				   }
            			   }
            		   }
            	   }
               }
			}
		}
		return explicitRelation;
	}

	@SuppressWarnings("unchecked")
	private void _addRelationshipTemplate(LinkedHashMap<String,Object> requirement, String rtype, NodeTemplate source) {
		LinkedHashMap<String,Object> req = (LinkedHashMap<String,Object>)CopyUtils.copyLhmOrAl(requirement);
		req.put("type",rtype);
		RelationshipTemplate tpl = new RelationshipTemplate(req, rtype, customDef, this, source);
		relationshipTpl.add(tpl);
	}

	public ArrayList<RelationshipTemplate> getRelationshipTemplate() {
		return relationshipTpl;
	}

	void _addNext(NodeTemplate nodetpl,RelationshipType relationship) {
		related.put(nodetpl,relationship);
	}
	
	public ArrayList<NodeTemplate> getRelatedNodes() {
		if(related.isEmpty()) { 
			for(Map.Entry<RelationshipType,NodeType> me: ((NodeType)typeDefinition).getRelationship().entrySet()) {
				RelationshipType relation = me.getKey();
				NodeType node = me.getValue();
				for(String tpl: templates.keySet()) {
					if(tpl.equals(node.getType())) {
						//BUG.. python has
						//    self.related[NodeTemplate(tpl)] = relation
						// but NodeTemplate doesn't have a constructor with just name...
						//????		
						related.put(new NodeTemplate(tpl,null,null,null,null),relation);
					}
				}
			}
		}
		return new ArrayList<NodeTemplate>(related.keySet());
	}

	public void validate(/*tosca_tpl=none is not used...*/) {
        _validateCapabilities();
        _validateRequirements();
        _validateProperties(entityTpl,(NodeType)typeDefinition);
        _validateInterfaces();
        for(Property prop: getPropertiesObjects()) {
        	prop.validate();
        }
	}

	private Metadata _metaData() {
		if(entityTpl.get(METADATA) != null) {
			return new Metadata((Map<String,Object>)entityTpl.get(METADATA));
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private void _validateRequirements() {
		ArrayList<Object> typeRequires = ((NodeType)typeDefinition).getAllRequirements();
		ArrayList<String> allowedReqs = new ArrayList<>();
		allowedReqs.add("template");
		if(typeRequires != null) {
			for(Object to: typeRequires) {
				LinkedHashMap<String,Object> treq = (LinkedHashMap<String,Object>)to;
				for(Map.Entry<String,Object> me: treq.entrySet()) {
					String key = me.getKey();
					Object  value = me.getValue();
					allowedReqs.add(key);
					if(value instanceof LinkedHashMap) {
						allowedReqs.addAll(((LinkedHashMap<String,Object>)value).keySet());
					}
				}
				
			}
		}
		
		ArrayList<Object> requires = (ArrayList<Object>)((NodeType)typeDefinition).getValue(REQUIREMENTS, entityTpl, false);
		if(requires != null) {
			if(!(requires instanceof ArrayList)) {
                ThreadLocalsHolder.getCollector().appendException(String.format(
                        "TypeMismatchError: \"requirements\" of template \"%s\" are not of type \"list\"",name));
			}
			else {
                for(Object ro: requires) {
                	LinkedHashMap<String,Object> req = (LinkedHashMap<String,Object>)ro;
                	for(Map.Entry<String,Object> me: req.entrySet()) {
                		String rl = me.getKey();
                		Object vo = me.getValue();
                		if(vo instanceof LinkedHashMap) {
                    		LinkedHashMap<String,Object> value = (LinkedHashMap<String,Object>)vo;
                			_validateRequirementsKeys(value);
                			_validateRequirementsProperties(value);
                			allowedReqs.add(rl);
                		}
                	}
                	_commonValidateField(req,allowedReqs,"requirements");
                }
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void _validateRequirementsProperties(LinkedHashMap<String,Object> reqs) {
        // TO-DO(anyone): Only occurrences property of the requirements is
        // validated here. Validation of other requirement properties are being
        // validated in different files. Better to keep all the requirements
        // properties validation here.
		for(Map.Entry<String,Object> me: reqs.entrySet()) {
			if(me.getKey().equals("occurrences")) {
				ArrayList<Object> val = (ArrayList<Object>)me.getValue();
				_validateOccurrences(val);
			}
			
		}
	}
	
	private void _validateOccurrences(ArrayList<Object> occurrences) {
        DataEntity.validateDatatype("list",occurrences,null,null,null);
        for(Object val: occurrences) {
            DataEntity.validateDatatype("Integer",val,null,null,null);
        }
        if(occurrences.size() != 2 || 
           !(0 <= (int)occurrences.get(0)  && (int)occurrences.get(0) <= (int)occurrences.get(1)) ||
           (int)occurrences.get(1) == 0) {
            ThreadLocalsHolder.getCollector().appendException(String.format(
                "InvalidPropertyValueError: property has invalid value %s",occurrences.toString()));
        }
	}
	
	private void _validateRequirementsKeys(LinkedHashMap<String,Object> reqs) {
		for(String key: reqs.keySet()) {
			boolean bFound = false;
			for(int i=0; i< REQUIREMENTS_SECTION.length; i++) {
				if(key.equals(REQUIREMENTS_SECTION[i])) {
					bFound = true;
					break;
				}
			}
			if(!bFound) {
                ThreadLocalsHolder.getCollector().appendException(String.format(
                        "UnknownFieldError: \"requirements\" of template \"%s\" contains unknown field \"%s\"",name,key));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void _validateInterfaces() {
		LinkedHashMap<String,Object> ifaces = (LinkedHashMap<String,Object>)
				((NodeType)typeDefinition).getValue(INTERFACES, entityTpl, false);
		if(ifaces != null) {
			for(Map.Entry<String,Object> me: ifaces.entrySet()) {
				String iname = me.getKey();
				LinkedHashMap<String,Object> value = (LinkedHashMap<String,Object>)me.getValue();
				if(iname.equals(InterfacesDef.LIFECYCLE) || iname.equals(InterfacesDef.LIFECYCLE_SHORTNAME)) {
					// maybe we should convert [] to arraylist???
					ArrayList<String> inlo = new ArrayList<>();
					for(int i=0; i<InterfacesDef.interfacesNodeLifecycleOperations.length; i++) {
						inlo.add(InterfacesDef.interfacesNodeLifecycleOperations[i]);
					}
                    _commonValidateField(value,inlo,"interfaces");
                }
				else if(iname.equals(InterfacesDef.CONFIGURE) || iname.equals(InterfacesDef.CONFIGURE_SHORTNAME)) {
					// maybe we should convert [] to arraylist???
					ArrayList<String> irco = new ArrayList<>();
					for(int i=0; i<InterfacesDef.interfacesRelationshipConfigureOperations.length; i++) {
						irco.add(InterfacesDef.interfacesRelationshipConfigureOperations[i]);
					}
                    _commonValidateField(value,irco,"interfaces");
                }
				else if(((NodeType)typeDefinition).getInterfaces().keySet().contains(iname)) {
					_commonValidateField(value,_collectCustomIfaceOperations(iname),"interfaces");
				}
				else {
                    ThreadLocalsHolder.getCollector().appendException(String.format(
                        "UnknownFieldError: \"interfaces\" of template \"%s\" contains unknown field %s",name,iname));
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> _collectCustomIfaceOperations(String iname) {
		ArrayList<String> allowedOperations = new ArrayList<>();
		LinkedHashMap<String,Object> nodetypeIfaceDef = (LinkedHashMap<String,Object>)((NodeType)
															typeDefinition).getInterfaces().get(iname);
		allowedOperations.addAll(nodetypeIfaceDef.keySet());
		String ifaceType = (String)nodetypeIfaceDef.get("type");
		if(ifaceType != null) {
			LinkedHashMap<String,Object> ifaceTypeDef = null;
			if(((NodeType)typeDefinition).customDef != null) {
				ifaceTypeDef = (LinkedHashMap<String,Object>)((NodeType)typeDefinition).customDef.get(ifaceType);
			}
			if(ifaceTypeDef == null) {
				ifaceTypeDef = (LinkedHashMap<String,Object>)EntityType.TOSCA_DEF.get(ifaceType);
			}
			allowedOperations.addAll(ifaceTypeDef.keySet());
		}
		// maybe we should convert [] to arraylist???
		ArrayList<String> idrw = new ArrayList<>();
		for(int i=0; i<InterfacesDef.INTERFACE_DEF_RESERVED_WORDS.length; i++) {
			idrw.add(InterfacesDef.INTERFACE_DEF_RESERVED_WORDS[i]);
		}
		allowedOperations.removeAll(idrw);
		return allowedOperations;
	}

	private void _validateFields(LinkedHashMap<String,Object> nodetemplate) {
		for(String ntname: nodetemplate.keySet()) {
			boolean bFound = false;
			for(int i=0; i< SECTIONS.length; i++) {
				if(ntname.equals(SECTIONS[i])) {
					bFound = true;
					break;
				}
			}
			if(!bFound) {
				for(int i=0; i< SPECIAL_SECTIONS.length; i++) {
					if(ntname.equals(SPECIAL_SECTIONS[i])) {
						bFound = true;
						break;
					}
				}
				
			}
			if(!bFound) {
                ThreadLocalsHolder.getCollector().appendException(String.format(
	                    "UnknownFieldError: Node template \"%s\" has unknown field \"%s\"",name,ntname));
			}
		}
	}
	
	// getter/setter
	
	public SubstitutionMappings getSubMappingToscaTemplate() {
		return subMappingToscaTemplate;
	}
	
	public void setSubMappingToscaTemplate(SubstitutionMappings sm) {
		subMappingToscaTemplate = sm;
	}
	
	// **experimental** (multilevel nesting)
	public SubstitutionMappings getSubMappingToscaTemplate2() {
		return subMappingToscaTemplate2;
	}
	
	public void setSubMappingToscaTemplate2(SubstitutionMappings sm) {
		subMappingToscaTemplate2 = sm;
	}
	
	public Metadata getMetaData() {
		return metadata;
	}

	public void setMetaData(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return getName();
	}

}

/*python

from toscaparser.common.exception import ExceptionCollector
from toscaparser.common.exception import InvalidPropertyValueError
from toscaparser.common.exception import MissingRequiredFieldError
from toscaparser.common.exception import TypeMismatchError
from toscaparser.common.exception import UnknownFieldError
from toscaparser.common.exception import ValidationError
from toscaparser.dataentity import DataEntity
from toscaparser.elements.interfaces import CONFIGURE
from toscaparser.elements.interfaces import CONFIGURE_SHORTNAME
from toscaparser.elements.interfaces import INTERFACE_DEF_RESERVED_WORDS
from toscaparser.elements.interfaces import InterfacesDef
from toscaparser.elements.interfaces import LIFECYCLE
from toscaparser.elements.interfaces import LIFECYCLE_SHORTNAME
from toscaparser.elements.relationshiptype import RelationshipType
from toscaparser.entity_template import EntityTemplate
from toscaparser.relationship_template import RelationshipTemplate
from toscaparser.utils.gettextutils import _

log = logging.getLogger('tosca')


class NodeTemplate(EntityTemplate):
    '''Node template from a Tosca profile.'''
    def __init__(self, name, node_templates, custom_def=None,
                 available_rel_tpls=None, available_rel_types=None):
        super(NodeTemplate, self).__init__(name, node_templates[name],
                                           'node_type',
                                           custom_def)
        self.templates = node_templates
        self._validate_fields(node_templates[name])
        self.custom_def = custom_def
        self.related = {}
        self.relationship_tpl = []
        self.available_rel_tpls = available_rel_tpls
        self.available_rel_types = available_rel_types
        self._relationships = {}
        self.sub_mapping_tosca_template = None

    @property
    def relationships(self):
        if not self._relationships:
            requires = self.requirements
            if requires and isinstance(requires, list):
                for r in requires:
                    for r1, value in r.items():
                        explicit = self._get_explicit_relationship(r, value)
                        if explicit:
                            for key, value in explicit.items():
                                self._relationships[key] = value
        return self._relationships

    def _get_explicit_relationship(self, req, value):
        """Handle explicit relationship

        For example,
        - req:
            node: DBMS
            relationship: tosca.relationships.HostedOn
        """
        explicit_relation = {}
        node = value.get('node') if isinstance(value, dict) else value

        if node:
            # TO-DO(spzala) implement look up once Glance meta data is available
            # to find a matching TOSCA node using the TOSCA types
            msg = _('Lookup by TOSCA types is not supported. '
                    'Requirement for "%s" can not be full-filled.') % self.name
            if (node in list(self.type_definition.TOSCA_DEF.keys())
               or node in self.custom_def):
                ExceptionCollector.appendException(NotImplementedError(msg))
                return

            if node not in self.templates:
                ExceptionCollector.appendException(
                    KeyError(_('Node template "%s" was not found.') % node))
                return

            related_tpl = NodeTemplate(node, self.templates, self.custom_def)
            relationship = value.get('relationship') \
                if isinstance(value, dict) else None
            # check if it's type has relationship defined
            if not relationship:
                parent_reqs = self.type_definition.get_all_requirements()
                if parent_reqs is None:
                    ExceptionCollector.appendException(
                        ValidationError(message='parent_req is ' +
                                        str(parent_reqs)))
                else:
                    for key in req.keys():
                        for req_dict in parent_reqs:
                            if key in req_dict.keys():
                                relationship = (req_dict.get(key).
                                                get('relationship'))
                                break
            if relationship:
                found_relationship_tpl = False
                # apply available relationship templates if found
                if self.available_rel_tpls:
                    for tpl in self.available_rel_tpls:
                        if tpl.name == relationship:
                            rtype = RelationshipType(tpl.type, None,
                                                     self.custom_def)
                            explicit_relation[rtype] = related_tpl
                            tpl.target = related_tpl
                            tpl.source = self
                            self.relationship_tpl.append(tpl)
                            found_relationship_tpl = True
                # create relationship template object.
                rel_prfx = self.type_definition.RELATIONSHIP_PREFIX
                if not found_relationship_tpl:
                    if isinstance(relationship, dict):
                        relationship = relationship.get('type')
                        if relationship:
                            if self.available_rel_types and \
                               relationship in self.available_rel_types.keys():
                                pass
                            elif not relationship.startswith(rel_prfx):
                                relationship = rel_prfx + relationship
                        else:
                            ExceptionCollector.appendException(
                                MissingRequiredFieldError(
                                    what=_('"relationship" used in template '
                                           '"%s"') % related_tpl.name,
                                    required=self.TYPE))
                    for rtype in self.type_definition.relationship.keys():
                        if rtype.type == relationship:
                            explicit_relation[rtype] = related_tpl
                            related_tpl._add_relationship_template(req,
                                                                   rtype.type,
                                                                   self)
                        elif self.available_rel_types:
                            if relationship in self.available_rel_types.keys():
                                rel_type_def = self.available_rel_types.\
                                    get(relationship)
                                if 'derived_from' in rel_type_def:
                                    super_type = \
                                        rel_type_def.get('derived_from')
                                    if not super_type.startswith(rel_prfx):
                                        super_type = rel_prfx + super_type
                                    if rtype.type == super_type:
                                        explicit_relation[rtype] = related_tpl
                                        related_tpl.\
                                            _add_relationship_template(
                                                req, rtype.type, self)
        return explicit_relation

    def _add_relationship_template(self, requirement, rtype, source):
        req = requirement.copy()
        req['type'] = rtype
        tpl = RelationshipTemplate(req, rtype, self.custom_def, self, source)
        self.relationship_tpl.append(tpl)

    def get_relationship_template(self):
        return self.relationship_tpl

    def _add_next(self, nodetpl, relationship):
        self.related[nodetpl] = relationship

    @property
    def related_nodes(self):
        if not self.related:
            for relation, node in self.type_definition.relationship.items():
                for tpl in self.templates:
                    if tpl == node.type:
                        self.related[NodeTemplate(tpl)] = relation
        return self.related.keys()

    def validate(self, tosca_tpl=None):
        self._validate_capabilities()
        self._validate_requirements()
        self._validate_properties(self.entity_tpl, self.type_definition)
        self._validate_interfaces()
        for prop in self.get_properties_objects():
            prop.validate()

    def _validate_requirements(self):
        type_requires = self.type_definition.get_all_requirements()
        allowed_reqs = ["template"]
        if type_requires:
            for treq in type_requires:
                for key, value in treq.items():
                    allowed_reqs.append(key)
                    if isinstance(value, dict):
                        for key in value:
                            allowed_reqs.append(key)

        requires = self.type_definition.get_value(self.REQUIREMENTS,
                                                  self.entity_tpl)
        if requires:
            if not isinstance(requires, list):
                ExceptionCollector.appendException(
                    TypeMismatchError(
                        what='"requirements" of template "%s"' % self.name,
                        type='list'))
            else:
                for req in requires:
                    for r1, value in req.items():
                        if isinstance(value, dict):
                            self._validate_requirements_keys(value)
                            self._validate_requirements_properties(value)
                            allowed_reqs.append(r1)
                    self._common_validate_field(req, allowed_reqs,
                                                'requirements')

    def _validate_requirements_properties(self, requirements):
        # TO-DO(anyone): Only occurrences property of the requirements is
        # validated here. Validation of other requirement properties are being
        # validated in different files. Better to keep all the requirements
        # properties validation here.
        for key, value in requirements.items():
            if key == 'occurrences':
                self._validate_occurrences(value)
                break

    def _validate_occurrences(self, occurrences):
        DataEntity.validate_datatype('list', occurrences)
        for value in occurrences:
            DataEntity.validate_datatype('integer', value)
        if len(occurrences) != 2 or not (0 <= occurrences[0] <= occurrences[1]) \
                or occurrences[1] == 0:
            ExceptionCollector.appendException(
                InvalidPropertyValueError(what=(occurrences)))

    def _validate_requirements_keys(self, requirement):
        for key in requirement.keys():
            if key not in self.REQUIREMENTS_SECTION:
                ExceptionCollector.appendException(
                    UnknownFieldError(
                        what='"requirements" of template "%s"' % self.name,
                        field=key))

    def _validate_interfaces(self):
        ifaces = self.type_definition.get_value(self.INTERFACES,
                                                self.entity_tpl)
        if ifaces:
            for name, value in ifaces.items():
                if name in (LIFECYCLE, LIFECYCLE_SHORTNAME):
                    self._common_validate_field(
                        value, InterfacesDef.
                        interfaces_node_lifecycle_operations,
                        'interfaces')
                elif name in (CONFIGURE, CONFIGURE_SHORTNAME):
                    self._common_validate_field(
                        value, InterfacesDef.
                        interfaces_relationship_configure_operations,
                        'interfaces')
                elif name in self.type_definition.interfaces.keys():
                    self._common_validate_field(
                        value,
                        self._collect_custom_iface_operations(name),
                        'interfaces')
                else:
                    ExceptionCollector.appendException(
                        UnknownFieldError(
                            what='"interfaces" of template "%s"' %
                            self.name, field=name))

    def _collect_custom_iface_operations(self, name):
        allowed_operations = []
        nodetype_iface_def = self.type_definition.interfaces[name]
        allowed_operations.extend(nodetype_iface_def.keys())
        if 'type' in nodetype_iface_def:
            iface_type = nodetype_iface_def['type']
            if iface_type in self.type_definition.custom_def:
                iface_type_def = self.type_definition.custom_def[iface_type]
            else:
                iface_type_def = self.type_definition.TOSCA_DEF[iface_type]
            allowed_operations.extend(iface_type_def.keys())
        allowed_operations = [op for op in allowed_operations if
                              op not in INTERFACE_DEF_RESERVED_WORDS]
        return allowed_operations

    def _validate_fields(self, nodetemplate):
        for name in nodetemplate.keys():
            if name not in self.SECTIONS and name not in self.SPECIAL_SECTIONS:
                ExceptionCollector.appendException(
                    UnknownFieldError(what='Node template "%s"' % self.name,
                                      field=name))*/