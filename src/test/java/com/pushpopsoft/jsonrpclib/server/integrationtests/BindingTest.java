package com.pushpopsoft.jsonrpclib.server.integrationtests;

import com.pushpopsoft.jsonrpclib.binding.BindingErrorException;
import com.pushpopsoft.jsonrpclib.binding.annotations.*;
import com.pushpopsoft.jsonrpclib.server.ServiceActivatorBuilder;
import org.junit.Test;

public class BindingTest {

    @Test
    public void serviceActivatorBuilder_shouldBindSuncessfully() {
        ServiceActivatorBuilder
                .create()
                .register(SuccessfullyBoundReceiver.class, SuccessfullyBoundReceiver::new)
                .build();
    }

    public static class SuccessfullyBoundReceiver {
        @Bind(as = "method", paramsTypes = ParamsType.NAMED)
        public void namedParamsMethod1(@Param("i") int i, @Param("k") int k) {

        }

        @Bind(as = "method", paramsTypes = ParamsType.NAMED)
        public void namedParamsMethod2(@Param("i") int i, @Param("j") int j) {

        }

        @Bind(as = "method", paramsTypes = ParamsType.POSITIONAL)
        public void positionalParamsMethod1(int i, int j) {

        }

        @Bind(as = "method", paramsTypes = ParamsType.POSITIONAL)
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
        @Bind(as = "method", paramsTypes = ParamsType.MISSING)
        public void method1() {

        }

        @Bind(as = "method", paramsTypes = ParamsType.MISSING)
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
        @Bind(paramsTypes = ParamsType.POSITIONAL)
        public void method(int i) {

        }

        @Bind(paramsTypes = ParamsType.POSITIONAL)
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
        @Bind(paramsTypes = ParamsType.NAMED)
        public void method(@Param("i") int i) {

        }

        @Bind(paramsTypes = ParamsType.NAMED)
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
        @Bind(as = "method", paramsTypes = ParamsType.NAMED)
        public void method1() {

        }

        @BindToSingleArgument(as = "method")
        public void method2() {

        }
    }
}
