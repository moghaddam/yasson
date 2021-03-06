/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.defaultmapping.anonymous;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

/**
 * This class contains tests for marshalling/unmarshalling anonymous classes.
 *
 * @author Dmitry Kornilov
 */
public class AnonymousClassTest {

    @Test
    public void testMarshallInnerClass() {
        assertEquals("{\"anonymousField\":\"anonymousValue\",\"id\":1,\"name\":\"pojoName\"}", defaultJsonb.toJson(new InnerPojo() {
            public String anonymousField = "anonymousValue";

            @Override
            public Integer getId() {
                return 1;
            }

            @Override
            public String getName() {
                return "pojoName";
            }
        }));
    }

    @Test
    public void testMarshallOuterClass() {
        assertEquals("{\"id\":1,\"anonymousField\":\"anonymousValue\"}", defaultJsonb.toJson(new OuterPojo() {
            public String anonymousField = "anonymousValue";
        }));
    }

    public static class InnerPojo {
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
