package com.code4people.jsonrpclib.server.handlers.methods;

import com.code4people.jsonrpclib.server.exceptions.InternalErrorException;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.WrongMethodTypeException;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodTest {
    @Test
    public void invoke_shouldReturnResult() throws Throwable {
        MethodHandle methodHandle = mock(MethodHandle.class, Mockito.RETURNS_SMART_NULLS);
        Supplier receiverSupplier = mock(Supplier.class, Mockito.RETURNS_SMART_NULLS);
        Object receiver = new Object();
        when(receiverSupplier.get()).thenReturn(receiver);
        when(methodHandle.invokeWithArguments(receiver, "1", "2")).thenReturn("result");
        Method method = new Method(receiverSupplier, methodHandle);

        Object result = method.invoke(new Object[] { "1", "2" });

        assertEquals("result", result);
    }

    @Test
    public void invoke_shouldReturnResult_whenPassingZeroParams() throws Throwable {
        MethodHandle methodHandle = mock(MethodHandle.class, Mockito.RETURNS_SMART_NULLS);
        Supplier receiverSupplier = mock(Supplier.class, Mockito.RETURNS_SMART_NULLS);
        Object receiver = new Object();
        when(receiverSupplier.get()).thenReturn(receiver);
        when(methodHandle.invokeWithArguments(receiver)).thenReturn("result");
        Method method = new Method(receiverSupplier, methodHandle);

        Object result = method.invoke(new Object[] { });

        assertEquals("result", result);
    }

    @Test(expected = NullPointerException.class)
    public void invoke_shouldThrow_whenParamsIsNull() throws Throwable {
        MethodHandle methodHandle = mock(MethodHandle.class, Mockito.RETURNS_SMART_NULLS);
        Supplier receiverSupplier = mock(Supplier.class, Mockito.RETURNS_SMART_NULLS);
        Object receiver = new Object();
        when(receiverSupplier.get()).thenReturn(receiver);
        Method method = new Method(receiverSupplier, methodHandle);

        method.invoke(null);
    }

    @Test(expected = InternalErrorException.class)
    public void invoke_shouldThrow_whenReceiverIsNull() throws Throwable {
        MethodHandle methodHandle = mock(MethodHandle.class, Mockito.RETURNS_SMART_NULLS);
        Supplier receiverSupplier = mock(Supplier.class, Mockito.RETURNS_SMART_NULLS);
        when(receiverSupplier.get()).thenReturn(null);
        Method method = new Method(receiverSupplier, methodHandle);

        method.invoke(new Object[] { });
    }

    @Test(expected = InternalErrorException.class)
    public void invoke_shouldThrow_whenExceptionWasThrownDuringObtainingOfReceiver() throws Throwable {
        MethodHandle methodHandle = mock(MethodHandle.class, Mockito.RETURNS_SMART_NULLS);
        Supplier receiverSupplier = mock(Supplier.class, Mockito.RETURNS_SMART_NULLS);
        when(receiverSupplier.get()).thenThrow(new RuntimeException());
        Method method = new Method(receiverSupplier, methodHandle);

        method.invoke(new Object[] { });
    }

    @Test(expected = InternalErrorException.class)
    public void invoke_shouldThrow_whenMethodHandleThrowsClassCastException() throws Throwable {
        MethodHandle methodHandle = mock(MethodHandle.class, Mockito.RETURNS_SMART_NULLS);
        Supplier receiverSupplier = mock(Supplier.class, Mockito.RETURNS_SMART_NULLS);
        Object receiver = new Object();
        when(receiverSupplier.get()).thenReturn(receiver);
        when(methodHandle.invokeWithArguments(receiver)).thenThrow(new ClassCastException());
        Method method = new Method(receiverSupplier, methodHandle);

        method.invoke(new Object[] { });
    }

    @Test(expected = InternalErrorException.class)
    public void invoke_shouldThrow_whenMethodHandleThrowsWrongMethodTypeException() throws Throwable {
        MethodHandle methodHandle = mock(MethodHandle.class, Mockito.RETURNS_SMART_NULLS);
        Supplier receiverSupplier = mock(Supplier.class, Mockito.RETURNS_SMART_NULLS);
        Object receiver = new Object();
        when(receiverSupplier.get()).thenReturn(receiver);
        when(methodHandle.invokeWithArguments(receiver)).thenThrow(new WrongMethodTypeException());

        Method method = new Method(receiverSupplier, methodHandle);

        method.invoke(new Object[] { });
    }

    @Test(expected = Throwable.class)
    public void invoke_shouldThrow_whenMethodHandleThrowsThrowable() throws Throwable {
        MethodHandle methodHandle = mock(MethodHandle.class, Mockito.RETURNS_SMART_NULLS);
        Supplier receiverSupplier = mock(Supplier.class, Mockito.RETURNS_SMART_NULLS);
        Object receiver = new Object();
        when(receiverSupplier.get()).thenReturn(receiver);
        when(methodHandle.invokeWithArguments(receiver)).thenThrow(new Throwable());

        Method method = new Method(receiverSupplier, methodHandle);

        method.invoke(new Object[] { });
    }
}