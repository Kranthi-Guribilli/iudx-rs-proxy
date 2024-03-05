package iudx.rs.proxy.apiserver.handlers;

import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import iudx.rs.proxy.apiserver.util.ApiServerConstants;
import iudx.rs.proxy.authenticator.AuthenticationService;
import iudx.rs.proxy.authenticator.model.JwtData;
import iudx.rs.proxy.common.Api;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Map;

import static iudx.rs.proxy.apiserver.util.ApiServerConstants.HEADER_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    RoutingContext routingContext;
    @Mock
    HttpServerResponse httpServerResponse;
    @Mock
    HttpServerRequest httpServerRequest;
    @Mock
    HttpMethod httpMethod;
    @Mock
    AsyncResult<JsonObject> asyncResult;
    @Mock
    MultiMap map;
    @Mock
    Throwable throwable;
    @Mock
    Future<Void> voidFuture;

    AuthHandler authHandler;
    JsonObject jsonObject;
    @Mock
    RequestBody requestBody;

    @BeforeEach
    public void setUp(VertxTestContext vertxTestContext) {
        authHandler = new AuthHandler();
        jsonObject = new JsonObject();
        jsonObject.put("Dummy Key", "Dummy Value");
        jsonObject.put("IID", "Dummy IID value");
        jsonObject.put("USER_ID", "Dummy USER_ID");
        jsonObject.put("EXPIRY", "Dummy EXPIRY");
        lenient().when(httpServerRequest.method()).thenReturn(httpMethod);
        lenient().when(httpMethod.toString()).thenReturn("GET");
        lenient().when(routingContext.request()).thenReturn(httpServerRequest);

        vertxTestContext.completeNow();
    }

    @DisplayName("Test create method")
    @Test
    public void testCreate(VertxTestContext vertxTestContext) {
        AuthHandler.authenticator = mock(AuthenticationService.class);
        Api api = mock(Api.class);
        assertNotNull(AuthHandler.create(Vertx.vertx(), api,true));
        vertxTestContext.completeNow();
    }

    @Test
    @DisplayName("Test handler for succeeded authHandler")
    public void testHandleSuccess(VertxTestContext vertxTestContext) {
        when(routingContext.body()).thenReturn(requestBody);
        when(requestBody.asJsonObject()).thenReturn(jsonObject);
        when(httpServerRequest.path()).thenReturn("/ngsi-ld/v1/entities");
        AuthHandler.authenticator = mock(AuthenticationService.class);
        AuthHandler.api = mock(Api.class);
        when(httpServerRequest.headers()).thenReturn(map);
        when(map.get(anyString())).thenReturn("Dummy Token");
        when(asyncResult.succeeded()).thenReturn(true);
        when(asyncResult.result()).thenReturn(jsonObject);

        when(AuthHandler.api.getEntitiesEndpoint()).thenReturn("/ngsi-ld/v1/entities");


        doAnswer(new Answer<AsyncResult<JsonObject>>() {
            @Override
            public AsyncResult<JsonObject> answer(InvocationOnMock arg0) throws Throwable {
                ((Handler<AsyncResult<JsonObject>>) arg0.getArgument(3)).handle(asyncResult);
                return null;
            }
        }).when(AuthHandler.authenticator).tokenIntrospect(any(), any(), any(), any());

        authHandler.handle(routingContext);

        assertEquals("/ngsi-ld/v1/entities", routingContext.request().path());
        assertEquals("Dummy Token", routingContext.request().headers().get(ApiServerConstants.HEADER_TOKEN));
        assertEquals("GET", routingContext.request().method().toString());
        verify(AuthHandler.authenticator, times(1)).tokenIntrospect(any(), any(), any(), any());
        verify(routingContext, times(2)).body();

        vertxTestContext.completeNow();
    }

    @Test
    @DisplayName("Test handle method for Item not found")
    public void testHandleFailure(VertxTestContext vertxTestContext) {
        authHandler = new AuthHandler();
        //String str = Constants.IUDX_ASYNC_STATUS;
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("Dummy Key", "Dummy Value");
        jsonObject.put("q","Ppbno==T13010001107;referenceLevel>15.0");


        when(routingContext.body()).thenReturn(requestBody);
        when(requestBody.asJsonObject()).thenReturn(jsonObject);
        when(httpServerRequest.path()).thenReturn("/ngsi-ld/v1/entities");
        AuthHandler.authenticator = mock(AuthenticationService.class);
        AuthHandler.api = mock(Api.class);
        when(routingContext.request()).thenReturn(httpServerRequest);
        when(httpServerRequest.getParam(any())).thenReturn("Ppbno==T13010001107;referenceLevel>15.0");

        when(httpServerRequest.headers()).thenReturn(map);
        when(map.get(anyString())).thenReturn("Dummy token");
        when(asyncResult.cause()).thenReturn(throwable);
        when(throwable.getMessage()).thenReturn("Dummy throwable message: Not Found");
        when(routingContext.response()).thenReturn(httpServerResponse);
        when(httpServerResponse.putHeader(anyString(), anyString())).thenReturn(httpServerResponse);
        when(httpServerResponse.setStatusCode(anyInt())).thenReturn(httpServerResponse);
        when(httpServerResponse.end(anyString())).thenReturn(voidFuture);
        when(asyncResult.succeeded()).thenReturn(false);

        when(AuthHandler.api.getEntitiesEndpoint()).thenReturn("/ngsi-ld/v1/entities");

        doAnswer((Answer<AsyncResult<JsonObject>>) arg0 -> {
            ((Handler<AsyncResult<JsonObject>>) arg0.getArgument(3)).handle(asyncResult);
            return null;
        }).when(AuthHandler.authenticator).tokenIntrospect(any(), any(), any(), any());


        authHandler.handle(routingContext);

        assertEquals("/ngsi-ld/v1/entities", routingContext.request().path());
        assertEquals("Dummy token", routingContext.request().headers().get(HEADER_TOKEN));
        assertEquals("GET", routingContext.request().method().toString());
        verify(AuthHandler.authenticator, times(1)).tokenIntrospect(any(), any(), any(), any());
        verify(httpServerResponse, times(1)).setStatusCode(anyInt());
        verify(httpServerResponse, times(1)).putHeader(anyString(), anyString());
        verify(httpServerResponse, times(1)).end(anyString());
        verify(routingContext, times(3)).body();

        vertxTestContext.completeNow();

    }

    @Test
    @DisplayName("Test handle method for Authentication Failure")
    public void testCanHandleAuthenticationFailure(VertxTestContext vertxTestContext) {
        authHandler = new AuthHandler();
        String str = "Dummy Path";
        JsonObject jsonObject = mock(JsonObject.class);
        Map<String, String> stringMap = mock(Map.class);

        when(routingContext.body()).thenReturn(requestBody);
        when(requestBody.asJsonObject()).thenReturn(jsonObject);
        when(httpServerRequest.path()).thenReturn("/ngsi-ld/v1/entities");

        when(routingContext.pathParams()).thenReturn(stringMap);
        lenient().when(routingContext.pathParams().isEmpty()).thenReturn(false);
        AuthHandler.authenticator = mock(AuthenticationService.class);
        AuthHandler.api = mock(Api.class);
        lenient().when(routingContext.pathParams().containsKey(anyString())).thenReturn(true);
        lenient().when(routingContext.pathParams().get(anyString())).thenReturn("Dummy_value");
        when(httpServerRequest.headers()).thenReturn(map);
        when(map.get(anyString())).thenReturn("Dummy token");
        when(asyncResult.cause()).thenReturn(throwable);
        when(throwable.getMessage()).thenReturn("Dummy throwable message: Authentication Failure");
        when(routingContext.response()).thenReturn(httpServerResponse);
        when(httpServerResponse.putHeader(anyString(), anyString())).thenReturn(httpServerResponse);
        when(httpServerResponse.setStatusCode(anyInt())).thenReturn(httpServerResponse);
        when(httpServerResponse.end(anyString())).thenReturn(voidFuture);
        when(asyncResult.succeeded()).thenReturn(false);
        when(AuthHandler.api.getEntitiesEndpoint()).thenReturn("/ngsi-ld/v1/entities");

        doAnswer(new Answer<AsyncResult<JwtData>>() {
            @Override
            public AsyncResult<JwtData> answer(InvocationOnMock arg0) throws Throwable {
                ((Handler<AsyncResult<JsonObject>>) arg0.getArgument(3)).handle(asyncResult);
                return null;
            }
        }).when(AuthHandler.authenticator).tokenIntrospect(any(), any(), any(), any());

        authHandler.handle(routingContext);

        assertEquals("/ngsi-ld/v1/entities", routingContext.request().path());
        assertEquals("Dummy token", routingContext.request().headers().get(HEADER_TOKEN));
        assertEquals("GET", routingContext.request().method().toString());
        verify(AuthHandler.authenticator, times(1)).tokenIntrospect(any(), any(), any(), any());
        verify(httpServerResponse, times(1)).setStatusCode(anyInt());
        verify(httpServerResponse, times(1)).putHeader(anyString(), anyString());
        verify(httpServerResponse, times(1)).end(anyString());
        verify(routingContext, times(2)).body();

        vertxTestContext.completeNow();
    }
}