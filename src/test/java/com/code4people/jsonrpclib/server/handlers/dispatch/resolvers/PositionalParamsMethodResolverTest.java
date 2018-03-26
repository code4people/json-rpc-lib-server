package com.code4people.jsonrpclib.server.handlers.dispatch.resolvers;

import com.code4people.jsonrpclib.server.exceptions.InvalidParamsException;
import com.code4people.jsonrpclib.server.handlers.methods.PositionalParamsMethod;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PositionalParamsMethodResolverTest {

    private static PositionalParamsMethod createMethod(int mandatoryParamsCount, int optionalParamsCount) {

        PositionalParamsMethod mock = mock(PositionalParamsMethod.class, Mockito.RETURNS_SMART_NULLS);
        when(mock.getMandatoryParamsCount()).thenReturn(mandatoryParamsCount);
        when(mock.getParamsCount()).thenReturn(optionalParamsCount + mandatoryParamsCount);
        return mock;
    }

    @Test
    public void resolve_shouldReturnResult_whenSingleMatchingMethodExists() throws Exception {
        PositionalParamsMethod method = createMethod(1, 0);
        List<PositionalParamsMethod> methods = Collections.singletonList(method);
        PositionalParamsMethodResolver matcher = new PositionalParamsMethodResolver(methods);

        PositionalParamsMethod result = matcher.resolve(1);

        assertEquals(method, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void resolve_shouldThrow_whenCandidateMethodHasLessParameters() throws Exception {
        PositionalParamsMethod method = createMethod(1, 0);
        List<PositionalParamsMethod> methods = Collections.singletonList(method);
        PositionalParamsMethodResolver matcher = new PositionalParamsMethodResolver(methods);

        PositionalParamsMethod result = matcher.resolve(3);

        assertEquals(method, result);
    }

    @Test
    public void resolve_shouldReturnResult_whenMultipleCandidateMethodsExists() throws Exception {
        PositionalParamsMethod method1 = createMethod(1, 0);
        PositionalParamsMethod method2 = createMethod(2, 0);
        List<PositionalParamsMethod> methods = Arrays.asList(method1, method2);
        PositionalParamsMethodResolver matcher = new PositionalParamsMethodResolver(methods);

        PositionalParamsMethod result = matcher.resolve(2);

        assertEquals(method2, result);
    }


    @Test(expected = InvalidParamsException.class)
    public void resolve_shouldThrow_whenNoneOfMethodCandidatesMatch() throws Exception {
        PositionalParamsMethod method1 = createMethod(1, 0);
        PositionalParamsMethod method2 = createMethod(2, 0);
        List<PositionalParamsMethod> methods = Arrays.asList(method1, method2);
        PositionalParamsMethodResolver matcher = new PositionalParamsMethodResolver(methods);

        matcher.resolve(3);
    }

    @Test
    public void resolve_shouldReturnResult_whenThereAreMultipleUnambiguousMatchingMethodCandidates() throws Exception {
        PositionalParamsMethod method1 = createMethod(1, 0);
        PositionalParamsMethod method2 = createMethod(2, 0);
        PositionalParamsMethod method3 = createMethod(2, 1);
        List<PositionalParamsMethod> methods = Arrays.asList(method1, method2, method3);
        PositionalParamsMethodResolver matcher = new PositionalParamsMethodResolver(methods);

        PositionalParamsMethod result = matcher.resolve(2);

        assertEquals(method2, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void resolve_shouldThrow_whenThereAreMultipleAmbiguousMatchingMethodCandidates() throws Exception {
        PositionalParamsMethod method1 = createMethod(1, 1);
        PositionalParamsMethod method2 = createMethod(2, 0);
        PositionalParamsMethod method3 = createMethod(2, 1);
        List<PositionalParamsMethod> methods = Arrays.asList(method1, method2, method3);
        PositionalParamsMethodResolver matcher = new PositionalParamsMethodResolver(methods);

        matcher.resolve(2);
    }
}