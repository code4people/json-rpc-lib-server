package com.code4people.jsonrpclib.server.handlers.methods;

import com.code4people.jsonrpclib.server.exceptions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.code4people.jsonrpclib.server.handlers.errorresolving.ServerError;
import com.code4people.jsonrpclib.server.serialization.ResultSerializer;
import com.code4people.jsonrpclib.server.handlers.errorresolving.MethodErrorMapping;
import com.code4people.jsonrpclib.server.serialization.ParamsDeserializer;
import com.code4people.jsonrpclib.server.serialization.SerializationException;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonMethodAdapterTest {
    @Test
    public void invoke_shouldReturnResult() throws Throwable {
        TextNode expectedResult = TextNode.valueOf("result");
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenReturn("abc");
        when(resultSerializer.serialize("result")).thenReturn(expectedResult);
        when(method.invoke(new Object[] { "abc" })).thenReturn("result");
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes, false, methodErrorMapping, paramsDeserializer, resultSerializer);

        JsonNode result = jsonMethodAdapter.invoke(new JsonNode[]{paramValue});

        assertEquals(expectedResult, result);
    }

    @Test
    public void invoke_shouldReturnMissingNode_whenMethodIsVoid() throws Throwable {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenReturn("abc");
        when(method.invoke(new Object[] { "abc" })).thenReturn(null);
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes, true, methodErrorMapping, paramsDeserializer, resultSerializer);

        JsonNode result = jsonMethodAdapter.invoke(new JsonNode[]{ paramValue });

        assertEquals(MissingNode.getInstance(), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invoke_shouldThrow_whenPassingWrongNumberOfParams() throws Exception {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class, String.class };
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes, false, methodErrorMapping, paramsDeserializer, resultSerializer);

        jsonMethodAdapter.invoke(new JsonNode[] { paramValue });
    }

    @Test
    public void invoke_shouldThrow_whenParamFailsToDeserialize() throws Exception {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        SerializationException serializationException = new SerializationException("", null);
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenThrow(serializationException);
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes, false, methodErrorMapping, paramsDeserializer, resultSerializer);

        try {
            jsonMethodAdapter.invoke(new JsonNode[]{paramValue});
            fail("Expected exception to be thrown");
        }
        catch (InvalidParamsException e) {
            assertNotNull(e.getMessage());
            assertEquals(serializationException, e.getCause());
        }
    }

    @Test
    public void invoke_shouldThrow_whenCustomErrorExceptionIsThrown() throws Throwable {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        Object errorData = new Object();
        String errorMessage = "Custom error detailMessage";
        int errorCode = 9;
        TextNode serializedErrorData = TextNode.valueOf("Serialized Error data");
        CustomErrorException customErrorException = new CustomErrorException(errorCode, errorMessage, errorData, null);
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenReturn("abc");
        when(resultSerializer.serialize(errorData)).thenReturn(serializedErrorData);
        when(method.invoke(new Object[] { "abc" })).thenThrow(customErrorException);
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes, false, methodErrorMapping, paramsDeserializer, resultSerializer);

        try {
            jsonMethodAdapter.invoke(new JsonNode[] { paramValue });
            fail("Expected exception to be thrown");
        }
        catch (SpecificServerErrorException e) {
            assertEquals(customErrorException, e.getCause());
            assertEquals(errorCode, e.getCode());
            assertEquals(serializedErrorData, e.getData());
            assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test(expected = InternalErrorException.class)
    public void invoke_shouldThrow_whenErrorDataFromCustomErrorExceptionFailsToDeserialize() throws Throwable {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        Object errorData = new Object();
        CustomErrorException customErrorException = new CustomErrorException(9, "error", errorData, null);
        SerializationException serializationException = new SerializationException("", null);
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenReturn("abc");
        when(resultSerializer.serialize(errorData)).thenThrow(serializationException);
        when(method.invoke(new Object[] { "abc" })).thenThrow(customErrorException);
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes,false, methodErrorMapping, paramsDeserializer, resultSerializer);

        jsonMethodAdapter.invoke(new JsonNode[] { paramValue });
    }

    @Test
    public void invoke_shouldThrow_whenGeneralThrowableIsThrownAndNoMappingIsFound() throws Throwable {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        Throwable throwable = new Throwable();
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenReturn("abc");
        when(methodErrorMapping.resolve(throwable)).thenReturn(Optional.empty());
        when(method.invoke(new Object[] { "abc" })).thenThrow(throwable);
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes,false, methodErrorMapping, paramsDeserializer, resultSerializer);

        try {
            jsonMethodAdapter.invoke(new JsonNode[] { paramValue });
            fail("Expected exception to be thrown");
        }
        catch (InternalErrorException e) {
            assertNotNull(e.getMessage());
            assertEquals(throwable, e.getCause());
        }
    }

    @Test
    public void invoke_shouldRethrow_whenBaseErrorExceptionIsThrown() throws Throwable {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        BaseErrorException baseErrorException = new BaseErrorException("");
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenReturn("abc");
        when(method.invoke(new Object[] { "abc" })).thenThrow(baseErrorException);
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes,false, methodErrorMapping, paramsDeserializer, resultSerializer);

        try {
            jsonMethodAdapter.invoke(new JsonNode[] { paramValue });
            fail("Expected exception to be thrown");
        }
        catch (BaseErrorException e) {
            assertEquals(baseErrorException, e);
        }
    }

    @Test
    public void invoke_shouldThrow_whenGeneralThrowableIsThrownAndMappingIsFound() throws Throwable {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        Object errorData = new Object();
        String errorMessage = "Custom error detailMessage";
        int errorCode = 9;
        Throwable throwable = new Throwable();
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenReturn("abc");
        when(methodErrorMapping.resolve(throwable)).thenReturn(Optional.of(ServerError.of(errorCode, errorMessage, errorData)));
        when(method.invoke(new Object[] { "abc" })).thenThrow(throwable);
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes, false, methodErrorMapping, paramsDeserializer, resultSerializer);

        try {
            jsonMethodAdapter.invoke(new JsonNode[] { paramValue });
            fail("Expected exception to be thrown");
        }
        catch (SpecificServerErrorException e) {
            assertEquals(throwable, e.getCause());
            assertEquals(errorCode, e.getCode());
            assertEquals(null, e.getData());
            assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test
    public void invoke_shouldThrow_whenResultFailsToDeserialize() throws Throwable {
        TextNode paramValue = new TextNode("abc");
        Type[] paramTypes = new Type[] { String.class };
        SerializationException serializationException = new SerializationException("", null);
        ParamsDeserializer paramsDeserializer = mock(ParamsDeserializer.class, Mockito.RETURNS_SMART_NULLS);
        ResultSerializer resultSerializer = mock(ResultSerializer.class, Mockito.RETURNS_SMART_NULLS);
        Method method = mock(Method.class, Mockito.RETURNS_SMART_NULLS);
        MethodErrorMapping methodErrorMapping = mock(MethodErrorMapping.class, Mockito.RETURNS_SMART_NULLS);
        when(paramsDeserializer.deserialize(paramValue, String.class)).thenReturn("abc");
        when(resultSerializer.serialize("result")).thenThrow(serializationException);
        when(method.invoke(new Object[] { "abc" })).thenReturn("result");
        JsonMethodAdapter jsonMethodAdapter = new JsonMethodAdapter(method, paramTypes, false, methodErrorMapping, paramsDeserializer, resultSerializer);

        try {
            jsonMethodAdapter.invoke(new JsonNode[] { paramValue });
            fail("Expected exception to be thrown");
        }
        catch (InternalErrorException e) {
            assertNotNull(e.getMessage());
            assertEquals(serializationException, e.getCause());
        }
    }
}