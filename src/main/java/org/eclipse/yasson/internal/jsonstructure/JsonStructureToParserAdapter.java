/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.yasson.internal.jsonstructure;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;

/**
 * Adapter for {@link JsonParser}, that reads a {@link JsonStructure} content tree instead of JSON text.
 *
 * Yasson and jsonb API components are using {@link JsonParser} as its input API.
 * This adapter allows deserialization of {@link JsonStructure} into java content tree using same components
 * as when parsing JSON text.
 */
public class JsonStructureToParserAdapter implements JsonParser {

    private Deque<JsonStructureIterator> iterators = new ArrayDeque<>();

    private final JsonStructure rootStructure;

    /**
     * Creates new {@link JsonStructure} parser.
     *
     * @param structure json structure
     */
    public JsonStructureToParserAdapter(JsonStructure structure) {
        this.rootStructure = structure;
    }

    @Override
    public boolean hasNext() {
        return iterators.peek().hasNext();
    }

    @Override
    public Event next() {
        if (iterators.isEmpty()) {
            if (rootStructure instanceof JsonObject) {
                iterators.push(new JsonObjectIterator((JsonObject) rootStructure));
                return Event.START_OBJECT;
            } else if (rootStructure instanceof JsonArray) {
                iterators.push(new JsonArrayIterator((JsonArray) rootStructure));
                return Event.START_ARRAY;
            }
        }
        JsonStructureIterator current = iterators.peek();
        Event next = current.next();
        if (next == Event.START_OBJECT) {
            iterators.push(new JsonObjectIterator((JsonObject) iterators.peek().getValue()));
        } else if (next == Event.START_ARRAY) {
            iterators.push(new JsonArrayIterator((JsonArray) iterators.peek().getValue()));
        } else if (next == Event.END_OBJECT || next == Event.END_ARRAY) {
            iterators.pop();
        }
        return next;
    }

    @Override
    public String getString() {
        return iterators.peek().getString();
    }

    @Override
    public boolean isIntegralNumber() {
        return getJsonNumberValue().isIntegral();
    }

    @Override
    public int getInt() {
        return getJsonNumberValue().intValueExact();
    }

    @Override
    public long getLong() {
        return getJsonNumberValue().longValueExact();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return getJsonNumberValue().bigDecimalValue();
    }

    private JsonNumber getJsonNumberValue() {
        JsonStructureIterator iterator = iterators.peek();
        JsonValue value = iterator.getValue();
        if (value.getValueType() != JsonValue.ValueType.NUMBER) {
            throw iterator.createIncompatibleValueError();
        }
        return (JsonNumber) value;
    }

    @Override
    public JsonLocation getLocation() {
        throw new JsonbException("Operation not supported");
    }

    @Override
    public void close() {
        //noop
    }
}
