/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import java.util.Base64;

import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Serializes byte array with Base64.
 */
public class ByteArrayBase64Serializer extends AbstractValueTypeSerializer<byte[]> {

    /**
     * Creates a new instance.
     *
     * @param customization Customization model.
     */
    public ByteArrayBase64Serializer(Customization customization) {
        super(customization);
    }

    @Override
    protected void serialize(byte[] obj, JsonGenerator generator, Marshaller marshaller) {
        generator.write(getEncoder(marshaller.getJsonbContext().getConfigProperties().getBinaryDataStrategy())
                                .encodeToString(obj));
    }

    private Base64.Encoder getEncoder(String strategy) {
        switch (strategy) {
        case BinaryDataStrategy.BASE_64:
            return Base64.getEncoder();
        case BinaryDataStrategy.BASE_64_URL:
            return Base64.getUrlEncoder();
        default:
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR,
                                                         "Invalid strategy: " + strategy));
        }
    }
}
