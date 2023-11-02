package ua.com.obox.dbschema.tools.examples;

public class TenantResponseExample {
    public static final String POST_BODY = "{ \"name\": \"Kyiv Food LLC\", \"language\":\"en-US\" }";
    public static final String GET_ALL_200_RESPONSE_EXAMPLE = "[ { \"tenant_id\": \"13ebc562-6a87-4504-9b61-4d03d602c959\", \"restaurant_id\": \"0ea492a8-4ea4-40b8-a43a-28aaba9b4364\", \"translation_id\": \"f45a7da8-0c5f-43ba-8901-13a96a490dc6\", \"content\": { \"uk-UA\": { \"name\": \"Ресторан корисної їжі\", \"address\": \"вул. Богдана Хмельницького 77\" }, \"en-US\": { \"name\": \"Healthy food restaurant\", \"address\": null } } }, { \"tenant_id\": \"13ebc562-6a87-4504-9b61-4d03d602c959\", \"restaurant_id\": \"6c871b8f-e459-4972-aa23-47c2fa58ac63\", \"translation_id\": \"4816b961-f4cd-4780-b42f-fa991c69876f\", \"content\": { \"uk-UA\": { \"name\": \"Автентична кухня від оливок\", \"address\": \"пр. Перемоги 23\" } } } ]";
    public static final String GET_200_RESPONSE_EXAMPLE = "{ \"tenant_id\": \"92fc1335-3bd1-4f72-9aa1-0bb6f53fe63d\", \"translation_id\": \"6519acb4-74b8-488f-b59e-9b3945af0472\", \"content\": { \"en-US\": { \"name\": \"Kyiv Food LLC\" } } }";
    public static final String GET_403_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-08-24T06:34:35.850+00:00\", \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Tenant with id a95de739-40fa-414a-9f62-fdaedb2a8282 forbidden\", \"path\": \"/tenants/a95de739-40fa-414a-9f62-fdaedb2a8282\" }";
    public static final String POST_201_RESPONSE_EXAMPLE = "{ \"tenant_id\": \"c6f22a4c-1d7f-4f3c-ab43-a4986db87e34\" }";
    public static final String POST_400_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-11-01T23:37:36.189+00:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"400 BAD_REQUEST\", \"path\": \"/tenants/\", \"fields\": { \"name\": \"Field name is required\", \"language\": \"The language field is mandatory in the format en-US\" } }";
    public static final String PATCH_400_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-11-01T23:46:43.429+00:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"400 BAD_REQUEST\", \"path\": \"/tenants/54a6486d-f394-45e5-9da0-8a99ca2eb6bf\", \"fields\": { \"name\": \"Field name is required\", \"language\": \"The language field is mandatory in the format en-US\" } }";
    public static final String ALL_MAPPINGS_404_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-08-24T06:40:39.627+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Tenant with id b2268525-099d-4e8e-80ce-x258066c3aec not found\", \"path\": \"/tenants/b2268525-099d-4e8e-80ce-x258066c3aec\" }";

}
