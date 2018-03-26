package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import com.code4people.jsonrpclib.binding.BindingErrorException;
import com.code4people.jsonrpclib.binding.annotations.*;
import org.junit.Test;

public class BindingTest {

    @Test
    public void serviceActivatorBuilder_shouldBindSuccessfully() {
        ServiceActivatorBuilder
                .create()
                .register(SuccessfullyBoundReceiver.class, SuccessfullyBoundReceiver::new)
                .build();
    }

    public static class SuccessfullyBoundReceiver {
        @Bind(as = "method", paramsType = ParamsType.NAMED)
        public void namedParamsMethod1(@Param("i") int i, @Param("k") int k) {

        }

        @Bind(as = "method", paramsType = ParamsType.NAMED)
        public void namedParamsMethod2(@Param("i") int i, @Param("j") int j) {

        }

        @Bind(as = "method", paramsType = ParamsType.POSITIONAL)
        public void positionalParamsMethod1(int i, int j) {

        }

        @Bind(as = "method", paramsType = ParamsType.POSITIONAL)
        public void positionalParamsMethod2(int i, int j, int k) {

        }
    }

    @Test(expected = BindingErrorException.class)
    public void serviceActivatorBuilder_shouldThrow_whenConflictingMissingParamsMethodIsFound() {
        ServiceActivatorBuilder
                .create()
                .register(ConflictingMissingParamsOverloadReceiver.class, ConflictingMissingParamsOverloadReceiver::new)
                .build();
    }

    public static class ConflictingMissingParamsOverloadReceiver {
        @Bind(as = "method")
        public void method1() {

        }

        @Bind(as = "method")
        public void method2() {

        }
    }

    @Test(expected = BindingErrorException.class)
    public void serviceActivatorBuilder_shouldThrow_whenConflictingPositionalParamsMethodOverloadIsFound() {
        ServiceActivatorBuilder
                .create()
                .register(ConflictingPositionalParamsOverloadReceiver.class, ConflictingPositionalParamsOverloadReceiver::new)
                .build();
    }

    public static class ConflictingPositionalParamsOverloadReceiver {
        @Bind
        public void method(int i) {

        }

        @Bind
        public void method(String i) {

        }
    }

    @Test(expected = BindingErrorException.class)
    public void serviceActivatorBuilder_shouldThrow_whenConflictingNamedParamsMethodOverloadIsFound() {
        ServiceActivatorBuilder
                .create()
                .register(ConflictingNamedParamsOverloadReceiver.class, ConflictingNamedParamsOverloadReceiver::new)
                .build();
    }

    public static class ConflictingNamedParamsOverloadReceiver {
        @Bind
        public void method(@Param("i") int i) {

        }

        @Bind
        public void method(@Param("i") String i, @Optional @Param("j") String j) {

        }
    }

    @Test(expected = BindingErrorException.class)
    public void serviceActivatorBuilder_shouldThrow_whenMethodNameConflictIsFound() {
        ServiceActivatorBuilder
                .create()
                .register(ConflictingMethodNamesReceiver.class, ConflictingMethodNamesReceiver::new)
                .build();
    }

    public static class ConflictingMethodNamesReceiver {
        @Bind(as = "method")
        public void method1() {

        }

        @BindToSingleArgument(as = "method")
        public void method2() {

        }
    }

    @Test(expected = BindingErrorException.class)
    public void serviceActivatorBuilder_shouldThrow_whenMissingParamsAnnotation() {
        ServiceActivatorBuilder
                .create()
                .register(WrongBindingClass.class, WrongBindingClass::new)
                .build();
    }

    public static class WrongBindingClass {
        @Bind(paramsType = ParamsType.NAMED)
        public void method(String s) {

        }
    }
}
