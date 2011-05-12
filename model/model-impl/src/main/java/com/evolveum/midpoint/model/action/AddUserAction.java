/*
 * Copyright (c) 2011 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 *
 * Portions Copyrighted 2011 [name of copyright owner]
 * Portions Copyrighted 2010 Forgerock
 */

package com.evolveum.midpoint.model.action;

import com.evolveum.midpoint.model.*;
import com.evolveum.midpoint.api.logging.Trace;
import com.evolveum.midpoint.logging.TraceManager;
import com.evolveum.midpoint.model.xpath.SchemaHandlingException;
import com.evolveum.midpoint.util.DebugUtil;
import com.evolveum.midpoint.util.patch.PatchException;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ObjectContainerType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ObjectFactory;
import com.evolveum.midpoint.xml.ns._public.common.common_1.PropertyReferenceListType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ResourceObjectShadowChangeDescriptionType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ResourceObjectShadowType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.UserType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.SynchronizationSituationType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.UserTemplateType;
import com.evolveum.midpoint.xml.ns._public.model.model_1.FaultMessage;
import com.evolveum.midpoint.xml.schema.SchemaConstants;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Vilo Repan
 */
public class AddUserAction extends BaseAction {

    private static Trace trace = TraceManager.getTrace(AddUserAction.class);

    @Override
    public String executeChanges(String userOid, ResourceObjectShadowChangeDescriptionType change,
            SynchronizationSituationType situation, ResourceObjectShadowType shadowAfterChange) throws SynchronizationException {
        UserType userType = getUser(userOid);

        ObjectFactory of = new ObjectFactory();
        if (userType == null) {
            //user was not found, so create user
            userType = of.createUserType();
            UserTemplateType userTemplate = getUserTemplate();

            try {

                if (trace.isDebugEnabled()) {
                    trace.debug("Action:addUser: Resource Object Shadow before action: {}", DebugUtil.toReadableString(shadowAfterChange));
                }
                userType = getSchemaHandling().applyInboundSchemaHandlingOnUser(userType, shadowAfterChange);

                if (trace.isDebugEnabled()) {
                    trace.debug("Action:addUser: User after processing of inbound expressions: {}",DebugUtil.toReadableString(userType));
                }

                //apply user template
                userType = getSchemaHandling().applyUserTemplate(userType, userTemplate);

                if (trace.isDebugEnabled()) {
                    trace.debug("Action:addUser: User after processing of user template: {}",DebugUtil.toReadableString(userType));
                }

                //save user
                ObjectContainerType userContainer = of.createObjectContainerType();
                userContainer.setObject(userType);
                userOid = getModel().addObject(userContainer);
            } catch (com.evolveum.midpoint.xml.ns._public.model.model_1.FaultMessage ex) {
                throw new SynchronizationException("Can't save user", ex, ex.getFaultInfo());
            } catch (SchemaHandlingException ex) {
                throw new SynchronizationException("Couldn't apply user template '" +
                        userTemplate.getOid() + "' on user '" + userOid + "'.", ex, ex.getFaultType());
            } catch (PatchException ex) {
                throw new SynchronizationException("Couldn't apply user template '" +
                        userTemplate.getOid() + "' on user '" + userOid + "'.", ex, null);
            }
        } else {
            trace.debug("User already exists ({}), skipping create.", userType.getOid());
        }

        return userOid;
    }

    private String getUserTemplateOid() {
        List<Object> parameters = getParameters();
        Element userTemplateRef = null;
        for (Object object : parameters) {
            if (!(object instanceof Element)) {
                continue;
            }
            Element element = (Element) object;
            if ("userTemplateRef".equals(element.getLocalName()) &&
                    SchemaConstants.NS_C.equals(element.getNamespaceURI())) {
                userTemplateRef = element;
                break;
            }
        }

        if (userTemplateRef != null) {
            return userTemplateRef.getAttribute("oid");
        }

        return null;
    }

    private UserTemplateType getUserTemplate() throws SynchronizationException {
        String userTemplateOid = getUserTemplateOid();
        if (userTemplateOid == null) {
            throw new SynchronizationException("User Template Oid not defined in parameters for this action.");
        }

        UserTemplateType userTemplate = null;
        try {
            ObjectContainerType container = getModel().getObject(userTemplateOid, new PropertyReferenceListType());
            userTemplate = (UserTemplateType) container.getObject();
        } catch (FaultMessage ex) {
            throw new SynchronizationException("Couldn't get user template with oid '" +
                    userTemplateOid + "'.", ex, ex.getFaultInfo());
        }

        return userTemplate;
    }
}
