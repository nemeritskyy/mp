package ua.com.obox.dbschema.tools.examples;

public class CategoryResponseExample {
    public static final String POST_BODY = "{ \"menu_id\":\"58e7b847-c797-4eb6-96d9-28f56e669b61\", \"name\":\"Борщ\", \"description\":\"Українська рідка страва, що вариться з посічених буряків, капусти з додатком картоплі, квасолі та різних приправ\", \"language\":\"uk-UA\", \"state\":\"ENABLED\" }";
    public static final String GET_ALL_200_RESPONSE_EXAMPLE = "[ { \"category_id\": \"e63cdb81-a849-4def-a44e-9f1f5220c266\", \"dish_id\": \"c3354a13-4048-4746-9c30-466cdfb2d004\", \"translation_id\": \"95a0b3c6-28bf-4cf1-8b7a-4a467b90290a\", \"state\": \"ENABLED\", \"in_stock\": \"ENABLED\", \"content\": { \"uk-UA\": { \"name\": \"Пісний борщ з грибами і чорносливом\", \"description\": \"Дуже смачний пісний борщик за рецептом моєї бабусі. Можна готувати і в зимку, і літом, бо дуже смакує як охолодженим в спекотні дні, так і гарячим, коли хочеться зігрітися.\" } }, \"price\": 22.99, \"special_price\": null, \"weight\": \"300/100/50\", \"weight_unit\": \"bbf46e26-2ce6-40bc-af51-939205ab0461\", \"cooking_time\": 15, \"calories\": 300, \"image\": null, \"allergens\": null, \"tags\": null } ]";
    public static final String GET_200_RESPONSE_EXAMPLE = "{ \"menu_id\": \"6ca6cf88-4b81-4e51-9848-45aaf9e0ddc2\", \"category_id\": \"725069bf-4bd2-499f-9788-c8644aadb956\", \"translation_id\": \"94f77fdd-e31e-4718-be12-86ba779db442\", \"state\": \"ENABLED\", \"content\": { \"uk-UA\": { \"name\": \"Одношарові\", \"description\": \"Зріз пляцка яскравий і багатобарвний, часом хитромудрий\" } } }";
    public static final String POST_201_RESPONSE_EXAMPLE = "{ \"category_id\": \"a031ee28-4a62-4f76-a5e5-ca2acd81d384\" }";
    public static final String POST_400_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-11-02T00:38:21.035+00:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"400 BAD_REQUEST\", \"path\": \"/categories/\", \"fields\": { \"name\": \"Це поле обов'язкове для заповнення\", \"description\": \"Поле Опис має містити від 1 до 255 символів\", \"language\": \"Поле language обовязкове у форматі uk-UA\", \"state\": \"Невірний стан\", \"menu_id\": \"Меню з id 58e7b847-c797-4eb6-96d9-28f56e66b61 не знайдено\" } }";
    public static final String PATCH_400_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-11-02T00:42:46.887+00:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"400 BAD_REQUEST\", \"path\": \"/categories/42bc32e5-cb31-46de-b05c-c353c13b86dd\", \"fields\": { \"name\": \"Це поле обов'язкове для заповнення\", \"description\": \"Поле Опис має містити від 1 до 255 символів\", \"language\": \"Поле language обовязкове у форматі uk-UA\", \"state\": \"Невірний стан\" } }";
    public static final String PATCH_BODY = "{ \"name\":\"Одношарові\", \"description\":\"Зріз пляцка яскравий і багатобарвний, часом хитромудрий\", \"language\":\"uk-UA\", \"state\":\"ENABLED\" }";
    public static final String ALL_MAPPINGS_404_RESPONSE_EXAMPLE = "{ \"timestamp\": \"2023-08-25T05:38:33.335+00:00\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Category with id fd236x1e-8103-4c06-872c-c796262aa795 not found\", \"path\": \"/categories/fd236x1e-8103-4c06-872c-c796262aa795\" }";
}
