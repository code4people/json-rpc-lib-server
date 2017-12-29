package com.nullables.jsonrpclib.server.server.handlers.dispatch.resolvers;

import com.nullables.jsonrpclib.server.handlers.methods.NamedParamDefinition;
import com.nullables.jsonrpclib.server.handlers.methods.NamedParamsMethod;
import com.nullables.jsonrpclib.server.exceptions.InvalidParamsException;
import com.nullables.jsonrpclib.server.handlers.dispatch.resolvers.NamedParamsMethodResolver;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NamedParamsMethodResolverTest {

    @Test
    public void resolve_shouldReturnResult_whenSingleCandidate() throws Exception {

        NamedParamsMethod method = mock(NamedParamsMethod.class);
        ArrayList<NamedParamsMethod> methods = new ArrayList<>();
        methods.add(method);
        NamedParamsMethodResolver matcher = new NamedParamsMethodResolver(methods);
        when(method.getNamedParamDefinitions()).thenReturn(Collections.emptyList());

        NamedParamsMethod result = matcher.resolve(new HashSet<>());

        assertEquals(method, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void resolve_shouldThrow_whenMultipleMatches() throws Exception {

        NamedParamsMethod method1 = mock(NamedParamsMethod.class);
        NamedParamsMethod method2 = mock(NamedParamsMethod.class);
        List<NamedParamDefinition> namedParamDefs1 = Collections.singletonList(
                new NamedParamDefinition("param1", true));
        List<NamedParamDefinition> namedParamDefs2 = Arrays.asList(
                new NamedParamDefinition("param1", true),
                new NamedParamDefinition("param2", false));
        ArrayList<NamedParamsMethod> methods = new ArrayList<>();
        methods.add(method1);
        methods.add(method2);
        NamedParamsMethodResolver matcher = new NamedParamsMethodResolver(methods);
        when(method1.getNamedParamDefinitions()).thenReturn(namedParamDefs1);
        when(method2.getNamedParamDefinitions()).thenReturn(namedParamDefs2);

        matcher.resolve(new HashSet<>(Arrays.asList("param1", "param2")));
    }

    @Test()
    public void resolve_shouldReturnResult_whenThereAreMultipleMethodsWithDifferentSignatures() throws Exception {

        NamedParamsMethod method1 = mock(NamedParamsMethod.class);
        NamedParamsMethod method2 = mock(NamedParamsMethod.class);
        List<NamedParamDefinition> namedParamDefs1 = Collections.singletonList(
                new NamedParamDefinition("param1", true));
        List<NamedParamDefinition> namedParamDefs2 = Arrays.asList(
                new NamedParamDefinition("paramA", true),
                new NamedParamDefinition("paramB", false));
        ArrayList<NamedParamsMethod> methods = new ArrayList<>();
        methods.add(method1);
        methods.add(method2);
        NamedParamsMethodResolver matcher = new NamedParamsMethodResolver(methods);
        when(method1.getNamedParamDefinitions()).thenReturn(namedParamDefs1);
        when(method2.getNamedParamDefinitions()).thenReturn(namedParamDefs2);

        NamedParamsMethod result = matcher.resolve(new HashSet<>(Arrays.asList("paramA", "paramB")));

        assertEquals(method2, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void resolve_shouldThrow_whenThereIsNoMatch() throws Exception {

        NamedParamsMethod method1 = mock(NamedParamsMethod.class);
        NamedParamsMethod method2 = mock(NamedParamsMethod.class);
        List<NamedParamDefinition> namedParamDefs1 = Collections.singletonList(
                new NamedParamDefinition("param1", false));
        List<NamedParamDefinition> namedParamDefs2 = Arrays.asList(
                new NamedParamDefinition("param1", false),
                new NamedParamDefinition("param2", false));
        ArrayList<NamedParamsMethod> methods = new ArrayList<>();
        methods.add(method1);
        methods.add(method2);
        NamedParamsMethodResolver matcher = new NamedParamsMethodResolver(methods);
        when(method1.getNamedParamDefinitions()).thenReturn(namedParamDefs1);
        when(method2.getNamedParamDefinitions()).thenReturn(namedParamDefs2);

        matcher.resolve(new HashSet<>(Arrays.asList("param1", "paramX")));
    }

    @Test(expected = InvalidParamsException.class)
    public void resolve_shouldThrow_whenThereIsAmbiguity() throws Exception {
        NamedParamsMethod method1 = mock(NamedParamsMethod.class);
        NamedParamsMethod method2 = mock(NamedParamsMethod.class);
        NamedParamsMethod method3 = mock(NamedParamsMethod.class);
        List<NamedParamDefinition> namedParamDefs1 = Arrays.asList(
                new NamedParamDefinition("param1", false),
                new NamedParamDefinition("param3", true));
        List<NamedParamDefinition> namedParamDefs2 = Arrays.asList(
                new NamedParamDefinition("param1", false),
                new NamedParamDefinition("param2", true));
        List<NamedParamDefinition> namedParamDefs3 = Arrays.asList(
                new NamedParamDefinition("param5", false),
                new NamedParamDefinition("param6", true));
        ArrayList<NamedParamsMethod> methods = new ArrayList<>();
        methods.add(method1);
        methods.add(method2);
        methods.add(method3);
        when(method1.getNamedParamDefinitions()).thenReturn(namedParamDefs1);
        when(method2.getNamedParamDefinitions()).thenReturn(namedParamDefs2);
        when(method3.getNamedParamDefinitions()).thenReturn(namedParamDefs3);

        NamedParamsMethodResolver matcher = new NamedParamsMethodResolver(methods);

        matcher.resolve(new HashSet<>(Collections.singletonList("param1")));
    }
}