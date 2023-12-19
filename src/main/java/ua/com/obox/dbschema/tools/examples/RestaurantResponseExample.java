package ua.com.obox.dbschema.tools.examples;

public class RestaurantResponseExample {
    public static final String POST_BODY = "{ \"tenant_id\":\"92fc1335-3bd1-4f72-9aa1-0bb6f53fe63d\", \"name\":\"Ресторан корисної їжі\", \"address\":\"вул. Богдана Хмельницького 77\", \"language\":\"uk-UA\" }";
    public static final String PATCH_BODY = "{ \"name\":\"Ресторан корисної їжі\", \"address\":\"вул. Богдана Хмельницького 77\", \"language\":\"uk-UA\" }";
    public static final String GET_ALL_DETAILS = "[ { \"restaurant_id\": \"e33a1b35-b9e1-40dc-b67a-3618622b33c9\", \"menu_id\": \"885ec23a-4d77-42e6-b8e6-27d16c50bc38\", \"original_language\": \"en-US\", \"translation_id\": \"523705e1-6076-4475-912a-f68d2cdfe1c9\", \"state\": \"ENABLED\", \"content\": { \"en-US\": { \"name\": \"Pilsner Bee\" } }, \"categories\": [ { \"menu_id\": \"885ec23a-4d77-42e6-b8e6-27d16c50bc38\", \"category_id\": \"659e9a99-dce3-49a0-8575-9819298db56b\", \"original_language\": \"uk-UA\", \"translation_id\": \"0f690cc6-d64a-443f-92bf-14081ef5919a\", \"state\": \"ENABLED\", \"content\": { \"uk-UA\": { \"name\": \"Пиво пєннє\", \"description\": \"Современнє\" } }, \"dishes\": [ { \"category_id\": \"659e9a99-dce3-49a0-8575-9819298db56b\", \"dish_id\": \"cbc5e834-20c7-40ac-b244-1d5bd2132868\", \"original_language\": \"uk-UA\", \"translation_id\": \"7e81ff73-adbe-41db-b21e-7f0ba0abf071\", \"state\": \"ENABLED\", \"in_stock\": \"ENABLED\", \"content\": { \"uk-UA\": { \"name\": \"Півандєр\", \"description\": \"Півандєр саламандєр\" } }, \"price\": 22.99, \"special_price\": 19.99, \"weight\": \"500\", \"weight_unit\": \"ML\", \"cooking_time\": 5, \"calories\": 777, \"image\": null, \"allergens\": null, \"marks\": null } ] } ] } ]";
    public static final String GET_ALL_200_RESPONSE_EXAMPLE = "[ { \"restaurant_id\": \"e33a1b35-b9e1-40dc-b67a-3618622b33c9\", \"menu_id\": \"885ec23a-4d77-42e6-b8e6-27d16c50bc38\", \"original_language\": \"en-US\", \"translation_id\": \"523705e1-6076-4475-912a-f68d2cdfe1c9\", \"state\": \"ENABLED\", \"content\": { \"en-US\": { \"name\": \"Pilsner Bee\" } } } ]";
    public static final String GET_200_RESPONSE_EXAMPLE = "{ \"tenant_id\": \"f1af540c-3ccc-4483-997f-ff3d7d14fa36\", \"restaurant_id\": \"e33a1b35-b9e1-40dc-b67a-3618622b33c9\", \"original_language\": \"uk-UA\", \"translation_id\": \"286aa04c-c064-46e6-8886-f9d7e2bf7637\", \"content\": { \"uk-UA\": { \"name\": \"Ресторан пива\", \"address\": \"вул. Богдана Хмельницького 77\" } } }";
    public static final String POST_201_RESPONSE_EXAMPLE = "{ \"menu_id\": \"0c19ee0e-ed72-4d74-a31e-56a366be9b2b\" }";
    public static final String POST_400_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-11-01T23:54:48.336+00:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"400 BAD_REQUEST\", \"path\": \"/restaurants/\", \"fields\": { \"tenant_id\": \"Тенант з id 92fc1335-3bd1-4f72-9aa1-0bb6f53fe6d не знайдено\", \"address\": \"Поле Адреса має містити від 1 до 255 символів\", \"name\": \"Це поле обов'язкове для заповнення\", \"language\": \"Поле language обовязкове у форматі uk-UA\" } }";
    public static final String PATCH_400_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-11-02T00:01:51.406+00:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"400 BAD_REQUEST\", \"path\": \"/restaurants/0ea492a8-4ea4-40b8-a43a-28aaba9b4364\", \"fields\": { \"address\": \"Field Address must contain from 1 to 255 characters\", \"name\": \"Field name is required\", \"language\": \"The language field is mandatory in the format en-US\" } }}";
    public static final String ALL_MAPPINGS_404_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-08-24T11:53:36.794+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Restaurant with id 9aff3e00-451c-49xe-b48b-c4315785b75e not found\", \"path\": \"/restaurants/9aff3e00-451c-49xe-b48b-c4315785b75e\" }";
}
